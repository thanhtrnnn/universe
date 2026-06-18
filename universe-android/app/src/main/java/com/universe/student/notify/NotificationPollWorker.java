package com.universe.student.notify;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.universe.student.data.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * Chạy nền (WorkManager): gọi API student/notifications, so sánh với danh sách id
 * đã thấy và bắn system notification cho các thông báo MỚI.
 *
 * Lần chạy đầu tiên chỉ lập "baseline" (ghi nhớ tất cả id hiện có, không bắn) để
 * tránh dội một loạt thông báo cũ. Từ lần sau, chỉ thông báo mới mới ping.
 */
public final class NotificationPollWorker extends Worker {

    private static final String PREFS = "universe_notify";
    private static final String KEY_SEEN = "seen_ids";
    private static final String KEY_BASELINE = "baseline_done";
    private static final int MAX_PER_RUN = 5;

    public NotificationPollWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context ctx = getApplicationContext();
        SessionManager session = new SessionManager(ctx);
        String token = session.getToken();
        if (token == null || token.isEmpty()
                || !"Student".equalsIgnoreCase(session.getRole())) {
            return Result.success(); // chưa đăng nhập / không phải SV -> bỏ qua
        }

        String json;
        try {
            json = fetch(session.getApiBaseUrl() + "student/notifications", token);
        } catch (Exception e) {
            return Result.retry(); // lỗi mạng -> thử lại sau
        }
        if (json == null) {
            return Result.success();
        }

        SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        Set<String> seen = new HashSet<>(prefs.getStringSet(KEY_SEEN, new HashSet<>()));
        boolean firstRun = !prefs.getBoolean(KEY_BASELINE, false);

        try {
            JSONArray arr = new JSONArray(json);
            int shown = 0;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.optJSONObject(i);
                if (o == null) {
                    continue;
                }
                String id = o.optString("id");
                if (id.isEmpty() || seen.contains(id)) {
                    continue;
                }
                seen.add(id);
                if (!firstRun && shown < MAX_PER_RUN) {
                    NotificationHelper.show(ctx, id.hashCode(),
                            o.optString("title"), o.optString("content"));
                    shown++;
                }
            }
            prefs.edit()
                    .putStringSet(KEY_SEEN, seen)
                    .putBoolean(KEY_BASELINE, true)
                    .apply();
        } catch (Exception e) {
            return Result.success();
        }
        return Result.success();
    }

    private String fetch(String urlStr, String token) throws Exception {
        HttpURLConnection c = null;
        try {
            URL url = new URL(urlStr);
            c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setConnectTimeout(15_000);
            c.setReadTimeout(30_000);
            c.setRequestProperty("Accept", "application/json");
            c.setRequestProperty("Authorization", "Bearer " + token);
            int status = c.getResponseCode();
            if (status >= 400) {
                return null;
            }
            return readBody(c.getInputStream());
        } finally {
            if (c != null) {
                c.disconnect();
            }
        }
    }

    private String readBody(InputStream input) throws Exception {
        if (input == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
