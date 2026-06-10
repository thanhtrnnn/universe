package com.universe.view;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.universe.dao.ClassSectionDAO;
import com.universe.dao.ClassSessionDAO;
import com.universe.dao.QRCodeDAO;
import com.universe.entity.ClassSection;
import com.universe.entity.ClassSession;
import com.universe.entity.QRCode;
import com.universe.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Giao diện tạo/tắt mã QR điểm danh (Chương 4.3.1.7, đã chuẩn hoá tên lớp).
 * btnSearchClass → searchClassSection(); btnGenerateQr → createSession()+generateQr();
 * btnDeactivateQr → deactivateQr().
 */
public class QRCodeFrm extends JFrame implements ActionListener {

    private final User currentUser;
    private final ClassSectionDAO classSectionDAO = new ClassSectionDAO();
    private final ClassSessionDAO classSessionDAO = new ClassSessionDAO();
    private final QRCodeDAO qrCodeDAO = new QRCodeDAO();

    private final JTextField txtClassSection = new JTextField(16);
    private final JButton btnSearchClass = new JButton("Tìm lớp");
    private final JButton btnGenerateQr = new JButton("Tạo mã QR");
    private final JButton btnDeactivateQr = new JButton("Tắt mã QR");
    private final JTable tblClass;
    private final DefaultTableModel model;
    private final JLabel qrImageLabel = new JLabel("(Chưa có mã QR)", SwingConstants.CENTER);

    private List<ClassSection> classList;
    private ClassSession currentSession;
    private QRCode currentQr;

    public QRCodeFrm(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Tạo mã QR điểm danh");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(760, 480);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(new Object[]{"Mã lớp", "Tên lớp", "Học kỳ"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblClass = new JTable(model);

        buildUI();
    }

    private void buildUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Tìm lớp:"));
        top.add(txtClassSection);
        top.add(btnSearchClass);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actions.add(btnGenerateQr);
        actions.add(btnDeactivateQr);

        qrImageLabel.setPreferredSize(new Dimension(240, 240));
        qrImageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        for (JButton b : new JButton[]{btnSearchClass, btnGenerateQr, btnDeactivateQr}) {
            b.addActionListener(this);
        }
        txtClassSection.addActionListener(this);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(tblClass), qrImageLabel);
        split.setResizeWeight(0.55);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);
    }

    private void loadClasses(String keyword) {
        classList = classSectionDAO.searchClassSection(keyword);
        model.setRowCount(0);
        for (ClassSection c : classList) {
            model.addRow(new Object[]{c.getClassId(), c.getName(), c.getSemester()});
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnSearchClass || src == txtClassSection) {
            loadClasses(txtClassSection.getText().trim());
        } else if (src == btnGenerateQr) {
            generateQr();
        } else if (src == btnDeactivateQr) {
            deactivateQr();
        }
    }

    private void generateQr() {
        int row = tblClass.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp học phần.");
            return;
        }
        ClassSection cls = classList.get(row);
        try {
            currentSession = classSessionDAO.createSession(cls.getId());
            currentQr = qrCodeDAO.generateQr(currentSession);
            renderQrImage(currentQr.getId());
            JOptionPane.showMessageDialog(this, "Đã tạo mã QR điểm danh cho lớp " + cls.getClassId());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tạo QR: " + ex.getMessage());
        }
    }

    private void deactivateQr() {
        if (currentQr == null) {
            JOptionPane.showMessageDialog(this, "Chưa có mã QR nào đang hoạt động.");
            return;
        }
        boolean ok = qrCodeDAO.deactivateQr(currentQr.getId());
        if (ok) {
            qrImageLabel.setIcon(null);
            qrImageLabel.setText("(Đã tắt mã QR)");
            currentQr = null;
            JOptionPane.showMessageDialog(this, "Đã tắt mã QR / đóng điểm danh thành công.");
        }
    }

    /** Vẽ ảnh QR từ token bằng ZXing. */
    private void renderQrImage(String content) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 220, 220);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
            qrImageLabel.setText(null);
            qrImageLabel.setIcon(new ImageIcon(image));
        } catch (Exception ex) {
            qrImageLabel.setText("Lỗi render QR");
        }
    }
}
