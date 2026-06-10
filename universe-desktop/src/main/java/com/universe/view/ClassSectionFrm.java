package com.universe.view;

import com.universe.dao.ClassSectionDAO;
import com.universe.entity.ClassSection;
import com.universe.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Giao diện quản lý lớp học phần (Chương 4.3.2.5, bước 1-11).
 * getListClassSection() → bảng; chọn + sửa → mở UpdateClassSectionFrm.
 */
public class ClassSectionFrm extends JFrame implements ActionListener {

    private final User currentUser;
    private final ClassSectionDAO classSectionDAO = new ClassSectionDAO();

    private final JButton subUpdate = new JButton("Sửa lớp đã chọn");
    private final JTable tblClass;
    private final DefaultTableModel model;

    private List<ClassSection> classList;

    public ClassSectionFrm(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Quản lý lớp học phần");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(760, 440);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(
                new Object[]{"Mã lớp", "Tên lớp", "Học kỳ", "Năm", "Sĩ số tối đa", "Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblClass = new JTable(model);

        buildUI();
        loadClassSections();
    }

    private void buildUI() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(subUpdate);
        subUpdate.addActionListener(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(tblClass), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadClassSections() {
        classList = classSectionDAO.getListClassSection();
        model.setRowCount(0);
        for (ClassSection c : classList) {
            model.addRow(new Object[]{
                    c.getClassId(), c.getName(), c.getSemester(), c.getYear(),
                    c.getMaxStudents(), c.getStatus()});
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == subUpdate) {
            int row = tblClass.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp học phần.");
                return;
            }
            ClassSection cls = classList.get(row);
            new UpdateClassSectionFrm(cls, this::loadClassSections).setVisible(true);
        }
    }
}
