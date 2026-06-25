package com.universe.dao;

import com.universe.entity.CourseRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * STT 9 - Chức năng: Xem điểm.
 * Trường hợp kiểm thử: "Xem điểm khi giảng viên đã nhập".
 *
 * Đối tượng test: {@link CourseRecordDAO#viewGrade(String)}.
 * Mô phỏng 1 bản ghi đã có điểm (giảng viên đã nhập) và kiểm tra DAO
 * ánh xạ đúng điểm + tên lớp học phần.
 */
class XemDiemTest {

    @Test
    @DisplayName("Xem điểm khi giảng viên đã nhập -> trả về bản ghi kèm điểm")
    void xemDiemKhiDaNhap() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);   // đúng 1 bản ghi

        when(rs.getString("id")).thenReturn("CR1");
        when(rs.getString("status")).thenReturn("Đang học");
        when(rs.getString("tblStudentid")).thenReturn("SV001");
        when(rs.getString("tblClassSectionid")).thenReturn("CS01");
        when(rs.getString("csCode")).thenReturn("INT1340.01");
        when(rs.getString("csName")).thenReturn("Lập trình hướng đối tượng");
        when(rs.getDate("enrolledAt")).thenReturn(null);
        // Giảng viên đã nhập điểm -> các cột điểm có giá trị, không NULL.
        when(rs.getDouble(anyString())).thenReturn(8.0);
        when(rs.wasNull()).thenReturn(false);

        CourseRecordDAO dao = spy(new CourseRecordDAO());
        doReturn(con).when(dao).getConnection();

        List<CourseRecord> ketQua = dao.viewGrade("SV001");

        assertEquals(1, ketQua.size(), "Phải trả về 1 bản ghi điểm");
        CourseRecord r = ketQua.get(0);
        assertEquals(8.0, r.getScore1(), "Điểm thành phần phải được đọc đúng");
        assertEquals(8.0, r.getExamScore(), "Điểm thi phải được đọc đúng");
        assertEquals("INT1340.01 - Lập trình hướng đối tượng", r.getClassSectionName());
    }
}
