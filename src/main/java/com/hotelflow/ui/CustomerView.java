package com.hotelflow.ui;

import com.hotelflow.manager.CustomerManager;
import com.hotelflow.model.Customer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * CustomerView — Customer management with the purple palette theme.
 * Features: Register new customers, view in TableView.
 */
public class CustomerView {

    private final CustomerManager customerManager;
    private TableView<Customer> tableView;
    private VBox cachedView;

    public CustomerView(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    /**
     * Builds and returns the customer management layout.
     */
    public VBox getView() {
        if (cachedView != null) {
            tableView.refresh();
            return cachedView;
        }

        VBox root = new VBox(22);
        root.setPadding(new Insets(36, 40, 36, 40));
        root.getStyleClass().add("page-root");

        Label title = new Label("CUSTOMER MANAGEMENT");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Register and manage hotel guests");
        subtitle.getStyleClass().add("page-subtitle");

        VBox formCard = createAddCustomerForm();
        VBox tableCard = createCustomerTable();
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        root.getChildren().addAll(title, subtitle, formCard, tableCard);
        cachedView = root;
        return root;
    }

    /**
     * Creates the 'Register New Customer' form.
     */
    private VBox createAddCustomerForm() {
        VBox card = new VBox(16);
        card.getStyleClass().add("form-card");
        card.setPadding(new Insets(24));

        Label formTitle = new Label("REGISTER NEW CUSTOMER");
        formTitle.getStyleClass().add("card-title");

        HBox formRow = new HBox(16);
        formRow.setAlignment(Pos.CENTER_LEFT);

        // Customer ID
        VBox idBox = new VBox(6);
        Label idLabel = new Label("CUSTOMER ID");
        idLabel.getStyleClass().add("field-label");
        TextField idField = new TextField();
        idField.setPromptText("e.g. ID004");
        idField.getStyleClass().add("text-input");
        idBox.getChildren().addAll(idLabel, idField);

        // Name
        VBox nameBox = new VBox(6);
        Label nameLabel = new Label("FULL NAME");
        nameLabel.getStyleClass().add("field-label");
        TextField nameField = new TextField();
        nameField.setPromptText("e.g. John Doe");
        nameField.getStyleClass().add("text-input");
        nameBox.getChildren().addAll(nameLabel, nameField);

        // Phone
        VBox phoneBox = new VBox(6);
        Label phoneLabel = new Label("PHONE NUMBER");
        phoneLabel.getStyleClass().add("field-label");
        TextField phoneField = new TextField();
        phoneField.setPromptText("e.g. 9876543210");
        phoneField.getStyleClass().add("text-input");
        phoneBox.getChildren().addAll(phoneLabel, phoneField);

        HBox.setHgrow(idBox, Priority.ALWAYS);
        HBox.setHgrow(nameBox, Priority.ALWAYS);
        HBox.setHgrow(phoneBox, Priority.ALWAYS);

        formRow.getChildren().addAll(idBox, nameBox, phoneBox);

        Button addButton = new Button("+ Register Customer");
        addButton.getStyleClass().add("btn-primary");

        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");

        // --- Handler ---
        addButton.setOnAction(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();

            if (id.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                showStatus(statusLabel, "Please fill in all fields.", false);
                return;
            }

            if (!phone.matches("\\d{10}")) {
                showStatus(statusLabel, "Phone must be exactly 10 digits.", false);
                return;
            }

            Customer customer = new Customer(id, name, phone);
            if (customerManager.addCustomer(customer)) {
                showStatus(statusLabel, "Customer '" + name + "' registered!", true);
                idField.clear();
                nameField.clear();
                phoneField.clear();
                tableView.refresh();
            } else {
                showStatus(statusLabel, "Customer ID '" + id + "' already exists.", false);
            }
        });

        card.getChildren().addAll(formTitle, formRow, addButton, statusLabel);
        return card;
    }

    /**
     * Creates the customer TableView.
     */
    @SuppressWarnings("unchecked")
    private VBox createCustomerTable() {
        VBox card = new VBox(16);
        card.getStyleClass().add("table-card");
        card.setPadding(new Insets(24));
        VBox.setVgrow(card, Priority.ALWAYS);

        Label tableTitle = new Label("REGISTERED CUSTOMERS");
        tableTitle.getStyleClass().add("card-title");

        tableView = new TableView<>();
        tableView.getStyleClass().add("data-table");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        TableColumn<Customer, String> colId = new TableColumn<>("Customer ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colId.setMinWidth(120);

        TableColumn<Customer, String> colName = new TableColumn<>("Full Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setMinWidth(200);

        TableColumn<Customer, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPhone.setMinWidth(160);

        tableView.getColumns().addAll(colId, colName, colPhone);
        tableView.setItems(customerManager.getCustomerList());

        card.getChildren().addAll(tableTitle, tableView);
        return card;
    }

    private void showStatus(Label label, String message, boolean success) {
        label.setText(message);
        label.getStyleClass().removeAll("status-success", "status-error");
        label.getStyleClass().add(success ? "status-success" : "status-error");
    }
}
