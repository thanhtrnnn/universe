package com.universe.student;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.universe.student.data.Models;
import com.universe.student.data.SessionManager;
import com.universe.student.data.StudentRepository;
import com.universe.student.location.DeviceLocationProvider;
import com.universe.student.notify.NotificationHelper;
import com.universe.student.notify.NotificationScheduler;
import com.universe.student.ui.CardFactory;

import java.util.List;
import java.util.Locale;

public final class MainActivity extends Activity {

    private static final int REQUEST_SCAN_QR = 1001;
    private static final int REQUEST_LOCATION_PERMISSION = 1002;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1003;

    private LinearLayout contentContainer;
    private ProgressBar contentProgress;
    private ScrollView contentScroll;
    private StudentRepository repository;
    private SessionManager sessionManager;
    private DeviceLocationProvider locationProvider;
    private String pendingQrPayload;
    private AlertDialog locationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()
                || !"Student".equalsIgnoreCase(sessionManager.getRole())) {
            openLogin();
            return;
        }

        setContentView(R.layout.activity_main);
        repository = new StudentRepository(sessionManager);
        locationProvider = new DeviceLocationProvider(this);
        contentContainer = findViewById(R.id.contentContainer);
        contentProgress = findViewById(R.id.contentProgress);
        contentScroll = findViewById(R.id.contentScroll);

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText("Xin chào, " + sessionManager.getStudent().fullName);
        findViewById(R.id.logoutButton).setOnClickListener(view -> logout());
        configureNavigation();
        findViewById(R.id.scanButton).setOnClickListener(view ->
                startActivityForResult(
                        new Intent(this, QrScanActivity.class),
                        REQUEST_SCAN_QR));
        loadHome();

        // Thông báo đẩy: tạo kênh, xin quyền (Android 13+) và lập lịch kiểm tra nền.
        NotificationHelper.ensureChannel(this);
        ensureNotificationPermission();
        NotificationScheduler.start(this);
    }

    private void ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATION_PERMISSION);
        }
    }

    private void configureNavigation() {
        findViewById(R.id.navHome).setOnClickListener(view -> loadHome());
        findViewById(R.id.navRegistration).setOnClickListener(
                view -> loadRegistration(""));
        findViewById(R.id.navSchedule).setOnClickListener(view -> loadSchedule());
        findViewById(R.id.navGrades).setOnClickListener(view -> loadGrades());
        findViewById(R.id.navNotifications).setOnClickListener(
                view -> loadNotifications());
    }

    private void loadHome() {
        showLoading();
        repository.dashboard(new StudentRepository.Callback<>() {
            @Override
            public void onSuccess(Models.Dashboard dashboard) {
                showContent();
                Models.Student student = sessionManager.getStudent();
                addHeader("HÀNH TRÌNH HỌC TẬP", "Tổng quan",
                        "Thông tin học tập mới nhất của " + student.fullName);
                contentContainer.addView(CardFactory.dataCard(
                        MainActivity.this,
                        student.fullName + " · " + student.id,
                        student.major + " · " + student.className,
                        student.course + " · " + student.email));

                LinearLayout stats = new LinearLayout(MainActivity.this);
                stats.setOrientation(LinearLayout.HORIZONTAL);
                stats.setWeightSum(3f);
                stats.addView(CardFactory.statCard(MainActivity.this,
                        String.valueOf(dashboard.enrolledCount), "LỚP ĐÃ ĐĂNG KÝ", false));
                stats.addView(CardFactory.statCard(MainActivity.this,
                        String.format(Locale.US, "%.2f", dashboard.averageScore),
                        "ĐIỂM TRUNG BÌNH", false));
                stats.addView(CardFactory.statCard(MainActivity.this,
                        String.valueOf(dashboard.notificationCount), "THÔNG BÁO", true));
                contentContainer.addView(stats, marginParams(0, 16, 0, 22));

                addSectionTitle("Lịch học sắp tới");
                if (dashboard.upcomingClasses.isEmpty()) {
                    addEmptyText("Chưa có lịch học sắp tới.");
                } else {
                    for (Models.ScheduleEntry entry : dashboard.upcomingClasses) {
                        contentContainer.addView(scheduleCard(entry));
                    }
                }
            }

            @Override
            public void onError(String message) {
                showError(message, MainActivity.this::loadHome);
            }
        });
    }

    private void loadRegistration(String keyword) {
        showLoading();
        repository.enrollments(new StudentRepository.Callback<>() {
            @Override
            public void onSuccess(List<Models.Enrollment> enrollments) {
                repository.courses(keyword, new StudentRepository.Callback<>() {
                    @Override
                    public void onSuccess(List<Models.Course> courses) {
                        renderRegistration(keyword, enrollments, courses);
                    }

                    @Override
                    public void onError(String message) {
                        showError(message, () -> loadRegistration(keyword));
                    }
                });
            }

            @Override
            public void onError(String message) {
                showError(message, () -> loadRegistration(keyword));
            }
        });
    }

    private void renderRegistration(String keyword, List<Models.Enrollment> enrollments,
                                    List<Models.Course> courses) {
        showContent();
        addHeader("HỌC PHẦN ĐANG MỞ", "Đăng ký lớp học phần",
                "Tìm học phần và chọn lớp phù hợp.");

        LinearLayout searchRow = new LinearLayout(this);
        searchRow.setGravity(Gravity.CENTER_VERTICAL);
        searchRow.setOrientation(LinearLayout.HORIZONTAL);
        EditText searchInput = new EditText(this);
        searchInput.setHint(R.string.search_hint);
        searchInput.setSingleLine(true);
        searchInput.setInputType(InputType.TYPE_CLASS_TEXT);
        searchInput.setText(keyword);
        searchInput.setTextColor(getColor(R.color.slate_900));
        searchInput.setHintTextColor(getColor(R.color.slate_400));
        searchRow.addView(searchInput, new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        Button searchButton = styledButton(getString(R.string.search));
        searchButton.setOnClickListener(view -> loadRegistration(textOf(searchInput)));
        LinearLayout.LayoutParams searchButtonParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        searchButtonParams.setMargins(dp(10), 0, 0, 0);
        searchRow.addView(searchButton, searchButtonParams);
        contentContainer.addView(searchRow, marginParams(0, 0, 0, 20));

        addSectionTitle("Học phần đã đăng ký");
        if (enrollments.isEmpty()) {
            addEmptyText("Bạn chưa đăng ký lớp học phần nào.");
        } else {
            for (Models.Enrollment enrollment : enrollments) {
                contentContainer.addView(CardFactory.dataCard(
                        this,
                        enrollment.classCode + " · " + enrollment.className,
                        "Ngày đăng ký: " + enrollment.enrolledAt,
                        enrollment.status));
            }
        }

        addSectionTitle("Danh sách học phần");
        if (courses.isEmpty()) {
            addEmptyText("Không tìm thấy học phần phù hợp.");
        } else {
            for (Models.Course course : courses) {
                View card = CardFactory.dataCard(
                        this,
                        course.id + " · " + course.name,
                        course.credits + " tín chỉ · " + course.department,
                        course.enrolled ? "Đã đăng ký một lớp của học phần" : "Có thể đăng ký");
                CardFactory.setAction(card, "Xem lớp", view -> showSections(course), true);
                contentContainer.addView(card);
            }
        }
    }

    private void showSections(Models.Course course) {
        repository.sections(course.id, new StudentRepository.Callback<>() {
            @Override
            public void onSuccess(List<Models.ClassSection> sections) {
                LinearLayout list = new LinearLayout(MainActivity.this);
                list.setOrientation(LinearLayout.VERTICAL);
                list.setPadding(dp(16), dp(12), dp(16), dp(12));
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(course.id + " · " + course.name)
                        .setView(wrapInScroll(list))
                        .setNegativeButton("Đóng", null)
                        .create();

                if (sections.isEmpty()) {
                    list.addView(messageText("Học phần chưa có lớp đang mở."));
                } else {
                    for (Models.ClassSection section : sections) {
                        View card = CardFactory.dataCard(
                                MainActivity.this,
                                section.code + " · " + section.name,
                                section.lecturer + " · " + section.semester + " " + section.year,
                                section.enrolled + "/" + section.capacity
                                        + " sinh viên · " + section.status);
                        boolean canRegister = !section.registered
                                && "open".equalsIgnoreCase(section.status)
                                && section.enrolled < section.capacity;
                        CardFactory.setAction(
                                card,
                                section.registered ? "Đã đăng ký" : "Đăng ký",
                                view -> registerSection(section, dialog),
                                canRegister);
                        list.addView(card);
                    }
                }
                dialog.show();
            }

            @Override
            public void onError(String message) {
                showDialog("Không thể tải danh sách lớp", message);
            }
        });
    }

    private void registerSection(Models.ClassSection section, AlertDialog dialog) {
        repository.register(section.id, new StudentRepository.Callback<>() {
            @Override
            public void onSuccess(String message) {
                dialog.dismiss();
                showDialog("Đăng ký thành công", message);
                loadRegistration("");
            }

            @Override
            public void onError(String message) {
                showDialog("Không thể đăng ký", message);
            }
        });
    }

    private void loadSchedule() {
        showLoading();
        repository.schedule(new StudentRepository.Callback<>() {
            @Override
            public void onSuccess(List<Models.ScheduleEntry> schedule) {
                showContent();
                addHeader("THỜI KHÓA BIỂU", "Lịch học",
                        "Lịch của tất cả lớp học phần đã đăng ký.");
                if (schedule.isEmpty()) {
                    addEmptyText("Bạn chưa có lịch học.");
                } else {
                    for (Models.ScheduleEntry entry : schedule) {
                        contentContainer.addView(scheduleCard(entry));
                    }
                }
            }

            @Override
            public void onError(String message) {
                showError(message, MainActivity.this::loadSchedule);
            }
        });
    }

    private View scheduleCard(Models.ScheduleEntry entry) {
        return CardFactory.dataCard(
                this,
                entry.classCode + " · " + entry.className,
                entry.dayOfWeek + " · tiết " + entry.startPeriod + "-" + entry.endPeriod
                        + " · phòng " + entry.room,
                entry.appliedFrom + " → " + entry.appliedTo);
    }

    private void loadGrades() {
        showLoading();
        repository.grades(new StudentRepository.Callback<>() {
            @Override
            public void onSuccess(List<Models.Grade> grades) {
                showContent();
                addHeader("KẾT QUẢ HỌC TẬP", "Bảng điểm",
                        "Điểm thành phần và điểm tổng kết của bạn.");

                int passed = 0;
                int graded = 0;
                double totalScore = 0;
                for (Models.Grade grade : grades) {
                    if (grade.totalScore == null) {
                        continue;
                    }
                    graded++;
                    totalScore += grade.totalScore;
                    if (grade.totalScore >= 4) {
                        passed++;
                    }
                }
                double average = graded == 0 ? 0 : totalScore / graded;
                LinearLayout stats = new LinearLayout(MainActivity.this);
                stats.setOrientation(LinearLayout.HORIZONTAL);
                stats.setWeightSum(3f);
                stats.addView(CardFactory.statCard(MainActivity.this,
                        String.valueOf(grades.size()), "MÔN ĐÃ ĐĂNG KÝ", false));
                stats.addView(CardFactory.statCard(MainActivity.this,
                        String.valueOf(passed), "MÔN ĐÃ QUA", false));
                stats.addView(CardFactory.statCard(MainActivity.this,
                        String.format(Locale.US, "%.2f", average), "ĐIỂM TRUNG BÌNH", true));
                contentContainer.addView(stats, marginParams(0, 0, 0, 20));

                if (grades.isEmpty()) {
                    addEmptyText("Chưa có dữ liệu điểm.");
                } else {
                    for (Models.Grade grade : grades) {
                        String componentScores = "CC: " + score(grade.score1)
                                + " · GK: " + score(grade.score2)
                                + " · TP3: " + score(grade.score3)
                                + " · Thi: " + score(grade.examScore);
                        String result = grade.totalScore == null
                                ? "Chưa có điểm tổng kết"
                                : "Tổng kết: " + score(grade.totalScore)
                                + (grade.totalScore >= 4 ? " · PASSED" : " · FAILED");
                        contentContainer.addView(CardFactory.dataCard(
                                MainActivity.this,
                                grade.classCode + " · " + grade.className,
                                componentScores,
                                result));
                    }
                }
            }

            @Override
            public void onError(String message) {
                showError(message, MainActivity.this::loadGrades);
            }
        });
    }

    private void loadNotifications() {
        showLoading();
        repository.notifications(new StudentRepository.Callback<>() {
            @Override
            public void onSuccess(List<Models.NotificationItem> notifications) {
                showContent();
                addHeader("TIN TỨC VÀ CẬP NHẬT", "Thông báo",
                        "Thông báo mới nhất từ giảng viên và hệ thống.");
                if (notifications.isEmpty()) {
                    addEmptyText("Bạn chưa có thông báo.");
                } else {
                    for (Models.NotificationItem notification : notifications) {
                        contentContainer.addView(CardFactory.dataCard(
                                MainActivity.this,
                                notification.title,
                                notification.content,
                                notification.sentAt));
                    }
                }
            }

            @Override
            public void onError(String message) {
                showError(message, MainActivity.this::loadNotifications);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_SCAN_QR || resultCode != RESULT_OK || data == null) {
            return;
        }
        handleScannedCode(data.getStringExtra(QrScanActivity.EXTRA_QR_PAYLOAD));
    }

    private void handleScannedCode(String rawValue) {
        if (rawValue == null || !rawValue.startsWith("UNIVERSE|")) {
            showDialog("Mã QR không hợp lệ", "Đây không phải mã điểm danh UniVerse.");
            return;
        }
        pendingQrPayload = rawValue;
        if (hasPreciseLocationPermission()) {
            requestLocationAndAttend();
        } else {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_LOCATION_PERMISSION) {
            return;
        }
        if (hasPreciseLocationPermission()) {
            requestLocationAndAttend();
        } else {
            pendingQrPayload = null;
            showPreciseLocationPermissionDialog();
        }
    }

    private boolean hasPreciseLocationPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationAndAttend() {
        locationDialog = new AlertDialog.Builder(this)
                .setTitle("Đang lấy GPS chính xác")
                .setMessage("Geofence có bán kính 50 m. Quá trình có thể mất tối đa "
                        + "30 giây; hãy bật Vị trí chính xác và giữ điện thoại gần cửa sổ.")
                .setCancelable(false)
                .show();
        locationProvider.getCurrentLocation(new DeviceLocationProvider.Callback() {
            @Override
            public void onLocation(Location location) {
                dismissLocationDialog();
                submitAttendance(location);
            }

            @Override
            public void onError(String message) {
                dismissLocationDialog();
                pendingQrPayload = null;
                showDialog("Không lấy được vị trí", message);
            }
        });
    }

    private void showPreciseLocationPermissionDialog() {
        if (isFinishing()) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Cần Vị trí chính xác")
                .setMessage("Điểm danh geofence 50 m không thể dùng vị trí gần đúng. "
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

    private void submitAttendance(Location location) {
        String payload = pendingQrPayload;
        pendingQrPayload = null;
        if (payload == null) {
            return;
        }
        repository.markAttendance(
                payload,
                location.getLatitude(),
                location.getLongitude(),
                location.getAccuracy(),
                new StudentRepository.Callback<>() {
                    @Override
                    public void onSuccess(Models.AttendanceReceipt receipt) {
                        String detail = receipt.message + "\n\n"
                                + receipt.classCode + " · " + receipt.className + "\n"
                                + "Phòng " + receipt.room + "\n"
                                + receipt.attendedAt;
                        showDialog(receipt.alreadyMarked
                                ? "Đã điểm danh trước đó"
                                : "Điểm danh thành công", detail);
                    }

                    @Override
                    public void onError(String message) {
                        showDialog("Điểm danh thất bại", message);
                    }
                });
    }

    private void logout() {
        repository.logout(new StudentRepository.Callback<>() {
            @Override
            public void onSuccess(String value) {
                clearSessionAndExit();
            }

            @Override
            public void onError(String message) {
                clearSessionAndExit();
            }
        });
    }

    private void clearSessionAndExit() {
        NotificationScheduler.stop(this);
        sessionManager.clear();
        openLogin();
    }

    private void openLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading() {
        contentContainer.removeAllViews();
        contentProgress.setVisibility(View.VISIBLE);
    }

    private void showContent() {
        contentProgress.setVisibility(View.GONE);
        contentContainer.removeAllViews();
        contentScroll.scrollTo(0, 0);
    }

    private void showError(String message, Runnable retry) {
        showContent();
        addHeader("KHÔNG THỂ TẢI DỮ LIỆU", "Có lỗi xảy ra", message);
        Button button = styledButton(getString(R.string.retry));
        button.setOnClickListener(view -> retry.run());
        contentContainer.addView(button);
    }

    private void addHeader(String kicker, String title, String subtitle) {
        TextView kickerView = messageText(kicker);
        kickerView.setTextColor(getColor(R.color.cyan_700));
        kickerView.setTextSize(12);
        kickerView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        contentContainer.addView(kickerView);

        TextView titleView = messageText(title);
        titleView.setTextSize(28);
        titleView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        titleView.setTextColor(getColor(R.color.slate_900));
        contentContainer.addView(titleView, marginParams(0, 5, 0, 6));

        TextView subtitleView = messageText(subtitle);
        subtitleView.setTextColor(getColor(R.color.slate_600));
        contentContainer.addView(subtitleView, marginParams(0, 0, 0, 22));
    }

    private void addSectionTitle(String title) {
        TextView titleView = messageText(title);
        titleView.setTextColor(getColor(R.color.slate_900));
        titleView.setTextSize(19);
        titleView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        contentContainer.addView(titleView, marginParams(0, 18, 0, 12));
    }

    private void addEmptyText(String message) {
        TextView empty = messageText(message);
        empty.setGravity(Gravity.CENTER);
        empty.setTextColor(getColor(R.color.slate_600));
        empty.setPadding(dp(12), dp(28), dp(12), dp(28));
        contentContainer.addView(empty);
    }

    private TextView messageText(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(getColor(R.color.slate_600));
        view.setTextSize(14);
        return view;
    }

    private Button styledButton(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextColor(getColor(R.color.white));
        button.setBackground(getDrawable(R.drawable.button_primary));
        return button;
    }

    private ScrollView wrapInScroll(View child) {
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(child);
        return scrollView;
    }

    private String textOf(EditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    private String score(Double value) {
        return value == null ? "-" : String.format(Locale.US, "%.2f", value);
    }

    private LinearLayout.LayoutParams marginParams(int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(dp(left), dp(top), dp(right), dp(bottom));
        return params;
    }

    private void showDialog(String title, String message) {
        if (isFinishing()) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Đóng", null)
                .show();
    }

    private void dismissLocationDialog() {
        if (locationDialog != null) {
            locationDialog.dismiss();
            locationDialog = null;
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
