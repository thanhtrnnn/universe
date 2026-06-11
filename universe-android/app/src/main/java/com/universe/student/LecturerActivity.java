package com.universe.student;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.universe.student.data.LecturerRepository;
import com.universe.student.data.Models;
import com.universe.student.data.SessionManager;
import com.universe.student.data.StudentRepository;
import com.universe.student.ui.CardFactory;

import java.util.List;
import java.util.Locale;

public final class LecturerActivity extends Activity {

    private LinearLayout contentContainer;
    private ProgressBar contentProgress;
    private ScrollView contentScroll;
    private LecturerRepository repository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()
                || !"Lecturer".equalsIgnoreCase(sessionManager.getRole())) {
            openLogin();
            return;
        }

        setContentView(R.layout.activity_lecturer);
        repository = new LecturerRepository(sessionManager);
        contentContainer = findViewById(R.id.lecturerContentContainer);
        contentProgress = findViewById(R.id.lecturerContentProgress);
        contentScroll = findViewById(R.id.lecturerContentScroll);

        Models.UserProfile profile = sessionManager.getProfile();
        TextView toolbarTitle = findViewById(R.id.lecturerToolbarTitle);
        toolbarTitle.setText(profile.fullName + " · " + profile.degree);
        findViewById(R.id.lecturerLogoutButton).setOnClickListener(view -> logout());
        findViewById(R.id.lecturerLocationButton).setOnClickListener(view ->
                startActivity(new Intent(this, LocationCalibrationActivity.class)));
        configureNavigation();
        loadHome();
    }

    private void configureNavigation() {
        findViewById(R.id.lecturerNavHome).setOnClickListener(view -> loadHome());
        findViewById(R.id.lecturerNavSchedule).setOnClickListener(
                view -> loadSchedule());
        findViewById(R.id.lecturerNavAttendance).setOnClickListener(
                view -> loadAttendanceClasses());
        findViewById(R.id.lecturerNavGrades).setOnClickListener(
                view -> loadGradeClasses());
        findViewById(R.id.lecturerNavNotification).setOnClickListener(
                view -> loadNotificationForm());
    }

    private void loadHome() {
        showLoading();
        repository.dashboard(new StudentRepository.Callback<>() {
            @Override
            public void onSuccess(Models.LecturerDashboard dashboard) {
                showContent();
                Models.UserProfile profile = sessionManager.getProfile();
                addHeader(
                        "TỔNG QUAN GIẢNG DẠY",
                        "Xin chào, " + profile.fullName,
                        profile.degree + " · " + profile.department);

                LinearLayout stats = new LinearLayout(LecturerActivity.this);
                stats.setOrientation(LinearLayout.HORIZONTAL);
                stats.setWeightSum(3f);
                stats.addView(CardFactory.statCard(
                        LecturerActivity.this,
                        String.valueOf(dashboard.classCount),
                        "LỚP PHỤ TRÁCH"));
                stats.addView(CardFactory.statCard(
                        LecturerActivity.this,
                        String.valueOf(dashboard.studentCount),
                        "SINH VIÊN"));
                stats.addView(CardFactory.statCard(
                        LecturerActivity.this,
                        String.valueOf(dashboard.scheduleCount),
                        "LỊCH TRONG TUẦN"));
                contentContainer.addView(stats, marginParams(0, 0, 0, 22));

                addSectionTitle("Lớp học phần phụ trách");
                renderClassCards(
                        dashboard.classes,
                        "Xem điểm danh",
                        LecturerActivity.this::showSessions);
            }

            @Override
            public void onError(String message) {
                showError(message, LecturerActivity.this::loadHome);
            }
        });
    }

    private void loadSchedule() {
        showLoading();
        repository.schedule(new StudentRepository.Callback<>() {
            @Override
            public void onSuccess(List<Models.ScheduleEntry> schedule) {
                showContent();
                addHeader(
                        "LỊCH GIẢNG DẠY",
                        "Thời khóa biểu",
                        "Lịch của tất cả lớp học phần đang phụ trách.");
                if (schedule.isEmpty()) {
                    addEmptyText("Chưa có lịch giảng dạy.");
                    return;
                }
                for (Models.ScheduleEntry entry : schedule) {
                    contentContainer.addView(CardFactory.dataCard(
                            LecturerActivity.this,
                            entry.classCode + " · " + entry.className,
                            entry.dayOfWeek + " · tiết "
                                    + entry.startPeriod + "-" + entry.endPeriod
                                    + " · phòng " + entry.room,
                            entry.appliedFrom + " → " + entry.appliedTo));
                }
            }

            @Override
            public void onError(String message) {
                showError(message, LecturerActivity.this::loadSchedule);
            }
        });
    }

    private void loadAttendanceClasses() {
        loadClasses(
                "QUẢN LÝ ĐIỂM DANH",
                "Chọn lớp học phần",
                "Chọn lớp rồi chọn buổi học để xem và điều chỉnh trạng thái.",
                "Chọn buổi học",
                this::showSessions);
    }

    private void showSessions(Models.LecturerClass lecturerClass) {
        repository.sessions(lecturerClass.id, new StudentRepository.Callback<>() {
            @Override
            public void onSuccess(List<Models.LecturerSession> sessions) {
                LinearLayout list = dialogList();
                AlertDialog dialog = new AlertDialog.Builder(LecturerActivity.this)
                        .setTitle(lecturerClass.code + " · Chọn buổi học")
                        .setView(wrapInScroll(list))
                        .setNegativeButton("Đóng", null)
                        .create();
                if (sessions.isEmpty()) {
                    list.addView(messageText(
                            "Lớp chưa có buổi học. Hãy tạo buổi học trên desktop."));
                } else {
                    for (Models.LecturerSession session : sessions) {
                        View card = CardFactory.dataCard(
                                LecturerActivity.this,
                                session.date + " · tiết "
                                        + session.startPeriod + "-" + session.endPeriod,
                                "Phòng " + session.room,
                                session.status + " · " + session.id);
                        CardFactory.setAction(card, "Xem danh sách", view -> {
                            dialog.dismiss();
                            loadAttendance(session);
                        }, true);
                        list.addView(card);
                    }
                }
                dialog.show();
            }

            @Override
            public void onError(String message) {
                showDialog("Không tải được buổi học", message);
            }
        });
    }

    private void loadAttendance(Models.LecturerSession session) {
        showLoading();
        repository.attendance(session.id, new StudentRepository.Callback<>() {
            @Override
            public void onSuccess(List<Models.LecturerAttendance> attendance) {
                showContent();
                addHeader(
                        "DANH SÁCH ĐIỂM DANH",
                        session.date + " · phòng " + session.room,
                        "Nhấn nút trên từng sinh viên để chuyển trạng thái.");
                if (attendance.isEmpty()) {
                    addEmptyText("Buổi học chưa có sinh viên.");
                    return;
                }
                for (Models.LecturerAttendance item : attendance) {
                    View card = CardFactory.dataCard(
                            LecturerActivity.this,
                            item.studentId + " · " + item.studentName,
                            item.attendedAt.isEmpty()
                                    ? "Chưa ghi nhận thời gian"
                                    : item.attendedAt + " · " + item.method,
                            attendanceLabel(item.status));
                    String nextStatus = nextAttendanceStatus(item.status);
                    CardFactory.setAction(
                            card,
                            "Đổi sang " + attendanceLabel(nextStatus),
                            view -> updateAttendance(
                                    session,
                                    item.studentId,
                                    nextStatus),
                            true);
                    contentContainer.addView(card);
                }
            }

            @Override
            public void onError(String message) {
                showError(message, () -> loadAttendance(session));
            }
        });
    }

    private void updateAttendance(
            Models.LecturerSession session,
            String studentId,
            String status) {
        repository.updateAttendance(
                session.id,
                studentId,
                status,
                new StudentRepository.Callback<>() {
                    @Override
                    public void onSuccess(String message) {
                        loadAttendance(session);
                    }

                    @Override
                    public void onError(String message) {
                        showDialog("Không cập nhật được điểm danh", message);
                    }
                });
    }

    private void loadGradeClasses() {
        loadClasses(
                "NHẬP VÀ SỬA ĐIỂM",
                "Chọn lớp học phần",
                "Mở danh sách sinh viên để cập nhật điểm thành phần và điểm thi.",
                "Mở bảng điểm",
                this::loadGrades);
    }

    private void loadGrades(Models.LecturerClass lecturerClass) {
        showLoading();
        repository.grades(lecturerClass.id, new StudentRepository.Callback<>() {
            @Override
            public void onSuccess(List<Models.LecturerGrade> grades) {
                showContent();
                addHeader(
                        "BẢNG ĐIỂM",
                        lecturerClass.code,
                        lecturerClass.name
                                + " · chạm Sửa điểm để cập nhật từng sinh viên.");
                if (grades.isEmpty()) {
                    addEmptyText("Lớp chưa có sinh viên.");
                    return;
                }
                for (Models.LecturerGrade grade : grades) {
                    View card = CardFactory.dataCard(
                            LecturerActivity.this,
                            grade.studentId + " · " + grade.studentName,
                            "CC: " + score(grade.score1)
                                    + " · GK: " + score(grade.score2)
                                    + " · TP3: " + score(grade.score3),
                            "Thi: " + score(grade.examScore));
                    CardFactory.setAction(
                            card,
                            "Sửa điểm",
                            view -> showGradeEditor(lecturerClass, grade),
                            true);
                    contentContainer.addView(card);
                }
            }

            @Override
            public void onError(String message) {
                showError(message, () -> loadGrades(lecturerClass));
            }
        });
    }

    private void showGradeEditor(
            Models.LecturerClass lecturerClass,
            Models.LecturerGrade grade) {
        LinearLayout form = dialogList();
        EditText score1 = scoreInput("Điểm chuyên cần", grade.score1);
        EditText score2 = scoreInput("Điểm giữa kỳ", grade.score2);
        EditText score3 = scoreInput("Điểm thành phần 3", grade.score3);
        EditText exam = scoreInput("Điểm thi", grade.examScore);
        form.addView(score1);
        form.addView(score2);
        form.addView(score3);
        form.addView(exam);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(grade.studentId + " · " + grade.studentName)
                .setView(form)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Lưu", null)
                .create();
        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(view -> {
                            try {
                                grade.score1 = parseScore(score1);
                                grade.score2 = parseScore(score2);
                                grade.score3 = parseScore(score3);
                                grade.examScore = parseScore(exam);
                            } catch (IllegalArgumentException ex) {
                                showDialog("Điểm không hợp lệ", ex.getMessage());
                                return;
                            }
                            repository.updateGrade(
                                    grade,
                                    new StudentRepository.Callback<>() {
                                        @Override
                                        public void onSuccess(String message) {
                                            dialog.dismiss();
                                            loadGrades(lecturerClass);
                                        }

                                        @Override
                                        public void onError(String message) {
                                            showDialog(
                                                    "Không cập nhật được điểm",
                                                    message);
                                        }
                                    });
                        }));
        dialog.show();
    }

    private void loadNotificationForm() {
        showLoading();
        repository.classes(new StudentRepository.Callback<>() {
            @Override
            public void onSuccess(List<Models.LecturerClass> classes) {
                showContent();
                addHeader(
                        "GỬI THÔNG BÁO",
                        "Thông báo theo lớp",
                        "Thông báo được gửi tới toàn bộ sinh viên đã đăng ký lớp.");
                if (classes.isEmpty()) {
                    addEmptyText("Bạn chưa phụ trách lớp học phần nào.");
                    return;
                }

                Spinner classInput = new Spinner(LecturerActivity.this);
                classInput.setAdapter(new ArrayAdapter<>(
                        LecturerActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        classes));
                EditText titleInput = textInput("Tiêu đề thông báo", false);
                EditText contentInput = textInput("Nội dung thông báo", true);
                Button sendButton = styledButton("Gửi thông báo");
                sendButton.setOnClickListener(view -> {
                    Models.LecturerClass selected =
                            (Models.LecturerClass) classInput.getSelectedItem();
                    String title = textOf(titleInput);
                    String content = textOf(contentInput);
                    if (selected == null || title.isEmpty() || content.isEmpty()) {
                        showDialog(
                                "Thiếu thông tin",
                                "Vui lòng chọn lớp, nhập tiêu đề và nội dung.");
                        return;
                    }
                    sendButton.setEnabled(false);
                    repository.sendNotification(
                            selected.id,
                            title,
                            content,
                            new StudentRepository.Callback<>() {
                                @Override
                                public void onSuccess(String message) {
                                    sendButton.setEnabled(true);
                                    titleInput.setText("");
                                    contentInput.setText("");
                                    showDialog("Gửi thành công", message);
                                }

                                @Override
                                public void onError(String message) {
                                    sendButton.setEnabled(true);
                                    showDialog("Gửi thất bại", message);
                                }
                            });
                });

                contentContainer.addView(label("Lớp học phần"));
                contentContainer.addView(classInput, marginParams(0, 7, 0, 14));
                contentContainer.addView(label("Tiêu đề"));
                contentContainer.addView(titleInput, marginParams(0, 7, 0, 14));
                contentContainer.addView(label("Nội dung"));
                contentContainer.addView(contentInput, marginParams(0, 7, 0, 18));
                contentContainer.addView(sendButton);
            }

            @Override
            public void onError(String message) {
                showError(message, LecturerActivity.this::loadNotificationForm);
            }
        });
    }

    private void loadClasses(
            String kicker,
            String title,
            String subtitle,
            String actionLabel,
            ClassAction action) {
        showLoading();
        repository.classes(new StudentRepository.Callback<>() {
            @Override
            public void onSuccess(List<Models.LecturerClass> classes) {
                showContent();
                addHeader(kicker, title, subtitle);
                renderClassCards(classes, actionLabel, action);
            }

            @Override
            public void onError(String message) {
                showError(
                        message,
                        () -> loadClasses(kicker, title, subtitle, actionLabel, action));
            }
        });
    }

    private void renderClassCards(
            List<Models.LecturerClass> classes,
            String actionLabel,
            ClassAction action) {
        if (classes.isEmpty()) {
            addEmptyText("Bạn chưa phụ trách lớp học phần nào.");
            return;
        }
        for (Models.LecturerClass lecturerClass : classes) {
            View card = CardFactory.dataCard(
                    this,
                    lecturerClass.code + " · " + lecturerClass.name,
                    lecturerClass.semester + " " + lecturerClass.year,
                    lecturerClass.enrolled + "/" + lecturerClass.capacity
                            + " sinh viên · " + lecturerClass.status);
            CardFactory.setAction(
                    card,
                    actionLabel,
                    view -> action.run(lecturerClass),
                    true);
            contentContainer.addView(card);
        }
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

    private TextView label(String text) {
        TextView view = messageText(text);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setTextColor(getColor(R.color.slate_900));
        return view;
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

    private EditText textInput(String hint, boolean multiline) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setTextColor(getColor(R.color.slate_900));
        input.setHintTextColor(getColor(R.color.slate_400));
        input.setBackground(getDrawable(R.drawable.input_background));
        input.setPadding(dp(16), dp(12), dp(16), dp(12));
        if (multiline) {
            input.setMinLines(5);
            input.setGravity(Gravity.TOP);
            input.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        } else {
            input.setSingleLine(true);
        }
        return input;
    }

    private EditText scoreInput(String hint, Double value) {
        EditText input = textInput(hint + " (0-10, có thể để trống)", false);
        input.setInputType(InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (value != null) {
            input.setText(String.format(Locale.US, "%.2f", value));
        }
        LinearLayout.LayoutParams params = marginParams(0, 0, 0, 10);
        input.setLayoutParams(params);
        return input;
    }

    private Double parseScore(EditText input) {
        String value = textOf(input);
        if (value.isEmpty()) {
            return null;
        }
        try {
            double score = Double.parseDouble(value);
            if (!Double.isFinite(score) || score < 0 || score > 10) {
                throw new NumberFormatException();
            }
            return score;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Mỗi điểm phải nằm trong khoảng 0 đến 10.");
        }
    }

    private LinearLayout dialogList() {
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(dp(16), dp(12), dp(16), dp(12));
        return list;
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

    private String attendanceLabel(String status) {
        return switch (status) {
            case "PRESENT" -> "Có mặt";
            case "LATE" -> "Đi muộn";
            default -> "Vắng mặt";
        };
    }

    private String nextAttendanceStatus(String status) {
        return switch (status) {
            case "PRESENT" -> "LATE";
            case "LATE" -> "ABSENT";
            default -> "PRESENT";
        };
    }

    private LinearLayout.LayoutParams marginParams(
            int left,
            int top,
            int right,
            int bottom) {
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

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private interface ClassAction {
        void run(Models.LecturerClass lecturerClass);
    }
}
