package com.universe.mobileapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.concurrent.Executors;

public final class MobileApiServer {

    private final TokenService tokenService = new TokenService();
    private final AuthService authService = new AuthService();
    private final StudentService studentService = new StudentService();
    private final LecturerService lecturerService = new LecturerService();
    private final LocationCalibrationService locationCalibrationService =
            new LocationCalibrationService();

    public static void main(String[] args) throws IOException {
        DatabaseInitializer.initialize();
        new MobileApiServer().start();
    }

    private void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(Config.port()), 0);
        server.createContext("/api", this::handle);
        server.setExecutor(Executors.newFixedThreadPool(
                Math.max(4, Runtime.getRuntime().availableProcessors())));
        server.start();
        System.out.println("UniVerse Mobile API listening on http://0.0.0.0:"
                + Config.port() + "/api");
    }

    private void handle(HttpExchange exchange) throws IOException {
        // CORS: cho phép mọi origin (vd. trang Moodle) gọi API từ trình duyệt.
        // Dùng token Bearer trong header Authorization (không dùng cookie) nên
        // Access-Control-Allow-Origin: * là an toàn ở đây.
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers", "Authorization, Content-Type");
        exchange.getResponseHeaders().set("Access-Control-Max-Age", "86400");
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }
        try {
            route(exchange);
        } catch (ServiceException ex) {
            JsonHttp.send(exchange, ex.status(), JsonHttp.message(ex.getMessage()));
        } catch (JsonParseException | IllegalStateException ex) {
            JsonHttp.send(exchange, 400, JsonHttp.message("Nội dung JSON không hợp lệ."));
        } catch (SQLException ex) {
            ex.printStackTrace();
            JsonHttp.send(exchange, 500, JsonHttp.message("Lỗi truy cập cơ sở dữ liệu."));
        } catch (Exception ex) {
            ex.printStackTrace();
            JsonHttp.send(exchange, 500, JsonHttp.message("Lỗi máy chủ nội bộ."));
        } finally {
            exchange.close();
        }
    }

    private void route(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod();
        String path = normalizePath(exchange.getRequestURI().getPath());

        if ("GET".equals(method) && "/api/health".equals(path)) {
            JsonObject health = new JsonObject();
            health.addProperty("status", "ok");
            JsonHttp.send(exchange, 200, health);
            return;
        }

        if ("POST".equals(method) && "/api/auth/login".equals(path)) {
            login(exchange);
            return;
        }

        TokenService.Principal principal = requirePrincipal(exchange);
        if ("POST".equals(method) && "/api/auth/logout".equals(path)) {
            tokenService.revoke(bearerToken(exchange));
            JsonHttp.send(exchange, 200, JsonHttp.message("Đã đăng xuất."));
            return;
        }

        if ("POST".equals(method) && "/api/location-calibrations".equals(path)) {
            requireRole(principal, "Lecturer");
            createLocationCalibration(exchange, principal.userId());
            return;
        }

        if ("GET".equals(method)
                && path.matches("/api/location-calibrations/[^/]+")) {
            requireRole(principal, "Lecturer");
            JsonHttp.send(
                    exchange,
                    200,
                    locationCalibrationService.status(
                            segment(path, 3),
                            principal.userId()));
            return;
        }

        if ("POST".equals(method)
                && path.matches("/api/location-calibrations/[^/]+/location")) {
            requireRole(principal, "Lecturer");
            submitLocationCalibration(
                    exchange,
                    segment(path, 3),
                    principal.userId());
            return;
        }

        if (path.startsWith("/api/student/")) {
            requireRole(principal, "Student");
            routeStudent(exchange, method, path, principal.userId());
            return;
        }

        if (path.startsWith("/api/lecturer/")) {
            requireRole(principal, "Lecturer");
            routeLecturer(exchange, method, path, principal.userId());
            return;
        }

        throw new ServiceException(404, "Không tìm thấy API.");
    }

    private void routeStudent(
            HttpExchange exchange,
            String method,
            String path,
            String studentId) throws Exception {
        if ("GET".equals(method) && "/api/student/dashboard".equals(path)) {
            JsonHttp.send(exchange, 200, studentService.dashboard(studentId));
        } else if ("GET".equals(method) && "/api/student/courses".equals(path)) {
            String keyword = JsonHttp.queryParameters(exchange).getOrDefault("q", "");
            JsonHttp.send(exchange, 200, studentService.courses(studentId, keyword));
        } else if ("GET".equals(method)
                && path.matches("/api/student/courses/[^/]+/sections")) {
            JsonHttp.send(exchange, 200,
                    studentService.sections(studentId, segment(path, 4)));
        } else if ("POST".equals(method)
                && path.matches("/api/student/sections/[^/]+/register")) {
            JsonHttp.send(exchange, 201,
                    studentService.register(studentId, segment(path, 4)));
        } else if ("GET".equals(method) && "/api/student/enrollments".equals(path)) {
            JsonHttp.send(exchange, 200, studentService.enrollments(studentId));
        } else if ("GET".equals(method) && "/api/student/schedule".equals(path)) {
            JsonHttp.send(exchange, 200, studentService.schedule(studentId));
        } else if ("GET".equals(method) && "/api/student/grades".equals(path)) {
            JsonHttp.send(exchange, 200, studentService.grades(studentId));
        } else if ("GET".equals(method) && "/api/student/notifications".equals(path)) {
            JsonHttp.send(exchange, 200, studentService.notifications(studentId));
        } else if ("POST".equals(method)
                && "/api/student/attendance/scan".equals(path)) {
            markAttendance(exchange, studentId);
        } else {
            throw new ServiceException(404, "Không tìm thấy API.");
        }
    }

    private void routeLecturer(
            HttpExchange exchange,
            String method,
            String path,
            String lecturerId) throws Exception {
        if ("GET".equals(method) && "/api/lecturer/dashboard".equals(path)) {
            JsonHttp.send(exchange, 200, lecturerService.dashboard(lecturerId));
        } else if ("GET".equals(method) && "/api/lecturer/classes".equals(path)) {
            JsonHttp.send(exchange, 200, lecturerService.classes(lecturerId));
        } else if ("GET".equals(method) && "/api/lecturer/schedule".equals(path)) {
            JsonHttp.send(exchange, 200, lecturerService.schedule(lecturerId));
        } else if ("GET".equals(method)
                && path.matches("/api/lecturer/classes/[^/]+/sessions")) {
            JsonHttp.send(
                    exchange,
                    200,
                    lecturerService.sessions(lecturerId, segment(path, 4)));
        } else if ("GET".equals(method)
                && path.matches("/api/lecturer/sessions/[^/]+/attendance")) {
            JsonHttp.send(
                    exchange,
                    200,
                    lecturerService.attendance(lecturerId, segment(path, 4)));
        } else if ("POST".equals(method)
                && path.matches(
                        "/api/lecturer/sessions/[^/]+/attendance/[^/]+")) {
            JsonObject request = JsonHttp.readObject(exchange);
            JsonHttp.send(
                    exchange,
                    200,
                    lecturerService.updateAttendance(
                            lecturerId,
                            segment(path, 4),
                            segment(path, 6),
                            requiredString(request, "status")));
        } else if ("GET".equals(method)
                && path.matches("/api/lecturer/classes/[^/]+/grades")) {
            JsonHttp.send(
                    exchange,
                    200,
                    lecturerService.grades(lecturerId, segment(path, 4)));
        } else if ("POST".equals(method)
                && path.matches("/api/lecturer/grades/[^/]+")) {
            JsonObject request = JsonHttp.readObject(exchange);
            JsonHttp.send(
                    exchange,
                    200,
                    lecturerService.updateGrade(
                            lecturerId,
                            segment(path, 4),
                            optionalDouble(request, "score1"),
                            optionalDouble(request, "score2"),
                            optionalDouble(request, "score3"),
                            optionalDouble(request, "examScore")));
        } else if ("POST".equals(method)
                && "/api/lecturer/notifications".equals(path)) {
            JsonObject request = JsonHttp.readObject(exchange);
            JsonHttp.send(
                    exchange,
                    201,
                    lecturerService.sendNotification(
                            lecturerId,
                            requiredString(request, "classSectionId"),
                            requiredString(request, "title"),
                            requiredString(request, "content")));
        } else {
            throw new ServiceException(404, "Không tìm thấy API.");
        }
    }

    private void login(HttpExchange exchange) throws Exception {
        JsonObject request = JsonHttp.readObject(exchange);
        String username = requiredString(request, "username");
        String password = requiredString(request, "password");
        AuthService.LoginData loginData = authService.authenticate(username, password);
        String token = tokenService.create(loginData.userId(), loginData.role());
        JsonObject response = new JsonObject();
        response.addProperty("token", token);
        response.addProperty("role", loginData.role());
        response.add("profile", loginData.profile());
        JsonHttp.send(exchange, 200, response);
    }

    private void markAttendance(HttpExchange exchange, String studentId) throws Exception {
        JsonObject request = JsonHttp.readObject(exchange);
        String qrPayload = requiredString(request, "qrPayload");
        double latitude = requiredDouble(request, "latitude");
        double longitude = requiredDouble(request, "longitude");
        double accuracy = requiredDouble(request, "accuracy");
        JsonHttp.send(exchange, 200, studentService.markAttendance(
                studentId, qrPayload, latitude, longitude, accuracy));
    }

    private void createLocationCalibration(
            HttpExchange exchange,
            String lecturerId) throws Exception {
        JsonObject request = JsonHttp.readObject(exchange);
        JsonHttp.send(
                exchange,
                201,
                locationCalibrationService.create(
                        requiredString(request, "classSessionId"),
                        requiredString(request, "classSectionId"),
                        lecturerId));
    }

    private void submitLocationCalibration(
            HttpExchange exchange,
            String token,
            String lecturerId)
            throws IOException {
        JsonObject request = JsonHttp.readObject(exchange);
        JsonHttp.send(
                exchange,
                200,
                locationCalibrationService.submit(
                        token,
                        lecturerId,
                        requiredDouble(request, "latitude"),
                        requiredDouble(request, "longitude"),
                        requiredDouble(request, "accuracy")));
    }

    private TokenService.Principal requirePrincipal(HttpExchange exchange) {
        TokenService.Principal principal =
                tokenService.resolve(bearerToken(exchange));
        if (principal == null) {
            throw new ServiceException(401, "Phiên đăng nhập đã hết hạn.");
        }
        return principal;
    }

    private void requireRole(TokenService.Principal principal, String role) {
        if (!role.equalsIgnoreCase(principal.role())) {
            throw new ServiceException(
                    403,
                    "Tài khoản không có quyền sử dụng chức năng này.");
        }
    }

    private String bearerToken(HttpExchange exchange) {
        String authorization = exchange.getRequestHeaders().getFirst("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring("Bearer ".length()).trim();
    }

    private String requiredString(JsonObject request, String field) {
        if (!request.has(field) || request.get(field).isJsonNull()) {
            throw new ServiceException(400, "Thiếu trường " + field + ".");
        }
        String value = request.get(field).getAsString().trim();
        if (value.isEmpty()) {
            throw new ServiceException(400, "Trường " + field + " không được để trống.");
        }
        return value;
    }

    private double requiredDouble(JsonObject request, String field) {
        if (!request.has(field) || request.get(field).isJsonNull()) {
            throw new ServiceException(400, "Thiếu trường " + field + ".");
        }
        try {
            return request.get(field).getAsDouble();
        } catch (RuntimeException ex) {
            throw new ServiceException(400, "Trường " + field + " phải là số.");
        }
    }

    private Double optionalDouble(JsonObject request, String field) {
        if (!request.has(field) || request.get(field).isJsonNull()) {
            return null;
        }
        try {
            double value = request.get(field).getAsDouble();
            if (!Double.isFinite(value)) {
                throw new NumberFormatException();
            }
            return value;
        } catch (RuntimeException ex) {
            throw new ServiceException(400, "Trường " + field + " phải là số.");
        }
    }

    private String normalizePath(String path) {
        if (path.length() > 1 && path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    private String segment(String path, int index) {
        String[] segments = path.split("/");
        if (segments.length <= index) {
            throw new ServiceException(404, "Đường dẫn API không hợp lệ.");
        }
        return segments[index];
    }
}
