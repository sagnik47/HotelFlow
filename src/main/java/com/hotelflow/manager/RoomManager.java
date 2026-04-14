package com.hotelflow.manager;

import com.hotelflow.model.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

/**
 * RoomManager handles all room-related operations.
 * Uses in-memory storage via HashMap and exposes an ObservableList for UI binding.
 */
public class RoomManager {

    // HashMap for O(1) lookup by room number
    private final Map<Integer, Room> roomMap = new HashMap<>();

    // ObservableList for automatic TableView updates
    private final ObservableList<Room> roomList = FXCollections.observableArrayList();

    public RoomManager() {
        // Seed with sample rooms for demonstration
        addRoom(new Room(101, "Single", 2500.00, true));
        addRoom(new Room(102, "Single", 2500.00, true));
        addRoom(new Room(201, "Double", 4000.00, true));
        addRoom(new Room(202, "Double", 4000.00, true));
        addRoom(new Room(301, "Suite", 7500.00, true));
        addRoom(new Room(302, "Suite", 7500.00, true));
        addRoom(new Room(401, "Deluxe", 12000.00, true));
        addRoom(new Room(402, "Deluxe", 12000.00, true));
    }

    /**
     * Adds a new room if the room number doesn't already exist.
     *
     * @param room The room to add
     * @return true if added successfully, false if room number already exists
     */
    public boolean addRoom(Room room) {
        if (roomMap.containsKey(room.getRoomNumber())) {
            return false; // Duplicate room number
        }
        roomMap.put(room.getRoomNumber(), room);
        roomList.add(room);
        return true;
    }

    /**
     * Retrieves a room by its room number.
     *
     * @param roomNumber The room number to lookup
     * @return The Room object, or null if not found
     */
    public Room getRoom(int roomNumber) {
        return roomMap.get(roomNumber);
    }

    /**
     * Updates the availability status of a room.
     *
     * @param roomNumber The room number to update
     * @param available  The new availability status
     */
    public void updateAvailability(int roomNumber, boolean available) {
        Room room = roomMap.get(roomNumber);
        if (room != null) {
            room.setAvailable(available);
        }
    }

    /**
     * Returns the observable list of all rooms for UI binding.
     */
    public ObservableList<Room> getRoomList() {
        return roomList;
    }

    /**
     * Returns a list of only available rooms.
     */
    public ObservableList<Room> getAvailableRooms() {
        ObservableList<Room> available = FXCollections.observableArrayList();
        for (Room room : roomList) {
            if (room.isAvailable()) {
                available.add(room);
            }
        }
        return available;
    }

    // --- Dashboard Statistics ---

    public int getTotalRooms() { return roomList.size(); }

    public int getAvailableCount() {
        return (int) roomList.stream().filter(Room::isAvailable).count();
    }

    public int getOccupiedCount() {
        return (int) roomList.stream().filter(r -> !r.isAvailable()).count();
    }

    public double getTotalRevenuePotential() {
        return roomList.stream().mapToDouble(Room::getPricePerNight).sum();
    }
}
