package com.hotelflow.model;

import javafx.beans.property.*;

/**
 * Room model class representing a hotel room.
 * Uses JavaFX properties for seamless TableView binding.
 */
public class Room {

    private final IntegerProperty roomNumber;
    private final StringProperty roomType;
    private final DoubleProperty pricePerNight;
    private final BooleanProperty available;

    /**
     * Constructs a new Room with the given details.
     *
     * @param roomNumber    Unique room number
     * @param roomType      Type of room (Single, Double, Suite, Deluxe)
     * @param pricePerNight Nightly rate for the room
     * @param available     Whether the room is currently available
     */
    public Room(int roomNumber, String roomType, double pricePerNight, boolean available) {
        this.roomNumber = new SimpleIntegerProperty(roomNumber);
        this.roomType = new SimpleStringProperty(roomType);
        this.pricePerNight = new SimpleDoubleProperty(pricePerNight);
        this.available = new SimpleBooleanProperty(available);
    }

    // --- Room Number ---
    public int getRoomNumber() { return roomNumber.get(); }
    public void setRoomNumber(int roomNumber) { this.roomNumber.set(roomNumber); }
    public IntegerProperty roomNumberProperty() { return roomNumber; }

    // --- Room Type ---
    public String getRoomType() { return roomType.get(); }
    public void setRoomType(String roomType) { this.roomType.set(roomType); }
    public StringProperty roomTypeProperty() { return roomType; }

    // --- Price Per Night ---
    public double getPricePerNight() { return pricePerNight.get(); }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight.set(pricePerNight); }
    public DoubleProperty pricePerNightProperty() { return pricePerNight; }

    // --- Availability ---
    public boolean isAvailable() { return available.get(); }
    public void setAvailable(boolean available) { this.available.set(available); }
    public BooleanProperty availableProperty() { return available; }

    @Override
    public String toString() {
        return "Room " + getRoomNumber() + " (" + getRoomType() + ")";
    }
}
