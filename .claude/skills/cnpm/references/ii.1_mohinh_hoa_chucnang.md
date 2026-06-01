<!-- Pha II – Analysis, Section 1 -->

## II.1. Mô hình hóa chức năng

Viết **một bảng 2 cột** cho **từng UC** trong module. Cột trái là tên trường, cột phải là nội dung. Các trường cố định theo thứ tự: Use case, Actor, Tiền điều kiện, Hậu điều kiện, Kịch bản chính, Ngoại lệ.

**Yêu cầu bắt buộc:**
- **Kịch bản chính** — nội dung là danh sách đánh số, mỗi bước **nguyên tử** (tách riêng: hành động của actor, phản hồi của hệ thống). Không gộp nhiều thao tác vào một bước.
- **Khi hệ thống hiển thị dữ liệu có cấu trúc** (danh sách khách hàng, đối tác, mặt hàng, lịch thanh toán, thông tin xác nhận...) → **BẮT BUỘC dùng HTML table inline ngay trong ô đó, KHÔNG dùng bullet**. Điền đủ 2–4 hàng dữ liệu mẫu thực tế, có đầy đủ các cột quan trọng. KHÔNG được thay bằng mô tả chung chung như "hiển thị danh sách kết quả" hay bullet liệt kê tên cột.
- **Số liệu nhất quán xuyên suốt:** mã/tên/số liệu xuất hiện trong bảng kết quả phải được dùng lại chính xác ở các bước "chọn dòng..." phía sau.
- **Ngoại lệ** — nội dung là danh sách đánh số theo bước rẽ nhánh. Mỗi ngoại lệ bắt đầu bằng số bước gốc (VD: `6. Hệ thống báo không tìm thấy`), tiếp theo là các bước con `6.1`, `6.2`...

> ❌ **SAI** (dùng bullet thay bảng):
> ```
> 6. Hệ thống hiển thị giao diện kết quả:
>    ▪ nút Tìm + Thêm mới
>    ▪ Danh sách khách hàng: Mã, Tên, CCCD, Địa chỉ, SDT, Email, Ghi chú
> ```
>
> ✅ **ĐÚNG** (dùng HTML table inline):
> ```
> 6. Hệ thống hiển thị giao diện kết quả có nút Tìm, nút Thêm mới và danh sách:<br><table><tr><th>Mã</th><th>Tên</th><th>CCCD</th><th>Địa chỉ</th><th>SDT</th><th>Email</th><th>Ghi chú</th></tr><tr><td>02</td><td>Aaa</td><td>03133</td><td>Hà Nội</td><td>01234</td><td>aaa@a.com</td><td></td></tr><tr><td>13</td><td>AB</td><td>02364</td><td>HCM</td><td>02345</td><td>aba@b.com</td><td></td></tr><tr><td>78</td><td>A</td><td>05674</td><td>Ba Vì</td><td>03426</td><td>aca@c.com</td><td></td></tr></table>
> ```

**Cấu trúc bảng:**

| Trường | Nội dung |
|---|---|
| **Use case** | [Tên UC] |
| **Actor** | [Tên Actor] |
| **Tiền điều kiện** | ... |
| **Hậu điều kiện** | ... |
| **Kịch bản chính** | 1. [Actor] chọn chức năng "[Tên]" từ giao diện chính.<br>2. Hệ thống hiển thị giao diện "[Tên màn hình]" có:<br>▪ ô nhập tên<br>▪ nút Tìm; nút Thêm mới<br>3. [Actor] hỏi thông tin [đối tượng].<br>4. [Đối tượng] cung cấp [thông tin] cụ thể: [trường] = [Giá trị]<br>5. [Actor] nhập từ khóa '[Giá trị]' và nhấn nút "Tìm".<br>6. Hệ thống hiển thị giao diện kết quả:<br>▪ nút Tìm + Thêm mới<br>▪ Danh sách [đối tượng] có tên trùng từ khóa:<br><table><tr><th>Mã</th><th>Tên</th><th>Cột A</th><th>Cột B</th><th>...</th></tr><tr><td>02</td><td>Aaa</td><td>...</td><td>...</td><td>...</td></tr><tr><td>13</td><td>AB</td><td>...</td><td>...</td><td>...</td></tr><tr><td>78</td><td>A</td><td>...</td><td>...</td><td>...</td></tr></table><br>7. [Actor] chọn dòng số 1 (mã = 02, tên = Aaa).<br>8. Hệ thống chuyển sang giao diện "[Bước tiếp theo]".<br>...<br>N. [Actor] nhấn "[Lưu / Xác nhận / In]".<br>N+1. Hệ thống lưu thành công, thông báo "[Nội dung cụ thể]". |
| **Ngoại lệ** | 6. Hệ thống báo [đối tượng] không tồn tại<br>6.1 [Actor] click OK thông báo và chọn '[Thêm mới]'<br>6.2 Hệ thống hiển thị giao diện nhập thông tin<br>6.3 [Đối tượng] cung cấp đầy đủ thông tin<br>6.4 [Actor] nhập vào hệ thống: [các trường]...<br>6.5 Hệ thống thêm [đối tượng] mới<br>6.6 [Actor] click Tiếp tục (Bước 6)<br><br>10. Không tìm thấy [đối tượng khác]<br><br>N. [Điều kiện ngoại lệ].<br>N.1 [Actor] [hành động]<br>... |

---

### Ví dụ áp dụng: UC "Sửa phòng"

| Trường | Nội dung |
|---|---|
| **Use case** | Sửa phòng |
| **Actor** | Quản lý |
| **Tiền điều kiện** | Quản lý đã đăng nhập thành công |
| **Hậu điều kiện** | Thông tin phòng được cập nhật trong CSDL |
| **Kịch bản chính** | 1. Quản lý chọn chức năng "Quản lý phòng" từ giao diện chính.<br>2. Hệ thống hiển thị giao diện quản lý phòng với ba tùy chọn: thêm, sửa, xóa.<br>3. Quản lý chọn "Sửa phòng".<br>4. Hệ thống hiển thị giao diện tìm kiếm phòng với ô nhập từ khóa và nút Tìm.<br>5. Quản lý nhập từ khóa "305" và nhấn nút Tìm.<br>6. Hệ thống hiển thị danh sách phòng có tên chứa từ khóa:<br><table><tr><th>Mã</th><th>Tên</th><th>Giá</th><th>Loại</th><th>Mô tả</th></tr><tr><td>1</td><td>305</td><td>1000</td><td>Đôi</td><td>Nhìn ra biển</td></tr><tr><td>2</td><td>305bis</td><td>500</td><td>Đơn</td><td>Nhìn ra vườn</td></tr></table><br>7. Quản lý nhấn vào dòng số 1 (mã = 1, tên = 305).<br>8. Hệ thống hiển thị giao chỉnh sửa với các ô nhập đã chứa sẵn giá trị: Tên = 305, Giá = 1000, Loại = Đôi, Mô tả = Nhìn ra biển. Mã chỉ xem.<br>9. Quản lý thay đổi Giá thành 800 và nhấn nút Lưu.<br>10. Hệ thống thông báo cập nhật thành công và quay về giao diện chính. |
| **Ngoại lệ** | 4. Hệ thống thông báo tên đăng nhập hoặc mật khẩu không chính xác.<br><br>6. Không tìm thấy phòng nào.<br>6.1 Hệ thống hiển thị thông báo "Không tìm thấy phòng".<br>6.2 Quản lý nhập lại từ khóa khác (quay về Bước 5). |
