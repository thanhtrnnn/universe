# Thiết kế cơ sở dữ liệu – Phần mềm: Nghệ thuật Thiết kế và Lập trình
**  
Các bước trong quy trình**

Lấy sơ đồ lớp thực thể ở giai đoạn thiết kế làm đầu vào, sau đó xử lý qua năm bước sau:

*   Bước 1: Với mỗi lớp thực thể, tạo một bảng tương ứng. Tên bảng nên có tiền tố tbl + tên của lớp thực thể. Ví dụ, từ lớp thực thể Room, bảng tương ứng sẽ là tblRoom.
*   Bước 2: Với mỗi lớp thực thể, chuyển tất cả các thuộc tính KHÔNG PHẢI ĐỐI TƯỢNG thành các cột của bảng tương ứng. Ví dụ, trong lớp Hotel, thuộc tính listRoom có kiểu Room\[\] là thuộc tính ĐỐI TƯỢNG, nên sẽ bị loại bỏ, các thuộc tính còn lại là id, name, starLevel, address, description sẽ đóng vai trò là các cột của tblHotel.
*   Bước 3: Xem xét quan hệ số lượng giữa các lớp thực thể. Các quan hệ này sẽ là quan hệ giữa các bảng tương ứng:
    *   Nếu quan hệ là 1-1 thì hai bảng liên quan nên được gộp lại thành một. Tuy nhiên, trong một số trường hợp đặc biệt, hai bảng liên quan có thể được giữ riêng biệt.
    *   Nếu quan hệ là 1-n thì hai bảng liên quan nên được tách riêng.
    *   Nếu quan hệ là n-n thì cần tạo một số bảng trung gian để tách quan hệ n-n thành hai hoặc nhiều quan hệ kiểu 1-n. Nên quay lại giai đoạn phân tích để chỉnh sửa quan hệ này.
*   Bước 4: Cấu hình các cột khóa cho các bảng:
    *   Khóa chính PK: Bảng nào có thuộc tính id thì thiết lập thuộc tính đó làm khóa chính cho bảng.
    *   Khóa ngoại FK: Nếu hai bảng tblA và tblB có quan hệ 1-n, tức 1 tblA có n tblB, thì trong bảng tblB phải có khóa ngoại tham chiếu đến khóa chính của bảng tblA. Khóa ngoại có thể đặt tên là aId hoặc idA.
*   Bước 5: Loại bỏ các thuộc tính gây dư thừa dữ liệu. Có hai loại thuộc tính gây dư thừa dữ liệu:
    *   Thuộc tính bị trùng lặp: thường là thuộc tính của cùng một đối tượng, không phải thuộc tính khóa, nhưng xuất hiện ở hai bảng khác nhau.
    *   Thuộc tính dẫn xuất: là thuộc tính có thể tính toán cơ học từ các thuộc tính khác trong CSDL. Ví dụ một số thuộc tính xuất thân từ các lớp thực thể thống kê thì thường là thuộc tính dẫn xuất. Sau khi loại bỏ các thuộc tính dư thừa, nếu xuất hiện bảng nào không còn thuộc tính nào nữa ngoài 1 khóa ngoại thì có thể loại bỏ luôn bảng đấy.

##### Áp dụng

Đầu vào là sơ đồ lớp thực thể từ giai đoạn thiết kế:

![](https://softwaredesign.home.blog/wp-content/uploads/2020/12/image-12.png?w=776)

_**Bước 1**_

Mỗi lớp thực thể đề xuất bảng tương ứng:

*   Lớp Truong -> bảng tblTruong
*   Lớp Khoa -> bảng tblKhoa
*   Lớp Toanha -> bảng tblToanha
*   ….

_**Bước 2**_

Đưa thuộc tính không phải đối tượng của lớp thực thể thành thuộc tính của bảng tương ứng:

*   tblTruong có các thuộc tính: id, tên, mô tả
*   tblKhoa: id, tên, mô tả
*   tblToanha: id, tên, mô tả
*   ….

_**Bước 3**_

Chuyển quan hệ số lượng giữa các lớp thực thể thành quan hệ số lượng giữa các bảng:

*   1 tblTruong – n tblKhoa
*   1 tblTruong – n tblToanha
*   1 tblToanha – n tblPhonghoc
*   ….

_**Bước 4**_

Bổ sung các thuộc tính khóa. Khóa chính được thiết lập với thuộc tính id của các bảng tương ứng: trừ các bảng tblTK, tblSinhvien, tblThanhvien, tblGiangvien, tblNVQuanli, tblNVKhaothi, tblGiaovu.

Khóa ngoại được thiết lập cho các bảng:

*   1 tblTruong – n tblKhoa -> bảng tblKhoa có khóa ngoại tblTruongid
*   1 tblTruong – n tblToanha -> bảng tblToanha có khóa ngoại tblTruongid
*   1 tblToanha – n tblPhonghoc -> bảng tblPhonghoc có khóa ngoại tblToanhaid
*   ….

_**Bước 5**_

Các thuộc tính dẫn xuất:

*   điểm TBM, điểm TB chữ trong bảng tblDangkihoc
*   Các thuộc tính của các lớp thống kê -> loại bỏ hết các bảng thống kê.

Kết quả thu được CSDL toàn hệ thống.

![](https://softwaredesign.home.blog/wp-content/uploads/2020/12/image-11.png?w=756)
