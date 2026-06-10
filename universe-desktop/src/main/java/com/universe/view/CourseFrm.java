package com.universe.view;

import com.universe.dao.CourseDAO;
import com.universe.entity.Course;
import com.universe.entity.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Giao diện quản lý học phần (CourseFrm).
 */
public class CourseFrm extends VBox {

    private final User currentUser;
    private final CourseDAO courseDAO = new CourseDAO();
    
    private final TableView<Course> tblCourse = new TableView<>();
    private final ObservableList<Course> data = FXCollections.observableArrayList();

    public CourseFrm(User currentUser) {
        this.currentUser = currentUser;
        buildUI();
        loadData();
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        setSpacing(24);

        // ── Header ──
        Label title = new Label("Quản lý Học phần");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("DANH MỤC HỌC PHẦN ĐÀO TẠO");
        subtitle.getStyleClass().add("section-kicker");
        VBox header = new VBox(8, subtitle, title);

        // ── Controls ──
        HBox controls = new HBox(16);
        controls.setAlignment(Pos.CENTER_LEFT);

        Button btnRefresh = new Button("Làm mới");
        btnRefresh.getStyleClass().add("btn-secondary");
        btnRefresh.setOnAction(e -> loadData());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAdd = new Button("Thêm mới");
        btnAdd.getStyleClass().add("btn-primary");
        btnAdd.setOnAction(e -> new CreateCourseFrm(this::loadData).show());

        Button btnEdit = new Button("Sửa");
        btnEdit.getStyleClass().add("btn-secondary");
        btnEdit.setOnAction(e -> {
            Course selected = tblCourse.getSelectionModel().getSelectedItem();
            if (selected == null) {
                FxHelper.showWarning("Vui lòng chọn một học phần để sửa");
                return;
            }
            new UpdateCourseFrm(selected, this::loadData).show();
        });

        Button btnDelete = new Button("Xóa");
        btnDelete.getStyleClass().add("btn-accent");
        btnDelete.setOnAction(e -> {
            Course selected = tblCourse.getSelectionModel().getSelectedItem();
            if (selected == null) {
                FxHelper.showWarning("Vui lòng chọn một học phần để xóa");
                return;
            }
            try {
                if (courseDAO.deleteCourse(selected.getId())) {
                    FxHelper.showInfo("Đã xóa học phần thành công");
                    loadData();
                } else {
                    FxHelper.showError("Không thể xóa học phần");
                }
            } catch (Exception ex) {
                FxHelper.showError("Lỗi khi xóa học phần (Dữ liệu có thể đang được tham chiếu): " + ex.getMessage());
            }
        });

        controls.getChildren().addAll(btnRefresh, spacer, btnAdd, btnEdit, btnDelete);

        // ── Table ──
        VBox tableCard = new VBox();
        tableCard.getStyleClass().add("card");
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        TableColumn<Course, String> colId = new TableColumn<>("Mã HP");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(120);

        TableColumn<Course, String> colName = new TableColumn<>("Tên học phần");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(300);

        TableColumn<Course, Integer> colCredits = new TableColumn<>("Số TC");
        colCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));
        colCredits.setPrefWidth(80);

        TableColumn<Course, String> colDept = new TableColumn<>("Khoa quản lý");
        colDept.setCellValueFactory(new PropertyValueFactory<>("department"));
        colDept.setPrefWidth(200);

        tblCourse.getColumns().addAll(colId, colName, colCredits, colDept);
        tblCourse.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblCourse.setPlaceholder(new Label("Chưa có học phần nào"));
        tblCourse.setItems(data);

        VBox.setVgrow(tblCourse, Priority.ALWAYS);
        tableCard.getChildren().add(tblCourse);

        getChildren().addAll(header, controls, tableCard);
    }

    private void loadData() {
        List<Course> list = courseDAO.getListCourse();
        data.setAll(list);
    }
}
