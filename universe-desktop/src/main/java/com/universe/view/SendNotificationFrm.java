package com.universe.view;

import com.universe.dao.ClassSectionDAO;
import com.universe.dao.NotificationDAO;
import com.universe.dao.UserDAO;
import com.universe.entity.ClassSection;
import com.universe.entity.Notification;
import com.universe.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Giao diện gửi thông báo đến tất cả sinh viên của một lớp (Chương 4.3.2.2).
 * search lớp (ClassSectionDAO) → chọn lớp → nhập title/content →
 * NotificationDAO.sendNotification() (publish Kafka, consumer ghi DB).
 */
public class SendNotificationFrm extends JFrame implements ActionListener {

    private final User currentUser;
    private final ClassSectionDAO classSectionDAO = new ClassSectionDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final UserDAO userDAO = new UserDAO();

    private final JTextField inSearch = new JTextField(18);
    private final JButton subSearch = new JButton("Tìm lớp");
    private final JTable tblClass;
    private final DefaultTableModel classModel;

    private final JTextField inTitle = new JTextField(28);
    private final JTextArea inContent = new JTextArea(5, 28);
    private final JButton subSend = new JButton("Gửi thông báo");

    private List<ClassSection> classList;

    public SendNotificationFrm(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Gửi thông báo");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(680, 520);
        setLocationRelativeTo(null);

        classModel = new DefaultTableModel(new Object[]{"Mã lớp", "Tên lớp", "Học kỳ"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblClass = new JTable(classModel);

        buildUI();
    }

    private void buildUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Tìm lớp:"));
        top.add(inSearch);
        top.add(subSearch);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.gridx = 0; gc.gridy = 0; form.add(new JLabel("Tiêu đề:"), gc);
        gc.gridx = 1; form.add(inTitle, gc);
        gc.gridx = 0; gc.gridy = 1; form.add(new JLabel("Nội dung:"), gc);
        gc.gridx = 1; form.add(new JScrollPane(inContent), gc);
        gc.gridx = 1; gc.gridy = 2; gc.anchor = GridBagConstraints.EAST; form.add(subSend, gc);

        subSearch.addActionListener(this);
        subSend.addActionListener(this);
        inSearch.addActionListener(this);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tblClass), form);
        split.setResizeWeight(0.5);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    private void loadClasses(String keyword) {
        classList = classSectionDAO.searchClassSection(keyword);
        classModel.setRowCount(0);
        for (ClassSection c : classList) {
            classModel.addRow(new Object[]{c.getClassId(), c.getName(), c.getSemester()});
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == subSearch || src == inSearch) {
            loadClasses(inSearch.getText().trim());
        } else if (src == subSend) {
            sendNotification();
        }
    }

    private void sendNotification() {
        int row = tblClass.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp học phần.");
            return;
        }
        String title = inTitle.getText().trim();
        String content = inContent.getText().trim();
        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tiêu đề và nội dung.");
            return;
        }

        ClassSection cls = classList.get(row);
        List<String> studentIds = userDAO.findStudentIdsByClassSection(cls.getId());
        if (studentIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lớp chưa có sinh viên đăng ký.");
            return;
        }

        try {
            for (String sid : studentIds) {
                Notification n = new Notification();
                n.setTitle(title);
                n.setContent(content);
                n.setRecipientType("class:" + cls.getId());
                n.setUserId(sid);
                notificationDAO.sendNotification(n);  // publish Kafka
            }
            JOptionPane.showMessageDialog(this,
                    "Đã gửi thông báo tới " + studentIds.size() + " sinh viên (qua Kafka).");
            inTitle.setText("");
            inContent.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi gửi thông báo: " + ex.getMessage());
        }
    }
}
