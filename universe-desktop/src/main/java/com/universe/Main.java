package com.universe;

import com.universe.consumer.NotificationConsumer;
import com.universe.view.LoginFrm;

import javax.swing.*;

/**
 * Điểm khởi động ứng dụng UniVerse Desktop.
 * Mở giao diện đăng nhập và khởi động consumer Kafka nền (ghi thông báo vào DB).
 */
public class Main {

    public static void main(String[] args) {
        // Khởi động consumer Kafka ở thread nền (nếu Kafka không sẵn sàng, app vẫn chạy)
        try {
            NotificationConsumer.startInBackground();
        } catch (Exception e) {
            System.err.println("Không khởi động được NotificationConsumer: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignore) {
                // dùng look and feel mặc định
            }
            new LoginFrm().setVisible(true);
        });
    }
}
