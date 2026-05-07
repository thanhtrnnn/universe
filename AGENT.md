# UNIVERSE - HỆ SINH THÁI ĐẠI HỌC SỐ THÔNG MINH
**File Context & System Instructions for AI Coding Assistants**

## 1. TỔNG QUAN DỰ ÁN VÀ PHẠM VI HỆ THỐNG (PROJECT SCOPE & USE CASES)
Dự án UniVerse là nền tảng quản lý giáo dục đại học All-in-One. Hệ thống được giới hạn trong các Use Case (UC) cốt lõi dành cho 3 Actor chính:

### 1.1. Actor: Admin (Quản trị viên - Web Portal)
- **UC Quản lý người dùng:** Thêm/sửa/xóa và phân quyền tài khoản Giảng viên, Sinh viên.
- **UC Quản lý danh mục đào tạo:** Quản lý Khoa, Ngành, Môn học, Lớp hành chính, Lớp học phần.
- **UC Quản lý thời khóa biểu:** Phân bổ lịch học, phòng học, giảng viên phụ trách.
- **UC Báo cáo thống kê:** Xuất dữ liệu điểm danh, điểm số, và hoạt động toàn hệ thống.

### 1.2. Actor: Lecturer (Giảng viên - Web Portal & Mobile App)
- **UC Xem lịch giảng dạy:** Theo dõi thời khóa biểu, ca dạy trong tuần/tháng.
- **UC Quản lý điểm danh (Smart Attendance):**
  - Mở phiên điểm danh (Tự động sinh Dynamic QR Code kết hợp giới hạn tọa độ GPS).
  - Đóng phiên điểm danh / Chỉnh sửa trạng thái điểm danh thủ công.
- **UC Quản lý điểm số:** Nhập, cập nhật và chốt điểm thành phần cho sinh viên.
- **UC Truyền thông:** Gửi thông báo khẩn cấp đến lớp học phần, chat 1-1 với sinh viên.

### 1.3. Actor: Student (Sinh viên - Mobile App)
- **UC Xem thông tin cá nhân & Lịch học:** Theo dõi thời khóa biểu và phòng học.
- **UC Điểm danh:** Quét QR Code do giảng viên cung cấp (hệ thống tự động lấy tọa độ GPS để xác thực khoảng cách).
- **UC Tra cứu học tập:** Xem lịch sử điểm danh, tra cứu điểm số các học kỳ.
- **UC Tương tác:** Nhận thông báo (Real-time/Push Notification), chat với giảng viên hoặc bộ phận hỗ trợ.

## 2. QUY CHUẨN CÔNG NGHỆ (TECH STACK)
- **Frontend - Mobile App:** `React Native` (Hỗ trợ iOS/Android).
- **Frontend - Web Portal:** `Next.js` (SSR/SSG) kết hợp `TailwindCSS`.
- **Backend:** `NestJS` (Node.js framework), áp dụng kiến trúc Module hóa. Toàn bộ code dùng `TypeScript`.
- **Cơ sở dữ liệu (Multi-Database):** `PostgreSQL` (Relational/Core), `MongoDB` (NoSQL/Document), `Redis` (Cache/Session).
- **Event Streaming & Realtime:** `Apache Kafka` (Message Broker) và `Socket.IO` / `WebSocket`.

## 3. PHÂN ĐỊNH RANH GIỚI DỮ LIỆU (DATABASE BOUNDARIES)
Hệ thống sử dụng Multi-Database, tuyệt đối không nhầm lẫn vị trí lưu trữ:

### 3.1. PostgreSQL (Relational Data)
Dùng cho dữ liệu có cấu trúc, yêu cầu ACID. Truy vấn bằng ORM TypeORM.
- **Bảng Master:** `Users`, `Roles`, `Courses`, `Classes`, `Rooms`.
- **Bảng Transaction:** `Schedules` (Lịch học), `Attendances` (Bản ghi điểm danh), `Grades` (Điểm số), `Enrollments`.

### 3.2. MongoDB (Document Data)
Dùng cho dữ liệu phi cấu trúc, lưu lượng lớn.
- **Collections:** `messages` (Chat), `notifications` (Thông báo realtime), `activity_logs` (Lịch sử thao tác hệ thống).

### 3.3. Redis (Caching & In-memory)
- Quản lý phiên (Session JWT, Refresh token).
- Lưu trữ cấu hình tạm thời của phiên điểm danh (QR Token tự động hủy sau mỗi 5s).
- Cache dữ liệu thời khóa biểu để giảm tải cho PostgreSQL.

## 4. THUẬT TOÁN & LƯU ĐỒ NGHIỆP VỤ LÕI (CORE BUSINESS LOGIC)

### 4.1. Thuật toán Điểm danh Thông minh (Smart Attendance)
Bắt buộc thỏa mãn đồng thời 2 lớp xác thực:
1. **Dynamic QR Code (Lớp 1):** Server tạo token chứa `session_id` và `timestamp`, thay đổi mỗi 5 giây, ký `HMAC-SHA256` bằng secret key.
2. **GPS Geo-fencing (Lớp 2):** Khi sinh viên quét QR, App gửi tọa độ GPS lên server. Server dùng **công thức Haversine** để tính khoảng cách giữa Sinh viên và Phòng học. Ngưỡng hợp lệ: `≤ 50 mét`.

### 4.2. Kiến trúc Luồng Thông báo (Kafka + Socket.IO)
1. Giảng viên tạo thông báo qua API $\rightarrow$ Lưu MongoDB.
2. Publish event vào topic `class-notifications` của Kafka.
3. Kafka Consumer xử lý $\rightarrow$ Push qua `Socket.IO` (user online) hoặc `Firebase FCM` (user offline).

## 5. QUY TẮC DÀNH CHO AI (AI CODING DIRECTIVES)
- **Always TypeScript:** Mọi logic code phải có Type/Interface rõ ràng.
- **Kiến trúc NestJS:** Tuân thủ luồng `Controller` $\rightarrow$ `Service` $\rightarrow$ `Repository`. Bắt buộc dùng DTO để validate dữ liệu đầu vào.
- **Toán học thực tế:** Không dùng code giả (mock) cho tính toán khoảng cách GPS. Hãy implement chuẩn thuật toán Haversine.
- **Bảo mật:** Mật khẩu hash bằng `bcrypt`. API bảo vệ bằng `JwtAuthGuard` và `RolesGuard` tương ứng với các Actor trong Use Case. Xử lý lỗi HTTP minh bạch, trả về mã lỗi rõ ràng (VD: Lỗi quá khoảng cách điểm danh, lỗi mã QR hết hạn).