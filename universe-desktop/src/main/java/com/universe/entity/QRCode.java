package com.universe.entity;

import java.time.LocalDateTime;

/** Mã QR điểm danh gắn với một buổi học (tblQRCode). */
public class QRCode {

    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private double latitude;
    private double longitude;
    private double radius;
    private String status;   // active | inactive

    public QRCode() {
    }

    public QRCode(String id, LocalDateTime createdAt, LocalDateTime expiredAt,
                  double latitude, double longitude, double radius, String status) {
        this.id = id;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiredAt() { return expiredAt; }
    public void setExpiredAt(LocalDateTime expiredAt) { this.expiredAt = expiredAt; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getRadius() { return radius; }
    public void setRadius(double radius) { this.radius = radius; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
