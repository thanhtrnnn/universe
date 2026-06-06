# Báo Cáo Bài Tập Lớn - UniVerse
> Nhóm HTH | Môn: Nhập môn Công nghệ Phần mềm | GVHD: Đỗ Thị Liên
> Công nghệ giao diện: **HTML (React / Next.js)**

---

## Outline yêu cầu của cô (giữ nguyên để tham chiếu)

> CHƯƠNG 1 : Tổng quan về bài toán / lĩnh vực nghiên cứu
> o Xác định yêu cầu
> o Khảo sát những nghiên cứu liên quan
> o Thiết kế tương tác cho sản phẩm
>
> CHƯƠNG 2 : Nghiên cứu phương pháp tiếp cận và giải quyết vấn đề
> o Mô hình tổng quát hệ thống
> o Phương pháp xây dựng phần mềm
> o Mô hình phát triển phần mềm
> o Kiến trúc phần mềm được áp dụng trong triển khai lập trình hệ thống
> o Lựa chọn công nghệ phù hợp để triển khai xây dựng hệ thống
>
> CHƯƠNG 3 : Phân tích thiết kế và thực nghiệm hệ thống (theo phương pháp đưa ra ở chương 2)
> o Phân tích thiết kế hệ thống : dùng ngôn ngữ mô hình hóa hợp nhất UML,…
> o Thiết kế cơ sở dữ liệu (nếu có).
> o Thực nghiệm (nếu có) :
> ▪ Dữ liệu thực nghiệm
> ▪ Phương pháp thực nghiệm
> ▪ Kết quả thực nghiệm

---

## CHƯƠNG 1: TỔNG QUAN VỀ BÀI TOÁN

*[Ai viết: cả nhóm thống nhất — phần chung, hoàn thành trước khi chia việc]*

### 1.1. Xác định yêu cầu

**Bối cảnh & vấn đề đặt ra**
- Quản lý đại học hiện tại bị phân mảnh: điểm danh thủ công dễ gian lận, hệ thống điểm số riêng lẻ, thiếu kênh giao tiếp trực tiếp, thông báo không kịp thời.
- Sinh viên phải dùng nhiều ứng dụng khác nhau để tra lịch, xem điểm, liên hệ giảng viên.

**Mục tiêu hệ thống UniVerse**
- Xây dựng hệ sinh thái số tích hợp cho môi trường đại học.
- Kết nối 3 nhóm người dùng: Sinh viên, Giảng viên, Quản trị viên trên một nền tảng duy nhất.

**Danh sách Actor**

| Actor | Mô tả |
|-------|-------|
| Sinh viên | Điểm danh QR+GPS, xem lịch học, đăng ký môn, xem điểm |
| Giảng viên | Tạo QR điểm danh, quản lý điểm danh, nhập điểm, xem lịch dạy |
| Quản trị viên | CRUD người dùng, quản lý khóa học/lớp học, xếp lịch, gửi thông báo |

**Danh sách yêu cầu chức năng (UC01–UC13)**

| Mã UC | Actor | Chức năng |
|-------|-------|-----------|
| UC01 | Sinh viên, Giảng viên, Admin | Đăng nhập |
| UC02 | Sinh viên, Giảng viên, Admin | Đăng xuất |
| UC03 | Admin | Quản lý người dùng (CRUD + bulk import) |
| UC04 | Admin | Gửi thông báo hệ thống |
| UC05 | Admin | Quản lý khóa học & lớp học |
| UC06 | Admin | Quản lý thời khóa biểu |
| UC07 | Sinh viên | Đăng ký môn học |
| UC08 | Sinh viên | Xem lịch học |
| UC09 | Giảng viên | Tạo mã QR điểm danh |
| UC10 | Sinh viên | Điểm danh QR + GPS |
| UC11 | Giảng viên | Quản lý & chỉnh sửa điểm danh |
| UC12 | Giảng viên | Nhập & công bố điểm số |
| UC13 | Sinh viên | Xem điểm & tiến độ học tập |

**Yêu cầu phi chức năng**
- Bảo mật: JWT (Access Token 15 phút + Refresh Token 7 ngày), bcrypt, RBAC 3 vai trò
- Hiệu năng: QR token HMAC-SHA256 xoay vòng 5 giây (Redis TTL), Haversine GPS ≤ 50m
- Khả năng mở rộng: Docker Compose 8 services, Kafka event streaming
- Độ tin cậy: TypeORM migrations, conflict detection lịch học

### 1.2. Khảo sát những nghiên cứu liên quan

- Các hệ thống LMS phổ biến: Moodle, Google Classroom — chưa tích hợp điểm danh sinh trắc học.
- Hệ thống điểm danh QR hiện tại: thiếu xác minh vị trí GPS → dễ gian lận bằng cách chia sẻ QR.
- Hướng giải quyết của UniVerse: kết hợp HMAC-SHA256 (chống replay attack) + Haversine Geo-fencing (xác minh vị trí thực).

### 1.3. Thiết kế tương tác cho sản phẩm

*[Chia theo module — 1 người viết wireframe cho module của mình]*

- **Module 1:** Wireframe LoginPage, UserManagementPage, NotificationComposePage
- **Module 2:** Wireframe ClassManagementPage, CourseRegistrationPage, SchedulePage
- **Module 3:** Wireframe AttendancePage (QR scan + GPS indicator), GradeManagementPage, GradesPage

Luồng tương tác theo vai trò:
- **Sinh viên:** Đăng nhập → Xem lịch → Quét QR điểm danh → Xem điểm
- **Giảng viên:** Đăng nhập → Tạo QR → Xem/sửa điểm danh → Nhập điểm
- **Admin:** Đăng nhập → Quản lý user → Tạo lớp/lịch → Gửi thông báo

---

## CHƯƠNG 2: PHƯƠNG PHÁP TIẾP CẬN VÀ GIẢI QUYẾT VẤN ĐỀ

*[Ai viết: Nguyễn Bá Hùng phụ trách phần 2.1 + 2.4; cả nhóm thống nhất 2.2 + 2.3 + 2.5]*

### 2.1. Mô hình tổng quát hệ thống

Kiến trúc 3 tầng:
```
[Client Layer]   Next.js Web  +  React Native Mobile
      ↕  REST API  +  Socket.IO (WebSocket)
[API Layer]      NestJS (TypeScript) — 7 module
      ↕  TypeORM  /  Mongoose  /  Redis Client  /  Kafka Producer
[Data Layer]     PostgreSQL  +  MongoDB  +  Redis  +  Kafka
```

Luồng dữ liệu chính:
- REST API: xác thực, CRUD nghiệp vụ
- Socket.IO: thông báo real-time, cập nhật điểm danh tức thì
- Kafka: streaming sự kiện thông báo bất đồng bộ

### 2.2. Phương pháp xây dựng phần mềm

**Unified Process (UP):**
- Hướng Use-case: mọi quyết định thiết kế đều xuất phát từ UC.
- Lặp và tăng dần (Iterative & Incremental): 3 vòng lặp song song theo 3 module.
- Tập trung vào kiến trúc: xác định sớm kiến trúc NestJS module + PostgreSQL + Redis.

**4 pha UP ánh xạ vào báo cáo:**
| Pha UP | Nội dung | Ánh xạ báo cáo |
|--------|----------|----------------|
| Inception | Xác định yêu cầu, actor, phạm vi | Chương 1.1 |
| Elaboration | Phân tích (BCE, UC detail, sequence) | Chương 3.1 |
| Construction | Thiết kế CSDL, cài đặt, test | Chương 3.2 + 3.3 |
| Transition | Demo, đánh giá kết quả | Chương 3.3 |

### 2.3. Mô hình phát triển phần mềm

- **Iterative & Incremental** chia theo module chức năng.
- 3 module phát triển song song, mỗi thành viên phụ trách 1 module từ Analysis → Design → Test.
- Quy trình: Requirements → Analysis (BCE) → Design (DB + Sequence) → Implement → Test.

### 2.4. Kiến trúc phần mềm được áp dụng

**Backend — NestJS Module Pattern:**
- Mỗi module: Controller (route) → Service (business logic) → Repository (TypeORM/Mongoose).
- Guard: JWT AuthGuard + RolesGuard (RBAC).
- Gateway: Socket.IO gateway cho real-time events.

**Frontend — Next.js App Router:**
- App Router với layout theo role (admin, lecturer, student).
- React Server Components + Client Components (tương tác).
- Boundary (React Component) → Service call (fetch/axios) → API.

**Bảo mật:**
- JWT: Access Token 15 phút (memory), Refresh Token 7 ngày (Redis).
- QR điểm danh: HMAC-SHA256 ký payload `{scheduleId, timestamp}`, TTL 5 giây trong Redis.
- RBAC: 3 vai trò STUDENT / LECTURER / ADMIN kiểm tra bằng decorator `@Roles()`.

### 2.5. Lựa chọn công nghệ phù hợp

| Tầng | Công nghệ | Phiên bản | Lý do lựa chọn |
|------|-----------|-----------|----------------|
| Backend API | NestJS + TypeScript | 10.3 / 5.3 | Module pattern, DI, decorator, dễ test |
| Frontend Web | Next.js + React | 14+ / 18 | SSR/SSG, App Router, hỗ trợ 3 role portal |
| CSDL quan hệ | PostgreSQL | 15 | ACID, TypeORM migrations, UUID PK |
| CSDL tài liệu | MongoDB | 7.0 | Lưu tin nhắn, thông báo (schema linh hoạt) |
| Cache / Session | Redis | 7.2 | TTL ngắn (QR 5s, JWT refresh 7d), rate limiting |
| Message Broker | Apache Kafka | — | Async event streaming cho thông báo |
| Real-time | Socket.IO | 4.7 | WebSocket cho điểm danh, thông báo live |
| Hạ tầng | Docker Compose | — | Đóng gói 8 service, dễ deploy |
| Bảo mật | bcrypt + JWT + Passport | — | Mã hóa mật khẩu, stateless auth |
| Kiểm thử | Jest | 29.7 | Unit test + E2E test |

---

## CHƯƠNG 3: PHÂN TÍCH THIẾT KẾ VÀ THỰC NGHIỆM HỆ THỐNG

*[Pha III + IV — CHIA VIỆC CHO 3 NGƯỜI]*

### 3.1. Phân tích thiết kế hệ thống (UML)

*[Chia theo module — mỗi người viết đầy đủ biểu đồ cho module của mình]*

#### 3.1.0. Biểu đồ Use Case tổng quan (UC01–UC13)

- 3 Actor: Sinh viên, Giảng viên, Quản trị viên
- 3 Package: Module 1 (UC01–UC04), Module 2 (UC05–UC08), Module 3 (UC09–UC13)
- Ánh xạ: `report/md/requirements_he_thong.md` → Mục 3.3

#### 3.1.1. Module 1 — Xác thực & Quản lý người dùng *(Phạm Thị Thiên Hà)*

> Ánh xạ: `report/md/module1_auth_users.md`

| Mục | Nội dung |
|-----|----------|
| UC Diagram | UC01–UC04 với <<include>> / <<extend>> |
| Kịch bản UC01 | Đăng nhập (13 bước + 3 ngoại lệ: sai MK, tài khoản khóa, token hết hạn) |
| Kịch bản UC03 | Quản lý người dùng (11 bước + 2 ngoại lệ: email trùng, CSV lỗi) |
| BCE Class Diagram | Boundary: LoginPage, UserManagementPage, UserEditModal, UserCreateForm, NotificationComposePage |
| Sequence UC01 | Login → xác thực JWT → lưu token Redis |
| Sequence UC03 | Tìm kiếm → mở modal → cập nhật User |
| Entity | User (role: STUDENT\|LECTURER\|ADMIN, bcrypt password), Notification |

#### 3.1.2. Module 2 — Quản lý học tập *(Nguyễn Bá Hùng)*

> Ánh xạ: `report/md/module2_academic.md`

| Mục | Nội dung |
|-----|----------|
| UC Diagram | UC05–UC08 với <<include>> kiểm tra xung đột lịch |
| Kịch bản UC07 | Đăng ký môn học (11 bước + 3 ngoại lệ: lớp đầy, trùng lịch, đã đăng ký) |
| Kịch bản UC05 | Tạo lớp học (9 bước + 2 ngoại lệ: xung đột phòng/GV) |
| BCE Class Diagram | Boundary: ClassManagementPage, ClassCreateForm, ScheduleManagementPage, CourseRegistrationPage, SchedulePage |
| Sequence UC07 | Đăng ký → kiểm tra chỗ & lịch → tạo Enrollment |
| Sequence UC05 | Tạo lớp → nhập GPS coordinates → lưu Class |
| Entity | Course, Class (latitude/longitude GPS), Schedule, Enrollment |

#### 3.1.3. Module 3 — Điểm danh & Học vụ *(Trần Xuân Thành)*

> Ánh xạ: `report/md/module3_attendance_grades.md`

| Mục | Nội dung |
|-----|----------|
| UC Diagram | UC09–UC13 với <<include>> xác minh QR (HMAC) + GPS (Haversine ≤50m) |
| Kịch bản UC10 | Điểm danh QR+GPS (14 bước + 3 ngoại lệ: QR hết hạn, ngoài 50m, đã điểm danh) |
| Kịch bản UC12 | Nhập điểm (13 bước + 2 ngoại lệ: điểm ngoài 0–10, chưa publish) |
| BCE Class Diagram | Boundary: AttendanceManagementPage, AttendancePage, GradeManagementPage, GradesPage |
| Sequence UC10 | Quét QR → xác minh HMAC → Haversine → tạo Attendance + Socket.IO notify |
| Sequence UC12 | Nhập điểm → tính 0.2QT+0.3GK+0.5CK → publish Grade |
| Entity | Attendance (qrToken, gpsLat, gpsLon, distance, status), Grade (type, weight, score) |

### 3.2. Thiết kế cơ sở dữ liệu

*[Chia theo module]*

**PostgreSQL (TypeORM):**

| Bảng | Module | Cột chính |
|------|--------|-----------|
| `users` | 1 | id(UUID), email, password(bcrypt), fullName, role, isActive |
| `courses` | 2 | id, code, name, credits |
| `classes` | 2 | id, code, courseId, lecturerId, semester, room, maxStudents, **latitude, longitude** |
| `schedules` | 2 | id, classId, dayOfWeek(0–6), startTime, endTime, room |
| `enrollments` | 2 | id, studentId, classId, status(ENROLLED\|DROPPED\|COMPLETED) |
| `attendances` | 3 | id, scheduleId, studentId, sessionDate, status, qrToken, gpsLat, gpsLon, distance |
| `grades` | 3 | id, enrollmentId, type(MIDTERM\|FINAL\|ASSIGNMENT\|QUIZ), name, score, weight |

**MongoDB (Mongoose):**

| Collection | Module | Dùng cho |
|------------|--------|----------|
| `notifications` | 1 | Thông báo hệ thống: title, body, type, isRead, userId |

### 3.3. Thực nghiệm

*[Chia theo module — mỗi người test module của mình]*

#### 3.3.1. Dữ liệu thực nghiệm

- 3 tài khoản mẫu: 1 Admin, 1 Giảng viên (GV001), 1 Sinh viên (B23DCAT120)
- 2 lớp học: INT1340.01, INT1340.02 với tọa độ GPS thực (21.003451, 105.843670)
- 5 buổi điểm danh mẫu với kết quả PRESENT / ABSENT / LATE

#### 3.3.2. Phương pháp thực nghiệm

- **Black-box testing** theo UC: kiểm tra Input → Expected Output → Actual Output.
- Tiêu chí: PASS nếu output khớp expected; FAIL nếu lệch.

| Module | UC kiểm thử | Số test case |
|--------|------------|-------------|
| Module 1 | UC01, UC03 | 8 TC |
| Module 2 | UC07, UC05 | 8 TC |
| Module 3 | UC10, UC12 | 8 TC |

#### 3.3.3. Kết quả thực nghiệm

- Screenshot demo từng chức năng chính (đăng nhập, quét QR, nhập điểm).
- Bảng kết quả Pass/Fail theo từng test case.
- Nhận xét: các chức năng core đạt PASS; hạn chế ghi rõ (nếu có).

---

## PHÂN CÔNG CÔNG VIỆC

| Thành viên | Module phụ trách | Chương 3 mục | Chương 3.3 |
|------------|-----------------|-------------|------------|
| **Phạm Thị Thiên Hà** | Module 1: Auth & Users | 1.3 (wireframe M1), 3.1.1, 3.2 (users) | 3.3 Module 1 |
| **Nguyễn Bá Hùng** | Module 2: Academic | 1.3 (wireframe M2), 2.1 (arch), 3.1.2, 3.2 (academic) | 3.3 Module 2 |
| **Trần Xuân Thành** | Module 3: Attendance & Grades | 1.3 (wireframe M3), 3.1.3, 3.2 (attendance+grades) | 3.3 Module 3 |

---

## QUY TẮC THỐNG NHẤT NỘI DUNG (BẮT BUỘC cả 3 người tuân thủ)

### 1. Tên lớp phải khớp codebase

| Tầng | Codebase (thực tế) | Dùng trong tài liệu |
|------|-------------------|-------------------|
| Entity | `user.entity.ts` → class `User` | `User` |
| Entity | `class.entity.ts` → class `Class` | `Class` (lớp học) |
| Entity | `attendance.entity.ts` → class `Attendance` | `Attendance` |
| Entity | `grade.entity.ts` → class `Grade` | `Grade` |
| Entity | `enrollment.entity.ts` → class `Enrollment` | `Enrollment` |
| Entity | `schedule.entity.ts` → class `Schedule` | `Schedule` |
| Entity | `course.entity.ts` → class `Course` | `Course` |
| Boundary (React) | `page.tsx` files | `[Name]Page`, `[Name]Form`, `[Name]Modal` |
| DAO | Service layer: `users.service.ts` | `UserDAO`, `CourseDAO`... |

### 2. Ngôn ngữ theo pha

- **Pha II (Phân tích — Chương 3.1):** Sequence diagram dùng tiếng Việt tự nhiên + tên hàm đơn giản
- **Pha III (Thiết kế — Chương 3.2):** Tên hàm TypeScript đầy đủ + kiểu dữ liệu (`findById(id: string): Promise<User>`)

### 3. Công nghệ giao diện: HTML (React/Next.js)

- Boundary class → React Component (`<<Component>>`)
- Event handler: `handleSubmit`, `onClick`, `onChange` (không dùng ActionListener)
- Không dùng JFrame, JButton, JTextField

### 4. PlantUML bắt buộc

```
left to right direction
skinparam linetype ortho
skinparam packageStyle rectangle
```

### 5. Mã UC xuyên suốt

- UC01–UC04: Module 1 | UC05–UC08: Module 2 | UC09–UC13: Module 3
- Giữ nguyên mã UC từ chương 1 sang chương 3
- **UC14 (Nhắn tin) đã bỏ khỏi phạm vi báo cáo**

### 6. Nguồn markdown

Mỗi module có file markdown riêng trong `report/md/`:
- `report/md/module1_auth_users.md`
- `report/md/module2_academic.md`
- `report/md/module3_attendance_grades.md`
