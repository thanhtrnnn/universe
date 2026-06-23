package com.universe.view;

import com.universe.dao.ClassSectionDAO;
import com.universe.dao.CourseRecordDAO;
import com.universe.entity.ClassSection;
import com.universe.entity.Course;
import com.universe.entity.CourseRecord;
import com.universe.entity.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

/**
 * Giao diện đăng ký lớp học phần (ClassSectionRegistrationFrm).
 */
public class ClassSectionRegistrationFrm extends VBox implements ActionListener {

    private final User currentUser;
    private final Course course;
    private final ClassSectionDAO classSectionDAO = new ClassSectionDAO();
    private final CourseRecordDAO recordDAO = new CourseRecordDAO();

    private final TableView<ClassSection> tblClass = new TableView<>();
    private final ObservableList<ClassSection> data = FXCollections.observableArrayList();

    private final Button btnBack = new Button("🡠 Quay lại");
    private final Button btnRegister = new Button("Xác nhận đăng ký");

    public ClassSectionRegistrationFrm(User currentUser, Course course) {
        this.currentUser = currentUser;
        this.course = course;
        buildUI();
        loadData();
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        setSpacing(24);

        Label title = new Label("Lớp Học phần: " + course.getName());
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("CHỌN LỚP ĐỂ ĐĂNG KÝ MÔN " + course.getId());
        subtitle.getStyleClass().add("section-kicker");
        VBox header = new VBox(8, subtitle, title);

        // ── Controls ──
        HBox controls = new HBox(16);
        controls.setAlignment(Pos.CENTER_LEFT);

        btnBack.getStyleClass().add("btn-secondary");
        btnBack.setOnAction(this);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        btnRegister.getStyleClass().add("btn-primary");
        btnRegister.setOnAction(this);

        controls.getChildren().addAll(btnBack, spacer, btnRegister);

        // ── Table ──
        VBox tableCard = new VBox();
        tableCard.getStyleClass().add("card");
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        TableColumn<ClassSection, String> colClassId = new TableColumn<>("Mã Lớp");
        colClassId.setCellValueFactory(new PropertyValueFactory<>("classId"));
        colClassId.setPrefWidth(120);

        TableColumn<ClassSection, String> colName = new TableColumn<>("Tên Lớp");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(220);

        TableColumn<ClassSection, Integer> colMax = new TableColumn<>("Sĩ số tối đa");
        colMax.setCellValueFactory(new PropertyValueFactory<>("maxStudents"));
        colMax.setPrefWidth(100);

        TableColumn<ClassSection, String> colLecturer = new TableColumn<>("Giảng viên");
        colLecturer.setCellValueFactory(new PropertyValueFactory<>("lecturerId"));
        colLecturer.setPrefWidth(150);

        TableColumn<ClassSection, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    String statusClass = item.equalsIgnoreCase("open") ? "success" : "danger";
                    setGraphic(FxHelper.createBadge(item, statusClass));
                }
            }
        });
        colStatus.setPrefWidth(100);

        tblClass.getColumns().addAll(colClassId, colName, colMax, colLecturer, colStatus);
        tblClass.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblClass.setPlaceholder(new Label("Học phần này hiện chưa có lớp nào mở."));
        tblClass.setItems(data);

        tblClass.setRowFactory(tv -> {
            TableRow<ClassSection> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    registerClass();
                }
            });
            return row;
        });

        VBox.setVgrow(tblClass, Priority.ALWAYS);
        tableCard.getChildren().add(tblClass);

        getChildren().addAll(header, controls, tableCard);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnBack) {
            back();
        } else if (src == btnRegister) {
            registerClass();
        }
    }

    private void loadData() {
        List<ClassSection> list = classSectionDAO.getListClassSectionRegistration(course.getId());
        data.setAll(list);
    }

    private void back() {
        if (!FxHelper.replaceContent(this, new SearchCourseRegistrationFrm(currentUser))) {
            FxHelper.showError("Không thể quay lại danh sách học phần.");
        }
    }

    private void registerClass() {
        ClassSection selected = tblClass.getSelectionModel().getSelectedItem();
        if (selected == null) {
            FxHelper.showWarning("Vui lòng chọn một lớp để đăng ký.");
            return;
        }

        if (!"open".equalsIgnoreCase(selected.getStatus())) {
            FxHelper.showError("Lớp này đã đầy hoặc đã đóng đăng ký.");
            return;
        }

        if (!classSectionDAO.checkCapacity(selected.getId())) {
            classSectionDAO.registerClassSection(selected.getId()); // Đóng lớp
            loadData();
            FxHelper.showError("Lớp đã đủ sĩ số, hệ thống tự động đóng lớp.");
            return;
        }

        CourseRecord record = new CourseRecord(
                null, LocalDate.now(), "ENROLLED",
                null, null, null, null,
                currentUser.getId(), selected.getId()
        );

        if (recordDAO.confirmRegistration(record)) {
            classSectionDAO.registerClassSection(selected.getId()); // Cập nhật lại status nếu vừa đầy
            FxHelper.showInfo("Đăng ký thành công!");
            loadData();
        } else {
            FxHelper.showError("Đăng ký thất bại. Có thể bạn đã đăng ký lớp này rồi.");
        }
    }
}
