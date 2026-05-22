# Frontend Implementation Checklist

Checklist toàn diện cho hai nhánh frontend: `fe/web` (Next.js Admin/Lecturer Portal) và `fe/app` (React Native Mobile App), đối chiếu với báo cáo TTCS và các tính năng đã có sẵn ở backend.

---

## 1. Core Setup (Cả hai nhánh)

### 1.1. TypeScript & Types
- [ ] Khởi tạo `tsconfig.json` với strict mode.
- [ ] Đồng bộ Interface/Type từ Backend: `User`, `Role`, `Course`, `Class`, `Schedule`, `Attendance`, `Grade`, `Enrollment`, `Message`, `Notification`, `ActivityLog`.
- [ ] Tạo shared types package hoặc symlink để tránh trùng lặp giữa web và mobile.

### 1.2. API Client (Axios)
- [ ] Thiết lập `baseURL` từ biến môi trường (`.env`).
- [ ] Axios Interceptor tự động chèn JWT Access Token vào header `Authorization`.
- [ ] Xử lý logic tự động gọi Refresh Token API khi gặp lỗi 401 (Access Token hết hạn).
- [ ] Xử lý logout tự động khi Refresh Token cũng hết hạn.

### 1.3. Global State Management
- [ ] Cấu hình Zustand hoặc Redux Toolkit.
- [ ] Tạo store cho: `authStore`, `userStore`, `scheduleStore`, `notificationStore`.

### 1.4. Real-time Client (Socket.IO)
- [ ] Khởi tạo `socket.io-client` connection.
- [ ] Quản lý connection state (connected/disconnected/reconnecting).
- [ ] Lắng nghe events từ `NotificationsGateway` (thông báo mới, tin nhắn mới).
- [ ] Lắng nghe events từ `AttendanceGateway` (cập nhật trạng thái điểm danh real-time).

### 1.5. Bảo mật
- [ ] Lưu Access Token ở memory/localStorage, Refresh Token ở HttpOnly cookie (web) hoặc SecureStorage (mobile).
- [ ] Implement RBAC ở frontend: ẩn/hiện UI theo vai trò (Student/Lecturer/Admin).

---

## 2. Nhánh `fe/web` — Next.js Web Portal

### 2.1. Cấu hình ban đầu
- [ ] Thiết lập App Router (`src/app/`) với route groups: `(auth)`, `(admin)`, `(lecturer)`.
- [ ] Cài đặt thư viện UI: Shadcn UI / Ant Design + TailwindCSS.
- [ ] Cấu hình theme: Light mode / Dark mode (báo cáo có đề cập chế độ nền tối).
- [ ] Thiết lập layout phân quyền: `AdminLayout`, `LecturerLayout`.

### 2.2. Authentication Pages
- [ ] **Đăng nhập:** Form login, gọi API auth, lưu JWT, redirect theo role.
- [ ] **Đăng ký:** Form đăng ký (nếu có).
- [ ] **Quên mật khẩu:** Flow reset password qua email.

### 2.3. Admin — Quản lý người dùng
- [ ] Danh sách người dùng (bảng phân trang, tìm kiếm, lọc theo vai trò).
- [ ] Thêm/sửa/xóa tài khoản Sinh viên, Giảng viên.
- [ ] Phân quyền theo RBAC: gán vai trò cho từng tài khoản.
- [ ] Upload danh sách hàng loạt từ file (bulk import).

### 2.4. Admin — Quản lý đào tạo
- [ ] **Khoa / Ngành:** CRUD danh mục.
- [ ] **Môn học (Courses):** CRUD môn học, gắn với Ngành.
- [ ] **Lớp hành chính / Lớp học phần (Classes):** Mở lớp, gán giảng viên phụ trách.
- [ ] **Phòng học (Rooms):** Quản lý phòng học + tọa độ GPS (phục vụ Geo-fencing).

### 2.5. Admin — Thời khóa biểu (Schedules)
- [ ] Xếp lịch học: chọn lớp, phòng, giảng viên, khung giờ.
- [ ] Kiểm tra tự động xung đột lịch phòng và lịch giảng viên.
- [ ] Hiển thị thời khóa biểu theo tuần/tháng.

### 2.6. Admin — Dashboard & Thống kê
- [ ] Dashboard tổng quan: biểu đồ tỷ lệ chuyên cần theo lớp/khoa/toàn trường.
- [ ] Bộ lọc theo học kỳ, khoảng thời gian, khoa.
- [ ] Xuất báo cáo dạng Excel / PDF.

### 2.7. Giảng viên — Quản lý điểm danh
- [ ] Mở phiên điểm danh cho lớp học đang diễn ra.
- [ ] Hiển thị mã QR động (render từ token HMAC-SHA256, tự động refresh mỗi 5s qua WebSocket).
- [ ] Theo dõi danh sách sinh viên điểm danh real-time (cập nhật tức thì khi SV quét thành công).
- [ ] Đóng phiên điểm danh.
- [ ] Chỉnh sửa trạng thái điểm danh thủ công (điều chỉnh cho từng sinh viên, ghi lý do).

### 2.8. Giảng viên — Quản lý điểm số
- [ ] Chọn lớp học phần, loại điểm (quá trình / giữa kỳ / cuối kỳ).
- [ ] Nhập điểm cho từng sinh viên (bảng editable).
- [ ] Tự động tính điểm tổng kết theo công thức cấu hình.
- [ ] Công bố điểm → sinh viên thấy trên app.

### 2.9. Giảng viên — Lịch dạy & Danh sách lớp
- [ ] Xem lịch dạy theo tuần/tháng.
- [ ] Xem danh sách sinh viên từng lớp, thông tin phòng học.

### 2.10. Giảng viên — Phê duyệt đơn vắng phép
- [ ] Nhận thông báo khi có sinh viên gửi đơn xin nghỉ.
- [ ] Xem nội dung đơn, chọn Chấp nhận / Từ chối.
- [ ] Hệ thống cập nhật trạng thái điểm danh thành "Vắng có phép".

### 2.11. Giảng viên & Admin — Thông báo
- [ ] Soạn và gửi thông báo đến lớp / khoa / toàn hệ thống.
- [ ] Dropdown hiển thị notification real-time.
- [ ] Đánh dấu đã đọc/chưa đọc.

### 2.12. Chat
- [ ] Danh sách hội thoại (giảng viên ↔ sinh viên).
- [ ] Gửi/nhận tin nhắn real-time qua Socket.IO.
- [ ] Hiển thị trạng thái online/offline.

---

## 3. Nhánh `fe/app` — React Native Mobile App

### 3.1. Cấu hình ban đầu
- [ ] Thiết lập React Navigation:
  - [ ] `Stack Navigator` cho Auth (Login, Register).
  - [ ] `Bottom Tab Navigator` cho: Dashboard, Lịch học, Điểm danh, Chat, Cá nhân.
- [ ] Cài đặt thư viện UI: React Native Paper hoặc NativeWind.
- [ ] Cấu hình Material Design style.

### 3.2. Hardware Permissions (Quan trọng)
- [ ] **Camera:** Cấu hình quyền truy cập Camera (iOS `Info.plist`, Android `AndroidManifest.xml`).
- [ ] **GPS/Location:** Cấu hình quyền Vị trí — Foreground và Background Location.
- [ ] Xử lý trường hợp người dùng từ chối cấp quyền → hiển thị hướng dẫn.

### 3.3. Auth Screens
- [ ] Màn hình Đăng nhập: form + gọi API + lưu JWT.
- [ ] Màn hình Đăng ký (nếu cần).
- [ ] Auto-login khi token còn hiệu lực.

### 3.4. Dashboard / Lịch học (Student)
- [ ] Hiển thị buổi học sắp diễn ra trong ngày.
- [ ] Xem lịch học theo tuần/tháng.
- [ ] Chi tiết buổi học: tên môn, phòng, giảng viên, giờ bắt đầu/kết thúc.

### 3.5. Điểm danh thông minh (Smart Attendance)

#### Sinh viên (Student)
- [ ] Màn hình quét QR sử dụng `react-native-qrcode-scanner`.
- [ ] Lấy tọa độ GPS bằng `react-native-geolocation-service`.
- [ ] Gửi token QR + tọa độ GPS lên backend cùng lúc.
- [ ] Hiển thị kết quả: thành công / thất bại (mã hết hạn / ngoài phạm vi).
- [ ] Xem lịch sử điểm danh theo từng môn học.

#### Giảng viên (Lecturer — Mobile)
- [ ] Tạo QR Code động (hiển thị trên màn hình điện thoại).
- [ ] QR tự động refresh mỗi 5 giây (payload HMAC-SHA256).
- [ ] Theo dõi danh sách điểm danh real-time trên mobile.

### 3.6. Điểm số (Student)
- [ ] Danh sách môn học đã đăng ký theo học kỳ.
- [ ] Chi tiết điểm: quá trình, giữa kỳ, cuối kỳ, tổng kết.
- [ ] Hiển thị "Chưa có" khi giảng viên chưa công bố.

### 3.7. Tiến độ tín chỉ (Student)
- [ ] Tổng tín chỉ đã tích lũy vs. yêu cầu tốt nghiệp.
- [ ] Danh sách môn chưa đăng ký / chưa đạt.
- [ ] Ghi chú môn tiên quyết.

### 3.8. Chat
- [ ] Danh sách hội thoại (giảng viên có lớp chung).
- [ ] Gửi/nhận tin nhắn real-time.
- [ ] Hiển thị trạng thái online/offline.

### 3.9. Thông báo & Push Notification
- [ ] Lắng nghe thông báo real-time khi app đang mở (Socket.IO).
- [ ] Nhận Push Notification khi app ở background/killed (Firebase FCM).
- [ ] Nhấn vào thông báo → mở đúng nội dung / màn hình liên quan.
- [ ] Mục Thông báo: danh sách + đánh dấu đã đọc.

### 3.10. Cá nhân (Profile)
- [ ] Xem thông tin cá nhân.
- [ ] Chỉnh sửa thông tin cơ bản.
- [ ] Đổi mật khẩu.
- [ ] Đăng xuất.

---

## 4. Kiểm thử & Tối ưu

### 4.1. Functional Testing
- [ ] Luồng đăng nhập → phân quyền → redirect đúng role.
- [ ] Refresh token hoạt động đúng khi access token hết hạn.
- [ ] Điểm danh thành công trong phạm vi 50m.
- [ ] Từ chối điểm danh khi ở ngoài phạm vi GPS.
- [ ] Từ chối điểm danh khi QR Code hết hạn (sau 5 giây).
- [ ] QR Code động thay đổi đúng chu kỳ 5 giây.
- [ ] Thông báo real-time đến đúng người nhận.
- [ ] Chat gửi/nhận real-time giữa 2 user.

### 4.2. Performance & Edge Cases
- [ ] Xử lý đồng thời nhiều sinh viên điểm danh đầu buổi học (Kafka + Socket.IO).
- [ ] Kiểm tra tải trọng WebSocket cho chat/notification.
- [ ] Offline handling: hiển thị thông báo khi mất kết nối.
- [ ] GPS không khả dụng trong nhà → fallback message.

### 4.3. UI/UX
- [ ] Responsive trên các kích thước màn hình phổ biến.
- [ ] Dark mode hoạt động đúng.
- [ ] Loading states và error states cho mọi API call.
