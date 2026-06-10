package com.universe.view;

import com.universe.dao.AttendanceDAO;
import com.universe.dao.ClassSectionDAO;
import com.universe.dao.ClassSessionDAO;
import com.universe.entity.Attendance;
import com.universe.entity.ClassSection;
import com.universe.entity.ClassSession;
import com.universe.entity.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Giao diện quản lý điểm danh (AttendanceManageFrm).
 */
public class AttendanceManageFrm extends VBox {

    private final User currentUser;
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final ClassSectionDAO classSectionDAO = new ClassSectionDAO();
    private final ClassSessionDAO classSessionDAO = new ClassSessionDAO();

    private final ComboBox<ClassSection> inClassSection = new ComboBox<>();
    private final ComboBox<ClassSession> inClassSession = new ComboBox<>();
    private final TableView<Attendance> tblAttendance = new TableView<>();
    private final ObservableList<Attendance> data = FXCollections.observableArrayList();

    public AttendanceManageFrm(User currentUser) {
        this.currentUser = currentUser;
        buildUI();
        loadClasses();
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        setSpacing(24);

        Label title = new Label("Quản lý Điểm danh");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("THEO DÕI & CHỈNH SỬA KẾT QUẢ ĐIỂM DANH");
        subtitle.getStyleClass().add("section-kicker");
        VBox header = new VBox(8, subtitle, title);

        // ── Controls ──
        HBox controls = new HBox(16);
        controls.setAlignment(Pos.CENTER_LEFT);

        inClassSection.setPromptText("Chọn lớp học phần");
        inClassSection.setPrefWidth(280);
        inClassSection.valueProperty().addListener((obs, oldValue, newValue) -> loadSessions(newValue));

        inClassSession.setPromptText("Chọn buổi học");
        inClassSession.setPrefWidth(330);
        inClassSession.valueProperty().addListener((obs, oldValue, newValue) -> loadData());

        Button btnSearch = new Button("Xem danh sách");
        btnSearch.getStyleClass().add("btn-secondary");
        btnSearch.setMinWidth(Region.USE_PREF_SIZE);
        btnSearch.setOnAction(e -> loadData());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnSave = new Button("Lưu trạng thái điểm danh");
        btnSave.getStyleClass().add("btn-primary");
        btnSave.setMinWidth(Region.USE_PREF_SIZE);
        btnSave.setOnAction(e -> saveAttendance());

        controls.getChildren().addAll(inClassSection, inClassSession, btnSearch, spacer, btnSave);

        // ── Table ──
        VBox tableCard = new VBox();
        tableCard.getStyleClass().add("card");
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        TableColumn<Attendance, String> colStudentId = new TableColumn<>("Mã Sinh viên");
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colStudentId.setPrefWidth(120);
        TableColumn<Attendance, String> colStudentName = new TableColumn<>("Họ và Tên");
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colStudentName.setPrefWidth(220);

        TableColumn<Attendance, String> colTime = new TableColumn<>("Thời gian quét QR");
        colTime.setCellValueFactory(new PropertyValueFactory<>("attendedAt"));
        colTime.setPrefWidth(150);

        TableColumn<Attendance, String> colMethod = new TableColumn<>("Phương thức");
        colMethod.setCellValueFactory(new PropertyValueFactory<>("method"));
        colMethod.setPrefWidth(120);

        TableColumn<Attendance, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            private final ComboBox<String> combo = new ComboBox<>(FXCollections.observableArrayList("PRESENT", "ABSENT", "LATE"));
            {
                combo.setOnAction(e -> {
                    Attendance a = getTableRow().getItem();
                    if (a != null && combo.getValue() != null) {
                        a.setStatus(combo.getValue());
                    }
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    combo.setValue(item);
                    setGraphic(combo);
                }
            }
        });
        colStatus.setPrefWidth(120);

        tblAttendance.getColumns().addAll(colStudentId, colStudentName, colTime, colMethod, colStatus);
        tblAttendance.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblAttendance.setPlaceholder(new Label("Nhập mã buổi học để xem kết quả điểm danh"));
        tblAttendance.setItems(data);

        // Click to toggle status was removed since we use ComboBox now
        tblAttendance.setRowFactory(tv -> new TableRow<>());

        VBox.setVgrow(tblAttendance, Priority.ALWAYS);
        tableCard.getChildren().add(tblAttendance);

        getChildren().addAll(header, controls, tableCard);
    }

    private void loadData() {
        ClassSession session = inClassSession.getValue();
        if (session == null) {
            data.clear();
            return;
        }
        List<Attendance> list = attendanceDAO.viewAttendance(session.getId());
        data.setAll(list);
    }

    private void loadClasses() {
        List<ClassSection> classes = classSectionDAO.getByLecturer(currentUser.getId());
        inClassSection.setItems(FXCollections.observableArrayList(classes));
        if (!classes.isEmpty()) {
            inClassSection.setValue(classes.get(0));
        }
    }

    private void loadSessions(ClassSection classSection) {
        List<ClassSession> sessions = classSection == null
                ? List.of()
                : classSessionDAO.getSessions(classSection.getId());
        inClassSession.setItems(FXCollections.observableArrayList(sessions));
        if (!sessions.isEmpty()) {
            inClassSession.setValue(sessions.get(0));
        } else {
            data.clear();
        }
    }

    private void saveAttendance() {
        if (data.isEmpty()) {
            FxHelper.showWarning("Không có dữ liệu điểm danh để lưu.");
            return;
        }
        boolean ok = true;
        for (Attendance a : data) {
            if (!attendanceDAO.markManualAttendance(a)) {
                ok = false;
            }
        }
        if (ok) {
            FxHelper.showInfo("Lưu trạng thái điểm danh thành công!");
        } else {
            FxHelper.showError("Có lỗi xảy ra khi lưu điểm danh.");
        }
    }
}
