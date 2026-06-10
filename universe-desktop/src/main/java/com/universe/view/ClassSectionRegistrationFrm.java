package com.universe.view;

import com.universe.dao.ClassSectionDAO;
import com.universe.dao.CourseRecordDAO;
import com.universe.entity.ClassSection;
import com.universe.entity.Course;
import com.universe.entity.CourseRecord;
import com.universe.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

/**
 * Giao diện đăng ký lớp học phần (Chương 4.3.2.6, bước 14-end).
 * getListClassSectionRegistration() → checkCapacity() → registerClassSection()
 * → CourseRecordDAO.confirmRegistration().
 */
public class ClassSectionRegistrationFrm extends JFrame implements ActionListener {

    private final User currentUser;
    private final Course course;
    private final ClassSectionDAO classSectionDAO = new ClassSectionDAO();
    private final CourseRecordDAO courseRecordDAO = new CourseRecordDAO();

    private final JButton subChoose = new JButton("Đăng ký lớp đã chọn");
    private final JTable tblClass;
    private final DefaultTableModel model;

    private List<ClassSection> classList;

    public ClassSectionRegistrationFrm(User currentUser, Course course) {
        this.currentUser = currentUser;
        this.course = course;
        setTitle("Đăng ký lớp học phần - " + course.getName());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(680, 420);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(
                new Object[]{"Mã lớp", "Tên lớp", "Học kỳ", "Sĩ số tối đa", "Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblClass = new JTable(model);

        buildUI();
        loadClassSections();
    }

    private void buildUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Các lớp học phần của: " + course.getName()));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(subChoose);
        subChoose.addActionListener(this);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tblClass), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadClassSections() {
        classList = classSectionDAO.getListClassSectionRegistration(course.getId());
        model.setRowCount(0);
        for (ClassSection c : classList) {
            model.addRow(new Object[]{
                    c.getClassId(), c.getName(), c.getSemester(), c.getMaxStudents(), c.getStatus()});
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != subChoose) return;

        int row = tblClass.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp học phần.");
            return;
        }
        ClassSection cls = classList.get(row);

        // checkCapacity
        if (!classSectionDAO.checkCapacity(cls.getId())) {
            JOptionPane.showMessageDialog(this, "Lớp học phần đã đầy, vui lòng chọn lớp khác.");
            return;
        }
        // chống đăng ký trùng
        if (courseRecordDAO.exists(currentUser.getId(), cls.getId())) {
            JOptionPane.showMessageDialog(this, "Bạn đã đăng ký lớp này rồi.");
            return;
        }

        // confirmRegistration
        CourseRecord r = new CourseRecord();
        r.setId("CR-" + System.nanoTime());
        r.setEnrolledAt(LocalDate.now());
        r.setStatus("Registered");
        r.setStudentId(currentUser.getId());
        r.setClassSectionId(cls.getId());

        boolean ok = courseRecordDAO.confirmRegistration(r);
        if (ok) {
            classSectionDAO.registerClassSection(cls.getId());
            JOptionPane.showMessageDialog(this, "Đăng ký lớp học phần thành công");
            loadClassSections();
        } else {
            JOptionPane.showMessageDialog(this, "Đăng ký thất bại");
        }
    }
}
