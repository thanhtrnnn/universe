# Mục lục – Tài liệu từng Module

Mỗi module cần sinh đủ 11 mục theo 4 pha. Kết quả pha trước là đầu vào bắt buộc cho pha sau.

---

## Quy tắc style chung cho MỌI biểu đồ (BẮT BUỘC)

Áp dụng cho toàn bộ biểu đồ trong tài liệu module: UC, lớp, tuần tự, ERD.

1. **Dàn bố cục trên nhiều dòng:** Khi có nhiều package / module / nhóm, xếp theo chiều dọc (top-to-bottom) hoặc dạng lưới. KHÔNG đặt tất cả trên một hàng ngang. Các module phải được xác định trong plan từ đầu.

2. **Không viết tắt tên, giữ mã UC xuyên suốt:**
   - Tên hiển thị trong biểu đồ PHẢI đầy đủ, KHÔNG viết tắt.
   - Mã UC (UC01, UC02...) từ biểu đồ tổng quan hệ thống PHẢI được giữ nguyên khi mang sang biểu đồ chi tiết module. Không đổi sang alias khác (VD: không dùng `UC_main`, `UC_sub1` thay cho `UC01`, `UC02`).

3. **Mũi tên thẳng, phần tử dàn đều:**
   - Tất cả mũi tên PHẢI thẳng (`-->`, `.<...>>`), KHÔNG dùng mũi tên chéo.
   - Dàn đều các phần tử trong mỗi package / nhóm, không để chồng chéo hoặc quá gần nhau.
   - Dùng `top to right direction` hoặc `left to right direction` để điều hướng mũi tên.

---

## Pha I – Requirements

| Mục | File | Nội dung chính |
|-----|------|----------------|
| I.1 | [i.1_mohinh_nghiepvu.md](i.1_mohinh_nghiepvu.md) | Biểu đồ Use Case chi tiết + mô tả UC |

## Pha II – Analysis

| Mục | File | Nội dung chính |
|-----|------|----------------|
| II.1 | [ii.1_mohinh_hoa_chucnang.md](ii.1_mohinh_hoa_chucnang.md) | Kịch bản chuẩn + ngoại lệ cho từng UC |
| II.2 | [ii.2_mohinh_hoa_lop.md](ii.2_mohinh_hoa_lop.md) | Trích xuất lớp thực thể (noun extraction) |
| II.3 | [ii.3_sodo_lop_phantich.md](ii.3_sodo_lop_phantich.md) | Sơ đồ lớp phân tích (BCE) |
| II.4 | [ii.4_tuantu_phantich.md](ii.4_tuantu_phantich.md) | Biểu đồ tuần tự phân tích |

## Pha III – Design

| Mục | File | Nội dung chính |
|-----|------|----------------|
| III.1 | [iii.1_thietke_lop_thucthe.md](iii.1_thietke_lop_thucthe.md) | Thiết kế lớp thực thể (kiểu dữ liệu, PK) |
| III.2 | [iii.2_thietke_coso_dulieu.md](iii.2_thietke_coso_dulieu.md) | ERD + thiết kế CSDL |
| III.3.1 | [iii.3.1_thietke_giaodien.md](iii.3.1_thietke_giaodien.md) | Wireframe ASCII |
| III.3.2 | [iii.3.2_sodo_lop_thietke.md](iii.3.2_sodo_lop_thietke.md) | Sơ đồ lớp thiết kế (DAO + Boundary + Entity) |
| III.4 | [iii.4_tuantu_thietke.md](iii.4_tuantu_thietke.md) | Biểu đồ tuần tự thiết kế |

## Pha IV – Test

| Mục | File | Nội dung chính |
|-----|------|----------------|
| IV | [iv_kiemthu.md](iv_kiemthu.md) | Test Plan + Test Case hộp đen |

---

## Quy trình thực hiện

```
Pha I → Pha II → Pha III → Pha IV
  ↓         ↓         ↓         ↓
  UC      Phân tích  Thiết kế  Kiểm thử
```

Sau mỗi pha, hỏi: *"Pha [X] đã hoàn thành. Bạn có muốn điều chỉnh gì không trước khi sang pha tiếp theo?"*
