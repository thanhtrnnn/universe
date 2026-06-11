package com.universe.student.data;

import java.util.Collections;
import java.util.List;

public final class Models {

    private Models() {
    }

    public static final class UserProfile {
        public final String id;
        public final String fullName;
        public final String email;
        public final String role;
        public final String course;
        public final String major;
        public final String className;
        public final String department;
        public final String degree;

        public UserProfile(
                String id,
                String fullName,
                String email,
                String role,
                String course,
                String major,
                String className,
                String department,
                String degree) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.role = role;
            this.course = course;
            this.major = major;
            this.className = className;
            this.department = department;
            this.degree = degree;
        }
    }

    public static final class Student {
        public final String id;
        public final String fullName;
        public final String email;
        public final String major;
        public final String className;
        public final String course;

        public Student(String id, String fullName, String email, String major,
                       String className, String course) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.major = major;
            this.className = className;
            this.course = course;
        }
    }

    public static final class LoginResult {
        public final String token;
        public final UserProfile profile;

        public LoginResult(String token, UserProfile profile) {
            this.token = token;
            this.profile = profile;
        }
    }

    public static final class LecturerDashboard {
        public final int classCount;
        public final int studentCount;
        public final int scheduleCount;
        public final List<LecturerClass> classes;

        public LecturerDashboard(
                int classCount,
                int studentCount,
                int scheduleCount,
                List<LecturerClass> classes) {
            this.classCount = classCount;
            this.studentCount = studentCount;
            this.scheduleCount = scheduleCount;
            this.classes = classes == null ? Collections.emptyList() : classes;
        }
    }

    public static final class LecturerClass {
        public final String id;
        public final String code;
        public final String name;
        public final String semester;
        public final String year;
        public final String status;
        public final int enrolled;
        public final int capacity;

        public LecturerClass(
                String id,
                String code,
                String name,
                String semester,
                String year,
                String status,
                int enrolled,
                int capacity) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.semester = semester;
            this.year = year;
            this.status = status;
            this.enrolled = enrolled;
            this.capacity = capacity;
        }

        @Override
        public String toString() {
            return code + " - " + name;
        }
    }

    public static final class LecturerSession {
        public final String id;
        public final String date;
        public final int startPeriod;
        public final int endPeriod;
        public final String room;
        public final String status;

        public LecturerSession(
                String id,
                String date,
                int startPeriod,
                int endPeriod,
                String room,
                String status) {
            this.id = id;
            this.date = date;
            this.startPeriod = startPeriod;
            this.endPeriod = endPeriod;
            this.room = room;
            this.status = status;
        }
    }

    public static final class LecturerAttendance {
        public final String studentId;
        public final String studentName;
        public final String attendedAt;
        public final String method;
        public String status;

        public LecturerAttendance(
                String studentId,
                String studentName,
                String attendedAt,
                String method,
                String status) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.attendedAt = attendedAt;
            this.method = method;
            this.status = status;
        }
    }

    public static final class LecturerGrade {
        public final String recordId;
        public final String studentId;
        public final String studentName;
        public Double score1;
        public Double score2;
        public Double score3;
        public Double examScore;

        public LecturerGrade(
                String recordId,
                String studentId,
                String studentName,
                Double score1,
                Double score2,
                Double score3,
                Double examScore) {
            this.recordId = recordId;
            this.studentId = studentId;
            this.studentName = studentName;
            this.score1 = score1;
            this.score2 = score2;
            this.score3 = score3;
            this.examScore = examScore;
        }
    }

    public static final class Dashboard {
        public final int enrolledCount;
        public final double averageScore;
        public final int notificationCount;
        public final List<ScheduleEntry> upcomingClasses;

        public Dashboard(int enrolledCount, double averageScore, int notificationCount,
                         List<ScheduleEntry> upcomingClasses) {
            this.enrolledCount = enrolledCount;
            this.averageScore = averageScore;
            this.notificationCount = notificationCount;
            this.upcomingClasses = upcomingClasses == null
                    ? Collections.emptyList()
                    : upcomingClasses;
        }
    }

    public static final class Course {
        public final String id;
        public final String name;
        public final int credits;
        public final String department;
        public final boolean enrolled;

        public Course(String id, String name, int credits, String department, boolean enrolled) {
            this.id = id;
            this.name = name;
            this.credits = credits;
            this.department = department;
            this.enrolled = enrolled;
        }
    }

    public static final class ClassSection {
        public final String id;
        public final String code;
        public final String name;
        public final String semester;
        public final String year;
        public final String lecturer;
        public final String status;
        public final int enrolled;
        public final int capacity;
        public final boolean registered;

        public ClassSection(String id, String code, String name, String semester, String year,
                            String lecturer, String status, int enrolled, int capacity,
                            boolean registered) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.semester = semester;
            this.year = year;
            this.lecturer = lecturer;
            this.status = status;
            this.enrolled = enrolled;
            this.capacity = capacity;
            this.registered = registered;
        }
    }

    public static final class Enrollment {
        public final String classCode;
        public final String className;
        public final String enrolledAt;
        public final String status;

        public Enrollment(String classCode, String className, String enrolledAt, String status) {
            this.classCode = classCode;
            this.className = className;
            this.enrolledAt = enrolledAt;
            this.status = status;
        }
    }

    public static final class ScheduleEntry {
        public final String classCode;
        public final String className;
        public final String dayOfWeek;
        public final int startPeriod;
        public final int endPeriod;
        public final String room;
        public final String appliedFrom;
        public final String appliedTo;

        public ScheduleEntry(String classCode, String className, String dayOfWeek,
                             int startPeriod, int endPeriod, String room,
                             String appliedFrom, String appliedTo) {
            this.classCode = classCode;
            this.className = className;
            this.dayOfWeek = dayOfWeek;
            this.startPeriod = startPeriod;
            this.endPeriod = endPeriod;
            this.room = room;
            this.appliedFrom = appliedFrom;
            this.appliedTo = appliedTo;
        }
    }

    public static final class Grade {
        public final String classCode;
        public final String className;
        public final Double score1;
        public final Double score2;
        public final Double score3;
        public final Double examScore;
        public final Double totalScore;

        public Grade(String classCode, String className, Double score1, Double score2,
                     Double score3, Double examScore, Double totalScore) {
            this.classCode = classCode;
            this.className = className;
            this.score1 = score1;
            this.score2 = score2;
            this.score3 = score3;
            this.examScore = examScore;
            this.totalScore = totalScore;
        }
    }

    public static final class NotificationItem {
        public final String id;
        public final String title;
        public final String content;
        public final String sentAt;

        public NotificationItem(String id, String title, String content, String sentAt) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.sentAt = sentAt;
        }
    }

    public static final class AttendanceReceipt {
        public final boolean alreadyMarked;
        public final String message;
        public final String classCode;
        public final String className;
        public final String room;
        public final String attendedAt;

        public AttendanceReceipt(boolean alreadyMarked, String message, String classCode,
                                 String className, String room, String attendedAt) {
            this.alreadyMarked = alreadyMarked;
            this.message = message;
            this.classCode = classCode;
            this.className = className;
            this.room = room;
            this.attendedAt = attendedAt;
        }
    }
}
