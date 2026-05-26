# UNIVERSE — Hệ Sinh Thái Đại Học Số Thông Minh
**File Context & System Instructions for AI Coding Assistants**

---

## 1. TỔNG QUAN DỰ ÁN (PROJECT OVERVIEW)

UniVerse là nền tảng quản lý giáo dục đại học All-in-One, giải quyết sự phân mảnh trong quản lý đại học hiện nay. Hệ thống kết nối Sinh viên – Giảng viên – Nhà trường theo thời gian thực thông qua Mobile App (React Native) và Web Portal (Next.js).

### 1.1. Nhóm phát triển (Team HTH)
| Thành viên | MSSV | Vai trò | Branch phụ trách |
|---|---|---|---|
| Trần Xuân Thành | B23DCAT280 | Frontend Mobile | `fe/app` |
| Nguyễn Bá Hùng | B23DCAT120 | Backend & Database | `biden` |
| Phạm Thị Thiên Hà | B23DCCN266 | Frontend Web & UI/UX | `fe/web` |

**Giảng viên hướng dẫn:** Đỗ Thị Liên — Lớp D23CQCE01-B

### 1.2. Báo cáo TTCS (report/)
File `[TTCS] Báo cáo - Nhóm HTH.docx` trong thư mục `report/` là báo cáo bài tập lớn Thực tập cơ sở, gồm 5 chương:
- **Chương 1: Mở đầu** — Bối cảnh, mục tiêu, khảo sát hiện trạng, phân tích tính khả thi, quản trị rủi ro.
- **Chương 2: Xác định yêu cầu** — Mô hình nghiệp vụ, tiến trình cốt lõi, Use Case diagrams.
- **Chương 3: Phân tích và kiến trúc hệ thống** — Phương pháp OOSE, mô hình Agile/Scrum, kiến trúc Microservices-lite.
- **Chương 4: Thiết kế hệ thống** — Kiến trúc Client-Server, ERD PostgreSQL, Schema MongoDB, giải thuật QR+GPS, UI/UX Design.
- **Chương 5: Kết quả cài đặt** — Demo tính năng, test cases, đánh giá hiệu năng.

---

## 2. ACTORS & USE CASES

### 2.1. Student (Sinh viên — Mobile App)
- Điểm danh thông minh (quét QR + GPS Geo-fencing).
- Xem lịch học / lịch thi.
- Xem điểm số theo học kỳ.
- Theo dõi tiến độ tín chỉ tốt nghiệp.
- Chat với giảng viên.
- Nhận thông báo real-time / push notification.

### 2.2. Lecturer (Giảng viên — Web Portal & Mobile App)
- Tạo mã QR điểm danh động (refresh mỗi 5s).
- Theo dõi điểm danh real-time (danh sách cập nhật tức thì).
- Chỉnh sửa trạng thái điểm danh thủ công.
- Nhập / cập nhật / công bố điểm số.
- Phê duyệt đơn vắng phép.
- Gửi thông báo khẩn đến lớp.
- Chat với sinh viên.

### 2.3. Admin (Quản trị viên — Web Portal)
- Quản lý tài khoản người dùng (CRUD + bulk import).
- Phân quyền theo RBAC.
- Quản lý danh mục: Khoa, Ngành, Môn học, Lớp, Phòng học.
- Xếp thời khóa biểu (kiểm tra xung đột tự động).
- Dashboard thống kê: tỷ lệ chuyên cần theo lớp/khoa/toàn trường.
- Xuất báo cáo Excel/PDF.
- Gửi thông báo toàn hệ thống.

---

## 3. TECH STACK

| Layer | Technology | Ghi chú |
|---|---|---|
| Mobile App | React Native | iOS + Android, QR scanner, GPS |
| Web Portal | Next.js + TailwindCSS | SSR/SSG, Shadcn UI / Ant Design |
| Backend | NestJS (Node.js/TypeScript) | Module hóa, DI, RESTful API |
| Database (Relational) | PostgreSQL 15 | Users, Courses, Classes, Schedules, Attendances, Grades, Enrollments |
| Database (Document) | MongoDB 7.0 | messages, notifications, activity_logs |
| Cache / Session | Redis 7.2 | JWT session, QR token cache, schedule cache |
| Message Broker | Apache Kafka | Event streaming: class-notifications topic |
| Real-time | Socket.IO / WebSocket | Chat, attendance updates, notifications |
| AI Service | Python + Flask + OpenAI API | Chatbot hỗ trợ sinh viên |
| Containerization | Docker + Docker Compose | 8 services: api, web, postgres, mongodb, redis, zookeeper, kafka, ai-service |

---

## 4. PROJECT STRUCTURE (Thực tế trên `main` branch)

```
universe/
├── AGENT.md                          # File này
├── README.md                         # Project overview
├── STRUCTURE.md                      # Cấu trúc dự kiến (cần cập nhật)
├── docker-compose.yml                # Docker orchestration (8 services)
│
├── apps/
│   ├── server/                       # NestJS Backend (code trên branch `biden`)
│   │   └── package.json
│   ├── web/                          # Next.js Web Portal (code trên branch `fe/web`)
│   │   └── package.json
│   ├── mobile/                       # React Native App (code trên branch `fe/app`)
│   │   └── package.json
│   └── ai-service/                   # Python AI Chatbot
│       ├── main.py
│       └── requirements.txt
│
├── docs/
│   ├── BACKEND_CHECKLIST.md          # Checklist backend chi tiết (biden branch)
│   ├── FRONTEND_CHECKLIST.md         # Checklist frontend chi tiết (fe/web + fe/app)
│   └── REPORT_OUTLINE.md             # Outline báo cáo TTCS
│
└── report/
    └── [TTCS] Báo cáo - Nhóm HTH.docx   # Báo cáo bài tập lớn (untracked)
```

**Lưu ý quan trọng:** `STRUCTURE.md` mô tả cấu trúc cũ (top-level `mobile/`, `web-admin/`, `backend/`). Cấu trúc thực tế sử dụng `apps/` prefix. Cần cập nhật STRUCTURE.md.

---

## 5. DATABASE BOUNDARIES

### 5.1. PostgreSQL (Relational — TypeORM)
Dữ liệu có cấu trúc, yêu cầu ACID:
- **Master tables:** `Users`, `Roles`, `Courses`, `Classes`, `Rooms`
- **Transaction tables:** `Schedules`, `Attendances`, `Grades`, `Enrollments`
- **Junction tables:** `Enrollment` (Student–Class), `Conversation` (Student–Lecturer)

### 5.2. MongoDB (Document — Mongoose)
Dữ liệu phi cấu trúc, lưu lượng lớn:
- `messages` — Nội dung chat (cấu trúc conversation/message).
- `notifications` — Thông báo theo user, trạng thái đã đọc/chưa đọc.
- `activity_logs` — Nhật ký hoạt động hệ thống.

### 5.3. Redis (Cache — ioredis)
- JWT session & Refresh token.
- QR token cache (tự động hủy sau 5s).
- Cache thời khóa biểu giảm tải PostgreSQL.

---

## 6. CORE BUSINESS LOGIC

### 6.1. Smart Attendance — Điểm danh thông minh (2 lớp xác thực)
1. **Dynamic QR Code (Lớp 1):** Server tạo token chứa `session_id` + `timestamp`, ký HMAC-SHA256, thay đổi mỗi 5 giây.
2. **GPS Geo-fencing (Lớp 2):** Client gửi tọa độ GPS. Server tính khoảng cách bằng **công thức Haversine**. Ngưỡng: `≤ 50m`.
3. **Bắt buộc thỏa mãn đồng thời cả 2 lớp.** Chỉ QR hợp lệ mà ngoài phạm vi → từ chối.

### 6.2. Real-time Notification Flow (Kafka + Socket.IO)
1. Giảng viên tạo thông báo → API lưu MongoDB.
2. Publish event vào Kafka topic `class-notifications`.
3. Kafka Consumer → Push qua Socket.IO (user online) hoặc Firebase FCM (user offline).

### 6.3. Authentication & Authorization
- JWT-based: Access Token (ngắn hạn) + Refresh Token (dài hạn).
- RBAC: 3 vai trò `student`, `lecturer`, `admin` với quyền hạn khác nhau.
- Guards: `JwtAuthGuard`, `RolesGuard` ở backend.

---

## 7. AI CODING DIRECTIVES

- **Always TypeScript** — Mọi logic code phải có Type/Interface rõ ràng.
- **NestJS Architecture** — Tuân thủ: `Controller` → `Service` → `Repository`. Bắt buộc dùng DTO với `class-validator`.
- **Thực thi toán học thực tế** — Không mock cho GPS/Haversine. Implement chuẩn thuật toán.
- **Bảo mật** — Mật khẩu hash bằng `bcrypt`. API bảo vệ bằng JWT + Roles. Xử lý lỗi HTTP minh bạch.
- **Frontend-Backend Sync** — TypeScript interfaces phải đồng bộ giữa FE và BE.
- **Monorepo Layout** — Code nằm trong `apps/` prefix. Mỗi app có `package.json` riêng.
- **Docker First** — Mọi dịch vụ phải chạy được qua `docker-compose up -d`.

---

## 8. COMPREHENSIVE PROJECT CHECKLIST

### 8.1. Backend (`biden` branch)
- [ ] **TypeORM Migrations:** Cấu hình migration thay vì `synchronize: true`.
- [ ] **DTOs:** Bổ sung DTO cho Courses, Schedule, Grades, Attendance, Pagination. (Hiện đang dùng `any`).
- [ ] **Testing:** Unit tests + e2e tests cho APIs cốt lõi.
- [ ] **Rate Limiting & Throttling:** Bảo vệ API chống spam/DDoS.
- [ ] **File Upload:** Upload ảnh đại diện, file đính kèm chat.
- [ ] **Push Notification (FCM):** Tích hợp Firebase Cloud Messaging cho user offline.
- [ ] **AI Chatbot:** Module AI trả lời câu hỏi thường gặp.
- [ ] **System Observability:** Logging middleware + Health check endpoint.
- [ ] **Data Seeding:** Script tạo dữ liệu mẫu ban đầu.
- [ ] **Swagger:** Đảm bảo API docs đầy đủ tại `/api`.
- [ ] **Absence Approval API:** Endpoint cho giảng viên phê duyệt đơn vắng phép.
- [ ] **Grade Publishing API:** Endpoint công bố điểm → sinh viên thấy trên app.
- [ ] **Bulk Import API:** Endpoint upload danh sách người dùng từ file.
- [ ] **Kafka Topics:** `class-notifications`, `attendance-events`.
- [ ] **Socket.IO Gateways:** AttendanceGateway, NotificationsGateway, ChatGateway.
- [ ] **Haversine:** Implement chuẩn công thức Haversine cho GPS Geo-fencing.
- [ ] **Schedule Conflict Detection:** Kiểm tra trùng phòng + trùng giảng viên.
- [ ] **Audit Log:** Ghi activity_log cho mọi thay đổi quan trọng.

Xem chi tiết tại [docs/BACKEND_CHECKLIST.md](docs/BACKEND_CHECKLIST.md).

### 8.2. Web Portal (`fe/web` branch)
- [x] **Architecture Sync:** Đồng bộ cấu trúc thực tế (`apps/web/`) với `STRUCTURE.md`. (Lưu ý: `admin-web/` và `lecturer-web/` nằm ở root, không phải trong `apps/web/`).
- [x] **API Client & State:** Axios interceptors (JWT auto-attach + refresh), Global State (Zustand/Redux).
- [x] **Real-time Client:** `socket.io-client` cho thông báo, chat, attendance updates.
- [x] **Auth Pages:** Đăng nhập UI có (`admin-web/login/`), đã có API call + JWT. Lecturer login page mới tạo. Middleware route protection.
- [x] **Admin UI:** Dashboard (KPI + charts), User list (search/filter/pagination), Create user form, Classes list + detail, Statistics (donut + bar chart), Notifications compose + preview, Settings (dark mode toggle).
- [x] **Admin Logic:** CRUD operations (fetch + delete confirm modal + toast), Notifications gửi via API, Notification bell dropdown (unread badge + mark as read).
- [x] **Lecturer UI:** Dashboard (schedule + warnings), Classes + attendance detail (per-session checkboxes), Grades (editable inputs + auto-calc QT 20% GK 30% CK 50%), Statistics, Leave requests (accept/reject), Messages (compose + preview).
- [x] **Lecturer Logic:** QR generation (qrcode.react + 5s QRTimer + API refresh), Grade publish API, Leave request accept/reject API, Real-time attendance polling.
- [x] **Design System:** TailwindCSS v4 + Material Design 3 tokens (light + dark). ThemeProvider với localStorage + FOUC prevention.
- [x] **Type Sync:** Đồng bộ TypeScript interfaces từ Backend — `src/types/index.ts`.

Xem chi tiết tại [docs/FRONTEND_CHECKLIST.md](docs/FRONTEND_CHECKLIST.md).

### 8.3. Mobile App (`fe/app` branch)
- [ ] **UI/UX:** Hoàn thiện toàn bộ luồng giao diện (Auth, Dashboard, QR Attendance, Chat, Schedule, Grades, Profile).
- [ ] **Navigation:** Stack (Auth) + Bottom Tab (Main App).
- [ ] **Hardware Permissions:** Camera (QR scan), Location/GPS (Geo-fencing).
- [ ] **API & Real-time:** Axios, Global State, Socket.IO.
- [ ] **Push Notifications:** FCM background/foreground handling.
- [ ] **Credit Tracking:** Màn hình tiến độ tín chỉ tốt nghiệp.
- [ ] **Attendance History:** Xem lịch sử điểm danh theo môn.

Xem chi tiết tại [docs/FRONTEND_CHECKLIST.md](docs/FRONTEND_CHECKLIST.md).

### 8.4. Infrastructure & DevOps
- [ ] **Dockerfiles:** Tạo Dockerfile cho `apps/server`, `apps/web`, `apps/ai-service` (hiện chưa có trên main).
- [ ] **.env.example:** File mẫu biến môi trường cho tất cả services.
- [ ] **.gitignore:** Loại bỏ `node_modules`, `.env`, `dist`, `report/`.
- [ ] **STRUCTURE.md:** Cập nhật cho khớp với cấu trúc thực tế (`apps/` prefix).
- [ ] **Monorepo Tooling:** Cân nhắc pnpm-workspace hoặc turborepo.

### 8.5. Báo cáo TTCS (report/)
- [ ] **Danh mục bảng/hình vẽ:** Bổ sung danh sách bảng và hình vẽ (hiện ghi "sẽ bổ sung").
- [ ] **Hình ảnh demo:** Chụp screenshot các tính năng đã hoàn thành cho Chương 5.
- [ ] **Flowcharts:** Hoàn thiện các flowchart còn thiếu (Đăng nhập, Điểm danh, Thông báo).
- [ ] **Test Cases:** Bảng test cases chi tiết cho tính năng QR + GPS.
- [ ] **Video Demo:** Quay video demo sản phẩm.
- [x] **Figma Links:** Đính kèm link Figma thiết kế giao diện.
- [ ] **GitHub Link:** Cập nhật link source code chính xác.

---

## 9. REPORT ↔ CODEBASE MAPPING

| Báo cáo | Codebase | Branch |
|---|---|---|
| Chương 1-2: Phân tích, yêu cầu | AGENT.md, REPORT_OUTLINE.md | `main` |
| Chương 3.1: Kiến trúc tổng thể | `docker-compose.yml`, NestJS modules | `biden` |
| Chương 3.2-3.3: ERD, Schema | TypeORM entities, Mongoose schemas | `biden` |
| Chapter 4.1: Kiến trúc Client-Server | `apps/server/`, `apps/web/`, `apps/mobile/` | All branches |
| Chương 4.2: Database Design | PostgreSQL + MongoDB + Redis setup | `biden` |
| Chương 4.3: Giải thuật QR + GPS | Attendance module (HMAC-SHA256 + Haversine) | `biden` |
| Chương 4.4: UI/UX Design | Next.js pages, React Native screens | `fe/web`, `fe/app` |
| Chương 5.1: Môi trường | Docker Compose, Node.js v20, Python 3.11 | `main` |
| Chương 5.2: Demo | Web portal, Mobile app screenshots | `fe/web`, `fe/app` |
| Chương 5.3: Kiểm thử | Unit tests, e2e tests | `biden` |
