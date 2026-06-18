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
import java.util.LinkedHashSet;
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
        if (!KafkaUtil.enabled()) {
            return insert(n); // Kafka tắt: ghi thẳng DB
        }
        try {
            KafkaUtil.publish(GSON.toJson(NotificationPayload.from(n)));
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi publish Kafka: " + e.getMessage(), e);
        }
    }

    /**
     * Ghi thông báo cho từng người nhận ngay trong CSDL, sau đó phát Kafka nền
     * để giữ luồng real-time mà không làm treo giao diện khi broker tạm ngắt.
     */
    public int sendToRecipients(String title, String content, String recipientType,
                                List<String> recipientIds) {
        List<String> uniqueIds = new ArrayList<>(new LinkedHashSet<>(recipientIds));
        if (uniqueIds.isEmpty()) {
            return 0;
        }

        List<Notification> notifications = new ArrayList<>();
        String baseId = "NTF" + Long.toString(System.nanoTime(), 36).toUpperCase();
        for (int i = 0; i < uniqueIds.size(); i++) {
            String id = (baseId + Integer.toString(i, 36).toUpperCase());
            if (id.length() > 20) {
                id = id.substring(0, 20);
            }
            notifications.add(new Notification(
                    id, title, content, recipientType, LocalDateTime.now(), uniqueIds.get(i)));
        }

        insertBatch(notifications);
        publishInBackground(notifications);
        return notifications.size();
    }

    /** Ghi thẳng 1 thông báo vào tblNotification (dùng bởi consumer). */
    public boolean insert(Notification n) {
        String sql = "INSERT INTO tblNotification (id, title, content, recipientType, sentAt, tblUserid) " +
                     "VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO NOTHING";
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

    private void insertBatch(List<Notification> notifications) {
        String sql = "INSERT INTO tblNotification " +
                "(id, title, content, recipientType, sentAt, tblUserid) " +
                "VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO NOTHING";
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (Notification n : notifications) {
                    ps.setString(1, n.getId());
                    ps.setString(2, n.getTitle());
                    ps.setString(3, n.getContent());
                    ps.setString(4, n.getRecipientType());
                    ps.setTimestamp(5, Timestamp.valueOf(n.getSentAt()));
                    ps.setString(6, n.getUserId());
                    ps.addBatch();
                }
                ps.executeBatch();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lưu danh sách thông báo: " + e.getMessage(), e);
        }
    }

    private void publishInBackground(List<Notification> notifications) {
        Thread publisher = new Thread(() -> {
            for (Notification notification : notifications) {
                try {
                    KafkaUtil.publish(GSON.toJson(NotificationPayload.from(notification)));
                } catch (Exception e) {
                    System.err.println("Không publish được thông báo "
                            + notification.getId() + ": " + e.getMessage());
                    break;
                }
            }
        }, "notification-publisher");
        publisher.setDaemon(true);
        publisher.start();
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
