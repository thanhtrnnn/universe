package com.universe.view;

import com.universe.dao.ClassSectionDAO;
import com.universe.dao.CourseDAO;
import com.universe.dao.UserDAO;
import com.universe.entity.ClassSection;
import com.universe.entity.Course;
import com.universe.entity.User;

import javafx.collections.FXCollections;
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

public class CreateClassSectionFrm extends Stage {

    private final ClassSectionDAO classSectionDAO = new ClassSectionDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final UserDAO userDAO = new UserDAO();
    private final Runnable onSaved;

    private final TextField inId = new TextField();
    private final TextField inClassId = new TextField();
    private final TextField inName = new TextField();
    private final TextField inSemester = new TextField();
    private final TextField inYear = new TextField();
    private final TextField inMaxStudents = new TextField();
    private final ComboBox<Course> inCourse = new ComboBox<>();
    private final ComboBox<User> inLecturer = new ComboBox<>();
    private final ComboBox<String> inStatus = new ComboBox<>(FXCollections.observableArrayList("open", "full", "closed"));

    public CreateClassSectionFrm(Runnable onSaved) {
        this.onSaved = onSaved;
        setTitle("Mở lớp học phần mới");
        initModality(Modality.APPLICATION_MODAL);
        buildUI();
    }

    private void buildUI() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Mở lớp học phần mới");
        title.getStyleClass().add("section-title");

        VBox form = new VBox(12);
        
        inStatus.setValue("open");
        inCourse.setItems(FXCollections.observableArrayList(courseDAO.getListCourse()));
        inLecturer.setItems(FXCollections.observableArrayList(userDAO.findUsersByRole("Lecturer")));

        form.getChildren().addAll(
                createFormRow("ID Lớp", inId),
                createFormRow("Mã lớp hiển thị", inClassId),
                createFormRow("Tên lớp học phần", inName),
                createFormRow("Học kỳ", inSemester),
                createFormRow("Năm học", inYear),
                createFormRow("Sĩ số tối đa", inMaxStudents),
                createFormRow("Học phần", inCourse),
                createFormRow("Giảng viên phụ trách", inLecturer),
                createFormRow("Trạng thái", inStatus)
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

        Scene scene = FxHelper.createFormScene(root, 760);
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
        ClassSection cs = new ClassSection();
        cs.setId(inId.getText().trim());
        cs.setClassId(inClassId.getText().trim());
        cs.setName(inName.getText().trim());
        cs.setSemester(inSemester.getText().trim());
        cs.setYear(inYear.getText().trim());
        try {
            cs.setMaxStudents(Integer.parseInt(inMaxStudents.getText().trim()));
        } catch (NumberFormatException e) {
            FxHelper.showWarning("Sĩ số tối đa phải là số nguyên!");
            return;
        }
        Course selectedCourse = inCourse.getValue();
        User selectedLecturer = inLecturer.getValue();
        if (selectedCourse == null || selectedLecturer == null) {
            FxHelper.showWarning("Vui lòng chọn học phần và giảng viên phụ trách.");
            return;
        }
        cs.setCourseId(selectedCourse.getId());
        cs.setLecturerId(selectedLecturer.getId());
        cs.setStatus(inStatus.getValue());

        if (classSectionDAO.insertClassSection(cs)) {
            FxHelper.showInfo("Mở lớp thành công");
            if (onSaved != null) onSaved.run();
            close();
        } else {
            FxHelper.showError("Mở lớp thất bại");
        }
    }
}
