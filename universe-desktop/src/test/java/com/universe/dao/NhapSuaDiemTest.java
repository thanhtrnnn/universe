package com.universe.dao;

import com.universe.entity.CourseRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * STT 10 - Chức năng: Nhập / sửa điểm.
 * Trường hợp kiểm thử: "Nhập điểm trong khoảng 0-10 hợp lệ".
 *
 * Đối tượng test: {@link CourseRecordDAO#enterGrade(List)} (nhập điểm hàng loạt).
 * Các điểm hợp lệ (0..10) được ghi xuống DB qua batch update.
 */
class NhapSuaDiemTest {

    @Test
    @DisplayName("Nhập điểm trong khoảng 0-10 hợp lệ -> lưu thành công")
    void nhapDiemHopLe_thanhCong() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);

        CourseRecordDAO dao = spy(new CourseRecordDAO());
        doReturn(con).when(dao).getConnection();

        CourseRecord r = new CourseRecord();
        r.setId("CR1");
        r.setScore1(8.0);
        r.setScore2(7.5);
        r.setScore3(9.0);
        r.setExamScore(8.5);

        // Tiền điều kiện của trường hợp test: mọi điểm đều nằm trong [0, 10].
        for (Double diem : List.of(r.getScore1(), r.getScore2(), r.getScore3(), r.getExamScore())) {
            assertTrue(diem >= 0 && diem <= 10, "Điểm phải trong khoảng 0-10");
        }

        boolean ketQua = dao.enterGrade(List.of(r));

        assertTrue(ketQua, "Nhập điểm hợp lệ phải trả về true");
        verify(con).prepareStatement(contains("UPDATE tblCourseRecord"));
        verify(ps).setDouble(1, 8.0);   // score1
        verify(ps).setDouble(4, 8.5);   // examScore
        verify(ps).addBatch();
        verify(ps).executeBatch();
        verify(con).commit();
    }
}
