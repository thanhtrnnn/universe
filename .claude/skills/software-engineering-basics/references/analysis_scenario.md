# Kịch bản -- Phần mềm: Nghệ thuật Thiết kế và Lập trình
#### **Kịch bản tiêu chuẩn của ca sử dụng: chỉnh sửa phòng**

1.  Một quản lý khởi động ứng dụng để chỉnh sửa phòng 305.
2.  Giao diện đăng nhập xuất hiện với: một ô nhập tên đăng nhập, một ô nhập mật khẩu, một nút đăng nhập.
3.  Quản lý nhập tên đăng nhập là man01, mật khẩu là \*\*\*\*\*\* và sau đó, nhấn vào nút đăng nhập.
4.  Giao diện chính của quản lý xuất hiện với ba tùy chọn: Quản lý khách sạn, quản lý phòng, và xem báo cáo.
5.  Quản lý chọn quản lý phòng.
6.  Giao diện quản lý phòng xuất hiện với ba tùy chọn: thêm, sửa, xóa phòng.
7.  Quản lý chọn sửa một phòng.
8.  Giao diện tìm kiếm phòng xuất hiện với: một ô nhập từ khóa là tên phòng, một nút tìm kiếm.
9.  Quản lý nhập từ khóa là 305 và sau đó, nhấn vào nút tìm kiếm.
10.  Danh sách tất cả các phòng có tên chứa từ khóa đã nhập xuất hiện như sau:


|Mã |Tên   |Giá  |Loại  |Mô tả          |
|---|------|-----|------|----------------|
|1  |305   |1000 |Đôi   |Nhìn ra biển    |
|2  |305bis|500  |Đơn   |Nhìn ra vườn    |


11.  Quản lý nhấn vào dòng đầu tiên, tương ứng với phòng 305.
12.  Giao diện chỉnh sửa phòng xuất hiện với các thuộc tính và giá trị của phòng đã chọn:

Mã: 1 -- chỉ xem

Tên: 305

Giá: 1000

Loại: đôi

Mô tả: nhìn ra biển

nút cập nhật và đặt lại.

13.  Quản lý thay đổi giá thành 800 và nhấn vào nút lưu.
14.  Một thông báo thành công xuất hiện và sau đó, hệ thống quay về giao diện chính của quản lý.

_**Kịch bản ngoại lệ:**_

4\. Hệ thống thông báo rằng tên đăng nhập hoặc mật khẩu không chính xác.

10\. Không tìm thấy phòng nào.

#### **Kịch bản tiêu chuẩn cho ca sử dụng: đặt phòng**

1.  Nhân viên bán hàng A nhấn vào tùy chọn đặt phòng trong menu quản lý đặt phòng. A muốn đặt một phòng cho khách hàng B, người đang gọi điện cho A để đặt phòng.
2.  Giao diện tìm kiếm phòng trống xuất hiện với: một ô nhập ngày nhận phòng, một ô nhập ngày trả phòng, một nút tìm kiếm.
3.  A hỏi B ngày nhận phòng và trả phòng mong muốn.
4.  B trả lời A rằng muốn đặt từ 30/04/2020 đến 01/05/2020.
5.  A nhập ngày nhận phòng là 30/04/2020, ngày trả phòng là 01/05/2020 và sau đó, nhấn vào nút tìm kiếm.
6.  Danh sách tất cả các phòng trống trong khoảng thời gian đó được liệt kê như sau:


|Mã |Tên |Giá  |Loại           |Mô tả          |
|---|----|-----|---------------|----------------|
|1  |305 |1000 |Đôi            |Nhìn ra biển    |
|2  |201 |500  |Đơn            |Nhìn ra vườn    |
|3  |202 |1000 |Giường đôi nhỏ |Nhìn ra vườn    |


7.  A thông báo các phòng này cho B và yêu cầu B chọn một phòng.
8.  B chọn phòng có tầm nhìn ra biển.
9.  A nhấn vào phòng 305 -- dòng số 1
10.  Giao diện thông tin khách hàng xuất hiện với các ô nhập: tên, số chứng minh nhân dân, địa chỉ, số điện thoại, email.
11.  A yêu cầu B cung cấp các thông tin đó.
12.  B cung cấp cho A: Tên là B, địa chỉ là Ha Noi, số chứng minh nhân dân là 123456, số điện thoại là 77777777, và email là b77@gmail.com.
13.  A nhập B vào ô tên, và sau đó nhấn vào nút tìm kiếm.
14.  Danh sách tất cả khách hàng có tên chứa từ khóa B được liệt kê như sau:


|Mã |Tên |Địa chỉ         |Số chứng minh nhân dân|Số điện thoại |Email         |
|---|----|-----------------|----------------------|--------------|--------------|
|1  |B   |Ha Noi           |123456                |77777777      |b77@gmail.com |
|2  |BC  |Da Nang          |223344                |88888888      |bc88@Gmail.com|
|3  |BB  |Ho Chi Minh city |343434                |5555555       |null          |


15.  A nhận ra rằng B đã có trong hệ thống ở dòng 1. A nhấn vào dòng 1.
16.  Giao diện xác nhận xuất hiện với thông tin: Thông tin về phòng đã chọn: tên 305, đôi, nhìn ra biển, giá 1000 mỗi đêm. Thông tin về khách hàng: tên là B, đến từ Ha Noi... Thông tin về đặt phòng: nhận phòng từ 30/04 đến 01/05. Một nút xác nhận và một nút hủy.
17.  A nhắc lại các thông tin đó cho B và yêu cầu B xác nhận.
18.  B xác nhận rằng tất cả đều đúng.
19.  A nhấn vào nút xác nhận.
20.  Hệ thống hiển thị thông báo đặt phòng thành công và quay về giao diện chính của nhân viên bán hàng.
21.  A thông báo cho B rằng việc đặt phòng đã thành công và kết thúc cuộc gọi.

**_Kịch bản ngoại lệ:_**

6\. Không tìm thấy phòng trống nào.

14\. Không tìm thấy khách hàng nào hoặc khách hàng B chưa có trong hệ thống.

#### **Kịch bản tiêu chuẩn cho ca sử dụng: xem báo cáo**

1.  Một quản lý chọn tùy chọn xem báo cáo sau khi đã đăng nhập. Quản lý muốn xem doanh thu theo phòng từ đầu tháng.
2.  Giao diện cấu hình báo cáo xuất hiện với: một lựa chọn đối tượng báo cáo bao gồm khách sạn, phòng, khách hàng, dịch vụ, doanh thu. Lựa chọn tiêu chí báo cáo bao gồm: theo thời gian, theo doanh thu.
3.  Quản lý chọn đối tượng là phòng, tiêu chí là theo doanh thu.
4.  Giao diện xem báo cáo xuất hiện với: một ô nhập ngày bắt đầu, một ô nhập ngày kết thúc, và một nút xem.
5.  Quản lý nhập ngày bắt đầu là 01/05/2020, ngày kết thúc là 30/05/2020, và nhấn vào nút xem.
6.  Danh sách tất cả các phòng được liệt kê như sau:


|STT|Tên |Loại           |Tổng số ngày có khách|Tổng doanh thu|
|---|----|---------------|---------------------|--------------|
|1  |305 |đôi            |15                   |15000         |
|2  |201 |đơn            |24                   |12000         |
|3  |202 |giường đôi nhỏ |10                   |10000         |
|4  |203 |đôi            |5                    |5000          |
|5  |301 |đơn            |2                    |1000          |
|6  |302 |giường đôi nhỏ |0                    |0             |


7.  Quản lý nhấn vào phòng 305 để xem chi tiết.
8.  Danh sách tất cả khách hàng đã ở phòng 305 trong khoảng thời gian đó:


|STT  |Tên khách hàng|Ngày nhận phòng|Ngày trả phòng|Giá  |Tổng số ngày|Tổng doanh thu|
|-----|--------------|---------------|--------------|-----|------------|--------------|
|1    |B             |01/05/20       |05/05/20      |1000 |4           |4000          |
|2    |CC            |09/05/20       |10/05/20      |1000 |1           |1000          |
|3    |zz            |13/05/20       |21/05/20      |1000 |8           |8000          |
|4    |kk            |24/05/20       |26/05/20      |1000 |2           |2000          |
|Tổng |15            |15000          |              |     |            |              |


9.  Quản lý nhấn vào nút quay lại.
10.  Hệ thống quay về giao diện chính của quản lý.