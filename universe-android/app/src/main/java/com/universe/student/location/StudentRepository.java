package com.universe.student.data;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class StudentRepository {

    public interface Callback<T> {
        void onSuccess(T value);

        void onError(String message);
    }

    private final ApiClient apiClient;
    private final SessionManager sessionManager;

    public StudentRepository(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.apiClient = new ApiClient(sessionManager);
    }

    public void checkConnection(Callback<String> callback) {
        apiClient.getObject("health", "", adapt(callback,
                json -> json.optString("status", "unknown")));
    }

    public void login(String username, String password, Callback<Models.LoginResult> callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("username", username);
            body.put("password", password);
        } catch (JSONException ex) {
            callback.onError("Không thể tạo yêu cầu đăng nhập.");
            return;
        }

        apiClient.post("auth/login", "", body, adapt(callback, json -> {
            JSONObject profile = json.getJSONObject("profile");
            Models.LoginResult result = new Models.LoginResult(
                    json.getString("token"),
                    parseProfile(profile, json.optString("role")));
            sessionManager.save(result);
            return result;
        }));
    }

    public void logout(Callback<String> callback) {
        apiClient.post("auth/logout", token(), new JSONObject(),
                adapt(callback, json -> json.optString("message", "Đã đăng xuất.")));
    }

    public void dashboard(Callback<Models.Dashboard> callback) {
        apiClient.getObject("student/dashboard", token(), adapt(callback, json -> {
            List<Models.ScheduleEntry> upcoming = parseSchedule(
                    json.optJSONArray("upcomingClasses"));
            return new Models.Dashboard(
                    json.optInt("enrolledCount"),
                    json.optDouble("averageScore"),
                    json.optInt("notificationCount"),
                    upcoming);
        }));
    }

    public void courses(String keyword, Callback<List<Models.Course>> callback) {
        String encoded = Uri.encode(keyword == null ? "" : keyword);
        apiClient.getArray("student/courses?q=" + encoded, token(),
                adaptArray(callback, this::parseCourses));
    }

    public void sections(String courseId, Callback<List<Models.ClassSection>> callback) {
        apiClient.getArray("student/courses/" + encodePath(courseId) + "/sections", token(),
                adaptArray(callback, this::parseSections));
    }

    public void register(String sectionId, Callback<String> callback) {
        apiClient.post("student/sections/" + encodePath(sectionId) + "/register",
                token(), new JSONObject(),
                adapt(callback, json -> json.optString("message", "Đăng ký thành công.")));
    }

    public void enrollments(Callback<List<Models.Enrollment>> callback) {
        apiClient.getArray("student/enrollments", token(),
                adaptArray(callback, this::parseEnrollments));
    }

    public void schedule(Callback<List<Models.ScheduleEntry>> callback) {
        apiClient.getArray("student/schedule", token(),
                adaptArray(callback, this::parseSchedule));
    }

    public void grades(Callback<List<Models.Grade>> callback) {
        apiClient.getArray("student/grades", token(),
                adaptArray(callback, this::parseGrades));
    }

    public void notifications(Callback<List<Models.NotificationItem>> callback) {
        apiClient.getArray("student/notifications", token(),
                adaptArray(callback, this::parseNotifications));
    }

    public void markAttendance(String qrPayload, double latitude, double longitude,
                               float accuracy, Callback<Models.AttendanceReceipt> callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("qrPayload", qrPayload);
            body.put("latitude", latitude);
            body.put("longitude", longitude);
            body.put("accuracy", accuracy);
        } catch (JSONException ex) {
            callback.onError("Không thể tạo yêu cầu điểm danh.");
            return;
        }

        apiClient.post("student/attendance/scan", token(), body,
                adapt(callback, json -> new Models.AttendanceReceipt(
                        json.optBoolean("alreadyMarked"),
                        json.optString("message"),
                        json.optString("classCode"),
                        json.optString("className"),
                        json.optString("room"),
                        json.optString("attendedAt"))));
    }

    private String token() {
        return sessionManager.getToken();
    }

    private Models.Student parseStudent(JSONObject json) {
        return new Models.Student(
                json.optString("id"),
                json.optString("fullName"),
                json.optString("email"),
                json.optString("major"),
                json.optString("className"),
                json.optString("course"));
    }

    private Models.UserProfile parseProfile(JSONObject json, String responseRole) {
        return new Models.UserProfile(
                json.optString("id"),
                json.optString("fullName"),
                json.optString("email"),
                json.optString("role", responseRole),
                json.optString("course"),
                json.optString("major"),
                json.optString("className"),
                json.optString("department"),
                json.optString("degree"));
    }

    private List<Models.Course> parseCourses(JSONArray array) throws JSONException {
        List<Models.Course> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            result.add(new Models.Course(
                    item.getString("id"),
                    item.getString("name"),
                    item.optInt("credits"),
                    item.optString("department"),
                    item.optBoolean("enrolled")));
        }
        return result;
    }

    private List<Models.ClassSection> parseSections(JSONArray array) throws JSONException {
        List<Models.ClassSection> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            result.add(new Models.ClassSection(
                    item.getString("id"),
                    item.optString("code"),
                    item.optString("name"),
                    item.optString("semester"),
                    item.optString("year"),
                    item.optString("lecturer"),
                    item.optString("status"),
                    item.optInt("enrolled"),
                    item.optInt("capacity"),
                    item.optBoolean("registered")));
        }
        return result;
    }

    private List<Models.Enrollment> parseEnrollments(JSONArray array) throws JSONException {
        List<Models.Enrollment> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            result.add(new Models.Enrollment(
                    item.optString("classCode"),
                    item.optString("className"),
                    item.optString("enrolledAt"),
                    item.optString("status")));
        }
        return result;
    }

    private List<Models.ScheduleEntry> parseSchedule(JSONArray array) throws JSONException {
        List<Models.ScheduleEntry> result = new ArrayList<>();
        if (array == null) {
            return result;
        }
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            result.add(new Models.ScheduleEntry(
                    item.optString("classCode"),
                    item.optString("className"),
                    item.optString("dayOfWeek"),
                    item.optInt("startPeriod"),
                    item.optInt("endPeriod"),
                    item.optString("room"),
                    item.optString("appliedFrom"),
                    item.optString("appliedTo")));
        }
        return result;
    }

    private List<Models.Grade> parseGrades(JSONArray array) throws JSONException {
        List<Models.Grade> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            result.add(new Models.Grade(
                    item.optString("classCode"),
                    item.optString("className"),
                    nullableDouble(item, "score1"),
                    nullableDouble(item, "score2"),
                    nullableDouble(item, "score3"),
                    nullableDouble(item, "examScore"),
                    nullableDouble(item, "totalScore")));
        }
        return result;
    }

    private List<Models.NotificationItem> parseNotifications(JSONArray array)
            throws JSONException {
        List<Models.NotificationItem> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            result.add(new Models.NotificationItem(
                    item.optString("id"),
                    item.optString("title"),
                    item.optString("content"),
                    item.optString("sentAt")));
        }
        return result;
    }

    private Double nullableDouble(JSONObject object, String key) {
        return object.isNull(key) || !object.has(key) ? null : object.optDouble(key);
    }

    private String encodePath(String value) {
        return Uri.encode(value);
    }

    private <T> ApiClient.Callback<JSONObject> adapt(
            Callback<T> callback, JsonMapper<JSONObject, T> mapper) {
        return new ApiClient.Callback<>() {
            @Override
            public void onSuccess(JSONObject value) {
                try {
                    callback.onSuccess(mapper.map(value));
                } catch (JSONException ex) {
                    callback.onError("API trả về dữ liệu không đầy đủ.");
                }
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        };
    }

    private <T> ApiClient.Callback<JSONArray> adaptArray(
            Callback<T> callback, JsonMapper<JSONArray, T> mapper) {
        return new ApiClient.Callback<>() {
            @Override
            public void onSuccess(JSONArray value) {
                try {
                    callback.onSuccess(mapper.map(value));
                } catch (JSONException ex) {
                    callback.onError("API trả về dữ liệu không đầy đủ.");
                }
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        };
    }

    private interface JsonMapper<S, T> {
        T map(S source) throws JSONException;
    }
}
