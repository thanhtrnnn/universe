package com.universe.view;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.universe.dao.ClassSectionDAO;
import com.universe.dao.ClassSessionDAO;
import com.universe.dao.QRCodeDAO;
import com.universe.entity.ClassSection;
import com.universe.entity.ClassSession;
import com.universe.entity.QRCode;
import com.universe.entity.User;
import com.universe.util.LocationCalibrationClient;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Tạo QR điểm danh động: payload thay đổi mỗi 5 giây, còn phiên QR và tọa độ
 * được lưu trong CSDL theo đúng buổi học.
 */
public class QRCodeFrm extends VBox {

    private static final int ROTATION_SECONDS = 5;
    private static final int VALID_MINUTES = 15;
    private static final double GEOFENCE_RADIUS_METERS = 50.0;
    private static final double MAX_CALIBRATION_ACCURACY_METERS = 25.0;

    private final User currentUser;
    private final ClassSectionDAO classSectionDAO = new ClassSectionDAO();
    private final ClassSessionDAO classSessionDAO = new ClassSessionDAO();
    private final QRCodeDAO qrCodeDAO = new QRCodeDAO();
    private final LocationCalibrationClient locationCalibrationClient;

    private final ComboBox<ClassSection> inClassSection = new ComboBox<>();
    private final ComboBox<ClassSession> inSession = new ComboBox<>();
    private final TextField inLat = new TextField();
    private final TextField inLng = new TextField();
    private final TextField inRadius = new TextField("50");
    private final Button btnDetectLocation = new Button("Lấy vị trí bằng điện thoại");
    private final Label lblLocationStatus = new Label(
            "Chọn buổi học, tạo QR lấy vị trí rồi dùng GPS điện thoại giảng viên để quét.");

    private final ImageView qrView = new ImageView();
    private final Label lblCountdown = new Label("Chưa phát hành mã QR.");
    private final Label lblStatus = new Label();

    private Timeline rotationTimeline;
    private Timeline calibrationTimeline;
    private QRCode currentQr;
    private ClassSession currentSession;
    private int secondsRemaining = ROTATION_SECONDS;
    private int calibrationSecondsRemaining;
    private String calibrationToken;
    private boolean calibrationPollInFlight;

    public QRCodeFrm(User currentUser) {
        this.currentUser = currentUser;
        this.locationCalibrationClient = new LocationCalibrationClient(
                currentUser.getUsername(),
                currentUser.getPassword());
        buildUI();
        loadClasses();
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (oldScene != null && newScene == null) {
                stopRotation();
                stopCalibration(false);
            }
        });
    }

    private void buildUI() {
        setSpacing(24);
        setMaxWidth(940);

        Label title = new Label("Tạo Mã QR Điểm danh");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("QR ĐỘNG 5 GIÂY KẾT HỢP TỌA ĐỘ GPS");
        subtitle.getStyleClass().add("section-kicker");
        VBox header = new VBox(8, subtitle, title);

        HBox content = new HBox(28);
        content.setAlignment(Pos.TOP_LEFT);

        VBox formCard = new VBox(16);
        formCard.getStyleClass().add("card");
        formCard.setMinWidth(410);
        HBox.setHgrow(formCard, Priority.ALWAYS);

        inClassSection.valueProperty().addListener((obs, oldValue, newValue) -> loadSessions(newValue));
        inSession.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (oldValue != newValue) {
                stopCalibration(true);
                clearCapturedLocation();
            }
        });
        inClassSection.setMaxWidth(Double.MAX_VALUE);
        inSession.setMaxWidth(Double.MAX_VALUE);
        inLat.setPromptText("Nhận tự động từ GPS điện thoại");
        inLng.setPromptText("Nhận tự động từ GPS điện thoại");
        inLat.setEditable(false);
        inLng.setEditable(false);
        inLat.setFocusTraversable(false);
        inLng.setFocusTraversable(false);
        inRadius.setEditable(false);
        inRadius.setFocusTraversable(false);

        Button btnCreateSession = new Button("Tạo buổi học hôm nay");
        btnCreateSession.getStyleClass().add("btn-secondary");
        btnCreateSession.setOnAction(e -> createSession());

        btnDetectLocation.getStyleClass().add("btn-secondary");
        btnDetectLocation.setMaxWidth(Double.MAX_VALUE);
        btnDetectLocation.setOnAction(e -> detectLocation());

        lblLocationStatus.getStyleClass().add("location-status");
        lblLocationStatus.setWrapText(true);
        lblLocationStatus.setMaxWidth(Double.MAX_VALUE);

        VBox locationActions = new VBox(8, btnDetectLocation, lblLocationStatus);

        formCard.getChildren().addAll(
                createFormRow("Lớp học phần", inClassSection),
                createFormRow("Buổi học", inSession),
                btnCreateSession,
                createFormRow("Vĩ độ phòng học", inLat),
                createFormRow("Kinh độ phòng học", inLng),
                locationActions,
                createFormRow("Bán kính geofence cố định (mét)", inRadius)
        );

        Button btnGenerate = new Button("Phát hành QR động");
        btnGenerate.getStyleClass().add("btn-primary");
        btnGenerate.setOnAction(e -> generateQR());

        Button btnDeactivate = new Button("Đóng điểm danh");
        btnDeactivate.getStyleClass().add("btn-danger");
        btnDeactivate.setOnAction(e -> deactivateQR());

        HBox actions = new HBox(12, btnGenerate, btnDeactivate);
        actions.setPadding(new Insets(12, 0, 0, 0));
        formCard.getChildren().add(actions);

        VBox previewCard = new VBox(14);
        previewCard.getStyleClass().add("card");
        previewCard.setAlignment(Pos.TOP_CENTER);
        previewCard.setMinWidth(340);

        qrView.setFitWidth(260);
        qrView.setFitHeight(260);
        qrView.setPreserveRatio(true);

        lblCountdown.getStyleClass().add("section-title");
        lblCountdown.setWrapText(true);
        lblCountdown.setTextAlignment(TextAlignment.CENTER);
        lblCountdown.setMaxWidth(300);
        lblCountdown.setMinHeight(52);

        lblStatus.getStyleClass().add("body-text");
        lblStatus.setWrapText(true);
        lblStatus.setTextAlignment(TextAlignment.CENTER);
        lblStatus.setMaxWidth(300);
        lblStatus.setMinHeight(48);

        previewCard.getChildren().addAll(qrView, lblCountdown, lblStatus);
        content.getChildren().addAll(formCard, previewCard);
        getChildren().addAll(header, content);
    }

    private VBox createFormRow(String labelText, Region input) {
        VBox row = new VBox(7);
        Label label = new Label(labelText);
        label.getStyleClass().add("caption-text");
        input.setMaxWidth(Double.MAX_VALUE);
        row.getChildren().addAll(label, input);
        return row;
    }

    private void loadClasses() {
        List<ClassSection> classes = "Lecturer".equalsIgnoreCase(currentUser.getRole())
                ? classSectionDAO.getByLecturer(currentUser.getId())
                : classSectionDAO.getListClassSection();
        inClassSection.setItems(FXCollections.observableArrayList(classes));
        if (!classes.isEmpty()) {
            inClassSection.setValue(classes.get(0));
        }
    }

    private void loadSessions(ClassSection classSection) {
        stopRotation();
        stopCalibration(true);
        currentQr = null;
        currentSession = null;
        qrView.setImage(null);
        lblCountdown.setText("Chưa phát hành mã QR.");
        lblStatus.setText("");

        List<ClassSession> sessions = classSection == null
                ? List.of()
                : classSessionDAO.getSessions(classSection.getId());
        inSession.setItems(FXCollections.observableArrayList(sessions));
        if (!sessions.isEmpty()) {
            inSession.setValue(sessions.get(0));
        }
    }

    private void createSession() {
        ClassSection selectedClass = inClassSection.getValue();
        if (selectedClass == null) {
            FxHelper.showWarning("Vui lòng chọn lớp học phần.");
            return;
        }
        try {
            ClassSession session = classSessionDAO.createSession(selectedClass.getId());
            inSession.getItems().add(0, session);
            inSession.setValue(session);
            FxHelper.showInfo("Đã tạo buổi học hôm nay cho " + selectedClass.getClassId() + ".");
        } catch (RuntimeException ex) {
            FxHelper.showError("Không thể tạo buổi học: " + ex.getMessage());
        }
    }

    private void detectLocation() {
        ClassSection selectedClass = inClassSection.getValue();
        ClassSession selectedSession = inSession.getValue();
        if (selectedClass == null || selectedSession == null) {
            FxHelper.showWarning(
                    "Vui lòng chọn lớp và buổi học trước khi lấy vị trí.");
            return;
        }
        if (currentQr != null) {
            FxHelper.showWarning(
                    "Hãy đóng phiên điểm danh hiện tại trước khi lấy lại vị trí.");
            return;
        }

        stopCalibration(true);
        clearCapturedLocation();
        setLocationLoading(true);
        setLocationStatus(
                "Đang tạo QR lấy vị trí dùng một lần...",
                "location-status-info");

        CompletableFuture.supplyAsync(() -> locationCalibrationClient.create(
                        selectedSession.getId(),
                        selectedClass.getId()))
                .whenComplete((calibration, error) -> Platform.runLater(() -> {
                    if (error != null) {
                        setLocationLoading(false);
                        setLocationStatus(
                                getAsyncErrorMessage(error),
                                "location-status-error");
                        return;
                    }

                    calibrationToken = calibration.token();
                    calibrationSecondsRemaining = calibration.expiresInSeconds();
                    try {
                        renderQrPayload("UNIVERSE_LOCATION|" + calibrationToken);
                    } catch (RuntimeException ex) {
                        stopCalibration(true);
                        setLocationLoading(false);
                        setLocationStatus(
                                ex.getMessage(),
                                "location-status-error");
                        return;
                    }
                    lblCountdown.setText(
                            "QR lấy vị trí hết hạn sau "
                                    + calibrationSecondsRemaining + " giây");
                    lblStatus.setText(
                            "Trên điện thoại giảng viên: mở UniVerse → "
                                    + "Đặt vị trí phòng học bằng GPS → quét QR này.");
                    setLocationStatus(
                            "Đang chờ điện thoại gửi GPS sai số tối đa "
                                    + Math.round(calibration.maxAccuracyMeters()) + " m...",
                            "location-status-info");
                    startCalibrationPolling(calibrationToken);
                }));
    }

    private void startCalibrationPolling(String token) {
        pollCalibration(token);
        calibrationTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (!token.equals(calibrationToken)) {
                return;
            }
            calibrationSecondsRemaining--;
            if (calibrationSecondsRemaining <= 0) {
                stopCalibration(true);
                setLocationLoading(false);
                lblCountdown.setText("QR lấy vị trí đã hết hạn.");
                lblStatus.setText("Nhấn “Lấy vị trí bằng điện thoại” để tạo QR mới.");
                setLocationStatus(
                        "Chưa nhận được GPS trong hai phút.",
                        "location-status-error");
                return;
            }
            lblCountdown.setText(
                    "QR lấy vị trí hết hạn sau "
                            + calibrationSecondsRemaining + " giây");
            pollCalibration(token);
        }));
        calibrationTimeline.setCycleCount(Timeline.INDEFINITE);
        calibrationTimeline.play();
    }

    private void pollCalibration(String token) {
        if (calibrationPollInFlight || !token.equals(calibrationToken)) {
            return;
        }
        calibrationPollInFlight = true;
        CompletableFuture.supplyAsync(() -> locationCalibrationClient.status(token))
                .whenComplete((status, error) -> Platform.runLater(() -> {
                    calibrationPollInFlight = false;
                    if (!token.equals(calibrationToken)) {
                        return;
                    }
                    if (error != null) {
                        stopCalibration(true);
                        setLocationLoading(false);
                        setLocationStatus(
                                getAsyncErrorMessage(error),
                                "location-status-error");
                        return;
                    }
                    if (status.ready()) {
                        showLocationResult(status);
                    }
                }));
    }

    private void showLocationResult(
            LocationCalibrationClient.CalibrationStatus coordinates) {
        if (coordinates.accuracyMeters() > MAX_CALIBRATION_ACCURACY_METERS) {
            stopCalibration(true);
            setLocationLoading(false);
            setLocationStatus(
                    "GPS điện thoại chưa đạt sai số tối đa 25 m.",
                    "location-status-error");
            return;
        }

        inLat.setText(String.format(Locale.US, "%.6f", coordinates.latitude()));
        inLng.setText(String.format(Locale.US, "%.6f", coordinates.longitude()));
        stopCalibration(true);
        setLocationLoading(false);
        lblCountdown.setText("Đã nhận vị trí chính xác từ điện thoại.");
        lblStatus.setText("Tâm geofence đã sẵn sàng. Bây giờ có thể phát hành QR điểm danh.");
        setLocationStatus(
                "Đã tự điền tâm geofence, sai số GPS khoảng "
                        + Math.round(coordinates.accuracyMeters()) + " m.",
                "location-status-success");
    }

    private void setLocationLoading(boolean loading) {
        btnDetectLocation.setDisable(loading);
        btnDetectLocation.setText(
                loading ? "Đang chờ GPS điện thoại..." : "Lấy vị trí bằng điện thoại");
    }

    private void setLocationStatus(String message, String statusClass) {
        lblLocationStatus.setText(message);
        lblLocationStatus.getStyleClass().removeAll(
                "location-status-info",
                "location-status-success",
                "location-status-error");
        lblLocationStatus.getStyleClass().add(statusClass);
    }

    private String getAsyncErrorMessage(Throwable error) {
        Throwable cause = error;
        while ((cause instanceof CompletionException || cause.getCause() != null)
                && cause.getCause() != null) {
            cause = cause.getCause();
        }
        String message = cause.getMessage();
        return message == null || message.isBlank()
                ? "Không thể nhận vị trí từ điện thoại."
                : message;
    }

    private void generateQR() {
        ClassSession selectedSession = inSession.getValue();
        if (selectedSession == null) {
            FxHelper.showWarning("Vui lòng chọn hoặc tạo một buổi học.");
            return;
        }

        try {
            if (inLat.getText().isBlank() || inLng.getText().isBlank()) {
                FxHelper.showWarning(
                        "Hãy nhấn “Lấy vị trí bằng điện thoại” và quét QR trước.");
                return;
            }
            double latitude = Double.parseDouble(inLat.getText().trim());
            double longitude = Double.parseDouble(inLng.getText().trim());
            if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90
                    || !Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
                throw new NumberFormatException();
            }

            currentSession = selectedSession;
            stopCalibration(false);
            currentQr = qrCodeDAO.generateQr(
                    selectedSession,
                    latitude,
                    longitude,
                    GEOFENCE_RADIUS_METERS,
                    VALID_MINUTES);
            renderRotatingCode();
            startRotation();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
            lblStatus.setText("QR có hiệu lực đến "
                    + currentQr.getExpiredAt().format(formatter)
                    + ". Sinh viên phải ở trong geofence 50 m.");
        } catch (NumberFormatException ex) {
            FxHelper.showWarning(
                    "Vĩ độ phải từ -90 đến 90 và kinh độ từ -180 đến 180.");
        } catch (RuntimeException ex) {
            FxHelper.showError("Không thể tạo QR: " + ex.getMessage());
        }
    }

    private void startRotation() {
        stopRotation();
        secondsRemaining = ROTATION_SECONDS;
        lblCountdown.setText("Mã QR tự động thay đổi sau " + secondsRemaining + " giây");

        rotationTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsRemaining--;
            if (secondsRemaining <= 0) {
                renderRotatingCode();
                secondsRemaining = ROTATION_SECONDS;
            }
            lblCountdown.setText("Mã QR tự động thay đổi sau "
                    + secondsRemaining + " giây");
        }));
        rotationTimeline.setCycleCount(Timeline.INDEFINITE);
        rotationTimeline.play();
    }

    private void renderRotatingCode() {
        if (currentQr == null || currentSession == null) {
            return;
        }
        try {
            long timeSlot = System.currentTimeMillis() / (ROTATION_SECONDS * 1000L);
            String signature = createSignature(currentQr.getId(), currentSession.getId(), timeSlot);
            String payload = String.join("|",
                    "UNIVERSE",
                    currentQr.getId(),
                    currentSession.getId(),
                    Long.toString(timeSlot),
                    Double.toString(currentQr.getLatitude()),
                    Double.toString(currentQr.getLongitude()),
                    Double.toString(currentQr.getRadius()),
                    signature);

            renderQrPayload(payload);
        } catch (Exception ex) {
            stopRotation();
            lblCountdown.setText("Không thể hiển thị mã QR.");
            lblStatus.setText(ex.getMessage());
        }
    }

    private String createSignature(String qrId, String sessionId, long timeSlot) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(
                (qrId + "|" + sessionId + "|" + timeSlot).getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash, 0, 8);
    }

    private void deactivateQR() {
        ClassSession session = currentSession != null ? currentSession : inSession.getValue();
        if (session == null) {
            FxHelper.showWarning("Chưa chọn buổi học cần đóng điểm danh.");
            return;
        }
        try {
            if (qrCodeDAO.deactivateBySession(session.getId())) {
                stopRotation();
                qrView.setImage(null);
                currentQr = null;
                lblCountdown.setText("Điểm danh đã đóng.");
                lblStatus.setText("Mã QR của buổi học " + session.getId() + " không còn hiệu lực.");
            }
        } catch (RuntimeException ex) {
            FxHelper.showError("Không thể đóng điểm danh: " + ex.getMessage());
        }
    }

    private void stopRotation() {
        if (rotationTimeline != null) {
            rotationTimeline.stop();
            rotationTimeline = null;
        }
    }

    private void stopCalibration(boolean clearPreview) {
        if (calibrationTimeline != null) {
            calibrationTimeline.stop();
            calibrationTimeline = null;
        }
        calibrationToken = null;
        calibrationPollInFlight = false;
        if (clearPreview && currentQr == null) {
            qrView.setImage(null);
        }
    }

    private void clearCapturedLocation() {
        inLat.clear();
        inLng.clear();
        setLocationStatus(
                "Chọn buổi học, tạo QR lấy vị trí rồi dùng GPS điện thoại giảng viên để quét.",
                "location-status-info");
    }

    private void renderQrPayload(String payload) {
        try {
            BitMatrix matrix = new QRCodeWriter().encode(
                    payload,
                    BarcodeFormat.QR_CODE,
                    320,
                    320);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", output);
            qrView.setImage(new Image(new ByteArrayInputStream(output.toByteArray())));
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Không thể hiển thị mã QR: " + ex.getMessage(),
                    ex);
        }
    }
}
