package com.universe.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.universe.student.data.Models;
import com.universe.student.data.SessionManager;
import com.universe.student.data.StudentRepository;

public final class LoginActivity extends Activity {

    private EditText usernameInput;
    private EditText passwordInput;
    private EditText apiUrlInput;
    private Button loginButton;
    private Button testConnectionButton;
    private ProgressBar progressBar;
    private TextView errorText;
    private StudentRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            openMain();
            return;
        }

        setContentView(R.layout.activity_login);
        repository = new StudentRepository(sessionManager);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        apiUrlInput = findViewById(R.id.apiUrlInput);
        loginButton = findViewById(R.id.loginButton);
        testConnectionButton = findViewById(R.id.testConnectionButton);
        progressBar = findViewById(R.id.loginProgress);
        errorText = findViewById(R.id.errorText);

        usernameInput.setText("student");
        if (BuildConfig.DEBUG) {
            // Debug build: chốt cứng API URL từ BuildConfig (Render), bỏ ô nhập
            // địa chỉ + nút test để người dùng không tự đổi connection string.
            sessionManager.setApiBaseUrl(BuildConfig.API_BASE_URL);
            apiUrlInput.setVisibility(View.GONE);
            testConnectionButton.setVisibility(View.GONE);
        } else {
            apiUrlInput.setText(sessionManager.getApiBaseUrl());
            testConnectionButton.setOnClickListener(view -> testConnection());
        }
        loginButton.setOnClickListener(view -> login());
        passwordInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
                return true;
            }
            return false;
        });
    }

    private void login() {
        String username = valueOf(usernameInput);
        String password = valueOf(passwordInput);
        if (!BuildConfig.DEBUG) {
            saveApiAddress();
        }
        errorText.setTextColor(getColor(R.color.red_400));
        usernameInput.setError(username.isEmpty() ? "Vui lòng nhập tên đăng nhập." : null);
        passwordInput.setError(password.isEmpty() ? "Vui lòng nhập mật khẩu." : null);
        errorText.setVisibility(View.GONE);
        if (username.isEmpty() || password.isEmpty()) {
            return;
        }

        setLoading(true);
        repository.login(username, password, new StudentRepository.Callback<>() {
            @Override
            public void onSuccess(Models.LoginResult value) {
                setLoading(false);
                openMain();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                errorText.setText(message);
                errorText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setLoading(boolean loading) {
        loginButton.setEnabled(!loading);
        testConnectionButton.setEnabled(!loading);
        usernameInput.setEnabled(!loading);
        passwordInput.setEnabled(!loading);
        apiUrlInput.setEnabled(!loading);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void testConnection() {
        saveApiAddress();
        errorText.setVisibility(View.GONE);
        setLoading(true);
        repository.checkConnection(new StudentRepository.Callback<>() {
            @Override
            public void onSuccess(String value) {
                setLoading(false);
                errorText.setText("Kết nối API thành công.");
                errorText.setTextColor(getColor(R.color.green_400));
                errorText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                errorText.setTextColor(getColor(R.color.red_400));
                errorText.setText(message);
                errorText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void saveApiAddress() {
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.setApiBaseUrl(valueOf(apiUrlInput));
        apiUrlInput.setText(sessionManager.getApiBaseUrl());
    }

    private String valueOf(EditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    private void openMain() {
        SessionManager sessionManager = new SessionManager(this);
        Class<?> destination = "Lecturer".equalsIgnoreCase(sessionManager.getRole())
                ? LecturerActivity.class
                : MainActivity.class;
        startActivity(new Intent(this, destination));
        finish();
    }
}
