# UniVerse - Lecturer Portal (Cổng Dành Cho Giảng Viên)

Đây là phân hệ Web Portal dành cho Giảng viên (Lecturer) thuộc hệ sinh thái đại học số thông minh **UniVerse**. Dự án được xây dựng bằng Next.js (v16) và TailwindCSS (v4).

## 🚀 Tính năng nổi bật của Lecturer Portal
- **Quản lý Lịch dạy**: Xem thời khóa biểu cá nhân theo tuần/tháng tiện lợi.
- **Điểm danh Thông minh**: Tạo mã QR Code động tích hợp Geo-fencing (vị trí GPS) để sinh viên điểm danh theo thời gian thực.
- **Quản lý Điểm & Đánh giá**: Nhập điểm thi, điểm quá trình và chấm điểm bài tập của sinh viên.
- **Giao lưu & Trao đổi**: Hệ thống thông báo và tin nhắn trực tiếp với sinh viên và khoa.

---

## 🛠️ Công nghệ sử dụng
- **Framework**: Next.js 16.2.6 (App Router, Client Component `"use client"`)
- **UI & Styling**: React 19, TailwindCSS v4, Material Symbols Outlined
- **State Management**: Zustand (dùng cho quản lý trạng thái client)
- **Real-time & API (Dự kiến)**: Socket.IO Client, Axios (hiện tại dữ liệu đang được hardcode cho mục đích thiết kế giao diện mẫu)

---

## 💻 Hướng dẫn Cài đặt & Khởi chạy

### Yêu cầu hệ thống
- **Node.js**: Phiên bản 20 trở lên.
- **npm** hoặc **yarn/pnpm/bun**.

### Các bước thực hiện

1. **Di chuyển vào thư mục của dự án:**
   Nếu bạn đang ở thư mục gốc của repository `UniVerse`, hãy chuyển vào thư mục `lecturer-web`:
   ```bash
   cd lecturer-web
   ```

2. **Cài đặt các thư viện phụ thuộc (Dependencies):**
   ```bash
   npm install
   ```
   *(Hoặc sử dụng `pnpm install`, `yarn install` nếu bạn ưa thích)*

3. **Chạy ứng dụng ở chế độ phát triển (Development Mode):**
   ```bash
   npm run dev
   ```
   Sau khi chạy lệnh trên thành công, ứng dụng sẽ chạy trên cổng mặc định:
   👉 **[http://localhost:3000](http://localhost:3000)** (hoặc cổng tự động tiếp theo như `http://localhost:3001` nếu cổng 3000 đã được sử dụng bởi `admin-web`).

   *Mẹo: Để tránh xung đột cổng với `admin-web`, bạn có thể chỉ định cổng chạy cụ thể cho Lecturer Web (ví dụ cổng `3011`):*
   ```bash
   npm run dev -- -p 3011
   ```

4. **Biên dịch dự án cho môi trường Production (Build):**
   ```bash
   npm run build
   ```
   Sau khi build xong, bạn có thể khởi chạy server production bằng lệnh:
   ```bash
   npm run start
   ```

---

## 📂 Cấu trúc thư mục chính
- `src/app/(dashboard)/`: Chứa các trang nghiệp vụ dành cho giảng viên (Schedule, Attendance, Grades, Messages, v.v.).
- `src/components/`: Chứa các component dùng chung (Sidebar, Header, Button, AttendanceQR, v.v.).
- `src/context/`: Quản lý giao diện Sáng/Tối (ThemeContext / ThemeProvider).
- `public/`: Chứa các tài nguyên tĩnh như hình ảnh, biểu tượng.

---

> [!NOTE]  
> Hiện tại dự án đang ở giai đoạn xây dựng Prototype Giao diện (UI). Các luồng dữ liệu đều được lưu trữ trực tiếp (hardcode) tại client để phục vụ demo trực quan và chưa kết nối cơ sở dữ liệu thực tế hay NestJS Backend.
