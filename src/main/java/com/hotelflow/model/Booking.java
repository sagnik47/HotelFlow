package com.hotelflow.model;

import javafx.beans.property.*;
import java.time.LocalDate;

/**
 * Booking model class representing a room reservation.
 * Links a Customer to a Room with check-in/check-out dates.
 */
public class Booking {

    private final StringProperty bookingId;
    private final StringProperty customerName;
    private final StringProperty customerId;
    private final IntegerProperty roomNumber;
    private final StringProperty roomType;
    private final ObjectProperty<LocalDate> checkInDate;
    private final ObjectProperty<LocalDate> checkOutDate;
    private final DoubleProperty totalCost;
    private final BooleanProperty active;

    /**
     * Constructs a new Booking with the given details.
     *
     * @param bookingId    Unique booking identifier
     * @param customer     The customer who made the booking
     * @param room         The room being booked
     * @param checkInDate  Check-in date
     * @param checkOutDate Check-out date
     */
    public Booking(String bookingId, Customer customer, Room room,
                   LocalDate checkInDate, LocalDate checkOutDate) {
        this.bookingId = new SimpleStringProperty(bookingId);
        this.customerName = new SimpleStringProperty(customer.getName());
        this.customerId = new SimpleStringProperty(customer.getCustomerId());
        this.roomNumber = new SimpleIntegerProperty(room.getRoomNumber());
        this.roomType = new SimpleStringProperty(room.getRoomType());
        this.checkInDate = new SimpleObjectProperty<>(checkInDate);
        this.checkOutDate = new SimpleObjectProperty<>(checkOutDate);
        this.active = new SimpleBooleanProperty(true);

        // Calculate total cost based on number of nights
        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        this.totalCost = new SimpleDoubleProperty(nights * room.getPricePerNight());
    }

    // --- Booking ID ---
    public String getBookingId() { return bookingId.get(); }
    public StringProperty bookingIdProperty() { return bookingId; }

    // --- Customer Name ---
    public String getCustomerName() { return customerName.get(); }
    public StringProperty customerNameProperty() { return customerName; }

    // --- Customer ID ---
    public String getCustomerId() { return customerId.get(); }
    public StringProperty customerIdProperty() { return customerId; }

    // --- Room Number ---
    public int getRoomNumber() { return roomNumber.get(); }
    public IntegerProperty roomNumberProperty() { return roomNumber; }

    // --- Room Type ---
    public String getRoomType() { return roomType.get(); }
    public StringProperty roomTypeProperty() { return roomType; }

    // --- Check-in Date ---
    public LocalDate getCheckInDate() { return checkInDate.get(); }
    public ObjectProperty<LocalDate> checkInDateProperty() { return checkInDate; }

    // --- Check-out Date ---
    public LocalDate getCheckOutDate() { return checkOutDate.get(); }
    public ObjectProperty<LocalDate> checkOutDateProperty() { return checkOutDate; }

    // --- Total Cost ---
    public double getTotalCost() { return totalCost.get(); }
    public DoubleProperty totalCostProperty() { return totalCost; }

    // --- Active Status ---
    public boolean isActive() { return active.get(); }
    public void setActive(boolean active) { this.active.set(active); }
    public BooleanProperty activeProperty() { return active; }

    @Override
    public String toString() {
        return "Booking " + getBookingId() + ": " + getCustomerName()
                + " → Room " + getRoomNumber();
    }
}
