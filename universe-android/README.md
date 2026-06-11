# UniVerse Android

Hướng dẫn chạy APK trên điện thoại Android kết nối cùng Wi-Fi với PC.

## Chuẩn bị

- PC đã cài Docker Desktop, Java 17 và Maven.
- Điện thoại và PC kết nối cùng một mạng Wi-Fi.
- APK nằm tại:

```text
app\build\outputs\apk\debug\app-debug.apk
```

## 1. Khởi động PostgreSQL

Mở Docker Desktop và chờ Docker hoạt động.

Mở PowerShell:

```powershell
cd D:\Project\universe\universe-desktop
docker compose up -d postgres
docker compose ps postgres
```

PostgreSQL đã sẵn sàng khi container `universe-postgres` có trạng thái `Up`.

## 2. Khởi động API

Mở một cửa sổ PowerShell khác:

```powershell
cd D:\Project\universe\universe-android
.\run-api.ps1
```

Giữ cửa sổ này mở trong khi sử dụng app.

Kiểm tra API trên PC bằng trình duyệt:

```text
http://localhost:8080/api/health
```

Kết quả đúng:

```json
{"status":"ok"}
```

## 3. Lấy địa chỉ API cho điện thoại

Trong PowerShell:

```powershell
cd D:\Project\universe\universe-android
.\phone-setup.ps1
```

Script sẽ in URL tương tự:

```text
http://192.168.1.20:8080/api/
```

Ghi lại URL được in trên máy của bạn.

Không dùng các địa chỉ sau trên điện thoại thật:

```text
http://localhost:8080/api/
http://127.0.0.1:8080/api/
http://10.0.2.2:8080/api/
```

## 4. Mở Windows Firewall

Chỉ cần thực hiện bước này một lần.

Mở PowerShell bằng quyền `Run as administrator`:

```powershell
cd D:\Project\universe\universe-android
.\open-firewall.ps1
```

Script chỉ mở cổng API `8080` trên mạng `Private`.

## 5. Cài và đăng nhập app

1. Chuyển `app-debug.apk` sang điện thoại và cài đặt.
2. Mở app UniVerse.
3. Nhập một trong hai tài khoản:

```text
Sinh viên: student / 123456
Giảng viên: lecturer / 123456
```

Nhập `Địa chỉ API` lấy ở bước 3, ví dụ:

```text
http://192.168.1.20:8080/api/
```

4. Nhấn `Kiểm tra kết nối API`.
5. Khi báo kết nối thành công, nhấn `Đăng nhập`.

Tài khoản sinh viên mở giao diện học tập và quét QR điểm danh. Tài khoản giảng
viên mở giao diện riêng gồm tổng quan lớp, lịch giảng dạy, điểm danh, nhập điểm,
gửi thông báo và đồng bộ GPS cho desktop.

## 6. Giảng viên tự lấy vị trí phòng học

Không cần cài ứng dụng định vị trên desktop. Desktop nhận GPS từ điện thoại qua
Mobile API.

1. Giữ PostgreSQL và `run-api.ps1` đang chạy.
2. Mở UniVerse Desktop, đăng nhập giảng viên.
3. Vào `Tạo Mã QR Điểm danh`, chọn lớp và buổi học.
4. Nhấn `Lấy vị trí bằng điện thoại`. Desktop hiển thị QR dùng một lần trong 2 phút.
5. Trên điện thoại, đăng nhập tài khoản giảng viên `lecturer / 123456`.
6. Nhấn `Đồng bộ GPS cho UniVerse Desktop`, sau đó quét QR trên desktop.
7. Cấp quyền `Vị trí chính xác` và giữ điện thoại tại vị trí
   giảng viên đứng.
8. Khi GPS đạt sai số tối đa 25 m, điện thoại tự gửi tọa độ lên API. Desktop tự nhận,
   điền vĩ độ/kinh độ và báo sẵn sàng.
9. Trên desktop, nhấn `Phát hành QR động`.

Không cần chép tọa độ và không cần nhập tay.

## Nếu không kết nối được

Kiểm tra lần lượt:

1. PC và điện thoại có cùng Wi-Fi không.
2. Wi-Fi có phải mạng Guest không. Mạng Guest thường chặn các thiết bị.
3. Cửa sổ `run-api.ps1` có còn hoạt động không.
4. Docker và container PostgreSQL có đang chạy không.
5. Windows Firewall đã cho phép cổng `8080` chưa.
6. VPN trên PC hoặc điện thoại đã được tắt chưa.
7. IP của PC có thay đổi không. Chạy lại:

```powershell
.\phone-setup.ps1
```

Thử mở URL này bằng trình duyệt trên điện thoại:

```text
http://IP_CUA_PC:8080/api/health
```

Nếu trình duyệt trả về `{"status":"ok"}`, app sẽ kết nối được.

## Build lại APK

Mở project `D:\Project\universe\universe-android` bằng Android Studio, sau đó:

```text
Build -> Build Bundle(s) / APK(s) -> Build APK(s)
```

APK mới được tạo tại:

```text
app\build\outputs\apk\debug\app-debug.apk
```

Hoặc build bằng PowerShell:

```powershell
cd D:\Project\universe\universe-android
.\build-debug.ps1
```

## Lưu ý

- APK không kết nối trực tiếp tới PostgreSQL.
- PC phải chạy PostgreSQL và API trong lúc điện thoại sử dụng app.
- Không mở cổng PostgreSQL `5433` cho điện thoại.
- Chức năng QR hoạt động khi UniVerse Desktop và mobile API dùng cùng database.
- Điểm danh dùng geofence cố định 50 m và yêu cầu GPS điện thoại có sai số
  không quá 50 m.
- Trên điện thoại, phải cấp quyền `Vị trí chính xác`, không chọn vị trí gần đúng.
- Tâm geofence chỉ được nhận từ GPS điện thoại giảng viên khi sai số không quá 25 m.
- Desktop và điện thoại truyền vị trí qua Mobile API, không kết nối trực tiếp.
