package com.universe.view;

import com.universe.dao.CourseRecordDAO;
import com.universe.entity.CourseRecord;
import com.universe.entity.User;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Giao diện xem điểm của sinh viên (ViewGradeFrm).
 */
public class ViewGradeFrm extends VBox {

    private final User currentUser;
    private final CourseRecordDAO recordDAO = new CourseRecordDAO();

    private final TableView<CourseRecord> tblGrade = new TableView<>();
    private final ObservableList<CourseRecord> data = FXCollections.observableArrayList();

    public ViewGradeFrm(User currentUser) {
        this.currentUser = currentUser;
        buildUI();
        loadData();
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        setSpacing(24);

        Label title = new Label("Bảng điểm Cá nhân");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("KẾT QUẢ HỌC TẬP CỦA SINH VIÊN " + currentUser.getFullName().toUpperCase());
        subtitle.getStyleClass().add("section-kicker");
        VBox header = new VBox(8, subtitle, title);

        // ── Thống kê tổng quan ──
        HBox stats = new HBox(24);
        VBox cardTotal = FxHelper.createStatCard("MÔN ĐÃ ĐĂNG KÝ", String.valueOf(data.size()));
        
        long passed = data.stream().filter(r -> r.getTotalScore() != null && r.getTotalScore() >= 4.0).count();
        VBox cardPassed = FxHelper.createStatCard("SỐ MÔN ĐÃ QUA", String.valueOf(passed));
        cardPassed.setStyle("-fx-border-color: -ux-success;"); // Success color

        double sum = data.stream().filter(r -> r.getTotalScore() != null).mapToDouble(CourseRecord::getTotalScore).sum();
        long graded = data.stream().filter(r -> r.getTotalScore() != null).count();
        double avg = graded > 0 ? sum / graded : 0;
        VBox cardGPA = FxHelper.createStatCard("ĐIỂM TRUNG BÌNH", String.format("%.2f", avg));
        cardGPA.setStyle("-fx-border-color: -ux-warning;");

        stats.getChildren().addAll(cardTotal, cardPassed, cardGPA);

        // ── Table ──
        VBox tableCard = new VBox();
        tableCard.getStyleClass().add("card");
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        TableColumn<CourseRecord, String> colClassId = new TableColumn<>("Lớp Học phần");
        colClassId.setCellValueFactory(new PropertyValueFactory<>("classSectionName"));
        colClassId.setPrefWidth(250);

        TableColumn<CourseRecord, String> colScore1 = new TableColumn<>("CC (20%)");
        colScore1.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getScore1() != null ? String.valueOf(c.getValue().getScore1()) : "-"));
        colScore1.setPrefWidth(100);

        TableColumn<CourseRecord, String> colScore2 = new TableColumn<>("GK (30%)");
        colScore2.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getScore2() != null ? String.valueOf(c.getValue().getScore2()) : "-"));
        colScore2.setPrefWidth(100);

        TableColumn<CourseRecord, String> colExam = new TableColumn<>("Thi (50%)");
        colExam.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getExamScore() != null ? String.valueOf(c.getValue().getExamScore()) : "-"));
        colExam.setPrefWidth(100);

        TableColumn<CourseRecord, String> colTotal = new TableColumn<>("Tổng kết");
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTotalScore() != null ? String.valueOf(c.getValue().getTotalScore()) : "Chưa có"));
        colTotal.setPrefWidth(100);

        TableColumn<CourseRecord, String> colStatus = new TableColumn<>("Đánh giá");
        colStatus.setCellValueFactory(c -> {
            Double t = c.getValue().getTotalScore();
            if (t == null) return new SimpleStringProperty("PENDING");
            return new SimpleStringProperty(t >= 4.0 ? "PASSED" : "FAILED");
        });
        colStatus.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    String statusClass = item.equals("PASSED") ? "success" 
                                       : item.equals("FAILED") ? "danger" 
                                       : "warning";
                    setGraphic(FxHelper.createBadge(item, statusClass));
                }
            }
        });
        colStatus.setPrefWidth(100);

        tblGrade.getColumns().addAll(colClassId, colScore1, colScore2, colExam, colTotal, colStatus);
        tblGrade.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblGrade.setPlaceholder(new Label("Chưa có dữ liệu điểm."));
        tblGrade.setItems(data);

        VBox.setVgrow(tblGrade, Priority.ALWAYS);
        tableCard.getChildren().add(tblGrade);

        getChildren().addAll(header, stats, tableCard);
    }

    private void loadData() {
        List<CourseRecord> list = recordDAO.viewGrade(currentUser.getId());
        data.setAll(list);
        
        // Refresh UI (để cập nhật lại số liệu thống kê)
        if (!getChildren().isEmpty()) {
            HBox stats = (HBox) getChildren().get(1);
            
            ((Label) ((VBox) stats.getChildren().get(0)).getChildren().get(1)).setText(String.valueOf(data.size()));
            
            long passed = data.stream().filter(r -> r.getTotalScore() != null && r.getTotalScore() >= 4.0).count();
            ((Label) ((VBox) stats.getChildren().get(1)).getChildren().get(1)).setText(String.valueOf(passed));
            
            double sum = data.stream().filter(r -> r.getTotalScore() != null).mapToDouble(CourseRecord::getTotalScore).sum();
            long graded = data.stream().filter(r -> r.getTotalScore() != null).count();
            double avg = graded > 0 ? sum / graded : 0;
            ((Label) ((VBox) stats.getChildren().get(2)).getChildren().get(1)).setText(String.format("%.2f", avg));
        }
    }
}
