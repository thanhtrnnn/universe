package com.universe.view;

import com.universe.dao.UserDAO;
import com.universe.entity.User;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Giao diện quản lý người dùng (UserManageFrm).
 * Dùng như một Node (VBox) nhúng vào AdminHomeFrm.
 */
public class UserManageFrm extends VBox {

    private final User currentUser;
    private final UserDAO userDAO = new UserDAO();
    
    private final TextField inSearch = new TextField();
    private final TableView<User> tblUser = new TableView<>();
    private final ObservableList<User> data = FXCollections.observableArrayList();

    public UserManageFrm(User currentUser) {
        this.currentUser = currentUser;
        buildUI();
        search(); // Tải dữ liệu ban đầu
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        setSpacing(24);
        setPadding(new Insets(0, 0, 0, 0));

        // ── Header ──
        Label title = new Label("Quản lý Người dùng");
        title.getStyleClass().add("app-title");
        
        Label subtitle = new Label("TÌM KIẾM & CHỈNH SỬA TÀI KHOẢN");
        subtitle.getStyleClass().add("section-kicker");
        
        VBox header = new VBox(8, subtitle, title);

        // ── Controls ──
        HBox controls = new HBox(16);
        controls.setAlignment(Pos.CENTER_LEFT);
        
        inSearch.setPromptText("Nhập ID, Tên hoặc Username...");
        inSearch.setPrefWidth(300);
        inSearch.setOnAction(e -> search());

        Button btnSearch = new Button("Tìm kiếm");
        btnSearch.getStyleClass().add("btn-secondary");
        btnSearch.setOnAction(e -> search());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAdd = new Button("Thêm mới");
        btnAdd.getStyleClass().add("btn-primary");
        btnAdd.setOnAction(e -> {
            new AddUserFrm(this::search).show();
        });

        Button btnEdit = new Button("Sửa");
        btnEdit.getStyleClass().add("btn-secondary");
        btnEdit.setOnAction(e -> {
            User selected = tblUser.getSelectionModel().getSelectedItem();
            if (selected == null) {
                FxHelper.showWarning("Vui lòng chọn một người dùng để sửa");
                return;
            }
            new EditUserFrm(selected, this::search).show();
        });

        Button btnDelete = new Button("Xóa");
        btnDelete.getStyleClass().add("btn-accent");
        btnDelete.setOnAction(e -> {
            User selected = tblUser.getSelectionModel().getSelectedItem();
            if (selected == null) {
                FxHelper.showWarning("Vui lòng chọn một người dùng để xóa");
                return;
            }
            // In a real app we'd use a confirmation dialog, but for simplicity here we can just attempt deletion or assume confirmed.
            try {
                if (userDAO.deleteUser(selected.getId())) {
                    FxHelper.showInfo("Đã xóa người dùng thành công");
                    search();
                } else {
                    FxHelper.showError("Không thể xóa người dùng");
                }
            } catch (Exception ex) {
                FxHelper.showError("Không thể xóa người dùng. Có thể dữ liệu đang được sử dụng ở nơi khác.");
            }
        });

        controls.getChildren().addAll(inSearch, btnSearch, spacer, btnAdd, btnEdit, btnDelete);

        // ── Table ──
        VBox tableCard = new VBox();
        tableCard.getStyleClass().add("card");
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        TableColumn<User, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(100);

        TableColumn<User, String> colName = new TableColumn<>("Họ và tên");
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colName.setPrefWidth(200);

        TableColumn<User, String> colUsername = new TableColumn<>("Tên đăng nhập");
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUsername.setPrefWidth(150);

        TableColumn<User, String> colRole = new TableColumn<>("Vai trò");
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colRole.setPrefWidth(120);
        
        TableColumn<User, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
        colStatus.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    String statusClass = item.equalsIgnoreCase("Active") ? "success" : "danger";
                    setGraphic(FxHelper.createBadge(item, statusClass));
                }
            }
        });
        colStatus.setPrefWidth(100);

        tblUser.getColumns().addAll(colId, colName, colUsername, colRole, colStatus);
        tblUser.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblUser.setPlaceholder(new Label("Không có dữ liệu"));
        tblUser.setItems(data);

        // Double click to edit
        tblUser.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    User rowData = row.getItem();
                    new EditUserFrm(rowData, this::search).show();
                }
            });
            return row;
        });

        VBox.setVgrow(tblUser, Priority.ALWAYS);
        tableCard.getChildren().add(tblUser);

        getChildren().addAll(header, controls, tableCard);
    }

    private void search() {
        String kw = inSearch.getText().trim();
        List<User> list = userDAO.searchUser(kw);
        data.setAll(list);
    }
}
