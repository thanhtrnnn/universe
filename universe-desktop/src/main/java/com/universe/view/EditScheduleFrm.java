package com.universe.view;

import com.universe.dao.ScheduleDAO;
import com.universe.entity.Schedule;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Giao diện sửa Lịch học (EditScheduleFrm).
 */
public class EditScheduleFrm extends Stage {

    private final Schedule schedule;
    private final ScheduleDAO scheduleDAO = new ScheduleDAO();
    private final Runnable onSaved;

    private final ComboBox<String> inDayOfWeek = new ComboBox<>(FXCollections.observableArrayList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
    private final TextField inStart = new TextField();
    private final TextField inEnd = new TextField();
    private final TextField inRoom = new TextField();
    private final DatePicker inAppliedFrom = new DatePicker();
    private final DatePicker inAppliedTo = new DatePicker();

    public EditScheduleFrm(Schedule schedule, Runnable onSaved) {
        this.schedule = schedule;
        this.onSaved = onSaved;
        setTitle("Sửa Lịch học");
        initModality(Modality.APPLICATION_MODAL);
        buildUI();
    }

    private void buildUI() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(32));
        root.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Sửa thông tin lịch học");
        title.getStyleClass().add("section-title");

        VBox form = new VBox(16);
        
        inDayOfWeek.setValue(schedule.getDayOfWeek());
        inStart.setText(String.valueOf(schedule.getStartPeriod()));
        inEnd.setText(String.valueOf(schedule.getEndPeriod()));
        inRoom.setText(schedule.getRoom());
        inAppliedFrom.setValue(schedule.getAppliedFrom());
        inAppliedTo.setValue(schedule.getAppliedTo());

        HBox row1 = new HBox(16, createFormRow("Ngày trong tuần", inDayOfWeek), createFormRow("Phòng học", inRoom));
        HBox row2 = new HBox(16, createFormRow("Tiết bắt đầu", inStart), createFormRow("Tiết kết thúc", inEnd));
        HBox row3 = new HBox(16, createFormRow("Áp dụng từ ngày", inAppliedFrom), createFormRow("Áp dụng đến ngày", inAppliedTo));

        form.getChildren().addAll(row1, row2, row3);

        Button btnSave = new Button("Lưu lịch học");
        btnSave.getStyleClass().add("btn-primary");
        btnSave.setOnAction(e -> save());

        Button btnCancel = new Button("Hủy");
        btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(e -> close());

        HBox actions = new HBox(12, btnSave, btnCancel);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(16, 0, 0, 0));

        root.getChildren().addAll(title, form, actions);

        Scene scene = FxHelper.createFormScene(root, 560);
        setScene(scene);
        setMinWidth(560);
    }

    private VBox createFormRow(String labelText, javafx.scene.Node input) {
        VBox row = new VBox(4);
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("caption-text");
        
        if (input instanceof TextField || input instanceof ComboBox || input instanceof DatePicker) {
            ((javafx.scene.layout.Region) input).setMaxWidth(Double.MAX_VALUE);
        }
        
        row.getChildren().addAll(lbl, input);
        return row;
    }

    private void save() {
        try {
            schedule.setDayOfWeek(inDayOfWeek.getValue());
            schedule.setStartPeriod(Integer.parseInt(inStart.getText().trim()));
            schedule.setEndPeriod(Integer.parseInt(inEnd.getText().trim()));
            schedule.setRoom(inRoom.getText().trim());
            schedule.setAppliedFrom(inAppliedFrom.getValue());
            schedule.setAppliedTo(inAppliedTo.getValue());

            if (scheduleDAO.updateSchedule(schedule)) {
                FxHelper.showInfo("Cập nhật lịch học thành công");
                if (onSaved != null) onSaved.run();
                close();
            } else {
                FxHelper.showError("Cập nhật thất bại");
            }
        } catch (Exception ex) {
            FxHelper.showWarning("Vui lòng kiểm tra lại định dạng dữ liệu");
        }
    }
}
