package com.universe.view;

import com.universe.dao.UserDAO;
import com.universe.entity.User;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Giao diện đăng nhập (LoginFrm) - Modern Japanese Editorial Style.
 */
public class LoginFrm extends Stage {

    private final TextField inUsername = new TextField();
    private final PasswordField inPassword = new PasswordField();
    private final Button subLogin = new Button("Đăng nhập hệ thống");
    private final Label outError = new Label(" ");

    private final UserDAO userDAO = new UserDAO();

    public LoginFrm() {
        setTitle("UniVerse - Digital Higher Education");
        buildUI();
    }

    private void buildUI() {
        // ── Left Side (Branding) ──
        VBox leftPane = new VBox(24);
        leftPane.setAlignment(Pos.CENTER_LEFT);
        leftPane.setPadding(new Insets(64));
        
        try {
            String bgUrl = getClass().getResource("/images/login-bg.jpg").toExternalForm();
            leftPane.setStyle("-fx-background-image: url('" + bgUrl + "'); -fx-background-size: cover; -fx-background-position: center center;");
        } catch (Exception e) {
            leftPane.setStyle("-fx-background-color: #1a1a2e;");
        }

        ImageView iconView = new ImageView();
        try {
            Image iconImg = new Image(getClass().getResourceAsStream("/images/login-icon.png"));
            iconView.setImage(iconImg);
            iconView.setFitWidth(80);
            iconView.setFitHeight(80);
            iconView.setPreserveRatio(true);
        } catch (Exception e) {
            // fallback
        }

        Label brand = new Label("UniVerse");
        brand.getStyleClass().add("app-title");
        brand.setStyle("-fx-font-size: 42px; -fx-text-fill: #FFFFFF;");

        Label tagline = new Label("A calm digital space for academic management");
        tagline.getStyleClass().add("body-text");
        tagline.setStyle("-fx-font-size: 16px; -fx-font-style: italic; -fx-text-fill: #E2DDD2;");

        leftPane.getChildren().addAll(iconView, brand, tagline);
        HBox.setHgrow(leftPane, Priority.ALWAYS);

        // ── Right Side (Form) ──
        VBox rightPane = new VBox(24);
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setPadding(new Insets(64));
        rightPane.setStyle("-fx-background-color: #10172F;");
        rightPane.setMinWidth(400);
        rightPane.setMaxWidth(400);

        Label lblLogin = new Label("Đăng nhập");
        lblLogin.getStyleClass().add("section-title");

        Label lblUser = new Label("Tên đăng nhập / Email");
        VBox rowUser = new VBox(8);
        inUsername.setPromptText("Mã sinh viên / giảng viên...");
        inUsername.setPrefHeight(44);
        rowUser.getChildren().addAll(lblUser, inUsername);

        Label lblPass = new Label("Mật khẩu");
        VBox rowPass = new VBox(8);
        inPassword.setPromptText("Nhập mật khẩu...");
        inPassword.setPrefHeight(44);
        rowPass.getChildren().addAll(lblPass, inPassword);

        outError.getStyleClass().add("label-error");
        outError.setStyle("-fx-text-fill: #FB7185; -fx-font-size: 13px;");

        subLogin.getStyleClass().add("btn-primary");
        subLogin.setMaxWidth(Double.MAX_VALUE);
        subLogin.setPrefHeight(44);
        subLogin.setOnAction(e -> handleLogin());
        inPassword.setOnAction(e -> handleLogin());

        VBox form = new VBox(16);
        form.getChildren().addAll(rowUser, rowPass, outError, subLogin);
        rightPane.getChildren().addAll(lblLogin, form);

        // ── Layout ──
        HBox root = new HBox(leftPane, rightPane);
        setScene(FxHelper.createScene(root, 900, 600));

        // Animation
        FadeTransition ft = new FadeTransition(Duration.millis(800), rightPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void handleLogin() {
        String username = inUsername.getText().trim();
        String password = inPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        User user;
        try {
            user = userDAO.checkLogin(username, password);
        } catch (Exception ex) {
            showError("Lỗi kết nối CSDL.");
            return;
        }

        if (user == null) {
            showError("Thông tin đăng nhập không chính xác.");
            return;
        }

        close();
        switch (user.getRole()) {
            case "Admin" -> new AdminHomeFrm(user).show();
            case "Lecturer" -> new LecturerHomeFrm(user).show();
            default -> new StudentHomeFrm(user).show();
        }
    }

    private void showError(String msg) {
        outError.setText(msg);
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), outError);
        shake.setByX(6);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }
}
