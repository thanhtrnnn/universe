package com.universe.view;

import com.universe.dao.UserDAO;
import com.universe.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Giao diện chính quản trị viên (AdminHomeFrm).
 * Điều hướng tới các chức năng 4.3.2.1/3/4/5.
 */
public class AdminHomeFrm extends JFrame implements ActionListener {

    private final User currentUser;

    private final JButton btnUserManage   = new JButton("Quản lý người dùng");
    private final JButton btnScheduleManage = new JButton("Quản lý lịch học");
    private final JButton btnCourse       = new JButton("Quản lý học phần");
    private final JButton btnClassSection = new JButton("Quản lý lớp học phần");
    private final JButton btnLogout       = new JButton("Đăng xuất");

    private final UserDAO userDAO = new UserDAO();

    public AdminHomeFrm(User user) {
        this.currentUser = user;
        setTitle("UniVerse - Quản trị viên: " + user.getFullName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(560, 360);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel hi = new JLabel("Xin chào, " + currentUser.getFullName() + " (Quản trị viên)");
        hi.setFont(hi.getFont().deriveFont(Font.BOLD, 15f));
        header.add(hi);

        JPanel center = new JPanel(new GridLayout(4, 1, 12, 12));
        center.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));
        for (JButton b : new JButton[]{btnUserManage, btnScheduleManage, btnCourse, btnClassSection}) {
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
        if (src == btnUserManage) {
            new UserManageFrm(currentUser).setVisible(true);
        } else if (src == btnScheduleManage) {
            new ScheduleManageFrm(currentUser).setVisible(true);
        } else if (src == btnCourse) {
            new CourseFrm(currentUser).setVisible(true);
        } else if (src == btnClassSection) {
            new ClassSectionFrm(currentUser).setVisible(true);
        } else if (src == btnLogout) {
            userDAO.logout(currentUser.getId());
            dispose();
            new LoginFrm().setVisible(true);
        }
    }
}
