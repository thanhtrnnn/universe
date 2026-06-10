package com.universe.entity;

/** Lớp học phần - một phiên mở cụ thể của Course trong học kỳ (tblClassSection). */
public class ClassSection {

    private String id;
    private String classId;       // mã lớp hiển thị (vd INT1340.01)
    private String name;
    private String semester;
    private String year;
    private int maxStudents;
    private String status;
    private String courseId;      // FK -> tblCourse
    private String lecturerId;    // FK -> tblLecturer

    public ClassSection() {
    }

    public ClassSection(String id, String classId, String name, String semester, String year,
                        int maxStudents, String status, String courseId, String lecturerId) {
        this.id = id;
        this.classId = classId;
        this.name = name;
        this.semester = semester;
        this.year = year;
        this.maxStudents = maxStudents;
        this.status = status;
        this.courseId = courseId;
        this.lecturerId = lecturerId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public int getMaxStudents() { return maxStudents; }
    public void setMaxStudents(int maxStudents) { this.maxStudents = maxStudents; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getLecturerId() { return lecturerId; }
    public void setLecturerId(String lecturerId) { this.lecturerId = lecturerId; }

    @Override
    public String toString() {
        return classId + " - " + name;
    }
}
