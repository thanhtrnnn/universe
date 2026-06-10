package com.universe.entity;

import java.time.LocalDate;

/** Lịch học định kỳ của một lớp học phần (tblSchedule). */
public class Schedule {

    private String id;
    private String dayOfWeek;
    private int startPeriod;
    private int endPeriod;
    private String room;
    private LocalDate appliedFrom;
    private LocalDate appliedTo;
    private String classSectionId;   // FK -> tblClassSection

    public Schedule() {
    }

    public Schedule(String id, String dayOfWeek, int startPeriod, int endPeriod, String room,
                    LocalDate appliedFrom, LocalDate appliedTo, String classSectionId) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.room = room;
        this.appliedFrom = appliedFrom;
        this.appliedTo = appliedTo;
        this.classSectionId = classSectionId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public int getStartPeriod() { return startPeriod; }
    public void setStartPeriod(int startPeriod) { this.startPeriod = startPeriod; }

    public int getEndPeriod() { return endPeriod; }
    public void setEndPeriod(int endPeriod) { this.endPeriod = endPeriod; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public LocalDate getAppliedFrom() { return appliedFrom; }
    public void setAppliedFrom(LocalDate appliedFrom) { this.appliedFrom = appliedFrom; }

    public LocalDate getAppliedTo() { return appliedTo; }
    public void setAppliedTo(LocalDate appliedTo) { this.appliedTo = appliedTo; }

    public String getClassSectionId() { return classSectionId; }
    public void setClassSectionId(String classSectionId) { this.classSectionId = classSectionId; }
}
