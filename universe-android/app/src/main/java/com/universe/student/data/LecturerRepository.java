package com.universe.student.data;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class LecturerRepository {

    private final ApiClient apiClient;
    private final SessionManager sessionManager;

    public LecturerRepository(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.apiClient = new ApiClient(sessionManager);
    }

    public void dashboard(StudentRepository.Callback<Models.LecturerDashboard> callback) {
        apiClient.getObject("lecturer/dashboard", token(), adapt(callback, json ->
                new Models.LecturerDashboard(
                        json.optInt("classCount"),
                        json.optInt("studentCount"),
                        json.optInt("scheduleCount"),
                        parseClasses(json.optJSONArray("classes")))));
    }

    public void classes(StudentRepository.Callback<List<Models.LecturerClass>> callback) {
        apiClient.getArray(
                "lecturer/classes",
                token(),
                adaptArray(callback, this::parseClasses));
    }

    public void schedule(StudentRepository.Callback<List<Models.ScheduleEntry>> callback) {
        apiClient.getArray(
                "lecturer/schedule",
                token(),
                adaptArray(callback, this::parseSchedule));
    }

    public void sessions(
            String classSectionId,
            StudentRepository.Callback<List<Models.LecturerSession>> callback) {
        apiClient.getArray(
                "lecturer/classes/" + path(classSectionId) + "/sessions",
                token(),
                adaptArray(callback, this::parseSessions));
    }

    public void attendance(
            String sessionId,
            StudentRepository.Callback<List<Models.LecturerAttendance>> callback) {
        apiClient.getArray(
                "lecturer/sessions/" + path(sessionId) + "/attendance",
                token(),
                adaptArray(callback, this::parseAttendance));
    }

    public void updateAttendance(
            String sessionId,
            String studentId,
            String status,
            StudentRepository.Callback<String> callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("status", status);
        } catch (JSONException ex) {
            callback.onError("Không thể tạo yêu cầu cập nhật điểm danh.");
            return;
        }
        apiClient.post(
                "lecturer/sessions/" + path(sessionId)
                        + "/attendance/" + path(studentId),
                token(),
                body,
                adapt(callback, json ->
                        json.optString("message", "Đã cập nhật điểm danh.")));
    }

    public void grades(
            String classSectionId,
            StudentRepository.Callback<List<Models.LecturerGrade>> callback) {
        apiClient.getArray(
                "lecturer/classes/" + path(classSectionId) + "/grades",
                token(),
                adaptArray(callback, this::parseGrades));
    }

    public void updateGrade(
            Models.LecturerGrade grade,
            StudentRepository.Callback<String> callback) {
        JSONObject body = new JSONObject();
        try {
            putNullable(body, "score1", grade.score1);
            putNullable(body, "score2", grade.score2);
            putNullable(body, "score3", grade.score3);
            putNullable(body, "examScore", grade.examScore);
        } catch (JSONException ex) {
            callback.onError("Không thể tạo yêu cầu cập nhật điểm.");
            return;
        }
        apiClient.post(
                "lecturer/grades/" + path(grade.recordId),
                token(),
                body,
                adapt(callback, json ->
                        json.optString("message", "Đã cập nhật điểm.")));
    }

    public void sendNotification(
            String classSectionId,
            String title,
            String content,
            StudentRepository.Callback<String> callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("classSectionId", classSectionId);
            body.put("title", title);
            body.put("content", content);
        } catch (JSONException ex) {
            callback.onError("Không thể tạo thông báo.");
            return;
        }
        apiClient.post(
                "lecturer/notifications",
                token(),
                body,
                adapt(callback, json ->
                        json.optString("message", "Đã gửi thông báo.")));
    }

    public void submitLocationCalibration(
            String calibrationToken,
            double latitude,
            double longitude,
            float accuracy,
            StudentRepository.Callback<String> callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("latitude", latitude);
            body.put("longitude", longitude);
            body.put("accuracy", accuracy);
        } catch (JSONException ex) {
            callback.onError("Không thể tạo yêu cầu gửi vị trí.");
            return;
        }

        apiClient.post(
                "location-calibrations/" + path(calibrationToken) + "/location",
                token(),
                body,
                adapt(callback, json ->
                        json.optString("message", "Đã gửi vị trí phòng học.")));
    }

    public void logout(StudentRepository.Callback<String> callback) {
        apiClient.post(
                "auth/logout",
                token(),
                new JSONObject(),
                adapt(callback, json ->
                        json.optString("message", "Đã đăng xuất.")));
    }

    private List<Models.LecturerClass> parseClasses(JSONArray array)
            throws JSONException {
        List<Models.LecturerClass> result = new ArrayList<>();
        if (array == null) {
            return result;
        }
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            result.add(new Models.LecturerClass(
                    item.optString("id"),
                    item.optString("code"),
                    item.optString("name"),
                    item.optString("semester"),
                    item.optString("year"),
                    item.optString("status"),
                    item.optInt("enrolled"),
                    item.optInt("capacity")));
        }
        return result;
    }

    private List<Models.ScheduleEntry> parseSchedule(JSONArray array)
            throws JSONException {
        List<Models.ScheduleEntry> result = new ArrayList<>();
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

    private List<Models.LecturerSession> parseSessions(JSONArray array)
            throws JSONException {
        List<Models.LecturerSession> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            result.add(new Models.LecturerSession(
                    item.optString("id"),
                    item.optString("date"),
                    item.optInt("startPeriod"),
                    item.optInt("endPeriod"),
                    item.optString("room"),
                    item.optString("status")));
        }
        return result;
    }

    private List<Models.LecturerAttendance> parseAttendance(JSONArray array)
            throws JSONException {
        List<Models.LecturerAttendance> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            result.add(new Models.LecturerAttendance(
                    item.optString("studentId"),
                    item.optString("studentName"),
                    item.optString("attendedAt"),
                    item.optString("method"),
                    item.optString("status")));
        }
        return result;
    }

    private List<Models.LecturerGrade> parseGrades(JSONArray array)
            throws JSONException {
        List<Models.LecturerGrade> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            result.add(new Models.LecturerGrade(
                    item.optString("recordId"),
                    item.optString("studentId"),
                    item.optString("studentName"),
                    nullableDouble(item, "score1"),
                    nullableDouble(item, "score2"),
                    nullableDouble(item, "score3"),
                    nullableDouble(item, "examScore")));
        }
        return result;
    }

    private Double nullableDouble(JSONObject object, String key) {
        return !object.has(key) || object.isNull(key)
                ? null
                : object.optDouble(key);
    }

    private void putNullable(JSONObject object, String key, Double value)
            throws JSONException {
        object.put(key, value == null ? JSONObject.NULL : value);
    }

    private String token() {
        return sessionManager.getToken();
    }

    private String path(String value) {
        return Uri.encode(value);
    }

    private <T> ApiClient.Callback<JSONObject> adapt(
            StudentRepository.Callback<T> callback,
            JsonMapper<JSONObject, T> mapper) {
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
            StudentRepository.Callback<T> callback,
            JsonMapper<JSONArray, T> mapper) {
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
