package com.universe.entity;

import java.time.LocalDateTime;

/** Bản ghi điểm danh của một sinh viên trong một buổi học (tblAttendance). */
public class Attendance {

    private String id;
    private LocalDateTime attendedAt;
    private double latitude;
    private double longitude;
    private String method;   // QR | MANUAL
    private String status;   // PRESENT | ABSENT | LATE
    private String classSessionId;  // FK -> tblClassSession
    private String studentId;       // FK -> tblStudent

    // Trường tiện hiển thị (join), không lưu DB
    private transient String studentName;

    public Attendance() {
    }

    public Attendance(String id, LocalDateTime attendedAt, double latitude, double longitude,
                      String method, String status, String classSessionId, String studentId) {
        this.id = id;
        this.attendedAt = attendedAt;
        this.latitude = latitude;
        this.longitude = longitude;
        this.method = method;
        this.status = status;
        this.classSessionId = classSessionId;
        this.studentId = studentId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDateTime getAttendedAt() { return attendedAt; }
    public void setAttendedAt(LocalDateTime attendedAt) { this.attendedAt = attendedAt; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getClassSessionId() { return classSessionId; }
    public void setClassSessionId(String classSessionId) { this.classSessionId = classSessionId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
}
