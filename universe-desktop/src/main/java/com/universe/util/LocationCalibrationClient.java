package com.universe.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public final class LocationCalibrationClient {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(REQUEST_TIMEOUT)
            .build();
    private final String apiBaseUrl;
    private final String username;
    private final String password;
    private volatile String accessToken;

    public LocationCalibrationClient(String username, String password) {
        apiBaseUrl = normalizeBaseUrl(AppConfig.get(
                "mobile.api.base.url",
                "http://localhost:8080/api/"));
        this.username = username;
        this.password = password;
    }

    public CalibrationSession create(
            String classSessionId,
            String classSectionId) {
        JsonObject body = new JsonObject();
        body.addProperty("classSessionId", classSessionId);
        body.addProperty("classSectionId", classSectionId);

        JsonObject response = send(
                HttpRequest.newBuilder(uri("location-calibrations"))
                        .timeout(REQUEST_TIMEOUT)
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + accessToken())
                        .POST(HttpRequest.BodyPublishers.ofString(
                                body.toString(),
                                StandardCharsets.UTF_8))
                        .build());
        return new CalibrationSession(
                requiredString(response, "token"),
                response.get("expiresInSeconds").getAsInt(),
                response.get("maxAccuracyMeters").getAsDouble());
    }

    public CalibrationStatus status(String token) {
        JsonObject response = send(
                HttpRequest.newBuilder(uri("location-calibrations/" + token))
                        .timeout(REQUEST_TIMEOUT)
                        .header("Accept", "application/json")
                        .header("Authorization", "Bearer " + accessToken())
                        .GET()
                        .build());
        String status = requiredString(response, "status");
        if (!"ready".equals(status)) {
            return new CalibrationStatus(false, 0, 0, 0);
        }
        return new CalibrationStatus(
                true,
                response.get("latitude").getAsDouble(),
                response.get("longitude").getAsDouble(),
                response.get("accuracy").getAsDouble());
    }

    private JsonObject send(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            JsonObject json = parseObject(response.body());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String message = json.has("message")
                        ? json.get("message").getAsString()
                        : "API trả về HTTP " + response.statusCode() + ".";
                throw new IllegalStateException(message);
            }
            return json;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Yêu cầu lấy vị trí đã bị gián đoạn.", ex);
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Không kết nối được Mobile API tại " + apiBaseUrl
                            + " Hãy chạy run-api.ps1 trước.",
                    ex);
        }
    }

    private String accessToken() {
        String current = accessToken;
        if (current != null && !current.isBlank()) {
            return current;
        }
        synchronized (this) {
            if (accessToken != null && !accessToken.isBlank()) {
                return accessToken;
            }
            JsonObject body = new JsonObject();
            body.addProperty("username", username);
            body.addProperty("password", password);
            JsonObject response = send(
                    HttpRequest.newBuilder(uri("auth/login"))
                            .timeout(REQUEST_TIMEOUT)
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json; charset=UTF-8")
                            .POST(HttpRequest.BodyPublishers.ofString(
                                    body.toString(),
                                    StandardCharsets.UTF_8))
                            .build());
            String role = requiredString(response, "role");
            if (!"Lecturer".equalsIgnoreCase(role)) {
                throw new IllegalStateException(
                        "Tài khoản desktop không phải giảng viên.");
            }
            accessToken = requiredString(response, "token");
            return accessToken;
        }
    }

    private JsonObject parseObject(String value) {
        try {
            return JsonParser.parseString(value).getAsJsonObject();
        } catch (RuntimeException ex) {
            throw new IllegalStateException(
                    "Mobile API trả về dữ liệu không hợp lệ.", ex);
        }
    }

    private String requiredString(JsonObject object, String name) {
        if (!object.has(name) || object.get(name).isJsonNull()) {
            throw new IllegalStateException(
                    "Mobile API thiếu trường " + name + ".");
        }
        return object.get(name).getAsString();
    }

    private URI uri(String path) {
        return URI.create(apiBaseUrl + path);
    }

    private String normalizeBaseUrl(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalStateException(
                    "Chưa cấu hình mobile.api.base.url.");
        }
        return normalized.endsWith("/") ? normalized : normalized + "/";
    }

    public record CalibrationSession(
            String token,
            int expiresInSeconds,
            double maxAccuracyMeters) {
    }

    public record CalibrationStatus(
            boolean ready,
            double latitude,
            double longitude,
            double accuracyMeters) {
    }
}
