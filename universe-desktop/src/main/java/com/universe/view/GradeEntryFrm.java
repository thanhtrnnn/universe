package com.universe.view;

import com.universe.dao.ClassSectionDAO;
import com.universe.dao.CourseRecordDAO;
import com.universe.entity.ClassSection;
import com.universe.entity.CourseRecord;
import com.universe.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Giao diện nhập / sửa điểm (Chương 4.3.1.10, đã chuẩn hoá tên lớp).
 * btnSearchClass → searchClassSection(); chọn lớp → bảng SV ô nhập điểm;
 * btnSubmitGrades → enterGrade() (kiểm tra điểm trong [0,10] - test case 5.2.2.10).
 */
public class GradeEntryFrm extends JFrame implements ActionListener {

    private final User currentUser;
    private final ClassSectionDAO classSectionDAO = new ClassSectionDAO();
    private final CourseRecordDAO courseRecordDAO = new CourseRecordDAO();

    private final JTextField txtClassSection = new JTextField(16);
    private final JButton btnSearchClass = new JButton("Tìm lớp");
    private final JButton btnLoadStudents = new JButton("Tải sinh viên");
    private final JButton btnSubmitGrades = new JButton("Xác nhận nộp điểm");
    private final JComboBox<ClassSection> cbClass = new JComboBox<>();
    private final JTable tblGrades;
    private final DefaultTableModel model;

    private List<CourseRecord> records = new ArrayList<>();

    public GradeEntryFrm(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Nhập / Sửa điểm");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(820, 480);
        setLocationRelativeTo(null);

        // Cột TX, GK, TP, CK cho phép sửa
        model = new DefaultTableModel(
                new Object[]{"MSSV", "Họ tên", "TX", "GK", "Thành phần", "CK"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c >= 2; }
        };
        tblGrades = new JTable(model);

        buildUI();
    }

    private void buildUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Tìm lớp:"));
        top.add(txtClassSection);
        top.add(btnSearchClass);
        top.add(cbClass);
        top.add(btnLoadStudents);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnSubmitGrades);

        for (JButton b : new JButton[]{btnSearchClass, btnLoadStudents, btnSubmitGrades}) {
            b.addActionListener(this);
        }
        txtClassSection.addActionListener(this);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tblGrades), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void searchClasses() {
        cbClass.removeAllItems();
        for (ClassSection c : classSectionDAO.searchClassSection(txtClassSection.getText().trim())) {
            cbClass.addItem(c);
        }
    }

    private void loadStudents() {
        ClassSection cls = (ClassSection) cbClass.getSelectedItem();
        if (cls == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tìm và chọn một lớp học phần.");
            return;
        }
        records = courseRecordDAO.findByClassSection(cls.getId());
        model.setRowCount(0);
        for (CourseRecord r : records) {
            model.addRow(new Object[]{
                    r.getStudentId(), r.getStudentName(),
                    val(r.getScore1()), val(r.getScore2()), val(r.getScore3()), val(r.getExamScore())});
        }
        if (records.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lớp chưa có sinh viên đăng ký.");
        }
    }

    private void submitGrades() {
        if (records.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có dữ liệu để nộp.");
            return;
        }
        if (tblGrades.isEditing()) {
            tblGrades.getCellEditor().stopCellEditing();
        }
        try {
            for (int i = 0; i < records.size(); i++) {
                CourseRecord r = records.get(i);
                r.setScore1(parseScore(model.getValueAt(i, 2)));
                r.setScore2(parseScore(model.getValueAt(i, 3)));
                r.setScore3(parseScore(model.getValueAt(i, 4)));
                r.setExamScore(parseScore(model.getValueAt(i, 5)));
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }
        boolean ok = courseRecordDAO.enterGrade(records);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Nộp điểm thành công");
        } else {
            JOptionPane.showMessageDialog(this, "Nộp điểm thất bại");
        }
    }

    /** Parse và validate điểm trong [0,10], cho phép rỗng = null. */
    private Double parseScore(Object cell) {
        if (cell == null || cell.toString().trim().isEmpty()) {
            return null;
        }
        double v;
        try {
            v = Double.parseDouble(cell.toString().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Điểm phải là số: '" + cell + "'");
        }
        if (v < 0 || v > 10) {
            throw new IllegalArgumentException("Điểm phải trong khoảng [0, 10]: " + v);
        }
        return v;
    }

    private String val(Double d) {
        return d == null ? "" : String.valueOf(d);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnSearchClass || src == txtClassSection) {
            searchClasses();
        } else if (src == btnLoadStudents) {
            loadStudents();
        } else if (src == btnSubmitGrades) {
            submitGrades();
        }
    }
}
