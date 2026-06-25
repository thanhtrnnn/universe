package com.universe.dao;

import com.universe.entity.CourseRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * STT 6 - Chức năng: Đăng ký học phần.
 * Trường hợp kiểm thử: "Đăng ký môn học khi còn chỗ trống".
 *
 * Đối tượng test:
 *  - {@link ClassSectionDAO#checkCapacity(String)}  (điều kiện: lớp còn chỗ)
 *  - {@link CourseRecordDAO#confirmRegistration(CourseRecord)} (ghi nhận đăng ký)
 */
class DangKyHocPhanTest {

    @Test
    @DisplayName("Lớp còn chỗ (đã ghi danh < sức chứa) -> checkCapacity = true")
    void lopConChoTrong() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("enrolled")).thenReturn(20);     // đã đăng ký 20
        when(rs.getInt("maxStudents")).thenReturn(40);  // sức chứa 40

        ClassSectionDAO dao = spy(new ClassSectionDAO());
        doReturn(con).when(dao).getConnection();

        assertTrue(dao.checkCapacity("CS01"), "20 < 40 -> lớp còn chỗ trống");
    }

    @Test
    @DisplayName("Đăng ký khi chưa đăng ký trùng và còn chỗ -> ghi nhận thành công")
    void dangKyKhiConChoTrong_thanhCong() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);     // exists() = false -> chưa đăng ký lớp này
        when(ps.executeUpdate()).thenReturn(1);

        CourseRecordDAO dao = spy(new CourseRecordDAO());
        doReturn(con).when(dao).getConnection();

        CourseRecord dangKy = new CourseRecord();
        dangKy.setStudentId("SV001");
        dangKy.setClassSectionId("CS01");
        dangKy.setEnrolledAt(LocalDate.now());

        boolean ketQua = dao.confirmRegistration(dangKy);

        assertTrue(ketQua, "Đăng ký khi còn chỗ và chưa trùng phải trả về true");
        verify(con).prepareStatement(contains("INSERT INTO tblCourseRecord"));
        verify(ps).executeUpdate();
    }
}
