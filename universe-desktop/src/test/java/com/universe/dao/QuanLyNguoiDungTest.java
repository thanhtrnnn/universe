package com.universe.dao;

import com.universe.entity.Student;
import com.universe.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * STT 1 - Chức năng: Quản lý người dùng.
 * Trường hợp kiểm thử: "Sửa một người dùng đã có trong CSDL".
 *
 * Đối tượng test: {@link UserDAO#updateUser(User)} (theo sơ đồ lớp OOAD).
 * Cách test: spy DAO + mock JDBC (getConnection trả về Connection giả) nên
 * không cần CSDL thật. Kiểm tra DAO chạy đúng UPDATE và commit giao dịch.
 */
class QuanLyNguoiDungTest {

    @Test
    @DisplayName("Sửa người dùng đã tồn tại -> cập nhật thành công")
    void suaNguoiDungTonTai_thanhCong() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);   // 1 dòng bị ảnh hưởng = đã tồn tại

        UserDAO dao = spy(new UserDAO());
        doReturn(con).when(dao).getConnection();

        Student sv = new Student();
        sv.setId("SV001");
        sv.setFullName("Nguyễn Văn A");
        sv.setStatus("active");
        sv.setEmail("a@uni.edu.vn");
        sv.setCourse("K23");
        sv.setMajor("CNTT");
        sv.setClassName("K23CNTT");

        boolean ketQua = dao.updateUser(sv);

        assertTrue(ketQua, "Sửa người dùng đã có trong CSDL phải trả về true");
        verify(con).prepareStatement(contains("UPDATE tblUser"));
        verify(con).commit();
    }

    @Test
    @DisplayName("Sửa người dùng KHÔNG tồn tại -> trả về false (rollback)")
    void suaNguoiDungKhongTonTai_thatBai() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);   // không có dòng nào -> id không tồn tại

        UserDAO dao = spy(new UserDAO());
        doReturn(con).when(dao).getConnection();

        Student sv = new Student();
        sv.setId("KHONG_CO");
        sv.setFullName("Ẩn danh");
        sv.setStatus("active");

        assertFalse(dao.updateUser(sv));
        verify(con).rollback();
    }
}
