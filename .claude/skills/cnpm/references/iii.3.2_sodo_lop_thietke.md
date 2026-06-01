<!-- Pha III – Design, Section 3.2 -->

## III.3.2. Sơ đồ lớp thiết kế

**Kiến trúc DAO (BẮT BUỘC áp dụng):**
- Lớp **Boundary** (Form/Frame hoặc React Component): xử lý giao diện.
  - **JFrame:** bắt sự kiện `actionPerformed()`.
  - **React:** bắt sự kiện `handleSubmit()`, `onClick()`, `onChange()`.
- Lớp **DAO** (Data Access Object): thực hiện truy vấn CSDL. Đặt tên `[TênEntity]DAO`.
- Lớp **DAO** kế thừa từ lớp `DAO` chung (có `conn: Connection` và constructor `DAO()`).
- Lớp **Entity**: chỉ chứa thuộc tính + getter/setter, không chứa logic CSDL.

**Quy trình xác định chữ ký hàm (BẮT BUỘC trình bày reasoning):**

Với mỗi phương thức trong DAO, trình bày:
```
[Tên chức năng] => [tênHàmTiếngAnh()]
- Input: [liệt kê]
- Output: [liệt kê]
- Ứng viên tham số vào:
  [tênHàm](param1: KiểuDữLiệu, param2: KiểuDữLiệu)  → loại vì không hướng đối tượng
  [tênHàm](obj: TênLớp)                               → chọn (hướng đối tượng)
- Ứng viên tham số ra:
  [tênHàm](): void
  [tênHàm](): boolean                                  → chọn (cần biết thành công/thất bại)
  [tênHàm](): List<TênLớp>                             → chọn (trả về danh sách)
```

**Variant JFrame:**

```plantuml
@startuml
title Biểu đồ lớp thiết kế – Module [Tên] (JFrame)

class GDChinhFrm {
  -nv : NhanVien
  +btnChucNang : JButton
  +actionPerformed(e : ActionEvent) : void
}

class GDTimXFrm {
  -inTen : JTextField
  -subTim : JButton
  -outsubDSX : JTable
  +actionPerformed(e : ActionEvent) : void
}

class GDThemXFrm {
  -inTen : JTextField
  -inThuocTinh : JTextField
  -subThem : JButton
  -subHuy : JButton
  +ThemXFrm(x : TenEntity)
  +actionPerformed(e : ActionEvent) : void
}

abstract class DAO {
  #conn : Connection
  +DAO()
}

class TenEntityDAO {
  +timX(ten : String) : List<TenEntity>
  +themX(x : TenEntity) : boolean
  +luuX(x : TenEntity) : boolean
}

class TenEntity {
  -ma : int
  -ten : String
  +getTen() : String
  +setTen(ten : String) : void
}

DAO <|-- TenEntityDAO
TenEntityDAO --> TenEntity
GDChinhFrm --> GDTimXFrm
GDTimXFrm --> TenEntityDAO
GDThemXFrm --> TenEntityDAO
@enduml
```

**Variant React:**

**Quy ước đặt tên:** Tên class React dùng tiếng Anh + hậu tố loại component (`Page`, `Card`, `Panel`, `Modal`, `Form`, `Table`). Xem bảng quy ước chi tiết ở II.3.

```plantuml
@startuml
title Biểu đồ lớp thiết kế – Module [Tên] (React)

class EntityPage <<Component>> {
  -nv : NhanVien
  +btnChucNang : JSX.Element
  +handleClick() : void
}

class SearchEntityForm <<Component>> {
  -inTen : string (state)
  -tableData : Array (state)
  +handleSubmit() : void
  +handleChange(e) : void
  +render() : JSX
}

class AddEntityForm <<Component>> {
  -formData : State
  +handleSubmit() : void
  +handleChange(e) : void
  +render() : JSX
}

abstract class DAO {
  #conn : Connection
  +DAO()
}

class TenEntityDAO {
  +timX(ten : String) : List<TenEntity>
  +themX(x : TenEntity) : boolean
  +luuX(x : TenEntity) : boolean
}

class TenEntity {
  -ma : int
  -ten : String
  +getTen() : String
  +setTen(ten : String) : void
}

DAO <|-- TenEntityDAO
TenEntityDAO --> TenEntity
EntityPage --> SearchEntityForm
SearchEntityForm --> TenEntityDAO
AddEntityForm --> TenEntityDAO
@enduml
```
