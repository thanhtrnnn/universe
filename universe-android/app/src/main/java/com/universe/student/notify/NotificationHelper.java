package com.universe.student.notify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.universe.student.R;

/**
 * Tạo notification channel và hiển thị system notification trên thanh trạng thái.
 */
public final class NotificationHelper {

    public static final String CHANNEL_ID = "universe_notifications";
    private static final String CHANNEL_NAME = "Thông báo UniVerse";

    private NotificationHelper() {
    }

    /** Tạo channel (bắt buộc từ Android 8). Gọi nhiều lần an toàn. */
    public static void ensureChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Thông báo từ giảng viên và nhà trường");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /** Hiện 1 thông báo hệ thống. {@code notifId} nên ổn định để không trùng/nhân đôi. */
    public static void show(Context context, int notifId, String title, String content) {
        ensureChannel(context);
        String safeTitle = (title == null || title.isEmpty()) ? "Thông báo" : title;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(safeTitle)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        try {
            NotificationManagerCompat.from(context).notify(notifId, builder.build());
        } catch (SecurityException ignored) {
            // Chưa được cấp quyền POST_NOTIFICATIONS (Android 13+) -> bỏ qua êm.
        }
    }
}
