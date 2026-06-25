package com.universe.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * STT 2 - Chức năng: Gửi thông báo.
 * Trường hợp kiểm thử: "Gửi thông báo đến lớp học phần đã tồn tại".
 *
 * Đối tượng test: {@link NotificationDAO#sendToRecipients(String, String, String, List)}.
 * Mỗi sinh viên trong lớp nhận 1 bản ghi thông báo; danh sách người nhận trùng
 * phải được khử trùng. Kafka bị tắt trong test (app.properties test) nên chỉ
 * ghi DB qua JDBC mock.
 */
class GuiThongBaoTest {

    @Test
    @DisplayName("Gửi thông báo tới lớp có 2 sinh viên (1 trùng) -> ghi 2 bản ghi")
    void guiThongBaoDenLop_ghiChoTungSinhVien() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);

        NotificationDAO dao = spy(new NotificationDAO());
        doReturn(con).when(dao).getConnection();

        // Lớp học phần CS01 có SV001, SV002 (SV002 lặp lại -> khử trùng còn 2).
        int soNguoiNhan = dao.sendToRecipients(
                "Nghỉ học",
                "Buổi học thứ 2 được nghỉ.",
                "class:CS01",
                List.of("SV001", "SV002", "SV002"));

        assertEquals(2, soNguoiNhan, "Phải gửi cho đúng 2 sinh viên (đã khử trùng)");
        verify(ps, times(2)).addBatch();
        verify(ps).executeBatch();
        verify(con).commit();
    }
}
