package com.universe.student;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.universe.student.data.SessionManager;
import com.universe.student.data.LecturerRepository;
import com.universe.student.data.StudentRepository;
import com.universe.student.location.DeviceLocationProvider;

import java.util.Locale;

public final class LocationCalibrationActivity extends Activity {

    private static final int REQUEST_SCAN_QR = 3001;
    private static final int REQUEST_LOCATION_PERMISSION = 3002;
    private static final float MAX_CALIBRATION_ACCURACY_METERS = 50f;
    private static final String PAYLOAD_PREFIX = "UNIVERSE_LOCATION|";

    private Button scanButton;
    private ProgressBar progressBar;
    private TextView statusText;
    private LecturerRepository repository;
    private DeviceLocationProvider locationProvider;
    private String calibrationToken;
    private boolean completed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_calibration);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()
                || !"Lecturer".equalsIgnoreCase(sessionManager.getRole())) {
            finish();
            return;
        }
        repository = new LecturerRepository(sessionManager);
        locationProvider = new DeviceLocationProvider(this);
        scanButton = findViewById(R.id.scanCalibrationButton);
        progressBar = findViewById(R.id.calibrationProgress);
        statusText = findViewById(R.id.calibrationStatus);

        TextView apiAddress = findViewById(R.id.calibrationApiAddress);
        apiAddress.setText("API: " + sessionManager.getApiBaseUrl());
        scanButton.setOnClickListener(view -> {
            if (completed) {
                finish();
            } else {
                scanCalibrationQr();
            }
        });
        findViewById(R.id.closeCalibrationButton).setOnClickListener(view -> finish());
    }

    private void scanCalibrationQr() {
        Intent intent = new Intent(this, QrScanActivity.class);
        intent.putExtra(
                QrScanActivity.EXTRA_INSTRUCTION,
                "Quét QR lấy vị trí đang hiển thị trên desktop");
        startActivityForResult(intent, REQUEST_SCAN_QR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_SCAN_QR || resultCode != RESULT_OK || data == null) {
            return;
        }
        String payload = data.getStringExtra(QrScanActivity.EXTRA_QR_PAYLOAD);
        if (payload == null || !payload.startsWith(PAYLOAD_PREFIX)) {
            showError("Đây không phải QR lấy vị trí của UniVerse Desktop.");
            return;
        }
        String token = payload.substring(PAYLOAD_PREFIX.length()).trim();
        if (!token.matches("[A-Za-z0-9_-]{20,128}")) {
            showError("QR lấy vị trí không hợp lệ.");
            return;
        }
        calibrationToken = token;
        requestPreciseLocation();
    }

    private void requestPreciseLocation() {
        if (hasPreciseLocationPermission()) {
            locateAndSubmit();
            return;
        }
        requestPermissions(
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                REQUEST_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_LOCATION_PERMISSION) {
            return;
        }
        if (hasPreciseLocationPermission()) {
            locateAndSubmit();
        } else {
            calibrationToken = null;
            showPreciseLocationPermissionDialog();
        }
    }

    private boolean hasPreciseLocationPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void locateAndSubmit() {
        setLoading(true);
        setStatus(
                "Đang đo GPS. Giữ điện thoại tại vị trí giảng viên đứng và chờ tối đa "
                        + "30 giây...",
                R.color.cyan_700);
        locationProvider.getCurrentLocation(
                MAX_CALIBRATION_ACCURACY_METERS,
                new DeviceLocationProvider.Callback() {
                    @Override
                    public void onLocation(Location location) {
                        submitLocation(location);
                    }

                    @Override
                    public void onError(String message) {
                        calibrationToken = null;
                        setLoading(false);
                        showError(message);
                    }
                });
    }

    private void submitLocation(Location location) {
        setStatus(
                String.format(
                        Locale.US,
                        "Đã đạt sai số %.0f m. Đang gửi vị trí về desktop...",
                        location.getAccuracy()),
                R.color.cyan_700);
        repository.submitLocationCalibration(
                calibrationToken,
                location.getLatitude(),
                location.getLongitude(),
                location.getAccuracy(),
                new StudentRepository.Callback<>() {
                    @Override
                    public void onSuccess(String message) {
                        calibrationToken = null;
                        completed = true;
                        setLoading(false);
                        scanButton.setText("Hoàn tất");
                        setStatus(
                                message + "\nDesktop đã tự nhận tọa độ; bạn có thể đóng màn hình này.",
                                R.color.green_400);
                    }

                    @Override
                    public void onError(String message) {
                        calibrationToken = null;
                        setLoading(false);
                        showError(message);
                    }
                });
    }

    private void setLoading(boolean loading) {
        scanButton.setEnabled(!loading);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        setStatus(message, R.color.red_400);
    }

    private void setStatus(String message, int color) {
        statusText.setText(message);
        statusText.setTextColor(getColor(color));
        statusText.setVisibility(View.VISIBLE);
    }

    private void showPreciseLocationPermissionDialog() {
        if (isFinishing()) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Cần Vị trí chính xác")
                .setMessage("Đặt tâm geofence yêu cầu GPS sai số tối đa 50 m. "
                        + "Hãy mở quyền ứng dụng, chọn Vị trí và bật "
                        + "\"Sử dụng vị trí chính xác\".")
                .setNegativeButton("Đóng", null)
                .setPositiveButton("Mở cài đặt", (dialog, which) -> {
                    Intent intent = new Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                })
                .show();
    }
}
