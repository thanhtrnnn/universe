# Trích xuất lớp thực thể -- Phần mềm: Nghệ thuật Thiết kế và Lập trình
**Phương pháp trích xuất danh từ**

*   Bước 1: Mô tả các chức năng của hệ thống trong một đoạn văn ngắn gọn và đầy đủ.
*   Bước 2: Trích xuất tất cả các danh từ xuất hiện ở bước 1. Mỗi danh từ chỉ được đếm một lần.
*   Bước 3: Phân loại các danh từ ở bước 2.
    *   Một danh từ có thể trở thành một lớp thực thể.
    *   Một danh từ có thể trở thành một thuộc tính của một lớp thực thể.
    *   Loại bỏ một danh từ nếu nó quá chung chung, quá trừu tượng, hoặc nằm ngoài phạm vi của hệ thống.
*   Bước 4: Xem xét mối quan hệ định lượng giữa các lớp thực thể.
    *   Nếu là 1-1 thì: giữ nguyên hoặc gộp hai lớp thành một.
    *   Nếu là 1-n thì giữ nguyên.
    *   Nếu là n-n thì cần chia thành ít nhất 2 mối quan hệ kiểu 1-n.
*   Bước 5: Xác định mối quan hệ đối tượng giữa các lớp thực thể: tổng quát hóa, tập hợp, hợp thành, liên kết, phụ thuộc...

**Áp dụng**

_**Bước 1: mô tả hệ thống trong một đoạn văn**_

Hệ thống hỗ trợ quản lý thông tin về một khách sạn, các phòng của khách sạn, và các khách hàng đặt phòng tại khách sạn. Hệ thống cho phép người quản lý khách sạn quản lý thông tin về khách sạn, các phòng của khách sạn; xem một số loại báo cáo thống kê như: thống kê phòng, thống kê khách sạn, thống kê khách hàng, thống kê dịch vụ, và thống kê doanh thu. Hệ thống cho phép quản trị viên hệ thống quản lý tài khoản cho người dùng của hệ thống khi cần thiết. Hệ thống cho phép nhân viên bán hàng của khách sạn đặt phòng và hủy đặt phòng cho khách hàng từ xa qua điện thoại. Hệ thống cho phép nhân viên lễ tân của khách sạn đặt phòng, hủy đặt phòng, nhận phòng, trả phòng và xử lý thanh toán cho khách hàng tại chỗ ở quầy lễ tân. Khi thanh toán được xử lý, một hóa đơn được tạo ra với thông tin về khách hàng, các phòng đã đặt, và dịch vụ mà khách hàng đã sử dụng trong thời gian lưu trú tại khách sạn.

_**Bước 2+3: trích xuất danh từ và phân loại chúng**_

*   Hệ thống: danh từ trừu tượng --> loại bỏ.
*   Thông tin: danh từ chung --> loại bỏ.
*   Phòng: cần được quản lý --> một lớp: Room
*   Khách sạn: cần được quản lý --> một lớp: Hotel
*   Khách hàng: cần được quản lý --> một lớp: Client
*   Người quản lý: đây là một loại thành viên trong hệ thống, còn gọi là tài khoản, người dùng --> một lớp: User
*   Điện thoại: nằm ngoài phạm vi hệ thống --> loại bỏ
*   Nhân viên bán hàng: một loại User
*   Quầy lễ tân: nằm ngoài phạm vi hệ thống --> loại bỏ
*   Nhân viên lễ tân: một loại User
*   Hóa đơn: cần được quản lý --> một lớp: Bill
*   Dịch vụ: cần được quản lý --> một lớp: Service
*   Thống kê: thống kê phòng --> RoomStat; thống kê khách hàng --> ClientStat; thống kê dịch vụ --> ServiceStat; thống kê khách sạn --> HotelStat; thống kê thu nhập hoặc doanh thu --> IncomeStat.

Vậy chúng ta thu được các lớp ban đầu: Room, Hotel, Client, User, Bill, Service và các lớp thống kê: RoomStat, HotelStat, ClientStat, ServiceStat, IncomeStat.

_**Bước 4+5: quan hệ định lượng và quan hệ đối tượng giữa các lớp**_

Quan hệ giữa các lớp thực thể được xác định như sau:

*   Một Hotel có thể có nhiều Room, một Room chỉ thuộc về một Hotel. Vậy Hotel -- Room là 1-n.
*   Một Client có thể đặt nhiều Room, một Room có thể được đặt bởi nhiều Client ở các thời điểm khác nhau: vậy Client -- Room là n-n. Do đó chúng ta có thể đề xuất một lớp trung gian giữa chúng: Booking.
*   Một Client có thể có nhiều Booking ở các thời điểm khác nhau. Một Room cũng có thể được đặt trong nhiều Booking ở các thời điểm khác nhau. Hơn nữa, trong một Booking, khách hàng có thể đặt nhiều phòng cho nhóm khách. Vậy Booking -- Room vẫn là n-n. Chúng ta cần thêm một lớp trung gian giữa chúng: BookedRoom. Một Booking và một Room xác định duy nhất một BookedRoom. Mối quan hệ kết hợp này cũng xác định một số thông tin: ngày nhận phòng, ngày trả phòng, giá đặt phòng.
*   Một Booking có thể được thanh toán nhiều lần. Mỗi lần thanh toán phải có một hóa đơn. Vậy Booking -- Bill là 1-n.
*   Một nhân viên lễ tân có thể tạo nhiều Bill cho nhiều Booking. Vậy User -- Bill cũng là 1-n.
*   Một Service có thể được sử dụng bởi nhiều Client, trong nhiều BookedRoom. Tuy nhiên, một Service chỉ được thanh toán một lần trong một BookedRoom. Một BookedRoom có thể sử dụng nhiều Service: quan hệ BookedRoom -- Service là n-n. Vậy chúng ta tạo một lớp UsedService giữa BookedRoom và Service.
*   Đối với các lớp thống kê, chúng tái sử dụng một số thuộc tính của lớp thực thể tương ứng. Vì vậy chúng ta có thể xem chúng kế thừa từ lớp thực thể tương ứng: HotelStat kế thừa từ Hotel; RoomStat kế thừa từ Room; ServiceStat kế thừa từ Service; ClientStat kế thừa từ Client.
*   Do IncomeStat không tái sử dụng thuộc tính nào của các lớp khác, nó chỉ phụ thuộc vào Bill.

![](https://softwaredesign.home.blog/wp-content/uploads/2020/11/image-4.png?w=501)