# Frontend Implementation Checklist

Đây là checklist các công việc cần thực hiện để hoàn thiện hai nhánh frontend: `fe/web` (Next.js) và `fe/app` (React Native), kết nối trực tiếp với các tính năng đã có sẵn ở backend.

## 1. Môi trường & Core Setup (Cả hai nhánh)
- [ ] Khởi tạo/Cấu hình TypeScript (`tsconfig.json`).
- [ ] Khai báo đồng bộ các Interface/Type từ Backend (Ví dụ: `User`, `Course`, `Schedule`, `Attendance`, `Message`, `Notification`).
- [ ] Cấu hình API Client (Axios):
  - [ ] Thiết lập `baseURL` từ biến môi trường.
  - [ ] Viết Axios Interceptor tự động chèn JWT Access Token vào header.
  - [ ] Xử lý logic tự động gọi Refresh Token API khi Access Token hết hạn (Lỗi 401).
- [ ] Cấu hình Global State (ví dụ dùng Zustand hoặc Redux Toolkit).
- [ ] Tích hợp Real-time Client (`socket.io-client`):
  - [ ] Khởi tạo connection và quản lý connection state.
  - [ ] Lắng nghe event từ `NotificationsGateway`.

## 2. Nhánh `fe/web` (Next.js)
### 2.1. Cấu hình ban đầu
- [ ] Thiết lập App Router (`src/app/`) hoặc Pages Router (`src/pages/`).
- [ ] Cài đặt và cấu hình thư viện UI (ví dụ Shadcn UI, Ant Design hoặc NextUI kết hợp TailwindCSS có sẵn).
- [ ] Thiết lập cơ chế lưu trữ JWT an toàn (VD: lưu Access Token ở localStorage/Zustand, Refresh Token ở HttpOnly cookie).

### 2.2. Xây dựng Giao diện (UI/UX)
- [ ] **Auth Pages:** Đăng nhập, Đăng ký, Quên mật khẩu.
- [ ] **Layouts:** `AdminLayout`, `LecturerLayout`, `StudentLayout` (phân quyền hiển thị theo Role).
- [ ] **Quản lý khóa học (Courses):** Danh sách, thêm/sửa/xóa môn học và lớp học.
- [ ] **Quản lý sinh viên & Đăng ký (Enrollment):** Ghi danh sinh viên vào lớp.
- [ ] **Lịch học (Schedules):** Hiển thị thời khóa biểu của Giảng viên/Sinh viên.
- [ ] **Bảng điểm (Grades):** Xem điểm, cập nhật điểm số.
- [ ] **Cổng thông báo (Notifications):** Dropdown hiển thị real-time notification.

## 3. Nhánh `fe/app` (React Native)
### 3.1. Cấu hình ban đầu
- [ ] Thiết lập React Navigation:
  - [ ] `Stack Navigator` cho Auth (Login, Register).
  - [ ] `Bottom Tab Navigator` cho Dashboard, Lịch học, Quét QR, Cá nhân.
- [ ] Cài đặt thư viện UI (React Native Paper hoặc NativeWind).
- [ ] **Hardware Permissions (Quan trọng):**
  - [ ] Cấu hình quyền truy cập Camera (iOS `Info.plist`, Android `AndroidManifest.xml`).
  - [ ] Cấu hình quyền truy cập Vị trí/GPS (Foreground/Background Location).

### 3.2. Xây dựng Màn hình (Screens)
- [ ] **Auth:** Màn hình Login/Register.
- [ ] **Dashboard/Lịch học:** Xem các buổi học sắp diễn ra.
- [ ] **Smart Attendance (Điểm danh thông minh - SV & GV):**
  - [ ] *Giảng viên:* Tạo QR Code động (Dynamic QR sinh từ payload HMAC-SHA256).
  - [ ] *Sinh viên:* Màn hình quét QR sử dụng `react-native-qrcode-scanner`.
  - [ ] *Sinh viên:* Lấy tọa độ GPS bằng `react-native-geolocation-service` và gộp kèm token gửi lên backend.
- [ ] **Chat:** Giao diện tin nhắn trực tiếp với giáo viên/sinh viên khác.
- [ ] **Push Notification:** Lắng nghe và hiển thị local notification khi có tin mới/thông báo lớp học.

## 4. Kiểm thử & Tối ưu
- [ ] Kiểm tra luồng đăng nhập, refresh token trên cả hai nền tảng.
- [ ] Test độ chính xác của GPS/Haversine distance (Khoảng cách < 50m khi điểm danh).
- [ ] Đảm bảo QR Code động hoạt động tốt và timeout trong vòng 5 giây.
- [ ] Kiểm tra tải trọng của WebSockets cho chat/notification.
