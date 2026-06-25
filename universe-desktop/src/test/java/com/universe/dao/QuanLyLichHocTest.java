package com.universe.dao;

import com.universe.entity.Schedule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * STT 3 - Chức năng: Quản lý lịch học.
 * Trường hợp kiểm thử: "Sửa phòng lịch học hợp lệ".
 *
 * Đối tượng test: {@link ScheduleDAO#updateSchedule(Schedule)}.
 */
class QuanLyLichHocTest {

    @Test
    @DisplayName("Sửa phòng của lịch học hợp lệ -> cập nhật thành công")
    void suaPhongLichHoc_hopLe() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        ScheduleDAO dao = spy(new ScheduleDAO());
        doReturn(con).when(dao).getConnection();

        Schedule lich = new Schedule();
        lich.setId("SCH01");
        lich.setDayOfWeek("Thứ 2");
        lich.setStartPeriod(1);
        lich.setEndPeriod(3);
        lich.setRoom("B1.404");   // phòng mới hợp lệ

        boolean ketQua = dao.updateSchedule(lich);

        assertTrue(ketQua, "Sửa phòng lịch học hợp lệ phải trả về true");
        verify(con).prepareStatement(contains("UPDATE tblSchedule"));
        verify(ps).setString(4, "B1.404");   // tham số thứ 4 là 'room'
    }
}
