package com.universe.view;

import com.universe.dao.UserDAO;
import com.universe.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Giao diện chính giảng viên (LecturerHomeFrm).
 * Điều hướng tới: gửi thông báo (4.3.2.2), tạo QR (4.3.1.7),
 * quản lý điểm danh (4.3.1.8), nhập/sửa điểm (4.3.1.10).
 */
public class LecturerHomeFrm extends JFrame implements ActionListener {

    private final User currentUser;

    private final JButton btnSendNotification = new JButton("Gửi thông báo");
    private final JButton btnQRCode           = new JButton("Tạo mã QR điểm danh");
    private final JButton btnAttendance       = new JButton("Quản lý điểm danh");
    private final JButton btnEnterGrade       = new JButton("Nhập / Sửa điểm");
    private final JButton btnLogout           = new JButton("Đăng xuất");

    private final UserDAO userDAO = new UserDAO();

    public LecturerHomeFrm(User user) {
        this.currentUser = user;
        setTitle("UniVerse - Giảng viên: " + user.getFullName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(560, 380);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel hi = new JLabel("Xin chào, " + currentUser.getFullName() + " (Giảng viên)");
        hi.setFont(hi.getFont().deriveFont(Font.BOLD, 15f));
        header.add(hi);

        JPanel center = new JPanel(new GridLayout(4, 1, 12, 12));
        center.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));
        for (JButton b : new JButton[]{btnSendNotification, btnQRCode, btnAttendance, btnEnterGrade}) {
            b.addActionListener(this);
            center.add(b);
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLogout.addActionListener(this);
        footer.add(btnLogout);

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnSendNotification) {
            new SendNotificationFrm(currentUser).setVisible(true);
        } else if (src == btnQRCode) {
            new QRCodeFrm(currentUser).setVisible(true);
        } else if (src == btnAttendance) {
            new AttendanceManageFrm(currentUser).setVisible(true);
        } else if (src == btnEnterGrade) {
            new GradeEntryFrm(currentUser).setVisible(true);
        } else if (src == btnLogout) {
            userDAO.logout(currentUser.getId());
            dispose();
            new LoginFrm().setVisible(true);
        }
    }
}
