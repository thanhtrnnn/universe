package com.universe.dao;

import com.universe.entity.Attendance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO điểm danh (Chương 4.3.1.8 - quản lý điểm danh thủ công).
 * Phương thức: viewAttendance(), markManualAttendance().
 */
public class AttendanceDAO extends DAO {

    /** Danh sách điểm danh theo buổi học (kèm tên SV). */
    public List<Attendance> viewAttendance(String classSessionId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.id, a.attendedAt, a.latitude, a.longitude, a.distance, a.method, a.status, " +
                     "cr.tblStudentid, u.fullName AS sName " +
                     "FROM tblClassSession cs " +
                     "JOIN tblCourseRecord cr ON cr.tblClassSectionid = cs.tblClassSectionid " +
                     "JOIN tblUser u ON u.id = cr.tblStudentid " +
                     "LEFT JOIN tblAttendance a ON a.tblClassSessionid = cs.id " +
                     "AND a.tblStudentid = cr.tblStudentid " +
                     "WHERE cs.id = ? ORDER BY cr.tblStudentid";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, classSessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Attendance a = new Attendance();
                    a.setId(rs.getString("id"));
                    a.setAttendedAt(rs.getTimestamp("attendedAt") != null
                            ? rs.getTimestamp("attendedAt").toLocalDateTime() : null);
                    a.setLatitude(rs.getDouble("latitude"));
                    a.setLongitude(rs.getDouble("longitude"));
                    a.setDistance(rs.getDouble("distance"));
                    a.setMethod(rs.getString("method") == null ? "-" : rs.getString("method"));
                    a.setStatus(rs.getString("status") == null ? "ABSENT" : rs.getString("status"));
                    a.setClassSessionId(classSessionId);
                    a.setStudentId(rs.getString("tblStudentid"));
                    a.setStudentName(rs.getString("sName"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi viewAttendance: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Điểm danh thủ công: cập nhật nếu đã có bản ghi cho SV trong buổi,
     * ngược lại chèn mới (Chương 4.3.1.8, test case 5.2.2.8).
     */
    public boolean markManualAttendance(Attendance a) {
        String findSql = "SELECT id FROM tblAttendance WHERE tblClassSessionid = ? AND tblStudentid = ?";
        try (Connection con = getConnection();
             PreparedStatement find = con.prepareStatement(findSql)) {
            find.setString(1, a.getClassSessionId());
            find.setString(2, a.getStudentId());
            String existingId = null;
            try (ResultSet rs = find.executeQuery()) {
                if (rs.next()) {
                    existingId = rs.getString("id");
                }
            }
            if (existingId != null) {
                String upd = "UPDATE tblAttendance SET status = ?, method = 'MANUAL', attendedAt = ? WHERE id = ?";
                try (PreparedStatement ps = con.prepareStatement(upd)) {
                    ps.setString(1, a.getStatus());
                    ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setString(3, existingId);
                    return ps.executeUpdate() > 0;
                }
            } else {
                String ins = "INSERT INTO tblAttendance (id, attendedAt, method, status, tblClassSessionid, tblStudentid) " +
                             "VALUES (?, ?, 'MANUAL', ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(ins)) {
                    ps.setString(1, a.getId() != null ? a.getId() : "ATT-" + System.nanoTime());
                    ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setString(3, a.getStatus());
                    ps.setString(4, a.getClassSessionId());
                    ps.setString(5, a.getStudentId());
                    return ps.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi markManualAttendance: " + e.getMessage(), e);
        }
    }

    private Attendance map(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getString("id"));
        a.setAttendedAt(rs.getTimestamp("attendedAt") != null ? rs.getTimestamp("attendedAt").toLocalDateTime() : null);
        a.setLatitude(rs.getDouble("latitude"));
        a.setLongitude(rs.getDouble("longitude"));
        a.setDistance(rs.getDouble("distance"));
        a.setMethod(rs.getString("method"));
        a.setStatus(rs.getString("status"));
        a.setClassSessionId(rs.getString("tblClassSessionid"));
        a.setStudentId(rs.getString("tblStudentid"));
        return a;
    }
}
