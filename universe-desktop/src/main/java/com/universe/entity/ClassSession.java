package com.universe.entity;

import java.time.LocalDate;

/** Buổi học cụ thể sinh ra từ lịch học (tblClassSession). Liên kết 1-1 với QRCode. */
public class ClassSession {

    private String id;
    private LocalDate date;
    private int startPeriod;
    private int endPeriod;
    private String room;
    private String status;
    private String qrCodeId;        // FK -> tblQRCode (nullable khi chưa mở QR)
    private String classSectionId;  // FK -> tblClassSection

    public ClassSession() {
    }

    public ClassSession(String id, LocalDate date, int startPeriod, int endPeriod, String room,
                        String status, String qrCodeId, String classSectionId) {
        this.id = id;
        this.date = date;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.room = room;
        this.status = status;
        this.qrCodeId = qrCodeId;
        this.classSectionId = classSectionId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getStartPeriod() { return startPeriod; }
    public void setStartPeriod(int startPeriod) { this.startPeriod = startPeriod; }

    public int getEndPeriod() { return endPeriod; }
    public void setEndPeriod(int endPeriod) { this.endPeriod = endPeriod; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getQrCodeId() { return qrCodeId; }
    public void setQrCodeId(String qrCodeId) { this.qrCodeId = qrCodeId; }

    public String getClassSectionId() { return classSectionId; }
    public void setClassSectionId(String classSectionId) { this.classSectionId = classSectionId; }

    @Override
    public String toString() {
        return (date != null ? date.toString() : "?")
                + " - tiết " + startPeriod + "-" + endPeriod
                + " - " + room + " [" + id + "]";
    }
}
