---
name: cnpm
description: >
  Tự động tạo tài liệu phần mềm chuẩn Unified Process (UP) theo giáo trình Nhập môn
  Công nghệ Phần mềm. Kích hoạt skill này bất cứ khi nào người dùng đề cập đến:
  viết tài liệu UP, làm báo cáo CNPM, đặc tả use-case, phân tích thiết kế hệ thống
  theo module, vẽ biểu đồ lớp / tuần tự / ERD, viết kịch bản chuẩn & ngoại lệ,
  thiết kế CSDL theo UP, wireframe giao diện, viết test case hộp đen, hoặc bất kỳ
  yêu cầu nào liên quan đến 5 workflows / 4 pha của Unified Process. Luôn dùng skill
  này kể cả khi người dùng chỉ nói "làm phần requirements", "viết analysis cho module X",
  hay "sinh test case cho chức năng Y".
---

# UP Documentation Skill

Tạo tài liệu triển khai dự án phần mềm chuẩn **Unified Process (UP)** theo giáo trình
**Nhập môn Công nghệ Phần mềm**. 100% tiếng Việt. Biểu đồ UML dùng PlantUML.

---

## Nguyên tắc cốt lõi (BẮT BUỘC tuân thủ)

1. **Hướng Use-case:** Mọi phân tích, thiết kế đều xuất phát từ Use-case.
2. **BCE:** Luôn phân rã theo Boundary – Control – Entity.
3. **Phân biệt ngôn ngữ theo pha (NGHIÊM NGẶT):**
   - **Pha Phân tích:** Thông điệp sequence diagram = tiếng Việt tự nhiên + tên hàm tiếng Anh đơn giản (VD: `"Lớp Room gọi phương thức searchFreeRoom()"`, `"nhập ngày + nhấn Tìm"`)
   - **Pha Thiết kế:** Thông điệp = tên hàm tiếng Anh đầy đủ + kiểu dữ liệu (VD: `searchFreeRoom(checkin: Date, checkout: Date): List<Room>`, `actionPerformed(e: ActionEvent)`)
4. **Văn bản:** 100% tiếng Việt (trừ tên hàm/biến ở pha Thiết kế).
5. **UML:** PlantUML trong code block plantuml.
6. **Công nghệ giao diện:** Hỏi người dùng chọn JFrame (Java Swing) hoặc HTML (React) ngay từ BƯỚC 0 PLAN. Toàn bộ Boundary classes, wireframe, và sequence diagram phải thống nhất theo lựa chọn này.
7. **Diễn giải tuần tự (BẮT BUỘC cho II.4 và III.4):** Bên cạnh biểu đồ sequence diagram, PHẢI viết block diễn giải tuần tự dạng danh sách đánh số trong callout:
   - **II.4 (Phân tích):** Kịch bản phiên bản 2 — tiếng Việt tự nhiên, mô tả Actor ↔ Boundary ↔ Entity. Xem `references/ii.4_tuantu_phantich.md`.
   - **III.4 (Thiết kế):** Kịch bản phiên bản 3 — có tên hàm Java + kiểu dữ liệu, mô tả Actor ↔ Boundary ↔ DAO ↔ Entity. Xem `references/iii.4_tuantu_thietke.md`.

---

## Lựa chọn công nghệ giao diện

Ngay từ **BƯỚC 0 PLAN**, hỏi người dùng:

> **Bạn muốn thiết kế giao diện theo công nghệ nào?**
> 1. **JFrame (Java Swing)** — Desktop app, Boundary extends JFrame, dùng JButton/JTextField/JTable...
> 2. **HTML (React)** — Web app, Boundary là React component, dùng HTML form/input/table...

Lựa chọn này ảnh hưởng đến:
- **Mục 4 (Lớp BCE):** Tên và kiểu các thành phần giao diện
- **Mục 8 (Wireframe + Lớp TK):** Kiểu wireframe (ASCII desktop vs HTML layout) + class diagram chi tiết
- **Mục 9 (Tuần tự TK):** Cách bắt sự kiện (ActionListener vs onClick/handleSubmit)

---

## Luồng tổng thể

```
GIAI ĐOẠN 1 – Requirements TOÀN HỆ THỐNG
     → Bảng thuật ngữ
     → Mô hình nghiệp vụ bằng ngôn ngữ tự nhiên (2.1 → 2.6)
     → Mô hình nghiệp vụ bằng UML (3.1 → 3.3)
     ↓
GIAI ĐOẠN 2 – Đề xuất phân chia MODULE → chờ xác nhận
     ↓
GIAI ĐOẠN 3 – Với mỗi module (theo yêu cầu người dùng):
     Pha I – Requirements:   I.1  Mô hình nghiệp vụ bằng UML
     Pha II – Analysis:      II.1 Mô hình hóa chức năng
                              II.2 Mô hình hóa lớp
                              II.3 Sơ đồ lớp phân tích
                              II.4 Biểu đồ tuần tự phân tích
     Pha III – Design:       III.1  Thiết kế lớp thực thể
                              III.2  Thiết kế CSDL
                              III.3.1 Thiết kế giao diện
                              III.3.2 Sơ đồ lớp thiết kế
                              III.4  Biểu đồ tuần tự thiết kế
     Pha IV – Test:          IV   Cài đặt & Kiểm thử
```

---

## Hướng dẫn thực thi

### BƯỚC 0 – PLAN (BẮT BUỘC, luôn làm trước mọi thứ)

**Trước khi viết bất kỳ nội dung tài liệu nào**, phân tích yêu cầu của người dùng và sinh một **plan dưới dạng file `.md`** để user review và xác nhận.

Plan phải:
- **Xác định scope** dựa trên yêu cầu: toàn hệ thống, một module cụ thể, hay chỉ một vài mục.
- **Liệt kê từng mục sẽ viết** kèm thông tin quan trọng nhất đã suy luận được (không viết nội dung thật, chỉ tóm tắt những gì sẽ có).
- **Đặt câu hỏi làm rõ** nếu còn thiếu thông tin đầu vào quan trọng.

**Cấu trúc file plan:**

```markdown
# Plan tài liệu – [Tên hệ thống / Module / Phạm vi]

## Phạm vi
[Mô tả ngắn: viết toàn bộ / module X / chỉ mục Y, Z]

## Thông tin đầu vào đã có
- Tên hệ thống: ...
- Actor xác định được: ...
- Module / chức năng: ...
- Ngôn ngữ lập trình dự kiến (ảnh hưởng kiểu dữ liệu ở mục 6, 7): ...

## Câu hỏi cần làm rõ (nếu có)
- [ ] ...

## Danh sách mục sẽ viết

### Pha I – Requirements
| Mục | Tên | Nội dung chính sẽ có |
|-----|-----|----------------------|
| I.1 | Mô hình nghiệp vụ bằng UML | Actor: [A, B]; UC chính: [X]; UC con include: [a, b]; UC extend: [c] |

### Pha II – Analysis
| Mục | Tên | Nội dung chính sẽ có |
|-----|-----|----------------------|
| II.1 | Mô hình hóa chức năng | UC [X]: [N] bước, ngoại lệ tại bước [3, 10, 24] |
| II.2 | Mô hình hóa lớp | Lớp dự kiến: [A, B, C, D]; quan hệ n-n: [A–B] |
| II.3 | Sơ đồ lớp phân tích | Boundary: [Frm1, Frm2]; Entity: [A, B, C] |
| II.4 | Biểu đồ tuần tự phân tích | [N] biểu đồ cho [N] UC |

### Pha III – Design
| Mục | Tên | Nội dung chính sẽ có |
|-----|-----|----------------------|
| III.1 | Thiết kế lớp thực thể | Bổ sung kiểu dữ liệu Java; PK/FK; composition/aggregation |
| III.2 | Thiết kế CSDL | [N] bảng; bảng trung gian: [...] |
| III.3.1 | Thiết kế giao diện | [N] màn hình wireframe |
| III.3.2 | Sơ đồ lớp thiết kế | DAO cho: [A, B, C]; Boundary + Entity |
| III.4 | Biểu đồ tuần tự thiết kế | [N] biểu đồ |

### Pha IV – Test
| Mục | Tên | Nội dung chính sẽ có |
|-----|-----|----------------------|
| IV | Cài đặt & Kiểm thử | [N] TC; CSDL mẫu: [N] bảng |
```

Sau khi sinh plan, **chờ user xác nhận hoặc điều chỉnh** trước khi viết bất kỳ nội dung thật nào. Nếu user chỉ muốn làm một vài mục, chỉ giữ lại những mục đó trong plan rồi xác nhận lại.

---

### Giai đoạn 1 – Requirements toàn hệ thống
Đọc `references/requirements-system.md` và thực hiện đầy đủ.

Sau khi hoàn thành, **BẮT BUỘC** sinh đề xuất phân module theo mẫu:

```
Dựa trên các use-case đã xác định, mình đề xuất chia hệ thống thành [N] module:

| STT | Tên module | Phụ trách Use-case | Mô tả ngắn |
|-----|-----------|-------------------|------------|
| 1   | [Tên]     | UC01, UC02, ...   | ...        |
| 2   | [Tên]     | UC03, UC04, ...   | ...        |

Bạn có đồng ý với cách phân chia này không? Hay muốn điều chỉnh?
```

Chỉ tiếp tục khi người dùng xác nhận.

### Giai đoạn 3 – Triển khai từng module
Hỏi người dùng muốn bắt đầu với module nào.
Đọc `references/table_of_contents.md` để biết danh sách 11 mục theo 4 pha, sau đó đọc từng file tương ứng:
- **Pha I:** `references/i.1_mohinh_nghiepvu.md`
- **Pha II:** `references/ii.1_mohinh_hoa_chucnang.md`, `ii.2_mohinh_hoa_lop.md`, `ii.3_sodo_lop_phantich.md`, `ii.4_tuantu_phantich.md`
- **Pha III:** `references/iii.1_thietke_lop_thucthe.md`, `iii.2_thietke_coso_dulieu.md`, `iii.3.1_thietke_giaodien.md`, `iii.3.2_sodo_lop_thietke.md`, `iii.4_tuantu_thietke.md`
- **Pha IV:** `references/iv_kiemthu.md`

Thực hiện đúng các mục đã được xác nhận trong plan.
Sau mỗi pha, hỏi: *"Pha [X] đã hoàn thành. Bạn có muốn điều chỉnh gì không trước khi sang pha tiếp theo?"*

---

## Định dạng đầu ra chung

- Tiêu đề mỗi mục: `## [Pha].[Số]. [Tên mục]` (VD: `## II.3. Sơ đồ lớp phân tích`)
- Bảng Markdown chuẩn, có header rõ ràng.
- PlantUML đặt trong code block plantuml.
- Wireframe dùng ASCII box diagram (xem ví dụ trong `references/module-phases.md`).

### Quy tắc Columns (BẮT BUỘC cho Notion output)

<callout icon="🔑" color="purple">
**Nguyên tắc cứng:** Tối đa 4 columns. Nếu BẤT KỲ bước nào chứa bảng (markdown table hoặc HTML table), tối đa chỉ được 2 columns.
</callout>

| Tình huống | Số cột | Cách nhóm |
|------------|--------|-----------|
| Quy trình 2–3 bước, không có bảng | 2–3 cột | Mỗi bước 1 cột |
| Quy trình 4 bước, không có bảng | 4 cột (nếu ngắn) hoặc 2 cột (2 bước/cột) | Nhóm từng đôi |
| Quy trình 4–5 bước, CÓ BẢNG | **2 cột** | Nhóm bước lại, mỗi cột 2–3 bước |
| Quy trình 5+ bước | **2 cột** | Bắt buộc nhóm, không quá 4 |

### Quy tắc Callout Pairs (BẮT BUỘC)

Khi có 2 callout liên quan (VD: "Mục tiêu" + "Đầu vào"), LUÔN đặt trong 2 columns với heading tiêu đề:

```html
<columns>
<column>

### Mục tiêu
<callout icon="🎯" color="blue">
Nội dung mục tiêu...
</callout>

</column>
<column>

### Đầu vào
<callout icon="📝" color="gray">
Nội dung đầu vào...
</callout>

</column>
</columns>
```

---

## PlantUML Style Rules (BẮT BUỘC)

### Tổng quan layout

Mọi biểu đồ UML **PHẢI** tuân thủ style sau:

```plantuml
@startuml
left to right direction
skinparam linetype ortho
skinparam packageStyle rectangle
@enduml
```

- **`left to right direction`** — layout ngang từ trái sang phải
- **`skinparam linetype ortho`** — đường thẳng gấp khúc, KHÔNG cong
- **`skinparam packageStyle rectangle`** — package dạng hình chữ nhật

### Biểu đồ lớp (Class Diagram)

**Quy tắc chung:**
- Lớp xếp theo chiều ngang, chia rõ 3 zone: Boundary | DAO/Control | Entity
- Đường nối thẳng, gấp khúc (`linetype ortho`)
- Package dọc theo chiều ngang (trái → phải)
- **KHÔNG xếp dọc** — classes trong mỗi package phải dàn ngang, không chồng chất

**Cách tránh xếp dọc (BẮT BUỘC):**
- Dùng `together { }` để nhóm classes nằm ngang trong cùng package
- Nếu nhiều classes, chia thành nhiều package nhỏ thay vì 1 package lớn
- Dùng hidden links `hidden` để kéo classes ra xa nhau theo chiều ngang
- `skinparam packageMaxWidth 800` nếu cần mở rộng package

**Boundary classes — Theo lựa chọn công nghệ:**

**Option A: JFrame (Java Swing)**

| Field type | Kiểu | Ví dụ |
|------------|------|-------|
| Frame | `JFrame` extends... | Giao diện chính |
| Button | `JButton` | Nút bấm |
| TextField | `JTextField` | Ô nhập text |
| PasswordField | `JPasswordField` | Ô nhập mật khẩu |
| Table | `JTable` | Bảng hiển thị |
| ComboBox | `JComboBox` | Dropdown chọn |
| Label | `JLabel` | Nhãn hiển thị |

- Mỗi Boundary class implements `ActionListener`
- Method: `actionPerformed(e: ActionEvent): void`

**Option B: HTML (React)**

| Field type | Kiểu | Ví dụ |
|------------|------|-------|
| Page | `Component` (React) | Giao diện chính |
| Button | `<button>` / `onClick` | Nút bấm |
| Input | `<input type="text">` | Ô nhập text |
| Password | `<input type="password">` | Ô nhập mật khẩu |
| Table | `<table>` / map array | Bảng hiển thị |
| Select | `<select>` / dropdown | Dropdown chọn |
| Label | `<label>` / `<span>` | Nhãn hiển thị |

- Mỗi Boundary class là React functional component
- Event handler: `handleSubmit`, `onClick`, `onChange` (không cần interface)
- **Tên class:** dùng tiếng Anh + hậu tố loại component (xem bảng dưới). KHÔNG dùng `Page[Name]` cho tất cả.

| Hậu tố | Loại component | Ví dụ |
|--------|---------------|-------|
| `Page` | Trang gắn URL/Router | `RoomPage`, `OrderPage` |
| `Card` | Ô thông tin nhỏ trong danh sách | `RoomCard`, `ProductCard` |
| `Panel` | Vùng nội dung lớn trên trang | `SessionDetailPanel` |
| `Modal` | Hộp thoại bật lên | `ExtendTimeModal` |
| `Form` | Vùng nhập liệu | `OrderForm`, `AddClientForm` |
| `Table` | Bảng dữ liệu | `RoomListTable` |

**DAO classes:**
- Abstract class `DAO` có `#conn: Connection` + `+DAO()`
- Mỗi entity có class DAO kế thừa `DAO`
- Methods rõ ràng tên tiếng Anh + tham số kiểu

**Entity classes:**
- Thuộc tính private với kiểu Java cụ thể
- `+getter/setter` (không cần hiện chi tiết)

### Ví dụ template — JFrame (Java Swing)

```plantuml
@startuml
left to right direction
skinparam linetype ortho
skinparam packageStyle rectangle
skinparam packageMaxWidth 800
title Biểu đồ lớp – Module [Tên] (JFrame)

package "Boundary" #DDEEFF {
  together {
    class FrmA extends JFrame implements ActionListener {
      -inField : JTextField
      -btnAction : JButton
      +actionPerformed(e : ActionEvent) : void
    }
    class FrmB extends JFrame implements ActionListener {
      -outTable : JTable
      +actionPerformed(e : ActionEvent) : void
    }
  }
}

package "DAO" #FFE0B2 {
  abstract class DAO {
    #conn : Connection
    +DAO()
  }
  together {
    class ADao extends DAO {
      +findByName(name : String) : List<A>
      +save(entity : A) : boolean
    }
    class BDao extends DAO {
      +findAll() : List<B>
      +delete(id : int) : boolean
    }
  }
}

package "Entity" #FFF3CD {
  together {
    class A {
      -id : int
      -name : String
      +getter/setter
    }
    class B {
      -id : int
      -value : String
      +getter/setter
    }
  }
}

FrmA --> ADao
FrmB --> BDao
ADao --> A
BDao --> B
@enduml
```

### Ví dụ template — HTML (React)

```plantuml
@startuml
left to right direction
skinparam linetype ortho
skinparam packageStyle rectangle
skinparam packageMaxWidth 800
title Biểu đồ lớp – Module [Tên] (React)

package "Boundary" #DDEEFF {
  together {
    class EntityPage <<Component>> {
      -formData : State
      +handleSubmit() : void
      +render() : JSX
    }
    class SearchEntityForm <<Component>> {
      -tableData : State
      +render() : JSX
    }
  }
}

package "DAO" #FFE0B2 {
  abstract class DAO {
    #conn : Connection
    +DAO()
  }
  together {
    class ADao extends DAO {
      +findByName(name : String) : List<A>
      +save(entity : A) : boolean
    }
    class BDao extends DAO {
      +findAll() : List<B>
      +delete(id : int) : boolean
    }
  }
}

package "Entity" #FFF3CD {
  together {
    class A {
      -id : int
      -name : String
      +getter/setter
    }
    class B {
      -id : int
      -value : String
      +getter/setter
    }
  }
}

EntityPage --> SearchEntityForm
SearchEntityForm --> ADao
EntityPage --> BDao
ADao --> A
BDao --> B
@enduml
```

### Biểu đồ Tuần tự (Sequence Diagram)

- Participants xếp theo thứ tự: Actor → Boundary → [Control] → DAO → Entity
- Thông điệp đánh số liên tục
- Dùng `alt` cho ngoại lệ
- Phân tích: tiếng Việt · Thiết kế: tên hàm tiếng Anh

### Biểu đồ UC (Use Case)

- `left to right direction`
- Actors bên trái, use cases bên phải trong package
- `<<include>>` và `<<extend>>` dùng mũi tên đứt nét
- **Generalization:** UC con kế thừa UC cha — mũi tên tam giác rỗng hướng lên UC cha. VD: "Tìm sách" là cha, "Tìm theo tên sách" và "Tìm theo mã sách" là con
- **Extension points:** Hiển thị trong UC cha, ghi rõ UC extend nào mở rộng tại điểm nào
- **Layout ngang:** Sắp xếp UC theo chiều rộng, tránh chồng chất theo chiều dọc. UC chính ở giữa, các UC include/extend/generalization tỏa ra hai bên

**Cấu trúc UC phân rã chuẩn:**
```
Actor (trái) → UC chính (giữa) → UC include (phải)
                                  ↘ UC extend (dưới phải)
UC cha (trên) ← UC con generalization (dưới)
```

**Ví dụ PlantUML UC với generalization:**
```plantuml
@startuml
left to right direction
skinparam linetype ortho
skinparam packageStyle rectangle
title Biểu đồ Use Case – Quản lý mượn sách

left to right direction

rectangle "Quản lý mượn sách" {
  usecase "Mượn sách" as UC1
  usecase "Đăng nhập" as UC2
  usecase "Lập phiếu mượn" as UC3
  usecase "Tìm sách" as UC4
  usecase "Tìm thông tin độc giả" as UC5
  usecase "Thêm độc giả" as UC6
  usecase "Tìm theo mã độc giả" as UC7
  usecase "Tìm theo tên độc giả" as UC8
  usecase "Tìm theo mã sách" as UC9
  usecase "Tìm theo tên sách" as UC10
}

actor "Thư thư" as A1

A1 --> UC1
UC1 ..> UC2 : <<include>>
UC1 ..> UC3 : <<include>>
UC1 ..> UC4 : <<include>>
UC1 ..> UC5 : <<include>>
UC6 ..> UC5 : <<Extend>>
UC4 <|-- UC9
UC4 <|-- UC10
UC5 <|-- UC7
UC5 <|-- UC8
@enduml
```
