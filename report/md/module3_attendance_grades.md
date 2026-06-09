# MODULE 3: ĐIỂM DANH & HỌC VỤ
> Phụ trách: **Trần Xuân Thành**
> Công nghệ giao diện: HTML (React / Next.js)
> Phạm vi: UC08–UC11

---

## I.1. Mô hình nghiệp vụ bằng UML – Module 3

### (a) UC08 – Tạo/tắt mã QR điểm danh

```plantuml
@startuml
left to right direction
skinparam linetype ortho
skinparam packageStyle rectangle
title Biểu đồ Use Case – UC08: Tạo/tắt mã QR điểm danh

actor "Giảng viên" as GV

rectangle "Education System" {
  usecase "Generate QR" as UC08a
  usecase "Deactivate QR" as UC08b
  usecase "Lecturer Login" as UCLogin
  usecase "Search class section" as UCSearch
  usecase "Search class section by id" as UCSearchId
  usecase "Search class section by name" as UCSearchName

  UC08a ..> UCLogin : <<include>>
  UC08a ..> UCSearch : <<include>>
  UC08b ..> UCLogin : <<include>>
  UC08b ..> UCSearch : <<include>>
  UCSearch <|-- UCSearchId
  UCSearch <|-- UCSearchName
}

GV --> UC08a
GV --> UC08b
@enduml
```

**Mô tả UC:**
- **Generate QR:** GV tạo mã QR điểm danh cho một buổi học cụ thể, kèm tọa độ GPS và thời hạn sử dụng.
- **Deactivate QR:** GV tắt mã QR trước khi hết hạn; các SV chưa điểm danh tự động bị ghi nhận vắng.
- **Search class section:** GV tìm kiếm lớp học phần (theo mã hoặc tên) để phục vụ thao tác.

---

### (b) UC09 – Quản lý điểm danh

```plantuml
@startuml
left to right direction
skinparam linetype ortho
skinparam packageStyle rectangle
title Biểu đồ Use Case – UC09: Quản lý điểm danh

actor "Giảng viên" as GV

rectangle "Education System" {
  usecase "Manage attendance" as UC09
  usecase "View attendance" as UC09a
  usecase "Lecturer Login" as UCLogin
  usecase "Mark manual attendance" as UC09b

  UC09 ..> UC09a : <<include>>
  UC09 ..> UCLogin : <<include>>
  UC09b ..> UC09 : <<extend>>
}

GV --> UC09
@enduml
```

**Mô tả UC:**
- **Manage attendance:** GV xem và chỉnh sửa kết quả điểm danh của SV theo từng buổi học.
- **View attendance:** GV xem trạng thái điểm danh toàn bộ SV trong một buổi học.
- **Mark manual attendance:** GV điểm danh thủ công hoặc chỉnh sửa trạng thái điểm danh đã có cho một SV cụ thể.

---

### (c) UC10 – Xem điểm

```plantuml
@startuml
left to right direction
skinparam linetype ortho
skinparam packageStyle rectangle
title Biểu đồ Use Case – UC10: Xem điểm

actor "Sinh viên" as SV

rectangle "Education System" {
  usecase "View grade" as UC10
  usecase "Student Login" as UCLogin
  usecase "View grade by semester" as UC10a

  UC10 ..> UCLogin : <<include>>
  UC10a ..> UC10 : <<extend>>
}

SV --> UC10
@enduml
```

**Mô tả UC:**
- **View grade:** SV xem các đầu điểm và điểm tổng kết của các học phần đã đăng ký.
- **View grade by semester:** SV xem chi tiết điểm theo kỳ học.

---

### (d) UC11 – Nhập/sửa điểm

```plantuml
@startuml
left to right direction
skinparam linetype ortho
skinparam packageStyle rectangle
title Biểu đồ Use Case – UC11: Nhập/sửa điểm

actor "Giảng viên" as GV

rectangle "Education System" {
  usecase "Enter grade" as UC11a
  usecase "Edit grade" as UC11b
  usecase "Lecturer Login" as UCLogin
  usecase "Search class section" as UCSearch
  usecase "Search class section by id" as UCSearchId
  usecase "Search class section by name" as UCSearchName

  UC11a ..> UCLogin : <<include>>
  UC11a ..> UCSearch : <<include>>
  UC11b ..> UCLogin : <<include>>
  UC11b ..> UCSearch : <<include>>
  UCSearch <|-- UCSearchId
  UCSearch <|-- UCSearchName
}

GV --> UC11a
GV --> UC11b
@enduml
```

**Mô tả UC:**
- **Enter grade:** GV nhập điểm thành phần và điểm thi cho SV trong lớp phụ trách.
- **Edit grade:** GV chỉnh sửa điểm đã nhập trong thời gian cho phép.

---

## II.1. Mô hình hóa chức năng – Module 3

### Kịch bản UC08: Tạo/tắt mã QR điểm danh

| Trường | Nội dung |
|--------|---------|
| **Use case** | Tạo/tắt mã QR điểm danh |
| **Actor** | Giảng viên |
| **Tiền điều kiện** | Giảng viên đã đăng nhập; buổi học chưa mở điểm danh |
| **Hậu điều kiện** | Buổi học có danh sách điểm danh hoàn chỉnh cho tất cả sinh viên |
| **Kịch bản chính** | 1. Giảng viên chọn chức năng "Điểm danh" từ giao diện chính.<br>2. Hệ thống hiển thị giao diện tìm kiếm lớp học phần với ô nhập từ khóa và nút Tìm.<br>3. Giảng viên nhập từ khóa "INT1340" và nhấn nút Tìm.<br>4. Hệ thống hiển thị danh sách lớp học phần phù hợp:<br><table><tr><th>Mã lớp</th><th>Tên học phần</th><th>Phòng</th><th>Giờ</th></tr><tr><td>INT1340.01</td><td>Nhập môn CNPM</td><td>A101</td><td>07:30</td></tr><tr><td>INT1340.02</td><td>Nhập môn CNPM</td><td>B203</td><td>09:30</td></tr></table><br>5. Giảng viên chọn lớp INT1340.01.<br>6. Hệ thống hiển thị danh sách các buổi học của lớp INT1340.01.<br>7. Giảng viên chọn buổi học ngày hôm nay và nhấn "Tạo mã QR".<br>8. Hệ thống hiển thị mã QR điểm danh cho buổi học trên màn hình.<br>9. (Sinh viên điểm danh trong thời gian mã mở.)<br>10. Giảng viên nhấn "Tắt mã QR" sau khi kết thúc điểm danh.<br>11. Hệ thống đóng điểm danh, chốt danh sách và đánh dấu các sinh viên chưa điểm danh là "vắng".<br>12. Hệ thống thông báo "Đã đóng điểm danh – X sinh viên có mặt, Y sinh viên vắng". |
| **Ngoại lệ** | 7. Buổi học đã có mã QR đang hoạt động.<br>7.1 Hệ thống hiển thị thông báo "Buổi học đã mở điểm danh".<br>7.2 Giảng viên chọn buổi học khác (quay về Bước 6). |

---

### Kịch bản UC09: Quản lý điểm danh

| Trường | Nội dung |
|--------|---------|
| **Use case** | Quản lý điểm danh |
| **Actor** | Giảng viên |
| **Tiền điều kiện** | Giảng viên đã đăng nhập |
| **Hậu điều kiện** | Trạng thái điểm danh của sinh viên được cập nhật chính xác |
| **Kịch bản chính** | 1. Giảng viên chọn chức năng "Quản lý điểm danh" từ giao diện chính.<br>2. Hệ thống hiển thị giao diện tìm kiếm lớp học phần.<br>3. Giảng viên tìm và chọn lớp học phần INT1340.01.<br>4. Hệ thống hiển thị danh sách các buổi học của lớp.<br>5. Giảng viên chọn buổi học cần xem.<br>6. Hệ thống hiển thị danh sách sinh viên kèm trạng thái điểm danh của buổi học:<br><table><tr><th>MSSV</th><th>Họ tên</th><th>Trạng thái</th><th>Giờ điểm danh</th><th>Hình thức</th></tr><tr><td>B23DCAT120</td><td>Nguyễn Bá Hùng</td><td>Có mặt</td><td>07:35</td><td>QR</td></tr><tr><td>B23DCAT280</td><td>Trần Xuân Thành</td><td>Có mặt</td><td>07:38</td><td>QR</td></tr><tr><td>B23DCCN266</td><td>Phạm Thị Thiên Hà</td><td>Vắng</td><td>—</td><td>—</td></tr></table><br>7. Giảng viên chọn sinh viên Phạm Thị Thiên Hà để chỉnh trạng thái.<br>8. Hệ thống hiển thị form chỉnh sửa với dropdown trạng thái (vắng/có mặt/đi muộn) và ô nhập lý do.<br>9. Giảng viên chọn trạng thái "Có mặt", nhập lý do "Có giấy phép" và nhấn "Lưu".<br>10. Hệ thống cập nhật trạng thái điểm danh và thông báo "Cập nhật thành công". |
| **Ngoại lệ** | 9. Trạng thái mới trùng với trạng thái cũ.<br>9.1 Hệ thống hiển thị cảnh báo "Trạng thái không thay đổi".<br>9.2 Giảng viên chọn trạng thái khác (quay về Bước 8). |

---

### Kịch bản UC10: Xem điểm

| Trường | Nội dung |
|--------|---------|
| **Use case** | Xem điểm |
| **Actor** | Sinh viên |
| **Tiền điều kiện** | Sinh viên đã đăng nhập |
| **Hậu điều kiện** | Sinh viên xem được kết quả điểm số các môn đã đăng ký |
| **Kịch bản chính** | 1. Sinh viên chọn chức năng "Xem điểm" từ giao diện chính.<br>2. Hệ thống hiển thị danh sách lớp học đã đăng ký kèm trạng thái điểm:<br><table><tr><th>Mã lớp</th><th>Tên học phần</th><th>Học kỳ</th><th>Trạng thái</th></tr><tr><td>INT1340.01</td><td>Nhập môn CNPM</td><td>2024-1</td><td>Đã có điểm</td></tr><tr><td>INT1305.02</td><td>Lập trình Web</td><td>2024-1</td><td>Chưa có điểm</td></tr></table><br>3. Sinh viên chọn học kỳ "2024-1" để xem chi tiết.<br>4. Hệ thống hiển thị bảng chi tiết điểm các môn trong học kỳ:<br><table><tr><th>Tên học phần</th><th>Thường xuyên</th><th>Giữa kỳ</th><th>Cuối kỳ</th><th>Tổng kết</th><th>Xếp loại</th></tr><tr><td>Nhập môn CNPM</td><td>9.0</td><td>8.5</td><td>9.0</td><td>8.9</td><td>A</td></tr></table> |
| **Ngoại lệ** | 3. Học phần chưa có điểm.<br>3.1 Hệ thống hiển thị thông báo "Chưa có điểm" tại dòng học phần đó.<br>3.2 Sinh viên tiếp tục xem các môn khác. |

---

### Kịch bản UC11: Nhập/sửa điểm

| Trường | Nội dung |
|--------|---------|
| **Use case** | Nhập/sửa điểm |
| **Actor** | Giảng viên |
| **Tiền điều kiện** | Giảng viên đã đăng nhập; lớp học phần đang trong giai đoạn nhập điểm |
| **Hậu điều kiện** | Điểm số của sinh viên được lưu vào hệ thống |
| **Kịch bản chính** | 1. Giảng viên chọn chức năng "Nhập điểm" từ giao diện chính.<br>2. Hệ thống hiển thị giao diện tìm kiếm lớp học phần với ô nhập từ khóa và nút Tìm.<br>3. Giảng viên nhập từ khóa "INT1340" và nhấn Tìm.<br>4. Hệ thống hiển thị danh sách lớp học phần phù hợp:<br><table><tr><th>Mã lớp</th><th>Tên học phần</th><th>Số SV</th><th>Trạng thái</th></tr><tr><td>INT1340.01</td><td>Nhập môn CNPM</td><td>35</td><td>Đang nhập điểm</td></tr></table><br>5. Giảng viên chọn lớp INT1340.01.<br>6. Hệ thống hiển thị bảng sinh viên với các ô nhập điểm:<br><table><tr><th>MSSV</th><th>Họ tên</th><th>Thường xuyên</th><th>Giữa kỳ</th><th>Cuối kỳ</th><th>Tổng kết</th></tr><tr><td>B23DCAT120</td><td>Nguyễn Bá Hùng</td><td>8.5</td><td>7.0</td><td>[__]</td><td>—</td></tr><tr><td>B23DCAT280</td><td>Trần Xuân Thành</td><td>9.0</td><td>8.5</td><td>[__]</td><td>—</td></tr><tr><td>B23DCCN266</td><td>Phạm Thị Thiên Hà</td><td>9.5</td><td>9.0</td><td>[__]</td><td>—</td></tr></table><br>7. Giảng viên nhập điểm cuối kỳ cho từng sinh viên.<br>8. Hệ thống tính và hiển thị điểm tổng kết tương ứng.<br>9. Giảng viên nhấn "Xác nhận nộp điểm".<br>10. Hệ thống lưu điểm và thông báo "Nộp điểm thành công". |
| **Ngoại lệ** | 7. Điểm nhập ngoài thang [0, 10].<br>7.1 Hệ thống hiển thị lỗi "Điểm phải trong khoảng 0–10" ngay tại ô nhập.<br>7.2 Giảng viên nhập lại giá trị hợp lệ (quay về Bước 7).<br><br>9. Giảng viên sửa điểm ngoài thời gian cho phép.<br>9.1 Hệ thống hiển thị thông báo "Đã quá thời hạn sửa điểm".<br>9.2 Giảng viên liên hệ bộ phận đào tạo để được hỗ trợ. |

---

## II.2. Mô hình hóa lớp – Module 3

### Bước 1: Mô tả chức năng bằng đoạn văn xuôi

Giảng viên mở mã QR điểm danh cho một buổi học; hệ thống hiển thị mã QR trên giao diện để sinh viên quét. Khi giảng viên tắt mã QR, hệ thống chốt danh sách và ghi nhận sinh viên chưa điểm danh là vắng. Giảng viên có thể xem toàn bộ danh sách điểm danh theo buổi và điểm danh thủ công/chỉnh sửa trạng thái cho từng sinh viên. Giảng viên nhập điểm số cho từng loại bài kiểm tra (thường xuyên, giữa kỳ, cuối kỳ); hệ thống hiển thị điểm tổng kết. Sinh viên xem điểm theo môn học và theo học kỳ.

### Bước 2 + 3: Trích danh từ và đánh giá

```
▪ Mã QR điểm danh   → loại: cơ chế kỹ thuật tạm thời — không thành Entity lâu dài; lưu như thuộc tính của Attendance
▪ Buổi học          → lớp Schedule (đã định nghĩa ở Module 2)
▪ Lớp học phần      → lớp Class (đã định nghĩa ở Module 2)
▪ Sinh viên         → Actor = User với role = STUDENT
▪ Bản ghi điểm danh → lớp Attendance: scheduleId, studentId, sessionDate, status, method
▪ Trạng thái điểm danh → thuộc tính của Attendance (enum: present/absent/late)
▪ Hình thức điểm danh → thuộc tính của Attendance (enum: qr/manual) — khớp cột "hình thức" trong docx
▪ Điểm số           → lớp Grade: enrollmentId, type, name, score, maxScore, weight
▪ Loại bài kiểm tra → thuộc tính của Grade (enum: assignment/midterm/final)
▪ Trọng số          → thuộc tính của Grade.weight
▪ Điểm tổng kết     → giá trị tính toán từ các Grade, không lưu riêng
▪ Đăng ký học phần  → lớp Enrollment (đã định nghĩa ở Module 2)
▪ Tin nhắn          → loại: UC14 "Nhắn tin" đã bỏ khỏi phạm vi — không tạo lớp Message
```

**Các lớp giữ lại (mới trong Module 3):** `Attendance`, `Grade`
**Tái sử dụng từ module khác:** `Schedule`, `Class`, `Enrollment`, `User`

### Bước 4: Xác định quan hệ số lượng

- 1 Schedule (lịch học trong tuần) có nhiều ClassSession (buổi học cụ thể theo ngày) → Schedule – ClassSession: 1 – n
- 1 ClassSession gắn với 1 QRCode → ClassSession – QRCode: 1 – 1
- 1 Student (User) và 1 ClassSession có quan hệ n – n thông qua Attendance
- 1 Enrollment có nhiều Grade → Enrollment – Grade: 1 – n

### Bước 5: Sơ đồ thực thể

```plantuml
@startuml
title Biểu đồ thực thể – Module 3: Điểm danh & Học vụ

class Schedule {
  -classId
  -dayOfWeek
  -startTime
  -endTime
  -room
}

class ClassSession {
  -scheduleId
  -sessionDate
}

class QRCode {
  -classSessionId
  -token
  -isActive
}

class Attendance {
  -classSessionId
  -studentId
  -status
  -method
}

class Enrollment {
  -studentId
  -classId
  -status
}

class Grade {
  -enrollmentId
  -type
  -name
  -score
  -maxScore
  -weight
}

class User {
  -role
}

Schedule "1" -- "n" ClassSession : tạo buổi học
ClassSession "1" -- "1" QRCode : gắn mã QR
User "1" -- "n" Attendance : điểm danh (Student)
ClassSession "1" -- "n" Attendance : thuộc buổi học
Enrollment "1" -- "n" Grade : có điểm số
@enduml
```

---

## II.3. Sơ đồ lớp phân tích BCE – Module 3

### UC08 – Tạo/tắt mã QR

**Phân tích chi tiết chức năng Tạo/tắt mã QR:**
- GV vào giao diện → đề xuất lớp `QRSessionView` với:
  - `inClassSection` — ô nhập từ khóa tìm lớp học phần
  - `outsubListClass` — bảng kết quả lớp học phần (hiển thị và chọn)
  - `subGenerateQr` — nút "Tạo mã QR"
  - `subDeactivateQr` — nút "Tắt mã QR"
  - `outAttendanceList` — bảng danh sách sinh viên kèm trạng thái điểm danh
- GV nhập từ khóa và nhấn tìm → `QRSessionView` gọi `searchClassSection()` của lớp `Class`; kết quả hiển thị ở `outsubListClass`.
- GV chọn lớp, chọn buổi học và nhấn `subGenerateQr` → `QRSessionView` gọi `generateQr()` của lớp `Attendance`.
- Hệ thống hiển thị mã QR điểm danh → `QRSessionView` cập nhật giao diện.
- GV nhấn `subDeactivateQr` → `QRSessionView` gọi `deactivateQr()` của lớp `Attendance`.
- Hệ thống đóng điểm danh, ghi vắng cho SV chưa điểm danh → `outAttendanceList` cập nhật trạng thái.

```plantuml
@startuml
left to right direction
skinparam linetype ortho
skinparam packageStyle rectangle
title Biểu đồ lớp phân tích BCE – UC08: Tạo/tắt mã QR

package "Boundary" #DDEEFF {
  class QRSessionView {
    -inClassSection
    -outsubListClass
    -subGenerateQr
    -subDeactivateQr
    -outAttendanceList
  }
}

package "Entity" #FFF3CD {
  class Attendance {
    +generateQr()
    +deactivateQr()
  }
  class Class {
    +searchClassSection()
  }
}

QRSessionView --> Attendance
QRSessionView --> Class
@enduml
```

---

### UC09 – Quản lý điểm danh

**Phân tích chi tiết chức năng Quản lý điểm danh:**
- GV vào giao diện → đề xuất lớp `AttendanceManageView` với:
  - `inSession` — dropdown chọn buổi học
  - `outsubAttendance` — bảng danh sách SV kèm trạng thái điểm danh (hiển thị và chọn để sửa)
  - `subSaveManual` — nút "Lưu" trạng thái điểm danh thủ công
- GV chọn buổi học qua `inSession` → `AttendanceManageView` gọi `viewAttendance()` của lớp `Attendance`; kết quả hiển thị ở `outsubAttendance`.
- GV chọn SV trong `outsubAttendance` và chỉnh trạng thái → nhấn `subSaveManual` → `AttendanceManageView` gọi `markManualAttendance()` của lớp `Attendance`.
- Hệ thống lưu và thông báo cập nhật thành công → `outsubAttendance` làm mới trạng thái.

```plantuml
@startuml
left to right direction
skinparam linetype ortho
skinparam packageStyle rectangle
title Biểu đồ lớp phân tích BCE – UC09: Quản lý điểm danh

package "Boundary" #DDEEFF {
  class AttendanceManageView {
    -inSession
    -outsubAttendance
    -subSaveManual
  }
}

package "Entity" #FFF3CD {
  class Attendance {
    +viewAttendance()
    +markManualAttendance()
  }
}

AttendanceManageView --> Attendance
@enduml
```

---

### UC10 – Xem điểm

**Phân tích chi tiết chức năng Xem điểm:**
- SV vào giao diện xem điểm → đề xuất lớp `GradeView` với:
  - `outsubCourseList` — danh sách lớp học phần đã đăng ký kèm điểm tổng kết (hiển thị và chọn để xem chi tiết)
  - `inSemester` — bộ lọc học kỳ (dropdown)
  - `outGradeDetail` — bảng chi tiết các đầu điểm theo kỳ học
- SV mở trang → `GradeView` gọi `viewGrade()` của lớp `Grade`; kết quả hiển thị ở `outsubCourseList`.
- SV chọn học kỳ qua `inSemester` → `GradeView` gọi `viewGradeBySemester()` của lớp `Grade`; kết quả hiển thị ở `outGradeDetail`.

```plantuml
@startuml
left to right direction
skinparam linetype ortho
skinparam packageStyle rectangle
title Biểu đồ lớp phân tích BCE – UC10: Xem điểm

package "Boundary" #DDEEFF {
  class GradeView {
    -outsubCourseList
    -inSemester
    -outGradeDetail
  }
}

package "Entity" #FFF3CD {
  class Grade {
    +viewGrade()
    +viewGradeBySemester()
  }
}

GradeView --> Grade
@enduml
```

---

### UC11 – Nhập/sửa điểm

**Phân tích chi tiết chức năng Nhập/sửa điểm:**
- GV vào giao diện nhập điểm → đề xuất lớp `GradeEntryView` với:
  - `inClassSection` — ô nhập từ khóa tìm lớp học phần
  - `outsubListClass` — bảng kết quả lớp học phần (hiển thị và chọn)
  - `inoutGrades` — bảng nhập điểm (hiển thị điểm hiện tại và cho phép nhập/sửa inline)
  - `subSubmitGrades` — nút "Xác nhận nộp điểm"
- GV nhập từ khóa vào `inClassSection` và nhấn tìm → `GradeEntryView` gọi `searchClassSection()` của lớp `Class`; kết quả hiển thị ở `outsubListClass`.
- GV chọn lớp → hệ thống tải bảng SV vào `inoutGrades`.
- GV nhập/sửa điểm trong `inoutGrades` và nhấn `subSubmitGrades` → `GradeEntryView` gọi `enterGrade()` hoặc `editGrade()` của lớp `Grade`.
- Hệ thống lưu và thông báo thành công.

```plantuml
@startuml
left to right direction
skinparam linetype ortho
skinparam packageStyle rectangle
title Biểu đồ lớp phân tích BCE – UC11: Nhập/sửa điểm

package "Boundary" #DDEEFF {
  class GradeEntryView {
    -inClassSection
    -outsubListClass
    -inoutGrades
    -subSubmitGrades
  }
}

package "Entity" #FFF3CD {
  class Grade {
    +enterGrade()
    +editGrade()
  }
  class Class {
    +searchClassSection()
  }
}

GradeEntryView --> Grade
GradeEntryView --> Class
@enduml
```

---

## II.4. Biểu đồ tuần tự phân tích – Module 3

### Biểu đồ UC08: Tạo/tắt mã QR

**Kịch bản phiên bản 2 – UC08 Tạo/tắt mã QR**

1. Giảng viên chọn chức năng Điểm danh và truy cập giao diện `QRSessionView`.
2. Giảng viên nhập từ khóa tìm kiếm lớp học phần và nhấn Tìm.
3. Lớp `QRSessionView` gọi lớp `Class` để thực hiện tìm kiếm qua phương thức `searchClassSection`.
4. Lớp `Class` trả về danh sách lớp học phần phù hợp.
5. Lớp `QRSessionView` hiển thị danh sách lớp cho giảng viên.
6. Giảng viên chọn lớp INT1340.01 và chọn buổi học, sau đó nhấn "Tạo mã QR".
7. Lớp `QRSessionView` gọi lớp `Attendance` qua phương thức `generateQr`.
8. Lớp `Attendance` tạo mã QR và trả kết quả về `QRSessionView`.
9. Lớp `QRSessionView` hiển thị mã QR điểm danh cho giảng viên.
10. Giảng viên nhấn "Tắt mã QR" sau khi kết thúc điểm danh.
11. Lớp `QRSessionView` gọi lớp `Attendance` qua phương thức `deactivateQr`.
12. Lớp `Attendance` đóng điểm danh, ghi vắng cho SV chưa điểm danh và trả kết quả.
13. Lớp `QRSessionView` hiển thị thông báo đóng điểm danh và cập nhật danh sách.

```plantuml
@startuml
skinparam shadowing false
skinparam SequenceMessageAlign left
skinparam SequenceLifeLineBackgroundColor #7AD2FF
skinparam SequenceLifeLineBorderColor #000000
<style>
sequenceDiagram {
  Shadowing 0
  RoundCorner 0
  FontName "Arial"
  FontSize 10
  FontColor #000000
  boundary { BackgroundColor #7AD2FF; LineColor #000000 }
  entity { BackgroundColor #7AD2FF; LineColor #000000 }
  actor { BackgroundColor transparent; LineColor #000000 }
  lifeline { LineColor #000000; LineStyle 5-5 }
  arrow { LineColor #000000; LineThickness 1; FontSize 10 }
}
</style>
title UC08 – Tạo/tắt mã QR (Tuần tự Phân tích)

actor "Giảng viên" as GV
boundary QRSessionView
entity Class
entity Attendance

GV -> QRSessionView : 1: mở giao diện điểm danh
activate QRSessionView
GV -> QRSessionView : 2: nhập từ khóa + nhấn Tìm
QRSessionView -> Class : 3: gọi searchClassSection()
activate Class
Class --> QRSessionView : 4: trả về danh sách lớp
deactivate Class
QRSessionView --> GV : 5: hiển thị danh sách lớp học phần
GV -> QRSessionView : 6: chọn lớp INT1340.01, chọn buổi + nhấn "Tạo mã QR"
QRSessionView -> Attendance : 7: gọi generateQr()
activate Attendance
Attendance --> QRSessionView : 8: trả về mã QR
deactivate Attendance
QRSessionView --> GV : 9: hiển thị mã QR điểm danh
GV -> QRSessionView : 10: nhấn "Tắt mã QR"
QRSessionView -> Attendance : 11: gọi deactivateQr()
activate Attendance
Attendance --> QRSessionView : 12: trả về kết quả + danh sách đã cập nhật
deactivate Attendance
QRSessionView --> GV : 13: thông báo đóng điểm danh + hiển thị danh sách
deactivate QRSessionView
@enduml
```

---

### Biểu đồ UC09: Quản lý điểm danh

**Kịch bản phiên bản 2 – UC09 Quản lý điểm danh**

1. Giảng viên chọn chức năng Quản lý điểm danh và truy cập giao diện `AttendanceManageView`.
2. Lớp `AttendanceManageView` tự động tải danh sách lớp học phần của giảng viên.
3. Giảng viên chọn lớp INT1340.01 và chọn buổi học cần xem.
4. Lớp `AttendanceManageView` gọi lớp `Attendance` qua phương thức `viewAttendance`.
5. Lớp `Attendance` trả về danh sách sinh viên kèm trạng thái điểm danh.
6. Lớp `AttendanceManageView` hiển thị bảng điểm danh cho giảng viên.
7. Giảng viên chọn sinh viên Phạm Thị Thiên Hà để chỉnh sửa trạng thái.
8. Giảng viên chọn trạng thái "Có mặt", nhập lý do và nhấn "Lưu".
9. Lớp `AttendanceManageView` gọi lớp `Attendance` qua phương thức `markManualAttendance`.
10. Lớp `Attendance` cập nhật trạng thái và trả kết quả thành công.
11. Lớp `AttendanceManageView` hiển thị thông báo "Cập nhật thành công" và cập nhật bảng.

```plantuml
@startuml
skinparam shadowing false
skinparam SequenceMessageAlign left
skinparam SequenceLifeLineBackgroundColor #7AD2FF
skinparam SequenceLifeLineBorderColor #000000
<style>
sequenceDiagram {
  Shadowing 0
  RoundCorner 0
  FontName "Arial"
  FontSize 10
  FontColor #000000
  boundary { BackgroundColor #7AD2FF; LineColor #000000 }
  entity { BackgroundColor #7AD2FF; LineColor #000000 }
  actor { BackgroundColor transparent; LineColor #000000 }
  lifeline { LineColor #000000; LineStyle 5-5 }
  arrow { LineColor #000000; LineThickness 1; FontSize 10 }
}
</style>
title UC09 – Quản lý điểm danh (Tuần tự Phân tích)

actor "Giảng viên" as GV
boundary AttendanceManageView
entity Attendance

GV -> AttendanceManageView : 1: mở giao diện quản lý điểm danh
activate AttendanceManageView
GV -> AttendanceManageView : 2: chọn lớp INT1340.01, chọn buổi học
AttendanceManageView -> Attendance : 3: gọi viewAttendance()
activate Attendance
Attendance --> AttendanceManageView : 4: trả về danh sách điểm danh
deactivate Attendance
AttendanceManageView --> GV : 5: hiển thị bảng điểm danh theo buổi
GV -> AttendanceManageView : 6: chọn SV Phạm Thị Thiên Hà, chọn trạng thái mới + nhấn Lưu
AttendanceManageView -> Attendance : 7: gọi markManualAttendance()
activate Attendance
Attendance --> AttendanceManageView : 8: trả về kết quả thành công
deactivate Attendance
AttendanceManageView --> GV : 9: thông báo "Cập nhật thành công" + cập nhật bảng
deactivate AttendanceManageView
@enduml
```

---

### Biểu đồ UC10: Xem điểm

**Kịch bản phiên bản 2 – UC10 Xem điểm**

1. Sinh viên chọn chức năng Xem điểm và truy cập giao diện `GradeView`.
2. Lớp `GradeView` gọi lớp `Grade` qua phương thức `viewGrade`.
3. Lớp `Grade` trả về danh sách lớp đã đăng ký kèm trạng thái điểm.
4. Lớp `GradeView` hiển thị danh sách cho sinh viên.
5. Sinh viên chọn học kỳ "2024-1" để xem chi tiết.
6. Lớp `GradeView` gọi lớp `Grade` qua phương thức `viewGradeBySemester`.
7. Lớp `Grade` trả về bảng chi tiết điểm theo kỳ học.
8. Lớp `GradeView` hiển thị bảng chi tiết điểm cho sinh viên.

```plantuml
@startuml
skinparam shadowing false
skinparam SequenceMessageAlign left
skinparam SequenceLifeLineBackgroundColor #7AD2FF
skinparam SequenceLifeLineBorderColor #000000
<style>
sequenceDiagram {
  Shadowing 0
  RoundCorner 0
  FontName "Arial"
  FontSize 10
  FontColor #000000
  boundary { BackgroundColor #7AD2FF; LineColor #000000 }
  entity { BackgroundColor #7AD2FF; LineColor #000000 }
  actor { BackgroundColor transparent; LineColor #000000 }
  lifeline { LineColor #000000; LineStyle 5-5 }
  arrow { LineColor #000000; LineThickness 1; FontSize 10 }
}
</style>
title UC10 – Xem điểm (Tuần tự Phân tích)

actor "Sinh viên" as SV
boundary GradeView
entity Grade

SV -> GradeView : 1: mở giao diện xem điểm
activate GradeView
GradeView -> Grade : 2: gọi viewGrade()
activate Grade
Grade --> GradeView : 3: trả về danh sách lớp + trạng thái điểm
deactivate Grade
GradeView --> SV : 4: hiển thị danh sách lớp đã đăng ký
SV -> GradeView : 5: chọn học kỳ "2024-1"
GradeView -> Grade : 6: gọi viewGradeBySemester()
activate Grade
Grade --> GradeView : 7: trả về bảng chi tiết điểm theo kỳ
deactivate Grade
GradeView --> SV : 8: hiển thị bảng chi tiết điểm
deactivate GradeView
@enduml
```

---

### Biểu đồ UC11: Nhập/sửa điểm

**Kịch bản phiên bản 2 – UC11 Nhập/sửa điểm**

1. Giảng viên chọn chức năng Nhập điểm và truy cập giao diện `GradeEntryView`.
2. Giảng viên nhập từ khóa tìm kiếm lớp học phần và nhấn Tìm.
3. Lớp `GradeEntryView` gọi lớp `Class` qua phương thức `searchClassSection`.
4. Lớp `Class` trả về danh sách lớp học phần phù hợp.
5. Lớp `GradeEntryView` hiển thị danh sách lớp cho giảng viên.
6. Giảng viên chọn lớp INT1340.01.
7. Lớp `GradeEntryView` hiển thị bảng sinh viên với các ô nhập điểm.
8. Giảng viên nhập điểm cuối kỳ cho từng sinh viên.
9. Giảng viên nhấn "Xác nhận nộp điểm".
10. Lớp `GradeEntryView` gọi lớp `Grade` qua phương thức `enterGrade`.
11. Lớp `Grade` lưu điểm và trả kết quả thành công.
12. Lớp `GradeEntryView` hiển thị thông báo "Nộp điểm thành công".

```plantuml
@startuml
skinparam shadowing false
skinparam SequenceMessageAlign left
skinparam SequenceLifeLineBackgroundColor #7AD2FF
skinparam SequenceLifeLineBorderColor #000000
<style>
sequenceDiagram {
  Shadowing 0
  RoundCorner 0
  FontName "Arial"
  FontSize 10
  FontColor #000000
  boundary { BackgroundColor #7AD2FF; LineColor #000000 }
  entity { BackgroundColor #7AD2FF; LineColor #000000 }
  actor { BackgroundColor transparent; LineColor #000000 }
  lifeline { LineColor #000000; LineStyle 5-5 }
  arrow { LineColor #000000; LineThickness 1; FontSize 10 }
}
</style>
title UC11 – Nhập/sửa điểm (Tuần tự Phân tích)

actor "Giảng viên" as GV
boundary GradeEntryView
entity Class
entity Grade

GV -> GradeEntryView : 1: mở giao diện nhập điểm
activate GradeEntryView
GV -> GradeEntryView : 2: nhập từ khóa + nhấn Tìm
GradeEntryView -> Class : 3: gọi searchClassSection()
activate Class
Class --> GradeEntryView : 4: trả về danh sách lớp
deactivate Class
GradeEntryView --> GV : 5: hiển thị danh sách lớp học phần
GV -> GradeEntryView : 6: chọn lớp INT1340.01
GradeEntryView --> GV : 7: hiển thị bảng SV với ô nhập điểm
GV -> GradeEntryView : 8: nhập điểm cuối kỳ cho từng SV
GV -> GradeEntryView : 9: nhấn "Xác nhận nộp điểm"
GradeEntryView -> Grade : 10: gọi enterGrade()
activate Grade
Grade --> GradeEntryView : 11: trả về kết quả thành công
deactivate Grade
GradeEntryView --> GV : 12: thông báo "Nộp điểm thành công"
deactivate GradeEntryView
@enduml
```

---

## III.1. Thiết kế lớp thực thể – Module 3

### Bước 1 – Thêm id

Thêm `id : int` cho các lớp mới trong module: `Attendance` và `Grade`.

### Bước 2 – Thêm kiểu dữ liệu (TypeScript/NestJS)

**Attendance:**
- `id: number`, `classSessionId: number`, `studentId: number`
- `status: AttendanceStatus` (enum: `PRESENT | ABSENT | LATE`)
- `method: AttendanceMethod` (enum: `QR | MANUAL`) — cột "hình thức" trong docx
- `qrToken: string | null`, `gpsLatitude: number | null`, `gpsLongitude: number | null`
- `distance: number | null`, `scannedAt: Date | null`, `note: string | null`

**Grade:**
- `id: number`, `enrollmentId: number`
- `type: GradeType` (enum: `ASSIGNMENT | MIDTERM | FINAL`)
- `name: string`, `score: number`, `maxScore: number`, `weight: number`

### Bước 3 – Chuyển association → composition/aggregation

- `Schedule 1 *── n ClassSession` — composition (ClassSession không tồn tại nếu không có Schedule)
- `ClassSession 1 ◇── n Attendance` — aggregation (ClassSession tồn tại độc lập)
- `User 1 ◇── n Attendance` — aggregation (User tồn tại độc lập)
- `Enrollment 1 *── n Grade` — composition (Grade không tồn tại nếu không có Enrollment)

### Bước 4 – Thuộc tính kiểu đối tượng

- `Attendance` chứa: `classSession: ClassSession`, `student: User`
- `Grade` chứa: `enrollment: Enrollment`

```plantuml
@startuml
title Thiết kế lớp thực thể – Module 3: Điểm danh & Học vụ

class Schedule {
  -id : int
  -classId : int
  -dayOfWeek : String
  -startTime : Date
  -endTime : Date
  -room : String
}

class ClassSession {
  -id : int
  -scheduleId : int
  -sessionDate : Date
}

class User {
  -id : int
  -role : String
  -fullName : String
}

class Enrollment {
  -id : int
  -studentId : int
  -classId : int
  -status : String
}

class Attendance {
  -id : int
  -classSessionId : int
  -studentId : int
  -status : String
  -method : String
  -qrToken : String
  -gpsLatitude : double
  -gpsLongitude : double
  -distance : double
  -scannedAt : Date
  -note : String
  -classSession : ClassSession
  -student : User
}

class Grade {
  -id : int
  -enrollmentId : int
  -type : String
  -name : String
  -score : double
  -maxScore : double
  -weight : double
  -enrollment : Enrollment
}

Schedule "1" *-- "n" ClassSession : composition
ClassSession "1" o-- "n" Attendance : aggregation
User "1" o-- "n" Attendance : aggregation
Enrollment "1" *-- "n" Grade : composition
@enduml
```

---

## III.2. Thiết kế CSDL – Module 3

### Bước 1–4: Bảng và khóa

**tblClassSession:**

| Cột | Kiểu | Ràng buộc | Ghi chú |
|-----|------|-----------|---------|
| classSessionId | SERIAL | PK | Khóa chính tự tăng |
| scheduleId | INTEGER(10) | FK → tblSchedule | Lịch học trong tuần |
| sessionDate | DATE | NOT NULL | Ngày buổi học cụ thể |

**tblAttendance:**

| Cột | Kiểu | Ràng buộc | Ghi chú |
|-----|------|-----------|---------|
| attendanceId | SERIAL | PK | Khóa chính tự tăng |
| classSessionId | INTEGER(10) | FK → tblClassSession | Buổi học cụ thể |
| studentId | INTEGER(10) | FK → tblUser | Sinh viên |
| status | VARCHAR(10) | NOT NULL | present / absent / late |
| method | VARCHAR(10) | NOT NULL | qr / manual |
| qrToken | VARCHAR(255) | NULL | Token QR đã dùng |
| gpsLatitude | DOUBLE(10) | NULL | Vĩ độ GPS |
| gpsLongitude | DOUBLE(10) | NULL | Kinh độ GPS |
| distance | DOUBLE(10) | NULL | Khoảng cách tính được (m) |
| scannedAt | TIMESTAMP | NULL | Thời điểm điểm danh |
| note | TEXT | NULL | Lý do chỉnh sửa thủ công |

**tblGrade:**

| Cột | Kiểu | Ràng buộc | Ghi chú |
|-----|------|-----------|---------|
| gradeId | SERIAL | PK | Khóa chính tự tăng |
| enrollmentId | INTEGER(10) | FK → tblEnrollment | Đăng ký học phần |
| type | VARCHAR(20) | NOT NULL | assignment / midterm / final |
| name | VARCHAR(100) | NOT NULL | Tên đầu điểm |
| score | DOUBLE(10) | NOT NULL | Điểm đạt được |
| maxScore | DOUBLE(10) | NOT NULL | Điểm tối đa |
| weight | DOUBLE(10) | NOT NULL | Trọng số (%) |

### Bước 5 – Loại bỏ dư thừa

Điểm tổng kết không lưu — tính runtime từ `score × weight`. Không có thuộc tính dẫn xuất nào cần loại bỏ.

### ERD PlantUML

```plantuml
@startuml
title ERD – Module 3: Điểm danh & Học vụ

entity "tblSchedule" as SCH {
  * scheduleId : integer(10) <<PK>>
  --
  classId : integer(10) <<FK>>
  dayOfWeek : varchar(20)
  startTime : time
  endTime : time
  room : varchar(50)
}

entity "tblClassSession" as CSS {
  * classSessionId : integer(10) <<PK>>
  --
  * scheduleId : integer(10) <<FK>>
  sessionDate : date
}

entity "tblUser" as USR {
  * userId : integer(10) <<PK>>
  --
  fullName : varchar(255)
  role : varchar(20)
}

entity "tblEnrollment" as ENR {
  * enrollmentId : integer(10) <<PK>>
  --
  studentId : integer(10) <<FK>>
  classId : integer(10) <<FK>>
  status : varchar(20)
}

entity "tblAttendance" as ATT {
  * attendanceId : integer(10) <<PK>>
  --
  * classSessionId : integer(10) <<FK>>
  * studentId : integer(10) <<FK>>
  status : varchar(10)
  method : varchar(10)
  qrToken : varchar(255)
  gpsLatitude : double(10)
  gpsLongitude : double(10)
  distance : double(10)
  scannedAt : timestamp
  note : text
}

entity "tblGrade" as GRD {
  * gradeId : integer(10) <<PK>>
  --
  * enrollmentId : integer(10) <<FK>>
  type : varchar(20)
  name : varchar(100)
  score : double(10)
  maxScore : double(10)
  weight : double(10)
}

SCH ||--o{ CSS : "1-n"
CSS ||--o{ ATT : "1-n"
USR ||--o{ ATT : "1-n"
ENR ||--o{ GRD : "1-n"
@enduml
```

---

## III.3.1. Thiết kế giao diện – Module 3

### Màn hình 1: QRSessionPage

```
┌─────────────────────────────────────────────────────────┐
│             Điểm danh – QR Session                      │
│                                                         │
│  Tìm lớp: [________________________]    [ Tìm ]         │
│                                                         │
│  ┌──────────────┬───────────────┬──────┬────────┐       │
│  │ Mã lớp       │ Tên học phần  │ Phòng│ Giờ    │       │
│  ├──────────────┼───────────────┼──────┼────────┤       │
│  │ INT1340.01   │ Nhập môn CNPM │ A101 │ 07:30  │       │
│  └──────────────┴───────────────┴──────┴────────┘       │
│                                                         │
│  Buổi học: [Thứ 2 – 09/06/2026 – 07:30-09:00]          │
│                                                         │
│  ┌──────────────────────────┐                           │
│  │                          │                           │
│  │    ██████████████        │  ← Mã QR điểm danh        │
│  │    ██          ██        │                           │
│  │    ██  ██████  ██        │                           │
│  │    ██          ██        │                           │
│  │    ██████████████        │                           │
│  │                          │                           │
│  └──────────────────────────┘                           │
│                                                         │
│  [ Tạo mã QR ]              [ Tắt mã QR ]               │
│                                                         │
│  ┌──────────────┬────────────────┬──────────┐           │
│  │ MSSV         │ Họ tên         │ Trạng thái│           │
│  ├──────────────┼────────────────┼──────────┤           │
│  │ B23DCAT120   │ Nguyễn Bá Hùng │ Có mặt   │           │
│  │ B23DCAT280   │ Trần Xuân Thành│ Có mặt   │           │
│  │ B23DCCN266   │ Phạm Thị T. Hà │ Vắng     │           │
│  └──────────────┴────────────────┴──────────┘           │
└─────────────────────────────────────────────────────────┘
```

### Màn hình 2: AttendanceManagePage

```
┌─────────────────────────────────────────────────────────┐
│             Quản lý điểm danh – INT1340.01              │
│                                                         │
│  Buổi học: [ Thứ 2 – 09/06/2026  ▼ ]                   │
│                                                         │
│  ┌──────────┬────────────────┬──────────┬──────┬──────┬───────┐ │
│  │ MSSV     │ Họ tên         │ Trạng thái│ Giờ  │ HTức │[Sửa] │ │
│  ├──────────┼────────────────┼──────────┼──────┼──────┼───────┤ │
│  │B23DCAT120│ Nguyễn Bá Hùng │ Có mặt   │07:35 │ QR   │[Sửa] │ │
│  │B23DCAT280│ Trần Xuân Thành│ Có mặt   │07:38 │ QR   │[Sửa] │ │
│  │B23DCCN266│ Phạm Thị T. Hà │ Vắng     │ —    │ —    │[Sửa] │ │
│  └──────────┴────────────────┴──────────┴──────┴──────┴───────┘ │
│                                                         │
│  ┌──────────────── Modal sửa trạng thái ─────────────┐  │
│  │ SV: Phạm Thị Thiên Hà – B23DCCN266               │  │
│  │ Trạng thái: [ Có mặt           ▼ ]                │  │
│  │ Lý do:      [____________________________]        │  │
│  │                    [ Hủy ]  [ Lưu ]               │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### Màn hình 3: GradePage

```
┌─────────────────────────────────────────────────────────┐
│                  Xem điểm – Sinh viên                   │
│                                                         │
│  Học kỳ: [ 2024-1  ▼ ]                                  │
│                                                         │
│  ┌──────────────┬───────────────┬──────┬──────┬───────┐  │
│  │ Mã lớp       │ Tên học phần  │ TX   │ GK   │ CK    │  │
│  ├──────────────┼───────────────┼──────┼──────┼───────┤  │
│  │ INT1340.01   │ Nhập môn CNPM │  9.0 │  8.5 │  9.0  │  │
│  │ INT1305.02   │ Lập trình Web │  —   │  —   │  —    │  │
│  └──────────────┴───────────────┴──────┴──────┴───────┘  │
│                                                         │
│  ┌──────────────────── Chi tiết: INT1340.01 ──────────┐  │
│  │ Loại điểm    │ Điểm │ Trọng số │                   │  │
│  │ Thường xuyên │  9.0 │   20%    │                   │  │
│  │ Giữa kỳ      │  8.5 │   30%    │                   │  │
│  │ Cuối kỳ      │  9.0 │   50%    │                   │  │
│  │ ────────────────────────────── │                   │  │
│  │ Tổng kết: 8.9   Xếp loại: A   │                   │  │
│  └────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### Màn hình 4: GradeEntryPage

```
┌─────────────────────────────────────────────────────────┐
│               Nhập điểm – Giảng viên                    │
│                                                         │
│  Tìm lớp: [________________________]    [ Tìm ]         │
│                                                         │
│  ┌──────────────┬───────────────┬──────┬────────┐       │
│  │ Mã lớp       │ Tên học phần  │ SV   │ Trạng thái│     │
│  ├──────────────┼───────────────┼──────┼───────────┤    │
│  │ INT1340.01   │ Nhập môn CNPM │  35  │ Đang nhập │    │
│  └──────────────┴───────────────┴──────┴───────────┘    │
│                                                         │
│  ┌──────────┬────────────────┬────┬────┬────┬───────┐   │
│  │ MSSV     │ Họ tên         │ TX │ GK │ CK │ Tổng  │   │
│  ├──────────┼────────────────┼────┼────┼────┼───────┤   │
│  │B23DCAT120│ Nguyễn Bá Hùng │8.5 │7.0 │[__]│  —    │   │
│  │B23DCAT280│ Trần Xuân Thành│9.0 │8.5 │[__]│  —    │   │
│  │B23DCCN266│ Phạm Thị T. Hà │9.5 │9.0 │[__]│  —    │   │
│  └──────────┴────────────────┴────┴────┴────┴───────┘   │
│                                                         │
│                      [ Xác nhận nộp điểm ]              │
└─────────────────────────────────────────────────────────┘
```

---

## III.3.2. Sơ đồ lớp thiết kế – Module 3 (React MVC)

```plantuml
@startuml
left to right direction
skinparam linetype ortho
skinparam packageStyle rectangle
skinparam packageMaxWidth 900
title Biểu đồ lớp thiết kế – Module 3: Điểm danh & Học vụ (React MVC)

package "<<Boundary>>" #E3F2FD {
  together {
    class QRSessionPage {
      - txtClassSection : TextBox
      - btnSearchClass : Button
      - tblAttendanceList : Table
      - btnGenerateQr : Button
      - btnDeactivateQr : Button
      + formLoad() : void
      + btnSearchClassClick() : void
      + btnGenerateQrClick() : void
      + btnDeactivateQrClick() : void
      + displayAttendanceList(attendances : List<Attendance>) : void
      + showMessage(msg : String) : void
    }
    class AttendanceManagePage {
      - tblAttendance : Table
      - btnSaveManual : Button
      + formLoad() : void
      + tblAttendanceClick(id : int) : void
      + btnSaveManualClick() : void
      + displayAttendance(list : List<Attendance>) : void
      + showMessage(msg : String) : void
    }
  }
  together {
    class GradePage {
      - tblCourseList : Table
      - tblGradeDetail : Table
      + formLoad() : void
      + tblCourseListClick(classId : int) : void
      + displayCourseList(classes : List<Class>) : void
      + displayGradeDetail(grades : List<Grade>) : void
    }
    class GradeEntryPage {
      - txtClassSection : TextBox
      - btnSearchClass : Button
      - tblGrades : Table
      - btnSubmitGrades : Button
      + formLoad() : void
      + btnSearchClassClick() : void
      + btnSubmitGradesClick() : void
      + displayGrades(grades : List<Grade>) : void
      + showMessage(msg : String) : void
    }
  }
}

package "<<Control>>" #E8F5E9 {
  class AttendanceController {
    + generateQr(schedule : Schedule) : Attendance
    + deactivateQr(schedule : Schedule) : boolean
    + viewAttendance(schedule : Schedule) : List<Attendance>
    + markManualAttendance(attendance : Attendance) : boolean
  }
  class GradeController {
    + enterGrade(grades : List<Grade>) : boolean
    + editGrade(grade : Grade) : boolean
    + viewGrade(student : User) : List<Grade>
    + viewGradeBySemester(student : User, semester : String) : List<Grade>
  }
  class ClassController {
    + searchClassSection(keyword : String) : List<Class>
  }
}

package "<<Entity>>" #FFF3E0 {
  class Attendance {
    - id : int
    - scheduleId : int
    - studentId : int
    - sessionDate : Date
    - status : String
    - method : String
    - qrToken : String
    - gpsLatitude : double
    - gpsLongitude : double
    - distance : double
    - scannedAt : Date
    - note : String
  }
  class Grade {
    - id : int
    - enrollmentId : int
    - type : String
    - name : String
    - score : double
    - maxScore : double
    - weight : double
  }
  class Class {
    - id : int
    - name : String
    - courseId : int
  }
  class Schedule {
    - id : int
    - classId : int
    - sessionDate : Date
  }
  class User {
    - id : int
    - role : String
  }
}

' Boundary -> Control
QRSessionPage --> AttendanceController
QRSessionPage --> ClassController
AttendanceManagePage --> AttendanceController
GradePage --> GradeController
GradeEntryPage --> GradeController
GradeEntryPage --> ClassController

' Control -> Entity
AttendanceController --> Attendance
AttendanceController --> Schedule
GradeController --> Grade
GradeController --> User
ClassController --> Class

' Entity relationships
Schedule "1" --> "*" Attendance
Class "1" --> "*" Schedule
@enduml
```

---

## III.4. Biểu đồ tuần tự thiết kế – Module 3

### Biểu đồ UC08: Tạo/tắt mã QR

**Kịch bản phiên bản 3 – UC08 Tạo/tắt mã QR**

1. Giảng viên truy cập giao diện `QRSessionPage`, phương thức `formLoad()` được gọi tự động.
2. Giảng viên nhập từ khóa "INT1340" và nhấn Tìm; `QRSessionPage` gọi `btnSearchClassClick()`.
3. Phương thức `btnSearchClassClick()` gọi `ClassController.searchClassSection(keyword: String)`.
4. `ClassController` gọi `Class` để truy vấn và trả về `List<Class>`.
5. `QRSessionPage` gọi `displayAttendanceList()` để hiển thị danh sách lớp.
6. Giảng viên chọn lớp INT1340.01, chọn buổi học và nhấn "Tạo mã QR"; `QRSessionPage` gọi `btnGenerateQrClick()`.
7. Phương thức `btnGenerateQrClick()` gọi `AttendanceController.generateQr(schedule: Schedule)`.
8. `AttendanceController` gọi `Attendance` để tạo mã QR và trả về đối tượng `Attendance`.
9. `QRSessionPage` nhận kết quả và hiển thị mã QR trên giao diện.
10. Giảng viên nhấn "Tắt mã QR"; `QRSessionPage` gọi `btnDeactivateQrClick()`.
11. Phương thức `btnDeactivateQrClick()` gọi `AttendanceController.deactivateQr(schedule: Schedule)`.
12. `AttendanceController` gọi `Attendance` để đóng điểm danh, trả về `boolean`.
13. `QRSessionPage` gọi `showMessage("Đã đóng điểm danh")` và cập nhật bảng.

```plantuml
@startuml
skinparam shadowing false
skinparam SequenceMessageAlign left
skinparam SequenceLifeLineBackgroundColor #7AD2FF
skinparam SequenceLifeLineBorderColor #000000
<style>
sequenceDiagram {
  Shadowing 0
  RoundCorner 0
  FontName "Arial"
  FontSize 10
  FontColor #000000
  boundary { BackgroundColor #7AD2FF; LineColor #000000 }
  control { BackgroundColor #7AD2FF; LineColor #000000 }
  entity { BackgroundColor #7AD2FF; LineColor #000000 }
  actor { BackgroundColor transparent; LineColor #000000 }
  lifeline { LineColor #000000; LineStyle 5-5 }
  arrow { LineColor #000000; LineThickness 1; FontSize 10 }
}
</style>
title UC08 – Tạo/tắt mã QR (Tuần tự Thiết kế)

actor "Giảng viên" as GV
boundary QRSessionPage
control ClassController
control AttendanceController
entity Class
entity Attendance

GV -> QRSessionPage : 1: formLoad()
activate QRSessionPage
GV -> QRSessionPage : 2: btnSearchClassClick()
QRSessionPage -> ClassController : 3: searchClassSection(keyword : String)
activate ClassController
ClassController -> Class : 4: searchClassSection(keyword : String) : List<Class>
activate Class
Class --> ClassController : 5: List<Class>
deactivate Class
ClassController --> QRSessionPage : 6: List<Class>
deactivate ClassController
QRSessionPage --> GV : 7: displayAttendanceList(classes)
GV -> QRSessionPage : 8: btnGenerateQrClick()
QRSessionPage -> AttendanceController : 9: generateQr(schedule : Schedule)
activate AttendanceController
AttendanceController -> Attendance : 10: generateQr(schedule : Schedule) : Attendance
activate Attendance
Attendance --> AttendanceController : 11: Attendance
deactivate Attendance
AttendanceController --> QRSessionPage : 12: Attendance
deactivate AttendanceController
QRSessionPage --> GV : 13: hiển thị mã QR
GV -> QRSessionPage : 14: btnDeactivateQrClick()
QRSessionPage -> AttendanceController : 15: deactivateQr(schedule : Schedule)
activate AttendanceController
AttendanceController -> Attendance : 16: deactivateQr(schedule : Schedule) : boolean
activate Attendance
Attendance --> AttendanceController : 17: true
deactivate Attendance
AttendanceController --> QRSessionPage : 18: true
deactivate AttendanceController
QRSessionPage --> GV : 19: showMessage("Đã đóng điểm danh") + cập nhật bảng
deactivate QRSessionPage
@enduml
```

---

### Biểu đồ UC09: Quản lý điểm danh

**Kịch bản phiên bản 3 – UC09 Quản lý điểm danh**

1. Giảng viên truy cập `AttendanceManagePage`, `formLoad()` tự động gọi `AttendanceController.viewAttendance(schedule: Schedule)`.
2. `AttendanceController` gọi `Attendance` truy vấn và trả về `List<Attendance>`.
3. `AttendanceManagePage` gọi `displayAttendance(list: List<Attendance>)` để hiển thị bảng.
4. Giảng viên click dòng SV Phạm Thị Thiên Hà; `AttendanceManagePage` gọi `tblAttendanceClick(id: int)`.
5. Giao diện hiển thị modal chỉnh sửa với trạng thái hiện tại.
6. Giảng viên chọn trạng thái "Có mặt", nhập lý do và nhấn Lưu; `btnSaveManualClick()` được kích hoạt.
7. `AttendanceManagePage` gọi `AttendanceController.markManualAttendance(attendance: Attendance)`.
8. `AttendanceController` gọi `Attendance` cập nhật và trả về `boolean`.
9. `AttendanceManagePage` gọi `showMessage("Cập nhật thành công")` và refresh bảng.

```plantuml
@startuml
skinparam shadowing false
skinparam SequenceMessageAlign left
skinparam SequenceLifeLineBackgroundColor #7AD2FF
skinparam SequenceLifeLineBorderColor #000000
<style>
sequenceDiagram {
  Shadowing 0
  RoundCorner 0
  FontName "Arial"
  FontSize 10
  FontColor #000000
  boundary { BackgroundColor #7AD2FF; LineColor #000000 }
  control { BackgroundColor #7AD2FF; LineColor #000000 }
  entity { BackgroundColor #7AD2FF; LineColor #000000 }
  actor { BackgroundColor transparent; LineColor #000000 }
  lifeline { LineColor #000000; LineStyle 5-5 }
  arrow { LineColor #000000; LineThickness 1; FontSize 10 }
}
</style>
title UC09 – Quản lý điểm danh (Tuần tự Thiết kế)

actor "Giảng viên" as GV
boundary AttendanceManagePage
control AttendanceController
entity Attendance

GV -> AttendanceManagePage : 1: formLoad()
activate AttendanceManagePage
AttendanceManagePage -> AttendanceController : 2: viewAttendance(schedule : Schedule)
activate AttendanceController
AttendanceController -> Attendance : 3: viewAttendance(schedule : Schedule) : List<Attendance>
activate Attendance
Attendance --> AttendanceController : 4: List<Attendance>
deactivate Attendance
AttendanceController --> AttendanceManagePage : 5: List<Attendance>
deactivate AttendanceController
AttendanceManagePage --> GV : 6: displayAttendance(list)
GV -> AttendanceManagePage : 7: tblAttendanceClick(id : int)
AttendanceManagePage --> GV : 8: hiển thị modal chỉnh sửa
GV -> AttendanceManagePage : 9: btnSaveManualClick()
AttendanceManagePage -> AttendanceController : 10: markManualAttendance(attendance : Attendance)
activate AttendanceController
AttendanceController -> Attendance : 11: markManualAttendance(attendance : Attendance) : boolean
activate Attendance
Attendance --> AttendanceController : 12: true
deactivate Attendance
AttendanceController --> AttendanceManagePage : 13: true
deactivate AttendanceController
AttendanceManagePage --> GV : 14: showMessage("Cập nhật thành công") + refresh
deactivate AttendanceManagePage
@enduml
```

---

### Biểu đồ UC10: Xem điểm

**Kịch bản phiên bản 3 – UC10 Xem điểm**

1. Sinh viên truy cập `GradePage`, `formLoad()` tự động gọi `GradeController.viewGrade(student: User)`.
2. `GradeController` gọi `Grade` truy vấn và trả về `List<Grade>`.
3. `GradePage` gọi `displayCourseList(classes: List<Class>)` để hiển thị danh sách môn.
4. Sinh viên click lớp INT1340.01; `GradePage` gọi `tblCourseListClick(classId: int)`.
5. `GradePage` gọi `GradeController.viewGradeBySemester(student: User, semester: String)`.
6. `GradeController` gọi `Grade` truy vấn chi tiết và trả về `List<Grade>`.
7. `GradePage` gọi `displayGradeDetail(grades: List<Grade>)` để hiển thị bảng chi tiết.

```plantuml
@startuml
skinparam shadowing false
skinparam SequenceMessageAlign left
skinparam SequenceLifeLineBackgroundColor #7AD2FF
skinparam SequenceLifeLineBorderColor #000000
<style>
sequenceDiagram {
  Shadowing 0
  RoundCorner 0
  FontName "Arial"
  FontSize 10
  FontColor #000000
  boundary { BackgroundColor #7AD2FF; LineColor #000000 }
  control { BackgroundColor #7AD2FF; LineColor #000000 }
  entity { BackgroundColor #7AD2FF; LineColor #000000 }
  actor { BackgroundColor transparent; LineColor #000000 }
  lifeline { LineColor #000000; LineStyle 5-5 }
  arrow { LineColor #000000; LineThickness 1; FontSize 10 }
}
</style>
title UC10 – Xem điểm (Tuần tự Thiết kế)

actor "Sinh viên" as SV
boundary GradePage
control GradeController
entity Grade

SV -> GradePage : 1: formLoad()
activate GradePage
GradePage -> GradeController : 2: viewGrade(student : User)
activate GradeController
GradeController -> Grade : 3: viewGrade(student : User) : List<Grade>
activate Grade
Grade --> GradeController : 4: List<Grade>
deactivate Grade
GradeController --> GradePage : 5: List<Grade>
deactivate GradeController
GradePage --> SV : 6: displayCourseList(classes)
SV -> GradePage : 7: tblCourseListClick(classId : int)
GradePage -> GradeController : 8: viewGradeBySemester(student : User, semester : String)
activate GradeController
GradeController -> Grade : 9: viewGradeBySemester(student : User, semester : String) : List<Grade>
activate Grade
Grade --> GradeController : 10: List<Grade>
deactivate Grade
GradeController --> GradePage : 11: List<Grade>
deactivate GradeController
GradePage --> SV : 12: displayGradeDetail(grades)
deactivate GradePage
@enduml
```

---

### Biểu đồ UC11: Nhập/sửa điểm

**Kịch bản phiên bản 3 – UC11 Nhập/sửa điểm**

1. Giảng viên truy cập `GradeEntryPage`, `formLoad()` khởi tạo giao diện.
2. Giảng viên nhập từ khóa và nhấn Tìm; `GradeEntryPage` gọi `btnSearchClassClick()`.
3. `btnSearchClassClick()` gọi `ClassController.searchClassSection(keyword: String)`.
4. `ClassController` gọi `Class` truy vấn, trả về `List<Class>`.
5. `GradeEntryPage` gọi `displayGrades()` để hiển thị danh sách lớp.
6. Giảng viên chọn lớp INT1340.01; giao diện hiển thị bảng SV với ô nhập điểm.
7. Giảng viên nhập điểm cuối kỳ cho từng SV.
8. Giảng viên nhấn "Xác nhận nộp điểm"; `GradeEntryPage` gọi `btnSubmitGradesClick()`.
9. `btnSubmitGradesClick()` gọi `GradeController.enterGrade(grades: List<Grade>)`.
10. `GradeController` gọi `Grade` để lưu và trả về `boolean`.
11. `GradeEntryPage` gọi `showMessage("Nộp điểm thành công")`.

```plantuml
@startuml
skinparam shadowing false
skinparam SequenceMessageAlign left
skinparam SequenceLifeLineBackgroundColor #7AD2FF
skinparam SequenceLifeLineBorderColor #000000
<style>
sequenceDiagram {
  Shadowing 0
  RoundCorner 0
  FontName "Arial"
  FontSize 10
  FontColor #000000
  boundary { BackgroundColor #7AD2FF; LineColor #000000 }
  control { BackgroundColor #7AD2FF; LineColor #000000 }
  entity { BackgroundColor #7AD2FF; LineColor #000000 }
  actor { BackgroundColor transparent; LineColor #000000 }
  lifeline { LineColor #000000; LineStyle 5-5 }
  arrow { LineColor #000000; LineThickness 1; FontSize 10 }
}
</style>
title UC11 – Nhập/sửa điểm (Tuần tự Thiết kế)

actor "Giảng viên" as GV
boundary GradeEntryPage
control ClassController
control GradeController
entity Class
entity Grade

GV -> GradeEntryPage : 1: formLoad()
activate GradeEntryPage
GV -> GradeEntryPage : 2: btnSearchClassClick()
GradeEntryPage -> ClassController : 3: searchClassSection(keyword : String)
activate ClassController
ClassController -> Class : 4: searchClassSection(keyword : String) : List<Class>
activate Class
Class --> ClassController : 5: List<Class>
deactivate Class
ClassController --> GradeEntryPage : 6: List<Class>
deactivate ClassController
GradeEntryPage --> GV : 7: displayGrades(classes)
GV -> GradeEntryPage : 8: nhập điểm cho từng SV
GV -> GradeEntryPage : 9: btnSubmitGradesClick()
GradeEntryPage -> GradeController : 10: enterGrade(grades : List<Grade>)
activate GradeController
GradeController -> Grade : 11: enterGrade(grades : List<Grade>) : boolean
activate Grade
Grade --> GradeController : 12: true
deactivate Grade
GradeController --> GradeEntryPage : 13: true
deactivate GradeController
GradeEntryPage --> GV : 14: showMessage("Nộp điểm thành công")
deactivate GradeEntryPage
@enduml
```

---

## IV. Cài đặt & Kiểm thử – Module 3

### 4a. Bảng test case

| TT | Module | Test case |
|----|--------|-----------|
| 1 | UC08 | Tạo QR thành công cho buổi học chưa có QR đang hoạt động |
| 2 | UC08 | Tạo QR thất bại khi buổi học đã có QR đang hoạt động |
| 3 | UC08 | Tắt QR thành công – SV chưa điểm danh bị ghi vắng tự động |
| 4 | UC09 | GV xem danh sách điểm danh theo buổi học thành công |
| 5 | UC09 | GV sửa trạng thái thủ công thành công (vắng → có mặt) |
| 6 | UC09 | GV sửa trạng thái trùng trạng thái cũ → cảnh báo "Trạng thái không thay đổi" |
| 7 | UC10 | SV xem danh sách lớp đã đăng ký kèm trạng thái điểm thành công |
| 8 | UC10 | SV xem chi tiết điểm theo kỳ học 2024-1 thành công |
| 9 | UC10 | SV xem học phần INT1305.02 chưa có điểm → hiển thị "Chưa có điểm" |
| 10 | UC11 | GV nhập điểm cuối kỳ và nộp thành công |
| 11 | UC11 | GV nhập điểm = 11 (ngoài [0, 10]) → lỗi validation inline |
| 12 | UC11 | GV sửa điểm ngoài thời gian cho phép → thông báo "Đã quá thời hạn sửa điểm" |

---

### 4b. Trạng thái CSDL trước khi test

```
tblSchedule
| scheduleId | classId | dayOfWeek | startTime | endTime | room  |
|------------|---------|-----------|-----------|---------|-------|
| 1          | 1       | Monday    | 07:30:00  | 09:00:00| A101  |
| 2          | 1       | Monday    | 07:30:00  | 09:00:00| A101  |

tblUser
| userId | fullName            | role     |
|--------|---------------------|----------|
| 1      | Nguyễn Bá Hùng      | STUDENT  |
| 2      | Trần Xuân Thành     | STUDENT  |
| 3      | Phạm Thị Thiên Hà   | STUDENT  |
| 4      | Nguyễn Văn Giảng    | LECTURER |

tblEnrollment
| enrollmentId | studentId | classId | status   |
|--------------|-----------|---------|----------|
| 1            | 1         | 1       | ENROLLED |
| 2            | 2         | 1       | ENROLLED |
| 3            | 3         | 1       | ENROLLED |

tblAttendance (trước TC01 – chưa có bản ghi nào cho scheduleId=1)
| attendanceId | scheduleId | studentId | sessionDate | status | method |
|--------------|------------|-----------|-------------|--------|--------|
| (trống)      |            |           |             |        |        |

tblGrade (trước TC10 – đã có điểm TX và GK, chưa có CK)
| gradeId | enrollmentId | type       | name        | score | maxScore | weight |
|---------|--------------|------------|-------------|-------|----------|--------|
| 1       | 1            | ASSIGNMENT | Thường xuyên| 8.5   | 10       | 20     |
| 2       | 1            | MIDTERM    | Giữa kỳ     | 7.0   | 10       | 30     |
| 3       | 2            | ASSIGNMENT | Thường xuyên| 9.0   | 10       | 20     |
| 4       | 2            | MIDTERM    | Giữa kỳ     | 8.5   | 10       | 30     |
| 5       | 3            | ASSIGNMENT | Thường xuyên| 9.5   | 10       | 20     |
| 6       | 3            | MIDTERM    | Giữa kỳ     | 9.0   | 10       | 30     |
```

---

### 4c. Kịch bản thực hiện chi tiết

#### TC01 – Tạo QR thành công (UC08)

| Kịch bản | Kết quả mong đợi |
|----------|-----------------|
| 1. GV đăng nhập với tài khoản giảng viên (userId=4) | Đăng nhập thành công, hiển thị giao diện chính |
| 2. GV chọn chức năng "Điểm danh" | Hiển thị `QRSessionPage` với ô tìm kiếm lớp |
| 3. GV nhập "INT1340" và nhấn Tìm | Hiển thị lớp INT1340.01 trong bảng kết quả |
| 4. GV chọn lớp INT1340.01, chọn buổi học scheduleId=1 | Giao diện hiển thị thông tin buổi học và nút "Tạo mã QR" |
| 5. GV nhấn "Tạo mã QR" | Hệ thống hiển thị mã QR điểm danh trên màn hình; bảng SV hiển thị trạng thái "Vắng" cho cả 3 SV |
| 6. GV nhấn "Tắt mã QR" | Thông báo "Đã đóng điểm danh – 0 có mặt, 3 vắng"; tblAttendance có 3 bản ghi mới với status=ABSENT |

#### TC10 – Nhập điểm thành công (UC11)

| Kịch bản | Kết quả mong đợi |
|----------|-----------------|
| 1. GV đăng nhập (userId=4) và chọn "Nhập điểm" | Hiển thị `GradeEntryPage` |
| 2. GV nhập "INT1340" và nhấn Tìm | Hiển thị lớp INT1340.01 với trạng thái "Đang nhập điểm" |
| 3. GV chọn lớp INT1340.01 | Bảng hiển thị 3 SV với điểm TX, GK đã có; cột CK để trống |
| 4. GV nhập: Hùng=8.0, Thành=9.0, Hà=9.5 | Cột Tổng kết tự tính: Hùng=7.8, Thành=8.65, Hà=9.25 |
| 5. GV nhấn "Xác nhận nộp điểm" | Thông báo "Nộp điểm thành công"; tblGrade có thêm 3 bản ghi FINAL |

---

### 4d. Trạng thái CSDL sau khi test

```
tblAttendance (sau TC01 – Tắt QR, 3 SV ghi vắng)
| attendanceId | scheduleId | studentId | sessionDate | status  | method |
|--------------|------------|-----------|-------------|---------|--------|
| 1            | 1          | 1         | 2026-06-09  | ABSENT  | QR     |
| 2            | 1          | 2         | 2026-06-09  | ABSENT  | QR     |
| 3            | 1          | 3         | 2026-06-09  | ABSENT  | QR     |

tblGrade (sau TC10 – thêm điểm cuối kỳ cho 3 SV)
| gradeId | enrollmentId | type       | name        | score | maxScore | weight |
|---------|--------------|------------|-------------|-------|----------|--------|
| 1       | 1            | ASSIGNMENT | Thường xuyên| 8.5   | 10       | 20     |
| 2       | 1            | MIDTERM    | Giữa kỳ     | 7.0   | 10       | 30     |
| 3       | 2            | ASSIGNMENT | Thường xuyên| 9.0   | 10       | 20     |
| 4       | 2            | MIDTERM    | Giữa kỳ     | 8.5   | 10       | 30     |
| 5       | 3            | ASSIGNMENT | Thường xuyên| 9.5   | 10       | 20     |
| 6       | 3            | MIDTERM    | Giữa kỳ     | 9.0   | 10       | 30     |
| 7       | 1            | FINAL      | Cuối kỳ     | 8.0   | 10       | 50     | ← mới
| 8       | 2            | FINAL      | Cuối kỳ     | 9.0   | 10       | 50     | ← mới
| 9       | 3            | FINAL      | Cuối kỳ     | 9.5   | 10       | 50     | ← mới

Điểm tổng kết tính runtime:
- Nguyễn Bá Hùng    (enrollmentId=1): 8.5×20% + 7.0×30% + 8.0×50% = 1.7 + 2.1 + 4.0 = 7.8
- Trần Xuân Thành   (enrollmentId=2): 9.0×20% + 8.5×30% + 9.0×50% = 1.8 + 2.55 + 4.5 = 8.85
- Phạm Thị Thiên Hà (enrollmentId=3): 9.5×20% + 9.0×30% + 9.5×50% = 1.9 + 2.7 + 4.75 = 9.35
```
