package com.universe.view;

import com.universe.dao.CourseRecordDAO;
import com.universe.entity.CourseRecord;
import com.universe.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Giao diện xem điểm của sinh viên (Chương 4.3.1.9, đã chuẩn hoá tên lớp).
 * viewGrade() → danh sách lớp + điểm tổng kết; chọn lớp → chi tiết điểm thành phần.
 */
public class ViewGradeFrm extends JFrame implements ActionListener {

    private final User currentUser;
    private final CourseRecordDAO courseRecordDAO = new CourseRecordDAO();

    private final JTable tblCourseList;
    private final DefaultTableModel courseModel;
    private final JTable tblGradeDetail;
    private final DefaultTableModel detailModel;
    private final JButton btnViewDetail = new JButton("Xem chi tiết điểm");

    private List<CourseRecord> records;

    public ViewGradeFrm(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Xem điểm - " + currentUser.getFullName());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(760, 480);
        setLocationRelativeTo(null);

        courseModel = new DefaultTableModel(
                new Object[]{"Lớp học phần", "Trạng thái", "Điểm tổng kết"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCourseList = new JTable(courseModel);

        detailModel = new DefaultTableModel(
                new Object[]{"Thành phần", "Điểm", "Trọng số"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblGradeDetail = new JTable(detailModel);

        buildUI();
        loadGrades();
    }

    private void buildUI() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnViewDetail);
        btnViewDetail.addActionListener(this);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tblCourseList), new JScrollPane(tblGradeDetail));
        split.setResizeWeight(0.55);

        setLayout(new BorderLayout());
        add(split, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadGrades() {
        records = courseRecordDAO.viewGrade(currentUser.getId());
        courseModel.setRowCount(0);
        for (CourseRecord r : records) {
            Double total = r.getTotalScore();
            courseModel.addRow(new Object[]{
                    r.getClassSectionName(), r.getStatus(),
                    total != null ? total : "Chưa có"});
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnViewDetail) {
            int row = tblCourseList.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp học phần.");
                return;
            }
            showDetail(records.get(row));
        }
    }

    private void showDetail(CourseRecord r) {
        detailModel.setRowCount(0);
        detailModel.addRow(new Object[]{"Thường xuyên", fmt(r.getScore1()), "20%"});
        detailModel.addRow(new Object[]{"Giữa kỳ", fmt(r.getScore2()), "30%"});
        detailModel.addRow(new Object[]{"Thành phần khác", fmt(r.getScore3()), "-"});
        detailModel.addRow(new Object[]{"Cuối kỳ", fmt(r.getExamScore()), "50%"});
        Double total = r.getTotalScore();
        detailModel.addRow(new Object[]{"Tổng kết", total != null ? total : "Chưa có", "100%"});
    }

    private String fmt(Double v) {
        return v == null ? "—" : String.valueOf(v);
    }
}
