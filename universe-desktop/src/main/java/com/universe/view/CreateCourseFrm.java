package com.universe.view;

import com.universe.dao.CourseDAO;
import com.universe.entity.Course;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Giao diện thêm học phần (Chương 4.3.2.4, bước 13-21).
 * subAdd → CourseDAO.createCourse() (kiểm tra trùng mã - test case 5.2.2.4).
 */
public class CreateCourseFrm extends JFrame implements ActionListener {

    private final Runnable onCreated;
    private final CourseDAO courseDAO = new CourseDAO();

    private final JTextField inId = new JTextField(15);
    private final JTextField inName = new JTextField(20);
    private final JTextField inCredits = new JTextField(5);
    private final JTextField inDepartment = new JTextField(20);
    private final JButton subAdd = new JButton("Lưu");

    public CreateCourseFrm(Runnable onCreated) {
        this.onCreated = onCreated;
        setTitle("Thêm học phần");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 280);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;

        int y = 0;
        gc.gridx = 0; gc.gridy = y; panel.add(new JLabel("Mã học phần:"), gc);
        gc.gridx = 1; panel.add(inId, gc);
        gc.gridx = 0; gc.gridy = ++y; panel.add(new JLabel("Tên học phần:"), gc);
        gc.gridx = 1; panel.add(inName, gc);
        gc.gridx = 0; gc.gridy = ++y; panel.add(new JLabel("Số tín chỉ:"), gc);
        gc.gridx = 1; panel.add(inCredits, gc);
        gc.gridx = 0; gc.gridy = ++y; panel.add(new JLabel("Khoa:"), gc);
        gc.gridx = 1; panel.add(inDepartment, gc);
        gc.gridx = 0; gc.gridy = ++y; gc.gridwidth = 2; gc.anchor = GridBagConstraints.CENTER;
        panel.add(subAdd, gc);

        subAdd.addActionListener(this);
        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != subAdd) return;

        String id = inId.getText().trim();
        String name = inName.getText().trim();
        String creditsStr = inCredits.getText().trim();
        if (id.isEmpty() || name.isEmpty() || creditsStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }
        if (courseDAO.existsById(id)) {
            JOptionPane.showMessageDialog(this, "Mã học phần đã tồn tại.");
            return;
        }
        int credits;
        try {
            credits = Integer.parseInt(creditsStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tín chỉ phải là số.");
            return;
        }

        Course c = new Course(id, credits, name, inDepartment.getText().trim());
        boolean ok = courseDAO.createCourse(c);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Thêm học phần thành công");
            if (onCreated != null) onCreated.run();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại");
        }
    }
}
