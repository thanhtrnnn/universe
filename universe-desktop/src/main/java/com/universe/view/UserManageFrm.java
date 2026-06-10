package com.universe.view;

import com.universe.dao.UserDAO;
import com.universe.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Giao diện quản lý người dùng (Chương 4.3.2.1, bước 17-28).
 * searchUser() → hiển thị bảng → click dòng mở EditUserFrm.
 */
public class UserManageFrm extends JFrame implements ActionListener {

    private final User currentUser;
    private final UserDAO userDAO = new UserDAO();

    private final JTextField inSearch = new JTextField(20);
    private final JButton subSearch = new JButton("Tìm kiếm");
    private final JButton subEdit = new JButton("Sửa người dùng đã chọn");
    private final JTable tblUser;
    private final DefaultTableModel model;

    private List<User> currentList;

    public UserManageFrm(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Quản lý người dùng");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(760, 460);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(
                new Object[]{"Mã số", "Họ tên", "Email", "Vai trò", "Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblUser = new JTable(model);

        buildUI();
        loadUsers("");   // formLoad: nạp toàn bộ
    }

    private void buildUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Từ khóa:"));
        top.add(inSearch);
        top.add(subSearch);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(subEdit);

        subSearch.addActionListener(this);
        subEdit.addActionListener(this);
        inSearch.addActionListener(this);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tblUser), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadUsers(String keyword) {
        currentList = userDAO.searchUser(keyword);
        model.setRowCount(0);
        for (User u : currentList) {
            model.addRow(new Object[]{u.getId(), u.getFullName(), u.getEmail(), u.getRole(), u.getStatus()});
        }
        if (currentList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả phù hợp.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == subSearch || src == inSearch) {
            loadUsers(inSearch.getText().trim());
        } else if (src == subEdit) {
            int row = tblUser.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng trong danh sách.");
                return;
            }
            User selected = currentList.get(row);
            // Mở EditUserFrm; sau khi lưu, callback nạp lại danh sách
            new EditUserFrm(selected, () -> loadUsers(inSearch.getText().trim())).setVisible(true);
        }
    }
}
