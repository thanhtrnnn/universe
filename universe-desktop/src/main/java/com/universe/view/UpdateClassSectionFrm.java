package com.universe.view;

import com.universe.dao.ClassSectionDAO;
import com.universe.dao.CourseDAO;
import com.universe.entity.ClassSection;
import com.universe.entity.Course;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Giao diện cập nhật lớp học phần (Chương 4.3.2.5, bước 12-33).
 * searchCourse() để gán học phần + updateClassSection() lưu thay đổi.
 */
public class UpdateClassSectionFrm extends JFrame implements ActionListener {

    private final ClassSection cls;
    private final Runnable onSaved;
    private final ClassSectionDAO classSectionDAO = new ClassSectionDAO();
    private final CourseDAO courseDAO = new CourseDAO();

    private final JTextField inClassId = new JTextField(14);
    private final JTextField inName = new JTextField(20);
    private final JTextField inSemester = new JTextField(10);
    private final JTextField inYear = new JTextField(10);
    private final JTextField inMaxStudents = new JTextField(6);
    private final JComboBox<String> inStatus = new JComboBox<>(new String[]{"open", "full", "closed"});

    private final JTextField inSearchCourse = new JTextField(12);
    private final JButton subSearchCourse = new JButton("Tìm học phần");
    private final JComboBox<Course> cbCourse = new JComboBox<>();

    private final JButton subUpdate = new JButton("Cập nhật");

    public UpdateClassSectionFrm(ClassSection cls, Runnable onSaved) {
        this.cls = cls;
        this.onSaved = onSaved;
        setTitle("Cập nhật lớp học phần - " + cls.getId());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(480, 420);
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
        gc.gridx = 0; gc.gridy = y; panel.add(new JLabel("Mã lớp:"), gc);
        gc.gridx = 1; panel.add(inClassId, gc);
        gc.gridx = 0; gc.gridy = ++y; panel.add(new JLabel("Tên lớp:"), gc);
        gc.gridx = 1; panel.add(inName, gc);
        gc.gridx = 0; gc.gridy = ++y; panel.add(new JLabel("Học kỳ:"), gc);
        gc.gridx = 1; panel.add(inSemester, gc);
        gc.gridx = 0; gc.gridy = ++y; panel.add(new JLabel("Năm học:"), gc);
        gc.gridx = 1; panel.add(inYear, gc);
        gc.gridx = 0; gc.gridy = ++y; panel.add(new JLabel("Sĩ số tối đa:"), gc);
        gc.gridx = 1; panel.add(inMaxStudents, gc);
        gc.gridx = 0; gc.gridy = ++y; panel.add(new JLabel("Trạng thái:"), gc);
        gc.gridx = 1; panel.add(inStatus, gc);

        JPanel coursePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        coursePanel.add(inSearchCourse);
        coursePanel.add(subSearchCourse);
        gc.gridx = 0; gc.gridy = ++y; panel.add(new JLabel("Học phần:"), gc);
        gc.gridx = 1; panel.add(coursePanel, gc);
        gc.gridx = 1; gc.gridy = ++y; panel.add(cbCourse, gc);

        gc.gridx = 0; gc.gridy = ++y; gc.gridwidth = 2; gc.anchor = GridBagConstraints.CENTER;
        panel.add(subUpdate, gc);

        subSearchCourse.addActionListener(this);
        subUpdate.addActionListener(this);
        add(panel);
    }

    private void fillData() {
        inClassId.setText(cls.getClassId());
        inName.setText(cls.getName());
        inSemester.setText(cls.getSemester());
        inYear.setText(cls.getYear());
        inMaxStudents.setText(String.valueOf(cls.getMaxStudents()));
        inStatus.setSelectedItem(cls.getStatus());
        reloadCourses(courseDAO.getListCourse());
    }

    private void reloadCourses(List<Course> courses) {
        cbCourse.removeAllItems();
        for (Course c : courses) {
            cbCourse.addItem(c);
            if (c.getId().equals(cls.getCourseId())) {
                cbCourse.setSelectedItem(c);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == subSearchCourse) {
            reloadCourses(courseDAO.searchCourse(inSearchCourse.getText().trim()));
        } else if (src == subUpdate) {
            try {
                cls.setClassId(inClassId.getText().trim());
                cls.setName(inName.getText().trim());
                cls.setSemester(inSemester.getText().trim());
                cls.setYear(inYear.getText().trim());
                cls.setMaxStudents(Integer.parseInt(inMaxStudents.getText().trim()));
                cls.setStatus((String) inStatus.getSelectedItem());
                Course selected = (Course) cbCourse.getSelectedItem();
                if (selected != null) {
                    cls.setCourseId(selected.getId());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Sĩ số tối đa phải là số.");
                return;
            }
            boolean ok = classSectionDAO.updateClassSection(cls);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Cập nhật lớp học phần thành công");
                if (onSaved != null) onSaved.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại");
            }
        }
    }
}
