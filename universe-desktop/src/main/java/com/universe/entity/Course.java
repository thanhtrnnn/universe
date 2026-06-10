package com.universe.entity;

/** Học phần / môn học (tblCourse). */
public class Course {

    private String id;
    private int credits;
    private String name;
    private String department;

    public Course() {
    }

    public Course(String id, int credits, String name, String department) {
        this.id = id;
        this.credits = credits;
        this.name = name;
        this.department = department;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}
