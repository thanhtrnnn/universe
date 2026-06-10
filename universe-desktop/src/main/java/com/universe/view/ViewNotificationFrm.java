package com.universe.view;

import com.universe.dao.NotificationDAO;
import com.universe.entity.Notification;
import com.universe.entity.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Giao diện xem thông báo (ViewNotificationFrm) - Dành cho Sinh viên (và có thể cả Giảng viên).
 */
public class ViewNotificationFrm extends VBox {

    private final User currentUser;
    private final NotificationDAO notificationDAO = new NotificationDAO();

    private final ListView<Notification> listNotification = new ListView<>();
    private final ObservableList<Notification> data = FXCollections.observableArrayList();

    public ViewNotificationFrm(User currentUser) {
        this.currentUser = currentUser;
        buildUI();
        loadData();
    }

    private void buildUI() {
        setSpacing(24);

        Label title = new Label("Hộp thư Thông báo");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("TIN TỨC VÀ CẬP NHẬT TỪ HỆ THỐNG");
        subtitle.getStyleClass().add("section-kicker");
        VBox header = new VBox(8, subtitle, title);

        // ── Controls ──
        HBox controls = new HBox();
        controls.setAlignment(Pos.CENTER_RIGHT);
        Button btnRefresh = new Button("Làm mới ⟳");
        btnRefresh.getStyleClass().add("btn-secondary");
        btnRefresh.setOnAction(e -> loadData());
        controls.getChildren().add(btnRefresh);

        // ── List ──
        VBox listCard = new VBox();
        listCard.getStyleClass().add("card");
        VBox.setVgrow(listCard, Priority.ALWAYS);

        listNotification.setItems(data);
        listNotification.setPlaceholder(new Label("Chưa có thông báo nào."));
        listNotification.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        listNotification.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Notification item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    VBox row = new VBox(8);
                    row.setPadding(new Insets(16));
                    row.setStyle("-fx-border-color: transparent transparent #E2E8F0 transparent; -fx-border-width: 0 0 1 0;");

                    HBox top = new HBox();
                    Label lblTitle = new Label(item.getTitle());
                    lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: -ux-accent-cyan;");
                    
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    Label lblTime = new Label(item.getSentAt().format(formatter));
                    lblTime.getStyleClass().add("caption-text");

                    top.getChildren().addAll(lblTitle, spacer, lblTime);

                    Label lblContent = new Label(item.getContent());
                    lblContent.getStyleClass().add("body-text");
                    lblContent.setWrapText(true);

                    row.getChildren().addAll(top, lblContent);
                    
                    row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: transparent transparent #CBD5E1 transparent; -fx-border-width: 0 0 1 0;"));
                    row.setOnMouseExited(e -> row.setStyle("-fx-background-color: transparent; -fx-border-color: transparent transparent #E2E8F0 transparent; -fx-border-width: 0 0 1 0;"));

                    setGraphic(row);
                    setText(null);
                    setStyle("-fx-background-color: transparent; -fx-padding: 0;");
                }
            }
        });

        VBox.setVgrow(listNotification, Priority.ALWAYS);
        listCard.getChildren().add(listNotification);

        getChildren().addAll(header, controls, listCard);
    }

    private void loadData() {
        List<Notification> list = notificationDAO.getNotifications(currentUser.getId());
        data.setAll(list);
    }
}
