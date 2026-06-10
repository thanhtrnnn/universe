package com.universe.view;

import com.universe.dao.UserDAO;
import com.universe.entity.Admin;
import com.universe.entity.Lecturer;
import com.universe.entity.Student;
import com.universe.entity.User;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddUserFrm extends Stage {

    private final UserDAO userDAO = new UserDAO();
    private final Runnable onSaved;

    private final TextField inId = new TextField();
    private final TextField inFullName = new TextField();
    private final DatePicker inDob = new DatePicker();
    private final ComboBox<String> inGender = new ComboBox<>(
            FXCollections.observableArrayList("Male", "Female", "Other"));
    private final TextField inUsername = new TextField();
    private final PasswordField inPassword = new PasswordField();
    private final TextField inPhone = new TextField();
    private final TextField inEmail = new TextField();
    private final ComboBox<String> inRole = new ComboBox<>(
            FXCollections.observableArrayList("Student", "Lecturer", "Admin"));
    private final ComboBox<String> inStatus = new ComboBox<>(
            FXCollections.observableArrayList("active", "inactive"));

    private final VBox roleFields = new VBox(12);
    private final TextField inDepartment = new TextField();
    private final TextField inDegree = new TextField();
    private final TextField inCourse = new TextField();
    private final TextField inMajor = new TextField();
    private final TextField inClassName = new TextField();

    public AddUserFrm(Runnable onSaved) {
        this.onSaved = onSaved;
        setTitle("Thêm người dùng mới");
        initModality(Modality.APPLICATION_MODAL);
        buildUI();
    }

    private void buildUI() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Thêm người dùng mới");
        title.getStyleClass().add("section-title");

        Label guide = new Label("Nhập thông tin tài khoản và thông tin hồ sơ theo vai trò.");
        guide.getStyleClass().add("body-text");
        guide.setWrapText(true);

        VBox form = new VBox(12);

        inRole.setValue("Student");
        inStatus.setValue("active");
        inGender.setValue("Male");
        inRole.valueProperty().addListener((obs, oldRole, newRole) -> updateRoleFields());

        form.getChildren().addAll(
                createFormRow("Mã người dùng", inId),
                createFormRow("Họ và tên", inFullName),
                createFormRow("Ngày sinh", inDob),
                createFormRow("Giới tính", inGender),
                createFormRow("Tên đăng nhập", inUsername),
                createFormRow("Mật khẩu", inPassword),
                createFormRow("Số điện thoại", inPhone),
                createFormRow("Email", inEmail),
                createFormRow("Vai trò", inRole),
                createFormRow("Trạng thái", inStatus),
                roleFields
        );
        updateRoleFields();

        Button btnSave = new Button("Thêm người dùng");
        btnSave.getStyleClass().add("btn-primary");
        btnSave.setOnAction(e -> save());

        Button btnCancel = new Button("Hủy");
        btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(e -> close());

        HBox actions = new HBox(12, btnSave, btnCancel);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(16, 0, 0, 0));

        root.getChildren().addAll(title, guide, form, actions);

        Scene scene = FxHelper.createFormScene(root, 760);
        setScene(scene);
        setMinWidth(560);
    }

    private void updateRoleFields() {
        roleFields.getChildren().clear();
        switch (inRole.getValue()) {
            case "Admin" ->
                    roleFields.getChildren().add(createFormRow("Phòng ban", inDepartment));
            case "Lecturer" -> roleFields.getChildren().addAll(
                    createFormRow("Khoa / Bộ môn", inDepartment),
                    createFormRow("Học vị", inDegree));
            default -> roleFields.getChildren().addAll(
                    createFormRow("Khóa học", inCourse),
                    createFormRow("Ngành đào tạo", inMajor),
                    createFormRow("Lớp hành chính", inClassName));
        }
    }

    private VBox createFormRow(String labelText, Region input) {
        VBox row = new VBox(6);
        Label label = new Label(labelText);
        label.getStyleClass().add("caption-text");
        input.setMaxWidth(Double.MAX_VALUE);
        row.getChildren().addAll(label, input);
        return row;
    }

    private void save() {
        if (inId.getText().isBlank() || inFullName.getText().isBlank()
                || inUsername.getText().isBlank() || inPassword.getText().isBlank()) {
            FxHelper.showWarning("Vui lòng nhập mã, họ tên, tên đăng nhập và mật khẩu.");
            return;
        }

        User user = createUserByRole();
        user.setId(inId.getText().trim());
        user.setFullName(inFullName.getText().trim());
        user.setDob(inDob.getValue());
        user.setGender(inGender.getValue());
        user.setUsername(inUsername.getText().trim());
        user.setPassword(inPassword.getText());
        user.setPhone(inPhone.getText().trim());
        user.setEmail(inEmail.getText().trim());
        user.setRole(inRole.getValue());
        user.setStatus(inStatus.getValue());

        try {
            if (userDAO.insertUser(user)) {
                FxHelper.showInfo("Thêm người dùng thành công.");
                if (onSaved != null) {
                    onSaved.run();
                }
                close();
            }
        } catch (RuntimeException ex) {
            FxHelper.showError("Không thể thêm người dùng. ID hoặc tên đăng nhập có thể đã tồn tại.");
        }
    }

    private User createUserByRole() {
        if ("Admin".equals(inRole.getValue())) {
            Admin admin = new Admin();
            admin.setDepartment(inDepartment.getText().trim());
            return admin;
        }
        if ("Lecturer".equals(inRole.getValue())) {
            Lecturer lecturer = new Lecturer();
            lecturer.setDepartment(inDepartment.getText().trim());
            lecturer.setDegree(inDegree.getText().trim());
            return lecturer;
        }
        Student student = new Student();
        student.setCourse(inCourse.getText().trim());
        student.setMajor(inMajor.getText().trim());
        student.setClassName(inClassName.getText().trim());
        return student;
    }
}
