package com.universe.dao;

import com.universe.entity.CourseRecord;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO bản ghi học tập (Chương 4.3.2.6 đăng ký + 4.3.1.9/10 xem/nhập điểm).
 * Phương thức: confirmRegistration(), viewGrade(), viewGradeBySemester(),
 * enterGrade(), editGrade().
 */
public class CourseRecordDAO extends DAO {

    /** Ghi nhận bản ghi đăng ký mới (Chương 4.3.2.6). */
    public boolean confirmRegistration(CourseRecord r) {
        if (exists(r.getStudentId(), r.getClassSectionId())) {
            return false;
        }
        if (r.getId() == null || r.getId().isBlank()) {
            r.setId("CR" + Long.toString(System.nanoTime(), 36).toUpperCase());
        }
        String sql = "INSERT INTO tblCourseRecord (id, enrolledAt, status, tblStudentid, tblClassSectionid) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getId());
            ps.setDate(2, r.getEnrolledAt() != null ? Date.valueOf(r.getEnrolledAt()) : null);
            ps.setString(3, r.getStatus() != null ? r.getStatus() : "Registered");
            ps.setString(4, r.getStudentId());
            ps.setString(5, r.getClassSectionId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi confirmRegistration: " + e.getMessage(), e);
        }
    }

    /** Kiểm tra SV đã đăng ký lớp này chưa (chống trùng). */
    public boolean exists(String studentId, String classSectionId) {
        String sql = "SELECT 1 FROM tblCourseRecord WHERE tblStudentid = ? AND tblClassSectionid = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, classSectionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi exists: " + e.getMessage(), e);
        }
    }

    /** Danh sách lớp đã đăng ký + điểm của một sinh viên (Chương 4.3.1.9). */
    public List<CourseRecord> viewGrade(String studentId) {
        List<CourseRecord> list = new ArrayList<>();
        String sql = "SELECT cr.*, cs.name AS csName, cs.classId AS csCode " +
                     "FROM tblCourseRecord cr JOIN tblClassSection cs ON cs.id = cr.tblClassSectionid " +
                     "WHERE cr.tblStudentid = ? ORDER BY cr.id";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CourseRecord r = map(rs);
                    r.setClassSectionName(rs.getString("csCode") + " - " + rs.getString("csName"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi viewGrade: " + e.getMessage(), e);
        }
        return list;
    }

    /** Điểm theo học kỳ của sinh viên (Chương 4.3.1.9 chi tiết). */
    public List<CourseRecord> viewGradeBySemester(String studentId, String semester) {
        List<CourseRecord> list = new ArrayList<>();
        String sql = "SELECT cr.*, cs.name AS csName, cs.classId AS csCode " +
                     "FROM tblCourseRecord cr JOIN tblClassSection cs ON cs.id = cr.tblClassSectionid " +
                     "WHERE cr.tblStudentid = ? AND cs.semester = ? ORDER BY cr.id";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, semester);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CourseRecord r = map(rs);
                    r.setClassSectionName(rs.getString("csCode") + " - " + rs.getString("csName"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi viewGradeBySemester: " + e.getMessage(), e);
        }
        return list;
    }

    /** Danh sách bản ghi học tập của một lớp (để giảng viên nhập điểm). */
    public List<CourseRecord> findByClassSection(String classSectionId) {
        List<CourseRecord> list = new ArrayList<>();
        String sql = "SELECT cr.*, u.fullName AS sName " +
                     "FROM tblCourseRecord cr JOIN tblUser u ON u.id = cr.tblStudentid " +
                     "WHERE cr.tblClassSectionid = ? ORDER BY cr.tblStudentid";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, classSectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CourseRecord r = map(rs);
                    r.setStudentName(rs.getString("sName"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi findByClassSection: " + e.getMessage(), e);
        }
        return list;
    }

    /** Nhập điểm hàng loạt (Chương 4.3.1.10). */
    public boolean enterGrade(List<CourseRecord> records) {
        String sql = "UPDATE tblCourseRecord SET score1 = ?, score2 = ?, score3 = ?, examScore = ? WHERE id = ?";
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (CourseRecord r : records) {
                    setNullableDouble(ps, 1, r.getScore1());
                    setNullableDouble(ps, 2, r.getScore2());
                    setNullableDouble(ps, 3, r.getScore3());
                    setNullableDouble(ps, 4, r.getExamScore());
                    ps.setString(5, r.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi enterGrade: " + e.getMessage(), e);
        }
    }

    /** Sửa điểm một bản ghi (Chương 4.3.1.10). */
    public boolean editGrade(CourseRecord r) {
        String sql = "UPDATE tblCourseRecord SET score1 = ?, score2 = ?, score3 = ?, examScore = ? WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            setNullableDouble(ps, 1, r.getScore1());
            setNullableDouble(ps, 2, r.getScore2());
            setNullableDouble(ps, 3, r.getScore3());
            setNullableDouble(ps, 4, r.getExamScore());
            ps.setString(5, r.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi editGrade: " + e.getMessage(), e);
        }
    }

    private void setNullableDouble(PreparedStatement ps, int idx, Double v) throws SQLException {
        if (v == null) {
            ps.setNull(idx, Types.DOUBLE);
        } else {
            ps.setDouble(idx, v);
        }
    }

    private CourseRecord map(ResultSet rs) throws SQLException {
        return new CourseRecord(
                rs.getString("id"),
                rs.getDate("enrolledAt") != null ? rs.getDate("enrolledAt").toLocalDate() : null,
                rs.getString("status"),
                getNullableDouble(rs, "score1"),
                getNullableDouble(rs, "score2"),
                getNullableDouble(rs, "score3"),
                getNullableDouble(rs, "examScore"),
                rs.getString("tblStudentid"),
                rs.getString("tblClassSectionid"));
    }

    private Double getNullableDouble(ResultSet rs, String col) throws SQLException {
        double v = rs.getDouble(col);
        return rs.wasNull() ? null : v;
    }
}
