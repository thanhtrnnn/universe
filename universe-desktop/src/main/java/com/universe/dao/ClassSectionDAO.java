package com.universe.dao;

import com.universe.entity.ClassSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO lớp học phần (dùng trong các chức năng 4.3.2.2/3/5/6/7/10).
 * Phương thức: searchClassSection(), getListClassSection(),
 * getListClassSectionRegistration(), updateClassSection(),
 * checkCapacity(), registerClassSection().
 */
public class ClassSectionDAO extends DAO {

    /** Tìm lớp học phần theo từ khóa (mã lớp / tên). */
    public List<ClassSection> searchClassSection(String keyword) {
        List<ClassSection> list = new ArrayList<>();
        String sql = "SELECT * FROM tblClassSection WHERE classId ILIKE ? OR name ILIKE ? ORDER BY id";
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
            throw new RuntimeException("Lỗi searchClassSection: " + e.getMessage(), e);
        }
        return list;
    }

    public List<ClassSection> getListClassSection() {
        List<ClassSection> list = new ArrayList<>();
        String sql = "SELECT * FROM tblClassSection ORDER BY id";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getListClassSection: " + e.getMessage(), e);
        }
        return list;
    }

    public List<ClassSection> getByLecturer(String lecturerId) {
        List<ClassSection> list = new ArrayList<>();
        String sql = "SELECT * FROM tblClassSection WHERE tblLecturerid = ? ORDER BY classId";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getByLecturer: " + e.getMessage(), e);
        }
        return list;
    }

    /** Danh sách lớp học phần thuộc một học phần (để đăng ký). */
    public List<ClassSection> getListClassSectionRegistration(String courseId) {
        List<ClassSection> list = new ArrayList<>();
        String sql = "SELECT * FROM tblClassSection WHERE tblCourseid = ? ORDER BY id";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getListClassSectionRegistration: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean insertClassSection(ClassSection c) {
        String sql = "INSERT INTO tblClassSection (id, classId, name, semester, year, maxStudents, status, tblCourseid, tblLecturerid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getId());
            ps.setString(2, c.getClassId());
            ps.setString(3, c.getName());
            ps.setString(4, c.getSemester());
            ps.setString(5, c.getYear());
            ps.setInt(6, c.getMaxStudents());
            ps.setString(7, c.getStatus());
            ps.setString(8, c.getCourseId());
            ps.setString(9, c.getLecturerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi insertClassSection: " + e.getMessage(), e);
        }
    }

    public boolean updateClassSection(ClassSection c) {
        String sql = "UPDATE tblClassSection SET classId = ?, name = ?, semester = ?, year = ?, " +
                     "maxStudents = ?, status = ?, tblCourseid = ?, tblLecturerid = ? WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getClassId());
            ps.setString(2, c.getName());
            ps.setString(3, c.getSemester());
            ps.setString(4, c.getYear());
            ps.setInt(5, c.getMaxStudents());
            ps.setString(6, c.getStatus());
            ps.setString(7, c.getCourseId());
            ps.setString(8, c.getLecturerId());
            ps.setString(9, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi updateClassSection: " + e.getMessage(), e);
        }
    }

    public boolean deleteClassSection(String id) {
        String sql = "DELETE FROM tblClassSection WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi deleteClassSection: " + e.getMessage(), e);
        }
    }

    /**
     * Kiểm tra lớp còn chỗ: số bản ghi đã đăng ký < maxStudents
     * (Chương 4.3.2.6 - checkCapacity).
     */
    public boolean checkCapacity(String classSectionId) {
        String sql = "SELECT cs.maxStudents, " +
                     "(SELECT COUNT(*) FROM tblCourseRecord cr WHERE cr.tblClassSectionid = cs.id) AS enrolled " +
                     "FROM tblClassSection cs WHERE cs.id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, classSectionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("enrolled") < rs.getInt("maxStudents");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi checkCapacity: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Đánh dấu/đóng đăng ký khi sĩ số đầy. Ở mức desktop, sĩ số được tính động
     * từ tblCourseRecord; phương thức này cập nhật trạng thái lớp nếu cần.
     */
    public boolean registerClassSection(String classSectionId) {
        String sql = "UPDATE tblClassSection SET status = " +
                     "CASE WHEN (SELECT COUNT(*) FROM tblCourseRecord cr WHERE cr.tblClassSectionid = ?) + 1 " +
                     ">= maxStudents THEN 'full' ELSE 'open' END WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, classSectionId);
            ps.setString(2, classSectionId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi registerClassSection: " + e.getMessage(), e);
        }
    }

    public ClassSection findById(String id) {
        String sql = "SELECT * FROM tblClassSection WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi findById: " + e.getMessage(), e);
        }
        return null;
    }

    private ClassSection map(ResultSet rs) throws SQLException {
        return new ClassSection(
                rs.getString("id"),
                rs.getString("classId"),
                rs.getString("name"),
                rs.getString("semester"),
                rs.getString("year"),
                rs.getInt("maxStudents"),
                rs.getString("status"),
                rs.getString("tblCourseid"),
                rs.getString("tblLecturerid"));
    }
}
