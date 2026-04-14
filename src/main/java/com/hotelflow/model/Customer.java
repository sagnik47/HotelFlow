package com.hotelflow.model;

import javafx.beans.property.*;

/**
 * Customer model class representing a hotel guest.
 * Uses JavaFX properties for seamless TableView binding.
 */
public class Customer {

    private final StringProperty customerId;
    private final StringProperty name;
    private final StringProperty phone;

    /**
     * Constructs a new Customer with the given details.
     *
     * @param customerId Unique customer ID (e.g., passport, govt ID)
     * @param name       Full name of the customer
     * @param phone      Contact phone number
     */
    public Customer(String customerId, String name, String phone) {
        this.customerId = new SimpleStringProperty(customerId);
        this.name = new SimpleStringProperty(name);
        this.phone = new SimpleStringProperty(phone);
    }

    // --- Customer ID ---
    public String getCustomerId() { return customerId.get(); }
    public void setCustomerId(String customerId) { this.customerId.set(customerId); }
    public StringProperty customerIdProperty() { return customerId; }

    // --- Name ---
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    // --- Phone ---
    public String getPhone() { return phone.get(); }
    public void setPhone(String phone) { this.phone.set(phone); }
    public StringProperty phoneProperty() { return phone; }

    @Override
    public String toString() {
        return getName() + " (" + getCustomerId() + ")";
    }
}
