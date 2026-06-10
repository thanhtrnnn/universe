package com.universe.dao;

import com.universe.entity.ClassSession;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO buổi học (Chương 4.3.1.7/8). Phương thức: createSession(), getSessions().
 */
public class ClassSessionDAO extends DAO {

    /** Tạo buổi học mới cho lớp học phần (ngày hôm nay). */
    public ClassSession createSession(String classSectionId) {
        ClassSession s = new ClassSession(
                "SESS-" + System.nanoTime(),
                LocalDate.now(),
                1, 3, "A101", "created", null, classSectionId);
        String sql = "INSERT INTO tblClassSession (id, date, startPeriod, endPeriod, room, status, tblClassSectionid) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getId());
            ps.setDate(2, Date.valueOf(s.getDate()));
            ps.setInt(3, s.getStartPeriod());
            ps.setInt(4, s.getEndPeriod());
            ps.setString(5, s.getRoom());
            ps.setString(6, s.getStatus());
            ps.setString(7, s.getClassSectionId());
            ps.executeUpdate();
            return s;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi createSession: " + e.getMessage(), e);
        }
    }

    public List<ClassSession> getSessions(String classSectionId) {
        List<ClassSession> list = new ArrayList<>();
        String sql = "SELECT * FROM tblClassSession WHERE tblClassSectionid = ? ORDER BY date DESC";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, classSectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getSessions: " + e.getMessage(), e);
        }
        return list;
    }

    public ClassSession findById(String id) {
        String sql = "SELECT * FROM tblClassSession WHERE id = ?";
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

    private ClassSession map(ResultSet rs) throws SQLException {
        return new ClassSession(
                rs.getString("id"),
                rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : null,
                rs.getInt("startPeriod"),
                rs.getInt("endPeriod"),
                rs.getString("room"),
                rs.getString("status"),
                rs.getString("tblQRCodeid"),
                rs.getString("tblClassSectionid"));
    }
}
