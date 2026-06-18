# UniVerse — Hướng dẫn Chạy & Test

Hệ thống quản lý đại học số gồm 3 phần:

| Thành phần | Vị trí | Vai trò |
|------------|--------|---------|
| **mobile-api** | `universe-android/mobile-api` | Backend REST (Java), **đã deploy** trên Render |
| **universe-desktop** | `universe-desktop` | App quản trị (JavaFX) cho Admin/Giảng viên/Sinh viên |
| **universe-android** | `universe-android` | App Android cho Sinh viên (quét QR điểm danh, nhận thông báo) |

Hạ tầng dùng chung (miễn phí, không cần Docker):
- **PostgreSQL** → Render (Singapore)
- **Redis** → Redis Cloud (Free 30MB) — lưu session token
- **Kafka** → DigitalOcean Managed Kafka — luồng gửi thông báo
- **API** → `https://universe-api-sgp7.onrender.com/api/`

> ⚠️ Thông tin kết nối/mật khẩu nằm trong `NOTE.md` và `universe-desktop/src/main/resources/app.properties`
> (đã được `.gitignore`, **không commit**). Máy khác clone về phải tự điền lại.

---

## 1. Yêu cầu

- **JDK 17+** (đã test với JDK 24). Kiểm tra: `java -version`
- Không cần cài Maven — đã có **Maven Wrapper** (`mvnw.cmd`).
- Để build Android: dùng **Gradle Wrapper** (`gradlew.bat`) sẵn trong repo.

---

## 2. Chạy & Test — Desktop

### Chạy
```powershell
cd D:\Code\Mobile\universe\universe-desktop
.\run.bat
```
Hoặc thủ công:
```powershell
cd D:\Code\Mobile\universe\universe-desktop
.\mvnw.cmd clean package          # build fat jar
java -jar target\universe-desktop.jar
```

> ❌ **Đừng dùng `mvnw javafx:run`** — plugin này lỗi trên JDK mới (`NoClassDefFoundError: Stage`).
> Luôn chạy bằng **fat jar** như trên.

### Tài khoản mẫu
| Vai trò | Username | Password |
|---------|----------|----------|
| Quản trị viên | `admin` | `123456` |
| Giảng viên | `lecturer` | `123456` |
| Sinh viên | `student` | `123456` |

### Checklist test
1. **Đăng nhập** `admin/123456` → vào được dashboard ⇒ Postgres + Redis OK.
2. **Chuyển tab** (Quản lý người dùng / học phần / lớp / lịch) → mượt, không đơ ⇒ connection pool (HikariCP) hoạt động.
3. **Gửi thông báo** (đăng nhập `lecturer` → "Gửi thông báo" → chọn lớp/sinh viên → Gửi).
   - Trên console (cửa sổ chạy jar) thấy dòng:
     ```
     [NotificationConsumer] Đã ghi thông báo NTF... cho user ...
     ```
     ⇒ Kafka (DigitalOcean) gửi–nhận OK.
   - Nếu **không** thấy dòng đó mà thấy `Kafka tắt` → kiểm tra `kafka.enabled=true` trong `app.properties`.

---

## 3. Chạy & Test — Android

### Build APK
```powershell
cd D:\Code\Mobile\universe\universe-android
.\gradlew.bat assembleDebug
```
APK ra ở:
```
app\build\outputs\apk\debug\app-debug.apk
```

### Cài lên điện thoại
- **Cách 1 (cáp USB, đã bật USB debugging):**
  ```powershell
  adb install -r app\build\outputs\apk\debug\app-debug.apk
  ```
- **Cách 2:** copy file `app-debug.apk` vào điện thoại rồi mở để cài (cho phép "cài từ nguồn không xác định").

### Checklist test
1. Mở app → **đăng nhập** `student/123456` ⇒ kết nối API Render OK.
2. App hỏi quyền **Thông báo** → bấm **Cho phép** (Android 13+).
3. **Quét QR điểm danh**: bấm Scan, cho quyền Camera + Vị trí chính xác, quét QR do giảng viên tạo trên desktop.
4. **Test push notification** 🔔:
   - Lần mở app đầu tiên chỉ **lập baseline** (không ping — để khỏi dội thông báo cũ).
   - Từ **desktop** (lecturer) gửi 1 thông báo cho chính sinh viên đó.
   - **Mở lại app** (kích hoạt kiểm tra ngay) hoặc đợi tối đa **15 phút** → điện thoại hiện thông báo.

> WorkManager kiểm tra nền mỗi 15 phút (giới hạn hệ thống) nên **không tức thì**.
> Muốn push tức thì cả khi app đóng cần tích hợp **FCM (Firebase)** — chưa làm trong bản này.

---

## 4. Cấu hình (khi đổi máy / đổi dịch vụ)

Sửa `universe-desktop/src/main/resources/app.properties`:

```properties
# PostgreSQL (Render - External URL + SSL)
db.url=jdbc:postgresql://<host>.singapore-postgres.render.com/<db>?sslmode=require
db.user=...
db.password=...

# Redis (Redis Cloud)
redis.host=...
redis.port=...
redis.password=...

# Kafka (DigitalOcean Managed - SASL_SSL + SCRAM-SHA-256 + CA PEM)
kafka.enabled=true
kafka.bootstrap=<host>:<port>
kafka.sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="..." password="...";
kafka.ssl.truststore.location=D:/Code/Mobile/universe/universe-desktop/do-kafka-ca.crt
```

App Android đọc URL backend từ `universe-android/gradle.properties` (`UNIVERSE_API_BASE_URL`).

---

## 5. Khắc phục sự cố

| Triệu chứng | Nguyên nhân & cách xử lý |
|-------------|--------------------------|
| `NoClassDefFoundError: Stage` | Đang chạy `javafx:run`. Dùng fat jar `java -jar target\universe-desktop.jar`. |
| Kafka `AUTHENTICATION_FAILED` / `getSubject is not supported` | kafka-clients cũ trên JDK mới — đã nâng lên 3.9.1. Nếu vẫn lỗi: kiểm tra mật khẩu/đổi `kafka.sasl.mechanism` sang `SCRAM-SHA-512`. |
| Kafka lỗi topic | Tạo topic `universe.notifications` trên DigitalOcean (managed Kafka tắt auto-create). |
| Đăng nhập lỗi kết nối | Kiểm tra API Render còn sống: mở `https://universe-api-sgp7.onrender.com/api/health` (Render free "ngủ" sau 15 phút, lần gọi đầu chậm ~30s). |
| Desktop đơ khi đổi tab | Đảm bảo đã build lại jar sau khi thêm HikariCP. |
| Android không ping | Kiểm tra đã cấp quyền Thông báo; nhớ baseline ở lần mở đầu; đợi ≤15 phút hoặc mở lại app. |
