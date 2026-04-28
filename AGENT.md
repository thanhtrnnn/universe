\# UNIVERSE - HỆ SINH THÁI ĐẠI HỌC SỐ THÔNG MINH

\*\*File Context \& System Instructions for AI Coding Assistants\*\*



\## 1. TỔNG QUAN DỰ ÁN (PROJECT CONTEXT)

\- \[cite\_start]\*\*Tên dự án:\*\* UniVerse\[cite: 112].

\- \[cite\_start]\*\*Mục tiêu:\*\* Xây dựng nền tảng All-in-One quản lý giáo dục đại học, kết nối Sinh viên – Giảng viên – Nhà trường theo thời gian thực\[cite: 112].

\- \[cite\_start]\*\*Tính năng cốt lõi:\*\* Điểm danh thông minh (chống gian lận 2 lớp), Quản lý học tập (tín chỉ, điểm số), Truyền thông nội bộ (Chat \& Thông báo realtime)\[cite: 113].

\- \*\*Phân quyền (RBAC Actors):\*\*

&#x20; - \[cite\_start]`Student` (Sinh viên): Dùng Mobile App\[cite: 345].

&#x20; - \[cite\_start]`Lecturer` (Giảng viên): Dùng Mobile App \& Web Portal\[cite: 346].

&#x20; - \[cite\_start]`Admin` (Quản trị viên): Dùng Web Admin Portal\[cite: 347].



\## 2. QUY CHUẨN CÔNG NGHỆ (TECH STACK)

Khi sinh code, \*\*bắt buộc\*\* sử dụng các công nghệ sau:

\- \[cite\_start]\*\*Frontend - Mobile App:\*\* `React Native` (Hỗ trợ iOS/Android)\[cite: 137, 138].

\- \[cite\_start]\*\*Frontend - Web Portal:\*\* `Next.js` (SSR/SSG) kết hợp `TailwindCSS`\[cite: 139, 140].

\- \[cite\_start]\*\*Backend:\*\* `NestJS` (Node.js framework) với kiến trúc Module hóa \[cite: 141]\[cite\_start], code bằng `TypeScript`\[cite: 142].

\- \[cite\_start]\*\*Cơ sở dữ liệu (Multi-Database):\*\* `PostgreSQL` (Core/SQL) \[cite: 144]\[cite\_start], `MongoDB` (NoSQL) \[cite: 146]\[cite\_start], `Redis` (Cache/In-memory)\[cite: 148].

\- \[cite\_start]\*\*Event Streaming \& Realtime:\*\* `Apache Kafka` (Message Broker) \[cite: 151] \[cite\_start]và `Socket.IO` / `WebSocket`\[cite: 150].

\- \[cite\_start]\*\*Containerization:\*\* `Docker` \& `Docker Compose`\[cite: 153].



\## 3. PHÂN ĐỊNH RANH GIỚI DỮ LIỆU (DATABASE BOUNDARIES)

Hệ thống sử dụng Multi-Database, tuyệt đối không nhầm lẫn vị trí lưu trữ:



\### 3.1. PostgreSQL (Relational Data)

\[cite\_start]Dùng cho dữ liệu có cấu trúc, quan hệ phức tạp, yêu cầu ACID\[cite: 144, 145]. \[cite\_start]Quản lý truy vấn bằng ORM TypeORM\[cite: 280].

\- \[cite\_start]Bảng cốt lõi: `User`, `Role`, `Course` (Môn học), `Class` (Lớp học)\[cite: 480].

\- \[cite\_start]Bảng giao dịch học tập: `Schedule` (Lịch học), `Attendance` (Điểm danh), `Grade` (Điểm số)\[cite: 480].

\- \[cite\_start]Bảng trung gian (Junction): `Enrollment` (Sinh viên - Lớp)\[cite: 480].



\### 3.2. MongoDB (Document Data)

\[cite\_start]Dùng cho dữ liệu phi cấu trúc, lưu lượng lớn\[cite: 146].

\- \[cite\_start]`messages`: Lưu trữ nội dung Chat 1-1\[cite: 482].

\- \[cite\_start]`notifications`: Lưu thông báo theo từng user (trạng thái read/unread)\[cite: 482].

\- \[cite\_start]`activity\_logs`: Audit logs hệ thống\[cite: 482].



\### 3.3. Redis (Caching)

\- \[cite\_start]Lưu trữ phiên đăng nhập (Session JWT/Refresh token)\[cite: 283, 316].

\- \[cite\_start]Cache dữ liệu truy cập thường xuyên (ví dụ: thời khóa biểu) để giảm tải cho PostgreSQL trong giờ cao điểm\[cite: 148, 316].



\## 4. THUẬT TOÁN \& LOGIC NGHIỆP VỤ LÕI (CORE BUSINESS LOGIC)



\### 4.1. Thuật toán Điểm danh Thông minh (Smart Attendance)

\[cite\_start]Yêu cầu \*\*bắt buộc thỏa mãn đồng thời 2 lớp\*\* để chống gian lận\[cite: 204, 205]:

1\. \*\*Dynamic QR Code (Lớp 1):\*\*

&#x20;  - \[cite\_start]Server tạo token ngẫu nhiên thay đổi mỗi \*\*5 giây\*\*\[cite: 129, 484].

&#x20;  - \[cite\_start]Payload của QR phải chứa: `session\_id`, `timestamp`\[cite: 484].

&#x20;  - \[cite\_start]Ký mã bằng `HMAC-SHA256` với secret key của server\[cite: 485].

2\. \*\*GPS Geo-fencing (Lớp 2):\*\*

&#x20;  - \[cite\_start]Khi quét QR, Mobile App gửi tọa độ GPS hiện tại lên server\[cite: 487].

&#x20;  - \[cite\_start]Bắt buộc triển khai \*\*công thức Haversine\*\* để tính khoảng cách giữa tọa độ Sinh viên và tọa độ Phòng học\[cite: 308, 488].

&#x20;  - \[cite\_start]Ngưỡng hợp lệ (Threshold): `≤ 50 mét`\[cite: 129, 489].



\### 4.2. Kiến trúc Luồng Sự Kiện (Kafka + Socket.IO)

\[cite\_start]Áp dụng cho tính năng Thông báo khẩn\[cite: 464, 490]:

1\. \[cite\_start]API nhận request tạo thông báo từ Giảng viên\[cite: 390, 391].

2\. \[cite\_start]Lưu vào MongoDB (`notifications`)\[cite: 392].

3\. \[cite\_start]Publish event lên Kafka topic: `class-notifications`\[cite: 392].

4\. \[cite\_start]Kafka Consumer đọc event và xác định danh sách sinh viên đích\[cite: 394].

5\. \[cite\_start]Đẩy thông báo qua `Socket.IO` cho User đang online\[cite: 396].

6\. \[cite\_start]Kích hoạt `Firebase FCM` cho User đang offline (Push Notification)\[cite: 398].



\## 5. QUY TẮC DÀNH CHO AI (AI CODING DIRECTIVES)

1\. \[cite\_start]\*\*Always TypeScript:\*\* Mọi logic code (React Native, Next.js, NestJS) đều phải dùng TypeScript\[cite: 142].

2\. \[cite\_start]\*\*NestJS Architecture:\*\* Tuân thủ mô hình Controller - Service - Module\[cite: 141]. \[cite\_start]Validate dữ liệu đầu vào tại tầng Controller\[cite: 281].

3\. \*\*Security First:\*\*

&#x20;  - \[cite\_start]Mật khẩu phải hash bằng `bcrypt`\[cite: 274].

&#x20;  - \[cite\_start]Mã hóa toàn bộ giao tiếp qua HTTPS/TLS và sử dụng JWT với thời gian hết hạn ngắn\[cite: 273].

4\. \[cite\_start]\*\*Logic toán học thực tế:\*\* Không dùng code giả (mock) đối với tính toán khoảng cách GPS, phải sử dụng đúng công thức Haversine\[cite: 308, 488].



\*\*\*

