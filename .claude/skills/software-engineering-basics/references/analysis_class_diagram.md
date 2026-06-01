# Sơ đồ lớp phân tích – Phần mềm: Nghệ thuật Thiết kế và Lập trình
**Các bước trong quy trình**

*   Bước 1: Một giao diện người dùng -- ngoại trừ cảnh báo/thông báo, hộp thoại xác nhận... -- tạo một lớp giao diện.
*   Bước 2: Xem xét các thành phần cần thiết trong mỗi giao diện, đặt tên thành phần với tiền tố tương ứng loại của nó:
    *   in: cho các thành phần nhập liệu -- ô nhập văn bản, ô nhập ngày tháng...
    *   out: cho các thành phần hiển thị -- bảng, nội dung...
    *   sub: cho các thành phần gửi dữ liệu -- nút bấm, liên kết...
    *   và có thể kết hợp các loại trên.
*   Bước 3: Xem xét xem chúng ta có cần thực hiện hành động/chức năng nào dưới lớp giao diện không. Với mỗi chức năng cần thiết, trả lời bốn câu hỏi:
    *   Tên phù hợp của phương thức là gì -- có thể đặt tên theo quy ước mã nguồn
    *   Các tham số đầu vào là gì?
    *   Tham số đầu ra là gì?
    *   Phương thức nên được gán vào lớp nào? Xem xét nguyên tắc sau:
        *   Nếu tham số đầu ra là một loại lớp thực thể, thì phương thức được gán cho lớp thực thể đó.
        *   Nếu không phải, xem xét các tham số đầu vào. Nếu chúng chỉ bao gồm một lớp thực thể, thì gán phương thức cho lớp thực thể đó. Nếu chúng bao gồm nhiều loại lớp thực thể, thì xem trong số đó lớp thực thể nào có thể chứa tất cả các tham số đầu vào để gán phương thức.
*   Bước 4: Xây dựng sơ đồ lớp cho mô-đun.

**Áp dụng**

_**a. Chỉnh sửa phòng**_

Phân tích mô-đun này:

*   Vào hệ thống -- Giao diện đăng nhập xuất hiện -- cần một lớp: LoginView
    *   ô nhập tên đăng nhập -- inUsername
    *   ô nhập mật khẩu -- inPassword
    *   nút gửi để đăng nhập -- subLogin
*   Nhập tên đăng nhập/mật khẩu -- hệ thống phải kiểm tra thông tin đăng nhập có đúng không -- cần một phương thức:
    *   tên: checkLogin
    *   đầu vào: tên đăng nhập, mật khẩu -- thuộc lớp User
    *   đầu ra: giá trị đúng/sai
    *   \-> gán cho lớp thực thể: User.
*   Khi đăng nhập thành công -- giao diện chính của quản lý xuất hiện -- cần một lớp: ManagerHomeView có ít nhất:
    *   một tùy chọn để chọn quản lý phòng -- subRoomManage.
*   Chọn tùy chọn quản lý phòng -- Giao diện quản lý phòng xuất hiện -- cần một lớp: RoomManageView có ít nhất:
    *   một tùy chọn để chọn chỉnh sửa phòng -- subEditRoom.
*   Chọn chỉnh sửa phòng -- Giao diện tìm kiếm phòng xuất hiện -- cần một lớp: SearchRoomView:
    *   ô nhập văn bản để nhập từ khóa -- tìm theo tên phòng -- inKey
    *   nút tìm kiếm -- subSearch
    *   bảng kết quả có thể nhấn vào để chọn phòng -- outsubListRoom.
*   Nhập từ khóa và nhấn tìm kiếm -- hệ thống phải tìm tất cả các phòng có tên chứa từ khóa đã nhập -- cần một phương thức:
    *   tên: searchRoom
    *   đầu vào: một từ khóa
    *   đầu ra: danh sách phòng
    *   \-> gán cho lớp thực thể: Room.
*   Danh sách phòng tìm được sẽ được hiển thị trong SearchRoomView.
*   Nhấn vào một phòng để chỉnh sửa -- giao diện chỉnh sửa phòng xuất hiện -- cần một lớp: EditRoomView
    *   mã phòng: chỉ đọc -- outId
    *   tên phòng: đọc và chỉnh sửa được -- inoutName
    *   loại phòng: đọc và chỉnh sửa được -- inoutType
    *   giá phòng: đọc và chỉnh sửa được -- inoutPrice
    *   mô tả phòng: đọc và chỉnh sửa được -- inoutDes
    *   nút lưu: gửi dữ liệu -- subSave.
*   Chỉnh sửa một số thuộc tính và nhấn lưu -- Hệ thống phải cập nhật vào cơ sở dữ liệu -- cần một phương thức:
    *   tên: updateRoom
    *   đầu vào: một đối tượng Room
    *   đầu ra: không hoặc giá trị đúng/sai
    *   \-> gán cho lớp thực thể: Room.
*   Sau khi cập nhật, hệ thống quay về ManagerHomeView.

![](https://softwaredesign.home.blog/wp-content/uploads/2020/12/image-6-1.png?w=664)

_**b. Đặt phòng**_

Phân tích mô-đun này -- bỏ qua bước đăng nhập

*   Khi đăng nhập thành công -- giao diện chính của nhân viên bán hàng xuất hiện -- cần một lớp: SellerHomeView có ít nhất:
    *   một tùy chọn để chọn đặt phòng -- subBooking.
*   Khi khách hàng gọi đến đặt phòng -- nhân viên bán hàng chọn tùy chọn đặt phòng -- giao diện tìm phòng trống xuất hiện -- cần một lớp: SearchFreeRoomView
    *   nhập ngày nhận phòng -- inCheckin
    *   nhập ngày trả phòng -- inCheckout
    *   nút tìm kiếm -- subSearch
    *   bảng hiển thị kết quả -- có thể nhấn vào -- outsubListRoom.
*   Nhập ngày nhận phòng, ngày trả phòng và nhấn nút tìm kiếm -- Hệ thống phải tìm tất cả các phòng trống trong khoảng thời gian đó -- cần một phương thức:
    *   tên: searchFreeRoom
    *   đầu vào: ngày nhận phòng, ngày trả phòng
    *   đầu ra: danh sách phòng
    *   gán phương thức này cho lớp thực thể: Room.
*   Kết quả được trả về và hiển thị trên SearchFreeRoomView.
*   Nhân viên bán hàng chọn một phòng phù hợp với yêu cầu của khách hàng -- Giao diện tìm kiếm khách hàng xuất hiện -- cần một lớp: SearchClientView
    *   ô nhập tên để tìm kiếm -- inKey
    *   nút tìm kiếm -- subSearch
    *   danh sách kết quả -- outsubListClient
    *   nút thêm khách hàng mới -- trong trường hợp khách hàng mới -- subAddClient.
*   Nhập tên khách hàng để tìm kiếm -- Hệ thống phải tìm tất cả khách hàng có tên chứa từ khóa đã nhập -- cần một phương thức:
    *   tên: searchClient
    *   đầu vào: một từ khóa
    *   đầu ra: danh sách khách hàng
    *   \-> gán phương thức này cho lớp thực thể: Client.
*   Kết quả được trả về và hiển thị trên SearchClientView.
*   Nhân viên bán hàng chọn đúng khách hàng -- trong trường hợp khách hàng mới, chọn thêm khách hàng mới và giao diện thêm khách hàng xuất hiện, cần một phương thức để thêm khách hàng mới vào cơ sở dữ liệu.
*   Giao diện xác nhận xuất hiện -- cần một lớp ConfirmView
    *   hiển thị tất cả thông tin về đơn đặt phòng -- outBooking
    *   nút xác nhận -- subConfirm.
*   Nhân viên bán hàng chọn xác nhận sau khi đã thỏa thuận với khách hàng -- Hệ thống phải lưu đơn đặt phòng vào cơ sở dữ liệu -- cần một phương thức:
    *   tên: addBooking
    *   đầu vào: một đối tượng Booking
    *   đầu ra: không hoặc giá trị đúng/sai
    *   \-> gán phương thức này cho lớp thực thể: Booking.
*   Sau khi lưu vào cơ sở dữ liệu, hệ thống quay về SellerHomeView.

![](https://softwaredesign.home.blog/wp-content/uploads/2020/12/image-4.png?w=643)

_**c. Xem báo cáo thống kê**_

Phân tích mô-đun này -- bỏ qua bước đăng nhập:

*   Khi đăng nhập thành công -- giao diện chính của quản lý xuất hiện -- cần một lớp: ManagerHomeView có ít nhất:
    *   một tùy chọn để chọn xem báo cáo -- subViewStat.
*   Chọn xem báo cáo -- Giao diện cấu hình báo cáo xuất hiện -- cần một lớp: SelectStatView
    *   danh sách để chọn đối tượng thống kê -- inStatObject
    *   danh sách để chọn loại thống kê -- inStatType.
*   Chọn thống kê theo phòng dựa trên doanh thu -- Giao diện thống kê xuất hiện -- cần một lớp: RoomStatView:
    *   nhập ngày bắt đầu -- inStartDate
    *   nhập ngày kết thúc -- inEndDate
    *   nút xem -- subView
    *   danh sách tất cả các phòng với thống kê tương ứng -- outsubListRoomStat.
*   Nhập ngày bắt đầu, ngày kết thúc mong muốn và nhấn nút xem -- Hệ thống phải truy vấn tất cả thống kê phòng -- cần một phương thức:
    *   tên: getRoomStat
    *   đầu vào: ngày bắt đầu và ngày kết thúc
    *   đầu ra: danh sách RoomStat
    *   \-> gán phương thức này cho lớp thực thể: RoomStat.
*   Kết quả được hiển thị trên RoomStatView
*   Nhấn vào một phòng để xem chi tiết -- Giao diện thống kê chi tiết của phòng xuất hiện -- cần một lớp: RoomDetailView
    *   danh sách hóa đơn liên quan đến phòng đã chọn trong khoảng thời gian đó -- outListBill
    *   nút quay lại -- subBack.
*   Để có dữ liệu hiển thị trên RoomDetailView, hệ thống phải truy vấn tất cả hóa đơn liên quan đến phòng trong khoảng thời gian đó -- cần một phương thức:
    *   tên: getBillbyRoom
    *   đầu vào: ngày bắt đầu, ngày kết thúc, và mã phòng
    *   đầu ra: danh sách Bill
    *   \-> gán phương thức này cho lớp thực thể: Bill.

![](https://softwaredesign.home.blog/wp-content/uploads/2020/12/image-2-1.png?w=480)