# Plan tài liệu – UniVerse (Nhóm HTH)

> BƯỚC 0 – PLAN theo Unified Process

---

## Phạm vi

Viết **toàn bộ báo cáo** 3 chương theo cấu trúc yêu cầu của cô, ánh xạ sang UP workflows:

- **Claude viết:** Chương 1 (Tổng quan + Requirements) + phần phân tích Pha II cho cả 3 module
- **3 thành viên viết tiếp:** Chương 3 (Design + CSDL + Test) theo module được phân công
- **UC14 (Nhắn tin trong lớp) đã bỏ khỏi phạm vi** — phạm vi giới hạn UC01–UC13

---

## Thông tin đầu vào đã có

| Mục                   | Giá trị                                                           |
| ---------------------- | ------------------------------------------------------------------- |
| Tên hệ thống        | UniVerse – Hệ thống quản lý đại học thông minh             |
| Nhóm                  | HTH — Trần Xuân Thành, Nguyễn Bá Hùng, Phạm Thị Thiên Hà |
| Actor                  | Sinh viên, Giảng viên, Quản trị viên                          |
| Công nghệ giao diện | **HTML (React / Next.js)** — đã xác định từ codebase   |
| Backend                | NestJS + TypeScript + PostgreSQL + MongoDB + Redis                  |
| Mã UC                 | UC01–UC13 (13 use cases, 3 module) — UC14 đã bỏ              |

---

## Câu hỏi cần làm rõ

- [ ] Xác nhận phân chia 3 module như đề xuất không? (xem bảng bên dưới)
- [ ] Chương 2 Pha II (Analysis): làm cho **cả 3 module** hay chỉ **1 module đại diện**?
- [ ] Phân công module theo đúng vai trò (Thiên Hà=Module 1, Hùng=Module 2, Thành=Module 3)?

---

## Đề xuất phân chia 3 module

| STT | Module                                   | Use Case                                                                                                       | Thành viên phụ trách (Pha III+IV) |
| --- | ---------------------------------------- | -------------------------------------------------------------------------------------------------------------- | ------------------------------------- |
| 1   | **Auth & Quản lý người dùng** | UC01 Đăng nhập, UC02 Đăng xuất, UC03 Quản lý người dùng, UC04 Gửi thông báo                      | Phạm Thị Thiên Hà                 |
| 2   | **Quản lý học tập (Academic)** | UC05 Quản lý khóa học/lớp học, UC06 Quản lý lịch học, UC07 Đăng ký môn học, UC08 Xem lịch học | Nguyễn Bá Hùng                     |
| 3   | **Điểm danh & Học vụ**         | UC09 Tạo QR, UC10 Điểm danh QR+GPS, UC11 Quản lý điểm danh, UC12 Nhập điểm, UC13 Xem điểm *(UC14 Nhắn tin: đã bỏ)* | Trần Xuân Thành |

---

## Danh sách mục Claude sẽ viết (Chương 1 + 2)

### Chương 1 — Mở đầu

| Mục | Nội dung chính                                                               |
| ---- | ------------------------------------------------------------------------------ |
| 1.1  | Tổng quan: bối cảnh đại học, vấn đề phân mảnh, giải pháp UniVerse |
| 1.2  | Công nghệ: bảng stack NestJS/Next.js/React Native/Docker                    |

### Giai đoạn 1 — Requirements toàn hệ thống (→ Mục 2.1–2.4)

| Mục UP                   | Mục báo cáo | Nội dung sẽ có                                                  |
| ------------------------- | -------------- | ------------------------------------------------------------------ |
| Bảng thuật ngữ         | 2.1.1          | ~15 thuật ngữ: QR Token, Geo-fencing, HMAC-SHA256, Enrollment... |
| 2.1 Mục tiêu & phạm vi | 2.1.2          | Văn xuôi mô tả hệ thống, 3 actor chính                      |
| 2.2 Actor                 | 2.2            | Bảng 3 actor + mô tả vai trò                                   |
| 2.3 Chức năng           | 2.2            | Use case per actor (14 UC)                                         |
| 2.4 Luồng hoạt động   | 2.1.4          | 14 luồng mũi tên → cho 14 UC                                   |
| 2.5 Đối tượng         | 2.1.3          | 7 entity PostgreSQL + 3 MongoDB schema                             |
| 2.6 Quan hệ              | 2.1.5          | Bảng quan hệ đầy đủ (1-n, n-n)                               |
| 3.1 Actor                 | 2.2            | Bảng Actor (đã có)                                             |
| 3.2 UC per Actor          | 2.2            | Bảng 14 UC theo actor                                             |
| 3.3 UC Diagram            | 2.4            | 4 PlantUML: tổng quan + 3 module                                  |

### Giai đoạn 3 — Analysis từng module (→ Mục 2.4 chi tiết + Pha II)

**Module 1: Auth & Quản lý người dùng (UC01–UC04)**

| Mục | Tên                           | Nội dung sẽ có                                                                        |
| ---- | ------------------------------ | ---------------------------------------------------------------------------------------- |
| I.1  | UC Module 1                    | UC01 đăng nhập <`<include>`> xác thực JWT; UC03 CRUD <`<include>`> kiểm quyền |
| II.1 | Kịch bản UC01 Đăng nhập   | 10 bước; ngoại lệ: sai MK, tài khoản bị khóa                                     |
| II.1 | Kịch bản UC03 Quản lý User | 8 bước CRUD + bulk import CSV                                                          |
| II.2 | Mô hình hóa lớp            | Lớp: User, Role (enum); quan hệ 1-n User→Attendance                                   |
| II.3 | Sơ đồ lớp phân tích      | Boundary: LoginPage, UserManagementPage; Entity: User                                    |
| II.4 | Tuần tự phân tích          | 2 biểu đồ: UC01 + UC03                                                                |

**Module 2: Quản lý học tập (UC05–UC08)**

| Mục | Tên                            | Nội dung sẽ có                                                               |
| ---- | ------------------------------- | ------------------------------------------------------------------------------- |
| I.1  | UC Module 2                     | UC05 Quản lý lớp <`<include>`> phát hiện xung đột lịch                |
| II.1 | Kịch bản UC07 Đăng ký môn | 8 bước; ngoại lệ: lớp đầy, trùng lịch                                  |
| II.1 | Kịch bản UC05 Quản lý lớp  | 10 bước; ngoại lệ: xung đột phòng/GV                                     |
| II.2 | Mô hình hóa lớp             | Course 1-n Class n-m Student (qua Enrollment); Class 1-n Schedule               |
| II.3 | Sơ đồ lớp phân tích       | Boundary: CoursePage, SchedulePage; Entity: Course, Class, Schedule, Enrollment |
| II.4 | Tuần tự phân tích           | 2 biểu đồ: UC07 + UC05                                                       |

**Module 3: Điểm danh & Học vụ (UC09–UC13)**

| Mục | Tên | Nội dung sẽ có |
|-----|-----|----------------|
| I.1 | UC Module 3 | UC09–UC13; UC10 <<include>> xác minh QR (HMAC) + xác minh GPS (Haversine) |
| II.1 | Kịch bản UC10 Điểm danh | 14 bước; ngoại lệ: QR hết hạn, ngoài 50m, đã điểm danh |
| II.1 | Kịch bản UC12 Nhập điểm | 13 bước; ngoại lệ: điểm ngoài 0–10, chưa publish |
| II.2 | Mô hình hóa lớp | Attendance n-1 Schedule; Grade n-1 Enrollment |
| II.3 | Sơ đồ lớp phân tích | Boundary: AttendancePage, AttendanceManagementPage, GradeManagementPage, GradesPage; Entity: Attendance, Grade |
| II.4 | Tuần tự phân tích | 2 biểu đồ: UC10 + UC12 |

---

## Danh sách mục 3 thành viên viết (Chương 3 + 4)

### Module 1 — Phạm Thị Thiên Hà

| Mục    | Tên                   | Nội dung                                                         |
| ------- | ---------------------- | ----------------------------------------------------------------- |
| III.1   | Thiết kế lớp Entity | `User { id: string, email: string, role: Role, ... }`           |
| III.2   | Thiết kế CSDL        | Bảng `users` (DDL)                                             |
| III.3.1 | Giao diện             | Wireframe: LoginPage, UserManagementPage, NotificationComposePage |
| III.3.2 | Sơ đồ lớp TK       | `LoginPage<<Component>>` → `UserDAO` → `User`             |
| III.4   | Tuần tự TK           | 2 biểu đồ: UC01 + UC03 (tên hàm TS đầy đủ)               |
| IV      | Test                   | 8 test case UC01 + UC03                                           |

### Module 2 — Nguyễn Bá Hùng

| Mục    | Tên                   | Nội dung                                                                    |
| ------- | ---------------------- | ---------------------------------------------------------------------------- |
| III.1   | Thiết kế lớp Entity | `Course`, `Class` (latitude/longitude GPS), `Schedule`, `Enrollment` |
| III.2   | Thiết kế CSDL        | 4 bảng:`courses`, `classes`, `schedules`, `enrollments`             |
| III.3.1 | Giao diện             | Wireframe: CourseMgmtPage, SchedulePage, EnrollmentPage                      |
| III.3.2 | Sơ đồ lớp TK       | DAO cho 4 entity                                                             |
| III.4   | Tuần tự TK           | 2 biểu đồ: UC07 + UC05                                                    |
| IV      | Test                   | 8 test case UC07 + UC05                                                      |

### Module 3 — Trần Xuân Thành

| Mục | Tên | Nội dung |
|-----|-----|----------|
| III.1 | Thiết kế lớp Entity | `Attendance` (qrToken, gpsLat, gpsLon, distance, status), `Grade` (type, weight, score) |
| III.2 | Thiết kế CSDL | Bảng `attendances`, `grades` |
| III.3.1 | Giao diện | Wireframe: AttendancePage (QR scan + GPS indicator), GradeManagementPage, GradesPage |
| III.3.2 | Sơ đồ lớp TK | DAO cho Attendance, Grade |
| III.3 | Thuật toán | Haversine formula, HMAC-SHA256 QR token (TTL 5s Redis), công thức điểm 0.2QT+0.3GK+0.5CK |
| III.4 | Tuần tự TK | 2 biểu đồ: UC10 + UC12 |
| IV | Test | 8 test case UC10 + UC12 |

---

## Quy tắc thống nhất (cả 3 người)

1. **Tên lớp = tên entity trong codebase** (User, Class, Attendance, Grade, Course, Schedule, Enrollment)
2. **Boundary dùng React Component**: `LoginPage<<Component>>`, không dùng JFrame
3. **DAO = Service layer**: `UserDAO` = `users.service.ts`
4. **Mã UC xuyên suốt**: UC01–UC13 không thay đổi giữa các chương — **UC14 đã bỏ**
5. **PlantUML**: `left to right direction` + `skinparam linetype ortho`
6. **File markdown nguồn**: `report/md/module1_auth_users.md`, `report/md/module2_academic.md`, `report/md/module3_attendance_grades.md`
7. **Ngôn ngữ**: 100% tiếng Việt (trừ tên hàm/lớp TypeScript ở Pha III)
8. **Cấu trúc chương theo yêu cầu cô**: 3 chương (Tổng quan / Phương pháp / Phân tích+Thực nghiệm)
