package com.hotelflow;

import com.hotelflow.manager.BookingManager;
import com.hotelflow.manager.CustomerManager;
import com.hotelflow.manager.RoomManager;
import com.hotelflow.ui.BookingView;
import com.hotelflow.ui.CustomerView;
import com.hotelflow.ui.DashboardView;
import com.hotelflow.ui.RoomView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * HotelFlowApp — Main entry point for the Hotel Management System.
 *
 * Architecture:
 *   Model:      Room, Customer, Booking       (data classes with JavaFX properties)
 *   Manager:    RoomManager, CustomerManager,  (business logic + in-memory storage)
 *              BookingManager
 *   View:       DashboardView, RoomView,       (JavaFX UI builders)
 *              CustomerView, BookingView
 *
 * Uses a sidebar layout inspired by the Primeclass hotel management UI,
 * with the purple palette (#50207A, #D6B9FC, #838CE5),
 * Bebas Neue for headings, and Inter for body text.
 */
public class HotelFlowApp extends Application {

    // Content pane that holds the active view
    private StackPane contentPane;

    // Reference to DashboardView for refresh
    private DashboardView dashboardView;

    // Track the currently active nav button to toggle styles
    private Button activeNavButton;

    @Override
    public void start(Stage primaryStage) {

        // === Load Custom Fonts ===
        Font.loadFont(getClass().getResourceAsStream("/fonts/BebasNeue-Regular.ttf"), 28);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Inter-Variable.ttf"), 13);

        // === Initialize Managers (in-memory data stores) ===
        RoomManager roomManager = new RoomManager();
        CustomerManager customerManager = new CustomerManager();
        BookingManager bookingManager = new BookingManager(roomManager, customerManager);

        // === Initialize Views ===
        dashboardView = new DashboardView(roomManager, customerManager, bookingManager);
        RoomView roomView = new RoomView(roomManager);
        CustomerView customerView = new CustomerView(customerManager);
        BookingView bookingView = new BookingView(roomManager, customerManager, bookingManager, dashboardView);

        // === Build Sidebar ===
        VBox sidebar = buildSidebar(dashboardView, roomView, customerView, bookingView);

        // === Content Area (scrollable) ===
        contentPane = new StackPane();
        contentPane.getStyleClass().add("content-area");

        // Show dashboard by default
        setContent(dashboardView.getView());

        ScrollPane scrollPane = new ScrollPane(contentPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("scroll-pane");

        // === Main Layout: Sidebar + Content ===
        HBox mainLayout = new HBox();
        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        mainLayout.getChildren().addAll(sidebar, scrollPane);

        // === Create Scene & Apply Stylesheet ===
        Scene scene = new Scene(mainLayout, 1280, 780);
        String css = getClass().getResource("/styles/application.css").toExternalForm();
        scene.getStylesheets().add(css);

        // === Configure Primary Stage ===
        primaryStage.setTitle("HotelFlow — Hotel Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(650);
        primaryStage.show();
    }

    /**
     * Builds the sidebar navigation panel with brand logo, nav items, and footer.
     */
    private VBox buildSidebar(DashboardView dashView, RoomView roomView,
                              CustomerView custView, BookingView bookView) {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);

        // --- Brand / Logo ---
        HBox brand = new HBox(4);
        brand.getStyleClass().add("sidebar-brand");
        brand.setAlignment(Pos.CENTER_LEFT);

        Label logoMain = new Label("HOTEL");
        logoMain.getStyleClass().add("sidebar-logo");
        Label logoAccent = new Label("FLOW");
        logoAccent.getStyleClass().add("sidebar-logo-accent");
        brand.getChildren().addAll(logoMain, logoAccent);

        // Divider below brand
        Region divider1 = new Region();
        divider1.getStyleClass().add("sidebar-divider");
        divider1.setMaxWidth(Double.MAX_VALUE);

        // --- Section: Main ---
        Label sectionMain = new Label("MAIN");
        sectionMain.getStyleClass().add("sidebar-section-label");

        Button navDashboard = createNavButton("📊   Dashboard");
        Button navRooms = createNavButton("🏨   Rooms");
        Button navCustomers = createNavButton("👤   Customers");
        Button navBookings = createNavButton("📋   Bookings");

        // Set dashboard as active by default
        navDashboard.getStyleClass().add("nav-btn-active");
        activeNavButton = navDashboard;

        // --- Navigation Handlers ---
        navDashboard.setOnAction(e -> {
            setActiveNav(navDashboard);
            dashView.refreshDashboard();
            setContent(dashView.getView());
        });

        navRooms.setOnAction(e -> {
            setActiveNav(navRooms);
            setContent(roomView.getView());
        });

        navCustomers.setOnAction(e -> {
            setActiveNav(navCustomers);
            setContent(custView.getView());
        });

        navBookings.setOnAction(e -> {
            setActiveNav(navBookings);
            setContent(bookView.getView());
        });

        // --- Section: System ---
        Label sectionSystem = new Label("SYSTEM");
        sectionSystem.getStyleClass().add("sidebar-section-label");

        // Divider
        Region divider2 = new Region();
        divider2.getStyleClass().add("sidebar-divider");
        divider2.setMaxWidth(Double.MAX_VALUE);

        // --- Footer ---
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox footer = new VBox(6);
        footer.setPadding(new Insets(16, 24, 24, 24));
        Label version = new Label("HotelFlow v1.0");
        version.setStyle("-fx-text-fill: #6B5C80; -fx-font-size: 11px; -fx-font-family: 'Inter';");
        Label session = new Label("In-Memory Session");
        session.setStyle("-fx-text-fill: #50207A; -fx-font-size: 10px; -fx-font-family: 'Inter'; -fx-font-weight: 600;");
        footer.getChildren().addAll(version, session);

        sidebar.getChildren().addAll(
            brand, divider1,
            sectionMain, navDashboard, navRooms, navCustomers, navBookings,
            spacer, divider2, footer
        );

        return sidebar;
    }

    /**
     * Creates a styled sidebar navigation button.
     */
    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    /**
     * Sets the active navigation button (updates visual state).
     */
    private void setActiveNav(Button btn) {
        if (activeNavButton != null) {
            activeNavButton.getStyleClass().remove("nav-btn-active");
        }
        btn.getStyleClass().add("nav-btn-active");
        activeNavButton = btn;
    }

    /**
     * Replaces the content pane with the given view.
     */
    private void setContent(Node view) {
        contentPane.getChildren().clear();
        contentPane.getChildren().add(view);
    }

    /**
     * Main method — launches the JavaFX application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
