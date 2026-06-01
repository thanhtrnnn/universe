<!-- Pha I – Requirements, Section 1 -->

## I.1. Mô hình nghiệp vụ bằng UML

**Quy trình 4 bước (BẮT BUỘC thực hiện và trình bày):**

- **Bước 1:** Copy các UC + actor liên quan từ UC tổng quan của hệ thống vào phạm vi module.
- **Bước 2:** Mỗi giao diện chính trong module → đề xuất thành 1 UC con.
- **Bước 3:** Xác định mối quan hệ giữa từng UC con với UC chính:
  - `<<include>>` nếu UC con là bước BẮT BUỘC trong UC chính.
  - `<<extend>>` nếu UC con chỉ xảy ra trong một số điều kiện nhất định.
  - **Generalization** (tổng quát hóa): nếu có nhiều UC con tương tự nhau, gộp thành 1 UC trừu tượng làm cha chung.
- **Bước 4:** Gộp một số UC con tương tự thành một UC tổng quát hơn nếu cần thiết. VD: "Tìm theo tên" và "Tìm theo mã" → gộp thành UC cha "Tìm kiếm", hai UC con kế thừa (generalization).

**Lưu ý về tác nhân trừu tượng và tác nhân gián tiếp:**
- **Tác nhân trừu tượng:** Khi nhiều actor có đặc điểm chung (VD: Lễ tân, Nhân viên bán hàng đều là nhân viên), đề xuất actor trừu tượng chung rồi dùng generalization.
- **Tác nhân gián tiếp:** Xem xét người dùng gián tiếp có thể khởi phát chức năng nào (VD: Khách hàng gián tiếp kích hoạt UC "Đặt phòng").

**Kèm mô tả từng UC bằng văn xuôi** (ví dụ: "UC 'Tìm kiếm khách hàng': UC này cho phép UC [chính] tìm khách hàng")

```plantuml
@startuml
left to right direction
skinparam packageStyle rectangle

actor "Tên Actor 1" as A1
actor "Tên Actor 2" as A2

package "Module: [Tên module]" {
  usecase "[Tên UC chính đầy đủ]" as UC01
  usecase "[Tên UC con bắt buộc 1]" as UC02
  usecase "[Tên UC con bắt buộc 2]" as UC03
  usecase "[Tên UC mở rộng]" as UC04

  UC01 .> UC02 : <<include>>
  UC01 .> UC03 : <<include>>
  UC04 .> UC01 : <<extend>>
}

A1 --> UC01
A2 --> UC01
@enduml
```

**Lưu ý:** Mã UC (UC01, UC02...) PHẢI khớp chính xác với bảng UC tổng quan ở giai đoạn 1. Khi copy UC sang biểu đồ chi tiết module, giữ nguyên mã, không đổi alias.

Mô tả các UC của module:
1. "[Tên UC]": UC này cho phép UC [chính] [mô tả ngắn]
2. ...
