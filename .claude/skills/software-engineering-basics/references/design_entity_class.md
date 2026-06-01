# Thiết kế các lớp thực thể -- Phần mềm: Nghệ thuật Thiết kế và Lập trình
Đầu vào của bước này là biểu đồ lớp thực thể phân tích. Chúng ta cần xử lý theo các bước sau:

*   **_Bước 1_**: Thêm thuộc tính id cho các lớp KHÔNG kế thừa từ lớp khác: Hotel, Room, Client, Bill, Booking, BookedRoom, User, Service, UsedService.
*   **_Bước 2_**: Thêm kiểu dữ liệu cho mỗi thuộc tính trong tất cả các lớp
*   **_Bước 3_**: Chuyển đổi tất cả các mối quan hệ liên kết thành các mối quan hệ kết tập/hợp thành tương ứng:
    *   Room + Booking -> BookedRoom được chuyển đổi thành: Room là thành phần của BookedRoom, BookedRoom là thành phần của Booking.
    *   BookedRoom + Service -> UsedService được chuyển đổi thành: Service là thành phần của UsedService, UsedService là thành phần của BookedRoom.
*   **_Bước 4_**: Thêm các thuộc tính đối tượng tương ứng với các mối quan hệ kết tập/hợp thành:
    *   Room là thành phần của Hotel, kiểu n-1 -> Hotel có một danh sách Room
    *   Room là thành phần của BookedRoom, kiểu 1-n -> BookedRoom có một Room
    *   BookedRoom là thành phần của Booking, kiểu n-1 -> Booking có một danh sách BookedRoom
    *   Client là thành phần của Booking, kiểu 1-n -> Booking có một Client
    *   User là thành phần của Booking, kiểu 1-n -> Booking có một User
    *   User là thành phần của Bill, kiểu 1-n -> Bill có một User
    *   Service là thành phần của UsedService, kiểu 1-n -> UsedService có một Service
    *   UsedService là thành phần của BookedRoom, kiểu n-1 -> BookedRoom có một danh sách UsedService

![](https://softwaredesign.home.blog/wp-content/uploads/2020/12/image-8.png?w=776)

![](https://softwaredesign.home.blog/wp-content/uploads/2020/12/image-7.png?w=776)