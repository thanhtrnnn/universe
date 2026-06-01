# Biểu đồ lớp thiết kế -- Phần mềm: Nghệ thuật Thiết kế và Lập trình
_**Chỉnh sửa phòng**_

Thiết kế dựa trên mô hình MVC:

*   Các lớp Giao diện:
    *   LoginFrm là giao diện để đăng nhập. Giao diện này cần một trường nhập liệu để nhập tên người dùng, một trường nhập liệu để nhập mật khẩu và một nút để đăng nhập.
    *   ManagerHomeFrm là giao diện trang chủ dành cho Quản lý. Giao diện này cần ít nhất một nút để chuyển đến chức năng quản lý phòng.
    *   ManageRoomFrm là giao diện để quản lý phòng. Giao diện này cần ít nhất một nút để chuyển đến chức năng chỉnh sửa phòng.
    *   SearchRoomFrm là giao diện để tìm kiếm phòng cần chỉnh sửa. Giao diện này cần một trường nhập liệu để nhập từ khóa tìm kiếm phòng theo tên, một nút tìm kiếm và một bảng để hiển thị danh sách phòng tìm được.
    *   EditRoomFrm là giao diện để chỉnh sửa các thuộc tính của phòng. Giao diện này cần các trường nhập liệu cho: tên, loại, giá, mô tả. Một nút cập nhật và một nút đặt lại.
*   Các lớp Điều khiển -- DAO - Đối tượng Truy cập Dữ liệu:
    *   DAO là lớp chung của DAO. Lớp này chỉ có hàm khởi tạo để kết nối tới cơ sở dữ liệu và cung cấp kết nối dùng chung cho tất cả các lớp DAO kế thừa trong hệ thống.
    *   UserDAO là lớp dùng để thao tác với cơ sở dữ liệu liên quan đến đối tượng User. Trong mô-đun này, lớp cần một phương thức để xác minh thông tin đăng nhập có chính xác hay không, đó là phương thức checkLogin.
    *   RoomDAO là lớp dùng để thao tác với cơ sở dữ liệu liên quan đến đối tượng Room. Trong mô-đun này, lớp cần hai phương thức:
        *   searchRoom: tìm kiếm tất cả các phòng có tên chứa từ khóa đã nhập.
        *   updateRoom: cập nhật thông tin của phòng được truyền vào.
*   Các lớp Thực thể: User, Room.

![](https://softwaredesign.home.blog/wp-content/uploads/2020/12/image-13.png?w=722)

_**Đặt phòng**_

Trong mô-đun này, quá trình xử lý đăng nhập được lược bỏ:

*   Các lớp Giao diện:
    *   SellerHomeFrm là giao diện trang chủ của Nhân viên bán hàng. Giao diện này cần ít nhất một nút để chuyển đến chức năng đặt phòng.
    *   SearchFreeRoomFrm là giao diện để tìm kiếm phòng trống. Giao diện này cần hai trường nhập liệu hoặc bộ chọn ngày để nhập ngày nhận phòng và ngày trả phòng, một nút tìm kiếm và một bảng để hiển thị kết quả.
    *   SearchClientFrm là giao diện để tìm kiếm và chọn khách hàng. Giao diện này cần một trường nhập liệu để nhập từ khóa tìm kiếm khách hàng theo tên, một nút tìm kiếm, một bảng để hiển thị danh sách khách hàng tìm được và một nút để thêm khách hàng mới nếu khách hàng đặt phòng chưa tồn tại trong cơ sở dữ liệu.
    *   AddClientFrm là giao diện để thêm khách hàng mới. Giao diện này cần các trường nhập liệu để nhập thông tin khách hàng: tên, địa chỉ, số chứng minh nhân dân, số điện thoại, ghi chú...
    *   ConfirmFrm là giao diện để xác nhận thông tin đặt phòng.
*   Các lớp Điều khiển -- DAO:
    *   RoomDAO có một phương thức để tìm kiếm phòng trống searchFreeRoom.
    *   ClientDAO có hai phương thức:
        *   searchClient: tìm kiếm khách hàng có tên chứa từ khóa đã nhập.
        *   addClient: thêm khách hàng mới vào cơ sở dữ liệu.
    *   BookingDAO có một phương thức để thêm một lượt đặt phòng mới addBooking.
*   Các lớp Thực thể: Room, Client, Booking, BookedRoom và User.

![](https://softwaredesign.home.blog/wp-content/uploads/2020/12/image-14.png?w=804)

_**Xem thống kê phòng**_

Trong mô-đun này, quá trình xử lý đăng nhập cũng được lược bỏ:

*   Các lớp Giao diện:
    *   ManagerHomeFrm là giao diện trang chủ dành cho Quản lý. Giao diện này cần ít nhất một nút để chuyển đến chức năng xem thống kê.
    *   SelectStatFrm là giao diện để cấu hình báo cáo thống kê. Giao diện có một lựa chọn để chọn loại và đối tượng của báo cáo thống kê.
    *   RoomStatFrm là giao diện thống kê phòng. Giao diện này cần hai trường nhập liệu hoặc bộ chọn ngày để thiết lập khoảng thời gian thống kê, một nút xem và một bảng để hiển thị kết quả.
    *   RoomDetailFrm là giao diện hiển thị thống kê chi tiết của một phòng được chọn. Giao diện này cần một bảng để hiển thị danh sách hóa đơn được phát hành trong khoảng thời gian thống kê.
*   Các lớp Điều khiển -- DAO:
    *   RoomStatDAO có một phương thức getRoomStat để lấy thống kê phòng trong khoảng thời gian được truyền vào.
    *   BillDAO có một phương thức getBillbyRoom để lấy danh sách hóa đơn của một phòng được chọn, được phát hành trong khoảng thời gian thống kê.
*   Các lớp Thực thể: RoomStat, Bill. RoomStat kế thừa từ Room. Bill cần các lớp thành phần User và Booking. Booking cần các lớp thành phần BookedRoom, Client và UsedService. UsedService cần lớp thành phần Service.

![](https://softwaredesign.home.blog/wp-content/uploads/2020/12/image-15.png?w=760)
