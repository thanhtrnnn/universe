package com.universe.view;

import com.universe.dao.ClassSectionDAO;
import com.universe.dao.NotificationDAO;
import com.universe.dao.UserDAO;
import com.universe.entity.ClassSection;
import com.universe.entity.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.text.Normalizer;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Form gửi thông báo với lựa chọn người nhận trực quan thay cho nhập mã tự do.
 */
public class SendNotificationFrm extends VBox {

    private static final String ALL_USERS = "Tất cả người dùng";
    private static final String ALL_STUDENTS = "Tất cả sinh viên";
    private static final String ALL_LECTURERS = "Tất cả giảng viên";
    private static final String BY_CLASS = "Sinh viên theo lớp học phần";
    private static final String ONE_USER = "Một người dùng cụ thể";

    private final User currentUser;
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final ClassSectionDAO classSectionDAO = new ClassSectionDAO();
    private final UserDAO userDAO = new UserDAO();

    private final TextField inTitle = new TextField();
    private final TextArea inContent = new TextArea();
    private final ComboBox<String> inAudience = new ComboBox<>();
    private final TextField inClassSearch = new TextField();
    private final ComboBox<ClassSection> inClassSection = new ComboBox<>();
    private final TextField inUserSearch = new TextField();
    private final ComboBox<User> inUser = new ComboBox<>();
    private final ObservableList<ClassSection> allClassSections = FXCollections.observableArrayList();
    private final ObservableList<User> allUsers = FXCollections.observableArrayList();
    private final FilteredList<ClassSection> filteredClassSections =
            new FilteredList<>(allClassSections, item -> true);
    private final FilteredList<User> filteredUsers =
            new FilteredList<>(allUsers, item -> true);
    private final VBox classTarget = new VBox(12);
    private final VBox userTarget = new VBox(12);
    private final Label recipientSummary = new Label();

    public SendNotificationFrm(User currentUser) {
        this.currentUser = currentUser;
        buildUI();
        loadTargets();
        updateTargetVisibility();
    }

    private void buildUI() {
        setSpacing(24);
        setMaxWidth(900);

        Label title = new Label("Gửi Thông báo");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("TẠO VÀ GỬI THÔNG BÁO ĐÚNG ĐỐI TƯỢNG");
        subtitle.getStyleClass().add("section-kicker");
        VBox header = new VBox(8, subtitle, title);

        VBox formCard = new VBox(18);
        formCard.getStyleClass().add("card");

        inTitle.setPromptText("Ví dụ: Thay đổi phòng học buổi sáng thứ Hai");
        inContent.setPromptText("Nhập nội dung đầy đủ để người nhận hiểu và có thể hành động...");
        inContent.setPrefRowCount(7);
        inContent.setWrapText(true);

        if ("Admin".equalsIgnoreCase(currentUser.getRole())) {
            inAudience.setItems(FXCollections.observableArrayList(
                    ALL_USERS, ALL_STUDENTS, ALL_LECTURERS, BY_CLASS, ONE_USER));
            inAudience.setValue(ALL_USERS);
        } else {
            inAudience.setItems(FXCollections.observableArrayList(BY_CLASS, ONE_USER));
            inAudience.setValue(BY_CLASS);
        }
        inAudience.valueProperty().addListener((obs, oldValue, newValue) -> updateTargetVisibility());
        inClassSection.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (BY_CLASS.equals(inAudience.getValue())) {
                updateRecipientSummary();
            }
        });
        inUser.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (ONE_USER.equals(inAudience.getValue())) {
                updateRecipientSummary();
            }
        });
        inClassSearch.textProperty().addListener(
                (obs, oldValue, newValue) -> filterClassSections(newValue));
        inUserSearch.textProperty().addListener(
                (obs, oldValue, newValue) -> filterUsers(newValue));

        inClassSearch.setPromptText("Tìm theo mã lớp, tên lớp, học kỳ...");
        inUserSearch.setPromptText("Tìm theo mã, họ tên, username hoặc email...");
        inClassSection.setMaxWidth(Double.MAX_VALUE);
        inUser.setMaxWidth(Double.MAX_VALUE);
        inClassSection.setItems(filteredClassSections);
        inUser.setItems(filteredUsers);
        configureClassSectionCells();
        configureUserCells();

        classTarget.getChildren().addAll(
                createFormRow("Tìm lớp học phần", inClassSearch),
                createFormRow("Chọn lớp học phần", inClassSection));
        userTarget.getChildren().addAll(
                createFormRow("Tìm người nhận", inUserSearch),
                createFormRow("Chọn một người dùng", inUser));

        recipientSummary.getStyleClass().add("body-text");
        recipientSummary.setWrapText(true);

        Label sender = new Label("Người gửi: " + currentUser.getFullName()
                + " (" + currentUser.getRole() + ")");
        sender.getStyleClass().add("caption-text");

        Button btnSend = new Button("Gửi thông báo");
        btnSend.getStyleClass().add("btn-primary");
        btnSend.setOnAction(e -> sendNotification());

        HBox actions = new HBox(btnSend);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(12, 0, 0, 0));

        formCard.getChildren().addAll(
                createFormRow("Tiêu đề", inTitle),
                createFormRow("Nội dung", inContent),
                createFormRow("Đối tượng nhận", inAudience),
                classTarget,
                userTarget,
                recipientSummary,
                sender,
                actions
        );
        getChildren().addAll(header, formCard);
    }

    private void loadTargets() {
        List<ClassSection> classes = "Lecturer".equalsIgnoreCase(currentUser.getRole())
                ? classSectionDAO.getByLecturer(currentUser.getId())
                : classSectionDAO.getListClassSection();
        allClassSections.setAll(classes);
        filterClassSections(inClassSearch.getText());

        List<User> users = "Lecturer".equalsIgnoreCase(currentUser.getRole())
                ? userDAO.findStudentsByLecturer(currentUser.getId())
                : userDAO.searchUser("");
        users.removeIf(user -> !"active".equalsIgnoreCase(user.getStatus()));
        users.removeIf(user -> user.getId().equals(currentUser.getId()));
        allUsers.setAll(users);
        filterUsers(inUserSearch.getText());
    }

    private VBox createFormRow(String labelText, Region input) {
        VBox row = new VBox(7);
        Label label = new Label(labelText);
        label.getStyleClass().add("caption-text");
        input.setMaxWidth(Double.MAX_VALUE);
        row.getChildren().addAll(label, input);
        VBox.setVgrow(input, Priority.NEVER);
        return row;
    }

    private void updateTargetVisibility() {
        boolean byClass = BY_CLASS.equals(inAudience.getValue());
        boolean oneUser = ONE_USER.equals(inAudience.getValue());
        classTarget.setVisible(byClass);
        classTarget.setManaged(byClass);
        userTarget.setVisible(oneUser);
        userTarget.setManaged(oneUser);
        updateRecipientSummary();
    }

    private void updateRecipientSummary() {
        List<String> ids = resolveRecipientIds();
        if (BY_CLASS.equals(inAudience.getValue())) {
            ClassSection selectedClass = inClassSection.getValue();
            if (selectedClass == null) {
                recipientSummary.setText(filteredClassSections.isEmpty()
                        ? "Không tìm thấy lớp học phần phù hợp."
                        : "Chọn một lớp học phần để gửi thông báo.");
                return;
            }
            recipientSummary.setText("Lớp " + selectedClass.getClassId()
                    + " có " + ids.size() + " sinh viên sẽ nhận thông báo.");
            return;
        }
        if (ONE_USER.equals(inAudience.getValue())) {
            User selectedUser = inUser.getValue();
            if (selectedUser == null) {
                recipientSummary.setText(filteredUsers.isEmpty()
                        ? "Không tìm thấy người dùng phù hợp."
                        : "Chọn một người dùng để gửi thông báo.");
                return;
            }
            recipientSummary.setText("Người nhận: " + formatUser(selectedUser) + ".");
            return;
        }
        recipientSummary.setText("Thông báo sẽ được gửi tới " + ids.size() + " người nhận.");
    }

    private void filterClassSections(String keyword) {
        String query = normalizeSearch(keyword);
        filteredClassSections.setPredicate(classSection -> query.isEmpty()
                || containsNormalized(classSection.getId(), query)
                || containsNormalized(classSection.getClassId(), query)
                || containsNormalized(classSection.getName(), query)
                || containsNormalized(classSection.getSemester(), query)
                || containsNormalized(classSection.getYear(), query)
                || containsNormalized(classSection.getCourseId(), query));
        keepOrSelectFirst(inClassSection, filteredClassSections);
    }

    private void filterUsers(String keyword) {
        String query = normalizeSearch(keyword);
        filteredUsers.setPredicate(user -> query.isEmpty()
                || containsNormalized(user.getId(), query)
                || containsNormalized(user.getFullName(), query)
                || containsNormalized(user.getUsername(), query)
                || containsNormalized(user.getEmail(), query)
                || containsNormalized(user.getRole(), query));
        keepSelectionOrClear(inUser, filteredUsers);
        if (ONE_USER.equals(inAudience.getValue()) && inUser.getValue() == null) {
            updateRecipientSummary();
        }
    }

    private <T> void keepOrSelectFirst(ComboBox<T> comboBox, List<T> filteredItems) {
        T selected = comboBox.getValue();
        if (selected != null && filteredItems.contains(selected)) {
            return;
        }
        if (filteredItems.isEmpty()) {
            comboBox.getSelectionModel().clearSelection();
        } else {
            comboBox.setValue(filteredItems.get(0));
        }
    }

    private <T> void keepSelectionOrClear(ComboBox<T> comboBox, List<T> filteredItems) {
        T selected = comboBox.getValue();
        if (selected != null && !filteredItems.contains(selected)) {
            comboBox.getSelectionModel().clearSelection();
        }
    }

    private void configureClassSectionCells() {
        inClassSection.setCellFactory(list -> createClassSectionCell());
        inClassSection.setButtonCell(createClassSectionCell());
    }

    private ListCell<ClassSection> createClassSectionCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(ClassSection item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatClassSection(item));
            }
        };
    }

    private void configureUserCells() {
        inUser.setCellFactory(list -> createUserCell());
        inUser.setButtonCell(createUserCell());
    }

    private ListCell<User> createUserCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatUser(item));
            }
        };
    }

    private String formatClassSection(ClassSection classSection) {
        String term = String.join(" ",
                nullToEmpty(classSection.getSemester()),
                nullToEmpty(classSection.getYear())).trim();
        return classSection.getClassId() + " - " + classSection.getName()
                + (term.isEmpty() ? "" : " | " + term);
    }

    private String formatUser(User user) {
        String email = nullToEmpty(user.getEmail()).trim();
        return user.getFullName() + " (" + user.getId() + ")"
                + (email.isEmpty() ? "" : " - " + email);
    }

    private boolean containsNormalized(String value, String normalizedQuery) {
        return normalizeSearch(value).contains(normalizedQuery);
    }

    private String normalizeSearch(String value) {
        String normalized = Normalizer.normalize(
                nullToEmpty(value).trim().toLowerCase(Locale.ROOT),
                Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}+", "")
                .replace('đ', 'd');
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private void sendNotification() {
        String title = inTitle.getText().trim();
        String content = inContent.getText().trim();
        if (title.isEmpty() || content.isEmpty()) {
            FxHelper.showWarning("Vui lòng nhập tiêu đề và nội dung thông báo.");
            return;
        }

        List<String> recipientIds = resolveRecipientIds();
        if (recipientIds.isEmpty()) {
            FxHelper.showWarning("Không tìm thấy người nhận phù hợp.");
            return;
        }

        try {
            int sent = notificationDAO.sendToRecipients(
                    title, content, resolveRecipientType(), recipientIds);
            FxHelper.showInfo("Đã gửi thông báo tới " + sent + " người nhận.");
            inTitle.clear();
            inContent.clear();
        } catch (RuntimeException ex) {
            FxHelper.showError("Gửi thông báo thất bại: " + ex.getMessage());
        }
    }

    private List<String> resolveRecipientIds() {
        String audience = inAudience.getValue();
        if (ALL_USERS.equals(audience)) {
            return userDAO.findActiveUserIds(null);
        }
        if (ALL_STUDENTS.equals(audience)) {
            return userDAO.findActiveUserIds("Student");
        }
        if (ALL_LECTURERS.equals(audience)) {
            return userDAO.findActiveUserIds("Lecturer");
        }
        if (BY_CLASS.equals(audience)) {
            ClassSection selectedClass = inClassSection.getValue();
            return selectedClass == null
                    ? Collections.emptyList()
                    : userDAO.findStudentIdsByClassSection(selectedClass.getId());
        }
        User selectedUser = inUser.getValue();
        return selectedUser == null
                ? Collections.emptyList()
                : Collections.singletonList(selectedUser.getId());
    }

    private String resolveRecipientType() {
        if (BY_CLASS.equals(inAudience.getValue())) {
            return "class:" + inClassSection.getValue().getId();
        }
        if (ONE_USER.equals(inAudience.getValue())) {
            return "user:" + inUser.getValue().getId();
        }
        if (ALL_STUDENTS.equals(inAudience.getValue())) {
            return "all-students";
        }
        if (ALL_LECTURERS.equals(inAudience.getValue())) {
            return "all-lecturers";
        }
        return "all-users";
    }
}
