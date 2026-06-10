package com.universe.view;

import com.universe.dao.ClassSectionDAO;
import com.universe.entity.ClassSection;
import com.universe.entity.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
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
 * Giao diện quản lý Lớp Học phần (ClassSectionFrm).
 */
public class ClassSectionFrm extends VBox {

    private final User currentUser;
    private final ClassSectionDAO classSectionDAO = new ClassSectionDAO();
    
    private final TextField inSearch = new TextField();
    private final TableView<ClassSection> tblClass = new TableView<>();
    private final ObservableList<ClassSection> data = FXCollections.observableArrayList();

    public ClassSectionFrm(User currentUser) {
        this.currentUser = currentUser;
        buildUI();
        search();
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        setSpacing(24);

        // ── Header ──
        Label title = new Label("Quản lý Lớp học phần");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("THEO DÕI & TỔ CHỨC CÁC LỚP HỌC");
        subtitle.getStyleClass().add("section-kicker");
        VBox header = new VBox(8, subtitle, title);

        // ── Controls ──
        HBox controls = new HBox(16);
        controls.setAlignment(Pos.CENTER_LEFT);

        inSearch.setPromptText("Nhập mã lớp / Tên lớp...");
        inSearch.setPrefWidth(300);
        inSearch.setOnAction(e -> search());

        Button btnSearch = new Button("Tìm kiếm");
        btnSearch.getStyleClass().add("btn-secondary");
        btnSearch.setOnAction(e -> search());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAdd = new Button("Mở lớp mới");
        btnAdd.getStyleClass().add("btn-primary");
        btnAdd.setOnAction(e -> new CreateClassSectionFrm(this::search).show());

        Button btnEdit = new Button("Sửa");
        btnEdit.getStyleClass().add("btn-secondary");
        btnEdit.setOnAction(e -> {
            ClassSection selected = tblClass.getSelectionModel().getSelectedItem();
            if (selected == null) {
                FxHelper.showWarning("Vui lòng chọn một lớp học phần để sửa");
                return;
            }
            new UpdateClassSectionFrm(selected, this::search).show();
        });

        Button btnDelete = new Button("Xóa");
        btnDelete.getStyleClass().add("btn-accent");
        btnDelete.setOnAction(e -> {
            ClassSection selected = tblClass.getSelectionModel().getSelectedItem();
            if (selected == null) {
                FxHelper.showWarning("Vui lòng chọn một lớp học phần để xóa");
                return;
            }
            try {
                if (classSectionDAO.deleteClassSection(selected.getId())) {
                    FxHelper.showInfo("Đã xóa lớp học phần thành công");
                    search();
                } else {
                    FxHelper.showError("Không thể xóa lớp học phần");
                }
            } catch (Exception ex) {
                FxHelper.showError("Lỗi khi xóa lớp học phần (Có thể dữ liệu đang được tham chiếu): " + ex.getMessage());
            }
        });

        controls.getChildren().addAll(inSearch, btnSearch, spacer, btnAdd, btnEdit, btnDelete);

        // ── Table ──
        VBox tableCard = new VBox();
        tableCard.getStyleClass().add("card");
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        TableColumn<ClassSection, String> colClassId = new TableColumn<>("Mã Lớp");
        colClassId.setCellValueFactory(new PropertyValueFactory<>("classId"));
        colClassId.setPrefWidth(120);

        TableColumn<ClassSection, String> colName = new TableColumn<>("Tên Lớp HP");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(220);

        TableColumn<ClassSection, String> colSemester = new TableColumn<>("Học kỳ");
        colSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
        colSemester.setPrefWidth(80);

        TableColumn<ClassSection, String> colYear = new TableColumn<>("Năm học");
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colYear.setPrefWidth(100);

        TableColumn<ClassSection, Integer> colMax = new TableColumn<>("Sĩ số tối đa");
        colMax.setCellValueFactory(new PropertyValueFactory<>("maxStudents"));
        colMax.setPrefWidth(100);

        TableColumn<ClassSection, String> colCourse = new TableColumn<>("Mã HP");
        colCourse.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        colCourse.setPrefWidth(100);

        TableColumn<ClassSection, String> colLecturer = new TableColumn<>("Giảng viên");
        colLecturer.setCellValueFactory(new PropertyValueFactory<>("lecturerId"));
        colLecturer.setPrefWidth(120);

        TableColumn<ClassSection, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    String statusClass = item.equalsIgnoreCase("open") ? "success" : "warning";
                    setGraphic(FxHelper.createBadge(item, statusClass));
                }
            }
        });
        colStatus.setPrefWidth(100);

        tblClass.getColumns().addAll(colClassId, colName, colSemester, colYear, colMax, colCourse, colLecturer, colStatus);
        tblClass.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblClass.setPlaceholder(new Label("Chưa có dữ liệu"));
        tblClass.setItems(data);

        tblClass.setRowFactory(tv -> {
            TableRow<ClassSection> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    new UpdateClassSectionFrm(row.getItem(), this::search).show();
                }
            });
            return row;
        });

        VBox.setVgrow(tblClass, Priority.ALWAYS);
        tableCard.getChildren().add(tblClass);

        getChildren().addAll(header, controls, tableCard);
    }

    private void search() {
        String kw = inSearch.getText().trim();
        List<ClassSection> list = classSectionDAO.searchClassSection(kw);
        data.setAll(list);
    }
}
