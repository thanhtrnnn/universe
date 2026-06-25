package com.universe.dao;

import com.universe.entity.Course;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * STT 4 - Chức năng: Quản lý học phần.
 * Trường hợp kiểm thử: "Thêm học phần mới thành công".
 *
 * Đối tượng test: {@link CourseDAO#createCourse(Course)}.
 */
class QuanLyHocPhanTest {

    @Test
    @DisplayName("Thêm học phần mới -> tạo thành công")
    void themHocPhanMoi_thanhCong() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        CourseDAO dao = spy(new CourseDAO());
        doReturn(con).when(dao).getConnection();

        Course hp = new Course("INT1340", 3, "Lập trình hướng đối tượng", "CNTT");

        boolean ketQua = dao.createCourse(hp);

        assertTrue(ketQua, "Thêm học phần mới phải trả về true");
        verify(con).prepareStatement(contains("INSERT INTO tblCourse"));
        verify(ps).setString(1, "INT1340");   // mã học phần
        verify(ps).setInt(2, 3);               // số tín chỉ
    }
}
