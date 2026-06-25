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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Popup sửa hồ sơ người dùng. Vai trò được giữ cố định để bảo toàn các quan hệ
 * RBAC và dữ liệu học tập đã liên kết với tài khoản.
 */
public class EditUserFrm extends Stage {

    private final User user;
    private final UserDAO userDAO = new UserDAO();
    private final Runnable onSaved;

    private final TextField inFullName = new TextField();
    private final DatePicker inDob = new DatePicker();
    private final ComboBox<String> inGender = new ComboBox<>(
            FXCollections.observableArrayList("Male", "Female", "Other"));
    private final TextField inPhone = new TextField();
    private final TextField inEmail = new TextField();
    private final TextField inRole = new TextField();
    private final ComboBox<String> inStatus = new ComboBox<>(
            FXCollections.observableArrayList("active", "inactive"));

    private final TextField inDepartment = new TextField();
    private final TextField inDegree = new TextField();
    private final TextField inCourse = new TextField();
    private final TextField inMajor = new TextField();
    private final TextField inClassName = new TextField();

    public EditUserFrm(User user, Runnable onSaved) {
        this.user = user;
        this.onSaved = onSaved;
        setTitle("Chỉnh sửa người dùng");
        initModality(Modality.APPLICATION_MODAL);
        buildUI();
    }

    private void buildUI() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Sửa thông tin người dùng");
        title.getStyleClass().add("section-title");

        Label account = new Label(user.getId() + " - " + user.getUsername());
        account.getStyleClass().add("body-text");

        VBox form = new VBox(12);
        inFullName.setText(user.getFullName());
        inDob.setValue(user.getDob());
        inGender.setValue(user.getGender());
        inPhone.setText(user.getPhone());
        inEmail.setText(user.getEmail());
        inRole.setText(user.getRole());
        inRole.setDisable(true);
        inStatus.setValue(user.getStatus() == null ? "active" : user.getStatus().toLowerCase());

        form.getChildren().addAll(
                createFormRow("Họ và tên", inFullName),
                createFormRow("Ngày sinh", inDob),
                createFormRow("Giới tính", inGender),
                createFormRow("Số điện thoại", inPhone),
                createFormRow("Email", inEmail),
                createFormRow("Vai trò", inRole),
                createFormRow("Trạng thái", inStatus)
        );
        addRoleFields(form);

        Button btnSave = new Button("Lưu thay đổi");
        btnSave.getStyleClass().add("btn-primary");
        btnSave.setOnAction(e -> save());

        Button btnCancel = new Button("Hủy");
        btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(e -> close());

        HBox actions = new HBox(12, btnSave, btnCancel);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(16, 0, 0, 0));

        root.getChildren().addAll(title, account, form, actions);

        Scene scene = FxHelper.createFormScene(root, 720);
        setScene(scene);
        setMinWidth(560);
    }

    private void addRoleFields(VBox form) {
        if (user instanceof Admin admin) {
            inDepartment.setText(admin.getDepartment());
            form.getChildren().add(createFormRow("Phòng ban", inDepartment));
        } else if (user instanceof Lecturer lecturer) {
            inDepartment.setText(lecturer.getDepartment());
            inDegree.setText(lecturer.getDegree());
            form.getChildren().addAll(
                    createFormRow("Khoa / Bộ môn", inDepartment),
                    createFormRow("Học vị", inDegree));
        } else if (user instanceof Student student) {
            inCourse.setText(student.getCourse());
            inMajor.setText(student.getMajor());
            inClassName.setText(student.getClassName());
            form.getChildren().addAll(
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
        if (inFullName.getText().isBlank()) {
            FxHelper.showWarning("Họ và tên không được để trống.");
            return;
        }

        String phone = inPhone.getText().trim();
        if (!phone.isEmpty() && (phone.length() > 15 || !phone.matches("[0-9+\\-\\s()]{1,15}"))) {
            FxHelper.showWarning("Số điện thoại không hợp lệ (tối đa 15 ký tự, chỉ chứa số và ký tự +,-,()");
            return;
        }
        String email = inEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[\\w.%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            FxHelper.showWarning("Địa chỉ email không hợp lệ.");
            return;
        }

        user.setFullName(inFullName.getText().trim());
        user.setDob(inDob.getValue());
        if (inGender.getValue() != null) {
            user.setGender(inGender.getValue());
        }
        if (inStatus.getValue() != null) {
            user.setStatus(inStatus.getValue());
        }
        user.setPhone(inPhone.getText().trim());
        user.setEmail(inEmail.getText().trim());

        if (user instanceof Admin admin) {
            admin.setDepartment(inDepartment.getText().trim());
        } else if (user instanceof Lecturer lecturer) {
            lecturer.setDepartment(inDepartment.getText().trim());
            lecturer.setDegree(inDegree.getText().trim());
        } else if (user instanceof Student student) {
            student.setCourse(inCourse.getText().trim());
            student.setMajor(inMajor.getText().trim());
            student.setClassName(inClassName.getText().trim());
        }

        try {
            if (userDAO.updateUser(user)) {
                FxHelper.showInfo("Cập nhật người dùng thành công.");
                if (onSaved != null) {
                    onSaved.run();
                }
                close();
            } else {
                FxHelper.showError("Không tìm thấy người dùng cần cập nhật.");
            }
        } catch (RuntimeException ex) {
            FxHelper.showError("Cập nhật thất bại. Vui lòng kiểm tra lại thông tin đã nhập.");
        }
    }
}
