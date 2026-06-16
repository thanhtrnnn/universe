package com.universe.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Lớp tiện ích dùng chung cho giao diện JavaFX: load CSS, tạo scene, sidebar, alert.
 */
public final class FxHelper {

    private static final String CSS_PATH = "/css/universe-theme.css";

    private FxHelper() {}

    /** Tạo Scene với CSS theme. */
    public static Scene createScene(javafx.scene.Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        applyStylesheet(scene);
        applyFastScrolling(scene);
        return scene;
    }

    /** Tạo scene popup có chiều rộng đồng nhất và tự cuộn khi màn hình thấp. */
    public static Scene createFormScene(VBox formRoot, double preferredHeight) {
        formRoot.getStyleClass().add("form-dialog");
        formRoot.setFillWidth(true);

        ScrollPane scrollPane = new ScrollPane(formRoot);
        scrollPane.getStyleClass().add("form-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);

        Scene scene = new Scene(scrollPane, 560, Math.min(preferredHeight, 760));
        applyStylesheet(scene);
        applyFastScrolling(scene);
        return scene;
    }

    private static void applyFastScrolling(Scene scene) {
        javafx.event.EventDispatcher originalDispatcher = scene.getEventDispatcher();
        scene.setEventDispatcher((event, tail) -> {
            if (event instanceof javafx.scene.input.ScrollEvent scrollEvent
                    && scrollEvent.getEventType() == javafx.scene.input.ScrollEvent.SCROLL) {
                javafx.scene.input.ScrollEvent fastScroll = new javafx.scene.input.ScrollEvent(
                        scrollEvent.getSource(), scrollEvent.getTarget(),
                        scrollEvent.getEventType(),
                        scrollEvent.getX(), scrollEvent.getY(),
                        scrollEvent.getScreenX(), scrollEvent.getScreenY(),
                        scrollEvent.isShiftDown(), scrollEvent.isControlDown(),
                        scrollEvent.isAltDown(), scrollEvent.isMetaDown(),
                        scrollEvent.isDirect(), scrollEvent.isInertia(),
                        scrollEvent.getDeltaX() * 3.0, scrollEvent.getDeltaY() * 3.0,
                        scrollEvent.getTotalDeltaX() * 3.0, scrollEvent.getTotalDeltaY() * 3.0,
                        scrollEvent.getTextDeltaXUnits(), scrollEvent.getTextDeltaX() * 3.0,
                        scrollEvent.getTextDeltaYUnits(), scrollEvent.getTextDeltaY() * 3.0,
                        scrollEvent.getTouchCount(),
                        scrollEvent.getPickResult()
                );
                return originalDispatcher.dispatchEvent(fastScroll, tail);
            }
            return originalDispatcher.dispatchEvent(event, tail);
        });
    }

    /** Bọc màn hình nội dung để nút và bảng không bị che khi cửa sổ thu nhỏ. */
    public static ScrollPane createContentScroll(Node content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.getStyleClass().add("content-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);
        return scrollPane;
    }

    /** Điều hướng trong vùng nội dung mà không phụ thuộc cấu trúc parent trực tiếp. */
    public static boolean replaceContent(Node source, Node nextView) {
        Node node = source;
        while (node != null) {
            if (node instanceof StackPane stackPane
                    && stackPane.getStyleClass().contains("content-area")) {
                stackPane.getChildren().setAll(createContentScroll(nextView));
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

    private static void applyStylesheet(Scene scene) {
        String css = FxHelper.class.getResource(CSS_PATH).toExternalForm();
        scene.getStylesheets().add(css);
    }

    /** Hiện thông báo Alert. */
    public static void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }

    /** Hiện cảnh báo Alert. */
    public static void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }

    /** Hiện lỗi Alert. */
    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }

    private static void styleAlert(Alert alert) {
        try {
            String css = FxHelper.class.getResource(CSS_PATH).toExternalForm();
            alert.getDialogPane().getStylesheets().add(css);
        } catch (Exception ignore) {}
    }

    /** Tạo sidebar button. */
    public static Button createSidebarButton(String text, String icon) {
        Button btn = new Button(icon + "  " + text);
        btn.getStyleClass().add("sidebar-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    /** Tạo sidebar cơ bản với brand và role. */
    public static VBox createSidebarBase(String roleName) {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setAlignment(Pos.TOP_CENTER);

        Label brand = new Label("UniVerse");
        brand.getStyleClass().add("sidebar-brand");

        Label role = new Label(roleName);
        role.getStyleClass().add("sidebar-role");

        Separator sep = new Separator();
        sep.setMaxWidth(Double.MAX_VALUE);

        sidebar.getChildren().addAll(brand, role, sep);
        return sidebar;
    }

    /** Spacer cho sidebar (đẩy logout xuống dưới). */
    public static Region createSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    /** Tạo một Stat Card mang phong cách editorial. */
    public static VBox createStatCard(String label, String value) {
        VBox card = new VBox(8);
        card.getStyleClass().add("stat-card");

        Label lblLabel = new Label(label);
        lblLabel.getStyleClass().add("metric-label");

        Label lblValue = new Label(value);
        lblValue.getStyleClass().add("metric-number");

        card.getChildren().addAll(lblLabel, lblValue);
        return card;
    }

    /** Tạo một Badge cho Table. status = success | warning | danger | neutral */
    public static Label createBadge(String text, String status) {
        Label badge = new Label(text);
        badge.getStyleClass().addAll("badge", "badge-" + status);
        return badge;
    }
}
