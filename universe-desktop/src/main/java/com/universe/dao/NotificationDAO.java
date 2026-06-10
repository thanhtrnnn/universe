package com.universe.dao;

import com.google.gson.Gson;
import com.universe.entity.Notification;
import com.universe.util.KafkaUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO thông báo (Chương 4.3.2.2 - gửi thông báo đến tất cả sinh viên).
 *
 * Khác thiết kế docx gốc (vốn ghi thẳng tblNotification): theo yêu cầu tích hợp
 * Kafka, {@link #sendNotification} PUBLISH payload JSON lên topic; lớp
 * {@code NotificationConsumer} đọc topic rồi gọi {@link #insert} ghi DB.
 */
public class NotificationDAO extends DAO {

    private static final Gson GSON = new Gson();

    /** Publish 1 thông báo lên Kafka. */
    public boolean sendNotification(Notification n) {
        if (n.getId() == null) {
            n.setId("NTF-" + System.nanoTime());
        }
        if (n.getSentAt() == null) {
            n.setSentAt(LocalDateTime.now());
        }
        try {
            KafkaUtil.publish(GSON.toJson(NotificationPayload.from(n)));
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi publish Kafka: " + e.getMessage(), e);
        }
    }

    /** Ghi thẳng 1 thông báo vào tblNotification (dùng bởi consumer). */
    public boolean insert(Notification n) {
        String sql = "INSERT INTO tblNotification (id, title, content, recipientType, sentAt, tblUserid) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, n.getId());
            ps.setString(2, n.getTitle());
            ps.setString(3, n.getContent());
            ps.setString(4, n.getRecipientType());
            ps.setTimestamp(5, n.getSentAt() != null ? Timestamp.valueOf(n.getSentAt())
                                                      : Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(6, n.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi insert notification: " + e.getMessage(), e);
        }
    }

    public List<Notification> getNotifications(String userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM tblNotification WHERE tblUserid = ? ORDER BY sentAt DESC";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Notification(
                            rs.getString("id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("recipientType"),
                            rs.getTimestamp("sentAt") != null ? rs.getTimestamp("sentAt").toLocalDateTime() : null,
                            rs.getString("tblUserid")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getNotifications: " + e.getMessage(), e);
        }
        return list;
    }

    /** Parse JSON payload (dùng bởi consumer). */
    public static Notification fromJson(String json) {
        NotificationPayload p = GSON.fromJson(json, NotificationPayload.class);
        return p.toNotification();
    }

    /** DTO trung gian để serialize qua Kafka (LocalDateTime -> String). */
    private static final class NotificationPayload {
        String id;
        String title;
        String content;
        String recipientType;
        String sentAt;
        String userId;

        static NotificationPayload from(Notification n) {
            NotificationPayload p = new NotificationPayload();
            p.id = n.getId();
            p.title = n.getTitle();
            p.content = n.getContent();
            p.recipientType = n.getRecipientType();
            p.sentAt = n.getSentAt() != null ? n.getSentAt().toString() : LocalDateTime.now().toString();
            p.userId = n.getUserId();
            return p;
        }

        Notification toNotification() {
            return new Notification(id, title, content, recipientType,
                    sentAt != null ? LocalDateTime.parse(sentAt) : LocalDateTime.now(), userId);
        }
    }
}
