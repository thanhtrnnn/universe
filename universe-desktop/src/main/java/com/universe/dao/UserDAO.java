package com.universe.dao;

import com.universe.entity.Admin;
import com.universe.entity.Lecturer;
import com.universe.entity.Student;
import com.universe.entity.User;
import com.universe.util.RedisUtil;
import org.mindrot.jbcrypt.BCrypt;

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

    private static final String USER_SELECT =
            "SELECT u.*, a.department AS adminDepartment, " +
            "l.department AS lecturerDepartment, l.degree, " +
            "s.course, s.major, s.className " +
            "FROM tblUser u " +
            "LEFT JOIN tblAdmin a ON a.id = u.id " +
            "LEFT JOIN tblLecturer l ON l.id = u.id " +
            "LEFT JOIN tblStudent s ON s.id = u.id ";

    /**
     * Xác thực đăng nhập. Khớp username trong tblUser, so sánh mật khẩu với bcrypt.
     * Khi thành công, lưu phiên vào Redis (session:{userId}).
     * @return User (đúng kiểu con theo role) hoặc null nếu sai / bị khóa.
     */
    public User checkLogin(String username, String password) {
        String sql = USER_SELECT + "WHERE u.username = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if ("inactive".equalsIgnoreCase(rs.getString("status"))) {
                        return null; // tài khoản bị vô hiệu hóa
                    }
                    String storedHash = rs.getString("password");
                    boolean matched;
                    if (storedHash != null && storedHash.startsWith("$2a$")) {
                        // bcrypt hash
                        matched = BCrypt.checkpw(password, storedHash);
                    } else {
                        // legacy plaintext
                        matched = password.equals(storedHash);
                        if (matched) {
                            // silently upgrade to bcrypt
                            String userId = rs.getString("id");
                            updatePasswordHash(userId, BCrypt.hashpw(password, BCrypt.gensalt()));
                        }
                    }
                    if (!matched) return null;

                    User u = mapUser(rs);
                    // Store plaintext in-memory for LocationCalibrationClient in the UI layer
                    u.setPassword(password);
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
        String sql = USER_SELECT +
                     "WHERE u.id ILIKE ? OR u.fullName ILIKE ? OR u.username ILIKE ? " +
                     "ORDER BY u.id";
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

    /** Thêm người dùng mới */
    public boolean insertUser(User u) {
        String sqlUser = "INSERT INTO tblUser (id, fullName, dob, gender, phone, username, password, status, role, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sqlUser)) {
                ps.setString(1, u.getId());
                ps.setString(2, u.getFullName());
                ps.setDate(3, u.getDob() != null ? java.sql.Date.valueOf(u.getDob()) : null);
                ps.setString(4, u.getGender());
                ps.setString(5, u.getPhone());
                ps.setString(6, u.getUsername());
                ps.setString(7, BCrypt.hashpw(u.getPassword(), BCrypt.gensalt()));
                ps.setString(8, u.getStatus() != null ? u.getStatus() : "active");
                ps.setString(9, u.getRole());
                ps.setString(10, u.getEmail());
                ps.executeUpdate();

                if ("Admin".equalsIgnoreCase(u.getRole())) {
                    String sqlRole = "INSERT INTO tblAdmin (id, department) VALUES (?, ?)";
                    try (PreparedStatement psRole = con.prepareStatement(sqlRole)) {
                        psRole.setString(1, u.getId());
                        psRole.setString(2, u instanceof Admin ? ((Admin)u).getDepartment() : "");
                        psRole.executeUpdate();
                    }
                } else if ("Lecturer".equalsIgnoreCase(u.getRole())) {
                    String sqlRole = "INSERT INTO tblLecturer (id, department, degree) VALUES (?, ?, ?)";
                    try (PreparedStatement psRole = con.prepareStatement(sqlRole)) {
                        psRole.setString(1, u.getId());
                        psRole.setString(2, u instanceof Lecturer ? ((Lecturer)u).getDepartment() : "");
                        psRole.setString(3, u instanceof Lecturer ? ((Lecturer)u).getDegree() : "");
                        psRole.executeUpdate();
                    }
                } else if ("Student".equalsIgnoreCase(u.getRole())) {
                    String sqlRole = "INSERT INTO tblStudent (id, course, major, className) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement psRole = con.prepareStatement(sqlRole)) {
                        psRole.setString(1, u.getId());
                        psRole.setString(2, u instanceof Student ? ((Student)u).getCourse() : "");
                        psRole.setString(3, u instanceof Student ? ((Student)u).getMajor() : "");
                        psRole.setString(4, u instanceof Student ? ((Student)u).getClassName() : "");
                        psRole.executeUpdate();
                    }
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi insertUser: " + e.getMessage(), e);
        }
    }

    /** Cập nhật thông tin chung và bảng chi tiết theo vai trò hiện tại. */
    public boolean updateUser(User u) {
        String sql = "UPDATE tblUser SET fullName = ?, dob = ?, gender = ?, phone = ?, " +
                     "email = ?, status = ? " +
                     "WHERE id = ?";
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, u.getFullName());
                    ps.setDate(2, u.getDob() != null ? java.sql.Date.valueOf(u.getDob()) : null);
                    ps.setString(3, u.getGender());
                    ps.setString(4, u.getPhone());
                    ps.setString(5, u.getEmail());
                    ps.setString(6, u.getStatus());
                    ps.setString(7, u.getId());
                    if (ps.executeUpdate() == 0) {
                        con.rollback();
                        return false;
                    }
                }
                updateRoleDetails(con, u);
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi updateUser: " + e.getMessage(), e);
        }
    }

    public User findById(String id) {
        String sql = USER_SELECT + "WHERE u.id = ?";
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

    /** Xóa người dùng (tài khoản) */
    public boolean deleteUser(String id) {
        String sql = "DELETE FROM tblUser WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi deleteUser: " + e.getMessage(), e);
        }
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

    public List<User> findUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = USER_SELECT +
                "WHERE u.role = ? AND u.status = 'active' ORDER BY u.fullName";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi findUsersByRole: " + e.getMessage(), e);
        }
        return users;
    }

    /**
     * Lấy sinh viên đang hoạt động thuộc ít nhất một lớp học phần do giảng viên phụ trách.
     */
    public List<User> findStudentsByLecturer(String lecturerId) {
        List<User> users = new ArrayList<>();
        String sql = USER_SELECT +
                "WHERE u.role = 'Student' AND u.status = 'active' " +
                "AND EXISTS (" +
                "SELECT 1 FROM tblCourseRecord cr " +
                "JOIN tblClassSection cs ON cs.id = cr.tblClassSectionid " +
                "WHERE cr.tblStudentid = u.id AND cs.tblLecturerid = ?" +
                ") ORDER BY u.fullName, u.id";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi findStudentsByLecturer: " + e.getMessage(), e);
        }
        return users;
    }

    public List<String> findActiveUserIds(String role) {
        List<String> ids = new ArrayList<>();
        String sql = role == null
                ? "SELECT id FROM tblUser WHERE status = 'active' ORDER BY id"
                : "SELECT id FROM tblUser WHERE status = 'active' AND role = ? ORDER BY id";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (role != null) {
                ps.setString(1, role);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getString("id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi findActiveUserIds: " + e.getMessage(), e);
        }
        return ids;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        User u;
        if ("Admin".equalsIgnoreCase(role)) {
            Admin admin = new Admin();
            admin.setDepartment(rs.getString("adminDepartment"));
            u = admin;
        } else if ("Lecturer".equalsIgnoreCase(role)) {
            Lecturer lecturer = new Lecturer();
            lecturer.setDepartment(rs.getString("lecturerDepartment"));
            lecturer.setDegree(rs.getString("degree"));
            u = lecturer;
        } else {
            Student student = new Student();
            student.setCourse(rs.getString("course"));
            student.setMajor(rs.getString("major"));
            student.setClassName(rs.getString("className"));
            u = student;
        }
        u.setId(rs.getString("id"));
        u.setFullName(rs.getString("fullName"));
        u.setDob(rs.getDate("dob") != null ? rs.getDate("dob").toLocalDate() : null);
        u.setGender(rs.getString("gender"));
        u.setPhone(rs.getString("phone"));
        u.setUsername(rs.getString("username"));
        // password is NOT set here; checkLogin sets it explicitly after verification
        u.setStatus(rs.getString("status"));
        u.setRole(role);
        u.setEmail(rs.getString("email"));
        return u;
    }

    /** Silently upgrade a legacy plaintext password to bcrypt. */
    private void updatePasswordHash(String userId, String hash) {
        String sql = "UPDATE tblUser SET password = ? WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setString(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            // non-critical migration; ignore failure
        }
    }

    private void updateRoleDetails(Connection con, User u) throws SQLException {
        if (u instanceof Admin admin) {
            String sql = "INSERT INTO tblAdmin (id, department) VALUES (?, ?) " +
                    "ON CONFLICT (id) DO UPDATE SET department = EXCLUDED.department";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, u.getId());
                ps.setString(2, admin.getDepartment());
                ps.executeUpdate();
            }
        } else if (u instanceof Lecturer lecturer) {
            String sql = "INSERT INTO tblLecturer (id, department, degree) VALUES (?, ?, ?) " +
                    "ON CONFLICT (id) DO UPDATE SET department = EXCLUDED.department, " +
                    "degree = EXCLUDED.degree";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, u.getId());
                ps.setString(2, lecturer.getDepartment());
                ps.setString(3, lecturer.getDegree());
                ps.executeUpdate();
            }
        } else if (u instanceof Student student) {
            String sql = "INSERT INTO tblStudent (id, course, major, className) VALUES (?, ?, ?, ?) " +
                    "ON CONFLICT (id) DO UPDATE SET course = EXCLUDED.course, " +
                    "major = EXCLUDED.major, className = EXCLUDED.className";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, u.getId());
                ps.setString(2, student.getCourse());
                ps.setString(3, student.getMajor());
                ps.setString(4, student.getClassName());
                ps.executeUpdate();
            }
        }
    }
}
