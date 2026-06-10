package com.universe.view;

import com.universe.dao.AttendanceDAO;
import com.universe.dao.ClassSectionDAO;
import com.universe.dao.ClassSessionDAO;
import com.universe.dao.CourseRecordDAO;
import com.universe.entity.Attendance;
import com.universe.entity.ClassSection;
import com.universe.entity.ClassSession;
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
 * Giao diện quản lý điểm danh thủ công (Chương 4.3.1.8, đã chuẩn hoá tên lớp).
 * viewAttendance() → bảng; chọn dòng + Sửa → markManualAttendance().
 * Nếu buổi học chưa có bản ghi điểm danh, sinh danh sách SV của lớp (mặc định ABSENT).
 */
public class AttendanceManageFrm extends JFrame implements ActionListener {

    private final User currentUser;
    private final ClassSectionDAO classSectionDAO = new ClassSectionDAO();
    private final ClassSessionDAO classSessionDAO = new ClassSessionDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final CourseRecordDAO courseRecordDAO = new CourseRecordDAO();

    private final JComboBox<ClassSection> cbClass = new JComboBox<>();
    private final JComboBox<ClassSession> cbSession = new JComboBox<>();
    private final JButton btnLoad = new JButton("Tải điểm danh");
    private final JButton btnSaveManual = new JButton("Sửa trạng thái dòng đã chọn");
    private final JTable tblAttendance;
    private final DefaultTableModel model;

    private List<Attendance> attendanceList = new ArrayList<>();

    public AttendanceManageFrm(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Quản lý điểm danh");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(760, 480);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(
                new Object[]{"MSSV", "Họ tên", "Trạng thái", "Phương thức"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblAttendance = new JTable(model);

        buildUI();
        loadClasses();
    }

    private void buildUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Lớp:"));
        top.add(cbClass);
        top.add(new JLabel("Buổi học:"));
        top.add(cbSession);
        top.add(btnLoad);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnSaveManual);

        cbClass.addActionListener(this);
        btnLoad.addActionListener(this);
        btnSaveManual.addActionListener(this);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tblAttendance), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadClasses() {
        cbClass.removeAllItems();
        for (ClassSection c : classSectionDAO.getListClassSection()) {
            cbClass.addItem(c);
        }
        reloadSessions();
    }

    private void reloadSessions() {
        cbSession.removeAllItems();
        ClassSection cls = (ClassSection) cbClass.getSelectedItem();
        if (cls == null) return;
        for (ClassSession s : classSessionDAO.getSessions(cls.getId())) {
            cbSession.addItem(s);
        }
    }

    private void loadAttendance() {
        ClassSession session = (ClassSession) cbSession.getSelectedItem();
        ClassSection cls = (ClassSection) cbClass.getSelectedItem();
        if (session == null || cls == null) {
            JOptionPane.showMessageDialog(this, "Lớp này chưa có buổi học. Hãy tạo QR để sinh buổi học trước.");
            return;
        }

        attendanceList = attendanceDAO.viewAttendance(session.getId());
        if (attendanceList.isEmpty()) {
            // Sinh danh sách từ SV đã đăng ký lớp (mặc định ABSENT, chưa lưu DB)
            for (CourseRecord cr : courseRecordDAO.findByClassSection(cls.getId())) {
                Attendance a = new Attendance();
                a.setClassSessionId(session.getId());
                a.setStudentId(cr.getStudentId());
                a.setStudentName(cr.getStudentName());
                a.setStatus("ABSENT");
                a.setMethod("-");
                attendanceList.add(a);
            }
        }
        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Attendance a : attendanceList) {
            model.addRow(new Object[]{a.getStudentId(), a.getStudentName(), a.getStatus(), a.getMethod()});
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == cbClass) {
            reloadSessions();
        } else if (src == btnLoad) {
            loadAttendance();
        } else if (src == btnSaveManual) {
            editSelected();
        }
    }

    private void editSelected() {
        int row = tblAttendance.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sinh viên.");
            return;
        }
        Attendance a = attendanceList.get(row);

        // Modal sửa trạng thái
        String[] options = {"PRESENT", "ABSENT", "LATE"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
                "Trạng thái cho " + a.getStudentName() + " (" + a.getStudentId() + "):",
                "Sửa điểm danh", JOptionPane.PLAIN_MESSAGE, null, options, a.getStatus());
        if (newStatus == null) return;

        a.setStatus(newStatus);
        a.setMethod("MANUAL");
        boolean ok = attendanceDAO.markManualAttendance(a);
        if (ok) {
            refreshTable();
            JOptionPane.showMessageDialog(this, "Cập nhật điểm danh thành công");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại");
        }
    }
}
