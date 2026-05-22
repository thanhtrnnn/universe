# Backend Implementation Checklist

Checklist toàn diện cho nhánh `biden` (NestJS Backend), đối chiếu với báo cáo TTCS — Chương 2 (yêu cầu), Chương 3 (kiến trúc), Chương 4 (thiết kế).

---

## 1. Project Setup & Infrastructure

### 1.1. NestJS Project Structure
- [ ] Module hóa theo feature: `AuthModule`, `UsersModule`, `CoursesModule`, `ClassesModule`, `RoomsModule`, `SchedulesModule`, `AttendancesModule`, `GradesModule`, `EnrollmentsModule`, `NotificationsModule`, `ChatModule`, `StatisticsModule`.
- [ ] Tuân thủ kiến trúc: `Controller` → `Service` → `Repository`.
- [ ] Cấu hình `TypeORM` với PostgreSQL + `Mongoose` với MongoDB.
- [ ] Cấu hình `ioredis` cho Redis.
- [ ] Cấu hình `@nestjs/websockets` + `socket.io` cho real-time.
- [ ] Cấu hình `@nestjs/microservices` + `kafkajs` cho Kafka.

### 1.2. Dockerfiles
- [ ] `apps/server/Dockerfile` (multi-stage build: dev + prod).
- [ ] `apps/server/Dockerfile.dev` (hot-reload cho development).
- [ ] `.dockerignore`.

### 1.3. Environment & Config
- [ ] `.env.example` với tất cả biến môi trường cần thiết.
- [ ] `@nestjs/config` module quản lý biến môi trường.
- [ ] Health check endpoint: `GET /health`.

---

## 2. Database Design (Đối chiếu Chương 4.2)

### 2.1. PostgreSQL — TypeORM Entities (9 đối tượng nghiệp vụ cốt lõi)

**User** (thông tin người dùng)
- [ ] `id` (PK), `email`, `password` (bcrypt hash), `fullName`, `studentId` (MSSV), `phone`, `avatarUrl`, `isActive`, `createdAt`, `updatedAt`.
- [ ] Quan hệ: N-1 với `Role`, N-M với `Class` (qua `Enrollment`).

**Role** (vai trò)
- [ ] `id` (PK), `name` (`student` / `lecturer` / `admin`), `description`.
- [ ] Quan hệ: 1-N với `User`.

**Course** (môn học)
- [ ] `id` (PK), `name`, `code`, `credits`, `department`, `description`.
- [ ] Quan hệ: N-1 với `Department`, 1-N với `Class`.

**Class** (lớp học phần)
- [ ] `id` (PK), `name`, `semester`, `academicYear`, `maxStudents`, `isActive`.
- [ ] Quan hệ: N-1 với `Course`, N-1 với `User` (lecturer), 1-N với `Schedule`, N-M với `User` (students qua `Enrollment`).

**Room** (phòng học)
- [ ] `id` (PK), `name`, `building`, `capacity`, `latitude`, `longitude`.
- [ ] `latitude` + `longitude` bắt buộc — phục vụ GPS Geo-fencing.
- [ ] Quan hệ: 1-N với `Schedule`.

**Schedule** (thời khóa biểu)
- [ ] `id` (PK), `dayOfWeek`, `startTime`, `endTime`, `recurrence`.
- [ ] Quan hệ: N-1 với `Class`, N-1 với `Room`.

**Attendance** (bản ghi điểm danh)
- [ ] `id` (PK), `sessionId`, `status` (`present` / `late` / `absent` / `excused`), `checkInTime`, `qrTokenUsed`, `studentLatitude`, `studentLongitude`, `distance`, `manualOverride`, `overrideReason`.
- [ ] Quan hệ: N-1 với `User` (student), N-1 với `Class`, N-1 với `AttendanceSession`.

**AttendanceSession** (phiên điểm danh — thêm mới)
- [ ] `id` (PK), `classId`, `lecturerId`, `startedAt`, `endedAt`, `status` (`active` / `closed`), `roomLatitude`, `roomLongitude`.
- [ ] Quan hệ: 1-N với `Attendance`.

**Grade** (điểm số)
- [ ] `id` (PK), `midtermScore`, `finalScore`, `processScore`, `totalScore`, `isPublished`.
- [ ] `totalScore` tự động tính theo công thức cấu hình.
- [ ] Quan hệ: N-1 với `User` (student), N-1 với `Class`.

**Enrollment** (bảng trung gian Sinh viên – Lớp)
- [ ] `id` (PK), `enrolledAt`, `status` (`enrolled` / `dropped` / `completed`).
- [ ] Quan hệ: N-1 với `User`, N-1 với `Class`.

**AbsenceRequest** (đơn vắng phép — thêm mới)
- [ ] `id` (PK), `reason`, `status` (`pending` / `approved` / `rejected`), `reviewedAt`, `reviewNote`.
- [ ] Quan hệ: N-1 với `User` (student), N-1 với `AttendanceSession`.

### 2.2. MongoDB — Mongoose Schemas

**messages** (chat)
- [ ] `conversationId`, `senderId`, `content`, `contentType` (`text` / `image` / `file`), `readAt`, `createdAt`.
- [ ] Index: `conversationId` + `createdAt`.

**notifications**
- [ ] `userId`, `title`, `content`, `type` (`attendance` / `grade` / `announcement` / `absence`), `isRead`, `relatedId`, `createdAt`.
- [ ] Index: `userId` + `isRead`.

**activity_logs**
- [ ] `userId`, `action`, `entityType`, `entityId`, `details`, `ipAddress`, `createdAt`.
- [ ] Ghi lại mọi thay đổi quan trọng (audit log).

### 2.3. TypeORM Migrations
- [ ] Cấu hình migration thay vì `synchronize: true`.
- [ ] Tạo migration cho tất cả entities.
- [ ] Seed data script: tài khoản admin mặc định, dữ liệu mẫu.

---

## 3. Authentication & Authorization (Đối chiếu Chương 4.1.2)

### 3.1. Auth Module
- [ ] `POST /auth/login` — Xác thực email + password → trả Access Token + Refresh Token.
- [ ] `POST /auth/register` — Đăng ký tài khoản mới.
- [ ] `POST /auth/refresh` — Refresh Token → Access Token mới.
- [ ] `POST /auth/logout` — Vô hiệu hóa Refresh Token.
- [ ] Password hash bằng `bcrypt`.
- [ ] Access Token: JWT, thời hạn ngắn (15-30 phút).
- [ ] Refresh Token: JWT, thời hạn dài (7 ngày), lưu Redis.

### 3.2. Guards
- [ ] `JwtAuthGuard` — Bảo vệ tất cả API (trừ auth endpoints).
- [ ] `RolesGuard` — Kiểm tra vai trò theo decorator `@Roles('admin')`, `@Roles('lecturer')`, `@Roles('student')`.

### 3.3. RBAC Permission Matrix
| Endpoint | Student | Lecturer | Admin |
|---|---|---|---|
| Quản lý người dùng | - | - | CRUD |
| Quản lý đào tạo | - | - | CRUD |
| Thời khóa biểu | Xem | Xem | CRUD |
| Điểm danh | Quét QR | Tạo/Đóng/Chỉnh sửa | Xem thống kê |
| Điểm số | Xem (khi published) | Nhập/Công bố | Xem thống kê |
| Chat | GV có lớp chung | SV có lớp chung | - |
| Thông báo | Nhận | Gửi đến lớp | Gửi toàn hệ thống |
| Thống kê | - | Lớp mình dạy | Toàn trường |

---

## 4. API Endpoints

### 4.1. Users (`/api/users`)
- [ ] `GET /users` — Danh sách (phân trang, tìm kiếm, lọc role). **Admin only.**
- [ ] `GET /users/:id` — Chi tiết.
- [ ] `POST /users` — Tạo mới. **Admin only.**
- [ ] `PATCH /users/:id` — Cập nhật. **Admin only.**
- [ ] `DELETE /users/:id` — Vô hiệu hóa. **Admin only.**
- [ ] `POST /users/bulk-import` — Upload file danh sách hàng loạt. **Admin only.**
- [ ] `GET /users/me` — Thông tin cá nhân (tất cả role).

### 4.2. Courses (`/api/courses`)
- [ ] CRUD đầy đủ. **Admin only** cho tạo/sửa/xóa.
- [ ] `GET /courses` — Danh sách (phân trang, lọc theo khoa).

### 4.3. Classes (`/api/classes`)
- [ ] CRUD. **Admin only** cho tạo/sửa/xóa.
- [ ] `GET /classes/:id/students` — Danh sách SV trong lớp.
- [ ] `POST /classes/:id/enroll` — Ghi danh SV. **Admin only.**

### 4.4. Rooms (`/api/rooms`)
- [ ] CRUD. **Admin only.**
- [ ] Bắt buộc có `latitude`, `longitude`.

### 4.5. Schedules (`/api/schedules`)
- [ ] CRUD. **Admin only** cho tạo/sửa/xóa.
- [ ] `GET /schedules` — Lịch học theo tuần/tháng (filter theo user/class).
- [ ] **Conflict detection:** Kiểm tra trùng phòng + trùng giảng viên khi tạo.

### 4.6. Attendance (`/api/attendance`)
- [ ] `POST /attendance/sessions` — Tạo phiên điểm danh. **Lecturer only.**
- [ ] `POST /attendance/sessions/:id/close` — Đóng phiên. **Lecturer only.**
- [ ] `POST /attendance/check-in` — SV quét QR + gửi GPS. **Student only.**
  - Body: `{ sessionId, qrToken, latitude, longitude }`
  - Logic: verify QR (HMAC-SHA256) + tính Haversine distance + ghi nhận.
- [ ] `GET /attendance/sessions/:id` — Danh sách điểm danh real-time.
- [ ] `PATCH /attendance/:id` — Chỉnh sửa thủ công + lý do. **Lecturer only.**
- [ ] `GET /attendance/history` — Lịch sử điểm danh theo môn. **Student only.**

### 4.7. Grades (`/api/grades`)
- [ ] `GET /grades?classId=` — Danh sách điểm lớp. **Lecturer/Admin.**
- [ ] `PATCH /grades/:id` — Cập nhật điểm. **Lecturer only.**
- [ ] `POST /grades/publish` — Công bố điểm → SV thấy. **Lecturer only.**
- [ ] `GET /grades/my` — Xem điểm cá nhân (chỉ khi published). **Student only.**
  - Trả về: `processScore`, `midtermScore`, `finalScore`, `totalScore` hoặc `null` nếu chưa publish.

### 4.8. Absence Requests (`/api/absence-requests`)
- [ ] `POST /absence-requests` — SV gửi đơn xin nghỉ + lý do. **Student only.**
- [ ] `GET /absence-requests` — Giảng viên xem danh sách đơn chờ duyệt. **Lecturer only.**
- [ ] `PATCH /absence-requests/:id` — Chấp nhận / Từ chối. **Lecturer only.**
  - Nếu chấp nhận: cập nhật `Attendance.status` thành `excused`.

### 4.9. Notifications (`/api/notifications`)
- [ ] `POST /notifications` — Tạo thông báo. **Lecturer/Admin.**
  - Lecturer: gửi đến lớp.
  - Admin: gửi đến toàn hệ thống / khoa / nhóm.
- [ ] `GET /notifications` — Danh sách thông báo cá nhân. **All roles.**
- [ ] `PATCH /notifications/:id/read` — Đánh dấu đã đọc.

### 4.10. Chat (`/api/chat`)
- [ ] `GET /chat/conversations` — Danh sách hội thoại.
- [ ] `GET /chat/conversations/:id/messages` — Tin nhắn (phân trang).
- [ ] `POST /chat/conversations/:id/messages` — Gửi tin nhắn.
- [ ] SV chỉ thấy GV có lớp chung. GV chỉ thấy SV trong lớp mình dạy.

### 4.11. Statistics (`/api/statistics`)
- [ ] `GET /statistics/attendance?classId=&departmentId=&semester=` — Thống kê chuyên cần.
  - Trả về: tỷ lệ chuyên cần theo lớp / khoa / toàn trường.
- [ ] `GET /statistics/attendance/export?format=excel|pdf` — Xuất báo cáo. **Admin only.**

---

## 5. Core Business Logic (Đối chiếu Chương 4.3)

### 5.1. Smart Attendance — Điểm danh thông minh

**QR Code Generation (4.3.1):**
- [ ] Tạo token ngẫu nhiên mỗi 5 giây.
- [ ] Token chứa: `sessionId` + `timestamp`.
- [ ] Ký bằng `HMAC-SHA256` với secret key server.
- [ ] Push token mới qua WebSocket cho giảng viên render QR.
- [ ] Token hết hạn sau 5s → lưu Redis với TTL.

**GPS Geo-fencing (4.3.1):**
- [ ] Implement **công thức Haversine** (không mock):
  ```
  a = sin²(Δlat/2) + cos(lat1) · cos(lat2) · sin²(Δlon/2)
  c = 2 · atan2(√a, √(1-a))
  d = R · c  (R = 6,371,000m — bán kính Trái Đất)
  ```
- [ ] So sánh khoảng cách SV vs. tọa độ phòng học.
- [ ] Ngưỡng hợp lệ: **≤ 50 mét**.
- [ ] Lưu `distance` vào bản ghi Attendance để audit.

**Check-in Flow:**
- [ ] Nhận `sessionId`, `qrToken`, `latitude`, `longitude` từ SV.
- [ ] Verify QR token: giải mã HMAC-SHA256, kiểm tra chưa hết hạn (Redis TTL).
- [ ] Tính Haversine distance.
- [ ] Nếu QR hợp lệ + distance ≤ 50m → ghi nhận `present`.
- [ ] Nếu distance > 50m → reject: "Ngoài phạm vi phòng học".
- [ ] Nếu QR hết hạn → reject: "Mã QR hết hạn".
- [ ] Mỗi SV chỉ check-in 1 lần / session.

### 5.2. Notification Flow (4.3.2 — Kafka + Socket.IO)
- [ ] Giảng viên tạo thông báo → API lưu MongoDB.
- [ ] Publish event vào Kafka topic `class-notifications`.
- [ ] Kafka Consumer đọc event:
  - User online → push qua Socket.IO.
  - User offline → push qua Firebase FCM.

### 5.3. Grade Calculation
- [ ] Công thức điểm tổng kết configurable (VD: 30% quá trình + 20% giữa kỳ + 50% cuối kỳ).
- [ ] Tự động tính khi giảng viên lưu điểm.
- [ ] `isPublished = false` mặc định → SV thấy "Chưa có".
- [ ] Khi giảng viên công bố → `isPublished = true` → SV thấy điểm.

### 5.4. Schedule Conflict Detection
- [ ] Khi tạo schedule mới: kiểm tra trùng `room` + `dayOfWeek` + `timeRange`.
- [ ] Kiểm tra trùng `lecturer` + `dayOfWeek` + `timeRange`.
- [ ] Trả lỗi rõ ràng nếu có xung đột.

---

## 6. Real-time (Socket.IO Gateways)

### 6.1. AttendanceGateway
- [ ] Namespace: `/attendance`.
- [ ] Event `attendance:join` — GV join room phiên điểm danh.
- [ ] Event `attendance:update` — Broadcast khi SV check-in thành công.
- [ ] Event `attendance:session-closed` — Thông báo đóng phiên.

### 6.2. NotificationsGateway
- [ ] Namespace: `/notifications`.
- [ ] Event `notification:new` — Push thông báo mới đến user.
- [ ] Event `notification:read` — Xác nhận đã đọc.

### 6.3. ChatGateway
- [ ] Namespace: `/chat`.
- [ ] Event `chat:message` — Gửi/nhận tin nhắn real-time.
- [ ] Event `chat:typing` — Đang nhập (optional).
- [ ] Event `chat:online` — Trạng thái online/offline.

---

## 7. Kafka Integration

### 7.1. Topics
- [ ] `class-notifications` — Thông báo đến lớp.
- [ ] `attendance-events` — Sự kiện điểm danh (cho analytics).

### 7.2. Producers
- [ ] NotificationService: publish khi có thông báo mới.
- [ ] AttendanceService: publish khi có check-in (cho real-time stats).

### 7.3. Consumers
- [ ] NotificationConsumer: đọc event → push Socket.IO / FCM.
- [ ] AttendanceConsumer: cập nhật real-time dashboard (optional).

---

## 8. External Integrations

### 8.1. Firebase Cloud Messaging (FCM)
- [ ] Cấu hình Firebase Admin SDK.
- [ ] Gửi push notification khi user offline.
- [ ] Lưu FCM token của mỗi device.
- [ ] Xử lý token expired / invalid.

### 8.2. AI Service (Python)
- [ ] `apps/ai-service/main.py` — Flask endpoint `/ask`.
- [ ] Tích hợp OpenAI API.
- [ ] Backend gọi AI service qua HTTP khi user hỏi chatbot.
- [ ] Training data: câu hỏi thường gặp về lịch thi, đăng ký tín chỉ, điều kiện tiên quyết.

---

## 9. Caching Strategy (Redis)

- [ ] **JWT Refresh Token:** lưu Redis, TTL 7 ngày.
- [ ] **QR Token:** lưu Redis, TTL 5 giây.
- [ ] **Schedule cache:** cache thời khóa biểu, invalidate khi có thay đổi.
- [ ] **User session:** cache thông tin user đã xác thực.
- [ ] **Rate limiting:** đếm request theo IP/user.

---

## 10. Middleware & Cross-cutting

- [ ] **Logging:** Request logging middleware (method, path, status, duration).
- [ ] **Exception Filter:** HTTP exception filter trả lỗi chuẩn `{ statusCode, message, error }`.
- [ ] **Validation Pipe:** `class-validator` + `class-transformer` cho tất cả DTOs.
- [ ] **CORS:** Cấu hình cho web + mobile origins.
- [ ] **Rate Limiting:** `@nestjs/throttler` chống spam/DDoS.
- [ ] **Swagger:** API docs đầy đủ tại `/api`. Cập nhật mô tả, examples.

---

## 11. DTOs (Data Transfer Objects)

Tất cả module bắt buộc có DTO với `class-validator`:

- [ ] `CreateUserDto`, `UpdateUserDto`, `LoginDto`, `BulkImportDto`.
- [ ] `CreateCourseDto`, `UpdateCourseDto`.
- [ ] `CreateClassDto`, `UpdateClassDto`, `EnrollStudentDto`.
- [ ] `CreateRoomDto`, `UpdateRoomDto`.
- [ ] `CreateScheduleDto`, `UpdateScheduleDto`.
- [ ] `CreateAttendanceSessionDto`, `CheckInDto`, `ManualOverrideDto`.
- [ ] `UpdateGradeDto`, `PublishGradeDto`.
- [ ] `CreateAbsenceRequestDto`, `ReviewAbsenceDto`.
- [ ] `CreateNotificationDto`.
- [ ] `SendMessageDto`.
- [ ] `PaginationDto` (dùng chung: `page`, `limit`, `sortBy`, `order`).

---

## 12. Testing

### 12.1. Unit Tests
- [ ] Service logic cho mỗi module.
- [ ] Haversine function với các tọa độ mẫu.
- [ ] QR token generation + verification.
- [ ] Grade calculation formula.

### 12.2. E2E Tests
- [ ] Auth flow: login → refresh → logout.
- [ ] Attendance flow: tạo session → SV check-in → verify result.
- [ ] Grade flow: nhập điểm → công bố → SV xem.
- [ ] Notification flow: tạo thông báo → nhận Socket.IO event.
- [ ] Permission: student không truy cập admin endpoints.

### 12.3. Test Cases theo báo cáo (Chương 5.3.1)
- [ ] TC1: Điểm danh thành công trong phạm vi 50m.
- [ ] TC2: Từ chối khi ở ngoài phạm vi GPS (>50m).
- [ ] TC3: Từ chối khi QR Code hết hạn.
- [ ] TC4: Xử lý đồng thời nhiều SV điểm danh.
- [ ] TC5: API response <200ms (với Redis cache).

---

## 13. Risk Mitigation (Đối chiếu Chương 1.4)

| Rủi ro | Giải pháp kỹ thuật |
|---|---|
| GPS Spoofing | Cơ chế 2 lớp: QR động + GPS. Chỉ GPS hợp lệ mà không có QR hợp lệ → reject |
| QR bị chia sẻ | QR refresh mỗi 5s, ký HMAC-SHA256, dùng 1 lần |
| Tắc nghẽn khi điểm danh đồng thời | Kafka async processing, Redis cache |
| JWT bị đánh cắp | Refresh Token rotation, short-lived Access Token |
| SQL Injection | TypeORM parameterized queries, class-validator |
| DDoS/Spam | Rate limiting (throttler), Kafka buffering |
| Kafka failure | Retry mechanism, fallback direct Socket.IO push |
| GPS không chính xác trong nhà | Ngưỡng 50m đủ rộng cho lỗi GPS 3-5m |
