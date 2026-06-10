package com.universe.view;

import com.universe.dao.ScheduleDAO;
import com.universe.entity.ClassSection;
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

public class AddScheduleFrm extends Stage {

    private final ClassSection classSection;
    private final ScheduleDAO scheduleDAO = new ScheduleDAO();
    private final Runnable onSaved;

    private final TextField inId = new TextField();
    private final ComboBox<String> inDay = new ComboBox<>(FXCollections.observableArrayList(
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"));
    private final TextField inStartPeriod = new TextField();
    private final TextField inEndPeriod = new TextField();
    private final TextField inRoom = new TextField();
    private final DatePicker inAppliedFrom = new DatePicker();
    private final DatePicker inAppliedTo = new DatePicker();

    public AddScheduleFrm(ClassSection classSection, Runnable onSaved) {
        this.classSection = classSection;
        this.onSaved = onSaved;
        setTitle("Thêm lịch học mới");
        initModality(Modality.APPLICATION_MODAL);
        buildUI();
    }

    private void buildUI() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(32));
        root.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Thêm lịch học: " + classSection.getName());
        title.getStyleClass().add("section-title");
        title.setWrapText(true);

        VBox form = new VBox(16);
        
        inDay.setValue("Monday");

        form.getChildren().addAll(
                createFormRow("ID Lịch học", inId),
                createFormRow("Thứ trong tuần", inDay),
                createFormRow("Tiết bắt đầu", inStartPeriod),
                createFormRow("Tiết kết thúc", inEndPeriod),
                createFormRow("Phòng học", inRoom),
                createFormRow("Áp dụng từ ngày", inAppliedFrom),
                createFormRow("Áp dụng đến ngày", inAppliedTo)
        );

        Button btnSave = new Button("Thêm mới");
        btnSave.getStyleClass().add("btn-primary");
        btnSave.setOnAction(e -> save());

        Button btnCancel = new Button("Hủy");
        btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(e -> close());

        HBox actions = new HBox(12, btnSave, btnCancel);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(16, 0, 0, 0));

        root.getChildren().addAll(title, form, actions);

        Scene scene = FxHelper.createFormScene(root, 700);
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
        Schedule s = new Schedule();
        s.setId(inId.getText().trim());
        s.setDayOfWeek(inDay.getValue());
        s.setRoom(inRoom.getText().trim());
        s.setAppliedFrom(inAppliedFrom.getValue());
        s.setAppliedTo(inAppliedTo.getValue());
        s.setClassSectionId(classSection.getId());

        try {
            s.setStartPeriod(Integer.parseInt(inStartPeriod.getText().trim()));
            s.setEndPeriod(Integer.parseInt(inEndPeriod.getText().trim()));
        } catch (NumberFormatException e) {
            FxHelper.showWarning("Tiết học phải là số nguyên!");
            return;
        }

        if (scheduleDAO.insertSchedule(s)) {
            FxHelper.showInfo("Thêm lịch học thành công");
            if (onSaved != null) onSaved.run();
            close();
        } else {
            FxHelper.showError("Thêm lịch học thất bại");
        }
    }
}
