package com.universe.entity;

import java.time.LocalDate;

/** Giảng viên - kế thừa User, bổ sung phòng ban và học vị (tblLecturer). */
public class Lecturer extends User {

    private String department;
    private String degree;

    public Lecturer() {
    }

    public Lecturer(String id, String fullName, LocalDate dob, String gender, String phone,
                    String username, String password, String status, String email,
                    String department, String degree) {
        super(id, fullName, dob, gender, phone, username, password, status, "Lecturer", email);
        this.department = department;
        this.degree = degree;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
}
