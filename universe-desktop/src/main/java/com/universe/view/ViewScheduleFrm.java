package com.universe.view;

import com.universe.dao.ScheduleDAO;
import com.universe.entity.Schedule;
import com.universe.entity.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Giao diện xem lịch học / lịch giảng dạy (View Schedule).
 * Sinh viên: xem TKB các lớp đã đăng ký.
 * Giảng viên: xem lịch giảng dạy.
 */
public class ViewScheduleFrm extends VBox {

    private final User currentUser;
    private final ScheduleDAO scheduleDAO = new ScheduleDAO();
    private final TableView<Schedule> tblSchedule = new TableView<>();

    public ViewScheduleFrm(User currentUser) {
        this.currentUser = currentUser;
        buildUI();
        loadSchedule();
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        setSpacing(16);
        setPadding(new Insets(4));

        boolean isLecturer = "Lecturer".equalsIgnoreCase(currentUser.getRole());
        String titleText = isLecturer ? "Lịch giảng dạy" : "Thời khóa biểu";

        Label title = new Label(titleText);
        title.getStyleClass().add("content-title");

        Label sub = new Label(isLecturer
                ? "Lịch dạy của giảng viên " + currentUser.getFullName()
                : "Lịch học của sinh viên " + currentUser.getFullName());
        sub.getStyleClass().add("label-subtitle");

        // Table columns
        TableColumn<Schedule, String> colClassSection = new TableColumn<>("Mã lớp HP");
        colClassSection.setCellValueFactory(new PropertyValueFactory<>("classSectionId"));
        colClassSection.setPrefWidth(140);

        TableColumn<Schedule, String> colDay = new TableColumn<>("Thứ");
        colDay.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
        colDay.setPrefWidth(80);

        TableColumn<Schedule, Integer> colStart = new TableColumn<>("Tiết BĐ");
        colStart.setCellValueFactory(new PropertyValueFactory<>("startPeriod"));
        colStart.setPrefWidth(80);

        TableColumn<Schedule, Integer> colEnd = new TableColumn<>("Tiết KT");
        colEnd.setCellValueFactory(new PropertyValueFactory<>("endPeriod"));
        colEnd.setPrefWidth(80);

        TableColumn<Schedule, String> colRoom = new TableColumn<>("Phòng");
        colRoom.setCellValueFactory(new PropertyValueFactory<>("room"));
        colRoom.setPrefWidth(100);

        TableColumn<Schedule, String> colFrom = new TableColumn<>("Từ ngày");
        colFrom.setCellValueFactory(new PropertyValueFactory<>("appliedFrom"));
        colFrom.setPrefWidth(110);

        TableColumn<Schedule, String> colTo = new TableColumn<>("Đến ngày");
        colTo.setCellValueFactory(new PropertyValueFactory<>("appliedTo"));
        colTo.setPrefWidth(110);

        tblSchedule.getColumns().addAll(colClassSection, colDay, colStart, colEnd, colRoom, colFrom, colTo);
        tblSchedule.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblSchedule.setPlaceholder(new Label("Không có lịch học nào"));

        VBox.setVgrow(tblSchedule, javafx.scene.layout.Priority.ALWAYS);
        getChildren().addAll(title, sub, tblSchedule);
    }

    private void loadSchedule() {
        List<Schedule> list;
        if ("Lecturer".equalsIgnoreCase(currentUser.getRole())) {
            list = scheduleDAO.getLecturerSchedule(currentUser.getId());
        } else {
            list = scheduleDAO.getStudentSchedule(currentUser.getId());
        }
        ObservableList<Schedule> data = FXCollections.observableArrayList(list);
        tblSchedule.setItems(data);
    }
}
