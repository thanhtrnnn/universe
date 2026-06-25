package com.universe.dao;

import com.universe.entity.ClassSession;
import com.universe.entity.QRCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * STT 7 - Chức năng: Tạo mã QR.
 * Trường hợp kiểm thử: "Tạo phiên điểm danh mới cho buổi học".
 *
 * Đối tượng test: {@link QRCodeDAO#generateQr(ClassSession)}.
 * Sinh mã QR 'active', gắn vào buổi học và mở buổi (status='open').
 */
class TaoMaQRTest {

    @Test
    @DisplayName("Tạo mã QR cho buổi học chưa có QR -> sinh QR active, gắn vào buổi")
    void taoMaQRChoBuoiHoc_thanhCong() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        QRCodeDAO dao = spy(new QRCodeDAO());
        doReturn(con).when(dao).getConnection();

        ClassSession buoiHoc = new ClassSession();
        buoiHoc.setId("SESS01");   // chưa có qrCodeId

        QRCode qr = dao.generateQr(buoiHoc);

        assertNotNull(qr, "Phải sinh được mã QR mới");
        assertEquals("active", qr.getStatus(), "QR mới phải ở trạng thái active");
        assertTrue(qr.getExpiredAt().isAfter(qr.getCreatedAt()), "Thời điểm hết hạn phải sau lúc tạo");
        assertEquals(qr.getId(), buoiHoc.getQrCodeId(), "QR phải được gắn vào buổi học");
        verify(con).prepareStatement(contains("INSERT INTO tblQRCode"));
        verify(con).prepareStatement(contains("UPDATE tblClassSession"));
        verify(con).commit();
    }
}
