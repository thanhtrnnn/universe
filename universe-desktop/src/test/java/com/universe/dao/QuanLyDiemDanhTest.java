package com.universe.dao;

import com.universe.entity.Attendance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * STT 8 - Chức năng: Quản lý điểm danh.
 * Trường hợp kiểm thử: "Điểm danh và lưu trạng thái thành công".
 *
 * Đối tượng test: {@link AttendanceDAO#markManualAttendance(Attendance)}.
 * Khi chưa có bản ghi điểm danh của SV trong buổi -> chèn mới (method='MANUAL').
 */
class QuanLyDiemDanhTest {

    @Test
    @DisplayName("Điểm danh thủ công SV chưa có bản ghi -> chèn mới, lưu trạng thái")
    void diemDanhVaLuuTrangThai_thanhCong() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);    // chưa có bản ghi -> sẽ INSERT
        when(ps.executeUpdate()).thenReturn(1);

        AttendanceDAO dao = spy(new AttendanceDAO());
        doReturn(con).when(dao).getConnection();

        Attendance dd = new Attendance();
        dd.setClassSessionId("SESS01");
        dd.setStudentId("SV001");
        dd.setStatus("PRESENT");

        boolean ketQua = dao.markManualAttendance(dd);

        assertTrue(ketQua, "Điểm danh và lưu trạng thái phải trả về true");
        verify(con).prepareStatement(contains("INSERT INTO tblAttendance"));
        verify(ps).executeUpdate();
    }
}
