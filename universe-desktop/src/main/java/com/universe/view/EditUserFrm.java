package com.universe.view;

import com.universe.dao.UserDAO;
import com.universe.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Giao diện chỉnh sửa người dùng (Chương 4.3.2.1, bước 29-42).
 * set lại thuộc tính User → UserDAO.updateUser() → thông báo thành công.
 */
public class EditUserFrm extends JFrame implements ActionListener {

    private final User user;
    private final Runnable onSaved;
    private final UserDAO userDAO = new UserDAO();

    private final JTextField outinFullName = new JTextField(20);
    private final JTextField outinPhone = new JTextField(20);
    private final JTextField outinEmail = new JTextField(20);
    private final JComboBox<String> inRole = new JComboBox<>(new String[]{"Admin", "Lecturer", "Student"});
    private final JComboBox<String> inStatus = new JComboBox<>(new String[]{"active", "inactive"});
    private final JButton subSave = new JButton("Lưu thay đổi");

    public EditUserFrm(User user, Runnable onSaved) {
        this.user = user;
        this.onSaved = onSaved;
        setTitle("Chỉnh sửa người dùng - " + user.getId());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);
        buildUI();
        fillData();
    }

    private void buildUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;

        int y = 0;
        gc.gridx = 0; gc.gridy = y; panel.add(new JLabel("Mã số:"), gc);
        gc.gridx = 1; panel.add(new JLabel(user.getId()), gc);

        gc.gridx = 0; gc.gridy = ++y; panel.add(new JLabel("Họ tên:"), gc);
        gc.gridx = 1; panel.add(outinFullName, gc);

        gc.gridx = 0; gc.gridy = ++y; panel.add(new JLabel("Số điện thoại:"), gc);
        gc.gridx = 1; panel.add(outinPhone, gc);

        gc.gridx = 0; gc.gridy = ++y; panel.add(new JLabel("Email:"), gc);
        gc.gridx = 1; panel.add(outinEmail, gc);

        gc.gridx = 0; gc.gridy = ++y; panel.add(new JLabel("Vai trò:"), gc);
        gc.gridx = 1; panel.add(inRole, gc);

        gc.gridx = 0; gc.gridy = ++y; panel.add(new JLabel("Trạng thái:"), gc);
        gc.gridx = 1; panel.add(inStatus, gc);

        gc.gridx = 0; gc.gridy = ++y; gc.gridwidth = 2; gc.anchor = GridBagConstraints.CENTER;
        panel.add(subSave, gc);

        subSave.addActionListener(this);
        add(panel);
    }

    private void fillData() {
        outinFullName.setText(user.getFullName());
        outinPhone.setText(user.getPhone());
        outinEmail.setText(user.getEmail());
        inRole.setSelectedItem(user.getRole());
        inStatus.setSelectedItem(user.getStatus());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == subSave) {
            // set lại thuộc tính cho thực thể User
            user.setFullName(outinFullName.getText().trim());
            user.setPhone(outinPhone.getText().trim());
            user.setEmail(outinEmail.getText().trim());
            user.setRole((String) inRole.getSelectedItem());
            user.setStatus((String) inStatus.getSelectedItem());

            boolean ok;
            try {
                ok = userDAO.updateUser(user);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + ex.getMessage());
                return;
            }
            if (ok) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công");
                if (onSaved != null) onSaved.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại");
            }
        }
    }
}
