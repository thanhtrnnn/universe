package com.universe.dao;

import com.universe.entity.Schedule;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO lịch học (Chương 4.3.2.3 - chức năng sửa lịch học).
 * Phương thức: updateSchedule(), getSchedulesByClassSection().
 */
public class ScheduleDAO extends DAO {

    public List<Schedule> getSchedulesByClassSection(String classSectionId) {
        List<Schedule> list = new ArrayList<>();
        String sql = "SELECT * FROM tblSchedule WHERE tblClassSectionid = ? ORDER BY id";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, classSectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getSchedulesByClassSection: " + e.getMessage(), e);
        }
        return list;
    }

    /** Lấy toàn bộ lịch của các lớp một sinh viên đã đăng ký (xem TKB). */
    public List<Schedule> getStudentSchedule(String studentId) {
        List<Schedule> list = new ArrayList<>();
        String sql = "SELECT s.* FROM tblSchedule s " +
                     "JOIN tblCourseRecord cr ON cr.tblClassSectionid = s.tblClassSectionid " +
                     "WHERE cr.tblStudentid = ? ORDER BY s.dayOfWeek, s.startPeriod";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getStudentSchedule: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean updateSchedule(Schedule s) {
        String sql = "UPDATE tblSchedule SET dayOfWeek = ?, startPeriod = ?, endPeriod = ?, " +
                     "room = ?, appliedFrom = ?, appliedTo = ? WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getDayOfWeek());
            ps.setInt(2, s.getStartPeriod());
            ps.setInt(3, s.getEndPeriod());
            ps.setString(4, s.getRoom());
            ps.setDate(5, s.getAppliedFrom() != null ? Date.valueOf(s.getAppliedFrom()) : null);
            ps.setDate(6, s.getAppliedTo() != null ? Date.valueOf(s.getAppliedTo()) : null);
            ps.setString(7, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi updateSchedule: " + e.getMessage(), e);
        }
    }

    private Schedule map(ResultSet rs) throws SQLException {
        return new Schedule(
                rs.getString("id"),
                rs.getString("dayOfWeek"),
                rs.getInt("startPeriod"),
                rs.getInt("endPeriod"),
                rs.getString("room"),
                rs.getDate("appliedFrom") != null ? rs.getDate("appliedFrom").toLocalDate() : null,
                rs.getDate("appliedTo") != null ? rs.getDate("appliedTo").toLocalDate() : null,
                rs.getString("tblClassSectionid"));
    }
}
