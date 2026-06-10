package com.universe.entity;

import java.time.LocalDate;

/**
 * Bản ghi học tập của sinh viên trong một lớp học phần (tblCourseRecord).
 * Lưu cả kết quả đăng ký và các cột điểm thành phần
 * (score1, score2, score3, examScore) — điểm KHÔNG tách thành entity riêng.
 */
public class CourseRecord {

    private String id;
    private LocalDate enrolledAt;
    private String status;
    private Double score1;     // dùng Double để cho phép NULL (chưa có điểm)
    private Double score2;
    private Double score3;
    private Double examScore;
    private String studentId;       // FK -> tblStudent
    private String classSectionId;  // FK -> tblClassSection

    // Trường tiện hiển thị (join), không lưu DB
    private transient String studentName;
    private transient String classSectionName;

    public CourseRecord() {
    }

    public CourseRecord(String id, LocalDate enrolledAt, String status,
                        Double score1, Double score2, Double score3, Double examScore,
                        String studentId, String classSectionId) {
        this.id = id;
        this.enrolledAt = enrolledAt;
        this.status = status;
        this.score1 = score1;
        this.score2 = score2;
        this.score3 = score3;
        this.examScore = examScore;
        this.studentId = studentId;
        this.classSectionId = classSectionId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDate enrolledAt) { this.enrolledAt = enrolledAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getScore1() { return score1; }
    public void setScore1(Double score1) { this.score1 = score1; }

    public Double getScore2() { return score2; }
    public void setScore2(Double score2) { this.score2 = score2; }

    public Double getScore3() { return score3; }
    public void setScore3(Double score3) { this.score3 = score3; }

    public Double getExamScore() { return examScore; }
    public void setExamScore(Double examScore) { this.examScore = examScore; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getClassSectionId() { return classSectionId; }
    public void setClassSectionId(String classSectionId) { this.classSectionId = classSectionId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getClassSectionName() { return classSectionName; }
    public void setClassSectionName(String classSectionName) { this.classSectionName = classSectionName; }

    /**
     * Điểm tổng kết theo trọng số mẫu: TX 20% + GK 30% + CK 50%
     * (score3 nếu có được gộp vào TX). Trả null nếu thiếu điểm cuối kỳ.
     */
    public Double getTotalScore() {
        if (examScore == null) {
            return null;
        }
        double tx = score1 != null ? score1 : 0;
        double gk = score2 != null ? score2 : 0;
        double extra = score3 != null ? score3 : tx;
        double txAvg = (tx + extra) / (score3 != null ? 2.0 : 1.0);
        return Math.round((txAvg * 0.2 + gk * 0.3 + examScore * 0.5) * 100.0) / 100.0;
    }
}
