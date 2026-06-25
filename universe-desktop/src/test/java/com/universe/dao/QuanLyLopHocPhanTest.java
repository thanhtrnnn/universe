package com.universe.dao;

import com.universe.entity.ClassSection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * STT 5 - Chức năng: Quản lý lớp học phần.
 * Trường hợp kiểm thử: "Sửa thông tin sức chứa của lớp hợp lệ".
 *
 * Đối tượng test: {@link ClassSectionDAO#updateClassSection(ClassSection)}.
 * Trường sức chứa là 'maxStudents' (tham số thứ 5 trong câu UPDATE).
 */
class QuanLyLopHocPhanTest {

    @Test
    @DisplayName("Sửa sức chứa lớp học phần hợp lệ -> cập nhật thành công")
    void suaSucChuaLop_hopLe() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        ClassSectionDAO dao = spy(new ClassSectionDAO());
        doReturn(con).when(dao).getConnection();

        ClassSection lop = new ClassSection(
                "CS01", "INT1340.01", "OOP", "HK1", "2025-2026",
                60,            // sức chứa mới
                "open", "INT1340", "GV01");

        boolean ketQua = dao.updateClassSection(lop);

        assertTrue(ketQua, "Sửa sức chứa hợp lệ phải trả về true");
        verify(con).prepareStatement(contains("UPDATE tblClassSection"));
        verify(ps).setInt(5, 60);   // maxStudents
    }
}
