package com.universe.view;

import com.universe.dao.CourseDAO;
import com.universe.dao.CourseRecordDAO;
import com.universe.entity.Course;
import com.universe.entity.CourseRecord;
import com.universe.entity.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Giao diện tìm kiếm học phần để đăng ký (SearchCourseRegistrationFrm).
 */
public class SearchCourseRegistrationFrm extends VBox {

    private final User currentUser;
    private final CourseDAO courseDAO = new CourseDAO();
    
    private final TextField inSearch = new TextField();
    private final TableView<Course> tblCourse = new TableView<>();
    private final ObservableList<Course> data = FXCollections.observableArrayList();
    
    private final CourseRecordDAO courseRecordDAO = new CourseRecordDAO();
    private final TableView<CourseRecord> tblEnrolled = new TableView<>();
    private final ObservableList<CourseRecord> enrolledData = FXCollections.observableArrayList();

    public SearchCourseRegistrationFrm(User currentUser) {
        this(currentUser, "");
    }

    public SearchCourseRegistrationFrm(User currentUser, String initialQuery) {
        this.currentUser = currentUser;
        buildUI();
        inSearch.setText(initialQuery == null ? "" : initialQuery);
        search();
        loadEnrolled();
    }
    
    private void loadEnrolled() {
        enrolledData.setAll(courseRecordDAO.viewGrade(currentUser.getId()));
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        setSpacing(24);

        Label title = new Label("Đăng ký Học phần");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("TÌM KIẾM HỌC PHẦN ĐỂ ĐĂNG KÝ");
        subtitle.getStyleClass().add("section-kicker");
        VBox header = new VBox(8, subtitle, title);

        // ── Controls ──
        HBox controls = new HBox(16);
        controls.setAlignment(Pos.CENTER_LEFT);

        inSearch.setPromptText("Nhập mã hoặc tên học phần...");
        inSearch.setPrefWidth(300);
        inSearch.setOnAction(e -> search());

        Button btnSearch = new Button("Tìm kiếm");
        btnSearch.getStyleClass().add("btn-secondary");
        btnSearch.setOnAction(e -> search());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnNext = new Button("Xem danh sách Lớp HP ➔");
        btnNext.getStyleClass().add("btn-primary");
        btnNext.setOnAction(e -> nextStep());

        controls.getChildren().addAll(inSearch, btnSearch, spacer, btnNext);

        // ── Table ──
        VBox tableCard = new VBox();
        tableCard.getStyleClass().add("card");
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        TableColumn<Course, String> colId = new TableColumn<>("Mã HP");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(120);

        TableColumn<Course, String> colName = new TableColumn<>("Tên học phần");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(350);

        TableColumn<Course, Integer> colCredits = new TableColumn<>("Số TC");
        colCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));
        colCredits.setPrefWidth(80);

        TableColumn<Course, String> colDept = new TableColumn<>("Khoa");
        colDept.setCellValueFactory(new PropertyValueFactory<>("department"));
        colDept.setPrefWidth(200);

        tblCourse.getColumns().addAll(colId, colName, colCredits, colDept);
        tblCourse.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblCourse.setPlaceholder(new Label("Chưa có dữ liệu học phần."));
        tblCourse.setItems(data);

        tblCourse.setRowFactory(tv -> {
            TableRow<Course> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    nextStep();
                }
            });
            return row;
        });

        VBox.setVgrow(tblCourse, Priority.ALWAYS);
        tableCard.getChildren().add(tblCourse);

        // ── Enrolled Table ──
        Label lblEnrolled = new Label("Các học phần đã đăng ký");
        lblEnrolled.getStyleClass().add("section-title");

        TableColumn<CourseRecord, String> colClassId = new TableColumn<>("Lớp HP");
        colClassId.setCellValueFactory(new PropertyValueFactory<>("classSectionName"));
        colClassId.setPrefWidth(250);

        TableColumn<CourseRecord, String> colDate = new TableColumn<>("Ngày đăng ký");
        colDate.setCellValueFactory(new PropertyValueFactory<>("enrolledAt"));
        colDate.setPrefWidth(120);

        TableColumn<CourseRecord, String> colStatusRecord = new TableColumn<>("Trạng thái");
        colStatusRecord.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatusRecord.setPrefWidth(120);

        tblEnrolled.getColumns().addAll(colClassId, colDate, colStatusRecord);
        tblEnrolled.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblEnrolled.setPlaceholder(new Label("Chưa đăng ký học phần nào."));
        tblEnrolled.setItems(enrolledData);
        VBox.setVgrow(tblEnrolled, Priority.ALWAYS);

        getChildren().addAll(header, controls, tableCard, lblEnrolled, tblEnrolled);
    }

    private void search() {
        String kw = inSearch.getText().trim();
        List<Course> list = courseDAO.searchCourse(kw);
        data.setAll(list);
    }

    private void nextStep() {
        Course selected = tblCourse.getSelectionModel().getSelectedItem();
        if (selected == null) {
            FxHelper.showWarning("Vui lòng chọn một học phần.");
            return;
        }
        
        // Chuyển sang View danh sách lớp học phần
        if (!FxHelper.replaceContent(this, new ClassSectionRegistrationFrm(currentUser, selected))) {
            FxHelper.showError("Không thể mở danh sách lớp học phần.");
        }
    }
}
