package com.universe.view;

import com.universe.dao.CourseDAO;
import com.universe.entity.Course;
import com.universe.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Giao diện quản lý học phần (Chương 4.3.2.4, bước 1-12).
 * getListCourse() → bảng; subAdd → mở CreateCourseFrm.
 */
public class CourseFrm extends JFrame implements ActionListener {

    private final User currentUser;
    private final CourseDAO courseDAO = new CourseDAO();

    private final JButton subAdd = new JButton("Thêm học phần");
    private final JTable tblCourse;
    private final DefaultTableModel model;

    public CourseFrm(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Quản lý học phần");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(640, 420);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(new Object[]{"Mã HP", "Tên học phần", "Tín chỉ", "Khoa"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCourse = new JTable(model);

        buildUI();
        loadCourses();
    }

    private void buildUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(subAdd);
        subAdd.addActionListener(this);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tblCourse), BorderLayout.CENTER);
    }

    private void loadCourses() {
        List<Course> list = courseDAO.getListCourse();
        model.setRowCount(0);
        for (Course c : list) {
            model.addRow(new Object[]{c.getId(), c.getName(), c.getCredits(), c.getDepartment()});
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == subAdd) {
            new CreateCourseFrm(this::loadCourses).setVisible(true);
        }
    }
}
