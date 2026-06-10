package com.universe.view;

import com.universe.dao.ClassSectionDAO;
import com.universe.dao.ScheduleDAO;
import com.universe.dao.UserDAO;
import com.universe.entity.ClassSection;
import com.universe.entity.Schedule;
import com.universe.entity.User;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Giao diện chính giảng viên (LecturerHomeFrm) - Modern Japanese Editorial Style.
 * Thiết kế theo hướng Dashboard: Tổng quan giảng dạy, thống kê.
 */
public class LecturerHomeFrm extends Stage {

    private final User currentUser;
    private final UserDAO userDAO = new UserDAO();

    private final Button btnHome           = FxHelper.createSidebarButton("Tổng quan", "⌂");
    private final Button btnViewSchedule   = FxHelper.createSidebarButton("Xem lịch giảng dạy", "📅");
    private final Button btnQRCode         = FxHelper.createSidebarButton("Tạo mã QR điểm danh", "📱");
    private final Button btnAttendance     = FxHelper.createSidebarButton("Quản lý điểm danh", "✅");
    private final Button btnEnterGrade     = FxHelper.createSidebarButton("Nhập / Sửa điểm", "📝");
    private final Button btnNotification   = FxHelper.createSidebarButton("Gửi thông báo", "🔔");
    private final Button btnLogout         = new Button("Đăng xuất");

    private final StackPane contentArea = new StackPane();
    private VBox dashboardView;

    public LecturerHomeFrm(User user) {
        this.currentUser = user;
        setTitle("UniVerse - Giảng viên: " + user.getFullName());
        buildUI();
    }

    private void buildUI() {
        VBox sidebar = FxHelper.createSidebarBase("Giảng viên");

        Label userName = new Label(currentUser.getFullName());
        userName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #F4F7FF;");
        Label userRole = new Label("Lecturer");
        userRole.setStyle("-fx-font-size: 12px; -fx-text-fill: #A8B3CF;");
        VBox userInfo = new VBox(2, userName, userRole);
        userInfo.setPadding(new Insets(0, 20, 12, 20));

        Separator sep2 = new Separator();

        btnHome.setOnAction(e -> openView(btnHome, dashboardView));

        btnViewSchedule.setOnAction(e -> openView(btnViewSchedule, new ViewScheduleFrm(currentUser)));
        btnQRCode.setOnAction(e -> openView(btnQRCode, new QRCodeFrm(currentUser)));
        btnAttendance.setOnAction(e -> openView(btnAttendance, new AttendanceManageFrm(currentUser)));
        btnEnterGrade.setOnAction(e -> openView(btnEnterGrade, new GradeEntryFrm(currentUser)));
        btnNotification.setOnAction(e -> openView(btnNotification, new SendNotificationFrm(currentUser)));

        btnLogout.getStyleClass().add("sidebar-logout");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> {
            userDAO.logout(currentUser.getId());
            close();
            new LoginFrm().show();
        });

        sidebar.getChildren().addAll(userInfo, sep2,
                btnHome, btnViewSchedule, btnQRCode, btnAttendance, btnEnterGrade, btnNotification,
                FxHelper.createSpacer(), btnLogout);

        contentArea.getStyleClass().add("content-area");
        contentArea.setAlignment(Pos.TOP_LEFT);
        
        dashboardView = buildDashboard();
        contentArea.getChildren().add(FxHelper.createContentScroll(dashboardView));
        
        // ── Root ──
        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(contentArea);

        try {
            String bgUrl = getClass().getResource("/images/login-bg.jpg").toExternalForm();
            root.setStyle("-fx-background-image: url('" + bgUrl + "'); -fx-background-size: cover; -fx-background-position: center center;");
        } catch (Exception e) {
            root.setStyle("-fx-background-color: #070B1A;");
        }

        setScene(FxHelper.createScene(root, 1200, 720));
        setActiveButton(btnHome, btnHome, btnViewSchedule, btnQRCode, btnAttendance, btnEnterGrade, btnNotification);
    }

    private VBox buildDashboard() {
        VBox dash = new VBox(32);
        dash.setAlignment(Pos.TOP_LEFT);

        // Header
        Label title = new Label("Tổng quan Giảng dạy");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("KẾ HOẠCH & LỊCH TRÌNH");
        subtitle.getStyleClass().add("section-kicker");
        VBox header = new VBox(8, subtitle, title);

        // Fetch DB Stats
        ClassSectionDAO classSectionDAO = new ClassSectionDAO();
        ScheduleDAO scheduleDAO = new ScheduleDAO();
        
        List<ClassSection> myClasses = classSectionDAO.getByLecturer(currentUser.getId());
        long classCount = myClasses.size();
        
        int totalEnrolled = 0;
        for (ClassSection cs : myClasses) {
            totalEnrolled += userDAO.findStudentIdsByClassSection(cs.getId()).size();
        }
        long avgSize = myClasses.isEmpty() ? 0 : totalEnrolled / classCount;
        
        List<Schedule> schedules = scheduleDAO.getLecturerSchedule(currentUser.getId());
        long schedCount = schedules.size();

        // Stats
        HBox stats = new HBox(24);
        VBox card1 = FxHelper.createStatCard("LỚP ĐANG DẠY", String.valueOf(classCount));
        VBox card2 = FxHelper.createStatCard("SĨ SỐ TRUNG BÌNH", String.valueOf(avgSize));
        VBox card3 = FxHelper.createStatCard("LỊCH TRONG TUẦN", String.valueOf(schedCount));
        
        card1.setStyle("-fx-border-color: -ux-accent-cyan;");
        card2.setStyle("-fx-border-color: -ux-accent-violet;");
        card3.setStyle("-fx-border-color: -ux-accent-pink;");

        stats.getChildren().addAll(card1, card2, card3);

        // Featured Card - Upcoming Class
        HBox upcoming = new HBox(24);
        upcoming.getStyleClass().add("card");
        upcoming.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(upcoming, Priority.ALWAYS);

        Circle deco = new Circle(40);
        deco.getStyleClass().add("deco-circle");
        try {
            javafx.scene.image.Image img = new javafx.scene.image.Image(getClass().getResource("/images/login-icon.png").toExternalForm());
            deco.setFill(new javafx.scene.paint.ImagePattern(img));
        } catch (Exception e) {
            deco.setStyle("-fx-fill: -ux-shadow-color;"); // Blue tint
        }

        VBox featInfo = new VBox(8);
        Label featSub = new Label("BUỔI HỌC SẮP DIỄN RA");
        featSub.getStyleClass().add("section-kicker");
        featSub.setStyle("-fx-text-fill: #1F4E5F;");
        
        String upcomingTitle = "Chưa có lịch học";
        String upcomingDesc = "Bạn không có lớp nào sắp diễn ra.";
        if (!schedules.isEmpty()) {
            Schedule next = schedules.get(0);
            ClassSection nextClass = classSectionDAO.findById(next.getClassSectionId());
            upcomingTitle = nextClass != null ? nextClass.getName() : "Không rõ tên lớp";
            upcomingDesc = "Tiết " + next.getStartPeriod() + "-" + next.getEndPeriod() + " - Phòng " + next.getRoom() + " (" + next.getDayOfWeek() + ")";
        }

        Label featTitle = new Label(upcomingTitle);
        featTitle.getStyleClass().add("section-title");
        Label featDesc = new Label(upcomingDesc);
        featDesc.getStyleClass().add("body-text");
        featInfo.getChildren().addAll(featSub, featTitle, featDesc);

        Button btnView = new Button("Tạo QR ngay");
        btnView.getStyleClass().add("btn-primary");
        btnView.setOnAction(e -> openView(btnQRCode, new QRCodeFrm(currentUser)));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        upcoming.getChildren().addAll(deco, featInfo, spacer, btnView);

        dash.getChildren().addAll(header, stats, upcoming);
        return dash;
    }

    private void openView(Button activeBtn, Node viewNode) {
        setActiveButton(activeBtn, btnHome, btnViewSchedule, btnQRCode, btnAttendance, btnEnterGrade, btnNotification);
        contentArea.getChildren().setAll(FxHelper.createContentScroll(viewNode));
    }

    private void setActiveButton(Button active, Button... allButtons) {
        for (Button b : allButtons) {
            if (b == active) {
                b.getStyleClass().removeAll("sidebar-btn");
                if (!b.getStyleClass().contains("sidebar-btn-active"))
                    b.getStyleClass().add("sidebar-btn-active");
            } else {
                b.getStyleClass().removeAll("sidebar-btn-active");
                if (!b.getStyleClass().contains("sidebar-btn"))
                    b.getStyleClass().add("sidebar-btn");
            }
        }
    }
}
