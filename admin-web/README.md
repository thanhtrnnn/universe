# UniVerse - Admin Portal (Cổng Quản Trị Hệ Thống)

Đây là phân hệ Web Portal dành cho Quản trị viên (Admin) thuộc hệ sinh thái đại học số thông minh **UniVerse**. Dự án được xây dựng bằng Next.js (v16) và TailwindCSS (v4).

## 🚀 Tính năng nổi bật của Admin Portal
- **Dashboard**: Thống kê số lượng sinh viên, giảng viên, lớp học và tỷ lệ đi học trực quan.
- **Quản lý Khoa & Phòng ban**: Thêm, sửa, xóa, tìm kiếm các khoa trong trường.
- **Quản lý Người dùng**: Quản lý tài khoản sinh viên, giảng viên và phân quyền.
- **Quản lý Lịch học & Điểm số**: Xem và điều chỉnh kế hoạch đào tạo.

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
   Nếu bạn đang ở thư mục gốc của repository `UniVerse`, hãy chuyển vào thư mục `admin-web`:
   ```bash
   cd admin-web
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
   👉 **[http://localhost:3000](http://localhost:3000)** (nếu cổng 3000 chưa bị chiếm dụng).

   *Mẹo: Nếu cổng 3000 đã được sử dụng bởi ứng dụng khác (ví dụ: `lecturer-web`), Next.js sẽ tự động chuyển sang cổng tiếp theo như `http://localhost:3001` hoặc bạn có thể chỉ định cổng cụ thể bằng lệnh:*
   ```bash
   npm run dev -- -p 3000
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
- `src/app/(dashboard)/`: Chứa các trang đã đăng nhập (Dashboard, Departments, Courses, v.v.).
- `src/components/`: Chứa các component dùng chung (Sidebar, Header, Button, Table, Card, v.v.).
- `src/context/`: Quản lý giao diện Sáng/Tối (ThemeContext / ThemeProvider).
- `public/`: Chứa các tài nguyên tĩnh như hình ảnh, biểu tượng.

---

> [!NOTE]  
> Hiện tại dự án đang ở giai đoạn xây dựng Prototype Giao diện (UI). Các luồng dữ liệu đều được lưu trữ trực tiếp (hardcode) tại client để phục vụ demo trực quan và chưa kết nối cơ sở dữ liệu thực tế hay NestJS Backend.
