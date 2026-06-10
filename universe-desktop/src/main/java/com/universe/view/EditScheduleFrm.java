package com.universe.view;

import com.universe.dao.ScheduleDAO;
import com.universe.entity.Schedule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Giao diện chỉnh sửa lịch học (Chương 4.3.2.3, bước 18-32).
 * set() thuộc tính Schedule → ScheduleDAO.updateSchedule().
 */
public class EditScheduleFrm extends JFrame implements ActionListener {

    private final Schedule schedule;
    private final ScheduleDAO scheduleDAO = new ScheduleDAO();

    private final JComboBox<String> inDay = new JComboBox<>(new String[]{
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"});
    private final JTextField inStart = new JTextField(5);
    private final JTextField inEnd = new JTextField(5);
    private final JTextField inRoom = new JTextField(12);
    private final JButton subSave = new JButton("Lưu");

    public EditScheduleFrm(Schedule schedule) {
        this.schedule = schedule;
        setTitle("Chỉnh sửa lịch học - " + schedule.getId());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(380, 260);
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
        gc.gridx = 0; gc.gridy = y; panel.add(new JLabel("Thứ:"), gc);
        gc.gridx = 1; panel.add(inDay, gc);
        gc.gridx = 0; gc.gridy = ++y; panel.add(new JLabel("Tiết bắt đầu:"), gc);
        gc.gridx = 1; panel.add(inStart, gc);
        gc.gridx = 0; gc.gridy = ++y; panel.add(new JLabel("Tiết kết thúc:"), gc);
        gc.gridx = 1; panel.add(inEnd, gc);
        gc.gridx = 0; gc.gridy = ++y; panel.add(new JLabel("Phòng:"), gc);
        gc.gridx = 1; panel.add(inRoom, gc);
        gc.gridx = 0; gc.gridy = ++y; gc.gridwidth = 2; gc.anchor = GridBagConstraints.CENTER;
        panel.add(subSave, gc);

        subSave.addActionListener(this);
        add(panel);
    }

    private void fillData() {
        inDay.setSelectedItem(schedule.getDayOfWeek());
        inStart.setText(String.valueOf(schedule.getStartPeriod()));
        inEnd.setText(String.valueOf(schedule.getEndPeriod()));
        inRoom.setText(schedule.getRoom());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == subSave) {
            try {
                schedule.setDayOfWeek((String) inDay.getSelectedItem());
                schedule.setStartPeriod(Integer.parseInt(inStart.getText().trim()));
                schedule.setEndPeriod(Integer.parseInt(inEnd.getText().trim()));
                schedule.setRoom(inRoom.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Tiết học phải là số.");
                return;
            }
            boolean ok = scheduleDAO.updateSchedule(schedule);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Cập nhật lịch học thành công");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại");
            }
        }
    }
}
