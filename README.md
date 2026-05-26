# UniVerse - Smart Campus Ecosystem

UniVerse là một hệ sinh thái đại học số thông minh, giải quyết sự phân mảnh trong quản lý đại học hiện nay thông qua nền tảng All-in-One kết nối Sinh viên - Giảng viên - Nhà trường theo thời gian thực (Real-time).

## Key Features
- **Smart Attendance**: QR Code động + Geo-fencing (GPS).
- **Academic Management**: Đăng ký tín chỉ, xem lịch học, lịch thi, điểm số.
- **Social**: Hệ thống Chat và Thông báo thời gian thực.
- **Admin Portal**: Quản lý hệ thống và Dashboard báo cáo thống kê.
- **AI Chatbot**: Hỗ trợ sinh viên trả lời câu hỏi thường gặp.

## Tech Stack
- **Frontend (Mobile)**: React Native.
- **Web Portal**: Next.js + TailwindCSS.
- **Backend**: Node.js (NestJS).
- **Database**: PostgreSQL, MongoDB, Redis.
- **Advanced Tech**: Socket.IO, Apache Kafka, Docker, OpenAI API.

## Project Structure
- `apps/server`: NestJS backend.
- `apps/web`: Next.js admin portal.
- `apps/mobile`: React Native mobile application.
- `apps/ai-service`: Python AI service (OpenAI integration).
- `docker`: Docker configuration files.
- `docs`: Detailed documentation and report outline.

## Setup & Development / Cài đặt & Phát triển

Hiện tại dự án đang ở giai đoạn hoàn thiện giao diện mẫu (UI Prototype). Bạn có thể chạy riêng biệt từng phân hệ Web Portal (Next.js) như sau:

### 1. Cổng Quản Trị Hệ Thống (Admin Portal)
- **Thư mục:** `/admin-web`
- **Cổng chạy mặc định:** `3000` (hoặc tự động tăng lên `3001` nếu bị trùng)
- **Lệnh thực hiện:**
  ```bash
  cd admin-web
  npm install
  npm run dev
  ```
  Truy cập: [http://localhost:3000](http://localhost:3000)

### 2. Cổng Dành Cho Giảng Viên (Lecturer Portal)
- **Thư mục:** `/lecturer-web`
- **Cổng chạy mặc định:** `3000` (để tránh xung đột cổng với Admin Portal, bạn nên chạy trên cổng khác ví dụ `3011`)
- **Lệnh thực hiện:**
  ```bash
  cd lecturer-web
  npm install
  # Chạy trên cổng 3011
  npm run dev -- -p 3011
  ```
  Truy cập: [http://localhost:3011](http://localhost:3011)

### 3. Cổng Dành Cho Sinh Viên (Student Portal)
- **Thư mục:** `/student-web`
- **Cổng chạy mặc định:** `3012` (đã được định hình sẵn trong `package.json`)
- **Lệnh thực hiện:**
  ```bash
  cd student-web
  npm install
  npm run dev
  ```
  Truy cập: [http://localhost:3012](http://localhost:3012)

---

### Chạy bằng Docker (Khi tích hợp Backend)
1. Hãy đảm bảo bạn đã cài đặt Docker và Node.js (v20+).
2. Khởi chạy các dịch vụ cơ sở hạ tầng (PostgreSQL, MongoDB, Redis, Kafka) bằng lệnh:
   ```bash
   docker-compose up -d
   ```
