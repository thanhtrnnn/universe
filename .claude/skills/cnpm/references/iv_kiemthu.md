<!-- Pha IV – Test -->

## IV. Cài đặt & Kiểm thử

### 4a. Bảng Test Case (tổng hợp)

Liệt kê tất cả test case theo dạng bảng ngắn gọn, phân loại theo từng module:

| TT | Module | Test case |
|----|--------|-----------|
| 1 | [Tên module] | [Mô tả ngắn TC thành công: đủ điều kiện] |
| 2 | [Tên module] | [Mô tả ngắn TC ngoại lệ 1] |
| 3 | [Tên module] | [Mô tả ngắn TC ngoại lệ 2] |
| ... | | |

**Lưu ý:** Bao phủ đủ các tổ hợp điều kiện. Ví dụ nếu module cần A, B, C tồn tại: test khi A không tồn tại, B không tồn tại, C không tồn tại, và các tổ hợp thiếu 2 điều kiện.

### 4b. Trạng thái CSDL trước khi test

Cung cấp **dữ liệu mẫu cụ thể** cho tất cả các bảng liên quan (đủ để chạy được test case):

```
tblTênBảng
| cột1 | cột2 | ... |
|------|------|-----|
| [giá trị cụ thể] | ... |
```

### 4c. Kịch bản thực hiện + Kết quả mong đợi

Trình bày từng bước thực hiện và kết quả mong đợi tương ứng:

| Kịch bản | Kết quả mong đợi |
|----------|-----------------|
| 1. [Actor thực hiện hành động] | [Giao diện/thông báo xuất hiện, kèm dữ liệu mẫu cụ thể nếu có] |
| 2. [Nhập dữ liệu = "...", click ...] | [Hệ thống hiển thị bảng kết quả: cột A, cột B, ...] với dữ liệu mẫu đầy đủ |
| ... | ... |
| N. [Click lưu/xác nhận] | Thông báo thành công |

**Lưu ý:** Kết quả mong đợi phải bao gồm cả nội dung dữ liệu trả về (ví dụ: hiển thị bảng gồm những hàng cụ thể nào), không chỉ mô tả chung chung.

### 4d. Trạng thái CSDL sau khi test

Hiển thị **toàn bộ** các bảng đã thay đổi sau khi chạy test case thành công, so sánh với trạng thái trước:

```
tblTênBảng (sau test)
| cột1 | cột2 | ... |
|------|------|-----|
| [dữ liệu cũ] | ... |
| [dữ liệu mới được thêm/sửa] | ... | ← hàng mới
```
