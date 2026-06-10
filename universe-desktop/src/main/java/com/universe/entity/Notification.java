package com.universe.entity;

import java.time.LocalDateTime;

/** Thông báo gửi đến người dùng trong hệ thống (tblNotification). */
public class Notification {

    private String id;
    private String title;
    private String content;
    private String recipientType;   // vd "all-students-of-class:CS01"
    private LocalDateTime sentAt;
    private String userId;          // FK -> tblUser (người nhận)

    public Notification() {
    }

    public Notification(String id, String title, String content, String recipientType,
                        LocalDateTime sentAt, String userId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.recipientType = recipientType;
        this.sentAt = sentAt;
        this.userId = userId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getRecipientType() { return recipientType; }
    public void setRecipientType(String recipientType) { this.recipientType = recipientType; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
