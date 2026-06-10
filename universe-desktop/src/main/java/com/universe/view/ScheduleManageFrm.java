package com.universe.view;

import com.universe.dao.ClassSectionDAO;
import com.universe.dao.ScheduleDAO;
import com.universe.entity.ClassSection;
import com.universe.entity.Schedule;
import com.universe.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Giao diện quản lý lịch học (Chương 4.3.2.3, bước 6-17).
 * search lớp → chọn lớp → mở EditScheduleFrm.
 */
public class ScheduleManageFrm extends JFrame implements ActionListener {

    private final User currentUser;
    private final ClassSectionDAO classSectionDAO = new ClassSectionDAO();
    private final ScheduleDAO scheduleDAO = new ScheduleDAO();

    private final JTextField inSearch = new JTextField(18);
    private final JButton subSearch = new JButton("Tìm lớp");
    private final JButton subEditSchedule = new JButton("Sửa lịch lớp đã chọn");
    private final JTable tblClass;
    private final DefaultTableModel model;

    private List<ClassSection> classList;

    public ScheduleManageFrm(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Quản lý lịch học");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(720, 440);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(new Object[]{"Mã lớp", "Tên lớp", "Học kỳ", "Năm học"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblClass = new JTable(model);

        buildUI();
        loadClasses("");
    }

    private void buildUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Tìm lớp:"));
        top.add(inSearch);
        top.add(subSearch);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(subEditSchedule);

        subSearch.addActionListener(this);
        subEditSchedule.addActionListener(this);
        inSearch.addActionListener(this);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tblClass), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadClasses(String keyword) {
        classList = classSectionDAO.searchClassSection(keyword);
        model.setRowCount(0);
        for (ClassSection c : classList) {
            model.addRow(new Object[]{c.getClassId(), c.getName(), c.getSemester(), c.getYear()});
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == subSearch || src == inSearch) {
            loadClasses(inSearch.getText().trim());
        } else if (src == subEditSchedule) {
            int row = tblClass.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp học phần.");
                return;
            }
            ClassSection cls = classList.get(row);
            List<Schedule> schedules = scheduleDAO.getSchedulesByClassSection(cls.getId());
            if (schedules.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lớp này chưa có lịch học.");
                return;
            }
            // Sửa buổi lịch đầu tiên (demo). EditScheduleFrm cho chọn/sửa chi tiết.
            new EditScheduleFrm(schedules.get(0)).setVisible(true);
        }
    }
}
