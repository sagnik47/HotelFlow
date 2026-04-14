package com.hotelflow.ui;

import com.hotelflow.manager.BookingManager;
import com.hotelflow.manager.CustomerManager;
import com.hotelflow.manager.RoomManager;
import com.hotelflow.model.Booking;
import com.hotelflow.model.Customer;
import com.hotelflow.model.Room;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.time.LocalDate;

/**
 * BookingView — Booking management with the purple palette theme.
 * Features: Create bookings, view in TableView, and checkout (free rooms).
 */
public class BookingView {

    private final RoomManager roomManager;
    private final CustomerManager customerManager;
    private final BookingManager bookingManager;
    private final DashboardView dashboardView;

    private TableView<Booking> tableView;
    private ComboBox<Room> roomCombo;
    private VBox cachedView;

    public BookingView(RoomManager roomManager, CustomerManager customerManager,
                       BookingManager bookingManager, DashboardView dashboardView) {
        this.roomManager = roomManager;
        this.customerManager = customerManager;
        this.bookingManager = bookingManager;
        this.dashboardView = dashboardView;
    }

    /**
     * Builds and returns the booking management layout.
     */
    public VBox getView() {
        if (cachedView != null) {
            if (roomCombo != null) {
                roomCombo.setItems(roomManager.getAvailableRooms());
            }
            tableView.refresh();
            return cachedView;
        }

        VBox root = new VBox(22);
        root.setPadding(new Insets(36, 40, 36, 40));
        root.getStyleClass().add("page-root");

        Label title = new Label("BOOKING MANAGEMENT");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Create bookings and manage check-outs");
        subtitle.getStyleClass().add("page-subtitle");

        VBox formCard = createBookingForm();
        VBox tableCard = createBookingTable();
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        root.getChildren().addAll(title, subtitle, formCard, tableCard);
        cachedView = root;
        return root;
    }

    /**
     * Applies styling to the DatePicker.
     * Uses setDayCellFactory to guarantee every calendar day cell retains its text color
     * even when the JavaFX skin redraws or updates cells.
     */
    private void styleDatePicker(DatePicker picker) {
        // Dark text field body
        picker.getEditor().setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #F0EAF6; "
            + "-fx-prompt-text-fill: #6B5C80; -fx-font-family: 'Inter'; -fx-font-size: 13px;"
        );

        // Cell Factory physically guarantees that our styles aren't overridden by virtualized layout calls
        picker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: white;");
                } else {
                    boolean isSelected = isSelected();
                    boolean isToday = item.equals(LocalDate.now());

                    if (isSelected) {
                        setStyle("-fx-background-color: #50207A; -fx-text-fill: white; -fx-font-family: 'Inter'; -fx-font-weight: 900; -fx-font-size: 13px; -fx-background-radius: 4;");
                    } else if (isToday) {
                        setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-family: 'Inter'; -fx-font-weight: 900; -fx-font-size: 13px; -fx-border-color: #838CE5; -fx-border-width: 2; -fx-border-radius: 4;");
                    } else {
                        setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-family: 'Inter'; -fx-font-weight: 900; -fx-font-size: 13px;");
                    }
                }
            }
        });

        // Retain header override
        picker.setOnShown(event -> {
            Node popupContent = picker.lookup(".date-picker-popup");
            if (popupContent != null) {
                popupContent.setStyle("-fx-background-color: white;"); // Match the cells

                Node monthYearPane = popupContent.lookup(".month-year-pane");
                if (monthYearPane != null) {
                    monthYearPane.setStyle("-fx-background-color: #50207A; -fx-padding: 8 10;");
                    for (Node label : monthYearPane.lookupAll(".label")) {
                        label.setStyle("-fx-text-fill: white; -fx-font-family: 'Inter'; -fx-font-weight: 900; -fx-font-size: 13px;");
                    }
                    for (Node arrow : monthYearPane.lookupAll(".left-arrow")) {
                        arrow.setStyle("-fx-background-color: white;");
                    }
                    for (Node arrow : monthYearPane.lookupAll(".right-arrow")) {
                        arrow.setStyle("-fx-background-color: white;");
                    }
                }

                // Day headers (Su, Mo, Tu...)
                for (Node dayName : popupContent.lookupAll(".day-name-cell")) {
                    dayName.setStyle("-fx-text-fill: black; -fx-background-color: white; -fx-font-family: 'Inter'; -fx-font-size: 12px; -fx-font-weight: 900;");
                }
                
                // Week numbers
                for (Node weekNum : popupContent.lookupAll(".week-number-cell")) {
                    weekNum.setStyle("-fx-text-fill: black; -fx-background-color: white; -fx-font-size: 11px; -fx-font-weight: 900;");
                }
            }
        });
    }

    /**
     * Creates the booking form.
     */
    private VBox createBookingForm() {
        VBox card = new VBox(16);
        card.getStyleClass().add("form-card");
        card.setPadding(new Insets(24));

        Label formTitle = new Label("NEW BOOKING");
        formTitle.getStyleClass().add("card-title");

        // --- Row 1: Customer + Room ---
        HBox row1 = new HBox(16);
        row1.setAlignment(Pos.CENTER_LEFT);

        VBox custBox = new VBox(6);
        Label custLabel = new Label("SELECT CUSTOMER");
        custLabel.getStyleClass().add("field-label");
        ComboBox<Customer> customerCombo = new ComboBox<>(
            customerManager.getCustomerList()
        );
        customerCombo.setPromptText("Choose a customer");
        customerCombo.getStyleClass().add("combo-input");
        customerCombo.setMaxWidth(Double.MAX_VALUE);
        customerCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Customer c) {
                return c == null ? "" : c.getName() + " (" + c.getCustomerId() + ")";
            }
            @Override public Customer fromString(String s) { return null; }
        });
        custBox.getChildren().addAll(custLabel, customerCombo);

        VBox roomBox = new VBox(6);
        Label roomLabel = new Label("SELECT ROOM");
        roomLabel.getStyleClass().add("field-label");
        roomCombo = new ComboBox<>();
        roomCombo.setPromptText("Choose a room");
        roomCombo.getStyleClass().add("combo-input");
        roomCombo.setMaxWidth(Double.MAX_VALUE);
        roomCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Room r) {
                return r == null ? ""
                    : "Room " + r.getRoomNumber() + "  -  " + r.getRoomType()
                      + "  (₹" + String.format("%,.0f", r.getPricePerNight()) + "/night)";
            }
            @Override public Room fromString(String s) { return null; }
        });
        roomCombo.setOnShowing(e -> roomCombo.setItems(roomManager.getAvailableRooms()));
        roomCombo.setItems(roomManager.getAvailableRooms());
        roomBox.getChildren().addAll(roomLabel, roomCombo);

        HBox.setHgrow(custBox, Priority.ALWAYS);
        HBox.setHgrow(roomBox, Priority.ALWAYS);
        row1.getChildren().addAll(custBox, roomBox);

        // --- Row 2: Dates ---
        HBox row2 = new HBox(16);
        row2.setAlignment(Pos.CENTER_LEFT);

        VBox checkInBox = new VBox(6);
        Label checkInLabel = new Label("CHECK-IN DATE");
        checkInLabel.getStyleClass().add("field-label");
        DatePicker checkInPicker = new DatePicker();
        checkInPicker.setPromptText("Select date");
        checkInPicker.getStyleClass().add("date-input");
        checkInPicker.setMaxWidth(Double.MAX_VALUE);
        styleDatePicker(checkInPicker); // Apply dark theme to popup
        checkInBox.getChildren().addAll(checkInLabel, checkInPicker);

        VBox checkOutBox = new VBox(6);
        Label checkOutLabel = new Label("CHECK-OUT DATE");
        checkOutLabel.getStyleClass().add("field-label");
        DatePicker checkOutPicker = new DatePicker();
        checkOutPicker.setPromptText("Select date");
        checkOutPicker.getStyleClass().add("date-input");
        checkOutPicker.setMaxWidth(Double.MAX_VALUE);
        styleDatePicker(checkOutPicker); // Apply dark theme to popup
        checkOutBox.getChildren().addAll(checkOutLabel, checkOutPicker);

        HBox.setHgrow(checkInBox, Priority.ALWAYS);
        HBox.setHgrow(checkOutBox, Priority.ALWAYS);
        row2.getChildren().addAll(checkInBox, checkOutBox);

        // Book Button
        Button bookButton = new Button("📋  Create Booking");
        bookButton.getStyleClass().add("btn-primary");

        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");

        // --- Handler ---
        bookButton.setOnAction(e -> {
            Customer customer = customerCombo.getValue();
            Room room = roomCombo.getValue();
            LocalDate checkIn = checkInPicker.getValue();
            LocalDate checkOut = checkOutPicker.getValue();

            if (customer == null || room == null) {
                showStatus(statusLabel, "Please select both a customer and a room.", false);
                return;
            }
            if (checkIn == null || checkOut == null) {
                showStatus(statusLabel, "Please select both dates.", false);
                return;
            }

            String result = bookingManager.createBooking(
                customer.getCustomerId(), room.getRoomNumber(), checkIn, checkOut
            );

            if (result.startsWith("ERROR:")) {
                showStatus(statusLabel, result.substring(7), false);
            } else {
                showStatus(statusLabel, "Booking " + result + " created successfully!", true);
                customerCombo.setValue(null);
                roomCombo.setValue(null);
                roomCombo.setItems(roomManager.getAvailableRooms());
                checkInPicker.setValue(null);
                checkOutPicker.setValue(null);
                tableView.refresh();
                dashboardView.refreshDashboard();
            }
        });

        card.getChildren().addAll(formTitle, row1, row2, bookButton, statusLabel);
        return card;
    }

    /**
     * Creates the booking TableView with checkout action.
     */
    @SuppressWarnings("unchecked")
    private VBox createBookingTable() {
        VBox card = new VBox(16);
        card.getStyleClass().add("table-card");
        card.setPadding(new Insets(24));
        VBox.setVgrow(card, Priority.ALWAYS);

        Label tableTitle = new Label("ALL BOOKINGS");
        tableTitle.getStyleClass().add("card-title");

        tableView = new TableView<>();
        tableView.getStyleClass().add("data-table");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        TableColumn<Booking, String> colId = new TableColumn<>("Booking ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colId.setMinWidth(90);

        TableColumn<Booking, String> colCust = new TableColumn<>("Customer");
        colCust.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colCust.setMinWidth(120);

        TableColumn<Booking, Integer> colRoom = new TableColumn<>("Room #");
        colRoom.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colRoom.setMinWidth(70);

        TableColumn<Booking, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colType.setMinWidth(75);

        TableColumn<Booking, LocalDate> colIn = new TableColumn<>("Check-in");
        colIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        colIn.setMinWidth(100);

        TableColumn<Booking, LocalDate> colOut = new TableColumn<>("Check-out");
        colOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        colOut.setMinWidth(100);

        TableColumn<Booking, Double> colCost = new TableColumn<>("Total (₹)");
        colCost.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        colCost.setMinWidth(90);
        colCost.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double cost, boolean empty) {
                super.updateItem(cost, empty);
                setText(empty || cost == null ? null : "₹" + String.format("%,.0f", cost));
            }
        });

        // Status badge — uses palette colors only
        TableColumn<Booking, Boolean> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("active"));
        colStatus.setMinWidth(100);
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean active, boolean empty) {
                super.updateItem(active, empty);
                if (empty || active == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(active ? "Active" : "Checked Out");
                    badge.getStyleClass().add(active ? "badge-active" : "badge-checkedout");
                    setGraphic(badge);
                }
                setText(null);
            }
        });

        // Checkout action
        TableColumn<Booking, Void> colAction = new TableColumn<>("Action");
        colAction.setMinWidth(110);
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button checkoutBtn = new Button("Checkout");
            {
                checkoutBtn.getStyleClass().add("btn-danger");
                checkoutBtn.setOnAction(e -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    if (booking.isActive()) {
                        bookingManager.checkout(booking.getBookingId());
                        getTableView().refresh();
                        // Refresh room combo so freed room is immediately available
                        if (roomCombo != null) {
                            roomCombo.setItems(roomManager.getAvailableRooms());
                        }
                        dashboardView.refreshDashboard();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Booking booking = getTableView().getItems().get(getIndex());
                    setGraphic(booking.isActive() ? checkoutBtn : null);
                }
            }
        });

        tableView.getColumns().addAll(colId, colCust, colRoom, colType,
                                      colIn, colOut, colCost, colStatus, colAction);
        tableView.setItems(bookingManager.getBookingList());

        card.getChildren().addAll(tableTitle, tableView);
        return card;
    }

    private void showStatus(Label label, String message, boolean success) {
        label.setText(message);
        label.getStyleClass().removeAll("status-success", "status-error");
        label.getStyleClass().add(success ? "status-success" : "status-error");
    }
}
