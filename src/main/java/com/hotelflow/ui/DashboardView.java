package com.hotelflow.ui;

import com.hotelflow.manager.BookingManager;
import com.hotelflow.manager.CustomerManager;
import com.hotelflow.manager.RoomManager;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

/**
 * DashboardView — Primeclass-inspired hotel dashboard.
 * Uses ONLY the 3 palette colors: #50207A, #D6B9FC, #838CE5
 * and their opacity/shade variations.
 */
public class DashboardView {

    private final RoomManager roomManager;
    private final CustomerManager customerManager;
    private final BookingManager bookingManager;

    private Label totalRoomsValue;
    private Label availableRoomsValue;
    private Label occupiedRoomsValue;
    private Label totalCustomersValue;
    private Label activeBookingsValue;
    private Label totalRevenueValue;

    private VBox cachedView;

    public DashboardView(RoomManager roomManager, CustomerManager customerManager,
                         BookingManager bookingManager) {
        this.roomManager = roomManager;
        this.customerManager = customerManager;
        this.bookingManager = bookingManager;
    }

    /**
     * Builds and returns the dashboard layout.
     */
    public VBox getView() {
        if (cachedView != null) {
            refreshDashboard();
            return cachedView;
        }

        VBox root = new VBox(28);
        root.setPadding(new Insets(36, 40, 36, 40));
        root.getStyleClass().add("dashboard-root");

        // --- Header ---
        VBox headerBox = new VBox(6);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("DASHBOARD");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Overview of your hotel at a glance");
        subtitle.getStyleClass().add("page-subtitle");

        headerBox.getChildren().addAll(title, subtitle);

        // --- Stat Cards Grid (3 columns × 2 rows) ---
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(18);
        statsGrid.setVgap(18);

        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(33.33);
            col.setHgrow(Priority.ALWAYS);
            statsGrid.getColumnConstraints().add(col);
        }

        // Initialize value labels
        totalRoomsValue = new Label("0");
        availableRoomsValue = new Label("0");
        occupiedRoomsValue = new Label("0");
        totalCustomersValue = new Label("0");
        activeBookingsValue = new Label("0");
        totalRevenueValue = new Label("₹0");

        // All cards use the 3 palette colors only
        // #50207A (deep purple), #D6B9FC (lavender), #838CE5 (periwinkle)
        VBox card1 = createStatCard("Total Rooms", totalRoomsValue,
            "rgba(80, 32, 122, 0.4)", "rgba(80, 32, 122, 0.15)", "rgba(80, 32, 122, 0.35)", "🏨");
        VBox card2 = createStatCard("Available", availableRoomsValue,
            "rgba(131, 140, 229, 0.25)", "rgba(131, 140, 229, 0.08)", "rgba(131, 140, 229, 0.2)", "✓");
        VBox card3 = createStatCard("Occupied", occupiedRoomsValue,
            "rgba(214, 185, 252, 0.2)", "rgba(214, 185, 252, 0.06)", "rgba(214, 185, 252, 0.18)", "⬤");
        VBox card4 = createStatCard("Customers", totalCustomersValue,
            "rgba(131, 140, 229, 0.25)", "rgba(131, 140, 229, 0.08)", "rgba(131, 140, 229, 0.2)", "👤");
        VBox card5 = createStatCard("Active Bookings", activeBookingsValue,
            "rgba(80, 32, 122, 0.4)", "rgba(80, 32, 122, 0.15)", "rgba(80, 32, 122, 0.35)", "📋");
        VBox card6 = createStatCard("Revenue", totalRevenueValue,
            "rgba(214, 185, 252, 0.2)", "rgba(214, 185, 252, 0.06)", "rgba(214, 185, 252, 0.18)", "₹");

        statsGrid.add(card1, 0, 0);
        statsGrid.add(card2, 1, 0);
        statsGrid.add(card3, 2, 0);
        statsGrid.add(card4, 0, 1);
        statsGrid.add(card5, 1, 1);
        statsGrid.add(card6, 2, 1);

        // --- Welcome Banner ---
        VBox welcomeBanner = createWelcomeBanner();

        root.getChildren().addAll(headerBox, statsGrid, welcomeBanner);

        refreshDashboard();
        animateEntrance(statsGrid);

        cachedView = root;
        return root;
    }

    /**
     * Creates a stat card using only the palette colors via inline styles.
     * @param gradFrom  Gradient start color (rgba)
     * @param gradTo    Gradient end color (rgba)
     * @param borderClr Border color (rgba)
     */
    private VBox createStatCard(String label, Label valueLabel,
                                String gradFrom, String gradTo, String borderClr,
                                String emoji) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(24));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + gradFrom + ", " + gradTo + "); "
            + "-fx-background-radius: 16; -fx-border-radius: 16; "
            + "-fx-border-color: " + borderClr + "; -fx-border-width: 1; "
            + "-fx-effect: dropshadow(gaussian, rgba(80, 32, 122, 0.2), 16, 0, 0, 4); "
            + "-fx-cursor: hand;"
        );

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + gradFrom + ", " + gradTo + "); "
            + "-fx-background-radius: 16; -fx-border-radius: 16; "
            + "-fx-border-color: " + borderClr + "; -fx-border-width: 1; "
            + "-fx-effect: dropshadow(gaussian, rgba(80, 32, 122, 0.4), 22, 0, 0, 6); "
            + "-fx-cursor: hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + gradFrom + ", " + gradTo + "); "
            + "-fx-background-radius: 16; -fx-border-radius: 16; "
            + "-fx-border-color: " + borderClr + "; -fx-border-width: 1; "
            + "-fx-effect: dropshadow(gaussian, rgba(80, 32, 122, 0.2), 16, 0, 0, 4); "
            + "-fx-cursor: hand;"
        ));

        Label icon = new Label(emoji);
        icon.setStyle("-fx-font-size: 26px; -fx-text-fill: #D6B9FC;");

        valueLabel.getStyleClass().add("stat-value");

        Label desc = new Label(label);
        desc.getStyleClass().add("stat-label");

        card.getChildren().addAll(icon, valueLabel, desc);
        return card;
    }

    /**
     * Creates the welcome banner.
     */
    private VBox createWelcomeBanner() {
        VBox banner = new VBox(10);
        banner.getStyleClass().add("welcome-banner");
        banner.setPadding(new Insets(28));

        Label bannerTitle = new Label("WELCOME TO HOTELFLOW");
        bannerTitle.getStyleClass().add("banner-title");

        Label bannerText = new Label(
            "Manage your rooms, customers, and bookings seamlessly from one place. "
            + "Use the sidebar to navigate between sections. "
            + "All data is stored in-memory for this session."
        );
        bannerText.getStyleClass().add("banner-text");
        bannerText.setWrapText(true);

        banner.getChildren().addAll(bannerTitle, bannerText);
        return banner;
    }

    /**
     * Refreshes all dashboard statistics.
     */
    public void refreshDashboard() {
        if (totalRoomsValue == null) return;
        totalRoomsValue.setText(String.valueOf(roomManager.getTotalRooms()));
        availableRoomsValue.setText(String.valueOf(roomManager.getAvailableCount()));
        occupiedRoomsValue.setText(String.valueOf(roomManager.getOccupiedCount()));
        totalCustomersValue.setText(String.valueOf(customerManager.getTotalCustomers()));
        activeBookingsValue.setText(String.valueOf(bookingManager.getActiveBookingCount()));
        totalRevenueValue.setText("₹" + String.format("%,.0f", bookingManager.getTotalRevenue()));
    }

    /**
     * Fade-in + slide-up entrance animation.
     */
    private void animateEntrance(GridPane grid) {
        grid.setOpacity(0);
        grid.setTranslateY(25);

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(grid.opacityProperty(), 0),
                new KeyValue(grid.translateYProperty(), 25)
            ),
            new KeyFrame(Duration.millis(500),
                new KeyValue(grid.opacityProperty(), 1, Interpolator.EASE_OUT),
                new KeyValue(grid.translateYProperty(), 0, Interpolator.EASE_OUT)
            )
        );
        timeline.play();
    }
}
