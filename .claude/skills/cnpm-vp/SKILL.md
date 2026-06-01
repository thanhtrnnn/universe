---
name: cnpm-vp
description: >
  Tạo biểu đồ UML trong Visual Paradigm thông qua VP MCP server, rồi xuất ảnh và nhúng vào
  tài liệu UP. Kích hoạt khi người dùng muốn: vẽ biểu đồ UML trong Visual Paradigm,
  tạo diagram trong VP, export ảnh VP, nhúng ảnh VP vào tài liệu, hoặc bất kỳ yêu cầu
  nào liên quan đến việc sử dụng Visual Paradigm kết hợp với tài liệu CNPM/UP.
  LUÔN dùng skill này khi người dùng nhắc đến "Visual Paradigm", "VP", "vẽ trong VP",
  "xuất ảnh VP", hoặc muốn diagram chuyên nghiệp thay vì PlantUML code block.
---

# CNPM-VP Skill — Visual Paradigm MCP Integration

Tạo biểu đồ UML trực tiếp trong Visual Paradigm qua VP MCP server, xuất ảnh, và nhúng vào
tài liệu UP. Kết hợp với skill `cnpm` để sinh nội dung tài liệu.

---

## Điều kiện tiên quyết

VP MCP server phải đang chạy và được đăng ký trong `.mcp.json`.

**Bước 1 — Khởi động VP MCP server:**
```bash
cd visual-paradigm-mcp-plugin
./run docker-up
```

**Bước 2 — Đăng ký MCP server với Claude Code:**
```bash
.claude/skills/cnpm-vp/scripts/vp-mcp-setup.sh
```
Script này tự tạo/cập nhật `.mcp.json` ở project root.

**Bước 3 — Xác nhận kết nối:**
```bash
.claude/skills/cnpm-vp/scripts/vp-mcp-verify.sh
```
Script kiểm tra: port mở, SSE endpoint reachable, MCP protocol respond, `.mcp.json` đúng cấu hình.

Sau đó restart Claude Code để nhận MCP config mới.

---

## Nguyên tắc cốt lõi

1. **Skill `cnpm` sinh nội dung, skill `cnpm-vp` vẽ trong VP.** Không tự viết PlantUML khi người dùng muốn dùng VP.
2. **Thứ tự tạo diagram:** Tạo diagram → thêm elements → thêm relationships → auto layout → export.
3. **Ảnh VP ưu tiên hơn PlantUML** khi người dùng yêu cầu hoặc khi diagram quá phức tạp cho code block.
4. **Luôn auto layout** sau khi thêm xong tất cả elements — VP layout engine tốt hơn sắp xếp thủ công.

---

## Nguyên tắc cấu trúc (khi vẽ trong VP)

Khi tạo biểu đồ trong VP, PHẢI tuân thủ các quy tắc cấu trúc sau:

1. **BCE:** Class diagram PHẢI phân rõ 3 package: Boundary | DAO/Control | Entity
2. **Màu sắc package BCE (BẮT BUỘC):** Mỗi package PHẢI có màu nền riêng:

| Package | Màu | Hex |
|---------|-----|-----|
| Boundary | Xanh dương nhạt | `#DDEEFF` |
| DAO/Control | Cam nhạt | `#FFE0B2` |
| Entity | Vàng nhạt | `#FFF3CD` |

Dùng `addPackage` để tạo container, hoặc `addClass` với tham số `packageName` + `packageColor`.

3. **Stereotype class (BẮT BUỘC):** Mỗi class PHẢI có stereotype:

| Loại class | Stereotype | Ví dụ |
|-----------|-----------|-------|
| Boundary (JFrame) | `Boundary` | `<<Boundary>>` trên GDChinhFrm |
| Boundary (React) | `Component` | `<<Component>>` trên RoomPage |
| DAO | `DAO` | `<<DAO>>` trên SachDAO |
| Entity | `Entity` | `<<Entity>>` trên Sach |
| Interface | `Interface` | `<<Interface>>` trên ISachService |

Dùng `addClass` với tham số `stereotype`. VP hiển thị stereotype dạng `<<tên>>` phía trên tên class.

4. **Phân biệt ngôn ngữ theo pha (NGHIÊM NGẶT):**
   - **Pha Phân tích:** Thông điệp = tiếng Việt tự nhiên + tên hàm tiếng Anh đơn giản (VD: `"nhập ngày + nhấn Tìm"`, `"searchFreeRoom()"`, `"hiển thị danh sách"`)
   - **Pha Thiết kế:** Thông điệp = tên hàm tiếng Anh đầy đủ + kiểu dữ liệu (VD: `searchFreeRoom(checkin: Date, checkout: Date): List<Room>`)
5. **UC Decomposition:** Include → Extend → Generalization. UC chính ở giữa, UC con tỏa ra.
6. **Horizontal layout:** Classes trong cùng package phải dàn ngang, KHÔNG xếp dọc.
7. **Sequence participant order:** Actor → Boundary → [Control] → DAO → Entity
8. **Công nghệ giao diện:** Phải thống nhất JFrame (Java Swing) hoặc HTML (React) từ đầu. Ảnh hưởng đến Boundary class attributes.

### Chọn công nghệ giao diện

Hỏi người dùng ngay từ đầu:
- **JFrame (Java Swing):** Boundary = JFrame, attributes = JTextField/JButton/JTable, event = `actionPerformed(e: ActionEvent)`. Tên class: `GD[TênMànHình]Frm`.
- **HTML (React):** Boundary = Component, attributes = State/JSX, event = `handleSubmit/onClick`. Tên class: tiếng Anh + hậu tố loại component:

| Hậu tố | Loại component | Ví dụ |
|--------|---------------|-------|
| `Page` | Trang gắn URL/Router | `RoomPage`, `OrderPage` |
| `Card` | Ô thông tin nhỏ trong danh sách | `RoomCard`, `ProductCard` |
| `Panel` | Vùng nội dung lớn trên trang | `SessionDetailPanel` |
| `Modal` | Hộp thoại bật lên | `ExtendTimeModal` |
| `Form` | Vùng nhập liệu | `OrderForm`, `AddClientForm` |
| `Table` | Bảng dữ liệu | `RoomListTable` |

### Tiền tố thuộc tính Boundary (Phân tích)

Boundary classes trong pha phân tích dùng tiền tố standardized:

| Tiền tố | Ý nghĩa | Ví dụ |
|---------|---------|-------|
| `in_` | Trường nhập liệu | `in_ten`, `in_username` |
| `out_` | Hiển thị/output | `out_ketQua`, `out_danhSach` |
| `sub_` | Nút bấm/submit | `sub_tim`, `sub_luu` |
| `outsub_` | Bảng/danh sách có thể click | `outsub_danhSachX` |
| `inout_` | Trường đọc-ghi | `inout_ten` |

---

## Danh sách MCP Tools (39 tools)

### Diagram Management

| Tool | Mô tả | Tham số chính |
|------|-------|---------------|
| `listDiagrams` | Liệt kê tất cả diagram (filter: UseCase/Class/Sequence/ER) | `type` ("" = all) |
| `getDiagramElements` | Lấy tất cả elements trên diagram | `diagramName` |
| `autoLayoutDiagram` | Tự động căn layout | `diagramName` |
| `removeDiagramElement` | Xóa element khỏi diagram | `diagramName`, `elementName` |

### Use Case Diagram

| Tool | Mô tả | Tham số chính |
|------|-------|---------------|
| `createUseCaseDiagram` | Tạo diagram mới | `diagramName` |
| `addActor` | Thêm actor | `actorName`, `diagramName` |
| `addUseCase` | Thêm use case | `useCaseName`, `diagramName` |
| `addRelationship` | Thêm Include/Extend/Generalization | `sourceName`, `targetName`, `relationshipType` |
| `generateUseCaseReport` | Sinh báo cáo phân tích | `diagramName` |

### Class Diagram

| Tool | Mô tả | Tham số chính |
|------|-------|---------------|
| `createClassDiagram` | Tạo diagram mới | `diagramName` |
| `addClass` | Thêm class | `diagramName`, `className`, `packageName`, `packageColor`, `stereotype`, `isAbstract`, `extendsClass`, `implementsInterfaces` |
| `addPackage` | Thêm package có màu nền | `diagramName`, `packageName`, `backgroundColor` |
| `setClassColor` | Đặt màu nền class | `diagramName`, `className`, `backgroundColor` |
| `addAttribute` | Thêm thuộc tính | `className`, `attributeName`, `attributeType`, `visibility` |
| `addOperation` | Thêm phương thức | `className`, `operationName`, `returnType`, `params` |
| `addAssociation` | Thêm association | `diagramName`, `fromClass`, `toClass`, multiplicities, `name` |
| `addGeneralization` | Thêm kế thừa | `diagramName`, `fromClass`, `toClass` |
| `addAggregation` | Thêm aggregation (◇) | `diagramName`, `fromClass`, `toClass`, multiplicities |
| `addComposition` | Thêm composition (◆) | `diagramName`, `fromClass`, `toClass`, multiplicities |
| `addDependency` | Thêm dependency (-->) | `diagramName`, `fromClass`, `toClass` |
| `addRealization` | Thêm implements | `diagramName`, `fromClass`, `toClass` |
| `addInterface` | Thêm interface | `diagramName`, `interfaceName` |
| `generateClassReport` | Sinh báo cáo | `diagramName` |

### ERD

| Tool | Mô tả | Tham số chính |
|------|-------|---------------|
| `createErd` | Tạo ERD mới | `diagramName` |
| `addTable` | Thêm bảng | `diagramName`, `tableName` |
| `addColumn` | Thêm cột | `tableName`, `columnName`, `columnType`, `length`, `scale`, `isPrimaryKey`, `isNullable` |
| `addForeignKey` | Thêm FK | `diagramName`, `fromTable`, `toTable`, columns, `relationshipName` |
| `addTableRelationship` | Thêm quan hệ bảng | `diagramName`, `fromTable`, `toTable`, `type`, multiplicities |
| `generateDdl` | Sinh DDL | `diagramName` |
| `generateErdReport` | Sinh báo cáo | `diagramName` |

### Sequence Diagram

| Tool | Mô tả | Tham số chính |
|------|-------|---------------|
| `createSequenceDiagram` | Tạo diagram mới | `diagramName` |
| `addLifeline` | Thêm lifeline | `diagramName`, `lifelineName`, `className`, `lifelineType`, `alias` |
| `addActivation` | Thêm activation bar | `diagramName`, `lifelineName` |
| `addMessage` | Thêm message | `diagramName`, `fromLifeline`, `toLifeline`, `messageName`, `sequenceNumber`, `messageType` |
| `addReturnMessage` | Thêm return message | `diagramName`, `fromLifeline`, `toLifeline`, `messageName`, `sequenceNumber` |
| `addCombinedFragment` | Thêm alt/opt/loop | `diagramName`, `operator`, `guard`, `coveredLifelines` |
| `generateSequenceReport` | Sinh báo cáo | `diagramName` |

---

## Quy trình tạo diagram theo loại

### Use Case Diagram

```
1. createUseCaseDiagram(diagramName)
2. addActor(actorName, diagramName)        — cho mỗi actor
3. addUseCase(useCaseName, diagramName)    — cho mỗi UC (bao gồm UC con generalization)
4. addRelationship(source, target, type)   — Include, Extend, hoặc Generalization
5. autoLayoutDiagram(diagramName)          — LUÔN chạy cuối cùng
6. generateUseCaseReport(diagramName)      — optional, kiểm tra kết quả
```

**Thứ tự thêm relationships:**
1. Include (UC chính → UC phụ bắt buộc)
2. Extend (UC mở rộng → UC chính)
3. Generalization (UC con → UC cha, mũi tên tam giác rỗng)

**Layout:** UC chính ở giữa, UC include tỏa phải, UC extend tỏa dưới phải, UC generalization tỏa dưới. Tránh xếp dọc — luôn dàn ngang.

**Ví dụ: Module Quản lý Khách hàng**
```
createUseCaseDiagram("UC - QuanLyKhachHang")
addActor("NhanVien", "UC - QuanLyKhachHang")
addUseCase("Tim kiem khach hang", "UC - QuanLyKhachHang")
addUseCase("Them moi khach hang", "UC - QuanLyKhachHang")
addUseCase("Xac minh CCCD", "UC - QuanLyKhachHang")
addUseCase("Tim theo ten", "UC - QuanLyKhachHang")
addUseCase("Tim theo ma", "UC - QuanLyKhachHang")
addRelationship("Tim kiem khach hang", "Xac minh CCCD", "Include")
addRelationship("Tim theo ten", "Tim kiem khach hang", "Generalization")
addRelationship("Tim theo ma", "Tim kiem khach hang", "Generalization")
autoLayoutDiagram("UC - QuanLyKhachHang")
```

### Class Diagram

**Quy tắc BCE (BẮT BUỘC):** Class diagram PHẢI phân rõ 3 nhóm class:
- **Boundary** (trái): Giao diện — JFrame (Swing) hoặc Component (React)
  - **JFrame:** tên class = `GD[TênMànHình]Frm`
  - **React:** tên class = tiếng Anh + hậu tố (`Page`, `Card`, `Panel`, `Modal`, `Form`, `Table`) — xem bảng ở mục "Chọn công nghệ giao diện"
- **DAO/Control** (giữa): Abstract DAO + DAO con kế thừa
- **Entity** (phải): Lớp thực thể từ phân tích

**Mẫu Abstract DAO (Pha Thiết kế — BẮT BUỘC):**
Class diagram thiết kế PHẢI có class AbstractDAO:
```
1. addClass(diagram, "AbstractDAO", packageName="DAO", packageColor="#FFE0B2", stereotype="DAO", isAbstract=true)
2. addAttribute("AbstractDAO", "conn", "Connection", "#")  // protected
3. addOperation("AbstractDAO", "AbstractDAO", "void", "")  // constructor
4. addGeneralization(diagram, "SachDAO", "AbstractDAO")    // mỗi DAO con kế thừa
```

**Màu sắc package (BẮT BUỘC):**
Dùng `addClass` với tham số `packageName` + `packageColor` để tự động đặt class vào package có màu:
```
addClass("Class - Sach", "GDChinhFrm", "Boundary", "#DDEEFF", "Boundary", false, "", "")
addClass("Class - Sach", "AbstractDAO", "DAO", "#FFE0B2", "DAO", true, "", "")
addClass("Class - Sach", "SachDAO", "DAO", "#FFE0B2", "DAO", false, "AbstractDAO", "")
addClass("Class - Sach", "Sach", "Entity", "#FFF3CD", "Entity", false, "", "")
```

**Thứ tự tạo class diagram:**
```
1. createClassDiagram(diagramName)
2. addClass — Boundary classes (mỗi giao diện = 1 class)
3. addClass — Abstract DAO class
4. addClass — DAO classes (kế thừa Abstract DAO)
5. addClass — Entity classes (từ phân tích thực thể)
6. addAttribute — cho mỗi class (Boundary: UI components; Entity: private fields; DAO: methods)
7. addOperation — cho mỗi class (Boundary: event handlers; DAO: CRUD methods)
8. addRelationships — theo thứ tự:
   a. addGeneralization — DAO extends Abstract DAO
   b. addComposition — Entity lifetime-dependent (TheBanDoc◆BanDoc, CTPhieuMuon◆PhieuMuon)
   c. addAggregation — Entity independent (DauSach◇CTPhieuMuon)
   d. addDependency — Boundary --> DAO
   e. addAssociation — Entity ↔ Entity (structural links)
9. autoLayoutDiagram(diagramName)
```

**Bảng hướng dẫn chọn relationship:**

| Quan hệ | Ký hiệu | Khi nào dùng |
|---------|---------|---------------|
| Generalization | Tam giác rỗng | DAO kế thừa AbstractDAO; UC con kế thừa UC cha |
| Composition (◆) | Hình thoi đặc | Lifetime dependent — TheBanDoc-BanDoc, CTPhieuMuon-PhieuMuon |
| Aggregation (◇) | Hình thoi rỗng | Independent, shared — DauSach-CTPhieuMuon |
| Association | Đường liền | General structural link giữa entities |
| Dependency | Đường chấm | Boundary "sử dụng" DAO |

**Ví dụ: Biểu đồ lớp Module Mượn Sách (JFrame)**
```
createClassDiagram("Class - MuonSach")
// Boundary (package #DDEEFF)
addClass("Class - MuonSach", "FrmMuonSach", "Boundary", "#DDEEFF", "Boundary", false, "", "")
addAttribute("FrmMuonSach", "inMaBanDoc", "JTextField", "private")
addAttribute("FrmMuonSach", "btnTimKiem", "JButton", "private")
addAttribute("FrmMuonSach", "tblKetQua", "JTable", "private")
addOperation("FrmMuonSach", "actionPerformed", "void", "e:ActionEvent")
// Abstract DAO (package #FFE0B2)
addClass("Class - MuonSach", "AbstractDAO", "DAO", "#FFE0B2", "DAO", true, "", "")
addAttribute("AbstractDAO", "conn", "Connection", "#")
addOperation("AbstractDAO", "AbstractDAO", "void", "")
// DAO (kế thừa AbstractDAO)
addClass("Class - MuonSach", "BanDocDAO", "DAO", "#FFE0B2", "DAO", false, "AbstractDAO", "")
addClass("Class - MuonSach", "PhieuMuonDAO", "DAO", "#FFE0B2", "DAO", false, "AbstractDAO", "")
addOperation("BanDocDAO", "timTheoMa", "BanDoc", "ma:String")
addOperation("PhieuMuonDAO", "taoPhieu", "boolean", "pm:PhieuMuon")
// Entity (package #FFF3CD)
addClass("Class - MuonSach", "BanDoc", "Entity", "#FFF3CD", "Entity", false, "", "")
addClass("Class - MuonSach", "TheBanDoc", "Entity", "#FFF3CD", "Entity", false, "", "")
addClass("Class - MuonSach", "PhieuMuon", "Entity", "#FFF3CD", "Entity", false, "", "")
addClass("Class - MuonSach", "CTPhieuMuon", "Entity", "#FFF3CD", "Entity", false, "", "")
addClass("Class - MuonSach", "DauSach", "Entity", "#FFF3CD", "Entity", false, "", "")
addAttribute("BanDoc", "ma", "String", "private")
addAttribute("BanDoc", "ten", "String", "private")
addAttribute("PhieuMuon", "ngayMuon", "Date", "private")
// Relationships
addComposition("Class - MuonSach", "BanDoc", "TheBanDoc", "1", "1")
addComposition("Class - MuonSach", "BanDoc", "PhieuMuon", "1", "n")
addComposition("Class - MuonSach", "PhieuMuon", "CTPhieuMuon", "1", "n")
addAggregation("Class - MuonSach", "DauSach", "CTPhieuMuon", "1", "n")
addDependency("Class - MuonSach", "FrmMuonSach", "BanDocDAO")
addDependency("Class - MuonSach", "FrmMuonSach", "PhieuMuonDAO")
autoLayoutDiagram("Class - MuonSach")
```

### ERD

**Quy tắc đặt tên ERD (BẮT BUỘC):**

| Quy tắc | Mẫu | Ví dụ |
|---------|-----|-------|
| Tên bảng | `tbl` + TênEntity | `tblSach`, `tblNhanVien` |
| Cột PK | `ma` hoặc `id` | `ma : integer(10) <<PK>>` |
| Cột FK | `tbl` + TênBangCha + `ma` | `tblNhanVienma : integer(10) <<FK>>` |
| Quan hệ FK | Parent `1` to Child `n` | `tblNhanVien \|\|--o{ tblSach` |

**Ánh xạ kiểu dữ liệu:**

| Java Type | SQL Type |
|-----------|----------|
| String | varchar(255) |
| int | integer(10) |
| double | double(10) |
| Date | date |

```
1. createErd(diagramName)
2. addTable(diagramName, tableName)                               — cho mỗi bảng (dùng `tbl` + tên entity)
3. addColumn(tableName, columnName, type, length, scale, PK, nullable)  — cho mỗi cột
4. addForeignKey(diagramName, from, to, columns, fkName)          — cho mỗi FK
5. addTableRelationship(diagramName, from, to, type, multiplicities)  — nếu cần
6. autoLayoutDiagram(diagramName)
7. generateDdl(diagramName)                                        — optional, sinh DDL
```

**Ví dụ:**
```
createErd("ERD - QuanLyKhachHang")
addTable("ERD - QuanLyKhachHang", "tblKhachHang")
addColumn("tblKhachHang", "ma", "INT", 10, 0, true, false)
addColumn("tblKhachHang", "ten", "VARCHAR", 255, 0, false, false)
addColumn("tblKhachHang", "cccd", "VARCHAR", 20, 0, false, false)
addTable("ERD - QuanLyKhachHang", "tblHopDong")
addColumn("tblHopDong", "ma", "INT", 10, 0, true, false)
addColumn("tblHopDong", "ngayKy", "DATE", 0, 0, false, false)
addColumn("tblHopDong", "tblKhachHangma", "INT", 10, 0, false, false)
addForeignKey("ERD - QuanLyKhachHang", "tblHopDong", "tblKhachHang", "tblKhachHangma", "ma", "FK_KH_HD")
autoLayoutDiagram("ERD - QuanLyKhachHang")
generateDdl("ERD - QuanLyKhachHang")
```

### Sequence Diagram

**Thứ tự participant (BẮT BUỘC):**
```
Actor → FrmX (Boundary) → [CtrlX (Control)] → XDao (DAO) → X (Entity)
```

**Alias lifeline (BẮT BUỘC):**
Mỗi lifeline PHẢI có alias ngắn gọn để layout đọc được:

| Vai trò | Mẫu alias | Ví dụ |
|---------|----------|-------|
| Actor | `Actor` | `Actor` |
| Boundary | `B` + index | `B0`, `B1` |
| Control | `C` + index | `C0` |
| DAO | `DAO` + index | `DAO1` |
| Entity | `E` + index | `E1` |

Dùng `addLifeline` với tham số `alias`. Alias xuất hiện làm label của lifeline.

**Lifeline type (BẮT BUỘC):**
Mỗi lifeline PHẢI có type stereotype để VP hiển thị icon đúng:
- `actor` — stick figure
- `boundary` — circle + line
- `control` — arrow
- `entity` — rectangle

Dùng `addLifeline` với tham số `lifelineType`.

**Số thứ tự thông điệp (BẮT BUỘC):**
Tất cả messages PHẢI được đánh số tuần tự:
- Dùng tham số `sequenceNumber` trên `addMessage` và `addReturnMessage`
- Bắt đầu từ `1`, tăng dần cho mỗi message
- Return message dùng số thứ tự tiếp theo (hoặc cùng số với call)
- Combined fragment: message bên trong tiếp tục đánh số

Ví dụ: 1, 2, 3, 4, 5 (trong alt: 5.1, 5.2), 6

**Ngôn ngữ theo pha:**
- **Phân tích:** Thông điệp = tiếng Việt tự nhiên + tên hàm tiếng Anh đơn giản: `"nhập ngày + nhấn Tìm"`, `"searchFreeRoom()"`, `"hiển thị kết quả"`
- **Thiết kế:** Thông điệp = tên hàm tiếng Anh đầy đủ + kiểu dữ liệu: `searchFreeRoom(checkin: Date, checkout: Date): List<Room>`, `actionPerformed(e: ActionEvent)`

**Diễn giải tuần tự (BẮT BUỘC alongside diagram):**

Bên cạnh biểu đồ sequence diagram, PHẢI viết thêm block diễn giải tuần tự dưới dạng danh sách đánh số:
- **Phân tích → Kịch bản phiên bản 2:** Danh sách bước bằng tiếng Việt tự nhiên, mô tả Actor ↔ Boundary ↔ Entity. Xem `cnpm/references/ii.4_tuantu_phantich.md` để biết format chi tiết.
- **Thiết kế → Kịch bản phiên bản 3:** Danh sách bước có tên hàm Java + kiểu dữ liệu, mô tả Actor ↔ Boundary ↔ DAO ↔ Entity. Xem `cnpm/references/iii.4_tuantu_thietke.md` để biết format chi tiết.

Block diễn giải giúp người đọc hiểu luồng xử lý mà không cần đọc biểu đồ UML. Luôn đặt ngay sau biểu đồ, trong callout `📖` màu green.

```
1. createSequenceDiagram(diagramName)
2. addLifeline — theo thứ tự: Actor, Boundary, [Control], DAO, Entity
3. addActivation — trước mỗi group message
4. addMessage — sync message (thứ tự tăng dần)
5. addReturnMessage — return (mỗi sync cần 1 return)
6. addCombinedFragment — alt cho ngoại lệ, opt cho tùy chọn, loop cho lặp
7. autoLayoutDiagram(diagramName)
```

**Hướng dẫn Combined Fragment:**
- `alt` + guard = ngoại lệ (VD: `"searchClient() trả về rỗng"`)
- `opt` = bước tùy chọn
- `loop` = lặp lại

**Ví dụ: Sequence Diagram "Mượn sách" (Phân tích)**
```
createSequenceDiagram("SD - MuonSach_PhanTich")
addLifeline("SD - MuonSach_PhanTich", "Actor", "ThuThu", "actor", "Actor")
addLifeline("SD - MuonSach_PhanTich", "FrmMuonSach", "FrmMuonSach", "boundary", "B0")
addLifeline("SD - MuonSach_PhanTich", "BanDocDAO", "BanDocDAO", "control", "DAO1")
addLifeline("SD - MuonSach_PhanTich", "BanDoc", "BanDoc", "entity", "E1")
addActivation("SD - MuonSach_PhanTich", "FrmMuonSach")
addMessage("SD - MuonSach_PhanTich", "Actor", "FrmMuonSach", "nhập mã bạn đọc + nhấn Tìm", "1", "sync")
addMessage("SD - MuonSach_PhanTich", "FrmMuonSach", "BanDocDAO", "searchReader()", "2", "sync")
addReturnMessage("SD - MuonSach_PhanTich", "BanDocDAO", "FrmMuonSach", "Reader", "3")
addReturnMessage("SD - MuonSach_PhanTich", "FrmMuonSach", "Actor", "hiển thị thông tin bạn đọc", "4")
addCombinedFragment("SD - MuonSach_PhanTich", "alt", "searchReader() trả về rỗng", "FrmMuonSach,Actor")
autoLayoutDiagram("SD - MuonSach_PhanTich")
```
addMessage("SD - TimKH", "GDTimKHFrm", "KhachHangDAO", "searchClient()", "2", "sync")
addReturnMessage("SD - TimKH", "KhachHangDAO", "GDTimKHFrm", "List<Client>", "3")
addReturnMessage("SD - TimKH", "GDTimKHFrm", "Actor", "hiển thị danh sách", "4")
addCombinedFragment("SD - TimKH", "alt", "searchClient() trả về rỗng", "GDTimKHFrm,Actor")
autoLayoutDiagram("SD - TimKH")
```

---

## Xuất ảnh và nhúng vào tài liệu

### Cách xuất ảnh từ VP

VP MCP hiện tại **không có tool export ảnh trực tiếp**. Các cách thay thế:

**Cách 1 — Screenshot từ VP application (khuyến nghị):**
1. Mở diagram trong Visual Paradigm
2. Menu: Diagram → Export as Image → PNG/SVG
3. Lưu vào thư mục `docs/images/`
4. Nhúng vào markdown: `![Tên biểu đồ](images/ten-file.png)`

**Cách 2 — VP Command Line (nếu có):**
```bash
# Export diagram as PNG via VP CLI (nếu cài đặt)
vpcmd -export diagram "Diagram Name" -format png -output docs/images/
```

**Cách 3 — Generate Report thay thế:**
Nếu không cần ảnh, dùng `generateXxxReport` để lấy text report nhúng vào tài liệu.

### Nhúng ảnh vào tài liệu Notion

Sau khi có file ảnh, dùng Notion API để upload và nhúng:

```markdown
![Biểu đồ lớp – Module KH](images/class-quanlykhachhang.png)
```

Hoặc nếu ảnh đã upload lên hosting khác:
```markdown
![Biểu đồ lớp – Module KH](https://url-to-image.png)
```

---

## Quy trình tổng hợp: cnpm + cnpm-vp

Khi người dùng muốn tài liệu UP với biểu đồ VP:

```
1. Dùng skill cnpm → BƯỚC 0 PLAN (hỏi cả công nghệ giao diện + có dùng VP không)
2. Dùng skill cnpm → sinh nội dung các mục (text + bảng)
3. Dùng skill cnpm-vp → tạo diagram trong VP theo nội dung đã sinh
4. Dùng skill cnpm-vp → export ảnh từ VP
5. Dùng skill notion-format → nhúng ảnh vào tài liệu Notion
```

**Khi nào dùng VP thay vì PlantUML:**
- Diagram phức tạp (nhiều class, nhiều relationship)
- Cần diagram chuyên nghiệp cho báo cáo/presentation
- Người dùng yêu cầu
- Diagram cần chỉnh sửa trực quan (drag & drop)

**Khi nào giữ PlantUML:**
- Diagram đơn giản
- Chỉ cần text-based documentation
- Không có VP MCP server đang chạy
- Người dùng không yêu cầu VP

---

## Kiểm tra cấu trúc sau khi vẽ (Verification)

Sau khi tạo xong diagram, **BẮT BUỘC** chạy verification trước khi export:

### Class Diagram verification
```
1. getDiagramElements(diagramName) → kiểm tra:
   - Có đủ 3 nhóm: Boundary classes, DAO classes, Entity classes
   - Mỗi Boundary class có attributes (JTextField/JButton hoặc State/JSX)
   - Mỗi Entity class có private attributes với kiểu dữ liệu
   - Mỗi DAO class extends abstract DAO
   - Relationships: >= 1 Generalization (DAO→DAO), N Dependencies (Boundary→DAO), N Associations (DAO→Entity)
2. autoLayoutDiagram(diagramName) → layout lại, kiểm tra không chồng chất
3. getDiagramElements(diagramName) → xác nhận elements spread theo chiều ngang (x positions khác nhau)
```

### Sequence Diagram verification
```
1. getDiagramElements(diagramName) → kiểm tra:
   - Lifelines theo đúng thứ tự: Actor → Boundary → [Control] → DAO → Entity
   - Mỗi sync message có ít nhất 1 return message
   - Có combined fragments (alt) cho các ngoại lệ
2. autoLayoutDiagram(diagramName)
```

### UC Diagram verification
```
1. getDiagramElements(diagramName) → kiểm tra:
   - Số actor đúng dự kiến
   - Số UC đúng dự kiến (bao gồm UC con generalization)
   - Có đủ relationship types: Include, Extend, Generalization
2. autoLayoutDiagram(diagramName)
```

### Report verification
```
generateUseCaseReport / generateClassReport / generateSequenceReport / generateErdReport
→ trả về element counts — so sánh với expected counts từ plan
```

---

## Constraints

- VP MCP server phải chạy trên `localhost:2026` (hoặc URL tùy chỉnh)
- Tên diagram trong VP phải unique — nếu trùng sẽ lỗi
- Tham số rỗng: truyền `""` (empty string), KHÔNG truyền null
- Tên element trong VP KHÔNG được chứa ký tự đặc biệt (dùng tiếng Việt không dấu nếu cần)
- `addColumn`: `length` và `scale` là integer, không phải string
- `addMessage`: `messageType` chỉ nhận `"sync"` hoặc `"async"`
- `addCombinedFragment`: `operator` chỉ nhận `"alt"`, `"opt"`, `"loop"`, `"break"`, `"par"`
- Relationship trong class diagram: thêm **sau khi** đã add tất cả classes và attributes
- Sequence diagram: add lifelines **trước**, messages **sau**
