# CHƯƠNG 1: MỞ ĐẦU

---

## 1.1. Tổng quan đề tài

Trong bối cảnh chuyển đổi số mạnh mẽ của ngành giáo dục đại học, các trường đại học hiện nay phải đối mặt với bài toán quản lý phân mảnh: điểm danh thủ công dễ gian lận, hệ thống quản lý điểm số riêng lẻ, thiếu kênh giao tiếp trực tiếp giữa sinh viên và giảng viên, và thông báo chưa kịp thời. Sinh viên phải dùng nhiều ứng dụng khác nhau để tra lịch học, xem điểm, và liên hệ giảng viên — tạo ra trải nghiệm học tập rời rạc, không nhất quán.

**UniVerse** ra đời với sứ mệnh xây dựng một hệ sinh thái số tích hợp cho môi trường đại học, kết nối ba nhóm người dùng chính — Sinh viên, Giảng viên và Quản trị viên — trên một nền tảng duy nhất. Hệ thống giải quyết đồng thời nhiều vấn đề cốt lõi:

- **Điểm danh gian lận:** Cơ chế QR Code động kết hợp định vị GPS (Geo-fencing) xác minh kép, đảm bảo sinh viên phải có mặt thực tế trong phạm vi 50m khu vực lớp học.
- **Quản lý học tập phân mảnh:** Một nền tảng thống nhất cho toàn bộ vòng đời học tập — từ đăng ký môn học, tra lịch học, đến xem điểm và tiến độ tín chỉ.
- **Thiếu giao tiếp thực thời:** Hệ thống chat nội bộ và thông báo real-time qua Socket.IO + Kafka đảm bảo thông tin được truyền tải tức thì.

Phạm vi áp dụng của UniVerse bao gồm toàn bộ quy trình quản lý học tập tại trường đại học: quản trị viên cấu hình hệ thống và quản lý người dùng; giảng viên tổ chức lớp học, điểm danh và nhập điểm; sinh viên theo dõi lịch học, điểm số và giao tiếp với giảng viên.

---

## 1.2. Giới thiệu công nghệ sử dụng

Hệ thống UniVerse được xây dựng theo kiến trúc đa tầng hiện đại, với các công nghệ được lựa chọn dựa trên tiêu chí hiệu năng cao, khả năng mở rộng và hỗ trợ real-time:

| Tầng | Công nghệ | Phiên bản | Vai trò |
|------|-----------|-----------|---------|
| **Backend API** | NestJS + TypeScript | 10.3 / 5.3 | REST API server, business logic, xác thực JWT |
| **Frontend Web** | Next.js + React | 14+ / 18 | Web portal cho 3 vai trò: Admin, Giảng viên, Sinh viên |
| **Mobile App** | React Native | — | Ứng dụng di động điểm danh QR+GPS (kế hoạch) |
| **CSDL quan hệ** | PostgreSQL | 15 | Lưu trữ dữ liệu cốt lõi (người dùng, lớp học, điểm số) |
| **CSDL tài liệu** | MongoDB | 7.0 | Lưu trữ tin nhắn, thông báo, nhật ký hoạt động |
| **Cache / Session** | Redis | 7.2 | JWT token, QR token (TTL 5s), rate limiting |
| **Message Broker** | Apache Kafka | — | Streaming sự kiện thông báo bất đồng bộ |
| **Giao tiếp thực thời** | Socket.IO | 4.7 | WebSocket cho điểm danh, thông báo và chat |
| **Hạ tầng** | Docker + Docker Compose | — | Đóng gói và triển khai 8 dịch vụ đồng thời |
| **Bảo mật** | bcrypt + JWT + Passport | — | Mã hóa mật khẩu, xác thực token, phân quyền RBAC |
| **Kiểm thử** | Jest | 29.7 | Unit test và end-to-end test |

**Điểm nổi bật về công nghệ:**
- **HMAC-SHA256 + Redis TTL 5s:** Mã QR điểm danh được ký bằng HMAC-SHA256 và tự động hủy sau 5 giây, ngăn chặn việc chia sẻ hoặc chụp màn hình mã QR để gian lận.
- **Haversine Formula:** Tính khoảng cách chính xác giữa tọa độ GPS của sinh viên và tọa độ phòng học đã lưu, xác minh sinh viên ở trong phạm vi 50m.
- **Material Design 3 + TailwindCSS v4:** Giao diện web hiện đại, hỗ trợ chế độ sáng/tối tự động theo cài đặt thiết bị.
- **TypeORM Migrations:** Quản lý schema CSDL có version control, đảm bảo nhất quán giữa các môi trường.
