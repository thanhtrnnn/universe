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
 * Giao diện tìm học phần để đăng ký (Chương 4.3.2.6, bước 1-13).
 * searchCourseRegistration() → chọn học phần → mở ClassSectionRegistrationFrm.
 */
public class SearchCourseRegistrationFrm extends JFrame implements ActionListener {

    private final User currentUser;
    private final CourseDAO courseDAO = new CourseDAO();

    private final JTextField inSearch = new JTextField(20);
    private final JButton subSearchCourseRegistration = new JButton("Tìm học phần");
    private final JButton subNext = new JButton("Tiếp tục");
    private final JTable tblCourse;
    private final DefaultTableModel model;

    private List<Course> courseList;

    public SearchCourseRegistrationFrm(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Đăng ký học phần - Tìm môn");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(640, 420);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(new Object[]{"Mã HP", "Tên học phần", "Tín chỉ", "Khoa"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCourse = new JTable(model);

        buildUI();
    }

    private void buildUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Tên/Mã học phần:"));
        top.add(inSearch);
        top.add(subSearchCourseRegistration);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(subNext);

        subSearchCourseRegistration.addActionListener(this);
        subNext.addActionListener(this);
        inSearch.addActionListener(this);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tblCourse), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadCourses(String keyword) {
        courseList = courseDAO.searchCourseRegistration(keyword);
        model.setRowCount(0);
        for (Course c : courseList) {
            model.addRow(new Object[]{c.getId(), c.getName(), c.getCredits(), c.getDepartment()});
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == subSearchCourseRegistration || src == inSearch) {
            loadCourses(inSearch.getText().trim());
        } else if (src == subNext) {
            int row = tblCourse.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một học phần.");
                return;
            }
            Course selected = courseList.get(row);
            new ClassSectionRegistrationFrm(currentUser, selected).setVisible(true);
        }
    }
}
