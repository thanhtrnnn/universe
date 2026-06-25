package com.universe.mobileapi;

import com.google.gson.JsonObject;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class LocationCalibrationService {

    static final double MAX_ACCURACY_METERS = 50.0;
    static final Duration SESSION_TTL = Duration.ofMinutes(2);

    private static final int MAX_ACTIVE_SESSIONS = 1_000;

    private final Map<String, CalibrationSession> sessions = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();
    private final SessionAuthorizer authorizer;
    private final Clock clock;

    LocationCalibrationService() {
        this(LocationCalibrationService::isAuthorizedSession, Clock.systemUTC());
    }

    LocationCalibrationService(SessionAuthorizer authorizer, Clock clock) {
        this.authorizer = authorizer;
        this.clock = clock;
    }

    JsonObject create(String classSessionId, String classSectionId, String lecturerId)
            throws SQLException {
        cleanupExpired();
        if (!authorizer.isAuthorized(classSessionId, classSectionId, lecturerId)) {
            throw new ServiceException(
                    403,
                    "Buổi học không thuộc lớp của giảng viên đã chọn.");
        }
        if (sessions.size() >= MAX_ACTIVE_SESSIONS) {
            throw new ServiceException(
                    503,
                    "Có quá nhiều phiên lấy vị trí đang hoạt động. Vui lòng thử lại.");
        }

        Instant expiresAt = clock.instant().plus(SESSION_TTL);
        String token;
        do {
            token = createToken();
        } while (sessions.putIfAbsent(
                token,
                new CalibrationSession(lecturerId, expiresAt)) != null);

        JsonObject response = new JsonObject();
        response.addProperty("token", token);
        response.addProperty("expiresAt", expiresAt.toString());
        response.addProperty("expiresInSeconds", SESSION_TTL.toSeconds());
        response.addProperty("maxAccuracyMeters", MAX_ACCURACY_METERS);
        return response;
    }

    JsonObject status(String token, String lecturerId) {
        CalibrationSession session = requireActive(token, lecturerId);
        JsonObject response = new JsonObject();
        if (session.location == null) {
            response.addProperty("status", "pending");
            response.addProperty("expiresAt", session.expiresAt.toString());
            return response;
        }

        response.addProperty("status", "ready");
        response.addProperty("latitude", session.location.latitude());
        response.addProperty("longitude", session.location.longitude());
        response.addProperty("accuracy", session.location.accuracy());
        response.addProperty("capturedAt", session.location.capturedAt().toString());
        return response;
    }

    JsonObject submit(
            String token,
            String lecturerId,
            double latitude,
            double longitude,
            double accuracy) {
        validateLocation(latitude, longitude, accuracy);
        CalibrationSession session = requireActive(token, lecturerId);
        synchronized (session) {
            if (session.location == null || accuracy < session.location.accuracy()) {
                session.location = new CalibratedLocation(
                        latitude,
                        longitude,
                        accuracy,
                        clock.instant());
            }
        }

        JsonObject response = JsonHttp.message(
                "Đã gửi vị trí phòng học về desktop.");
        response.addProperty("accuracy", session.location.accuracy());
        return response;
    }

    private CalibrationSession requireActive(String token, String lecturerId) {
        if (token == null || token.isBlank()) {
            throw new ServiceException(404, "Phiên lấy vị trí không tồn tại.");
        }
        CalibrationSession session = sessions.get(token);
        if (session == null) {
            throw new ServiceException(404, "Phiên lấy vị trí không tồn tại.");
        }
        if (!clock.instant().isBefore(session.expiresAt)) {
            sessions.remove(token, session);
            throw new ServiceException(
                    410,
                    "QR lấy vị trí đã hết hạn. Hãy tạo QR mới trên desktop.");
        }
        if (!session.lecturerId.equals(lecturerId)) {
            throw new ServiceException(
                    403,
                    "QR lấy vị trí thuộc tài khoản giảng viên khác.");
        }
        return session;
    }

    private void cleanupExpired() {
        Instant now = clock.instant();
        sessions.entrySet().removeIf(entry ->
                !now.isBefore(entry.getValue().expiresAt));
    }

    private String createToken() {
        byte[] bytes = new byte[24];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static void validateLocation(
            double latitude,
            double longitude,
            double accuracy) {
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90
                || !Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
            throw new ServiceException(400, "Tọa độ GPS không hợp lệ.");
        }
        if (!Double.isFinite(accuracy) || accuracy <= 0) {
            throw new ServiceException(400, "Sai số GPS không hợp lệ.");
        }
        if (accuracy > MAX_ACCURACY_METERS) {
            throw new ServiceException(
                    422,
                    "GPS phải đạt sai số tối đa " + (int) MAX_ACCURACY_METERS + " m để đặt tâm geofence.");
        }
    }

    private static boolean isAuthorizedSession(
            String classSessionId,
            String classSectionId,
            String lecturerId) throws SQLException {
        String sql = """
                SELECT 1
                FROM tblClassSession session
                JOIN tblClassSection section
                  ON section.id = session.tblClassSectionid
                WHERE session.id = ?
                  AND section.id = ?
                  AND section.tblLecturerid = ?
                """;
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, classSessionId);
            statement.setString(2, classSectionId);
            statement.setString(3, lecturerId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    interface SessionAuthorizer {
        boolean isAuthorized(String classSessionId, String classSectionId, String lecturerId)
                throws SQLException;
    }

    private static final class CalibrationSession {
        private final String lecturerId;
        private final Instant expiresAt;
        private volatile CalibratedLocation location;

        private CalibrationSession(String lecturerId, Instant expiresAt) {
            this.lecturerId = lecturerId;
            this.expiresAt = expiresAt;
        }
    }

    private record CalibratedLocation(
            double latitude,
            double longitude,
            double accuracy,
            Instant capturedAt) {
    }
}
