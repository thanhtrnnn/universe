package com.universe.view;

import com.universe.dao.CourseRecordDAO;
import com.universe.dao.ClassSectionDAO;
import com.universe.entity.ClassSection;
import com.universe.entity.CourseRecord;
import com.universe.entity.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.util.converter.DoubleStringConverter;

import java.util.List;

/**
 * Giao diện nhập điểm (GradeEntryFrm).
 */
public class GradeEntryFrm extends VBox {

    private final User currentUser;
    private final CourseRecordDAO recordDAO = new CourseRecordDAO();
    private final ClassSectionDAO classSectionDAO = new ClassSectionDAO();

    private final ComboBox<ClassSection> inClassSection = new ComboBox<>();
    private final TableView<CourseRecord> tblGrade = new TableView<>();
    private final ObservableList<CourseRecord> data = FXCollections.observableArrayList();

    public GradeEntryFrm(User currentUser) {
        this.currentUser = currentUser;
        buildUI();
        loadClasses();
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        setSpacing(24);

        Label title = new Label("Nhập & Quản lý Điểm số");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("CẬP NHẬT ĐIỂM THÀNH PHẦN VÀ CUỐI KỲ");
        subtitle.getStyleClass().add("section-kicker");
        VBox header = new VBox(8, subtitle, title);

        // ── Controls ──
        HBox controls = new HBox(16);
        controls.setAlignment(Pos.CENTER_LEFT);

        inClassSection.setPromptText("Chọn lớp học phần");
        inClassSection.setPrefWidth(420);
        inClassSection.valueProperty().addListener((obs, oldValue, newValue) -> loadData());

        Button btnSearch = new Button("Xem danh sách");
        btnSearch.getStyleClass().add("btn-secondary");
        btnSearch.setOnAction(e -> loadData());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnSave = new Button("Lưu Bảng điểm");
        btnSave.getStyleClass().add("btn-primary");
        btnSave.setOnAction(e -> saveGrades());

        controls.getChildren().addAll(inClassSection, btnSearch, spacer, btnSave);

        // ── Table ──
        VBox tableCard = new VBox();
        tableCard.getStyleClass().add("card");
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        tblGrade.setEditable(true); // Quan trọng để sửa trực tiếp trên bảng

        TableColumn<CourseRecord, String> colStudentId = new TableColumn<>("Mã Sinh viên");
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colStudentId.setPrefWidth(120);

        TableColumn<CourseRecord, String> colStudentName = new TableColumn<>("Họ và Tên");
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colStudentName.setPrefWidth(220);

        TableColumn<CourseRecord, Double> colScore1 = new TableColumn<>("Điểm CC (20%)");
        colScore1.setCellValueFactory(new PropertyValueFactory<>("score1"));
        colScore1.setCellFactory(createNumberCellFactory());
        colScore1.setOnEditCommit(e -> e.getRowValue().setScore1(e.getNewValue()));
        colScore1.setPrefWidth(100);

        TableColumn<CourseRecord, Double> colScore2 = new TableColumn<>("Điểm GK (30%)");
        colScore2.setCellValueFactory(new PropertyValueFactory<>("score2"));
        colScore2.setCellFactory(createNumberCellFactory());
        colScore2.setOnEditCommit(e -> e.getRowValue().setScore2(e.getNewValue()));
        colScore2.setPrefWidth(100);

        TableColumn<CourseRecord, Double> colExam = new TableColumn<>("Điểm Thi (50%)");
        colExam.setCellValueFactory(new PropertyValueFactory<>("examScore"));
        colExam.setCellFactory(createNumberCellFactory());
        colExam.setOnEditCommit(e -> e.getRowValue().setExamScore(e.getNewValue()));
        colExam.setPrefWidth(100);

        TableColumn<CourseRecord, Double> colTotal = new TableColumn<>("Tổng kết");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalScore"));
        colTotal.setPrefWidth(100);

        tblGrade.getColumns().addAll(colStudentId, colStudentName, colScore1, colScore2, colExam, colTotal);
        tblGrade.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblGrade.setPlaceholder(new Label("Nhập mã lớp để hiển thị bảng điểm. Bấm đúp vào ô điểm để sửa."));
        tblGrade.setItems(data);

        VBox.setVgrow(tblGrade, Priority.ALWAYS);
        tableCard.getChildren().add(tblGrade);

        getChildren().addAll(header, controls, tableCard);
    }

    private void loadData() {
        ClassSection classSection = inClassSection.getValue();
        if (classSection == null) {
            data.clear();
            return;
        }
        List<CourseRecord> list = recordDAO.findByClassSection(classSection.getId());
        data.setAll(list);
    }

    private void loadClasses() {
        List<ClassSection> classes = classSectionDAO.getByLecturer(currentUser.getId());
        inClassSection.setItems(FXCollections.observableArrayList(classes));
        if (!classes.isEmpty()) {
            inClassSection.setValue(classes.get(0));
        }
    }

    private void saveGrades() {
        if (data.isEmpty()) {
            FxHelper.showWarning("Bảng điểm trống.");
            return;
        }
        boolean ok = true;
        for (CourseRecord r : data) {
            if (!recordDAO.editGrade(r)) {
                ok = false;
            }
        }
        if (ok) {
            FxHelper.showInfo("Cập nhật điểm thành công!");
            tblGrade.refresh();
        } else {
            FxHelper.showError("Có lỗi xảy ra khi cập nhật điểm.");
        }
    }

    private Callback<TableColumn<CourseRecord, Double>, TableCell<CourseRecord, Double>> createNumberCellFactory() {
        return col -> {
            TextFieldTableCell<CourseRecord, Double> cell = new TextFieldTableCell<>(new DoubleStringConverter()) {
                @Override
                public void startEdit() {
                    super.startEdit();
                    if (getGraphic() instanceof TextField tf) {
                        tf.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                            if (!isNowFocused) {
                                try {
                                    commitEdit(getConverter().fromString(tf.getText()));
                                } catch (Exception ignored) {
                                    cancelEdit();
                                }
                            }
                        });
                    }
                }
            };
            return cell;
        };
    }
}
