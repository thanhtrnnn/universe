package com.universe.dao;

import com.universe.entity.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO học phần (Chương 4.3.2.4 thêm học phần, 4.3.2.5/4.3.2.6 tra cứu).
 * Phương thức: getListCourse(), createCourse(), searchCourse(), searchCourseRegistration().
 */
public class CourseDAO extends DAO {

    public List<Course> getListCourse() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM tblCourse ORDER BY id";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getListCourse: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean createCourse(Course c) {
        String sql = "INSERT INTO tblCourse (id, credits, name, department) VALUES (?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getId());
            ps.setInt(2, c.getCredits());
            ps.setString(3, c.getName());
            ps.setString(4, c.getDepartment());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi createCourse: " + e.getMessage(), e);
        }
    }

    /** Kiểm tra mã học phần đã tồn tại chưa (chống trùng - test case 5.2.2.4). */
    public boolean existsById(String id) {
        String sql = "SELECT 1 FROM tblCourse WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi existsById: " + e.getMessage(), e);
        }
    }

    public Course findById(String id) {
        String sql = "SELECT * FROM tblCourse WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi findById: " + e.getMessage(), e);
        }
    }

    public List<Course> searchCourse(String keyword) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM tblCourse WHERE id ILIKE ? OR name ILIKE ? ORDER BY id";
        String like = "%" + (keyword == null ? "" : keyword) + "%";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi searchCourse: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean updateCourse(Course c) {
        String sql = "UPDATE tblCourse SET credits = ?, name = ?, department = ? WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getCredits());
            ps.setString(2, c.getName());
            ps.setString(3, c.getDepartment());
            ps.setString(4, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi updateCourse: " + e.getMessage(), e);
        }
    }

    public boolean deleteCourse(String id) {
        String sql = "DELETE FROM tblCourse WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi deleteCourse: " + e.getMessage(), e);
        }
    }

    /** Tra cứu học phần mở đăng ký (Chương 4.3.2.6). */
    public List<Course> searchCourseRegistration(String keyword) {
        return searchCourse(keyword);
    }

    private Course map(ResultSet rs) throws SQLException {
        return new Course(
                rs.getString("id"),
                rs.getInt("credits"),
                rs.getString("name"),
                rs.getString("department"));
    }
}
