package com.universe.view;

import com.universe.dao.UserDAO;
import com.universe.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Giao diện đăng nhập (Chương 4.3.2.1, bước 1-11).
 * actionPerformed() → UserDAO.checkLogin() → mở Home theo vai trò.
 */
public class LoginFrm extends JFrame implements ActionListener {

    private final JTextField inUsername = new JTextField(18);
    private final JPasswordField inPassword = new JPasswordField(18);
    private final JButton subLogin = new JButton("Đăng nhập");
    private final JLabel outError = new JLabel(" ");

    private final UserDAO userDAO = new UserDAO();

    public LoginFrm() {
        setTitle("UniVerse - Đăng nhập");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 280);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("HỆ THỐNG QUẢN LÝ ĐẠI HỌC UNIVERSE");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        panel.add(title, gc);

        gc.gridwidth = 1;
        gc.gridx = 0; gc.gridy = 1; panel.add(new JLabel("Tên đăng nhập:"), gc);
        gc.gridx = 1; panel.add(inUsername, gc);

        gc.gridx = 0; gc.gridy = 2; panel.add(new JLabel("Mật khẩu:"), gc);
        gc.gridx = 1; panel.add(inPassword, gc);

        outError.setForeground(Color.RED);
        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2; panel.add(outError, gc);

        gc.gridx = 0; gc.gridy = 4; gc.gridwidth = 2; gc.anchor = GridBagConstraints.CENTER;
        panel.add(subLogin, gc);

        subLogin.addActionListener(this);
        inPassword.addActionListener(this); // Enter để đăng nhập

        add(panel);
        getRootPane().setDefaultButton(subLogin);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = inUsername.getText().trim();
        String password = new String(inPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            outError.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu");
            return;
        }

        User user;
        try {
            user = userDAO.checkLogin(username, password);
        } catch (Exception ex) {
            outError.setText("Lỗi kết nối CSDL: " + ex.getMessage());
            return;
        }

        if (user == null) {
            outError.setText("Thông tin đăng nhập không chính xác hoặc tài khoản bị khóa");
            return;
        }

        // Mở giao diện chính theo vai trò
        dispose();
        switch (user.getRole()) {
            case "Admin" -> new AdminHomeFrm(user).setVisible(true);
            case "Lecturer" -> new LecturerHomeFrm(user).setVisible(true);
            default -> new StudentHomeFrm(user).setVisible(true);
        }
    }
}
