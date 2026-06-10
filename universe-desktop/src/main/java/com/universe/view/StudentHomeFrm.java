package com.universe.view;

import com.universe.dao.UserDAO;
import com.universe.dao.CourseDAO;
import com.universe.dao.CourseRecordDAO;
import com.universe.dao.NotificationDAO;
import com.universe.entity.Course;
import com.universe.entity.CourseRecord;
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
 * Giao diện chính sinh viên (StudentHomeFrm) - Modern Japanese Editorial Style.
 * Thiết kế theo hướng "Discovery", hiển thị TKB sắp tới và thống kê học tập.
 */
public class StudentHomeFrm extends Stage {

    private final User currentUser;
    private final UserDAO userDAO = new UserDAO();
    private final CourseDAO courseDAO = new CourseDAO();

    private final Button btnHome           = FxHelper.createSidebarButton("Tổng quan", "⌂");
    private final Button btnRegister       = FxHelper.createSidebarButton("Đăng ký lớp học phần", "✦");
    private final Button btnViewSchedule   = FxHelper.createSidebarButton("Xem lịch học", "📅");
    private final Button btnViewGrade      = FxHelper.createSidebarButton("Xem điểm", "📊");
    private final Button btnNotification   = FxHelper.createSidebarButton("Xem thông báo", "🔔");
    private final Button btnLogout         = new Button("Đăng xuất");

    private final StackPane contentArea = new StackPane();
    private VBox dashboardView;

    public StudentHomeFrm(User user) {
        this.currentUser = user;
        setTitle("UniVerse - Sinh viên: " + user.getFullName());
        buildUI();
    }

    private void buildUI() {
        // ── Sidebar ──
        VBox sidebar = FxHelper.createSidebarBase("Sinh viên");

        Label userName = new Label(currentUser.getFullName());
        userName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #F4F7FF;");
        Label userRole = new Label("Student");
        userRole.setStyle("-fx-font-size: 12px; -fx-text-fill: #A8B3CF;");
        VBox userInfo = new VBox(2, userName, userRole);
        userInfo.setPadding(new Insets(0, 20, 12, 20));

        Separator sep2 = new Separator();

        // Nút trang chủ
        btnHome.setOnAction(e -> openView(btnHome, dashboardView));

        btnRegister.setOnAction(e -> openView(btnRegister, new SearchCourseRegistrationFrm(currentUser)));
        btnViewSchedule.setOnAction(e -> openView(btnViewSchedule, new ViewScheduleFrm(currentUser)));
        btnViewGrade.setOnAction(e -> openView(btnViewGrade, new ViewGradeFrm(currentUser)));
        btnNotification.setOnAction(e -> openView(btnNotification, new ViewNotificationFrm(currentUser)));

        btnLogout.getStyleClass().add("sidebar-logout");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> {
            userDAO.logout(currentUser.getId());
            close();
            new LoginFrm().show();
        });

        sidebar.getChildren().addAll(userInfo, sep2,
                btnHome, btnRegister, btnViewSchedule, btnViewGrade, btnNotification,
                FxHelper.createSpacer(), btnLogout);

        // ── Content Area ──
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
        setActiveButton(btnHome, btnHome, btnRegister, btnViewSchedule, btnViewGrade, btnNotification);
    }

    private VBox buildDashboard() {
        VBox dash = new VBox(32);
        dash.setAlignment(Pos.TOP_LEFT);

        // Header
        Label title = new Label("Khám phá Hành trình Học thuật");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("BẮT ĐẦU KỲ HỌC MỚI CỦA BẠN");
        subtitle.getStyleClass().add("section-kicker");
        VBox header = new VBox(8, subtitle, title);

        List<CourseRecord> grades = new CourseRecordDAO().viewGrade(currentUser.getId());
        double sum = grades.stream().filter(r -> r.getTotalScore() != null).mapToDouble(CourseRecord::getTotalScore).sum();
        long graded = grades.stream().filter(r -> r.getTotalScore() != null).count();
        double avg = graded > 0 ? sum / graded : 0;
        int notifCount = new NotificationDAO().getNotifications(currentUser.getId()).size();

        HBox stats = new HBox(24);
        VBox card1 = FxHelper.createStatCard("LỚP ĐÃ ĐĂNG KÝ", String.valueOf(grades.size()));
        VBox card2 = FxHelper.createStatCard("ĐIỂM TRUNG BÌNH", String.format("%.2f", avg));
        VBox card3 = FxHelper.createStatCard("THÔNG BÁO MỚI", String.valueOf(notifCount));
        
        card1.setStyle("-fx-border-color: -ux-accent-cyan;");
        card2.setStyle("-fx-border-color: -ux-accent-violet;");
        card3.setStyle("-fx-border-color: -ux-accent-pink;");
        stats.getChildren().addAll(card1, card2, card3);

        // Featured Card
        HBox featured = new HBox(24);
        featured.getStyleClass().add("card");
        featured.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(featured, Priority.ALWAYS);

        Circle deco = new Circle(40);
        try {
            javafx.scene.image.Image img = new javafx.scene.image.Image(getClass().getResource("/images/login-icon.png").toExternalForm());
            deco.setFill(new javafx.scene.paint.ImagePattern(img));
        } catch (Exception e) {
            deco.setStyle("-fx-fill: -ux-shadow-color;");
        }

        VBox featInfo = new VBox(8);
        Label featSub = new Label("HỌC PHẦN NỔI BẬT");
        featSub.getStyleClass().add("section-kicker");
        Label featTitle = new Label("Nhập môn Trí tuệ Nhân tạo");
        featTitle.getStyleClass().add("section-title");
        Label featDesc = new Label("Khám phá nền tảng AI và Machine Learning hiện đại.");
        featDesc.getStyleClass().add("body-text");
        featInfo.getChildren().addAll(featSub, featTitle, featDesc);

        Button btnView = new Button("Xem chi tiết");
        btnView.getStyleClass().add("btn-secondary");
        btnView.setOnAction(e -> {
            Course aiCourse = courseDAO.findById("C03");
            if (aiCourse == null) {
                openView(btnRegister, new SearchCourseRegistrationFrm(currentUser, "Trí tuệ nhân tạo"));
                return;
            }
            openView(btnRegister, new ClassSectionRegistrationFrm(currentUser, aiCourse));
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        featured.getChildren().addAll(deco, featInfo, spacer, btnView);

        dash.getChildren().addAll(header, stats, featured);
        return dash;
    }

    private void openView(Button activeBtn, Node viewNode) {
        setActiveButton(activeBtn, btnHome, btnRegister, btnViewSchedule, btnViewGrade, btnNotification);
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
