# VP MCP – Kinh nghiệm & Gotchas

Ghi lại từ test thực tế ngày 2026-06-09. Cập nhật khi phát hiện thêm vấn đề.

---

## ✅ Những gì hoạt động đúng

- `createClassDiagram` + `addClass` + `addAttribute` + `addOperation` + `addDependency` → thêm class, thuộc tính, operation, quan hệ dependency đúng.
- `addOperation` với `returnType: "void"` và `params: ""` hoạt động.
- `autoLayoutDiagram` hoạt động.
- `generateClassReport` + `generateSequenceReport` + `getElementCounts` hoạt động.
- `createSequenceDiagram` + `addLifeline` + `addMessage` + `addReturnMessage` → thêm lifeline và message đúng về mặt dữ liệu.
- Self-message (`Class → Class`) hoạt động, hiển thị "(self)" trong report.
- Return message (`addReturnMessage`) hoạt động.

---

## ❌ Vấn đề đã xác nhận (test 2026-06-09)

### 1. Tên class `"Class"` bị conflict với VP internal

**Triệu chứng:** Entity đặt tên `"Class"` hiển thị thành `"Class2"` trong diagram; attributes và operations không hiển thị.

**Nguyên nhân:** VP dùng `"Class"` là tên nội bộ cho model element. Đặt tên class là `"Class"` gây xung đột.

**Fix:** Dùng tên domain cụ thể thay "Class":
- `ClassSection` hoặc `CourseSection` thay cho `Class` (thực thể lớp học phần)
- Tránh các tên reserved: `Class`, `Node`, `Object`, `Type`

---

### 2. Package không hiển thị như container — chỉ là legend

**Triệu chứng:** Khi `addClass` với `packageName: "Boundary"` và `packageColor: "#DDEEFF"`, VP không vẽ package như khung bao quanh class. Thay vào đó, hiển thị legend riêng ở góc dưới trái (rectangle màu với nhãn "Boundary" / "Entity"), còn các class nằm tự do trên canvas.

**Nguyên nhân:** VP MCP API `addClass.packageName` không tạo UML Package container; chỉ gán màu/nhãn nhóm riêng.

**Fix hiện tại:** Chưa có workaround qua API. Chấp nhận layout này hoặc dùng `addPackage` tool riêng nếu có (cần test). Về mặt nội dung thông tin vẫn đúng — chỉ không đẹp bằng PlantUML.

---

### 3. Actor lifeline hiển thị là rectangle, không phải stick figure

**Triệu chứng:** `addLifeline` với `lifelineType: "actor"` → VP render một rectangle màu xanh (giống boundary), không phải biểu tượng actor UML (người que).

**Nguyên nhân:** VP MCP API có thể không map đúng `"actor"` sang UML actor shape.

**Fix:** Chưa rõ workaround. Cần test `lifelineType: "actor"` với VP version khác hoặc tham số khác.

---

### 4. Lifeline hiển thị suffix `: ClassN` (VP internal ID)

**Triệu chứng:** Lifelines hiển thị như `"LecturerHomeView : Class3"`, `"QRSessionView : Class4"` thay vì chỉ `"LecturerHomeView"`.

**Nguyên nhân:** Khi `addLifeline` tạo class mới trong VP (không tham chiếu class có sẵn), VP tự sinh internal class với tên generic (`Class3`, `Class4`...). Sequence diagram hiển thị theo format `<lifelineName> : <className>`.

**Fix:** Tạo class trước trong class diagram, sau đó dùng `className` trùng khớp khi `addLifeline`. Cần test xem VP có lookup class đã tồn tại không.

---

### 5. Messages không hiển thị trong sequence diagram (visual)

**Triệu chứng:** `generateSequenceReport` cho thấy 19 messages đã thêm thành công, nhưng diagram visual trong VP hầu như trống — không có đường message nào hiển thị. `getDiagramElements` cho thấy messages là `"? -> ?"`.

**Nguyên nhân:** Messages bị `"? -> ?"` vì một số lifeline không được resolve đúng (lỗi "From lifeline not found" khi add message lúc đầu). Message vẫn được persist vào diagram data nhưng không có lifeline reference hợp lệ.

**Root cause sâu hơn:** Actor lifeline bị lookup sai tên dẫn đến nhiều messages fail silently, phá vỡ sequence ordering.

**Fix:** Đảm bảo tất cả lifelines được add và verified trước khi add messages. Xem mục 6.

---

### 6. Actor lifeline phải reference bằng ALIAS, không phải lifelineName

**Triệu chứng:** `addLifeline` với `lifelineName: "GV"`, `alias: "Giang vien"` → khi `addMessage` dùng `fromLifeline: "GV"` → lỗi "From lifeline not found: GV". Dùng `fromLifeline: "Giang vien"` → thành công.

**Nguyên nhân:** VP tra cứu lifeline theo display name. Với alias thì display name = alias. Với non-actor (không có alias) thì display name = lifelineName.

**Fix (bắt buộc):** Khi add actor lifeline, **không dùng alias riêng**. Đặt `lifelineName` = tên muốn hiển thị luôn:

```json
{
  "lifelineName": "Giang vien",
  "className": "GiangVien",
  "lifelineType": "actor",
  "alias": ""
}
```

Sau đó trong `addMessage` dùng `"fromLifeline": "Giang vien"` — nhất quán.

**Quy tắc tổng quát:** `fromLifeline`/`toLifeline` trong `addMessage` phải dùng đúng giá trị sau:
- Nếu lifeline có alias → dùng alias
- Nếu lifeline không có alias → dùng lifelineName

---

## So sánh visual: VP MCP vs PlantUML

| Phần tử | PlantUML (mong đợi) | VP MCP (thực tế) |
|---------|---------------------|-----------------|
| Actor lifeline | Hình người que + tên | Rectangle màu xanh + tên |
| Lifeline name | `LecturerHomeView` | `LecturerHomeView : Class3` |
| Message arrow | Mũi tên có nhãn, đầy đủ | Không hiển thị nếu actor sai alias |
| Package BCE | Khung bao quanh classes | Legend box góc dưới trái |
| Entity "Class" | Class thông thường | Hiển thị thành "Class2" |
| Broken message | N/A | `? -> ?` nếu lifeline không resolve |

**Mô tả chi tiết từ screenshots (test 2026-06-09):**
- **BCE diagram:** 3 class đứng tự do trên canvas; legend box màu xanh nhạt (Boundary) và vàng nhạt (Entity) nằm ở góc dưới trái, KHÔNG bao quanh class như UML Package. Entity "Class" hiển thị là "Class2" không có thuộc tính.
- **Sequence diagram:** Lifeline header có dạng `"Tên : ClassN"` (VD: `"LecturerHomeView : Class3"`, `"QRSessionView : Class4"`). Khoảng giữa các lifelines gần như trống — đường message không hiển thị do actor alias lookup fail; report có 19 messages nhưng diagram visual gần trống.

---

## Kết luận & Khuyến nghị

**PlantUML là công cụ ưu tiên** cho output chất lượng báo cáo TTCS. VP MCP phù hợp cho:
- Nhập dữ liệu vào VP project (để giảng viên/mentor xem trong VP IDE)
- Kiểm tra cấu trúc class/quan hệ về mặt data (`generateClassReport`, `getElementCounts`)
- Tạo biểu đồ thô rồi chỉnh sửa thủ công trong VP

**Không dùng VP MCP để export diagram ảnh** cho báo cáo — visual output khác xa PlantUML và không đạt chuẩn trình bày.

---

## Quy trình 4 bước phân tích BCE (nguyên văn từ giảng viên, chốt 2026-06-09)

- **Bước 1:** Một giao diện người dùng -- ngoại trừ cảnh báo/thông báo, hộp thoại xác nhận... -- tạo một lớp giao diện.
- **Bước 2:** Xem xét các thành phần cần thiết trong mỗi giao diện, đặt tên thành phần với tiền tố tương ứng loại của nó:
  - `in`: cho các thành phần nhập liệu -- ô nhập văn bản, ô nhập ngày tháng...
  - `out`: cho các thành phần hiển thị -- bảng, nội dung...
  - `sub`: cho các thành phần gửi dữ liệu -- nút bấm, liên kết...
  - và có thể kết hợp các loại trên.
- **Bước 3:** Với mỗi chức năng cần thiết, trả lời bốn câu hỏi:
  - Tên phù hợp của phương thức là gì
  - Các tham số đầu vào là gì?
  - Tham số đầu ra là gì?
  - Phương thức nên được gán vào lớp nào? (output type là entity → gán cho entity đó; else xét input params)
- **Bước 4:** Xây dựng sơ đồ lớp cho mô-đun.
