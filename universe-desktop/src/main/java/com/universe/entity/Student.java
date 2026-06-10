package com.universe.entity;

import java.time.LocalDate;

/** Sinh viên - kế thừa User, bổ sung khóa học, ngành, lớp sinh hoạt (tblStudent). */
public class Student extends User {

    private String course;     // khóa học (vd K23)
    private String major;
    private String className;   // lớp sinh hoạt

    public Student() {
    }

    public Student(String id, String fullName, LocalDate dob, String gender, String phone,
                   String username, String password, String status, String email,
                   String course, String major, String className) {
        super(id, fullName, dob, gender, phone, username, password, status, "Student", email);
        this.course = course;
        this.major = major;
        this.className = className;
    }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
