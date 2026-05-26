# UniVerse - Student Portal (Cổng Dành Cho Sinh Viên)

Đây là phân hệ Web Portal dành cho Sinh viên (Student) thuộc hệ sinh thái đại học số thông minh **UniVerse**. Dự án được xây dựng bằng Next.js (v16) và TailwindCSS (v4).

## 🚀 Tính năng nổi bật của Student Portal
- **Xem Lịch học & Lịch thi**: Hiển thị chi tiết theo ngày/tuần trực quan, sinh động.
- **Điểm danh bằng QR Code**: Quét mã QR động từ giảng viên kết hợp định vị GPS (Geo-fencing) để xác nhận tham gia lớp học tức thì.
- **Theo dõi Điểm số & Học tập**: Tra cứu điểm thi, điểm quá trình và tiến độ học tập toàn diện.
- **Nhận Thông báo & Tin nhắn**: Cập nhật thông tin khẩn cấp từ nhà trường và trao đổi trực tiếp với giảng viên.

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
   Nếu bạn đang ở thư mục gốc của repository `UniVerse`, hãy chuyển vào thư mục `student-web`:
   ```bash
   cd student-web
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
   Sau khi chạy lệnh trên thành công, ứng dụng sẽ khởi chạy trên cổng được cấu hình sẵn:
   👉 **[http://localhost:3102](http://localhost:3012)** (hoặc truy cập trực tiếp **[http://localhost:3012](http://localhost:3012)**).

   *Lưu ý: Khác với các phân hệ khác, Student Portal đã được cấu hình mặc định chạy trên cổng `3012` trong `package.json` để tránh xung đột trực tiếp khi bạn chạy song song nhiều phân hệ web.*

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
- `src/app/(dashboard)/`: Chứa các trang nghiệp vụ dành cho sinh viên (Weekly Schedule, Exam Schedule, Attendance, Notifications, Messages, v.v.).
- `src/components/`: Chứa các component dùng chung (Sidebar, Header, Button, AttendanceModal, v.v.).
- `src/context/`: Quản lý giao diện Sáng/Tối (ThemeContext / ThemeProvider).
- `public/`: Chứa các tài nguyên tĩnh như hình ảnh, biểu tượng.

---

> [!NOTE]  
> Hiện tại dự án đang ở giai đoạn xây dựng Prototype Giao diện (UI). Các luồng dữ liệu đều được lưu trữ trực tiếp (hardcode) tại client để phục vụ demo trực quan và chưa kết nối cơ sở dữ liệu thực tế hay NestJS Backend.
