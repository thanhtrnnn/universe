-- =====================================================================
--  UniVerse - Thiết kế CSDL (Chương 4.2, baocao_NEW.docx)
--  12 bảng. Cột lấy chính xác từ bảng dữ liệu test case (Chương 5.2.2).
-- =====================================================================

DROP TABLE IF EXISTS tblNotification   CASCADE;
DROP TABLE IF EXISTS tblAttendance     CASCADE;
DROP TABLE IF EXISTS tblCourseRecord   CASCADE;
DROP TABLE IF EXISTS tblClassSession   CASCADE;
DROP TABLE IF EXISTS tblQRCode         CASCADE;
DROP TABLE IF EXISTS tblSchedule       CASCADE;
DROP TABLE IF EXISTS tblClassSection   CASCADE;
DROP TABLE IF EXISTS tblCourse         CASCADE;
DROP TABLE IF EXISTS tblStudent        CASCADE;
DROP TABLE IF EXISTS tblLecturer       CASCADE;
DROP TABLE IF EXISTS tblAdmin          CASCADE;
DROP TABLE IF EXISTS tblUser           CASCADE;

-- ------------------------- Người dùng + kế thừa -------------------------
CREATE TABLE tblUser (
    id        VARCHAR(20)  PRIMARY KEY,
    fullName  VARCHAR(100),
    dob       DATE,
    gender    VARCHAR(10),
    phone     VARCHAR(15),
    username  VARCHAR(50)  UNIQUE NOT NULL,
    password  VARCHAR(100) NOT NULL,
    status    VARCHAR(20)  DEFAULT 'active',          -- active | inactive
    role      VARCHAR(20)  NOT NULL,                  -- Admin | Lecturer | Student
    email     VARCHAR(100)
);

CREATE TABLE tblAdmin (
    id          VARCHAR(20) PRIMARY KEY REFERENCES tblUser(id) ON DELETE CASCADE,
    department  VARCHAR(100)
);

CREATE TABLE tblLecturer (
    id          VARCHAR(20) PRIMARY KEY REFERENCES tblUser(id) ON DELETE CASCADE,
    department  VARCHAR(100),
    degree      VARCHAR(50)
);

CREATE TABLE tblStudent (
    id          VARCHAR(20) PRIMARY KEY REFERENCES tblUser(id) ON DELETE CASCADE,
    course      VARCHAR(20),                          -- khóa học (vd K23)
    major       VARCHAR(100),
    className   VARCHAR(50)
);

-- ------------------------- Học phần & lớp học phần -------------------------
CREATE TABLE tblCourse (
    id          VARCHAR(20) PRIMARY KEY,
    credits     INT,
    name        VARCHAR(150),
    department  VARCHAR(100)
);

CREATE TABLE tblClassSection (
    id             VARCHAR(20) PRIMARY KEY,
    classId        VARCHAR(30),                        -- mã lớp hiển thị (vd INT1340.01)
    name           VARCHAR(150),
    semester       VARCHAR(20),
    year           VARCHAR(20),
    maxStudents    INT,
    status         VARCHAR(20),
    tblCourseid    VARCHAR(20) REFERENCES tblCourse(id),
    tblLecturerid  VARCHAR(20) REFERENCES tblLecturer(id)
);

-- ------------------------- Lịch học & buổi học -------------------------
CREATE TABLE tblSchedule (
    id                VARCHAR(20) PRIMARY KEY,
    dayOfWeek         VARCHAR(15),
    startPeriod       INT,
    endPeriod         INT,
    room              VARCHAR(30),
    appliedFrom       DATE,
    appliedTo         DATE,
    tblClassSectionid VARCHAR(20) REFERENCES tblClassSection(id)
);

CREATE TABLE tblQRCode (
    id         VARCHAR(20) PRIMARY KEY,
    createdAt  TIMESTAMP,
    expiredAt  TIMESTAMP,
    latitude   DOUBLE PRECISION,
    longitude  DOUBLE PRECISION,
    radius     DOUBLE PRECISION,
    status     VARCHAR(20)                              -- active | inactive
);

CREATE TABLE tblClassSession (
    id                VARCHAR(20) PRIMARY KEY,
    date              DATE,
    startPeriod       INT,
    endPeriod         INT,
    room              VARCHAR(30),
    status            VARCHAR(20),
    tblQRCodeid       VARCHAR(20) REFERENCES tblQRCode(id),
    tblClassSectionid VARCHAR(20) REFERENCES tblClassSection(id)
);

-- ------------------------- Điểm danh -------------------------
CREATE TABLE tblAttendance (
    id                VARCHAR(20) PRIMARY KEY,
    attendedAt        TIMESTAMP,
    latitude          DOUBLE PRECISION,
    longitude         DOUBLE PRECISION,
    method            VARCHAR(20),                      -- QR | MANUAL
    status            VARCHAR(20),                      -- PRESENT | ABSENT | LATE
    tblClassSessionid VARCHAR(20) REFERENCES tblClassSession(id),
    tblStudentid      VARCHAR(20) REFERENCES tblStudent(id)
);

-- ------------------------- Bản ghi học tập (đăng ký + điểm) -------------------------
CREATE TABLE tblCourseRecord (
    id                VARCHAR(20) PRIMARY KEY,
    enrolledAt        DATE,
    status            VARCHAR(20),                      -- Registered | Completed ...
    score1            DOUBLE PRECISION,                 -- điểm thành phần 1 (thường xuyên)
    score2            DOUBLE PRECISION,                 -- điểm thành phần 2 (giữa kỳ)
    score3            DOUBLE PRECISION,                 -- điểm thành phần 3
    examScore         DOUBLE PRECISION,                 -- điểm cuối kỳ
    tblStudentid      VARCHAR(20) REFERENCES tblStudent(id),
    tblClassSectionid VARCHAR(20) REFERENCES tblClassSection(id)
);

-- ------------------------- Thông báo -------------------------
CREATE TABLE tblNotification (
    id             VARCHAR(20) PRIMARY KEY,
    title          VARCHAR(200),
    content        TEXT,
    recipientType  VARCHAR(50),
    sentAt         TIMESTAMP,
    tblUserid      VARCHAR(20) REFERENCES tblUser(id)
);

-- =====================================================================
--  SEED DATA
-- =====================================================================

-- Users (password plaintext theo seed data báo cáo - T08)
INSERT INTO tblUser (id, fullName, dob, gender, phone, username, password, status, role, email) VALUES
  ('A01', 'Quản Trị Viên',     '1990-01-01', 'Male',   '0900000000', 'admin',      '123456', 'active', 'Admin',    'admin@universe'),
  ('GV01','Đỗ Thị Liên',        '1985-05-20', 'Female', '0911111111', 'lecturer',  '123456', 'active', 'Lecturer', 'lien@universe'),
  ('S01', 'Nguyễn Bá Hùng',     '2005-03-17', 'Male',   '0987654321', 'student', '123456', 'active', 'Student',  'hung@stu.universe'),
  ('S02', 'Trần Xuân Thành',    '2005-06-02', 'Male',   '0987654322', 'student2', '123456', 'active', 'Student',  'thanh@stu.universe'),
  ('S03', 'Phạm Thị Thiên Hà',  '2005-09-10', 'Female', '0987654323', 'student3', '123456', 'active', 'Student',  'ha@stu.universe');

INSERT INTO tblAdmin    (id, department)                  VALUES ('A01', 'Phòng Đào tạo');
INSERT INTO tblLecturer (id, department, degree)          VALUES ('GV01', 'Công nghệ Thông tin', 'Tiến sĩ');
INSERT INTO tblStudent  (id, course, major, className)    VALUES
  ('S01', 'K23', 'An toàn thông tin', 'D23CQAT01-B'),
  ('S02', 'K23', 'An toàn thông tin', 'D23CQAT01-B'),
  ('S03', 'K23', 'Công nghệ thông tin', 'D23CQCN02-B');

-- Courses
INSERT INTO tblCourse (id, credits, name, department) VALUES
  ('C01', 3, 'Nhập môn Công nghệ phần mềm', 'Công nghệ Thông tin'),
  ('C02', 3, 'Lập trình Web',                'Công nghệ Thông tin');

-- Class sections
INSERT INTO tblClassSection (id, classId, name, semester, year, maxStudents, status, tblCourseid, tblLecturerid) VALUES
  ('CS01', 'INT1340.01', 'Nhập môn CNPM - Nhóm 01', 'HK1', '2024-2025', 40, 'open',  'C01', 'GV01'),
  ('CS02', 'INT1305.02', 'Lập trình Web - Nhóm 02', 'HK1', '2024-2025', 35, 'open',  'C02', 'GV01');

-- Schedules
INSERT INTO tblSchedule (id, dayOfWeek, startPeriod, endPeriod, room, appliedFrom, appliedTo, tblClassSectionid) VALUES
  ('SCH01', 'Monday',    1, 3, 'A101', '2024-09-02', '2024-12-30', 'CS01'),
  ('SCH02', 'Wednesday', 4, 6, 'B202', '2024-09-02', '2024-12-30', 'CS02');

-- Course records (đăng ký + điểm mẫu)
INSERT INTO tblCourseRecord (id, enrolledAt, status, score1, score2, score3, examScore, tblStudentid, tblClassSectionid) VALUES
  ('CR01', '2024-08-15', 'Registered', 9.0, 8.5, NULL, 9.0, 'S01', 'CS01'),
  ('CR02', '2024-08-15', 'Registered', NULL, NULL, NULL, NULL, 'S01', 'CS02'),
  ('CR03', '2024-08-16', 'Registered', NULL, NULL, NULL, NULL, 'S02', 'CS01');
