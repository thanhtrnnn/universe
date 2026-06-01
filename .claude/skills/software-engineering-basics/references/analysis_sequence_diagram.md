# Biểu đồ tuần tự phân tích -- Phần mềm: Nghệ thuật Thiết kế và Lập trình
_**a. Chỉnh sửa phòng**_

Kịch bản phiên bản 2

1.  Người quản lý nhập tên đăng nhập/mật khẩu rồi nhấn nút Đăng nhập.
2.  Lớp LoginView gọi lớp User để xử lý.
3.  Lớp User gọi phương thức checkLogin. Đăng nhập thành công.
4.  Lớp User trả kết quả về cho lớp LoginView.
5.  Lớp LoginView gọi lớp ManagerHomeView.
6.  Lớp ManagerHomeView hiển thị giao diện cho người quản lý.
7.  Người quản lý chọn chức năng quản lý phòng.
8.  Lớp ManagerHomeView gọi lớp ManageRoomView.
9.  Lớp ManageRoomView hiển thị giao diện cho người quản lý.
10.  Người quản lý chọn chức năng chỉnh sửa phòng.
11.  Lớp ManageRoomView gọi lớp SearchRoomView.
12.  Lớp SearchRoomView hiển thị giao diện cho người quản lý.
13.  Người quản lý nhập từ khóa và nhấn nút tìm kiếm.
14.  Lớp SearchRoomView gọi lớp Room để xử lý.
15.  Lớp Room gọi phương thức searchRoom.
16.  Lớp Room trả kết quả về cho lớp SearchRoomView.
17.  Lớp SearchRoomView hiển thị kết quả cho người quản lý.
18.  Người quản lý chọn một phòng để chỉnh sửa.
19.  Lớp SearchRoomView gọi lớp EditRoomView.
20.  Lớp EditRoomView hiển thị thông tin hiện có của phòng đã chọn cho người quản lý.
21.  Người quản lý thay đổi một số thuộc tính và nhấn nút lưu.
22.  Lớp EditRoomView gọi lớp Room để xử lý.
23.  Lớp Room gọi phương thức updateRoom.
24.  Lớp Room trả về cho lớp EditRoomView.
25.  Lớp EditRoomView hiển thị thông báo thành công cho người quản lý.
26.  Người quản lý nhấn nút Đồng ý trên thông báo.
27.  Lớp EditRoomView gọi lớp ManagerHomeView.
28.  Lớp ManagerHomeView hiển thị giao diện cho người quản lý.

![](https://softwaredesign.home.blog/wp-content/uploads/2020/12/image-5.png?w=919)

_**b. Đặt phòng**_

Kịch bản phiên bản 2 -- không bao gồm giai đoạn đăng nhập

1.  Một khách hàng từ xa gọi điện cho nhân viên bán hàng để đặt phòng.
2.  Nhân viên bán hàng chọn chức năng đặt phòng trên giao diện SellerHomeView.
3.  Lớp SellerHomeView gọi lớp SearchFreeRoomView.
4.  Lớp SearchFreeRoomView hiển thị giao diện cho nhân viên bán hàng.
5.  Nhân viên bán hàng hỏi khách hàng ngày nhận phòng và trả phòng mong muốn.
6.  Khách hàng trả lời ngày nhận phòng và trả phòng.
7.  Nhân viên bán hàng nhập ngày nhận phòng, trả phòng và nhấn nút tìm kiếm.
8.  Lớp SearchFreeRoomView gọi lớp Room để xử lý.
9.  Lớp Room gọi phương thức searchFreeRoom.
10.  Lớp Room trả kết quả về cho lớp SearchFreeRoomView.
11.  Lớp SearchFreeRoomView hiển thị kết quả cho nhân viên bán hàng.
12.  Nhân viên bán hàng thông báo các phòng trống cho khách hàng và yêu cầu khách hàng chọn.
13.  Khách hàng cho biết lựa chọn của mình.
14.  Nhân viên bán hàng chọn các phòng phù hợp với yêu cầu của khách hàng.
15.  Lớp SearchFreeRoomView gọi lớp SearchClientView.
16.  Lớp SearchClientView hiển thị giao diện cho nhân viên bán hàng.
17.  Nhân viên bán hàng hỏi thông tin cá nhân của khách hàng.
18.  Khách hàng cung cấp thông tin cho nhân viên bán hàng.
19.  Nhân viên bán hàng nhập tên khách hàng và nhấn nút tìm kiếm.
20.  Lớp SearchClientView gọi lớp Client để xử lý.
21.  Lớp Client gọi phương thức searchClient.
22.  Lớp Client trả kết quả về cho lớp SearchClientView.
23.  Lớp SearchClientView hiển thị kết quả cho nhân viên bán hàng.
24.  Nhân viên bán hàng chọn dòng tương ứng với khách hàng hiện tại.
25.  Lớp SearchClientView gọi lớp ConfirmView.
26.  Lớp ConfirmView hiển thị toàn bộ thông tin đặt phòng cho nhân viên bán hàng.
27.  Nhân viên bán hàng nhắc lại các thông tin này cho khách hàng và yêu cầu khách hàng xác nhận.
28.  Khách hàng xác nhận.
29.  Nhân viên bán hàng nhấn nút xác nhận.
30.  Lớp ConfirmView gọi lớp Booking để xử lý.
31.  Lớp Booking gọi phương thức addBooking.
32.  Lớp Booking trả về cho lớp ConfirmView.
33.  Lớp ConfirmView hiển thị thông báo thành công cho nhân viên bán hàng.
34.  Nhân viên bán hàng nhấn nút Đồng ý trên thông báo.
35.  Lớp ConfirmView gọi lớp SellerHomeView.
36.  Lớp SellerHomeView hiển thị giao diện cho nhân viên bán hàng.
37.  Nhân viên bán hàng thông báo đặt phòng thành công cho khách hàng và kết thúc cuộc gọi.

![](https://softwaredesign.home.blog/wp-content/uploads/2020/12/image-3.png?w=878)

_**c. Xem thống kê phòng theo doanh thu**_

Kịch bản phiên bản 2 -- không bao gồm giai đoạn đăng nhập:

1.  Người quản lý chọn chức năng xem báo cáo trên giao diện ManagerHomeView.
2.  Lớp ManagerHomeView gọi lớp SelectStatView.
3.  Lớp SelectStatView hiển thị giao diện cho người quản lý.
4.  Người quản lý chọn xem thống kê phòng theo doanh thu.
5.  Lớp SelectStatView gọi lớp RoomStatView.
6.  Lớp RoomStatView hiển thị giao diện cho người quản lý.
7.  Người quản lý nhập ngày bắt đầu, ngày kết thúc và nhấn nút xem.
8.  Lớp RoomStatView gọi lớp RoomStat.
9.  Lớp RoomStat gọi phương thức getRoomStat.
10.  Lớp RoomStat trả kết quả về cho lớp RoomStatView.
11.  Lớp RoomStatView hiển thị kết quả cho người quản lý.
12.  Người quản lý nhấn vào một phòng để xem chi tiết.
13.  Lớp RoomStatView gọi lớp RoomDetailView.
14.  Lớp RoomDetailView gọi lớp Bill để lấy dữ liệu.
15.  Lớp Bill gọi phương thức getBillofRoom.
16.  Lớp Bill trả kết quả về cho lớp RoomDetailView.
17.  Lớp RoomDetailView hiển thị kết quả cho người quản lý.
18.  Người quản lý nhấn nút quay lại sau khi xem xong.
19.  Lớp RoomDetailView gọi lớp SelectStatView.
20.  Lớp SelectStatView hiển thị giao diện cho người quản lý.

![](https://softwaredesign.home.blog/wp-content/uploads/2020/12/image.png?w=789)
