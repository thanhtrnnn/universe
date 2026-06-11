# UniVerse Desktop

Ứng dụng quản lý đại học số **UniVerse** – phiên bản Desktop Client cho Quản trị viên,
Giảng viên và Sinh viên. Hiện thực hóa toàn bộ scenario thiết kế (pha thiết kế động)
và biểu đồ lớp MVC trong `report/baocao_NEW.docx`.

## Kiến trúc

Mẫu **MVC mở rộng với DAO** (Chương 2.4.1 báo cáo):

| Tầng | Vai trò | Gói |
|------|---------|-----|
| **Model** (Entity) | Lớp thực thể nghiệp vụ | `com.universe.entity` |
| **View** (Frm) | Màn hình JavaFX (`Stage` và các `Node` nhúng trong dashboard) | `com.universe.view` |
| **Controller/DAO** | Truy cập CSDL qua JDBC, kế thừa lớp `DAO` | `com.universe.dao` |

Hạ tầng bổ sung (theo yêu cầu tích hợp):

- **PostgreSQL** – lưu trữ chính qua JDBC.
- **Redis** (`util/RedisUtil`) – lưu token phiên đăng nhập (`session:{userId}`, TTL 900s).
- **Kafka** (`util/KafkaUtil` + `consumer/NotificationConsumer`) – luồng gửi thông báo:
  `NotificationDAO.sendNotification()` publish lên topic `universe.notifications`,
  consumer đọc và ghi `tblNotification`.

## 10 chức năng (khớp scenario thiết kế)

| # | Chức năng | Actor | View → DAO → Entity | Mục báo cáo |
|---|-----------|-------|---------------------|-------------|
| 1 | Đăng nhập + sửa thông tin người dùng | Admin | LoginFrm/UserManageFrm/EditUserFrm → UserDAO → User | 4.3.2.1 |
| 2 | Gửi thông báo đến SV | Giảng viên | SendNotificationFrm → NotificationDAO (Kafka) | 4.3.2.2 |
| 3 | Sửa lịch học | Admin | ScheduleManageFrm/EditScheduleFrm → ScheduleDAO | 4.3.2.3 |
| 4 | Thêm học phần | Admin | CourseFrm/CreateCourseFrm → CourseDAO | 4.3.2.4 |
| 5 | Sửa lớp học phần | Admin | ClassSectionFrm/UpdateClassSectionFrm → ClassSectionDAO | 4.3.2.5 |
| 6 | Đăng ký lớp học phần | Sinh viên | SearchCourseRegistrationFrm/ClassSectionRegistrationFrm → CourseRecordDAO | 4.3.2.6 |
| 7 | Tạo/tắt mã QR điểm danh | Giảng viên | QRCodeFrm → QRCodeDAO/ClassSessionDAO | 4.3.1.7 |
| 8 | Quản lý điểm danh thủ công | Giảng viên | AttendanceManageFrm → AttendanceDAO | 4.3.1.8 |
| 9 | Xem điểm | Sinh viên | ViewGradeFrm → CourseRecordDAO | 4.3.1.9 |
| 10 | Nhập/sửa điểm | Giảng viên | GradeEntryFrm → CourseRecordDAO | 4.3.1.10 |

> Các chức năng 7–10 trong docx dùng tên lớp theo quy ước React cũ
> (`QRSessionPage`, `AttendanceController`…); ở codebase này đã **chuẩn hoá**
> về quy ước Java Swing + DAO (`QRCodeFrm`, `AttendanceDAO`…) cho nhất quán.

## Cơ sở dữ liệu

12 bảng (Chương 4.2): `tblUser` (+ kế thừa `tblAdmin`/`tblLecturer`/`tblStudent`),
`tblCourse`, `tblClassSection`, `tblSchedule`, `tblClassSession`, `tblQRCode`,
`tblAttendance`, `tblCourseRecord`, `tblNotification`. Xem [`schema.sql`](schema.sql).
Điểm thành phần lưu trực tiếp ở `tblCourseRecord` (`score1/score2/score3/examScore`).

## Yêu cầu

- Java 17+ (đã build/thử với Temurin 26)
- Maven 3.8+
- Docker (để chạy PostgreSQL + Redis + Kafka + Zookeeper)

## Chạy thử

```bash
# 1. Bật hạ tầng (PostgreSQL nạp schema.sql + demo-seed.sql)
docker compose up -d

# 2. Biên dịch
mvn compile

# 3. Chạy ứng dụng (đã nhúng sẵn NotificationConsumer ở thread nền)
mvn exec:java -Dexec.mainClass="com.universe.Main"

#    Hoặc đóng gói jar chạy được rồi chạy:
mvn package
java -jar target/universe-desktop.jar
```

Có thể chạy consumer Kafka như tiến trình riêng:

```bash
mvn exec:java -Dexec.mainClass="com.universe.consumer.NotificationConsumer"
```

## Tài khoản mẫu (seed data)

| Vai trò | Tên đăng nhập | Mật khẩu |
|---------|---------------|----------|
| Quản trị viên | `admin` | `123456` |
| Giảng viên | `lecturer` | `123456` |
| Sinh viên | `student` | `123456` |

## Cấu hình

Sửa kết nối DB/Redis/Kafka và Mobile API tại
[`src/main/resources/app.properties`](src/main/resources/app.properties).

Khi tạo QR điểm danh, desktop không dùng định vị Windows. Giảng viên nhấn
`Lấy vị trí bằng điện thoại`, quét QR bằng app Android và desktop tự nhận GPS qua
`mobile.api.base.url` (mặc định `http://localhost:8080/api/`).

## Cấu trúc mã nguồn

```
src/main/java/com/universe/
├── Main.java                 # điểm khởi động
├── db/DBConnection.java      # JDBC connection
├── util/                     # AppConfig, RedisUtil, KafkaUtil
├── consumer/                 # NotificationConsumer (Kafka → DB)
├── entity/                   # 12 lớp Model
├── dao/                      # DAO (base) + 9 DAO nghiệp vụ
└── view/                     # các màn hình JavaFX theo vai trò
```
