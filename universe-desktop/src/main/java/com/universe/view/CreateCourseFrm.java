package com.universe.view;

import com.universe.dao.CourseDAO;
import com.universe.entity.Course;

import javafx.event.ActionEvent;
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

/**
 * Giao diện thêm mới học phần (CreateCourseFrm).
 */
public class CreateCourseFrm extends Stage implements ActionListener {

    private final CourseDAO courseDAO = new CourseDAO();
    private final Runnable onCreated;

    private final TextField inId = new TextField();
    private final TextField inName = new TextField();
    private final TextField inCredits = new TextField();
    private final TextField inDepartment = new TextField();

    private final Button btnSave = new Button("Lưu học phần");
    private final Button btnCancel = new Button("Hủy");

    public CreateCourseFrm(Runnable onCreated) {
        this.onCreated = onCreated;
        setTitle("Thêm học phần");
        initModality(Modality.APPLICATION_MODAL);
        buildUI();
    }

    private void buildUI() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(32));
        root.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Thêm học phần mới");
        title.getStyleClass().add("section-title");

        VBox form = new VBox(16);
        
        inId.setPromptText("Ví dụ: INT1340");
        inName.setPromptText("Tên môn học...");
        inCredits.setPromptText("Số tín chỉ (1-5)...");
        inDepartment.setPromptText("Khoa...");

        form.getChildren().addAll(
                createFormRow("Mã học phần", inId),
                createFormRow("Tên học phần", inName),
                createFormRow("Số tín chỉ", inCredits),
                createFormRow("Khoa quản lý", inDepartment)
        );

        btnSave.getStyleClass().add("btn-primary");
        btnSave.setOnAction(this);

        btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(this);

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

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnSave) {
            save();
        } else if (src == btnCancel) {
            close();
        }
    }

    private void save() {
        String id = inId.getText().trim();
        String name = inName.getText().trim();
        String creditsStr = inCredits.getText().trim();
        String dept = inDepartment.getText().trim();

        if (id.isEmpty() || name.isEmpty() || creditsStr.isEmpty()) {
            FxHelper.showWarning("Vui lòng nhập đủ thông tin mã, tên và số TC.");
            return;
        }

        try {
            int cred = Integer.parseInt(creditsStr);
            if (cred <= 0 || cred > 10) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            FxHelper.showWarning("Số tín chỉ phải là số nguyên hợp lệ.");
            return;
        }

        if (courseDAO.existsById(id)) {
            FxHelper.showError("Mã học phần đã tồn tại.");
            return;
        }

        Course c = new Course(id, Integer.parseInt(creditsStr), name, dept);
        if (courseDAO.createCourse(c)) {
            FxHelper.showInfo("Thêm học phần thành công");
            if (onCreated != null) onCreated.run();
            close();
        } else {
            FxHelper.showError("Thêm học phần thất bại.");
        }
    }
}
