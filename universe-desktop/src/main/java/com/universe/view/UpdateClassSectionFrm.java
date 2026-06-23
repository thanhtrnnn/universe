package com.universe.view;

import com.universe.dao.ClassSectionDAO;
import com.universe.dao.CourseDAO;
import com.universe.dao.UserDAO;
import com.universe.entity.ClassSection;
import com.universe.entity.Course;
import com.universe.entity.User;

import javafx.collections.FXCollections;
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
 * Giao diện sửa Lớp Học phần (UpdateClassSectionFrm).
 */
public class UpdateClassSectionFrm extends Stage implements ActionListener {

    private final ClassSection classSection;
    private final ClassSectionDAO classSectionDAO = new ClassSectionDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final UserDAO userDAO = new UserDAO();
    private final Runnable onSaved;

    private final TextField inClassId = new TextField();
    private final TextField inName = new TextField();
    private final TextField inSemester = new TextField();
    private final TextField inYear = new TextField();
    private final TextField inMaxStudents = new TextField();
    private final ComboBox<String> inStatus = new ComboBox<>(FXCollections.observableArrayList("open", "full", "closed"));
    private final ComboBox<Course> inCourse = new ComboBox<>();
    private final ComboBox<User> inLecturer = new ComboBox<>();

    private final Button btnSave = new Button("Lưu thay đổi");
    private final Button btnCancel = new Button("Hủy");

    public UpdateClassSectionFrm(ClassSection classSection, Runnable onSaved) {
        this.classSection = classSection;
        this.onSaved = onSaved;
        setTitle("Chỉnh sửa Lớp học phần");
        initModality(Modality.APPLICATION_MODAL);
        buildUI();
    }

    private void buildUI() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(32));
        root.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Sửa thông tin lớp học phần");
        title.getStyleClass().add("section-title");

        VBox form = new VBox(16);
        
        inClassId.setText(classSection.getClassId());
        inName.setText(classSection.getName());
        inSemester.setText(classSection.getSemester());
        inYear.setText(classSection.getYear());
        inMaxStudents.setText(String.valueOf(classSection.getMaxStudents()));
        inStatus.setValue(classSection.getStatus());
        inCourse.setItems(FXCollections.observableArrayList(courseDAO.getListCourse()));
        inLecturer.setItems(FXCollections.observableArrayList(userDAO.findUsersByRole("Lecturer")));
        inCourse.getItems().stream()
                .filter(course -> course.getId().equals(classSection.getCourseId()))
                .findFirst().ifPresent(inCourse::setValue);
        inLecturer.getItems().stream()
                .filter(user -> user.getId().equals(classSection.getLecturerId()))
                .findFirst().ifPresent(inLecturer::setValue);

        HBox row1 = new HBox(16, createFormRow("Mã lớp", inClassId), createFormRow("Tên lớp", inName));
        HBox row2 = new HBox(16, createFormRow("Học kỳ", inSemester), createFormRow("Năm học", inYear));
        HBox row3 = new HBox(16, createFormRow("Sĩ số tối đa", inMaxStudents), createFormRow("Trạng thái", inStatus));
        HBox row4 = new HBox(16, createFormRow("Học phần", inCourse), createFormRow("Giảng viên", inLecturer));

        form.getChildren().addAll(row1, row2, row3, row4);

        btnSave.getStyleClass().add("btn-primary");
        btnSave.setOnAction(this);

        btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(this);

        HBox actions = new HBox(12, btnSave, btnCancel);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(16, 0, 0, 0));

        root.getChildren().addAll(title, form, actions);

        Scene scene = FxHelper.createFormScene(root, 650);
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
        try {
            classSection.setClassId(inClassId.getText().trim());
            classSection.setName(inName.getText().trim());
            classSection.setSemester(inSemester.getText().trim());
            classSection.setYear(inYear.getText().trim());
            classSection.setMaxStudents(Integer.parseInt(inMaxStudents.getText().trim()));
            classSection.setStatus(inStatus.getValue());
            if (inCourse.getValue() == null || inLecturer.getValue() == null) {
                FxHelper.showWarning("Vui lòng chọn học phần và giảng viên.");
                return;
            }
            classSection.setCourseId(inCourse.getValue().getId());
            classSection.setLecturerId(inLecturer.getValue().getId());

            if (classSectionDAO.updateClassSection(classSection)) {
                FxHelper.showInfo("Cập nhật thành công");
                if (onSaved != null) onSaved.run();
                close();
            } else {
                FxHelper.showError("Cập nhật thất bại");
            }
        } catch (NumberFormatException ex) {
            FxHelper.showWarning("Sĩ số tối đa phải là số nguyên");
        }
    }
}
