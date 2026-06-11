package com.universe.student.data;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class ApiClient {

    private static final int CONNECT_TIMEOUT_MS = 15_000;
    private static final int READ_TIMEOUT_MS = 75_000;

    interface Callback<T> {
        void onSuccess(T value);

        void onError(String message);
    }

    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final SessionManager sessionManager;

    ApiClient(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    void getObject(String path, String token, Callback<JSONObject> callback) {
        request("GET", path, token, null, false, callback);
    }

    void getArray(String path, String token, Callback<JSONArray> callback) {
        request("GET", path, token, null, true, callback);
    }

    void post(String path, String token, JSONObject body, Callback<JSONObject> callback) {
        request("POST", path, token, body, false, callback);
    }

    private <T> void request(String method, String path, String token, JSONObject body,
                             boolean arrayResponse, Callback<T> callback) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sessionManager.getApiBaseUrl() + path);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(method);
                connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
                connection.setReadTimeout(READ_TIMEOUT_MS);
                connection.setRequestProperty("Accept", "application/json");
                if (token != null && !token.isEmpty()) {
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                }

                if (body != null) {
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    byte[] payload = body.toString().getBytes(StandardCharsets.UTF_8);
                    try (OutputStream output = connection.getOutputStream()) {
                        output.write(payload);
                    }
                }

                int status = connection.getResponseCode();
                String response = readBody(status >= 400
                        ? connection.getErrorStream()
                        : connection.getInputStream());
                if (status >= 400) {
                    postError(callback, extractError(response, status));
                    return;
                }

                Object parsed = arrayResponse ? new JSONArray(response) : new JSONObject(response);
                @SuppressWarnings("unchecked")
                T result = (T) parsed;
                mainHandler.post(() -> callback.onSuccess(result));
            } catch (IOException ex) {
                postError(callback,
                        "Không thể kết nối API tại " + sessionManager.getApiBaseUrl()
                                + "\n\nKiểm tra URL, mobile-api, PostgreSQL và kết nối "
                                + "giữa thiết bị với máy tính.");
            } catch (JSONException ex) {
                postError(callback, "API trả về dữ liệu không hợp lệ.");
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private String readBody(InputStream input) throws IOException {
        if (input == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString();
    }

    private String extractError(String body, int status) {
        try {
            String message = new JSONObject(body).optString("message");
            if (!message.isEmpty()) {
                return message;
            }
        } catch (JSONException ignored) {
        }
        return "Yêu cầu thất bại (HTTP " + status + ").";
    }

    private <T> void postError(Callback<T> callback, String message) {
        mainHandler.post(() -> callback.onError(message));
    }
}
