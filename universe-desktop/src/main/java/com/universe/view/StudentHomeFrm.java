package com.universe.view;

import com.universe.dao.UserDAO;
import com.universe.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Giao diện chính sinh viên (StudentHomeFrm).
 * Điều hướng tới: đăng ký học phần (4.3.2.6), xem điểm (4.3.1.9).
 */
public class StudentHomeFrm extends JFrame implements ActionListener {

    private final User currentUser;

    private final JButton btnRegister  = new JButton("Đăng ký lớp học phần");
    private final JButton btnViewGrade = new JButton("Xem điểm");
    private final JButton btnLogout    = new JButton("Đăng xuất");

    private final UserDAO userDAO = new UserDAO();

    public StudentHomeFrm(User user) {
        this.currentUser = user;
        setTitle("UniVerse - Sinh viên: " + user.getFullName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(520, 320);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel hi = new JLabel("Xin chào, " + currentUser.getFullName() + " (Sinh viên)");
        hi.setFont(hi.getFont().deriveFont(Font.BOLD, 15f));
        header.add(hi);

        JPanel center = new JPanel(new GridLayout(2, 1, 12, 12));
        center.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));
        for (JButton b : new JButton[]{btnRegister, btnViewGrade}) {
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
        if (src == btnRegister) {
            new SearchCourseRegistrationFrm(currentUser).setVisible(true);
        } else if (src == btnViewGrade) {
            new ViewGradeFrm(currentUser).setVisible(true);
        } else if (src == btnLogout) {
            userDAO.logout(currentUser.getId());
            dispose();
            new LoginFrm().setVisible(true);
        }
    }
}
