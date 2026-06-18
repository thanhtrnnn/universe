package com.universe.consumer;

import com.universe.dao.NotificationDAO;
import com.universe.entity.Notification;
import com.universe.util.AppConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * Tiến trình nền đọc topic universe.notifications và ghi từng thông báo vào
 * tblNotification. Đây là mắt xích còn thiếu của luồng Kafka (desktop không có
 * server-side process nào khác để host consumer).
 *
 * Chạy độc lập:  mvn exec:java -Dexec.mainClass="com.universe.consumer.NotificationConsumer"
 * Hoặc nhúng qua {@link #startInBackground()} khi app khởi động.
 */
public class NotificationConsumer {

    private static final NotificationDAO NOTIFICATION_DAO = new NotificationDAO();
    private static volatile boolean running = true;

    public static void main(String[] args) {
        System.out.println("[NotificationConsumer] Bắt đầu lắng nghe topic " + KafkaTopic());
        run();
    }

    /** Khởi động consumer trên thread daemon (nhúng vào app desktop). */
    public static void startInBackground() {
        if (!com.universe.util.KafkaUtil.enabled()) {
            System.out.println("[NotificationConsumer] Kafka tắt (kafka.enabled=false) - "
                    + "bỏ qua consumer; thông báo được ghi thẳng vào DB.");
            return;
        }
        System.out.println("[NotificationConsumer] Kafka bật - khởi động consumer nền, "
                + "lắng nghe topic " + KafkaTopic()
                + " @ " + AppConfig.get("kafka.bootstrap", "localhost:9092"));
        Thread t = new Thread(NotificationConsumer::run, "notification-consumer");
        t.setDaemon(true);
        t.start();
    }

    public static void stop() {
        running = false;
    }

    private static String KafkaTopic() {
        return AppConfig.get("kafka.topic.notifications", "universe.notifications");
    }

    private static void run() {
        Properties props = new Properties();
        props.put("bootstrap.servers", AppConfig.get("kafka.bootstrap", "localhost:9092"));
        props.put("group.id", AppConfig.get("kafka.consumer.group", "universe-notification-consumer"));
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", "true");
        com.universe.util.KafkaUtil.applySecurity(props);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(KafkaTopic()));
            System.out.println("[NotificationConsumer] Đã kết nối broker & subscribe topic "
                    + KafkaTopic() + " - sẵn sàng nhận thông báo.");
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        Notification n = NotificationDAO.fromJson(record.value());
                        NOTIFICATION_DAO.insert(n);
                        System.out.println("[NotificationConsumer] Đã ghi thông báo " + n.getId()
                                + " cho user " + n.getUserId());
                    } catch (Exception e) {
                        System.err.println("[NotificationConsumer] Lỗi xử lý message: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[NotificationConsumer] Dừng do lỗi: " + e.getMessage());
        }
    }
}
