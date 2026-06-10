package com.universe;

import com.universe.consumer.NotificationConsumer;
import com.universe.view.LoginFrm;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Điểm khởi động ứng dụng UniVerse Desktop (JavaFX).
 * Mở giao diện đăng nhập và khởi động consumer Kafka nền (ghi thông báo vào DB).
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        new LoginFrm().show();
    }

    public static void main(String[] args) {
        // Khởi tạo CSDL nếu cần
        com.universe.util.DatabaseSeeder.seedIfNeeded();

        // Khởi động consumer Kafka ở thread nền (nếu Kafka không sẵn sàng, app vẫn chạy)
        try {
            NotificationConsumer.startInBackground();
        } catch (Exception e) {
            System.err.println("Không khởi động được NotificationConsumer: " + e.getMessage());
        }

        launch(args);
    }
}
