CREATE TABLE IF NOT EXISTS tblUser (
    id VARCHAR(20) PRIMARY KEY,
    fullName VARCHAR(100),
    dob DATE,
    gender VARCHAR(10),
    phone VARCHAR(15),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    status VARCHAR(20) DEFAULT 'active',
    role VARCHAR(20) NOT NULL,
    email VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS tblAdmin (
    id VARCHAR(20) PRIMARY KEY REFERENCES tblUser(id) ON DELETE CASCADE,
    department VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS tblLecturer (
    id VARCHAR(20) PRIMARY KEY REFERENCES tblUser(id) ON DELETE CASCADE,
    department VARCHAR(100),
    degree VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS tblStudent (
    id VARCHAR(20) PRIMARY KEY REFERENCES tblUser(id) ON DELETE CASCADE,
    course VARCHAR(20),
    major VARCHAR(100),
    className VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS tblCourse (
    id VARCHAR(20) PRIMARY KEY,
    credits INT,
    name VARCHAR(150),
    department VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS tblClassSection (
    id VARCHAR(20) PRIMARY KEY,
    classId VARCHAR(30),
    name VARCHAR(150),
    semester VARCHAR(20),
    year VARCHAR(20),
    maxStudents INT,
    status VARCHAR(20),
    tblCourseid VARCHAR(20) REFERENCES tblCourse(id),
    tblLecturerid VARCHAR(20) REFERENCES tblLecturer(id)
);

CREATE TABLE IF NOT EXISTS tblSchedule (
    id VARCHAR(20) PRIMARY KEY,
    dayOfWeek VARCHAR(15),
    startPeriod INT,
    endPeriod INT,
    room VARCHAR(30),
    appliedFrom DATE,
    appliedTo DATE,
    tblClassSectionid VARCHAR(20) REFERENCES tblClassSection(id)
);

CREATE TABLE IF NOT EXISTS tblQRCode (
    id VARCHAR(20) PRIMARY KEY,
    createdAt TIMESTAMP,
    expiredAt TIMESTAMP,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    radius DOUBLE PRECISION,
    status VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS tblClassSession (
    id VARCHAR(20) PRIMARY KEY,
    date DATE,
    startPeriod INT,
    endPeriod INT,
    room VARCHAR(30),
    status VARCHAR(20),
    tblQRCodeid VARCHAR(20) REFERENCES tblQRCode(id),
    tblClassSectionid VARCHAR(20) REFERENCES tblClassSection(id)
);

CREATE TABLE IF NOT EXISTS tblAttendance (
    id VARCHAR(20) PRIMARY KEY,
    attendedAt TIMESTAMP,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    distance DOUBLE PRECISION,
    method VARCHAR(20),
    status VARCHAR(20),
    tblClassSessionid VARCHAR(20) REFERENCES tblClassSession(id),
    tblStudentid VARCHAR(20) REFERENCES tblStudent(id)
);

CREATE TABLE IF NOT EXISTS tblCourseRecord (
    id VARCHAR(20) PRIMARY KEY,
    enrolledAt DATE,
    status VARCHAR(20),
    score1 DOUBLE PRECISION,
    score2 DOUBLE PRECISION,
    score3 DOUBLE PRECISION,
    examScore DOUBLE PRECISION,
    tblStudentid VARCHAR(20) REFERENCES tblStudent(id),
    tblClassSectionid VARCHAR(20) REFERENCES tblClassSection(id),
    UNIQUE (tblStudentid, tblClassSectionid)
);

CREATE TABLE IF NOT EXISTS tblNotification (
    id VARCHAR(20) PRIMARY KEY,
    title VARCHAR(200),
    content TEXT,
    recipientType VARCHAR(50),
    sentAt TIMESTAMP,
    tblUserid VARCHAR(20) REFERENCES tblUser(id)
);

INSERT INTO tblUser
    (id, fullName, dob, gender, phone, username, password, status, role, email)
VALUES
    ('GV01', 'Giảng viên UniVerse', '1985-05-20', 'Female', '0911111111',
     'lecturer', '123456', 'active', 'Lecturer', 'lecturer@universe.edu.vn'),
    ('S01', 'Sinh viên UniVerse', '2005-03-17', 'Male', '0987654321',
     'student', '123456', 'active', 'Student', 'student@universe.edu.vn')
ON CONFLICT (id) DO NOTHING;

INSERT INTO tblLecturer (id, department, degree)
VALUES ('GV01', 'Công nghệ Thông tin', 'Tiến sĩ')
ON CONFLICT (id) DO NOTHING;

INSERT INTO tblStudent (id, course, major, className)
VALUES ('S01', 'K23', 'Công nghệ thông tin', 'D23CQCN01-B')
ON CONFLICT (id) DO NOTHING;

INSERT INTO tblCourse (id, credits, name, department)
VALUES
    ('C01', 3, 'Nhập môn Công nghệ phần mềm', 'Công nghệ Thông tin'),
    ('C02', 3, 'Lập trình Web', 'Công nghệ Thông tin'),
    ('C03', 3, 'Nhập môn Trí tuệ Nhân tạo', 'Công nghệ Thông tin')
ON CONFLICT (id) DO NOTHING;

INSERT INTO tblClassSection
    (id, classId, name, semester, year, maxStudents, status,
     tblCourseid, tblLecturerid)
VALUES
    ('CS01', 'INT1340.01', 'Nhập môn CNPM - Nhóm 01',
     'HK1', '2026-2027', 40, 'open', 'C01', 'GV01'),
    ('CS02', 'INT1305.02', 'Lập trình Web - Nhóm 02',
     'HK1', '2026-2027', 35, 'open', 'C02', 'GV01'),
    ('CS03', 'INT3401.01', 'Nhập môn Trí tuệ Nhân tạo - Nhóm 01',
     'HK1', '2026-2027', 45, 'open', 'C03', 'GV01')
ON CONFLICT (id) DO NOTHING;

INSERT INTO tblSchedule
    (id, dayOfWeek, startPeriod, endPeriod, room,
     appliedFrom, appliedTo, tblClassSectionid)
VALUES
    ('SCH01', 'Monday', 1, 3, 'A101', '2026-08-17', '2026-12-31', 'CS01'),
    ('SCH02', 'Wednesday', 4, 6, 'B202', '2026-08-17', '2026-12-31', 'CS02'),
    ('SCH03', 'Friday', 1, 3, 'A303', '2026-08-17', '2026-12-31', 'CS03')
ON CONFLICT (id) DO NOTHING;

INSERT INTO tblCourseRecord
    (id, enrolledAt, status, score1, score2, score3, examScore,
     tblStudentid, tblClassSectionid)
VALUES
    ('CR01', CURRENT_DATE, 'Registered', 9.0, 8.5, 8.0, 9.0, 'S01', 'CS01')
ON CONFLICT (id) DO NOTHING;

INSERT INTO tblNotification
    (id, title, content, recipientType, sentAt, tblUserid)
VALUES
    ('NTF01', 'Chào mừng đến UniVerse Cloud',
     'Ứng dụng Android đã kết nối thành công với hệ thống cloud.',
     'student', CURRENT_TIMESTAMP, 'S01')
ON CONFLICT (id) DO NOTHING;
