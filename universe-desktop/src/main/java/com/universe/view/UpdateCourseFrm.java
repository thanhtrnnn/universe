package com.universe.view;

import com.universe.dao.CourseDAO;
import com.universe.entity.Course;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UpdateCourseFrm extends Stage {

    private final Course course;
    private final CourseDAO courseDAO = new CourseDAO();
    private final Runnable onSaved;

    private final TextField inId = new TextField();
    private final TextField inName = new TextField();
    private final TextField inCredits = new TextField();
    private final TextField inDepartment = new TextField();

    public UpdateCourseFrm(Course course, Runnable onSaved) {
        this.course = course;
        this.onSaved = onSaved;
        setTitle("Sửa học phần");
        initModality(Modality.APPLICATION_MODAL);
        buildUI();
    }

    private void buildUI() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(32));
        root.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Sửa thông tin học phần");
        title.getStyleClass().add("section-title");

        VBox form = new VBox(16);
        
        inId.setText(course.getId());
        inId.setDisable(true); // Không cho sửa ID
        inName.setText(course.getName());
        inCredits.setText(String.valueOf(course.getCredits()));
        inDepartment.setText(course.getDepartment());

        form.getChildren().addAll(
                createFormRow("Mã HP (Không thể sửa)", inId),
                createFormRow("Tên học phần", inName),
                createFormRow("Số tín chỉ", inCredits),
                createFormRow("Khoa quản lý", inDepartment)
        );

        Button btnSave = new Button("Lưu thay đổi");
        btnSave.getStyleClass().add("btn-primary");
        btnSave.setOnAction(e -> save());

        Button btnCancel = new Button("Hủy");
        btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(e -> close());

        HBox actions = new HBox(12, btnSave, btnCancel);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(16, 0, 0, 0));

        root.getChildren().addAll(title, form, actions);

        Scene scene = FxHelper.createFormScene(root, 520);
        setScene(scene);
        setMinWidth(560);
    }

    private VBox createFormRow(String labelText, javafx.scene.Node input) {
        VBox row = new VBox(4);
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("caption-text");
        
        if (input instanceof TextField || input instanceof ComboBox) {
            ((javafx.scene.layout.Region) input).setMaxWidth(Double.MAX_VALUE);
        }
        
        row.getChildren().addAll(lbl, input);
        return row;
    }

    private void save() {
        course.setName(inName.getText().trim());
        course.setDepartment(inDepartment.getText().trim());
        try {
            course.setCredits(Integer.parseInt(inCredits.getText().trim()));
        } catch (NumberFormatException e) {
            FxHelper.showWarning("Số tín chỉ phải là số nguyên!");
            return;
        }

        if (courseDAO.updateCourse(course)) {
            FxHelper.showInfo("Sửa học phần thành công");
            if (onSaved != null) onSaved.run();
            close();
        } else {
            FxHelper.showError("Sửa học phần thất bại");
        }
    }
}
