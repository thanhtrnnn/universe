# Mô tả hệ thống bằng ngôn ngữ tự nhiên – Phần mềm: Nghệ thuật Thiết kế và Lập trình
_**Mục tiêu và phạm vi:**_

*   Đây là ứng dụng trên máy tính để bàn, được sử dụng nội bộ bên trong khách sạn.
*   Chỉ có nhân viên của khách sạn mới có thể sử dụng ứng dụng này, bao gồm: quản lý khách sạn, nhân viên bán hàng, quản trị viên hệ thống và lễ tân.
*   Ứng dụng này hỗ trợ quản lý cho duy nhất một khách sạn.
*   Ứng dụng này có thể được cài đặt trên nhiều máy tính của nhân viên khách sạn. Tuy nhiên, cơ sở dữ liệu được lưu trữ trên máy chủ của khách sạn.

_**Người dùng và các chức năng mà mỗi người dùng có thể sử dụng:**_

*   Chỉ có nhân viên của khách sạn mới có thể sử dụng ứng dụng này, bao gồm: quản lý khách sạn, nhân viên bán hàng, quản trị viên hệ thống và lễ tân.
*   Lễ tân có thể sử dụng các chức năng sau:
    *   Đặt phòng cho khách hàng tại chỗ,
    *   Hủy đặt phòng cho khách hàng tại chỗ,
    *   Làm thủ tục nhận phòng cho khách hàng,
    *   Làm thủ tục trả phòng và xử lý thanh toán cho khách hàng.
*   Nhân viên bán hàng có thể sử dụng các chức năng sau:
    *   Đặt phòng cho khách hàng từ xa qua điện thoại,
    *   Hủy đặt phòng cho khách hàng từ xa qua điện thoại.
*   Quản lý có thể sử dụng các chức năng sau:
    *   Quản lý thông tin khách sạn: thêm, sửa,
    *   Quản lý thông tin phòng: thêm, sửa, xóa,
    *   Xem báo cáo thống kê: báo cáo doanh thu theo phòng, theo khách hàng, theo tháng... Báo cáo tỷ lệ lấp đầy của phòng...
*   Quản trị viên hệ thống có thể sử dụng các chức năng sau:
    *   Quản lý tài khoản người dùng theo yêu cầu: thêm, sửa, xóa.

_**Quy trình nghiệp vụ chi tiết của các chức năng:**_

Phần này trình bày quy trình nghiệp vụ chi tiết của các chức năng chính trong hệ thống.

*   _Sửa thông tin phòng_: Quản lý đăng nhập vào hệ thống -> Giao diện của quản lý xuất hiện, có các lựa chọn sau: quản lý thông tin khách sạn, quản lý thông tin phòng và xem báo cáo thống kê -> Quản lý chọn quản lý thông tin phòng -> Giao diện quản lý thông tin phòng xuất hiện với ba lựa chọn: thêm phòng, sửa phòng, xóa phòng -> Quản lý nhấn vào chức năng sửa -> Giao diện tìm kiếm phòng xuất hiện với một ô nhập liệu để nhập từ khóa và một nút tìm kiếm -> Quản lý nhập từ khóa về tên của phòng cần sửa và sau đó nhấn nút tìm kiếm -> Danh sách tất cả các phòng có tên chứa từ khóa đã nhập xuất hiện, mỗi dòng tương ứng với một phòng gồm: mã, tên, loại, giá, mô tả -> Quản lý nhấn vào phòng cần sửa -> Giao diện sửa phòng xuất hiện với các ô nhập liệu có thể chỉnh sửa đã chứa sẵn các giá trị thuộc tính tương ứng, ngoại trừ mã phòng không thể chỉnh sửa -> Quản lý thay đổi một số giá trị thuộc tính và sau đó nhấn nút lưu -> Hệ thống thông báo thành công và sau đó quay về giao diện chính của quản lý.
*   _Đặt phòng cho khách hàng từ xa qua điện thoại_: Khách hàng gọi điện thoại đến khách sạn để đặt phòng -> Lễ tân chuyển cuộc gọi đến nhân viên bán hàng -> Nhân viên bán hàng hỏi khách hàng về khoảng thời gian mà khách hàng muốn lưu trú tại khách sạn và chọn chức năng đặt phòng trên giao diện chính của nhân viên bán hàng -> Giao diện tìm kiếm phòng trống xuất hiện với hai ô nhập ngày: ngày nhận phòng và ngày trả phòng, một nút tìm kiếm -> Nhân viên bán hàng nhập ngày nhận phòng và trả phòng theo yêu cầu của khách hàng và sau đó nhấn nút tìm kiếm -> Danh sách tất cả các phòng trống trong khoảng thời gian đó được liệt kê dưới dạng bảng, mỗi dòng tương ứng với một phòng gồm: mã, tên, loại, giá, mô tả -> Nhân viên bán hàng thông báo cho khách hàng tất cả các loại phòng trống và yêu cầu khách hàng chọn một phòng hoặc nhiều phòng -> Khách hàng thông báo lựa chọn của mình -> Nhân viên bán hàng nhấn vào phòng đáp ứng yêu cầu của khách hàng -> Hệ thống chuyển sang giao diện thông tin khách hàng với một ô nhập liệu và một nút tìm kiếm -> Nhân viên bán hàng yêu cầu khách hàng cung cấp thông tin về: số chứng minh nhân dân, tên, địa chỉ, số điện thoại và sau đó nhập tên khách hàng vào ô nhập liệu và nhấn nút tìm kiếm -> Danh sách tất cả khách hàng có tên chứa từ khóa đã nhập xuất hiện dưới dạng bảng, mỗi dòng tương ứng với một khách hàng gồm: số chứng minh nhân dân, tên, địa chỉ, số điện thoại, ghi chú -> Nhân viên bán hàng nhấn vào dòng có thông tin trùng khớp với khách hàng hiện tại. Nếu không có dòng nào phù hợp, phải nhấn nút thêm khách hàng mới để thêm khách hàng mới -> Hệ thống hiển thị giao diện xác nhận với: thông tin khách hàng, thông tin phòng đã chọn, ngày nhận phòng và trả phòng -> Nhân viên bán hàng xác nhận các thông tin này với khách hàng và khách hàng xác nhận đồng ý -> Nhân viên bán hàng nhấn nút xác nhận -> Hệ thống thông báo thành công và sau đó quay về giao diện chính của nhân viên bán hàng -> Nhân viên bán hàng thông báo đặt phòng thành công cho khách hàng và kết thúc cuộc gọi.
*   _Xem báo cáo phòng theo doanh thu_: Quản lý đăng nhập vào hệ thống -> Giao diện của quản lý xuất hiện, có các lựa chọn sau: quản lý thông tin khách sạn, quản lý thông tin phòng và xem báo cáo thống kê -> Quản lý chọn xem báo cáo thống kê -> Giao diện cấu hình báo cáo xuất hiện với hai danh sách lựa chọn. Thứ nhất, đối tượng của báo cáo, bao gồm: khách sạn, phòng, khách hàng, dịch vụ, doanh thu. Thứ hai, tiêu chí của báo cáo, bao gồm: theo doanh thu, theo thời gian nếu đối tượng là doanh thu, theo tỷ lệ lấp đầy nếu đối tượng là khách sạn hoặc phòng -> Quản lý chọn đối tượng là phòng, tiêu chí là theo doanh thu -> Giao diện báo cáo xuất hiện với hai ô nhập ngày: ngày bắt đầu và ngày kết thúc của khoảng thời gian cần thống kê -> Quản lý nhập các ngày này và nhấn nút xem -> Kết quả báo cáo được liệt kê dưới dạng bảng, sắp xếp theo tổng doanh thu, mỗi dòng tương ứng với một phòng gồm: mã, tên, loại, tổng số ngày lấp đầy, tổng doanh thu -> Nhân viên bán hàng nhấn vào một dòng để xem chi tiết -> Giao diện xuất hiện với thông tin phòng đã chọn và danh sách đặt phòng liên quan đến phòng đó, mỗi dòng tương ứng với một lần đặt phòng gom theo thứ tự thời gian nhận phòng: tên khách hàng, ngày nhận phòng, ngày trả phòng, giá, tổng thu nhập -> Nhân viên bán hàng nhấn nút quay lại để quay về giao diện chính của quản lý.