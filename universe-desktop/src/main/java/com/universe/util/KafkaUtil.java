package com.universe.util;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * Producer Kafka cho luồng gửi thông báo.
 * {@code NotificationDAO.sendNotification()} publish payload JSON lên topic
 * {@code universe.notifications}; {@code NotificationConsumer} đọc và ghi DB.
 *
 * Lưu ý: phần BỔ SUNG ngoài thiết kế docx gốc (vốn ghi thẳng tblNotification);
 * được thêm theo yêu cầu tích hợp Kafka.
 */
public final class KafkaUtil {

    private static final String TOPIC =
            AppConfig.get("kafka.topic.notifications", "universe.notifications");

    private static final Producer<String, String> PRODUCER = createProducer();

    private KafkaUtil() {
    }

    private static Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", AppConfig.get("kafka.bootstrap", "localhost:9092"));
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        props.put("acks", "all");
        // Tránh treo UI vô hạn nếu broker chết
        props.put("max.block.ms", "5000");
        props.put("delivery.timeout.ms", "10000");
        props.put("request.timeout.ms", "5000");
        return new KafkaProducer<>(props);
    }

    public static String topic() {
        return TOPIC;
    }

    /** Publish 1 message JSON lên topic notifications. */
    public static void publish(String json) {
        PRODUCER.send(new ProducerRecord<>(TOPIC, json));
        PRODUCER.flush();
    }
}
