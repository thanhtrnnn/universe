package com.universe.mobileapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

final class LecturerService {

    private static final Set<String> ATTENDANCE_STATUSES =
            Set.of("PRESENT", "ABSENT", "LATE");
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    JsonObject dashboard(String lecturerId) throws SQLException {
        JsonObject dashboard = new JsonObject();
        try (Connection connection = Database.open()) {
            dashboard.addProperty(
                    "classCount",
                    count(connection,
                            "SELECT COUNT(*) FROM tblClassSection WHERE tblLecturerid = ?",
                            lecturerId));
            dashboard.addProperty(
                    "studentCount",
                    count(connection, """
                            SELECT COUNT(DISTINCT cr.tblStudentid)
                            FROM tblCourseRecord cr
                            JOIN tblClassSection cs ON cs.id = cr.tblClassSectionid
                            WHERE cs.tblLecturerid = ?
                            """, lecturerId));
            dashboard.addProperty(
                    "scheduleCount",
                    count(connection, """
                            SELECT COUNT(*)
                            FROM tblSchedule schedule
                            JOIN tblClassSection cs
                              ON cs.id = schedule.tblClassSectionid
                            WHERE cs.tblLecturerid = ?
                            """, lecturerId));
        }
        dashboard.add("classes", classes(lecturerId));
        return dashboard;
    }

    JsonArray classes(String lecturerId) throws SQLException {
        String sql = """
                SELECT cs.id, cs.classId, cs.name, cs.semester, cs.year,
                       cs.maxStudents, cs.status,
                       COUNT(cr.id) AS enrolled
                FROM tblClassSection cs
                LEFT JOIN tblCourseRecord cr ON cr.tblClassSectionid = cs.id
                WHERE cs.tblLecturerid = ?
                GROUP BY cs.id, cs.classId, cs.name, cs.semester, cs.year,
                         cs.maxStudents, cs.status
                ORDER BY cs.classId
                """;
        JsonArray array = new JsonArray();
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lecturerId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    JsonObject item = new JsonObject();
                    item.addProperty("id", result.getString("id"));
                    item.addProperty("code", value(result, "classId"));
                    item.addProperty("name", value(result, "name"));
                    item.addProperty("semester", value(result, "semester"));
                    item.addProperty("year", value(result, "year"));
                    item.addProperty("status", value(result, "status"));
                    item.addProperty("enrolled", result.getInt("enrolled"));
                    item.addProperty("capacity", result.getInt("maxStudents"));
                    array.add(item);
                }
            }
        }
        return array;
    }

    JsonArray schedule(String lecturerId) throws SQLException {
        String sql = """
                SELECT cs.classId, cs.name, schedule.dayOfWeek,
                       schedule.startPeriod, schedule.endPeriod, schedule.room,
                       schedule.appliedFrom, schedule.appliedTo
                FROM tblSchedule schedule
                JOIN tblClassSection cs ON cs.id = schedule.tblClassSectionid
                WHERE cs.tblLecturerid = ?
                ORDER BY CASE schedule.dayOfWeek
                    WHEN 'Monday' THEN 1 WHEN 'Tuesday' THEN 2
                    WHEN 'Wednesday' THEN 3 WHEN 'Thursday' THEN 4
                    WHEN 'Friday' THEN 5 WHEN 'Saturday' THEN 6
                    WHEN 'Sunday' THEN 7 ELSE 8 END,
                    schedule.startPeriod
                """;
        JsonArray array = new JsonArray();
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lecturerId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    JsonObject item = new JsonObject();
                    item.addProperty("classCode", value(result, "classId"));
                    item.addProperty("className", value(result, "name"));
                    item.addProperty("dayOfWeek",
                            translateDay(value(result, "dayOfWeek")));
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

    JsonArray sessions(String lecturerId, String sectionId) throws SQLException {
        requireClassOwnership(lecturerId, sectionId);
        String sql = """
                SELECT id, date, startPeriod, endPeriod, room, status
                FROM tblClassSession
                WHERE tblClassSectionid = ?
                ORDER BY date DESC, startPeriod
                """;
        JsonArray array = new JsonArray();
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sectionId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    JsonObject item = new JsonObject();
                    item.addProperty("id", result.getString("id"));
                    item.addProperty("date", dateValue(result, "date"));
                    item.addProperty("startPeriod", result.getInt("startPeriod"));
                    item.addProperty("endPeriod", result.getInt("endPeriod"));
                    item.addProperty("room", value(result, "room"));
                    item.addProperty("status", value(result, "status"));
                    array.add(item);
                }
            }
        }
        return array;
    }

    JsonArray attendance(String lecturerId, String sessionId) throws SQLException {
        requireSessionOwnership(lecturerId, sessionId);
        String sql = """
                SELECT cr.tblStudentid, u.fullName,
                       attendance.attendedAt, attendance.method, attendance.status
                FROM tblClassSession session
                JOIN tblCourseRecord cr
                  ON cr.tblClassSectionid = session.tblClassSectionid
                JOIN tblUser u ON u.id = cr.tblStudentid
                LEFT JOIN tblAttendance attendance
                  ON attendance.tblClassSessionid = session.id
                 AND attendance.tblStudentid = cr.tblStudentid
                WHERE session.id = ?
                ORDER BY cr.tblStudentid
                """;
        JsonArray array = new JsonArray();
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sessionId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    JsonObject item = new JsonObject();
                    item.addProperty("studentId", result.getString("tblStudentid"));
                    item.addProperty("studentName", value(result, "fullName"));
                    Timestamp attendedAt = result.getTimestamp("attendedAt");
                    item.addProperty("attendedAt", attendedAt == null
                            ? ""
                            : attendedAt.toLocalDateTime().format(DATE_TIME_FORMAT));
                    item.addProperty("method", attendedAt == null
                            ? "-"
                            : value(result, "method"));
                    item.addProperty("status", attendedAt == null
                            ? "ABSENT"
                            : value(result, "status"));
                    array.add(item);
                }
            }
        }
        return array;
    }

    JsonObject updateAttendance(
            String lecturerId,
            String sessionId,
            String studentId,
            String status) throws SQLException {
        requireSessionOwnership(lecturerId, sessionId);
        String normalizedStatus = status == null
                ? ""
                : status.trim().toUpperCase(Locale.ROOT);
        if (!ATTENDANCE_STATUSES.contains(normalizedStatus)) {
            throw new ServiceException(400, "Trạng thái điểm danh không hợp lệ.");
        }
        try (Connection connection = Database.open()) {
            if (!isStudentInSession(connection, sessionId, studentId)) {
                throw new ServiceException(
                        404,
                        "Sinh viên không thuộc lớp của buổi học này.");
            }
            String update = """
                    UPDATE tblAttendance
                    SET status = ?, method = 'MANUAL', attendedAt = ?
                    WHERE tblClassSessionid = ? AND tblStudentid = ?
                    """;
            try (PreparedStatement statement = connection.prepareStatement(update)) {
                statement.setString(1, normalizedStatus);
                statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                statement.setString(3, sessionId);
                statement.setString(4, studentId);
                if (statement.executeUpdate() == 0) {
                    try (PreparedStatement insert = connection.prepareStatement("""
                            INSERT INTO tblAttendance
                                (id, attendedAt, method, status,
                                 tblClassSessionid, tblStudentid)
                            VALUES (?, ?, 'MANUAL', ?, ?, ?)
                            """)) {
                        insert.setString(1, shortId("ATT"));
                        insert.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                        insert.setString(3, normalizedStatus);
                        insert.setString(4, sessionId);
                        insert.setString(5, studentId);
                        insert.executeUpdate();
                    }
                }
            }
        }
        return JsonHttp.message("Đã cập nhật điểm danh.");
    }

    JsonArray grades(String lecturerId, String sectionId) throws SQLException {
        requireClassOwnership(lecturerId, sectionId);
        String sql = """
                SELECT cr.id, cr.tblStudentid, u.fullName,
                       cr.score1, cr.score2, cr.score3, cr.examScore
                FROM tblCourseRecord cr
                JOIN tblUser u ON u.id = cr.tblStudentid
                WHERE cr.tblClassSectionid = ?
                ORDER BY cr.tblStudentid
                """;
        JsonArray array = new JsonArray();
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sectionId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    JsonObject item = new JsonObject();
                    item.addProperty("recordId", result.getString("id"));
                    item.addProperty("studentId", result.getString("tblStudentid"));
                    item.addProperty("studentName", value(result, "fullName"));
                    addNullable(item, "score1", nullableDouble(result, "score1"));
                    addNullable(item, "score2", nullableDouble(result, "score2"));
                    addNullable(item, "score3", nullableDouble(result, "score3"));
                    addNullable(item, "examScore", nullableDouble(result, "examScore"));
                    array.add(item);
                }
            }
        }
        return array;
    }

    JsonObject updateGrade(
            String lecturerId,
            String recordId,
            Double score1,
            Double score2,
            Double score3,
            Double examScore) throws SQLException {
        validateScore(score1);
        validateScore(score2);
        validateScore(score3);
        validateScore(examScore);
        String sql = """
                UPDATE tblCourseRecord record
                SET score1 = ?, score2 = ?, score3 = ?, examScore = ?
                FROM tblClassSection section
                WHERE record.id = ?
                  AND section.id = record.tblClassSectionid
                  AND section.tblLecturerid = ?
                """;
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setNullableDouble(statement, 1, score1);
            setNullableDouble(statement, 2, score2);
            setNullableDouble(statement, 3, score3);
            setNullableDouble(statement, 4, examScore);
            statement.setString(5, recordId);
            statement.setString(6, lecturerId);
            if (statement.executeUpdate() == 0) {
                throw new ServiceException(
                        404,
                        "Không tìm thấy bản ghi điểm thuộc lớp của giảng viên.");
            }
        }
        return JsonHttp.message("Đã cập nhật điểm.");
    }

    JsonObject sendNotification(
            String lecturerId,
            String sectionId,
            String title,
            String content) throws SQLException {
        requireClassOwnership(lecturerId, sectionId);
        String normalizedTitle = title == null ? "" : title.trim();
        String normalizedContent = content == null ? "" : content.trim();
        if (normalizedTitle.isEmpty() || normalizedContent.isEmpty()) {
            throw new ServiceException(
                    400,
                    "Tiêu đề và nội dung thông báo không được để trống.");
        }
        if (normalizedTitle.length() > 200) {
            throw new ServiceException(400, "Tiêu đề thông báo tối đa 200 ký tự.");
        }

        int sent = 0;
        try (Connection connection = Database.open();
             PreparedStatement students = connection.prepareStatement("""
                     SELECT tblStudentid
                     FROM tblCourseRecord
                     WHERE tblClassSectionid = ?
                     ORDER BY tblStudentid
                     """)) {
            students.setString(1, sectionId);
            try (ResultSet result = students.executeQuery();
                 PreparedStatement insert = connection.prepareStatement("""
                         INSERT INTO tblNotification
                             (id, title, content, recipientType, sentAt, tblUserid)
                         VALUES (?, ?, ?, ?, ?, ?)
                         """)) {
                while (result.next()) {
                    insert.setString(1, shortId("NTF"));
                    insert.setString(2, normalizedTitle);
                    insert.setString(3, normalizedContent);
                    insert.setString(4, "class:" + sectionId);
                    insert.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                    insert.setString(6, result.getString("tblStudentid"));
                    insert.addBatch();
                    sent++;
                }
                if (sent > 0) {
                    insert.executeBatch();
                }
            }
        }
        JsonObject response = JsonHttp.message(
                "Đã gửi thông báo tới " + sent + " sinh viên.");
        response.addProperty("sent", sent);
        return response;
    }

    private void requireClassOwnership(String lecturerId, String sectionId)
            throws SQLException {
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT 1 FROM tblClassSection
                     WHERE id = ? AND tblLecturerid = ?
                     """)) {
            statement.setString(1, sectionId);
            statement.setString(2, lecturerId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new ServiceException(
                            404,
                            "Không tìm thấy lớp học phần thuộc giảng viên.");
                }
            }
        }
    }

    private void requireSessionOwnership(String lecturerId, String sessionId)
            throws SQLException {
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT 1
                     FROM tblClassSession session
                     JOIN tblClassSection section
                       ON section.id = session.tblClassSectionid
                     WHERE session.id = ? AND section.tblLecturerid = ?
                     """)) {
            statement.setString(1, sessionId);
            statement.setString(2, lecturerId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new ServiceException(
                            404,
                            "Không tìm thấy buổi học thuộc giảng viên.");
                }
            }
        }
    }

    private boolean isStudentInSession(
            Connection connection,
            String sessionId,
            String studentId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT 1
                FROM tblClassSession session
                JOIN tblCourseRecord record
                  ON record.tblClassSectionid = session.tblClassSectionid
                WHERE session.id = ? AND record.tblStudentid = ?
                """)) {
            statement.setString(1, sessionId);
            statement.setString(2, studentId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private int count(Connection connection, String sql, String lecturerId)
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lecturerId);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getInt(1);
            }
        }
    }

    private void validateScore(Double score) {
        if (score != null
                && (!Double.isFinite(score) || score < 0 || score > 10)) {
            throw new ServiceException(400, "Điểm phải nằm trong khoảng 0 đến 10.");
        }
    }

    private void setNullableDouble(
            PreparedStatement statement,
            int index,
            Double value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.DOUBLE);
        } else {
            statement.setDouble(index, value);
        }
    }

    private Double nullableDouble(ResultSet result, String column) throws SQLException {
        double value = result.getDouble(column);
        return result.wasNull() ? null : value;
    }

    private void addNullable(JsonObject object, String key, Double value) {
        if (value == null) {
            object.add(key, null);
        } else {
            object.addProperty(key, value);
        }
    }

    private String shortId(String prefix) {
        String random = UUID.randomUUID().toString()
                .replace("-", "")
                .toUpperCase(Locale.ROOT);
        return (prefix + random).substring(0, 20);
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
}
