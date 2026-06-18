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
> (đã được `.gitignore`, **không commit**). Máy khác clone về phải tự điền lại, và đặt `do-kafka-ca.crt`
> vào `universe-desktop/src/main/resources/` **trước khi build** để cert được nhúng vào jar.

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

### Đóng gói `.exe` portable (tuỳ chọn — gửi cho máy KHÔNG cài Java)
Dùng `jpackage` (có sẵn trong JDK, không cần cài thêm):
```powershell
cd D:\Code\Mobile\universe\universe-desktop
.\mvnw.cmd -B package -DskipTests                 # 1) build fat jar (đã nhúng CA)
mkdir build-input 2>$null; copy target\universe-desktop.jar build-input
jpackage --type app-image --name "UniVerse Desktop" --input build-input `
  --main-jar universe-desktop.jar --main-class com.universe.Launcher `
  --dest dist --java-options "--enable-native-access=ALL-UNNAMED"
```
Kết quả: `dist\UniVerse Desktop\` chứa `UniVerse Desktop.exe` + JRE nhúng (~184 MB). Nén **cả thư mục** rồi gửi;
máy khác **Extract All** → chạy `.exe`, không cần cài Java.

> ⚠️ Giữ nguyên `.exe` cạnh hai thư mục `app\` và `runtime\` — tách lẻ `.exe` sẽ lỗi `Failed to launch JVM`.
> Bản đóng gói **chứa secret** (app.properties + CA trong jar) nên chỉ gửi nội bộ, **đừng public**.

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
   - Khi khởi động, console (cửa sổ chạy jar) hiện:
     ```
     [NotificationConsumer] Kafka bật - khởi động consumer nền, lắng nghe topic universe.notifications @ ...
     [NotificationConsumer] Đã kết nối broker & subscribe topic universe.notifications - sẵn sàng nhận thông báo.
     ```
   - Sau khi gửi, console hiện tiếp:
     ```
     [NotificationConsumer] Đã ghi thông báo NTF... cho user ...
     ```
     ⇒ Kafka (DigitalOcean) gửi–nhận OK.
   - Nếu thấy `Kafka tắt` → `kafka.enabled=false`. Nếu thấy `Không khởi tạo được Kafka producer` → thiếu/sai
     CA `do-kafka-ca.crt`; app **vẫn chạy bình thường** và ghi thông báo thẳng vào DB (không treo).

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

### Các file env / cấu hình quan trọng

| File | Nội dung | Commit? |
|------|----------|---------|
| `NOTE.md` | **Tất cả** thông tin kết nối: Render Postgres, Redis Cloud, DigitalOcean Kafka (host/user/password) | ❌ gitignore |
| `universe-desktop/src/main/resources/app.properties` | Cấu hình DB / Redis / Kafka / API cho **desktop** (chứa secret) | ❌ gitignore |
| `universe-desktop/src/main/resources/do-kafka-ca.crt` | Chứng chỉ CA của DigitalOcean Kafka (TLS) — **phải đặt vào `resources/` để được đóng gói vào jar** (giúp chạy trên máy khác) | ❌ gitignore (`*.crt`) |
| `universe-android/gradle.properties` | `UNIVERSE_API_BASE_URL` — URL backend cho app Android | ✅ commit (không phải secret) |
| Render Dashboard → mobile-api → **Environment** | Biến môi trường backend (`DATABASE_URL`, …) cấu hình trực tiếp trên Render | — (trên Render) |

> 🔑 **Lấy secret chi tiết** (mật khẩu DB/Redis/Kafka, file `do-kafka-ca.crt`, biến env trên Render):
> liên hệ **@BidenJr**. Các file trên đã được `.gitignore` nên repo **không** chứa giá trị thật.

### Mẫu `app.properties`

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
kafka.sasl.mechanism=SCRAM-SHA-256
kafka.sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="..." password="...";
kafka.ssl.truststore.type=PEM
# CA nhúng trong jar (đặt file vào src/main/resources/) -> .exe/jar chạy được trên MỌI máy.
kafka.ssl.truststore.resource=do-kafka-ca.crt
# (Tuỳ chọn) đường dẫn tuyệt đối CHỈ dùng trên máy dev; máy khác để trống cũng được vì đã có bundle ở trên.
kafka.ssl.truststore.location=D:/Code/Mobile/universe/universe-desktop/do-kafka-ca.crt
```

> 🔌 **Cơ chế CA portable:** code đọc `truststore.location` nếu file tồn tại (máy dev); nếu không, tự trích
> `do-kafka-ca.crt` từ trong jar ra file tạm. Nhờ vậy bản `.exe`/jar mang sang máy khác **không còn lỗi
> `Failed to launch JVM`** do thiếu đường dẫn tuyệt đối. Nếu Kafka vẫn init lỗi, app tự ghi thẳng DB thay vì treo.

App Android đọc URL backend từ `universe-android/gradle.properties` (`UNIVERSE_API_BASE_URL`).

---

## 5. Khắc phục sự cố

| Triệu chứng | Nguyên nhân & cách xử lý |
|-------------|--------------------------|
| `NoClassDefFoundError: Stage` | Đang chạy `javafx:run`. Dùng fat jar `java -jar target\universe-desktop.jar`. |
| `Failed to launch JVM` khi chạy `.exe`/jar trên **máy khác** | Do cũ: `kafka.ssl.truststore.location` trỏ đường dẫn tuyệt đối không có trên máy đó → `KafkaUtil` crash ở static init. **Đã sửa**: CA nhúng trong jar (`resources/do-kafka-ca.crt`), tự trích khi chạy; producer init không còn làm chết app. Chỉ cần **rebuild lại** jar/exe. |
| Kafka `AUTHENTICATION_FAILED` / `getSubject is not supported` | kafka-clients cũ trên JDK mới — đã nâng lên 3.9.1. Nếu vẫn lỗi: kiểm tra mật khẩu/đổi `kafka.sasl.mechanism` sang `SCRAM-SHA-512`. |
| Kafka lỗi topic | Tạo topic `universe.notifications` trên DigitalOcean (managed Kafka tắt auto-create). |
| Đăng nhập lỗi kết nối | Kiểm tra API Render còn sống: mở `https://universe-api-sgp7.onrender.com/api/health` (Render free "ngủ" sau 15 phút, lần gọi đầu chậm ~30s). |
| Desktop đơ khi đổi tab | Đảm bảo đã build lại jar sau khi thêm HikariCP. |
| Android không ping | Kiểm tra đã cấp quyền Thông báo; nhớ baseline ở lần mở đầu; đợi ≤15 phút hoặc mở lại app. |
