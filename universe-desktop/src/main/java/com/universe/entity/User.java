package com.universe.entity;

import java.time.LocalDate;

/**
 * Lớp thực thể Người dùng (Model - Chương 4.1).
 * Là lớp cha của Admin / Lecturer / Student (kế thừa, quan hệ 1-1 trong CSDL).
 */
public class User {

    private String id;
    private String fullName;
    private LocalDate dob;
    private String gender;
    private String phone;
    private String username;
    private String password;
    private String status;   // active | inactive
    private String role;     // Admin | Lecturer | Student
    private String email;

    public User() {
    }

    public User(String id, String fullName, LocalDate dob, String gender, String phone,
                String username, String password, String status, String role, String email) {
        this.id = id;
        this.fullName = fullName;
        this.dob = dob;
        this.gender = gender;
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.status = status;
        this.role = role;
        this.email = email;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return fullName + " (" + id + ")";
    }
}
