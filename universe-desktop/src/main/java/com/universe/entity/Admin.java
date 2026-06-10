package com.universe.entity;

import java.time.LocalDate;

/** Quản trị viên - kế thừa User, bổ sung phòng ban công tác (tblAdmin). */
public class Admin extends User {

    private String department;

    public Admin() {
    }

    public Admin(String id, String fullName, LocalDate dob, String gender, String phone,
                 String username, String password, String status, String email,
                 String department) {
        super(id, fullName, dob, gender, phone, username, password, status, "Admin", email);
        this.department = department;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
