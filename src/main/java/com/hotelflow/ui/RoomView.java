package com.hotelflow.ui;

import com.hotelflow.manager.RoomManager;
import com.hotelflow.model.Room;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * RoomView — Room management with the purple palette theme.
 * Features: Add new rooms, view in TableView, toggle availability.
 */
public class RoomView {

    private final RoomManager roomManager;
    private TableView<Room> tableView;
    private VBox cachedView;

    public RoomView(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    /**
     * Builds and returns the room management layout.
     */
    public VBox getView() {
        if (cachedView != null) {
            tableView.refresh();
            return cachedView;
        }

        VBox root = new VBox(22);
        root.setPadding(new Insets(36, 40, 36, 40));
        root.getStyleClass().add("page-root");

        // --- Header ---
        Label title = new Label("ROOM MANAGEMENT");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Add, view, and manage hotel rooms");
        subtitle.getStyleClass().add("page-subtitle");

        // --- Add Room Form ---
        VBox formCard = createAddRoomForm();

        // --- Room Table ---
        VBox tableCard = createRoomTable();
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        root.getChildren().addAll(title, subtitle, formCard, tableCard);
        cachedView = root;
        return root;
    }

    /**
     * Creates the 'Add New Room' form.
     */
    private VBox createAddRoomForm() {
        VBox card = new VBox(16);
        card.getStyleClass().add("form-card");
        card.setPadding(new Insets(24));

        Label formTitle = new Label("ADD NEW ROOM");
        formTitle.getStyleClass().add("card-title");

        // Form fields
        HBox formRow = new HBox(16);
        formRow.setAlignment(Pos.CENTER_LEFT);

        // Room Number
        VBox roomNumBox = new VBox(6);
        Label roomNumLabel = new Label("ROOM NUMBER");
        roomNumLabel.getStyleClass().add("field-label");
        TextField roomNumField = new TextField();
        roomNumField.setPromptText("e.g. 101");
        roomNumField.getStyleClass().add("text-input");
        roomNumBox.getChildren().addAll(roomNumLabel, roomNumField);

        // Room Type
        VBox roomTypeBox = new VBox(6);
        Label roomTypeLabel = new Label("ROOM TYPE");
        roomTypeLabel.getStyleClass().add("field-label");
        ComboBox<String> roomTypeCombo = new ComboBox<>(
            FXCollections.observableArrayList("Single", "Double", "Suite", "Deluxe")
        );
        roomTypeCombo.setPromptText("Select type");
        roomTypeCombo.getStyleClass().add("combo-input");
        roomTypeCombo.setMaxWidth(Double.MAX_VALUE);
        roomTypeBox.getChildren().addAll(roomTypeLabel, roomTypeCombo);

        // Price Per Night
        VBox priceBox = new VBox(6);
        Label priceLabel = new Label("PRICE / NIGHT (₹)");
        priceLabel.getStyleClass().add("field-label");
        TextField priceField = new TextField();
        priceField.setPromptText("e.g. 3500");
        priceField.getStyleClass().add("text-input");
        priceBox.getChildren().addAll(priceLabel, priceField);

        HBox.setHgrow(roomNumBox, Priority.ALWAYS);
        HBox.setHgrow(roomTypeBox, Priority.ALWAYS);
        HBox.setHgrow(priceBox, Priority.ALWAYS);

        formRow.getChildren().addAll(roomNumBox, roomTypeBox, priceBox);

        // Add Button
        Button addButton = new Button("+ Add Room");
        addButton.getStyleClass().add("btn-primary");

        // Status label
        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");

        // --- Handler ---
        addButton.setOnAction(e -> {
            String roomNumText = roomNumField.getText().trim();
            String roomType = roomTypeCombo.getValue();
            String priceText = priceField.getText().trim();

            if (roomNumText.isEmpty() || roomType == null || priceText.isEmpty()) {
                showStatus(statusLabel, "Please fill in all fields.", false);
                return;
            }

            int roomNum;
            double price;
            try {
                roomNum = Integer.parseInt(roomNumText);
            } catch (NumberFormatException ex) {
                showStatus(statusLabel, "Room number must be a valid integer.", false);
                return;
            }
            try {
                price = Double.parseDouble(priceText);
                if (price <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showStatus(statusLabel, "Price must be a positive number.", false);
                return;
            }

            Room newRoom = new Room(roomNum, roomType, price, true);
            if (roomManager.addRoom(newRoom)) {
                showStatus(statusLabel, "Room " + roomNum + " added successfully!", true);
                roomNumField.clear();
                roomTypeCombo.setValue(null);
                priceField.clear();
                tableView.refresh();
            } else {
                showStatus(statusLabel, "Room " + roomNum + " already exists.", false);
            }
        });

        card.getChildren().addAll(formTitle, formRow, addButton, statusLabel);
        return card;
    }

    /**
     * Creates the room TableView.
     */
    @SuppressWarnings("unchecked")
    private VBox createRoomTable() {
        VBox card = new VBox(16);
        card.getStyleClass().add("table-card");
        card.setPadding(new Insets(24));
        VBox.setVgrow(card, Priority.ALWAYS);

        Label tableTitle = new Label("ALL ROOMS");
        tableTitle.getStyleClass().add("card-title");

        tableView = new TableView<>();
        tableView.getStyleClass().add("data-table");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        TableColumn<Room, Integer> colNum = new TableColumn<>("Room #");
        colNum.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colNum.setMinWidth(80);

        TableColumn<Room, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colType.setMinWidth(100);

        TableColumn<Room, Double> colPrice = new TableColumn<>("Price/Night");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        colPrice.setMinWidth(120);
        colPrice.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? null : "₹" + String.format("%,.0f", price));
            }
        });

        TableColumn<Room, Boolean> colAvail = new TableColumn<>("Status");
        colAvail.setCellValueFactory(new PropertyValueFactory<>("available"));
        colAvail.setMinWidth(120);
        colAvail.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean available, boolean empty) {
                super.updateItem(available, empty);
                if (empty || available == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(available ? "Available" : "Occupied");
                    badge.getStyleClass().add(available ? "badge-available" : "badge-occupied");
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        // Toggle availability action
        TableColumn<Room, Void> colAction = new TableColumn<>("Action");
        colAction.setMinWidth(130);
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button toggleBtn = new Button("Toggle");
            {
                toggleBtn.getStyleClass().add("btn-secondary");
                toggleBtn.setOnAction(e -> {
                    Room room = getTableView().getItems().get(getIndex());
                    room.setAvailable(!room.isAvailable());
                    getTableView().refresh();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : toggleBtn);
            }
        });

        tableView.getColumns().addAll(colNum, colType, colPrice, colAvail, colAction);
        tableView.setItems(roomManager.getRoomList());

        card.getChildren().addAll(tableTitle, tableView);
        return card;
    }

    private void showStatus(Label label, String message, boolean success) {
        label.setText(message);
        label.getStyleClass().removeAll("status-success", "status-error");
        label.getStyleClass().add(success ? "status-success" : "status-error");
    }
}
