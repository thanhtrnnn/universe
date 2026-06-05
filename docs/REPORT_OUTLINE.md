# Báo Cáo Bài Tập Lớn - UniVerse
> Nhóm HTH | Môn: Nhập môn Công nghệ Phần mềm | GVHD: Đỗ Thị Liên
> Công nghệ giao diện: **HTML (React / Next.js)**

---

## CHƯƠNG 1: MỞ ĐẦU
*[Ai viết: cả nhóm thống nhất — đây là phần chung]*

- **1.1. Tổng quan đề tài**
  - Bối cảnh, vấn đề đặt ra
  - Mục tiêu xây dựng hệ thống UniVerse
  - Phạm vi áp dụng
- **1.2. Giới thiệu công nghệ sử dụng**
  - Backend: NestJS 10 + TypeScript, PostgreSQL, MongoDB, Redis, Kafka
  - Frontend Web: Next.js 14 + TailwindCSS + Material Design 3
  - Mobile: React Native (kế hoạch)
  - Hạ tầng: Docker Compose (8 services)

---

## CHƯƠNG 2: PHÂN TÍCH HỆ THỐNG
*[Giai đoạn 1 – Requirements toàn hệ thống + Pha II – Analysis theo từng module]*
*[Ai viết: Claude hoàn thành toàn bộ chương này trước khi chia việc]*

### 2.1. Phân tích bài toán và quy trình nghiệp vụ
> Ánh xạ: `requirements-system.md` → Mục 2.1–2.6 (Mô hình nghiệp vụ ngôn ngữ tự nhiên)
- 2.1.1. Bảng thuật ngữ
- 2.1.2. Mục tiêu và phạm vi hệ thống
- 2.1.3. Các đối tượng nghiệp vụ (Entities chính)
- 2.1.4. Quy trình hoạt động từng chức năng (luồng mũi tên →)
- 2.1.5. Quan hệ giữa các đối tượng

### 2.2. Phân tích yêu cầu chức năng (Theo User Roles)
> Ánh xạ: `requirements-system.md` → Mục 3.1–3.2 (Danh sách Actor + Use Case)
- **Sinh viên (Student):** đăng nhập, điểm danh QR+GPS, xem lịch học, đăng ký môn, xem điểm, nhắn tin
- **Giảng viên (Lecturer):** tạo QR điểm danh, quản lý điểm danh, nhập điểm, xem lịch dạy, nhắn tin
- **Quản trị viên (Admin):** CRUD người dùng, quản lý khóa học/lớp học/phòng học, xếp lịch, gửi thông báo

### 2.3. Phân tích yêu cầu phi chức năng
- Hiệu năng: QR token xoay vòng 5 giây (Redis TTL), Haversine GPS ≤50m
- Bảo mật: JWT + bcrypt, RBAC 3 vai trò
- Khả năng mở rộng: Docker Compose, Kafka event streaming
- Độ tin cậy: MongoDB audit log, conflict detection lịch học

### 2.4. Biểu đồ Use Case tổng quát và chi tiết
> Ánh xạ: `requirements-system.md` → Mục 3.3 (Biểu đồ UC tổng quan + từng module)

#### Biểu đồ UC tổng quan (toàn hệ thống)
- 3 Actor: Sinh viên, Giảng viên, Quản trị viên
- 3 Package module: Module 1 Auth/Users, Module 2 Học tập, Module 3 Điểm danh & Học vụ

#### Module 1 — Xác thực & Quản lý người dùng
> Ánh xạ: `i.1` → `ii.1` → `ii.2` → `ii.3` → `ii.4`
| Mã UC | Actor | Use Case |
|-------|-------|----------|
| UC01 | Sinh viên, Giảng viên, Admin | Đăng nhập |
| UC02 | Sinh viên, Giảng viên, Admin | Đăng xuất |
| UC03 | Admin | Quản lý người dùng (CRUD + bulk import) |
| UC04 | Admin | Gửi thông báo hệ thống |

#### Module 2 — Quản lý học tập (Academic)
> Ánh xạ: `i.1` → `ii.1` → `ii.2` → `ii.3` → `ii.4`
| Mã UC | Actor | Use Case |
|-------|-------|----------|
| UC05 | Admin | Quản lý khóa học & lớp học |
| UC06 | Admin | Quản lý thời khóa biểu |
| UC07 | Sinh viên | Đăng ký môn học |
| UC08 | Sinh viên | Xem lịch học & lịch thi |

#### Module 3 — Điểm danh & Học vụ
> Ánh xạ: `i.1` → `ii.1` → `ii.2` → `ii.3` → `ii.4`
| Mã UC | Actor | Use Case |
|-------|-------|----------|
| UC09 | Giảng viên | Tạo mã QR điểm danh |
| UC10 | Sinh viên | Điểm danh QR + GPS |
| UC11 | Giảng viên | Quản lý & chỉnh sửa điểm danh |
| UC12 | Giảng viên | Nhập & công bố điểm số |
| UC13 | Sinh viên | Xem điểm & tiến độ học tập |
| UC14 | Sinh viên, Giảng viên | Nhắn tin trong lớp |

---

## CHƯƠNG 3: THIẾT KẾ HỆ THỐNG
*[Pha III – Design theo từng module — CHIA VIỆC CHO 3 NGƯỜI]*

### 3.1. Thiết kế kiến trúc tổng thể (System Architecture)
*[Cả nhóm thống nhất — 1 người viết phần này]*
- Kiến trúc 3-tier: Client (Next.js/React Native) → API (NestJS) → DB (PostgreSQL + MongoDB + Redis)
- Luồng dữ liệu: REST API + Socket.IO + Kafka
- Docker Compose orchestration

### 3.2. Thiết kế Cơ sở dữ liệu (Database Design)
> Ánh xạ: `iii.2_thietke_coso_dulieu.md`
*[Chia theo module]*

**Module 1 — Auth/Users:**
- Bảng `users` (id UUID, email, password, fullName, role, isActive)

**Module 2 — Academic:**
- Bảng `courses`, `classes`, `schedules`, `enrollments`

**Module 3 — Attendance/Grades:**
- Bảng `attendances`, `grades`
- Collection MongoDB: `notifications`, `messages`

### 3.3. Thiết kế giải thuật và xử lý logic đặc thù
> Ánh xạ: `iii.1_thietke_lop_thucthe.md` + thuật toán
*[Module 3 phụ trách phần này chủ yếu]*
- Giải thuật Haversine GPS
- Cơ chế QR xoay vòng HMAC-SHA256 (5s TTL Redis)
- Công thức tính điểm: 20% QT + 30% GK + 50% CK

### 3.4. Thiết kế giao diện (UI/UX Design)
> Ánh xạ: `iii.3.1_thietke_giaodien.md` + `iii.3.2_sodo_lop_thietke.md`
*[Chia theo module]*
- Wireframe ASCII cho từng màn hình
- Sơ đồ lớp thiết kế (Boundary/DAO/Entity với React Component)

---

## CHƯƠNG 4: KẾT QUẢ CÀI ĐẶT VÀ THỰC NGHIỆM
*[Pha IV – Test — CHIA VIỆC CHO 3 NGƯỜI]*
> Ánh xạ: `iv_kiemthu.md`

### 4.1. Môi trường cài đặt và công cụ phát triển
- Node.js 20 LTS, Docker Desktop, PostgreSQL 15, MongoDB 7
- IDE: VS Code + NestJS CLI + Next.js dev server
- Testing: Jest 29.7

### 4.2. Kết quả xây dựng ứng dụng (Kèm hình ảnh demo)
- **Module 1:** Screenshot màn hình đăng nhập, quản lý user admin
- **Module 2:** Screenshot quản lý lớp học, thời khóa biểu
- **Module 3:** Screenshot điểm danh QR, bảng điểm

### 4.3. Đánh giá và kiểm thử
- Test case hộp đen (Black-box) theo từng UC
- Kết quả kiểm thử: Pass/Fail

---

## CHƯƠNG 5: KẾT LUẬN
*[Cả nhóm thống nhất — 1 người tổng hợp]*

### 5.1. Các kết quả đạt được
- Backend NestJS ~85% hoàn thiện (7 module + real-time gateway)
- Frontend Web Next.js ~60% hoàn thiện (3 portal: Admin, Lecturer, Student)
- Tích hợp QR + GPS điểm danh thông minh

### 5.2. Hạn chế của hệ thống
- Mobile app chưa hoàn thiện (React Native scaffold)
- Firebase FCM chưa tích hợp
- Frontend chưa kết nối API đầy đủ

### 5.3. Hướng phát triển trong tương lai
- Hoàn thiện mobile app
- AI chatbot hỗ trợ sinh viên (Python Flask + OpenAI đã có sẵn)
- Mở rộng báo cáo thống kê, xuất Excel/PDF

---

## PHÂN CÔNG CÔNG VIỆC (Chương 3–4)

| Thành viên | Module phụ trách | Pha III mục | Pha IV mục |
|------------|-----------------|-------------|------------|
| **Phạm Thị Thiên Hà** (Frontend Web) | Module 1: Auth & Users | 3.2 (users table), 3.4 (wireframe Login, User Mgmt) | 4.2 + 4.3 Module 1 |
| **Nguyễn Bá Hùng** (Backend/DB) | Module 2: Academic | 3.1 (arch), 3.2 (courses/classes/schedules/enrollments), 3.4 (wireframe Academic) | 4.2 + 4.3 Module 2 |
| **Trần Xuân Thành** (Mobile) | Module 3: Attendance & Grades | 3.2 (attendances/grades + MongoDB), 3.3 (algorithms), 3.4 (wireframe QR/Grades) | 4.2 + 4.3 Module 3 |

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
| Boundary (React) | `page.tsx` files | `[Name]Page`, `[Name]Form`, `[Name]Table` |
| DAO | Service layer: `users.service.ts` | `UserDAO`, `CourseDAO`... |

### 2. Ngôn ngữ theo pha
- **Pha II (Phân tích):** Sequence diagram dùng tiếng Việt tự nhiên + tên hàm đơn giản
- **Pha III (Thiết kế):** Tên hàm TypeScript đầy đủ + kiểu dữ liệu (`findById(id: string): Promise<User>`)

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
- UC01–UC04: Module 1 | UC05–UC08: Module 2 | UC09–UC14: Module 3
- Giữ nguyên mã UC từ chương 2 sang chương 3 và 4

### 6. Cấu trúc file output
Mỗi người viết file `.md` riêng cho module của mình:
- `docs/module1_auth_users.md`
- `docs/module2_academic.md`
- `docs/module3_attendance_grades.md`
