package com.universe.dao;

import com.universe.entity.Admin;
import com.universe.entity.Lecturer;
import com.universe.entity.Student;
import com.universe.entity.User;
import com.universe.util.RedisUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO người dùng (Chương 4.3.2.1 - chức năng sửa thông tin người dùng + login).
 * Phương thức: checkLogin(), searchUser(), updateUser().
 */
public class UserDAO extends DAO {

    /**
     * Xác thực đăng nhập. Khớp username + password trong tblUser.
     * Khi thành công, lưu phiên vào Redis (session:{userId}).
     * @return User (đúng kiểu con theo role) hoặc null nếu sai / bị khóa.
     */
    public User checkLogin(String username, String password) {
        String sql = "SELECT * FROM tblUser WHERE username = ? AND password = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if ("inactive".equalsIgnoreCase(rs.getString("status"))) {
                        return null; // tài khoản bị vô hiệu hóa
                    }
                    User u = mapUser(rs);
                    // Bổ sung: lưu token phiên vào Redis
                    try {
                        RedisUtil.saveSession(u.getId());
                    } catch (Exception ignore) {
                        // Redis lỗi không chặn đăng nhập
                    }
                    return u;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi checkLogin: " + e.getMessage(), e);
        }
        return null;
    }

    /** Đăng xuất: xóa phiên khỏi Redis. */
    public void logout(String userId) {
        try {
            RedisUtil.deleteSession(userId);
        } catch (Exception ignore) {
        }
    }

    /** Tìm người dùng theo từ khóa (id / họ tên / username). */
    public List<User> searchUser(String keyword) {
        List<User> result = new ArrayList<>();
        String sql = "SELECT * FROM tblUser " +
                     "WHERE id ILIKE ? OR fullName ILIKE ? OR username ILIKE ? " +
                     "ORDER BY id";
        String like = "%" + (keyword == null ? "" : keyword) + "%";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi searchUser: " + e.getMessage(), e);
        }
        return result;
    }

    /** Cập nhật thông tin người dùng (họ tên, sđt, email, trạng thái, vai trò). */
    public boolean updateUser(User u) {
        String sql = "UPDATE tblUser SET fullName = ?, phone = ?, email = ?, status = ?, role = ? " +
                     "WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getPhone());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getStatus());
            ps.setString(5, u.getRole());
            ps.setString(6, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi updateUser: " + e.getMessage(), e);
        }
    }

    public User findById(String id) {
        String sql = "SELECT * FROM tblUser WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi findById: " + e.getMessage(), e);
        }
        return null;
    }

    /** Lấy danh sách id sinh viên đã đăng ký một lớp học phần (để gửi thông báo). */
    public List<String> findStudentIdsByClassSection(String classSectionId) {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT tblStudentid FROM tblCourseRecord WHERE tblClassSectionid = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, classSectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi findStudentIdsByClassSection: " + e.getMessage(), e);
        }
        return ids;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        User u;
        if ("Admin".equalsIgnoreCase(role)) {
            u = new Admin();
        } else if ("Lecturer".equalsIgnoreCase(role)) {
            u = new Lecturer();
        } else {
            u = new Student();
        }
        u.setId(rs.getString("id"));
        u.setFullName(rs.getString("fullName"));
        u.setDob(rs.getDate("dob") != null ? rs.getDate("dob").toLocalDate() : null);
        u.setGender(rs.getString("gender"));
        u.setPhone(rs.getString("phone"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setStatus(rs.getString("status"));
        u.setRole(role);
        u.setEmail(rs.getString("email"));
        return u;
    }
}
