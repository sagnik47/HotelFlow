package com.hotelflow.manager;

import com.hotelflow.model.Booking;
import com.hotelflow.model.Customer;
import com.hotelflow.model.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * BookingManager handles all booking operations.
 * Coordinates between RoomManager and CustomerManager to ensure
 * data consistency (e.g., marking rooms as occupied on booking).
 */
public class BookingManager {

    // HashMap for O(1) lookup by booking ID
    private final Map<String, Booking> bookingMap = new HashMap<>();

    // ObservableList for automatic TableView updates
    private final ObservableList<Booking> bookingList = FXCollections.observableArrayList();

    // References to other managers for cross-cutting operations
    private final RoomManager roomManager;
    private final CustomerManager customerManager;

    // Auto-incrementing booking counter
    private int bookingCounter = 1000;

    /**
     * Constructs a BookingManager with references to the room and customer managers.
     *
     * @param roomManager     The room manager for availability checks
     * @param customerManager The customer manager for customer lookups
     */
    public BookingManager(RoomManager roomManager, CustomerManager customerManager) {
        this.roomManager = roomManager;
        this.customerManager = customerManager;
    }

    /**
     * Creates a new booking after validating room availability and customer existence.
     *
     * @param customerId  The ID of the customer making the booking
     * @param roomNumber  The room number to book
     * @param checkIn     Check-in date
     * @param checkOut    Check-out date
     * @return The booking ID if successful, or an error message starting with "ERROR:"
     */
    public String createBooking(String customerId, int roomNumber,
                                LocalDate checkIn, LocalDate checkOut) {
        // Validate customer exists
        Customer customer = customerManager.getCustomer(customerId);
        if (customer == null) {
            return "ERROR: Customer not found.";
        }

        // Validate room exists
        Room room = roomManager.getRoom(roomNumber);
        if (room == null) {
            return "ERROR: Room not found.";
        }

        // Validate room availability
        if (!room.isAvailable()) {
            return "ERROR: Room " + roomNumber + " is currently occupied.";
        }

        // Validate dates
        if (checkIn == null || checkOut == null) {
            return "ERROR: Please select valid dates.";
        }
        if (!checkOut.isAfter(checkIn)) {
            return "ERROR: Check-out date must be after check-in date.";
        }

        // Generate unique booking ID
        bookingCounter++;
        String bookingId = "BK" + bookingCounter;

        // Create booking and mark room as occupied
        Booking booking = new Booking(bookingId, customer, room, checkIn, checkOut);
        bookingMap.put(bookingId, booking);
        bookingList.add(booking);
        roomManager.updateAvailability(roomNumber, false);

        return bookingId;
    }

    /**
     * Checks out a booking by freeing the associated room.
     *
     * @param bookingId The booking ID to check out
     * @return true if checkout was successful, false otherwise
     */
    public boolean checkout(String bookingId) {
        Booking booking = bookingMap.get(bookingId);
        if (booking == null || !booking.isActive()) {
            return false;
        }

        // Free the room and deactivate the booking
        roomManager.updateAvailability(booking.getRoomNumber(), true);
        booking.setActive(false);
        return true;
    }

    /**
     * Returns the observable list of all bookings for UI binding.
     */
    public ObservableList<Booking> getBookingList() {
        return bookingList;
    }

    /**
     * Returns the count of currently active bookings.
     */
    public int getActiveBookingCount() {
        return (int) bookingList.stream().filter(Booking::isActive).count();
    }

    /**
     * Returns total revenue from all bookings.
     */
    public double getTotalRevenue() {
        return bookingList.stream().mapToDouble(Booking::getTotalCost).sum();
    }
}
