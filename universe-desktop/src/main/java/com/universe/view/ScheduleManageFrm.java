package com.universe.view;

import com.universe.dao.ClassSectionDAO;
import com.universe.dao.ScheduleDAO;
import com.universe.entity.ClassSection;
import com.universe.entity.Schedule;
import com.universe.entity.User;

import javafx.beans.property.SimpleStringProperty;
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
 * Giao diện quản lý lịch học của Admin (ScheduleManageFrm).
 */
public class ScheduleManageFrm extends VBox {

    private final User currentUser;
    private final ClassSectionDAO classSectionDAO = new ClassSectionDAO();
    private final ScheduleDAO scheduleDAO = new ScheduleDAO();
    
    private final TextField inSearch = new TextField();
    private final TableView<ClassSection> tblClass = new TableView<>();
    private final TableView<Schedule> tblSchedule = new TableView<>();
    
    private final ObservableList<ClassSection> classData = FXCollections.observableArrayList();
    private final ObservableList<Schedule> scheduleData = FXCollections.observableArrayList();

    public ScheduleManageFrm(User currentUser) {
        this.currentUser = currentUser;
        buildUI();
        searchClasses();
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        setSpacing(24);

        Label title = new Label("Quản lý Lịch học");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("SẮP XẾP LỊCH TRÌNH CHO CÁC LỚP HỌC PHẦN");
        subtitle.getStyleClass().add("section-kicker");
        VBox header = new VBox(8, subtitle, title);

        // ── Layout 2 cột: Trái chọn lớp, Phải quản lý lịch ──
        HBox contentBox = new HBox(24);
        VBox.setVgrow(contentBox, Priority.ALWAYS);

        // Cột Trái (Danh sách lớp)
        VBox leftPane = new VBox(16);
        leftPane.getStyleClass().add("card");
        leftPane.setPrefWidth(500);

        Label lblLeft = new Label("1. Chọn Lớp học phần");
        lblLeft.getStyleClass().add("section-title");

        HBox searchBox = new HBox(8);
        inSearch.setPromptText("Mã / Tên lớp...");
        inSearch.setPrefWidth(250);
        inSearch.setOnAction(e -> searchClasses());
        Button btnSearch = new Button("Tìm");
        btnSearch.getStyleClass().add("btn-secondary");
        btnSearch.setOnAction(e -> searchClasses());
        searchBox.getChildren().addAll(inSearch, btnSearch);

        TableColumn<ClassSection, String> colClassId = new TableColumn<>("Mã Lớp");
        colClassId.setCellValueFactory(new PropertyValueFactory<>("classId"));
        colClassId.setPrefWidth(120);

        TableColumn<ClassSection, String> colName = new TableColumn<>("Tên Lớp HP");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(200);

        TableColumn<ClassSection, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        tblClass.getColumns().addAll(colClassId, colName, colStatus);
        tblClass.setItems(classData);
        tblClass.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        tblClass.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) loadSchedules(newSel.getId());
        });
        VBox.setVgrow(tblClass, Priority.ALWAYS);
        leftPane.getChildren().addAll(lblLeft, searchBox, tblClass);

        // Cột Phải (Chi tiết lịch)
        VBox rightPane = new VBox(16);
        rightPane.getStyleClass().add("card");
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        Label lblRight = new Label("2. Lịch học của lớp");
        lblRight.getStyleClass().add("section-title");

        HBox rightControls = new HBox(8);
        Button btnAdd = new Button("Thêm lịch học");
        btnAdd.getStyleClass().add("btn-primary");
        btnAdd.setOnAction(e -> {
            ClassSection selectedClass = tblClass.getSelectionModel().getSelectedItem();
            if (selectedClass == null) {
                FxHelper.showWarning("Vui lòng chọn một lớp học phần ở cột bên trái để thêm lịch!");
                return;
            }
            new AddScheduleFrm(selectedClass, () -> loadSchedules(selectedClass.getId())).show();
        });

        Button btnEdit = new Button("Sửa");
        btnEdit.getStyleClass().add("btn-secondary");
        btnEdit.setOnAction(e -> editSchedule());
        
        Button btnDelete = new Button("Xóa");
        btnDelete.getStyleClass().add("btn-accent");
        btnDelete.setOnAction(e -> {
            Schedule selected = tblSchedule.getSelectionModel().getSelectedItem();
            if (selected == null) {
                FxHelper.showWarning("Vui lòng chọn một lịch học để xóa");
                return;
            }
            try {
                if (scheduleDAO.deleteSchedule(selected.getId())) {
                    FxHelper.showInfo("Đã xóa lịch học thành công");
                    loadSchedules(selected.getClassSectionId());
                } else {
                    FxHelper.showError("Không thể xóa lịch học");
                }
            } catch (Exception ex) {
                FxHelper.showError("Lỗi khi xóa lịch học: " + ex.getMessage());
            }
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        rightControls.getChildren().addAll(spacer, btnAdd, btnEdit, btnDelete);

        TableColumn<Schedule, String> colDay = new TableColumn<>("Thứ");
        colDay.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));

        TableColumn<Schedule, String> colPeriods = new TableColumn<>("Tiết");
        colPeriods.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStartPeriod() + " - " + c.getValue().getEndPeriod()));

        TableColumn<Schedule, String> colRoom = new TableColumn<>("Phòng");
        colRoom.setCellValueFactory(new PropertyValueFactory<>("room"));

        TableColumn<Schedule, String> colFrom = new TableColumn<>("Từ ngày");
        colFrom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAppliedFrom() != null ? c.getValue().getAppliedFrom().toString() : ""));

        TableColumn<Schedule, String> colTo = new TableColumn<>("Đến ngày");
        colTo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAppliedTo() != null ? c.getValue().getAppliedTo().toString() : ""));

        tblSchedule.getColumns().addAll(colDay, colPeriods, colRoom, colFrom, colTo);
        tblSchedule.setItems(scheduleData);
        tblSchedule.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblSchedule.setPlaceholder(new Label("Chọn lớp để xem lịch"));

        tblSchedule.setRowFactory(tv -> {
            TableRow<Schedule> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    editSchedule();
                }
            });
            return row;
        });

        VBox.setVgrow(tblSchedule, Priority.ALWAYS);
        rightPane.getChildren().addAll(lblRight, rightControls, tblSchedule);

        contentBox.getChildren().addAll(leftPane, rightPane);
        getChildren().addAll(header, contentBox);
    }

    private void searchClasses() {
        classData.setAll(classSectionDAO.searchClassSection(inSearch.getText().trim()));
        scheduleData.clear();
    }

    private void loadSchedules(String classSectionId) {
        scheduleData.setAll(scheduleDAO.getSchedulesByClassSection(classSectionId));
    }

    private void editSchedule() {
        Schedule selected = tblSchedule.getSelectionModel().getSelectedItem();
        if (selected == null) {
            FxHelper.showWarning("Vui lòng chọn một lịch học để sửa");
            return;
        }
        new EditScheduleFrm(selected, () -> loadSchedules(selected.getClassSectionId())).show();
    }
}
