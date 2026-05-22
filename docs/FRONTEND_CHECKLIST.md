# Frontend Implementation Checklist

Checklist toàn diện cho `fe/web` (Next.js) và `fe/app` (React Native), đối chiếu chi tiết với báo cáo TTCS — Chương 2 (yêu cầu chức năng) và Chương 4.4 (thiết kế giao diện).

---

## 1. Core Setup (Cả hai nhánh)

### 1.1. TypeScript & Types
- [ ] `tsconfig.json` strict mode.
- [ ] Đồng bộ từ Backend: `User`, `Role`, `Course`, `Class`, `Room`, `Schedule`, `Attendance`, `Grade`, `Enrollment`, `Message`, `Notification`, `ActivityLog`, `AbsenceRequest`.

### 1.2. API Client (Axios)
- [ ] `baseURL` từ `.env`.
- [ ] Interceptor: auto-attach JWT Access Token vào header `Authorization`.
- [ ] Auto-refresh: gọi Refresh Token API khi gặp lỗi 401.
- [ ] Logout tự động khi Refresh Token cũng hết hạn.

### 1.3. Global State
- [ ] Zustand / Redux Toolkit.
- [ ] Stores: `authStore`, `userStore`, `scheduleStore`, `notificationStore`, `attendanceStore`.

### 1.4. Real-time (Socket.IO)
- [ ] Connection quản lý (connected/disconnected/reconnecting).
- [ ] Lắng nghe `NotificationsGateway` (thông báo mới, tin nhắn mới).
- [ ] Lắng nghe `AttendanceGateway` (cập nhật điểm danh real-time cho giảng viên).

### 1.5. Bảo mật & Phân quyền
- [ ] Access Token: memory/localStorage. Refresh Token: HttpOnly cookie (web) / SecureStorage (mobile).
- [ ] RBAC frontend: ẩn/hiện UI theo role `student` / `lecturer` / `admin`.
- [ ] Route guards: redirect nếu không đúng role.

### 1.6. Đa ngôn ngữ & Theme
- [ ] Hỗ trợ tiếng Việt (mặc định) và tiếng Anh.
- [ ] Dark mode / Light mode toggle.

---

## 2. Nhánh `fe/web` — Next.js Web Portal

### 2.1. Cấu hình
- [ ] App Router (`src/app/`) với route groups: `(auth)`, `(admin)`, `(lecturer)`.
- [ ] UI library: Shadcn UI / Ant Design + TailwindCSS.
- [ ] Layout phân quyền: `AdminLayout`, `LecturerLayout`.

### 2.2. Auth Pages
- [ ] **Đăng nhập:** Form → gọi API → nhận Access + Refresh Token → redirect theo role.
- [ ] **Quên mật khẩu:** Flow reset password.
- [ ] JWT flow: Client gửi login → Server trả token → Client đính kèm vào mọi request sau đó.

### 2.3. Phân hệ Admin — Quản lý người dùng
- [ ] Danh sách người dùng: bảng phân trang, tìm kiếm, lọc theo vai trò.
- [ ] CRUD: tạo mới, chỉnh sửa, vô hiệu hóa tài khoản Sinh viên / Giảng viên.
- [ ] **Bulk import:** Upload file danh sách hàng loạt.
- [ ] **RBAC:** Gán vai trò (Student/Lecturer/Admin) → hệ thống tự động áp dụng quyền.
- [ ] **Audit log:** Mọi thay đổi được ghi lại trong nhật ký hệ thống.

### 2.4. Phân hệ Admin — Quản lý đào tạo
- [ ] **Khoa / Ngành:** CRUD danh mục.
- [ ] **Môn học (Courses):** CRUD, gắn với Ngành.
- [ ] **Lớp học phần (Classes):** Mở lớp cho từng học kỳ, gán giảng viên phụ trách.
- [ ] **Phòng học (Rooms):** CRUD + **tọa độ GPS** (latitude, longitude) — bắt buộc cho Geo-fencing.

### 2.5. Phân hệ Admin — Thời khóa biểu
- [ ] Xếp lịch: chọn lớp + phòng + giảng viên + khung giờ.
- [ ] **Kiểm tra tự động xung đột** lịch phòng và lịch giảng viên.
- [ ] Hiển thị theo tuần/tháng.

### 2.6. Phân hệ Admin — Dashboard & Thống kê
- [ ] Biểu đồ tỷ lệ chuyên cần: theo lớp, theo khoa, toàn trường.
- [ ] Bộ lọc: học kỳ, khoảng thời gian, khoa.
- [ ] **Xuất báo cáo:** Excel / PDF.

### 2.7. Phân hệ Admin — Thông báo toàn hệ thống
- [ ] Soạn nội dung, chọn đối tượng nhận (toàn bộ SV, toàn bộ GV, hoặc một khoa).
- [ ] Gửi → hệ thống phân phối theo danh sách đối tượng.

### 2.8. Phân hệ Giảng viên — Điểm danh
- [ ] Chọn lớp học đang diễn ra → nhấn **Mở điểm danh**.
- [ ] Hiển thị **QR Code động** (token HMAC-SHA256, tự động refresh mỗi 5s qua WebSocket).
- [ ] **Theo dõi real-time:** danh sách SV cập nhật tức thì khi từng SV quét thành công.
- [ ] Thấy rõ: ai đã có mặt, ai trễ, ai chưa điểm danh.
- [ ] **Đóng phiên** điểm danh.
- [ ] **Chỉnh sửa thủ công:** điều chỉnh trạng thái từng SV + ghi lý do.

### 2.9. Phân hệ Giảng viên — Điểm số
- [ ] Chọn lớp học phần + loại điểm (quá trình / giữa kỳ / cuối kỳ).
- [ ] Bảng nhập điểm cho từng sinh viên (editable).
- [ ] **Tự động tính điểm tổng kết** theo công thức cấu hình.
- [ ] **Công bố điểm** → sinh viên thấy trên app. Trạng thái "Chưa có" khi chưa công bố.

### 2.10. Phân hệ Giảng viên — Lịch dạy & Danh sách lớp
- [ ] Lịch dạy theo tuần/tháng.
- [ ] Chọn lớp → xem danh sách SV, thông tin phòng học, ghi chú.

### 2.11. Phân hệ Giảng viên — Phê duyệt vắng phép
- [ ] Nhận thông báo khi SV gửi đơn xin nghỉ phép + lý do.
- [ ] Xem nội dung đơn → **Chấp nhận** / **Từ chối**.
- [ ] Kết quả: trạng thái điểm danh buổi đó cập nhật thành "Vắng có phép" (nếu chấp nhận).

### 2.12. Phân hệ Giảng viên — Thông báo & Chat
- [ ] Soạn thông báo → chọn lớp → gửi ngay (Kafka + Socket.IO/FCM).
- [ ] Chat 1-1 với sinh viên: real-time khi online, lưu trữ khi offline.

---

## 3. Nhánh `fe/app` — React Native Mobile App

### 3.1. Cấu hình
- [ ] React Navigation: `Stack` (Auth) + `Bottom Tab` (Dashboard, Lịch học, Điểm danh, Chat, Cá nhân).
- [ ] UI: React Native Paper / NativeWind. **Material Design** style.
- [ ] Dark mode support.

### 3.2. Hardware Permissions
- [ ] **Camera:** iOS `Info.plist`, Android `AndroidManifest.xml`.
- [ ] **GPS/Location:** Foreground + Background.
- [ ] Xử lý khi bị từ chối quyền → hướng dẫn người dùng bật lại.

### 3.3. Auth
- [ ] Login form → gọi API → lưu JWT → auto-login khi token còn hạn.

### 3.4. Phân hệ Sinh viên — Dashboard & Lịch học
- [ ] Dashboard: buổi học sắp diễn ra trong ngày.
- [ ] Lịch học: tuần/tháng. Chi tiết: tên môn, phòng, giảng viên, giờ.
- [ ] **Lịch thi** (nếu có data).

### 3.5. Phân hệ Sinh viên — Điểm danh thông minh
Theo đúng 5 bước từ báo cáo (Chức năng 1, mục 2.2.1):
- [ ] **Bước 1:** Màn hình chính → chọn "Điểm danh".
- [ ] **Bước 2:** Yêu cầu quyền Camera + Location nếu chưa cấp.
- [ ] **Bước 3:** Quét QR (dùng `react-native-qrcode-scanner`). Hướng camera vào QR trên màn hình GV/máy chiếu.
- [ ] **Bước 4:** Gửi token QR + tọa độ GPS lên server cùng lúc.
- [ ] **Bước 5:** Hiển thị kết quả:
  - Thành công: "Điểm danh thành công" + thời gian ghi nhận.
  - Thất bại: lý do rõ ràng ("Mã QR hết hạn" / "Ngoài phạm vi phòng học").
- [ ] **Lịch sử điểm danh** theo từng môn học.

### 3.6. Phân hệ Sinh viên — Điểm số
Theo 4 bước từ báo cáo (Chức năng 3, mục 2.2.1):
- [ ] Danh sách môn đã đăng ký theo học kỳ (hiện tại + trước).
- [ ] Chi tiết: điểm quá trình, giữa kỳ, cuối kỳ, tổng kết.
- [ ] "Chưa có" khi GV chưa công bố.

### 3.7. Phân hệ Sinh viên — Tiến độ tín chỉ
Theo 3 bước từ báo cáo (Chức năng 4, mục 2.2.1):
- [ ] Tổng tín chỉ đã tích lũy vs. yêu cầu tốt nghiệp.
- [ ] Danh sách môn chưa đăng ký / chưa đạt.
- [ ] Ghi chú **môn tiên quyết**.

### 3.8. Phân hệ Sinh viên — Chat
Theo 3 bước (Chức năng 5, mục 2.2.1):
- [ ] Danh sách tin nhắn → chọn **giảng viên có lớp chung** (không hiển thị GV không liên quan).
- [ ] Gửi/nhận real-time khi online. Nhận phản hồi sau khi offline.

### 3.9. Phân hệ Sinh viên — Thông báo
Theo 3 bước (Chức năng 6, mục 2.2.1):
- [ ] App đang mở → cảnh báo trực tiếp trên màn hình.
- [ ] App ở background/killed → **Push Notification (FCM)**.
- [ ] Nhấn thông báo → mở nội dung chi tiết.
- [ ] Đánh dấu đã đọc. Mục Thông báo → lịch sử toàn bộ.

### 3.10. Phân hệ Giảng viên — Điểm danh (Mobile)
Theo 3 bước (Chức năng 1, mục 2.2.2):
- [ ] Chọn lớp đang diễn ra → nhấn "Mở điểm danh".
- [ ] Hiển thị QR Code động trên màn hình điện thoại (auto-refresh 5s).
- [ ] Giữ phiên mở / chủ động đóng.

### 3.11. Phân hệ Giảng viên — Theo dõi điểm danh (Mobile)
Theo 3 bước (Chức năng 2, mục 2.2.2):
- [ ] Danh sách SV cập nhật real-time khi SV quét thành công.
- [ ] Thấy: đã có mặt / trễ / chưa điểm danh.
- [ ] Sau phiên: danh sách tổng kết + chỉnh sửa thủ công + lý do.

### 3.12. Phân hệ Giảng viên — Thông báo & Chat (Mobile)
- [ ] Gửi thông báo khẩn đến lớp.
- [ ] Chat 1-1 với SV.

### 3.13. Cá nhân (Cả hai role)
- [ ] Xem / chỉnh sửa thông tin cá nhân.
- [ ] Đổi mật khẩu.
- [ ] Đăng xuất.

---

## 4. Kiểm thử (Đối chiếu Chương 5.3)

### 4.1. Test Cases theo báo cáo
- [ ] **TC1:** Điểm danh thành công trong phạm vi 50m.
- [ ] **TC2:** Từ chối khi ở ngoài phạm vi GPS.
- [ ] **TC3:** Từ chối khi QR Code hết hạn (>5s).
- [ ] **TC4:** Xử lý đồng thời nhiều sinh viên điểm danh đầu buổi học.

### 4.2. Functional
- [ ] Login → phân quyền → redirect đúng role.
- [ ] Refresh token hoạt động khi access token hết hạn.
- [ ] QR động thay đổi đúng 5 giây.
- [ ] Thông báo real-time đến đúng người nhận.
- [ ] Chat gửi/nhận real-time.
- [ ] Công bố điểm → SV thấy "Chưa có" → sau khi publish → thấy điểm.
- [ ] Phê duyệt vắng phép → trạng thái điểm danh cập nhật.

### 4.3. Performance
- [ ] API response <200ms với Redis cache.
- [ ] Concurrent attendance: nhiều SV quét cùng lúc.
- [ ] WebSocket tải trọng cho chat/notification.

### 4.4. Edge Cases
- [ ] Mất kết nối internet → thông báo lỗi.
- [ ] GPS không khả dụng trong nhà → fallback message.
- [ ] QR hết hạn ngay khi vừa quét → thông báo rõ lý do.
