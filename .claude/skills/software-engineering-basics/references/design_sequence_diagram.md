# Biểu đồ tuần tự thiết kế -- Phần mềm: Nghệ thuật Thiết kế và Lập trình
_**Kịch bản phiên bản 3 cho chức năng Chỉnh sửa phòng**_

1.  Người quản lý nhập tên đăng nhập, mật khẩu và nhấn nút đăng nhập trên LoginFrm.
2.  Phương thức actionPerformed của LoginFrm được gọi.
3.  Phương thức actionPerformed gọi User để tạo một đối tượng User.
4.  Lớp User đóng gói thông tin vào một đối tượng User.
5.  Lớp User trả về đối tượng User cho phương thức actionPerformed.
6.  Phương thức actionPerformed gọi phương thức checkLogin của lớp UserDAO.
7.  Phương thức checkLogin kiểm tra thông tin đăng nhập.
8.  Phương thức checkLogin gọi lớp User để thiết lập thêm hai thuộc tính tên và chức vụ.
9.  Lớp User gọi phương thức setName, setPosition của nó.
10.  Lớp User trả về đối tượng User cho phương thức checkLogin.
11.  Phương thức checkLogin trả về kết quả cho phương thức actionPerformed.
12.  Phương thức actionPerformed gọi lớp ManagerHomeFrm.
13.  Hàm khởi tạo ManagerHomeFrm được gọi.
14.  Giao diện ManagerHomeFrm được hiển thị cho người quản lý.
15.  Người quản lý nhấn nút quản lý phòng.
16.  Phương thức actionPerformed được gọi.
17.  Phương thức actionPerformed gọi lớp RoomManageFrm.
18.  Hàm khởi tạo RoomManageFrm được gọi.
19.  Giao diện RoomManageFrm được hiển thị cho người quản lý.
20.  Người quản lý nhấn nút chỉnh sửa phòng.
21.  Phương thức actionPerformed được gọi.
22.  Phương thức actionPerformed gọi lớp SearchRoomFrm.
23.  Hàm khởi tạo SearchRoomFrm được gọi.
24.  Giao diện SearchRoomFrm được hiển thị cho người quản lý.
25.  Người quản lý nhập tên phòng và nhấn nút tìm kiếm.
26.  Phương thức actionPerformed được gọi.
27.  Phương thức actionPerformed gọi phương thức searchRoom của lớp RoomDAO.
28.  Phương thức searchRoom tìm kiếm phòng theo tên.
29.  Phương thức searchRoom gọi lớp Room để đóng gói kết quả.
30.  Lớp Room đóng gói từng đối tượng Room.
31.  Lớp Room trả về đối tượng Room cho phương thức searchRoom.
32.  Phương thức searchRoom trả về kết quả cho phương thức actionPerformed của lớp SearchRoomFrm.
33.  Phương thức actionPerformed hiển thị kết quả cho người quản lý.
34.  Người quản lý nhấn vào một phòng trong danh sách để chỉnh sửa.
35.  Phương thức actionPerformed được gọi.
36.  Phương thức actionPerformed gọi lớp EditRoomFrm.
37.  Hàm khởi tạo EditRoomFrm được gọi.
38.  Giao diện EditRoomFrm được hiển thị cho người quản lý.
39.  Người quản lý sửa đổi một số thuộc tính và nhấn nút lưu.
40.  Phương thức actionPerformed của lớp EditRoomFrm được gọi.
41.  Phương thức actionPerformed gọi lớp Room để thiết lập các thuộc tính đã sửa đổi.
42.  Lớp Room thiết lập các thuộc tính đã sửa đổi vào một đối tượng Room.
43.  Lớp Room trả về kết quả cho phương thức actionPerformed.
44.  Phương thức actionPerformed gọi phương thức updateRoom của lớp RoomDAO.
45.  Phương thức updateRoom thực thi.
46.  Phương thức updateRoom trả về cho phương thức actionPerformed.
47.  Phương thức actionPerformed hiển thị thông báo thành công.
48.  Người quản lý nhấn nút Đồng ý trên thông báo.
49.  Phương thức actionPerformed gọi lớp ManagerHomeFrm.
50.  Giao diện ManagerHomeFrm được hiển thị cho khách hàng.

![](https://softwaredesign.home.blog/wp-content/uploads/2020/12/sd-edit-room-design.jpg?w=722)

_**Kịch bản phiên bản 3 cho chức năng đặt phòng**_

1.  Một khách hàng gọi điện đến đại diện khách sạn là một nhân viên bán hàng để đặt phòng.
2.  Nhân viên bán hàng nhấn vào chức năng đặt phòng trên giao diện SellerHomeFrm.
3.  Phương thức actionPerformed của lớp SellerHomeFrm được gọi.
4.  Phương thức actionPerformed gọi lớp SearchFreeRoomFrm.
5.  Hàm khởi tạo SearchFreeRoomFrm được gọi.
6.  Giao diện SearchFreeRoomFrm được hiển thị cho nhân viên bán hàng.
7.  Nhân viên bán hàng hỏi khách hàng về ngày nhận phòng và ngày trả phòng.
8.  Khách hàng thông báo ngày nhận phòng và ngày trả phòng mong muốn cho nhân viên bán hàng.
9.  Nhân viên bán hàng nhập thông tin vào các trường ngày nhận phòng và ngày trả phòng rồi nhấn tìm kiếm.
10.  Phương thức actionPerformed của lớp SearchFreeRoomFrm được gọi.
11.  Phương thức actionPerformed gọi phương thức searchFreeRoom của lớp RoomDAO.
12.  Phương thức searchFreeRoom thực thi.
13.  Phương thức searchFreeRoom gọi lớp Room để đóng gói kết quả.
14.  Lớp Room đóng gói từng kết quả vào một đối tượng Room.
15.  Lớp Room trả về đối tượng cho phương thức searchFreeRoom.
16.  Phương thức searchFreeRoom trả về kết quả cho phương thức actionPerformed.
17.  Phương thức actionPerformed hiển thị kết quả trên giao diện SearchFreeRoomFrm cho nhân viên bán hàng.
18.  Nhân viên bán hàng thông báo tất cả các phòng còn trống cho khách hàng và yêu cầu khách chọn một phòng.
19.  Khách hàng thông báo phòng mong muốn cho nhân viên bán hàng.
20.  Nhân viên bán hàng nhấn vào phòng đáp ứng yêu cầu của khách hàng.
21.  Phương thức actionPerformed của lớp SearchFreeRoomFrm được gọi.
22.  Phương thức actionPerformed gọi lớp Booking để đóng gói thông tin nhằm chuyển sang giao diện khác.
23.  Lớp Booking gọi các phương thức thiết lập giá trị của nó.
24.  Lớp Booking gọi lớp BookedRoom để đóng gói thành phần con của nó.
25.  Lớp BookedRoom đóng gói các thuộc tính của nó.
26.  Lớp BookedRoom trả về đối tượng đã đóng gói cho lớp Booking.
27.  Lớp Booking trả về đối tượng Booking cho phương thức actionPerformed.
28.  Phương thức actionPerformed gọi lớp SearchClientFrm.
29.  Hàm khởi tạo SearchClientFrm được gọi.
30.  Giao diện SearchClientFrm được hiển thị cho nhân viên bán hàng.
31.  Nhân viên bán hàng yêu cầu thông tin cá nhân từ khách hàng.
32.  Khách hàng cung cấp thông tin cho nhân viên bán hàng.
33.  Nhân viên bán hàng nhập tên khách hàng và nhấn tìm kiếm.
34.  Phương thức actionPerformed của lớp SearchClientFrm được gọi.
35.  Phương thức actionPerformed gọi phương thức searchClient của lớp ClientDAO.
36.  Phương thức searchClient thực thi.
37.  Phương thức searchClient gọi lớp Client để đóng gói kết quả.
38.  Lớp Client đóng gói một đối tượng Client.
39.  Lớp Client trả về đối tượng đã đóng gói cho phương thức searchClient.
40.  Phương thức searchClient trả về kết quả cho phương thức actionPerformed.
41.  Phương thức actionPerformed hiển thị kết quả trên SearchClientFrm cho nhân viên bán hàng.
42.  Nhân viên bán hàng nhấn vào dòng tương ứng với khách hàng hiện tại.
43.  Phương thức actionPerformed của lớp SearchClientFrm được gọi.
44.  Phương thức actionPerformed gọi lớp Booking để thêm thông tin khách hàng vào đó.
45.  Lớp Booking gọi phương thức setClient.
46.  Lớp Booking trả về đối tượng đã đóng gói cho phương thức actionPerformed.
47.  Phương thức actionPerformed gọi lớp ConfirmFrm.
48.  Hàm khởi tạo ConfirmFrm được gọi.
49.  Giao diện ConfirmFrm được hiển thị cho nhân viên bán hàng.
50.  Nhân viên bán hàng nhắc lại thông tin đặt phòng cho khách hàng và yêu cầu khách xác nhận.
51.  Khách hàng xác nhận.
52.  Nhân viên bán hàng nhấn nút xác nhận.
53.  Phương thức actionPerformed của lớp ConfirmFrm được gọi.
54.  Phương thức actionPerformed gọi phương thức addBooking của lớp BookingDAO.
55.  Phương thức addBooking thực thi.
56.  Phương thức addBooking trả về quyền điều khiển cho phương thức actionPerformed.
57.  Phương thức actionPerformed hiển thị thông báo thành công.
58.  Nhân viên bán hàng nhấn nút Đồng ý trên thông báo.
59.  Phương thức actionPerformed gọi lại giao diện SellerHomeFrm.
60.  Giao diện SellerHomeFrm được hiển thị cho nhân viên bán hàng.
61.  Nhân viên bán hàng xác nhận việc đặt phòng thành công cho khách hàng và kết thúc cuộc gọi.

![](https://softwaredesign.home.blog/wp-content/uploads/2020/12/image-17.png?w=843)

_**Kịch bản phiên bản 3 cho chức năng xem thống kê phòng**_

1.  Người quản lý nhấn vào chức năng xem báo cáo trên giao diện ManagerHomeFrm sau khi đã đăng nhập.
2.  Phương thức actionPerformed của lớp ManagerHomeFrm được gọi.
3.  Phương thức actionPerformed gọi lớp SelectStatFrm.
4.  Hàm khởi tạo SelectStatFrm được gọi.
5.  Giao diện SelectStatFrm được hiển thị cho người quản lý.
6.  Người quản lý cấu hình để xem báo cáo phòng theo doanh thu.
7.  Phương thức actionPerformed của lớp SelectStatFrm được gọi.
8.  Phương thức actionPerformed gọi lớp RoomStatFrm.
9.  Hàm khởi tạo RoomStatFrm được gọi.
10.  Giao diện RoomStatFrm được hiển thị cho người quản lý.
11.  Người quản lý nhập ngày bắt đầu, ngày kết thúc thống kê và nhấn nút xem.
12.  Phương thức actionPerformed của lớp RoomStatFrm được gọi.
13.  Phương thức actionPerformed gọi phương thức getRoomStat của lớp RoomStatDAO.
14.  Phương thức getRoomStat thực thi.
15.  Phương thức getRoomStat gọi lớp RoomStat để đóng gói kết quả.
16.  Lớp RoomStat đóng gói các thuộc tính thông thường của nó.
17.  Lớp RoomStat gọi các phương thức thiết lập giá trị từ lớp Room để đóng gói thuộc tính kế thừa từ lớp Room.
18.  Lớp Room đóng gói các thuộc tính của nó.
19.  Lớp Room trả về đối tượng đã đóng gói cho lớp RoomStat.
20.  Lớp RoomStat trả về kết quả đã đóng gói cho phương thức getRoomStat.
21.  Phương thức getRoomStat trả về kết quả cho phương thức actionPerformed.
22.  Phương thức actionPerformed hiển thị kết quả trên giao diện RoomStatFrm cho người quản lý.
23.  Người quản lý nhấn vào một dòng để xem chi tiết của một phòng.
24.  Phương thức actionPerformed của lớp RoomStatFrm được gọi.
25.  Phương thức actionPerformed gọi lớp RoomDetailFrm.
26.  Hàm khởi tạo RoomDetailFrm được gọi.
27.  Hàm khởi tạo RoomDetailFrm gọi phương thức getBookingOfRoom của lớp BookingDAO.
28.  Phương thức getBookingOfRoom thực thi.
29.  Phương thức getBookingOfRoom gọi lớp Booking để đóng gói đối tượng.
30.  Lớp Booking đóng gói các thuộc tính thông thường của nó.
31.  Lớp Booking gọi lớp Client để đóng gói thuộc tính đối tượng của nó.
32.  Lớp Client đóng gói các thuộc tính của nó.
33.  Lớp Client trả về đối tượng đã đóng gói cho lớp Booking.
34.  Lớp Client gọi lớp BookedRoom để đóng gói thuộc tính đối tượng của nó.
35.  Lớp BookedRoom đóng gói các thuộc tính thông thường của nó.
36.  Lớp BookedRoom gọi lớp Room để đóng gói các thuộc tính đối tượng của nó.
37.  Lớp Room đóng gói các thuộc tính của nó.
38.  Lớp Room trả về đối tượng đã đóng gói cho lớp BookedRoom.
39.  Lớp BookedRoom trả về đối tượng cho lớp Booking.
40.  Lớp Booking trả về đối tượng cho phương thức getBookingOfRoom.
41.  Phương thức getBookingOfRoom trả về kết quả cho hàm khởi tạo RoomDetailFrm.
42.  Hàm khởi tạo RoomDetailFrm hiển thị thống kê chi tiết của phòng đã chọn trên giao diện RoomDetailFrm cho người quản lý.

![](https://softwaredesign.home.blog/wp-content/uploads/2020/12/image-18.png?w=985)