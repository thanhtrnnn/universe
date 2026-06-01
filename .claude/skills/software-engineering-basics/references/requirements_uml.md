# Mô tả hệ thống bằng UML – Phần mềm: Nghệ thuật Thiết kế và Lập trình
**Các bước xây dựng biểu đồ use case tổng quát:**

*   Bước 1: Đề xuất tác nhân. Với mỗi người dùng, ta có thể đề xuất họ là một tác nhân của hệ thống. Nếu một số người dùng có các đặc điểm chung, ta có thể đề xuất một tác nhân trừu tượng cho tất cả, rồi mỗi tác nhân tương ứng kế thừa từ tác nhân trừu tượng đó. Cũng cần xem xét mọi người dùng gián tiếp có thể khởi phát một chức năng nào đó của hệ thống.
*   Bước 2: Đề xuất các use case. Với mỗi chức năng của một tác nhân, hãy đề xuất nó như một use case.
*   Bước 3: Tinh chỉnh các use case. Nếu có ít nhất hai use case tương tự nhau, chúng nên được gộp lại thành một. Nếu việc gộp lại khiến số lượng tác nhân liên quan khó hiểu, ta có thể dùng một use case trừu tượng làm cha chung của chúng.

**Các bước xây dựng biểu đồ use case chi tiết cho từng chức năng:**

*   Bước 1: Trích phần use case của chức năng từ use case tổng quát.
*   Bước 2: Đề xuất các use case con: một (hoặc nhiều) giao diện có tương tác với tác nhân có thể là một use case con.
*   Bước 3: Xác định mối quan hệ giữa từng use case con với use case chính: tổng quát hóa, bao gồm, hoặc mở rộng.
*   Bước 4: Gộp một số use case con tương tự thành một use case tổng quát hơn nếu cần thiết.

**Xây dựng biểu đồ use case tổng quát**

Bước 1: tác nhân

*   Các tác nhân trực tiếp: quản lý, quản trị viên (admin), nhân viên bán hàng, lễ tân. Họ cùng thuộc loại nhân viên của khách sạn (Nhân viên – tác nhân trừu tượng).
*   Tác nhân gián tiếp: khách hàng. Họ có thể khởi phát một số chức năng: đặt phòng, nhận phòng, trả phòng, thanh toán.

Bước 2 và 3: use case

*   Lễ tân có thể sử dụng các chức năng sau:
    *   Đặt phòng cho khách hàng tại chỗ -> đặt phòng tại chỗ,
    *   Hủy đặt phòng cho khách hàng tại chỗ -> hủy đặt phòng tại chỗ,
    *   Làm thủ tục nhận phòng cho khách hàng -> nhận phòng,
    *   Làm thủ tục trả phòng và xử lý thanh toán cho khách hàng -> trả phòng.
*   Nhân viên bán hàng có thể sử dụng các chức năng sau:
    *   Đặt phòng cho khách hàng từ xa qua điện thoại -> đặt phòng qua điện thoại,
    *   Hủy đặt phòng cho khách hàng từ xa qua điện thoại -> hủy đặt phòng qua điện thoại.
*   Quản lý có thể sử dụng các chức năng sau:
    *   Quản lý thông tin khách sạn -> quản lý khách sạn
    *   Quản lý thông tin phòng -> quản lý phòng
    *   Xem báo cáo thống kê -> xem báo cáo
*   Quản trị viên hệ thống có thể sử dụng các chức năng sau:
    *   Quản lý tài khoản người dùng theo yêu cầu -> quản lý tài khoản.
*   Đặt phòng là cùng một chức năng dù là tại chỗ (với lễ tân) hay từ xa (với nhân viên bán hàng). Vì vậy, ta có thể đề xuất một use case trừu tượng: đặt phòng
*   Tương tự với hủy đặt phòng, ta đề xuất một use case trừu tượng: hủy đặt phòng.

Biểu đồ use case tổng quát thu được là:

![](https://softwaredesign.home.blog/wp-content/uploads/2020/11/image.png?w=569)

Các use case được mô tả như sau:

*   Quản lý khách sạn: use case này cho phép quản lý thông tin về khách sạn.
*   Quản lý phòng: use case này cho phép quản lý thông tin về các phòng.
*   Xem báo cáo: use case này cho phép quản lý xem báo cáo thống kê về khách sạn, phòng, khách hàng, dịch vụ hoặc doanh thu của khách sạn.
*   Quản lý tài khoản: use case này cho phép quản trị viên quản lý tài khoản người dùng theo yêu cầu của người liên quan.
*   Đặt phòng: use case này cho phép nhân viên bán hàng hoặc lễ tân đặt phòng cho một khách hàng.
*   Đặt phòng tại chỗ: use case này cho phép lễ tân đặt phòng cho một khách hàng tại chỗ.
*   Đặt phòng qua điện thoại: use case này cho phép nhân viên bán hàng đặt phòng cho một khách hàng từ xa qua điện thoại.
*   Hủy đặt phòng: use case này cho phép nhân viên bán hàng hoặc lễ tân hủy đặt phòng cho một khách hàng.
*   Hủy đặt phòng tại chỗ: use case này cho phép lễ tân hủy đặt phòng cho một khách hàng tại chỗ.
*   Hủy đặt phòng qua điện thoại: use case này cho phép nhân viên bán hàng hủy đặt phòng cho một khách hàng từ xa qua điện thoại.
*   Nhận phòng: use case này cho phép lễ tân làm thủ tục nhận phòng cho khách hàng tại quầy lễ tân.
*   Trả phòng: use case này cho phép lễ tân làm thủ tục trả phòng và xử lý thanh toán cho khách hàng.

_**Use case sửa phòng (quản lý phòng)**_

Hãy xem lại mô tả của chức năng này: Quản lý đăng nhập vào hệ thống -> Giao diện của quản lý xuất hiện, có các lựa chọn sau: quản lý thông tin khách sạn, quản lý thông tin phòng và xem báo cáo thống kê -> Quản lý chọn quản lý thông tin phòng -> Giao diện quản lý thông tin phòng xuất hiện với ba lựa chọn: thêm phòng, sửa phòng, xóa phòng -> Quản lý nhấp vào chức năng sửa -> Giao diện tìm kiếm phòng xuất hiện với một ô nhập liệu để nhập từ khóa và một nút tìm kiếm -> Quản lý nhập từ khóa về tên phòng cần sửa rồi nhấn nút tìm kiếm -> Danh sách tất cả các phòng có tên chứa từ khóa đã nhập xuất hiện, mỗi dòng tương ứng với một phòng gồm: mã, tên, loại, giá, mô tả -> Quản lý nhấp vào phòng cần sửa -> Giao diện sửa phòng xuất hiện với các ô nhập liệu có thể chỉnh sửa, đã chứa sẵn các giá trị thuộc tính tương ứng, ngoại trừ mã phòng không thể chỉnh sửa -> Quản lý thay đổi một số giá trị thuộc tính rồi nhấn nút lưu -> Hệ thống thông báo thành công và sau đó quay về giao diện chính của quản lý.

Vì vậy, chúng ta cần các giao diện với những tương tác liên quan sau: Đăng nhập. Quản lý phòng có ba chức năng con: thêm phòng, sửa phòng và xóa phòng. Sửa phòng còn có thêm một hành động nữa: tìm kiếm phòng để sửa. Vì vậy ta có use case như sau:

![](https://softwaredesign.home.blog/wp-content/uploads/2020/11/image-1.png?w=454)

Mô tả use case:

*   Thêm phòng: use case này cho phép quản lý thêm một phòng mới vào hệ thống.
*   Sửa phòng: use case này cho phép quản lý chỉnh sửa một phòng trong hệ thống.
*   Xóa phòng: use case này cho phép quản lý xóa một phòng khỏi hệ thống.
*   Tìm kiếm phòng: use case này cho phép quản lý tìm kiếm một phòng (theo mã hoặc theo tên) để sửa hoặc xóa.

_**Use case đặt phòng**_

Hãy xem lại mô tả của chức năng này: Khách hàng gọi điện đến khách sạn để đặt phòng -> Lễ tân chuyển cuộc gọi cho nhân viên bán hàng -> Nhân viên bán hàng hỏi khách hàng về khoảng thời gian khách hàng muốn lưu trú tại khách sạn và chọn chức năng đặt phòng trên giao diện chính của nhân viên bán hàng -> Giao diện tìm kiếm phòng trống xuất hiện với hai ô nhập ngày: ngày nhận phòng và ngày trả phòng, cùng một nút tìm kiếm -> Nhân viên bán hàng nhập ngày nhận phòng và ngày trả phòng theo yêu cầu của khách hàng rồi nhấn nút tìm kiếm -> Danh sách tất cả các phòng trống trong khoảng thời gian đó được liệt kê dưới dạng bảng, mỗi dòng tương ứng với một phòng gồm: mã, tên, loại, giá, mô tả -> Nhân viên bán hàng thông báo cho khách hàng tất cả các loại phòng trống và yêu cầu khách hàng chọn một phòng hoặc nhiều phòng -> Khách hàng thông báo lựa chọn của mình -> Nhân viên bán hàng nhấp vào phòng đáp ứng yêu cầu của khách hàng -> Hệ thống chuyển sang giao diện thông tin khách hàng với một ô nhập liệu và một nút tìm kiếm -> Nhân viên bán hàng yêu cầu khách hàng cung cấp thông tin về: số chứng minh nhân dân, tên, địa chỉ, số điện thoại rồi nhập tên khách hàng vào ô nhập liệu và nhấn nút tìm kiếm -> Danh sách tất cả khách hàng có tên chứa từ khóa đã nhập xuất hiện dưới dạng bảng, mỗi dòng tương ứng với một khách hàng gồm: số chứng minh nhân dân, tên, địa chỉ, số điện thoại, ghi chú -> Nhân viên bán hàng nhấp vào dòng có thông tin trùng khớp với khách hàng hiện tại. Nếu không có dòng nào phù hợp, phải nhấn nút thêm khách hàng mới để thêm khách hàng mới -> Hệ thống hiển thị giao diện xác nhận với: thông tin khách hàng, thông tin phòng đã chọn, ngày nhận phòng và ngày trả phòng -> Nhân viên bán hàng xác nhận các thông tin này với khách hàng và khách hàng xác nhận đồng ý -> Nhân viên bán hàng nhấn nút xác nhận -> Hệ thống thông báo thành công và sau đó quay về giao diện chính của nhân viên bán hàng -> Nhân viên bán hàng thông báo việc đặt phòng thành công cho khách hàng và kết thúc cuộc gọi.

Vì vậy, để đặt phòng, nhân viên bán hàng/lễ tân phải: đăng nhập vào hệ thống, tìm các phòng còn trống, tìm khách hàng để kiểm tra xem họ đã tồn tại trong hệ thống hay chưa (thêm khách hàng mới nếu chưa có), và xác nhận đặt phòng (việc này có thể tách thành một use case con hoặc tích hợp vào use case đặt phòng). Vì vậy ta có use case như sau:

![](https://softwaredesign.home.blog/wp-content/uploads/2020/11/image-2.png?w=625)

Mô tả use case:

*   Tìm phòng trống: use case này cho phép nhân viên bán hàng/lễ tân tìm các phòng còn trống để đặt cho khách hàng.
*   Tìm khách hàng: use case này cho phép nhân viên bán hàng/lễ tân tìm thông tin khách hàng để đặt phòng. Việc tìm kiếm có thể dựa trên tên khách hàng hoặc số điện thoại.
*   Thêm khách hàng: use case này cho phép nhân viên bán hàng/lễ tân thêm một khách hàng mới trong quá trình đặt phòng cho khách hàng.

_**Use case xem báo cáo phòng theo doanh thu**_

Hãy xem lại mô tả của chức năng này: Quản lý đăng nhập vào hệ thống -> Giao diện của quản lý xuất hiện, có các lựa chọn sau: quản lý thông tin khách sạn, quản lý thông tin phòng và xem báo cáo thống kê -> Quản lý chọn xem báo cáo thống kê -> Giao diện cấu hình báo cáo xuất hiện với hai danh sách lựa chọn. Thứ nhất là đối tượng của báo cáo, bao gồm: khách sạn, phòng, khách hàng, dịch vụ, doanh thu. Thứ hai là tiêu chí của báo cáo, bao gồm: theo doanh thu, theo thời gian nếu đối tượng là doanh thu, theo tỷ lệ lấp đầy nếu đối tượng là khách sạn hoặc phòng -> Quản lý chọn đối tượng là phòng, tiêu chí là theo doanh thu -> Giao diện báo cáo xuất hiện với hai ô nhập ngày: ngày bắt đầu và ngày kết thúc của khoảng thời gian cần thống kê -> Quản lý nhập các ngày này rồi nhấn nút xem -> Kết quả báo cáo được liệt kê dưới dạng bảng, sắp xếp theo tổng doanh thu, mỗi dòng tương ứng với một phòng gồm: mã, tên, loại, tổng số ngày lấp đầy, tổng doanh thu -> Quản lý nhấp vào một dòng để xem chi tiết -> Giao diện xuất hiện với thông tin phòng đã chọn và danh sách đặt phòng liên quan đến phòng đó, mỗi dòng tương ứng với một lần đặt phòng được sắp theo thứ tự thời gian nhận phòng: tên khách hàng, ngày nhận phòng, ngày trả phòng, giá, tổng thu nhập -> Quản lý nhấn nút quay lại để trở về giao diện chính của quản lý.

Vì vậy, trong use case này, quản lý phải đăng nhập, cấu hình báo cáo (được tích hợp trong chức năng xem báo cáo), chọn khoảng thời gian thống kê (có thể tích hợp trong phần xem báo cáo chính), và xem báo cáo chính. Quản lý cũng có thể chọn xem báo cáo chi tiết của một phòng (tùy chọn). Use case thu được như sau:

![](https://softwaredesign.home.blog/wp-content/uploads/2020/11/image-3.png?w=433)

Mô tả use case:

*   Xem báo cáo: use case này cho phép quản lý xem tất cả các loại báo cáo.
*   Xem báo cáo phòng: use case này cho phép quản lý xem báo cáo chính về phòng theo doanh thu.
*   Xem chi tiết một phòng: use case này cho phép quản lý xem các lần đặt phòng liên quan đến một phòng trong khoảng thời gian thống kê.