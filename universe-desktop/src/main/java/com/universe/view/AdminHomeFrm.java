package com.universe.view;

import com.universe.dao.ClassSectionDAO;
import com.universe.dao.CourseDAO;
import com.universe.dao.UserDAO;
import com.universe.entity.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Giao diện chính quản trị viên (AdminHomeFrm) - Modern Japanese Editorial Style.
 * Thiết kế theo hướng Dashboard hiện đại với Stat Cards và Chart.
 */
public class AdminHomeFrm extends Stage {

    private final User currentUser;
    private final UserDAO userDAO = new UserDAO();

    private final Button btnHome           = FxHelper.createSidebarButton("Tổng quan", "⌂");
    private final Button btnUserManage     = FxHelper.createSidebarButton("Quản lý người dùng", "👥");
    private final Button btnCourse         = FxHelper.createSidebarButton("Quản lý học phần", "📚");
    private final Button btnClassSection   = FxHelper.createSidebarButton("Quản lý lớp học phần", "🏫");
    private final Button btnScheduleManage = FxHelper.createSidebarButton("Quản lý lịch học", "📅");
    private final Button btnNotification   = FxHelper.createSidebarButton("Gửi thông báo", "🔔");
    private final Button btnLogout         = new Button("Đăng xuất");

    private final StackPane contentArea = new StackPane();
    private VBox dashboardView;

    public AdminHomeFrm(User user) {
        this.currentUser = user;
        setTitle("UniVerse - Quản trị viên: " + user.getFullName());
        buildUI();
    }

    private void buildUI() {
        // ── Sidebar ──
        VBox sidebar = FxHelper.createSidebarBase("Quản trị viên");

        Label userName = new Label(currentUser.getFullName());
        userName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #F4F7FF;");
        Label userRole = new Label("Administrator");
        userRole.setStyle("-fx-font-size: 12px; -fx-text-fill: #A8B3CF;");
        VBox userInfo = new VBox(2, userName, userRole);
        userInfo.setPadding(new Insets(0, 20, 12, 20));

        Separator sep2 = new Separator();

        btnHome.setOnAction(e -> openView(btnHome, dashboardView));

        btnUserManage.setOnAction(e -> openView(btnUserManage, new UserManageFrm(currentUser)));
        btnCourse.setOnAction(e -> openView(btnCourse, new CourseFrm(currentUser)));
        btnClassSection.setOnAction(e -> openView(btnClassSection, new ClassSectionFrm(currentUser)));
        btnScheduleManage.setOnAction(e -> openView(btnScheduleManage, new ScheduleManageFrm(currentUser)));
        btnNotification.setOnAction(e -> openView(btnNotification, new SendNotificationFrm(currentUser)));

        btnLogout.getStyleClass().add("sidebar-logout");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> {
            userDAO.logout(currentUser.getId());
            close();
            new LoginFrm().show();
        });

        sidebar.getChildren().addAll(userInfo, sep2,
                btnHome, btnUserManage, btnCourse, btnClassSection, btnScheduleManage, btnNotification,
                FxHelper.createSpacer(), btnLogout);

        // ── Content ──
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

        // Default active
        setActiveButton(btnHome, btnHome, btnUserManage, btnCourse, btnClassSection, btnScheduleManage, btnNotification);
    }

    private VBox buildDashboard() {
        VBox dash = new VBox(32);
        dash.setAlignment(Pos.TOP_LEFT);

        // Header
        Label title = new Label("Tổng quan Hệ thống");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("SỐ LIỆU & HOẠT ĐỘNG CHÍNH");
        subtitle.getStyleClass().add("section-kicker");
        VBox header = new VBox(8, subtitle, title);

        // Stats
        long studentCount = userDAO.findActiveUserIds("Student").size();
        long openClassesCount = new ClassSectionDAO().getListClassSection().stream().filter(c -> "open".equalsIgnoreCase(c.getStatus())).count();
        long courseCount = new CourseDAO().getListCourse().size();

        HBox stats = new HBox(24);
        VBox card1 = FxHelper.createStatCard("TỔNG SINH VIÊN", String.valueOf(studentCount));
        VBox card2 = FxHelper.createStatCard("LỚP ĐANG MỞ", String.valueOf(openClassesCount));
        VBox card3 = FxHelper.createStatCard("HỌC PHẦN", String.valueOf(courseCount));
        card1.setStyle("-fx-border-color: -ux-accent-violet;"); // Violet
        card2.setStyle("-fx-border-color: -ux-accent-cyan;"); // Cyan
        card3.setStyle("-fx-border-color: -ux-accent-pink;"); // Pink
        stats.getChildren().addAll(card1, card2, card3);

        // Chart
        VBox chartBox = new VBox(16);
        chartBox.getStyleClass().add("card");
        VBox.setVgrow(chartBox, Priority.ALWAYS);
        
        Label chartTitle = new Label("Lượt đăng ký lớp học phần");
        chartTitle.getStyleClass().add("section-title");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Tháng 8", 120));
        series.getData().add(new XYChart.Data<>("Tháng 9", 450));
        series.getData().add(new XYChart.Data<>("Tháng 10", 300));
        series.getData().add(new XYChart.Data<>("Tháng 11", 380));
        series.getData().add(new XYChart.Data<>("Tháng 12", 210));
        lineChart.getData().add(series);

        chartBox.getChildren().addAll(chartTitle, lineChart);

        dash.getChildren().addAll(header, stats, chartBox);
        return dash;
    }

    private void openView(Button activeBtn, Node viewNode) {
        setActiveButton(activeBtn, btnHome, btnUserManage, btnCourse, btnClassSection, btnScheduleManage, btnNotification);
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
