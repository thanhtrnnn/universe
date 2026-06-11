package com.universe.student.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.universe.student.BuildConfig;

public final class SessionManager {

    private static final String PREFS = "universe_session";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ROLE = "role";
    private static final String KEY_ID = "student_id";
    private static final String KEY_NAME = "student_name";
    private static final String KEY_EMAIL = "student_email";
    private static final String KEY_MAJOR = "student_major";
    private static final String KEY_CLASS = "student_class";
    private static final String KEY_COURSE = "student_course";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_DEGREE = "degree";
    private static final String KEY_API_BASE_URL = "api_base_url";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void save(Models.LoginResult loginResult) {
        Models.UserProfile profile = loginResult.profile;
        preferences.edit()
                .putString(KEY_TOKEN, loginResult.token)
                .putString(KEY_ROLE, profile.role)
                .putString(KEY_ID, profile.id)
                .putString(KEY_NAME, profile.fullName)
                .putString(KEY_EMAIL, profile.email)
                .putString(KEY_MAJOR, profile.major)
                .putString(KEY_CLASS, profile.className)
                .putString(KEY_COURSE, profile.course)
                .putString(KEY_DEPARTMENT, profile.department)
                .putString(KEY_DEGREE, profile.degree)
                .apply();
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, "");
    }

    public String getApiBaseUrl() {
        return preferences.getString(KEY_API_BASE_URL, BuildConfig.API_BASE_URL);
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        preferences.edit()
                .putString(KEY_API_BASE_URL, normalizeApiBaseUrl(apiBaseUrl))
                .apply();
    }

    public boolean isLoggedIn() {
        return !getToken().isEmpty() && !getRole().isEmpty();
    }

    public String getRole() {
        return preferences.getString(KEY_ROLE, "");
    }

    public Models.UserProfile getProfile() {
        return new Models.UserProfile(
                preferences.getString(KEY_ID, ""),
                preferences.getString(KEY_NAME, ""),
                preferences.getString(KEY_EMAIL, ""),
                getRole(),
                preferences.getString(KEY_COURSE, ""),
                preferences.getString(KEY_MAJOR, ""),
                preferences.getString(KEY_CLASS, ""),
                preferences.getString(KEY_DEPARTMENT, ""),
                preferences.getString(KEY_DEGREE, ""));
    }

    public Models.Student getStudent() {
        return new Models.Student(
                preferences.getString(KEY_ID, ""),
                preferences.getString(KEY_NAME, ""),
                preferences.getString(KEY_EMAIL, ""),
                preferences.getString(KEY_MAJOR, ""),
                preferences.getString(KEY_CLASS, ""),
                preferences.getString(KEY_COURSE, ""));
    }

    public void clear() {
        String apiBaseUrl = getApiBaseUrl();
        preferences.edit().clear().putString(KEY_API_BASE_URL, apiBaseUrl).apply();
    }

    public static String normalizeApiBaseUrl(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) {
            return BuildConfig.API_BASE_URL;
        }
        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            normalized = "http://" + normalized;
        }
        if (!normalized.endsWith("/")) {
            normalized += "/";
        }
        return normalized;
    }
}
