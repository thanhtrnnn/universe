package com.universe.mobileapi;

import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class AuthService {

    LoginData authenticate(String username, String password) throws SQLException {
        String sql = """
                SELECT u.id, u.fullName, u.email, u.status, u.role,
                       s.course, s.major, s.className,
                       l.department, l.degree
                FROM tblUser u
                LEFT JOIN tblStudent s ON s.id = u.id
                LEFT JOIN tblLecturer l ON l.id = u.id
                WHERE u.username = ? AND u.password = ?
                """;
        try (Connection connection = Database.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()
                        || !"active".equalsIgnoreCase(result.getString("status"))) {
                    throw new ServiceException(
                            401,
                            "Tên đăng nhập hoặc mật khẩu không chính xác.");
                }
                String role = value(result, "role");
                if (!"Student".equalsIgnoreCase(role)
                        && !"Lecturer".equalsIgnoreCase(role)) {
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

    private String value(ResultSet result, String column) throws SQLException {
        String value = result.getString(column);
        return value == null ? "" : value;
    }

    record LoginData(String userId, String role, JsonObject profile) {
    }
}
