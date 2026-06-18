-- Dữ liệu demo bổ sung. Có thể chạy nhiều lần nhờ ON CONFLICT DO NOTHING.

INSERT INTO tblUser
    (id, fullName, dob, gender, phone, username, password, status, role, email)
VALUES
    ('A01', 'Quản Trị Viên', '1990-01-01', 'Male', '0900000000',
     'admin', '123456', 'active', 'Admin', 'admin@universe'),
    ('GV01', 'Đỗ Thị Liên', '1985-05-20', 'Female', '0911111111',
     'lecturer', '123456', 'active', 'Lecturer', 'lien@universe'),
    ('GV02', 'Nguyễn Minh Quân', '1982-11-14', 'Male', '0911111122',
     'lecturer2', '123456', 'active', 'Lecturer', 'quan@universe'),
    ('S01', 'Nguyễn Bá Hùng', '2005-03-17', 'Male', '0987654321',
     'student', '123456', 'active', 'Student', 'hung@stu.universe'),
    ('S02', 'Trần Xuân Thành', '2005-06-02', 'Male', '0987654322',
     'student2', '123456', 'active', 'Student', 'thanh@stu.universe'),
    ('S03', 'Phạm Thị Thiên Hà', '2005-09-10', 'Female', '0987654323',
     'student3', '123456', 'active', 'Student', 'ha@stu.universe'),
    ('S04', 'Lê Hoàng Nam', '2005-01-21', 'Male', '0987654324',
     'student4', '123456', 'active', 'Student', 'nam@stu.universe'),
    ('S05', 'Vũ Minh Anh', '2005-08-08', 'Female', '0987654325',
     'student5', '123456', 'active', 'Student', 'anh@stu.universe'),
    ('S06', 'Đặng Thu Trang', '2005-12-12', 'Female', '0987654326',
     'student6', '123456', 'active', 'Student', 'trang@stu.universe')
ON CONFLICT DO NOTHING;

INSERT INTO tblAdmin (id, department)
VALUES ('A01', 'Phòng Đào tạo')
ON CONFLICT DO NOTHING;

INSERT INTO tblLecturer (id, department, degree)
VALUES
    ('GV01', 'Công nghệ Thông tin', 'Tiến sĩ'),
    ('GV02', 'An toàn Thông tin', 'Thạc sĩ')
ON CONFLICT DO NOTHING;

INSERT INTO tblStudent (id, course, major, className)
VALUES
    ('S01', 'K23', 'An toàn thông tin', 'D23CQAT01-B'),
    ('S02', 'K23', 'An toàn thông tin', 'D23CQAT01-B'),
    ('S03', 'K23', 'Công nghệ thông tin', 'D23CQCN02-B'),
    ('S04', 'K23', 'Công nghệ thông tin', 'D23CQCN01-B'),
    ('S05', 'K23', 'Khoa học máy tính', 'D23CQAI01-B'),
    ('S06', 'K23', 'Khoa học máy tính', 'D23CQAI01-B')
ON CONFLICT DO NOTHING;

INSERT INTO tblCourse (id, credits, name, department)
VALUES
    ('C01', 3, 'Nhập môn Công nghệ phần mềm', 'Công nghệ Thông tin'),
    ('C02', 3, 'Lập trình Web', 'Công nghệ Thông tin'),
    ('C03', 3, 'Nhập môn Trí tuệ Nhân tạo', 'Công nghệ Thông tin'),
    ('C04', 3, 'Cơ sở dữ liệu', 'Công nghệ Thông tin'),
    ('C05', 3, 'Mạng máy tính', 'An toàn Thông tin')
ON CONFLICT DO NOTHING;

INSERT INTO tblClassSection
    (id, classId, name, semester, year, maxStudents, status, tblCourseid, tblLecturerid)
VALUES
    ('CS01', 'INT1340.01', 'Nhập môn CNPM - Nhóm 01', 'HK1', '2025-2026', 40, 'open', 'C01', 'GV01'),
    ('CS02', 'INT1305.02', 'Lập trình Web - Nhóm 02', 'HK1', '2025-2026', 35, 'open', 'C02', 'GV01'),
    ('CS03', 'INT3401.01', 'Nhập môn Trí tuệ Nhân tạo - Nhóm 01', 'HK2', '2025-2026', 45, 'open', 'C03', 'GV01'),
    ('CS04', 'INT1313.01', 'Cơ sở dữ liệu - Nhóm 01', 'HK2', '2025-2026', 40, 'open', 'C04', 'GV02'),
    ('CS05', 'INT1336.01', 'Mạng máy tính - Nhóm 01', 'HK2', '2025-2026', 40, 'open', 'C05', 'GV02')
ON CONFLICT DO NOTHING;

INSERT INTO tblSchedule
    (id, dayOfWeek, startPeriod, endPeriod, room, appliedFrom, appliedTo, tblClassSectionid)
VALUES
    ('SCH01', 'Monday', 1, 3, 'A101', '2026-01-12', '2026-06-30', 'CS01'),
    ('SCH02', 'Wednesday', 4, 6, 'B202', '2026-01-12', '2026-06-30', 'CS02'),
    ('SCH03', 'Tuesday', 1, 3, 'A303', '2026-01-12', '2026-06-30', 'CS03'),
    ('SCH04', 'Thursday', 7, 9, 'A205', '2026-01-12', '2026-06-30', 'CS04'),
    ('SCH05', 'Friday', 4, 6, 'B304', '2026-01-12', '2026-06-30', 'CS05')
ON CONFLICT DO NOTHING;

INSERT INTO tblQRCode
    (id, createdAt, expiredAt, latitude, longitude, radius, status)
VALUES
    ('QR01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '15 minutes',
     21.028511, 105.804817, 50, 'active'),
    ('QR02', CURRENT_TIMESTAMP - INTERVAL '2 days',
     CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '15 minutes',
     21.028511, 105.804817, 50, 'inactive')
ON CONFLICT DO NOTHING;

INSERT INTO tblClassSession
    (id, date, startPeriod, endPeriod, room, status, tblQRCodeid, tblClassSectionid)
VALUES
    ('SES01', CURRENT_DATE, 1, 3, 'A101', 'open', 'QR01', 'CS01'),
    ('SES02', CURRENT_DATE - 2, 1, 3, 'A303', 'closed', 'QR02', 'CS03'),
    ('SES03', CURRENT_DATE - 1, 4, 6, 'B202', 'closed', NULL, 'CS02'),
    ('SES04', CURRENT_DATE, 7, 9, 'A205', 'created', NULL, 'CS04')
ON CONFLICT DO NOTHING;

INSERT INTO tblCourseRecord
    (id, enrolledAt, status, score1, score2, score3, examScore, tblStudentid, tblClassSectionid)
VALUES
    ('CR01', '2026-01-05', 'Registered', 9.0, 8.5, 8.0, 9.0, 'S01', 'CS01'),
    ('CR02', '2026-01-05', 'Registered', 8.0, 7.5, 8.5, 8.0, 'S01', 'CS02'),
    ('CR03', '2026-01-06', 'Registered', 7.5, 8.0, 8.0, NULL, 'S02', 'CS01'),
    ('CR04', '2026-01-06', 'Registered', 8.5, 9.0, NULL, NULL, 'S02', 'CS03'),
    ('CR05', '2026-01-07', 'Registered', 9.0, 9.0, 8.5, NULL, 'S03', 'CS03'),
    ('CR06', '2026-01-07', 'Registered', 7.0, 7.5, NULL, NULL, 'S04', 'CS02'),
    ('CR07', '2026-01-08', 'Registered', 8.0, 8.0, NULL, NULL, 'S05', 'CS03'),
    ('CR08', '2026-01-08', 'Registered', 8.5, 8.0, NULL, NULL, 'S06', 'CS03'),
    ('CR09', '2026-01-08', 'Registered', NULL, NULL, NULL, NULL, 'S04', 'CS04'),
    ('CR10', '2026-01-08', 'Registered', NULL, NULL, NULL, NULL, 'S05', 'CS05')
ON CONFLICT DO NOTHING;

INSERT INTO tblAttendance
    (id, attendedAt, latitude, longitude, method, status, tblClassSessionid, tblStudentid)
VALUES
    ('ATT01', CURRENT_TIMESTAMP - INTERVAL '10 minutes',
     21.028510, 105.804815, 'QR', 'PRESENT', 'SES01', 'S01'),
    ('ATT02', CURRENT_TIMESTAMP - INTERVAL '4 minutes',
     21.028520, 105.804810, 'QR', 'LATE', 'SES01', 'S02'),
    ('ATT03', CURRENT_TIMESTAMP - INTERVAL '2 days',
     21.028509, 105.804820, 'QR', 'PRESENT', 'SES02', 'S02'),
    ('ATT04', CURRENT_TIMESTAMP - INTERVAL '2 days',
     NULL, NULL, 'MANUAL', 'PRESENT', 'SES02', 'S03')
ON CONFLICT DO NOTHING;

INSERT INTO tblNotification
    (id, title, content, recipientType, sentAt, tblUserid)
VALUES
    ('NTF01', 'Chào mừng đến UniVerse',
     'Hệ thống đã sẵn sàng. Hãy kiểm tra thời khóa biểu và thông báo thường xuyên.',
     'all-students', CURRENT_TIMESTAMP - INTERVAL '3 days', 'S01'),
    ('NTF02', 'Nhắc lịch học Trí tuệ Nhân tạo',
     'Lớp INT3401.01 học tại phòng A303, tiết 1-3.',
     'class:CS03', CURRENT_TIMESTAMP - INTERVAL '1 day', 'S02'),
    ('NTF03', 'Nhắc lịch học Trí tuệ Nhân tạo',
     'Lớp INT3401.01 học tại phòng A303, tiết 1-3.',
     'class:CS03', CURRENT_TIMESTAMP - INTERVAL '1 day', 'S03'),
    ('NTF04', 'Nhắc lịch học Trí tuệ Nhân tạo',
     'Lớp INT3401.01 học tại phòng A303, tiết 1-3.',
     'class:CS03', CURRENT_TIMESTAMP - INTERVAL '1 day', 'S05'),
    ('NTF05', 'Nhắc lịch học Trí tuệ Nhân tạo',
     'Lớp INT3401.01 học tại phòng A303, tiết 1-3.',
     'class:CS03', CURRENT_TIMESTAMP - INTERVAL '1 day', 'S06'),
    ('NTF06', 'Cập nhật hệ thống',
     'Giảng viên có thể tạo QR động và theo dõi điểm danh theo thời gian thực.',
     'all-lecturers', CURRENT_TIMESTAMP - INTERVAL '2 hours', 'GV01'),
    ('NTF07', 'Cập nhật hệ thống',
     'Giảng viên có thể tạo QR động và theo dõi điểm danh theo thời gian thực.',
     'all-lecturers', CURRENT_TIMESTAMP - INTERVAL '2 hours', 'GV02'),
    ('NTF08', 'Nhắc nộp bài tập',
     'Các em nhớ nộp bài tập về nhà môn Công nghệ phần mềm nhé.',
     'class:CS01', CURRENT_TIMESTAMP - INTERVAL '1 hour', 'GV01'),
    ('NTF09', 'Thông báo nghỉ học',
     'Hôm nay môn Lập trình Web nghỉ do giảng viên bận công tác.',
     'class:CS02', CURRENT_TIMESTAMP - INTERVAL '30 minutes', 'GV01'),
    ('NTF10', 'Điểm danh cảnh báo',
     'Bạn đã vắng mặt 2 buổi học môn Nhập môn CNPM. Vui lòng chú ý đi học đầy đủ.',
     'student', CURRENT_TIMESTAMP - INTERVAL '5 hours', 'S01'),
    ('NTF11', 'Thông báo nộp học phí',
     'Hạn chót nộp học phí học kỳ 1 là ngày 15/09/2026.',
     'all-students', CURRENT_TIMESTAMP - INTERVAL '12 hours', 'S01'),
    ('NTF12', 'Thông báo nộp học phí',
     'Hạn chót nộp học phí học kỳ 1 là ngày 15/09/2026.',
     'all-students', CURRENT_TIMESTAMP - INTERVAL '12 hours', 'S04'),
    ('NTF13', 'Có điểm thi mới',
     'Môn Nhập môn CNPM đã có điểm thi. Vui lòng kiểm tra trên hệ thống.',
     'class:CS01', CURRENT_TIMESTAMP - INTERVAL '2 hours', 'S01')
ON CONFLICT DO NOTHING;
