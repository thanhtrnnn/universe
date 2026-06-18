package com.universe.util;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    private static final boolean ENABLED =
            Boolean.parseBoolean(AppConfig.get("kafka.enabled", "false"));

    private static final String TOPIC =
            AppConfig.get("kafka.topic.notifications", "universe.notifications");

    private static final Producer<String, String> PRODUCER = ENABLED ? createProducerSafe() : null;

    /** CA đã trích ra file tạm (cache để khỏi trích nhiều lần). */
    private static volatile String cachedTruststore;

    private KafkaUtil() {
    }

    /**
     * Kafka có dùng được không. Bật trong cấu hình VÀ producer khởi tạo thành công.
     * Nếu init lỗi (vd thiếu CA), trả false để app tự ghi thẳng DB thay vì treo.
     */
    public static boolean enabled() {
        return ENABLED && PRODUCER != null;
    }

    /** Khởi tạo producer nhưng KHÔNG để lỗi làm chết app (trước đây throw -> Failed to launch JVM). */
    private static Producer<String, String> createProducerSafe() {
        try {
            return createProducer();
        } catch (Exception e) {
            System.err.println("[KafkaUtil] Không khởi tạo được Kafka producer: " + e.getMessage()
                    + " -> tắt Kafka, thông báo sẽ ghi thẳng vào DB.");
            return null;
        }
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
        applySecurity(props);
        return new KafkaProducer<>(props);
    }

    /**
     * Áp cấu hình SASL_SSL cho Kafka cloud nếu {@code kafka.security.protocol}
     * được khai báo trong app.properties.
     *
     * Hỗ trợ:
     *  - Confluent Cloud / Redpanda: SASL_SSL + PLAIN (CA công khai, không cần truststore).
     *  - DigitalOcean Managed Kafka: SASL_SSL + SCRAM-SHA-256 + CA riêng của DO
     *    (khai báo ssl.truststore.* trỏ tới file CA dạng PEM).
     */
    public static void applySecurity(Properties props) {
        String protocol = AppConfig.get("kafka.security.protocol", "");
        if (protocol == null || protocol.isBlank()) {
            return;
        }
        props.put("security.protocol", protocol);
        props.put("sasl.mechanism", AppConfig.get("kafka.sasl.mechanism", "PLAIN"));
        putIfPresent(props, "sasl.jaas.config", "kafka.sasl.jaas.config");
        // TLS/CA cho broker dùng CA riêng (DigitalOcean). PEM => không cần keytool/JKS.
        putIfPresent(props, "ssl.truststore.type", "kafka.ssl.truststore.type");
        String truststore = resolveTruststore();
        if (truststore != null) {
            props.put("ssl.truststore.location", truststore);
        }
        putIfPresent(props, "ssl.truststore.password", "kafka.ssl.truststore.password");
    }

    /**
     * Trả về đường dẫn file CA dùng cho TLS, theo thứ tự ưu tiên:
     *  1) {@code kafka.ssl.truststore.location} nếu file tồn tại trên đĩa (máy dev).
     *  2) Trích CA bundle ({@code kafka.ssl.truststore.resource}) từ trong jar ra file tạm
     *     -> giúp .exe/.jar chạy được trên MÁY KHÁC không có đường dẫn tuyệt đối.
     * Trả {@code null} nếu không có CA nào (bỏ qua, để Kafka tự báo lỗi nếu cần).
     */
    private static String resolveTruststore() {
        if (cachedTruststore != null) {
            return cachedTruststore;
        }
        String configured = AppConfig.get("kafka.ssl.truststore.location", "");
        if (configured != null && !configured.isBlank() && Files.exists(Paths.get(configured))) {
            cachedTruststore = configured;
            return cachedTruststore;
        }
        String resource = AppConfig.get("kafka.ssl.truststore.resource", "do-kafka-ca.crt");
        try (InputStream in = KafkaUtil.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                System.err.println("[KafkaUtil] Không tìm thấy CA '" + resource + "' trong classpath.");
                return null;
            }
            Path tmp = Files.createTempFile("do-kafka-ca", ".crt");
            tmp.toFile().deleteOnExit();
            Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
            cachedTruststore = tmp.toAbsolutePath().toString();
            return cachedTruststore;
        } catch (IOException e) {
            System.err.println("[KafkaUtil] Lỗi trích CA ra file tạm: " + e.getMessage());
            return null;
        }
    }

    private static void putIfPresent(Properties props, String kafkaKey, String configKey) {
        String v = AppConfig.get(configKey, "");
        if (v != null && !v.isBlank()) {
            props.put(kafkaKey, v);
        }
    }

    public static String topic() {
        return TOPIC;
    }

    /** Publish 1 message JSON lên topic notifications (no-op nếu Kafka tắt). */
    public static void publish(String json) {
        if (PRODUCER == null) {
            return; // Kafka tắt: thông báo đã được ghi thẳng vào DB nên bỏ qua
        }
        PRODUCER.send(new ProducerRecord<>(TOPIC, json));
        PRODUCER.flush();
    }
}
