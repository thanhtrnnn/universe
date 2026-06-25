package com.universe.mobileapi;

import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class AuthService {

    LoginData authenticate(String username, String password) throws SQLException {
        String sql = """
                SELECT u.id, u.fullName, u.email, u.status, u.role, u.password,
                       s.course, s.major, s.className,
                       l.department, l.degree
                FROM tblUser u
                LEFT JOIN tblStudent s ON s.id = u.id
                LEFT JOIN tblLecturer l ON l.id = u.id
                WHERE u.username = ?
                """;
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new ServiceException(401, "Tên đăng nhập hoặc mật khẩu không chính xác.");
                }
                String storedHash = result.getString("password");
                boolean passwordValid;
                if (storedHash != null && storedHash.startsWith("$2a$")) {
                    passwordValid = org.mindrot.jbcrypt.BCrypt.checkpw(password, storedHash);
                } else {
                    // Legacy plaintext — accept and schedule upgrade (fire-and-forget)
                    passwordValid = password.equals(storedHash);
                    if (passwordValid) {
                        final String userId = result.getString("id");
                        final String newHash = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());
                        new Thread(() -> upgradePasswordHash(userId, newHash)).start();
                    }
                }
                if (!passwordValid || !"active".equalsIgnoreCase(result.getString("status"))) {
                    throw new ServiceException(401, "Tên đăng nhập hoặc mật khẩu không chính xác.");
                }
                String role = value(result, "role");
                if (!"Student".equalsIgnoreCase(role) && !"Lecturer".equalsIgnoreCase(role)) {
                    throw new ServiceException(
                            403,
                            "Ứng dụng Android hiện hỗ trợ tài khoản sinh viên và giảng viên.");
                }
                JsonObject profile = new JsonObject();
                profile.addProperty("id", result.getString("id"));
                profile.addProperty("fullName", value(result, "fullName"));
                profile.addProperty("email", value(result, "email"));
                profile.addProperty("role", role);
                if ("Student".equalsIgnoreCase(role)) {
                    profile.addProperty("course", value(result, "course"));
                    profile.addProperty("major", value(result, "major"));
                    profile.addProperty("className", value(result, "className"));
                } else {
                    profile.addProperty("department", value(result, "department"));
                    profile.addProperty("degree", value(result, "degree"));
                }
                return new LoginData(result.getString("id"), role, profile);
            }
        }
    }

    private void upgradePasswordHash(String userId, String newHash) {
        String sql = "UPDATE tblUser SET password = ? WHERE id = ?";
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newHash);
            statement.setString(2, userId);
            statement.executeUpdate();
        } catch (SQLException ignored) {
            // non-critical migration
        }
    }

    private String value(ResultSet result, String column) throws SQLException {
        String value = result.getString(column);
        return value == null ? "" : value;
    }

    record LoginData(String userId, String role, JsonObject profile) {
    }
}
