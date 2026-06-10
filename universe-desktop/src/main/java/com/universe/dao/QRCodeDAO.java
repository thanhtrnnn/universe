package com.universe.dao;

import com.universe.entity.ClassSession;
import com.universe.entity.QRCode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * DAO mã QR điểm danh (Chương 4.3.1.7 - chức năng tạo/tắt mã QR).
 * Phương thức: generateQr(), deactivateQr().
 */
public class QRCodeDAO extends DAO {

    /**
     * Tạo mã QR mới cho buổi học, gán vào ClassSession.
     * Tọa độ/bán kính lấy mặc định (phòng học) — desktop demo.
     */
    public QRCode generateQr(ClassSession session) {
        return generateQr(session, 21.0035, 105.8430, 50.0, 15);
    }

    public QRCode generateQr(ClassSession session, double latitude, double longitude,
                             double radius, int validMinutes) {
        LocalDateTime now = LocalDateTime.now();
        QRCode qr = new QRCode(
                "QR" + Long.toString(System.nanoTime(), 36).toUpperCase(),
                now,
                now.plusMinutes(validMinutes),
                latitude, longitude, radius,
                "active");

        String insertQr = "INSERT INTO tblQRCode (id, createdAt, expiredAt, latitude, longitude, radius, status) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String linkSession = "UPDATE tblClassSession SET tblQRCodeid = ?, status = 'open' WHERE id = ?";
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            if (session.getQrCodeId() != null) {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE tblQRCode SET status = 'inactive' WHERE id = ?")) {
                    ps.setString(1, session.getQrCodeId());
                    ps.executeUpdate();
                }
            }
            try (PreparedStatement ps = con.prepareStatement(insertQr)) {
                ps.setString(1, qr.getId());
                ps.setTimestamp(2, Timestamp.valueOf(qr.getCreatedAt()));
                ps.setTimestamp(3, Timestamp.valueOf(qr.getExpiredAt()));
                ps.setDouble(4, qr.getLatitude());
                ps.setDouble(5, qr.getLongitude());
                ps.setDouble(6, qr.getRadius());
                ps.setString(7, qr.getStatus());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(linkSession)) {
                ps.setString(1, qr.getId());
                ps.setString(2, session.getId());
                ps.executeUpdate();
            }
            con.commit();
            session.setQrCodeId(qr.getId());
            return qr;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi generateQr: " + e.getMessage(), e);
        }
    }

    /** Tắt mã QR (status = inactive). */
    public boolean deactivateQr(String qrCodeId) {
        String sql = "UPDATE tblQRCode SET status = 'inactive' WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, qrCodeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi deactivateQr: " + e.getMessage(), e);
        }
    }

    public boolean deactivateBySession(String sessionId) {
        String closeQr = "UPDATE tblQRCode q SET status = 'inactive' " +
                "FROM tblClassSession s WHERE s.tblQRCodeid = q.id AND s.id = ?";
        String closeSession = "UPDATE tblClassSession SET status = 'closed' WHERE id = ?";
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(closeQr)) {
                ps.setString(1, sessionId);
                ps.executeUpdate();
            }
            int updated;
            try (PreparedStatement ps = con.prepareStatement(closeSession)) {
                ps.setString(1, sessionId);
                updated = ps.executeUpdate();
            }
            con.commit();
            return updated > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi deactivateBySession: " + e.getMessage(), e);
        }
    }

    public QRCode findById(String id) {
        String sql = "SELECT * FROM tblQRCode WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new QRCode(
                            rs.getString("id"),
                            rs.getTimestamp("createdAt") != null ? rs.getTimestamp("createdAt").toLocalDateTime() : null,
                            rs.getTimestamp("expiredAt") != null ? rs.getTimestamp("expiredAt").toLocalDateTime() : null,
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude"),
                            rs.getDouble("radius"),
                            rs.getString("status"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi findById: " + e.getMessage(), e);
        }
        return null;
    }
}
