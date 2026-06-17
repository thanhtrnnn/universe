package com.universe.mobileapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

final class StudentService {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    JsonObject dashboard(String studentId) throws SQLException {
        JsonObject dashboard = new JsonObject();
        try (Connection connection = Database.open()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM tblCourseRecord WHERE tblStudentid = ?")) {
                statement.setString(1, studentId);
                try (ResultSet result = statement.executeQuery()) {
                    result.next();
                    dashboard.addProperty("enrolledCount", result.getInt(1));
                }
            }

            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT score1, score2, score3, examScore
                    FROM tblCourseRecord
                    WHERE tblStudentid = ?
                    """)) {
                statement.setString(1, studentId);
                double total = 0;
                int count = 0;
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        Double examScore = nullableDouble(result, "examScore");
                        if (examScore == null) {
                            continue;
                        }
                        double score1 = numberOrZero(nullableDouble(result, "score1"));
                        double score2 = numberOrZero(nullableDouble(result, "score2"));
                        Double score3 = nullableDouble(result, "score3");
                        double continuous = score3 == null
                                ? score1
                                : (score1 + score3) / 2.0;
                        total += continuous * 0.2 + score2 * 0.3 + examScore * 0.5;
                        count++;
                    }
                }
                dashboard.addProperty("averageScore", count == 0 ? 0 : total / count);
            }

            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM tblNotification WHERE tblUserid = ?")) {
                statement.setString(1, studentId);
                try (ResultSet result = statement.executeQuery()) {
                    result.next();
                    dashboard.addProperty("notificationCount", result.getInt(1));
                }
            }
        }
        JsonArray schedule = schedule(studentId);
        JsonArray upcoming = new JsonArray();
        for (int i = 0; i < Math.min(3, schedule.size()); i++) {
            upcoming.add(schedule.get(i));
        }
        dashboard.add("upcomingClasses", upcoming);
        return dashboard;
    }

    JsonArray courses(String studentId, String keyword) throws SQLException {
        String sql = """
                SELECT c.id, c.name, c.credits, c.department,
                       EXISTS (
                           SELECT 1
                           FROM tblCourseRecord cr
                           JOIN tblClassSection cs ON cs.id = cr.tblClassSectionid
                           WHERE cr.tblStudentid = ? AND cs.tblCourseid = c.id
                       ) AS enrolled
                FROM tblCourse c
                WHERE c.id ILIKE ? OR c.name ILIKE ?
                ORDER BY c.id
                """;
        String pattern = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        JsonArray array = new JsonArray();
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.setString(2, pattern);
            statement.setString(3, pattern);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    JsonObject item = new JsonObject();
                    item.addProperty("id", result.getString("id"));
                    item.addProperty("name", value(result, "name"));
                    item.addProperty("credits", result.getInt("credits"));
                    item.addProperty("department", value(result, "department"));
                    item.addProperty("enrolled", result.getBoolean("enrolled"));
                    array.add(item);
                }
            }
        }
        return array;
    }

    JsonArray sections(String studentId, String courseId) throws SQLException {
        String sql = """
                SELECT cs.id, cs.classId, cs.name, cs.semester, cs.year,
                       cs.maxStudents, cs.status, u.fullName AS lecturer,
                       (SELECT COUNT(*) FROM tblCourseRecord cr
                        WHERE cr.tblClassSectionid = cs.id) AS enrolled,
                       EXISTS (
                           SELECT 1 FROM tblCourseRecord cr
                           WHERE cr.tblClassSectionid = cs.id
                             AND cr.tblStudentid = ?
                       ) AS registered
                FROM tblClassSection cs
                LEFT JOIN tblUser u ON u.id = cs.tblLecturerid
                WHERE cs.tblCourseid = ?
                ORDER BY cs.classId
                """;
        JsonArray array = new JsonArray();
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.setString(2, courseId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    JsonObject item = new JsonObject();
                    item.addProperty("id", result.getString("id"));
                    item.addProperty("code", value(result, "classId"));
                    item.addProperty("name", value(result, "name"));
                    item.addProperty("semester", value(result, "semester"));
                    item.addProperty("year", value(result, "year"));
                    item.addProperty("lecturer", value(result, "lecturer"));
                    item.addProperty("status", value(result, "status"));
                    item.addProperty("enrolled", result.getInt("enrolled"));
                    item.addProperty("capacity", result.getInt("maxStudents"));
                    item.addProperty("registered", result.getBoolean("registered"));
                    array.add(item);
                }
            }
        }
        return array;
    }

    JsonObject register(String studentId, String sectionId) throws SQLException {
        try (Connection connection = Database.open()) {
            connection.setAutoCommit(false);
            try {
                int capacity;
                String status;
                try (PreparedStatement statement = connection.prepareStatement("""
                        SELECT maxStudents, status
                        FROM tblClassSection
                        WHERE id = ?
                        FOR UPDATE
                        """)) {
                    statement.setString(1, sectionId);
                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()) {
                            throw new ServiceException(404, "Không tìm thấy lớp học phần.");
                        }
                        capacity = result.getInt("maxStudents");
                        status = result.getString("status");
                    }
                }

                if (!"open".equalsIgnoreCase(status)) {
                    throw new ServiceException(409, "Lớp đã đóng đăng ký.");
                }
                if (isEnrolled(connection, studentId, sectionId)) {
                    throw new ServiceException(409, "Bạn đã đăng ký lớp học phần này.");
                }

                int enrolled = enrollmentCount(connection, sectionId);
                if (enrolled >= capacity) {
                    updateSectionStatus(connection, sectionId, "full");
                    connection.commit();
                    throw new ServiceException(409, "Lớp đã đủ sĩ số.");
                }

                try (PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO tblCourseRecord
                            (id, enrolledAt, status, tblStudentid, tblClassSectionid)
                        VALUES (?, ?, 'Registered', ?, ?)
                        """)) {
                    statement.setString(1, shortId("CR"));
                    statement.setDate(2, Date.valueOf(LocalDate.now()));
                    statement.setString(3, studentId);
                    statement.setString(4, sectionId);
                    statement.executeUpdate();
                }
                if (enrolled + 1 >= capacity) {
                    updateSectionStatus(connection, sectionId, "full");
                }
                connection.commit();
                return JsonHttp.message("Đã đăng ký lớp học phần thành công.");
            } catch (RuntimeException | SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    JsonArray enrollments(String studentId) throws SQLException {
        String sql = """
                SELECT cs.classId, cs.name, cr.enrolledAt, cr.status
                FROM tblCourseRecord cr
                JOIN tblClassSection cs ON cs.id = cr.tblClassSectionid
                WHERE cr.tblStudentid = ?
                ORDER BY cr.enrolledAt DESC, cs.classId
                """;
        JsonArray array = new JsonArray();
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    JsonObject item = new JsonObject();
                    item.addProperty("classCode", value(result, "classId"));
                    item.addProperty("className", value(result, "name"));
                    item.addProperty("enrolledAt",
                            result.getDate("enrolledAt") == null
                                    ? ""
                                    : result.getDate("enrolledAt").toLocalDate().toString());
                    item.addProperty("status", value(result, "status"));
                    array.add(item);
                }
            }
        }
        return array;
    }

    JsonArray schedule(String studentId) throws SQLException {
        String sql = """
                SELECT cs.classId, cs.name, s.dayOfWeek, s.startPeriod, s.endPeriod,
                       s.room, s.appliedFrom, s.appliedTo
                FROM tblSchedule s
                JOIN tblClassSection cs ON cs.id = s.tblClassSectionid
                JOIN tblCourseRecord cr ON cr.tblClassSectionid = cs.id
                WHERE cr.tblStudentid = ?
                ORDER BY CASE s.dayOfWeek
                    WHEN 'Monday' THEN 1
                    WHEN 'Tuesday' THEN 2
                    WHEN 'Wednesday' THEN 3
                    WHEN 'Thursday' THEN 4
                    WHEN 'Friday' THEN 5
                    WHEN 'Saturday' THEN 6
                    WHEN 'Sunday' THEN 7
                    ELSE 8 END,
                    s.startPeriod
                """;
        JsonArray array = new JsonArray();
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    JsonObject item = new JsonObject();
                    item.addProperty("classCode", value(result, "classId"));
                    item.addProperty("className", value(result, "name"));
                    item.addProperty("dayOfWeek", translateDay(value(result, "dayOfWeek")));
                    item.addProperty("startPeriod", result.getInt("startPeriod"));
                    item.addProperty("endPeriod", result.getInt("endPeriod"));
                    item.addProperty("room", value(result, "room"));
                    item.addProperty("appliedFrom", dateValue(result, "appliedFrom"));
                    item.addProperty("appliedTo", dateValue(result, "appliedTo"));
                    array.add(item);
                }
            }
        }
        return array;
    }

    JsonArray grades(String studentId) throws SQLException {
        String sql = """
                SELECT cs.classId, cs.name,
                       cr.score1, cr.score2, cr.score3, cr.examScore
                FROM tblCourseRecord cr
                JOIN tblClassSection cs ON cs.id = cr.tblClassSectionid
                WHERE cr.tblStudentid = ?
                ORDER BY cs.classId
                """;
        JsonArray array = new JsonArray();
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    JsonObject item = new JsonObject();
                    item.addProperty("classCode", value(result, "classId"));
                    item.addProperty("className", value(result, "name"));
                    Double score1 = nullableDouble(result, "score1");
                    Double score2 = nullableDouble(result, "score2");
                    Double score3 = nullableDouble(result, "score3");
                    Double examScore = nullableDouble(result, "examScore");
                    addNullable(item, "score1", score1);
                    addNullable(item, "score2", score2);
                    addNullable(item, "score3", score3);
                    addNullable(item, "examScore", examScore);
                    if (examScore == null) {
                        item.add("totalScore", null);
                    } else {
                        double continuous = score3 == null
                                ? numberOrZero(score1)
                                : (numberOrZero(score1) + score3) / 2.0;
                        double total = continuous * 0.2
                                + numberOrZero(score2) * 0.3
                                + examScore * 0.5;
                        item.addProperty("totalScore", Math.round(total * 100.0) / 100.0);
                    }
                    array.add(item);
                }
            }
        }
        return array;
    }

    JsonArray notifications(String studentId) throws SQLException {
        String sql = """
                SELECT id, title, content, sentAt
                FROM tblNotification
                WHERE tblUserid = ?
                ORDER BY sentAt DESC
                """;
        JsonArray array = new JsonArray();
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    JsonObject item = new JsonObject();
                    item.addProperty("id", result.getString("id"));
                    item.addProperty("title", value(result, "title"));
                    item.addProperty("content", value(result, "content"));
                    Timestamp sentAt = result.getTimestamp("sentAt");
                    item.addProperty("sentAt", sentAt == null
                            ? ""
                            : sentAt.toLocalDateTime().format(DATE_TIME_FORMAT));
                    array.add(item);
                }
            }
        }
        return array;
    }

    JsonObject markAttendance(String studentId, String payload, double latitude,
                              double longitude, double accuracy) throws SQLException {
        validateDeviceLocation(latitude, longitude, accuracy);
        QrVerifier.ParsedQr parsedQr = QrVerifier.parse(payload);
        if (!QrVerifier.hasValidSignature(parsedQr, System.currentTimeMillis())) {
            throw new ServiceException(410, "Mã QR đã đổi hoặc không còn hiệu lực.");
        }

        try (Connection connection = Database.open()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement lock = connection.prepareStatement(
                        "SELECT pg_advisory_xact_lock(hashtext(?))")) {
                    lock.setString(1, parsedQr.sessionId() + "|" + studentId);
                    lock.executeQuery();
                }

                SessionQr sessionQr = findSessionQr(connection, parsedQr.sessionId());
                if (!sessionQr.qrId().equals(parsedQr.qrId())) {
                    throw new ServiceException(409, "Mã QR không thuộc buổi học hiện tại.");
                }
                if (!"open".equalsIgnoreCase(sessionQr.sessionStatus())
                        || !"active".equalsIgnoreCase(sessionQr.qrStatus())) {
                    throw new ServiceException(410, "Giảng viên đã đóng điểm danh.");
                }
                if (sessionQr.expiredAt() == null
                        || sessionQr.expiredAt().isBefore(LocalDateTime.now())) {
                    throw new ServiceException(410, "Phiên điểm danh đã hết hạn.");
                }
                if (!isEnrolled(connection, studentId, sessionQr.classSectionId())) {
                    throw new ServiceException(
                            403, "Bạn không đăng ký lớp học phần của buổi học này.");
                }

                double distance = QrVerifier.distanceMeters(
                        latitude, longitude,
                        sessionQr.latitude(), sessionQr.longitude());
                double allowedRadius = GeofencePolicy.effectiveRadius(sessionQr.radius());
                boolean isInside = GeofencePolicy.isInside(distance, sessionQr.radius());

                String existingStatus = null;
                String existingId = null;
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT id, status FROM tblAttendance WHERE tblClassSessionid = ? AND tblStudentid = ?")) {
                    statement.setString(1, parsedQr.sessionId());
                    statement.setString(2, studentId);
                    try (ResultSet rs = statement.executeQuery()) {
                        if (rs.next()) {
                            existingId = rs.getString("id");
                            existingStatus = rs.getString("status");
                        }
                    }
                }

                LocalDateTime now = LocalDateTime.now();
                String newStatus = isInside ? "PRESENT" : "OUT_OF_RANGE";

                if (existingId != null) {
                    try (PreparedStatement statement = connection.prepareStatement(
                            "UPDATE tblAttendance SET latitude = ?, longitude = ?, distance = ?, status = ?, attendedAt = ? WHERE id = ?")) {
                        statement.setDouble(1, latitude);
                        statement.setDouble(2, longitude);
                        statement.setDouble(3, distance);
                        statement.setString(4, newStatus);
                        statement.setTimestamp(5, Timestamp.valueOf(now));
                        statement.setString(6, existingId);
                        statement.executeUpdate();
                    }
                    
                    if ("OUT_OF_RANGE".equals(existingStatus) && isInside) {
                        sendNotification(connection, studentId, "Cập nhật vị trí", "Bạn đã quay lại phạm vi lớp học.");
                        sendNotification(connection, sessionQr.lecturerId(), "Sinh viên đã quay lại", 
                            String.format(Locale.US, "Sinh viên %s đã quay lại phạm vi lớp học.", studentId));
                    } else if ("PRESENT".equals(existingStatus) && !isInside) {
                        sendNotification(connection, studentId, "Cảnh báo vị trí điểm danh", 
                            String.format(Locale.US, "Bạn đang ở ngoài phạm vi lớp học (%.0f m > %.0f m). Vui lòng di chuyển vào trong bán kính cho phép.", distance, allowedRadius));
                        sendNotification(connection, sessionQr.lecturerId(), "Cảnh báo sinh viên ngoài phạm vi", 
                            String.format(Locale.US, "Sinh viên %s đang điểm danh ngoài phạm vi lớp học (%.0f m).", studentId, distance));
                    }
                    
                    connection.commit();
                    return attendanceReceipt(sessionQr, now, true, "Đã cập nhật vị trí điểm danh mới nhất.");
                }

                try (PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO tblAttendance
                            (id, attendedAt, latitude, longitude, distance, method, status,
                             tblClassSessionid, tblStudentid)
                        VALUES (?, ?, ?, ?, ?, 'QR', ?, ?, ?)
                        """)) {
                    statement.setString(1, shortId("ATT"));
                    statement.setTimestamp(2, Timestamp.valueOf(now));
                    statement.setDouble(3, latitude);
                    statement.setDouble(4, longitude);
                    statement.setDouble(5, distance);
                    statement.setString(6, newStatus);
                    statement.setString(7, parsedQr.sessionId());
                    statement.setString(8, studentId);
                    statement.executeUpdate();
                }

                if (!isInside) {
                    sendNotification(connection, studentId, "Cảnh báo vị trí điểm danh", 
                        String.format(Locale.US, "Bạn đang ở ngoài phạm vi lớp học (%.0f m > %.0f m). Vui lòng di chuyển vào trong bán kính cho phép.", distance, allowedRadius));
                    sendNotification(connection, sessionQr.lecturerId(), "Cảnh báo sinh viên ngoài phạm vi", 
                        String.format(Locale.US, "Sinh viên %s đang điểm danh ngoài phạm vi lớp học (%.0f m).", studentId, distance));
                }

                connection.commit();
                return attendanceReceipt(
                        sessionQr, now, false, isInside ? "Điểm danh đã được ghi nhận." : "Đã ghi nhận điểm danh, nhưng bạn đang ở ngoài phạm vi. Hãy vào lớp!");
            } catch (RuntimeException | SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private SessionQr findSessionQr(Connection connection, String sessionId)
            throws SQLException {
        String sql = """
                SELECT s.id, s.status AS sessionStatus, s.room, s.tblClassSectionid,
                       q.id AS qrId, q.status AS qrStatus, q.expiredAt,
                       q.latitude, q.longitude, q.radius,
                       cs.classId, cs.name, cs.tblLecturerid
                FROM tblClassSession s
                JOIN tblQRCode q ON q.id = s.tblQRCodeid
                JOIN tblClassSection cs ON cs.id = s.tblClassSectionid
                WHERE s.id = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sessionId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new ServiceException(404, "Không tìm thấy phiên điểm danh.");
                }
                Timestamp expiredAt = result.getTimestamp("expiredAt");
                return new SessionQr(
                        result.getString("qrId"),
                        result.getString("sessionStatus"),
                        result.getString("qrStatus"),
                        expiredAt == null ? null : expiredAt.toLocalDateTime(),
                        result.getDouble("latitude"),
                        result.getDouble("longitude"),
                        result.getDouble("radius"),
                        result.getString("tblClassSectionid"),
                        value(result, "classId"),
                        value(result, "name"),
                        value(result, "room"),
                        result.getString("tblLecturerid"));
            }
        }
    }



    private JsonObject attendanceReceipt(SessionQr sessionQr, LocalDateTime attendedAt,
                                         boolean alreadyMarked, String message) {
        JsonObject receipt = new JsonObject();
        receipt.addProperty("alreadyMarked", alreadyMarked);
        receipt.addProperty("message", message);
        receipt.addProperty("classCode", sessionQr.classCode());
        receipt.addProperty("className", sessionQr.className());
        receipt.addProperty("room", sessionQr.room());
        receipt.addProperty("attendedAt", attendedAt.format(DATE_TIME_FORMAT));
        return receipt;
    }

    private boolean isEnrolled(Connection connection, String studentId, String sectionId)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT 1
                FROM tblCourseRecord
                WHERE tblStudentid = ? AND tblClassSectionid = ?
                """)) {
            statement.setString(1, studentId);
            statement.setString(2, sectionId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private int enrollmentCount(Connection connection, String sectionId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM tblCourseRecord WHERE tblClassSectionid = ?")) {
            statement.setString(1, sectionId);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getInt(1);
            }
        }
    }

    private void updateSectionStatus(Connection connection, String sectionId, String status)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE tblClassSection SET status = ? WHERE id = ?")) {
            statement.setString(1, status);
            statement.setString(2, sectionId);
            statement.executeUpdate();
        }
    }

    private void sendNotification(Connection connection, String userId, String title, String content) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO tblNotification (id, title, content, sentAt, tblUserid) VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, shortId("NTF"));
            statement.setString(2, title);
            statement.setString(3, content);
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(5, userId);
            statement.executeUpdate();
        }
    }

    private void validateDeviceLocation(double latitude, double longitude, double accuracy) {
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90
                || !Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
            throw new ServiceException(400, "Tọa độ thiết bị không hợp lệ.");
        }
        if (!GeofencePolicy.hasRequiredAccuracy(accuracy)) {
            throw new ServiceException(
                    422,
                    String.format(
                            Locale.US,
                            "GPS có sai số %.0f m; geofence yêu cầu sai số tối đa %.0f m. "
                                    + "Hãy bật Vị trí chính xác và thử lại.",
                            accuracy,
                            GeofencePolicy.MAX_LOCATION_ACCURACY_METERS));
        }
    }

    private String shortId(String prefix) {
        String random = UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ROOT);
        return (prefix + random).substring(0, 20);
    }

    private Double nullableDouble(ResultSet result, String column) throws SQLException {
        double value = result.getDouble(column);
        return result.wasNull() ? null : value;
    }

    private double numberOrZero(Double value) {
        return value == null ? 0 : value;
    }

    private void addNullable(JsonObject object, String key, Double value) {
        if (value == null) {
            object.add(key, null);
        } else {
            object.addProperty(key, value);
        }
    }

    private String dateValue(ResultSet result, String column) throws SQLException {
        Date date = result.getDate(column);
        return date == null ? "" : date.toLocalDate().toString();
    }

    private String value(ResultSet result, String column) throws SQLException {
        String value = result.getString(column);
        return value == null ? "" : value;
    }

    private String translateDay(String day) {
        return switch (day) {
            case "Monday" -> "Thứ Hai";
            case "Tuesday" -> "Thứ Ba";
            case "Wednesday" -> "Thứ Tư";
            case "Thursday" -> "Thứ Năm";
            case "Friday" -> "Thứ Sáu";
            case "Saturday" -> "Thứ Bảy";
            case "Sunday" -> "Chủ Nhật";
            default -> day;
        };
    }

    private record SessionQr(
            String qrId,
            String sessionStatus,
            String qrStatus,
            LocalDateTime expiredAt,
            double latitude,
            double longitude,
            double radius,
            String classSectionId,
            String classCode,
            String className,
            String room,
            String lecturerId) {
    }
}
