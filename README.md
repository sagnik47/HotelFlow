# HotelFlow — Hotel Management System

HotelFlow is a premium, modern desktop application built exclusively with **JavaFX and Java 17+**. Designed without FXML, the application leverages programmatic UI generation and an elegant in-memory MVC (Model-View-Controller) architecture to provide a lightning-fast and visually stunning user experience.

The application utilizes a custom dark-themed palette (`#0B0712`, `#50207A`, `#D6B9FC`, `#838CE5`) with glassmorphism effects and modern typography.

## 🗂️ Codebase Structure

The project strictly follows a custom Model-Manager-View (MVC) architecture separated into distinct, highly modular packages. 

```text
HotelFlow/
├── pom.xml                                   (Maven configuration & JavaFX dependencies)
└── src/
    └── main/
        ├── java/com/hotelflow/
        │   ├── HotelFlowApp.java             (The main entry point, root window, & sidebar)
        │   │
        │   ├── model/                        (Data structures — POJOs)
        │   │   ├── Room.java                 (Room properties: Number, Type, Price, Status)
        │   │   ├── Customer.java             (Customer properties: ID, Name, Phone)
        │   │   └── Booking.java              (Booking properties: Dates, Total Cost, Customer, Room)
        │   │
        │   ├── manager/                      (Controller layer — handles business logic & memory)
        │   │   ├── RoomManager.java          (Stores/fetches rooms)
        │   │   ├── CustomerManager.java      (Stores customer HashMap)
        │   │   └── BookingManager.java       (Handles booking logic, dates, & checkouts)
        │   │
        │   └── ui/                           (View layer — UI definitions with purple palette)
        │       ├── DashboardView.java        (Stat cards & welcome banner)
        │       ├── RoomView.java             (Add new rooms & view available rooms)
        │       ├── CustomerView.java         (Customer data table & add form)
        │       └── BookingView.java          (Date pickers, book actions, & checkout table)
        │
        └── resources/                        (Static assets)
            ├── fonts/
            │   ├── BebasNeue-Regular.ttf     (Title font)
            │   └── Inter-Variable.ttf        (Body text font)
            │
            └── styles/
                └── application.css           (Centralized styling, glassmorphism, & palette vars)
```

## 🛠️ JavaFX Integration & Unique Uses

Unlike traditional JavaFX applications that rely heavily on SceneBuilder and FXML files, HotelFlow was built entirely programmatically. This approach grants ultimate control over styling, rendering layers, and real-time updates.

### 1. Pure Programmatic UI Construction
Every component interfaces natively with Java objects. `VBox`, `HBox`, and `GridPane` layouts are instantiated and populated dynamically inside the `ui/` package. This creates a seamlessly smooth rendering transition when navigating via the sidebar, avoiding the overhead of loading `.fxml` resource graphs at runtime.

### 2. Custom CSS & Glassmorphism (`application.css`)
JavaFX supports W3C CSS syntax with `-fx-` variations. We heavily exploited this to implement modern web-design paradigms:
*   **Linear Gradients & Dropshadows**: Simulating glassmorphism on the dashboard stat cards by mixing variable-opacity hex colors (`rgba()`) and `dropshadow()` gaussian blur effects.
*   **System Fonts Injection**: Custom `.ttf` TrueType fonts are loaded programmatically in `HotelFlowApp.java` (`Font.loadFont()`) and applied globally via root variables in the CSS, entirely replacing the native `System` font.

### 3. Factory Pattern for Complex Overrides (`TableView` & `DatePicker`)
We bypass standard JavaFX rendering skins using cell factories:
*   **`setCellFactory()` on Columns**: Instead of showing raw booleans in the `TableView`, JavaFX dynamically catches boolean updates and returns styled `<Label>` badges (e.g., *Active*, *Checked Out*) or interactive `<Button>` classes (for checkout actions) directly into the table cells.
*   **`setDayCellFactory()` on DatePicker**: To overcome the rigid default styles of the JavaFX Calendar Popup (which ignores regular scene inheritances), we override `updateItem()` inside the cell factory. This forces the UI thread to paint exact font-weights (`900`) and pure black (`#000000`) text dynamically on every cell redraw.

### 4. Interactive Data Binding
The `ComboBox` dropdown menus populate reactively from `ObservableList` collections. When a room is booked, the data state shifts in memory, immediately triggering UI updates across tab contexts to ensure conflicting bookings cannot occur.

## 🚀 How to Run

Requirements: `JDK 17+` and `Maven`.

To compile and launch the application directly from the root directory, simply run:
```bash
mvn clean compile javafx:run
```
