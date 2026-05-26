# Frontend Implementation Checklist

Checklist toàn diện cho hai nhánh frontend: `fe/web` (Next.js Admin/Lecturer Portal) và `fe/app` (React Native Mobile App), đối chiếu với báo cáo TTCS và các tính năng đã có sẵn ở backend.

> **Trạng thái trên `fe/web`:** Hai portal `admin-web/` và ` lecturer-web/` đã có UI hoàn chỉnh với mock data. Chưa có API integration, auth, state management, real-time.

---

## 1. Core Setup (Cả hai nhánh)

### 1.1. TypeScript & Types
- [x] Khởi tạo `tsconfig.json` với strict mode.
- [x] Đồng bộ Interface/Type từ Backend: `User`, `Role`, `Course`, `Class`, `Schedule`, `Attendance`, `Grade`, `Enrollment`, `Message`, `Notification`, `ActivityLog`.
- [ ] Tạo shared types package hoặc symlink để tránh trùng lặp giữa web và mobile.

### 1.2. API Client (Axios)
- [x] Thiết lập `baseURL` từ biến môi trường (`.env`).
- [x] Axios Interceptor tự động chèn JWT Access Token vào header `Authorization`.
- [x] Xử lý logic tự động gọi Refresh Token API khi gặp lỗi 401 (Access Token hết hạn).
- [x] Xử lý logout tự động khi Refresh Token cũng hết hạn.

### 1.3. Global State Management
- [x] Cấu hình Zustand hoặc Redux Toolkit.
- [x] Tạo store cho: `authStore`, `userStore`, `scheduleStore`, `notificationStore`.

### 1.4. Real-time Client (Socket.IO)
- [x] Khởi tạo `socket.io-client` connection.
- [x] Quản lý connection state (connected/disconnected/reconnecting).
- [ ] Lắng nghe events từ `NotificationsGateway` (thông báo mới, tin nhắn mới).
- [ ] Lắng nghe events từ `AttendanceGateway` (cập nhật trạng thái điểm danh real-time).

### 1.5. Bảo mật
- [x] Lưu Access Token ở memory/localStorage, Refresh Token ở HttpOnly cookie (web) hoặc SecureStorage (mobile).
- [x] Implement RBAC ở frontend: ẩn/hiện UI theo vai trò (Student/Lecturer/Admin).

---

## 2. Nhánh `fe/web` — Next.js Web Portal

### 2.1. Cấu hình ban đầu
- [x] Thiết lập App Router (`src/app/`) với route groups: `(dashboard)` — cả admin-web và lecturer-web.
- [x] Cài đặt thư viện UI: TailwindCSS v4 + Material Design 3 custom CSS tokens (globals.css ~300 dòng, light + dark).
- [x] Cấu hình theme: Light mode / Dark mode — ThemeProvider với localStorage, FOUC prevention, toggle ở header + settings.
- [x] Thiết lập layout phân quyền: `AdminLayout` (sidebar 6 items), `LecturerLayout` (sidebar 8 items).

### 2.2. Authentication Pages
- [x] **Đăng nhập:** Form login UI tồn tại (`admin-web/login/page.tsx`) — **đã có API call, đã có JWT**.
- [x] **Đăng nhập Lecturer:** Form login mới tạo cho `lecturer-web/src/app/login/page.tsx`.
- [x] **Route Protection:** `middleware.ts` cho cả admin-web và lecturer-web.
- [x] **Đăng ký:** Bỏ qua (Tài khoản Admin và Giảng viên được cấp phát bởi hệ thống, không tự đăng ký).
- [x] **Quên mật khẩu:** Flow reset password qua email.

### 2.3. Admin — Quản lý người dùng
- [x] Danh sách người dùng (bảng phân trang, tìm kiếm, lọc theo vai trò) — UI hoàn chỉnh, kết nối API `GET /users`.
- [x] Thêm/sửa/xóa tài khoản Sinh viên, Giảng viên — Form create user UI tồn tại (avatar upload, role select), **Delete với confirm modal + API `DELETE /users/:id`**.
- [x] Phân quyền theo RBAC: gán vai trò cho từng tài khoản.
- [x] Upload danh sách hàng loạt từ file (bulk import).

### 2.4. Admin — Quản lý đào tạo
- [x] **Khoa / Ngành:** CRUD danh mục.
- [x] **Môn học (Courses):** CRUD môn học, gắn với Ngành.
- [x] **Lớp hành chính / Lớp học phần (Classes):** Danh sách lớp UI hoàn chỉnh (4 mock classes, search, filter theo học kỳ/khoa). Chi tiết lớp với bảng điểm danh SV.
- [x] **Phòng học (Rooms):** Quản lý phòng học + tọa độ GPS (phục vụ Geo-fencing).

### 2.5. Admin — Thời khóa biểu (Schedules)
- [x] Xếp lịch học: chọn lớp, phòng, giảng viên, khung giờ.
- [x] Kiểm tra tự động xung đột lịch phòng và lịch giảng viên.
- [x] Hiển thị thời khóa biểu theo tuần/tháng.

### 2.6. Admin — Dashboard & Thống kê
- [x] Dashboard tổng quan: 4 KPI cards, bar chart, hệ thống alerts, bảng lớp gần đây — UI hoàn chỉnh, mock data.
- [x] Statistics page: KPI cards, bar chart, SVG donut chart, trend placeholder — UI hoàn chỉnh, mock data.
- [x] Bộ lọc theo học kỳ, khoảng thời gian, khoa.
- [x] Xuất báo cáo dạng Excel / PDF.

### 2.7. Giảng viên — Quản lý điểm danh
- [x] Mở phiên điểm danh cho lớp học đang diễn ra.
- [x] Hiển thị mã QR động (render từ token HMAC-SHA256, tự động refresh mỗi 5s qua WebSocket) — **`qrcode.react` + QRTimer component + API refresh**.
- [x] Theo dõi danh sách sinh viên điểm danh real-time (cập nhật tức thì khi SV quét thành công) — UI class detail với per-session attendance checkboxes, attendance page với student list.
- [x] Đóng phiên điểm danh.
- [x] Chỉnh sửa trạng thái điểm danh thủ công (điều chỉnh cho từng sinh viên, ghi lý do) — UI checkboxes tồn tại, **đã lưu vào DB qua API**.

### 2.8. Giảng viên — Quản lý điểm số
- [x] Chọn lớp học phần, loại điểm (quá trình / giữa kỳ / cuối kỳ).
- [x] Nhập điểm cho từng sinh viên (bảng editable) — **UI hoàn chỉnh với editable inputs**.
- [x] Tự động tính điểm tổng kết theo công thức cấu hình — **calcTotal() tính QT 20% + GK 30% + CK 50%**.
- [x] Công bố điểm → sinh viên thấy trên app — **API `POST /grades/classes/:id/publish`** với confirm dialog.

### 2.9. Giảng viên — Lịch dạy & Danh sách lớp
- [x] Xem lịch dạy theo tuần/tháng — Dashboard có "Today's schedule" với 2 lớp.
- [x] Xem danh sách sinh viên từng lớp, thông tin phòng học — Class detail page với student attendance grid.

### 2.10. Giảng viên — Phê duyệt đơn vắng phép
- [x] Nhận thông báo khi có sinh viên gửi đơn xin nghỉ.
- [x] Xem nội dung đơn, chọn Chấp nhận / Từ chối — **UI hoàn chỉnh với pending/history tabs, 3 mock requests, accept/reject buttons, kết nối API `PATCH /leave-requests/:id/status`**.
- [x] Hệ thống cập nhật trạng thái điểm danh thành "Vắng có phép".

### 2.11. Giảng viên & Admin — Thông báo
- [x] Soạn và gửi thông báo đến lớp / khoa / toàn hệ thống — **Admin: compose form với title, content, recipient checkboxes, file upload, live preview, gửi API**. **Lecturer: compose với class selector chips, rich text toolbar, phone preview mockup, sent history**.
- [x] Dropdown hiển thị notification real-time — **NotificationBell component** với unread badge, API fetch, mark as read.
- [x] Đánh dấu đã đọc/chưa đọc.

### 2.12. Chat
- [x] Danh sách hội thoại (Group chat lớp học và chat 1-1 giữa Giảng viên ↔ Sinh viên).
- [x] Gửi/nhận tin nhắn real-time qua Socket.IO.
- [x] Hiển thị trạng thái online/offline.

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
- [x] QR Code động thay đổi đúng chu kỳ 5 giây.
- [ ] Thông báo real-time đến đúng người nhận.
- [ ] Chat gửi/nhận real-time giữa 2 user.

### 4.2. Performance & Edge Cases
- [ ] Xử lý đồng thời nhiều sinh viên điểm danh đầu buổi học (Kafka + Socket.IO).
- [ ] Kiểm tra tải trọng WebSocket cho chat/notification.
- [x] Offline handling: hiển thị thông báo khi mất kết nối.
- [ ] GPS không khả dụng trong nhà → fallback message.

### 4.3. UI/UX
- [x] Responsive trên các kích thước màn hình phổ biến — Tailwind responsive classes.
- [x] Dark mode hoạt động đúng — ThemeProvider + CSS tokens light/dark.
- [x] Loading states và error states cho mọi API call.
