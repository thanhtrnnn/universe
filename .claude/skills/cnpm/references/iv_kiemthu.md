<!-- Pha IV – Test -->

## IV. Cài đặt & Kiểm thử

### 1. Lập kế hoạch test

| STT | Chức năng | Trường hợp cần test |
|-----|-----------|---------------------|
| 1 | Tạo order | Không tìm thấy phòng trong CSDL |
| 2 | Tạo order | Có phòng nhưng không tìm thấy sản phẩm |
| 3 | Tạo order | Tìm thấy phòng và sản phẩm trong CSDL |
| 4 | Tạo order | Ấn nút thêm khi đã quá số lượng tồn kho |
| 5 | Báo cáo tình trạng hàng | Cơ sở vật chất chưa có trong CSDL |
| 6 | Báo cáo tình trạng hàng | Cơ sở vật chất có trong CSDL |
| 7 | Quản lý menu | Sản phẩm chưa có trong CSDL |
| 8 | Quản lý menu | Có sản phẩm trong CSDL |
| 9 | Quản lý kho | Không có tên nhà cung cấp trong CSDL |
| 10 | Quản lý kho | Đã có tên nhà cung cấp trong CSDL |

### 2. Các test case cho từng chức năng

#### a) Chức năng Tạo order

**Test case 1: Không tìm thấy phòng trong CSDL**

CSDL trước khi test:
```
tblEmployee:
| id | full_name | dob | tel | role | username | password | status |
|----|-----------|-----|-----|------|----------|----------|--------|
| 1 | Nguyễn Văn A | 1990-01-01 | 0901234567 | manager | admin | 123 | active |
| 2 | Trần Thị B | 1995-05-10 | 0912345678 | staff | staff1 | 123 | active |

tblRoom:
| id | name | type | price | capacity | status |
|----|------|------|-------|----------|--------|
| 1 | Phòng 101 | VIP | 200000 | 10 | active |
| 2 | Phòng 104 | Thường | 100000 | 5 | active |
```

CSDL sau khi test: không có gì thay đổi.

| Các bước thực hiện | Kết quả mong đợi |
|---------------------|------------------|
| 1. Nhân viên phục vụ (id = 2) đã đăng nhập. | Giao diện tìm phòng đang hoạt động hiện ra, có ô nhập tên phòng và nút tìm. |
| 2. Nhập 107 và click nút tìm | Kết quả hiện lên: |
| 3. Click OK | Quay về giao diện chính của nhân viên phục vụ. |

---

**Test case 3: Tìm thấy phòng và sản phẩm trong CSDL**

CSDL trước khi test:
```
tblEmployee:
| id | full_name | dob | tel | role | username | password | status |
|----|-----------|-----|-----|------|----------|----------|--------|
| 1 | Nguyễn Văn A | 1990-01-01 | 0901234567 | manager | admin | 123 | active |
| 2 | Trần Thị B | 1995-05-10 | 0912345678 | staff | staff1 | 123 | active |
| 3 | Lê Văn C | 1992-03-15 | 0923456789 | staff | staff2 | 123 | active |

tblRoom:
| id | name | type | price | capacity | status |
|----|------|------|-------|----------|--------|
| 1 | Phòng 101 | VIP | 200000 | 10 | active |
| 2 | Phòng 104 | Thường | 100000 | 5 | active |

tblProduct:
| id | name | category | unit | price | current_stock | safety_stock |
|----|------|----------|------|-------|---------------|--------------|
| 1 | Bia Heniken | Đồ uống | Chai | 20000 | 50 | 10 |
| 2 | Bim Bim | Đồ ăn | Gói | 10000 | 30 | 5 |

tblRoomReceipt:
| id | checkin_time | checkout_time | room_fee | service_fee | damage_fee | total_amount | status | employee_id | room_id |
|----|-------------|---------------|----------|-------------|------------|--------------|--------|-------------|---------|
| 1 | 2024-01-15 14:00 | null | 200000 | 0 | 0 | 200000 | active | 2 | 2 |
```

CSDL sau khi test:
```
tblOrder:
| id | order_time | total_amount | status | room_receipt_id | employee_id |
|----|-----------|--------------|--------|-----------------|-------------|
| 1 | 2024-01-15 15:30 | 50000 | completed | 1 | 3 |

tblOrderDetail:
| id | order_id | product_id | quantity | unit_price | line_total |
|----|----------|-----------|----------|------------|------------|
| 1 | 1 | 1 | 1 | 20000 | 20000 |
| 2 | 1 | 2 | 3 | 10000 | 30000 |
```

| Các bước thực hiện | Kết quả mong đợi |
|---------------------|------------------|
| 1. Nhân viên phục vụ (id = 3) đăng nhập thành công | Giao diện hiện lên, bao gồm: Danh sách phòng đang hoạt động, Có ô nhập tên phòng, Nút tìm. |
| 2. Nhập 104 và click tìm | Kết quả hiện lên: |
| 3. Click vào phòng 104 và click nút tạo order | Giao diện hiển thị bao gồm: Tên phòng, Danh sách các sản phẩm, Nút thêm, ô nhập tên sản phẩm và nút tìm. |
| 4. Nhập "Bia Heniken", "Bim Bim" | Kết quả hiện lên: |
| 5. Click nút "THÊM" | Kết quả hiện lên: |
| 6. Click nút gửi order | Kết quả hiện lên: |
| 7. Click nút OK | Lưu order vào CSDL và quay trở về trang chủ của nhân viên phục vụ. |

---

#### b) Chức năng Báo cáo tình trạng hàng hóa

**Test case 5: Cơ sở vật chất chưa có trong CSDL**

CSDL trước khi test:
```
tblEmployee:
| id | full_name | dob | tel | role | username | password | status |
|----|-----------|-----|-----|------|----------|----------|--------|
| 2 | Trần Thị B | 1995-05-10 | 0912345678 | staff | staff1 | 123 | active |

tblRoom:
| id | name | type | price | capacity | status |
|----|------|------|-------|----------|--------|
| 2 | Phòng 104 | Thường | 100000 | 5 | cleaning |

tblFacility:
| id | name | unit | compensation_price | stock |
|----|------|------|-------------------|-------|
| 1 | Cốc thủy tinh | Cái | 50000 | 100 |

tblRoomReceipt:
| id | checkin_time | checkout_time | room_fee | service_fee | damage_fee | total_amount | status | employee_id | room_id |
|----|-------------|---------------|----------|-------------|------------|--------------|--------|-------------|---------|
| 1 | 2024-01-15 14:00 | 2024-01-15 18:00 | 200000 | 0 | 0 | 200000 | completed | 2 | 2 |
```

CSDL sau khi test: không có gì thay đổi.

| Các bước thực hiện | Kết quả mong đợi |
|---------------------|------------------|
| 1. Nhân viên phục vụ (id = 2) đăng nhập thành công và ấn vào chức năng báo cáo tình trạng hàng. | Giao diện hiện lên gồm: Các phòng đang ở trạng thái chờ dọn, Ô nhập tên phòng, Nút tìm. |
| 2. Nhập 104 và click tìm | Kết quả hiện lên: |
| 3. Click vào phòng 104 và click nút tạo báo cáo | Giao diện hiển thị bao gồm: Tên phòng, Danh sách các cơ sở vật chất, Ô nhập tên csvc, Nút tìm, Ô nhập số lượng. |
| 4. Nhập "Thìa" và click tìm | Giao diện hiện lên: |
| 5. Click OK | Quay về giao diện báo cáo. |

---

#### c) Chức năng Quản lý menu

**Test case 8: Có sản phẩm trong CSDL**

CSDL trước khi test:
```
tblProduct:
| id | name | category | unit | price | current_stock | safety_stock |
|----|------|----------|------|-------|---------------|--------------|
| 1 | Bia Heniken | Đồ uống | Chai | 20000 | 50 | 10 |
```

CSDL sau khi test:
```
tblProduct:
| id | name | category | unit | price | current_stock | safety_stock |
|----|------|----------|------|-------|---------------|--------------|
| 1 | Bia Heniken | Đồ uống | Chai | 25000 | 50 | 10 |
```

| Các bước thực hiện | Kết quả mong đợi |
|---------------------|------------------|
| 1. Nhân viên quản lý (id = 1) đăng nhập thành công và click vào chức năng quản lý menu. | Giao diện hiển thị: Danh sách các sản phẩm, Ô nhập tên sản phẩm, Nút tìm, Nút thêm, Nút sửa, Nút xóa. |
| 2. Nhập "Bia Heniken" và click tìm kiếm | Giao diện hiển thị: |
| 3. Click vào chức năng "Sửa" | Giao diện hiển thị: |
| 4. Sửa giá sản phẩm thành "25,000" | Giao diện hiển thị: |
| 5. Click "Lưu" | Thông báo hiện lên "Sửa thành công" |
| 6. Click OK | Hệ thống cập nhật lại vào CSDL, về lại giao diện Menu. |

---

#### d) Chức năng Quản lý kho

**Test case 10: Đã có tên nhà cung cấp trong CSDL**

CSDL trước khi test:
```
tblEmployee:
| id | full_name | dob | tel | role | username | password | status |
|----|-----------|-----|-----|------|----------|----------|--------|
| 1 | Nguyễn Văn A | 1990-01-01 | 0901234567 | manager | admin | 123 | active |

tblProduct:
| id | name | category | unit | price | current_stock | safety_stock |
|----|------|----------|------|-------|---------------|--------------|
| 1 | Bia Heniken | Đồ uống | Chai | 20000 | 5 | 10 |

tblProvider:
| id | name | tel | address |
|----|------|-----|---------|
| 1 | Thực phẩm Tân Việt | 0901111111 | Hà Nội |
| 2 | Bia-rượu-nước giải khát | 0902222222 | TP.HCM |
```

CSDL sau khi test:
```
tblImportReceipt:
| id | import_date | total_cost | employee_id | provider_id |
|----|------------|------------|-------------|-------------|
| 1 | 2024-01-16 | 900000 | 1 | 2 |

tblImportDetail:
| id | product_id | import_receipt_id | quantity | unit_cost | line_total |
|----|-----------|-------------------|----------|-----------|------------|
| 1 | 1 | 1 | 50 | 18000 | 900000 |

tblProduct:
| id | name | category | unit | price | current_stock | safety_stock |
|----|------|----------|------|-------|---------------|--------------|
| 1 | Bia Heniken | Đồ uống | Chai | 20000 | 55 | 10 |
```

| Các bước thực hiện | Kết quả mong đợi |
|---------------------|------------------|
| 1. Nhân viên quản lý đăng nhập thành công và click vào chức năng quản lý kho. | Giao diện hiển thị danh sách thông tin các sản phẩm và có nút nhập hàng. |
| 2. Quản lý thấy một số sản phẩm sắp hết (báo đỏ) và click vào nút nhập hàng. | Giao diện hiển thị: Danh sách các nhà cung cấp, Ô nhập tên nhà cung cấp, Nút tìm, Nút tạo phiếu nhập hàng. |
| 3. Quản lý nhập "Bia-rượu-nước giải khát" | Giao diện hiển thị: |
| 4. Click vào nhà cung cấp và click nút tạo phiếu nhập hàng. | Giao diện hiển thị: |
| 5. Nhập các thông tin "Bia Heniken", "50", "18,000" và click "LƯU" | Hiển thị thông báo thành công. |
| 6. Click OK | Hệ thống lưu phiếu vào CSDL và quay lại màn hình chính của quản lý. |
