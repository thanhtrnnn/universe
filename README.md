# UniVerse – Hệ sinh thái Đại học số

Đồ án **Thực tập cơ sở** – Học viện Công nghệ Bưu chính Viễn thông (Nhóm HTH).
UniVerse là hệ thống quản lý đại học số, giải quyết sự phân mảnh trong quản lý đào tạo
qua nền tảng kết nối **Sinh viên – Giảng viên – Nhà trường**. Dự án gồm hai phần:

**(1) tài liệu phân tích–thiết kế** theo quy trình Unified Process và

**(2) ứng dụng Java Desktop** hiện thực hóa toàn bộ thiết kế đó.

---

## 1. Tài liệu (Báo cáo CNPM theo Unified Process)

Tài liệu được xây dựng theo quy trình **Unified Process (UP)** – lặp & tăng trưởng,
áp dụng triệt để phân tích–thiết kế hướng đối tượng (OOAD). Nằm trong thư mục
[`report/`](report/) (nhánh `report`).

**Cấu trúc 6 chương:**

1. Mở đầu – bối cảnh, khảo sát hiện trạng, thiết kế tương tác.
2. Phương pháp & công nghệ – Unified Process, kiến trúc MVC (Desktop) + Client–Server (Mobile).
3. Pha phân tích – mô hình hóa chức năng (use case), mô hình hóa lớp (thực thể/BCE), biểu đồ tuần tự phân tích.
4. Pha thiết kế – lớp thực thể, thiết kế CSDL, biểu đồ lớp MVC & biểu đồ tuần tự thiết kế cho từng chức năng.
5. Pha cài đặt & kiểm thử – tổ chức mã nguồn, kiểm thử hộp đen (black-box) với trạng thái CSDL trước/sau.
6. Kết luận.

**3 phân hệ – 10 chức năng nghiệp vụ:**

| Phân hệ                             | Chức năng                                                                         |
| ------------------------------------- | ----------------------------------------------------------------------------------- |
| Quản lý người dùng & thông báo | Đăng nhập/sửa thông tin người dùng; Gửi thông báo                        |
| Quản lý học vụ                    | Sửa lịch học; Thêm học phần; Sửa lớp học phần; Đăng ký lớp học phần |
| Điểm danh & điểm số              | Tạo/tắt mã QR; Quản lý điểm danh; Xem điểm; Nhập/sửa điểm              |

**Sản phẩm UML:** biểu đồ use case, biểu đồ lớp (thực thể / BCE phân tích / MVC thiết kế),
biểu đồ tuần tự (phân tích + thiết kế), ERD, biểu đồ thiết kế CSDL – xuất ra
[`report/diagrams/`](report/diagrams/). Báo cáo tổng hợp: `report/baocao_NEW.docx`.

---

## 2. Ứng dụng Java Desktop ([`universe-desktop/`](universe-desktop/))

Hiện thực hóa toàn bộ scenario thiết kế động và biểu đồ lớp MVC của báo cáo thành
ứng dụng desktop cho Quản trị viên, Giảng viên và Sinh viên.

**Kiến trúc – MVC mở rộng với DAO:**

| Tầng          | Vai trò                                                       | Công nghệ       |
| -------------- | -------------------------------------------------------------- | ----------------- |
| Model (Entity) | Lớp thực thể nghiệp vụ                                    | POJO              |
| View (Frm)     | Màn hình kế thừa `JFrame`, lắng nghe `ActionListener` | Java Swing        |
| Controller/DAO | Truy cập CSDL qua JDBC, thực thi SQL                         | JDBC + PostgreSQL |

Hạ tầng tích hợp thêm: **Redis** (lưu token phiên đăng nhập) và **Kafka** (luồng gửi
thông báo: publish → consumer ghi `tblNotification`). Mã QR điểm danh sinh bằng **ZXing**.

**Quy mô:** 12 lớp thực thể · 9 DAO · 19 màn hình JFrame · 12 bảng PostgreSQL · 10 chức năng.

**Chạy thử:**

```bash
cd universe-desktop
docker compose up -d          # PostgreSQL + Redis + Kafka + Zookeeper (nạp sẵn schema + seed)
mvn compile
mvn exec:java -Dexec.mainClass="com.universe.Main"
```

**Tài khoản mẫu** (mật khẩu `123456`): `admin` (Quản trị viên), `dothilien` (Giảng viên),
`b23dcat120` (Sinh viên). Chi tiết: [`universe-desktop/README.md`](universe-desktop/README.md).

---

## 3. Cấu trúc kho mã

```
universe/
├── report/             # Tài liệu báo cáo CNPM (UP): docx, markdown, biểu đồ  (nhánh report)
├── universe-desktop/   # Ứng dụng Java Swing + JDBC + Redis + Kafka          (nhánh main)
├── apps/               # (Tham khảo) phác thảo hệ sinh thái web/mobile/backend
└── docs/               # Tài liệu bổ trợ
```

> **Nhánh:** mã nguồn Java nằm ở nhánh `main` (`universe-desktop/`); tài liệu báo cáo
> được phát triển ở nhánh `report` (`report/`).
