package com.universe.student.notify;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * Lập lịch kiểm tra thông báo nền bằng WorkManager.
 *  - định kỳ mỗi 15 phút (tối thiểu của WorkManager) -> ping cả khi app đóng.
 *  - 1 lần ngay lập tức khi vào app -> demo nhanh + lập baseline lần đầu.
 */
public final class NotificationScheduler {

    private static final String PERIODIC_NAME = "universe-notif-poll";
    private static final String ONE_TIME_NAME = "universe-notif-poll-now";

    private NotificationScheduler() {
    }

    public static void start(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodic = new PeriodicWorkRequest.Builder(
                NotificationPollWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_NAME, ExistingPeriodicWorkPolicy.KEEP, periodic);

        OneTimeWorkRequest now = new OneTimeWorkRequest.Builder(NotificationPollWorker.class)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueueUniqueWork(
                ONE_TIME_NAME, ExistingWorkPolicy.REPLACE, now);
    }

    public static void stop(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_NAME);
        WorkManager.getInstance(context).cancelUniqueWork(ONE_TIME_NAME);
    }
}
