package com.hotelflow.manager;

import com.hotelflow.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

/**
 * CustomerManager handles all customer-related operations.
 * Uses in-memory storage via HashMap and exposes an ObservableList for UI binding.
 */
public class CustomerManager {

    // HashMap for O(1) lookup by customer ID
    private final Map<String, Customer> customerMap = new HashMap<>();

    // ObservableList for automatic TableView updates
    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

    public CustomerManager() {
        // Seed with sample customers for demonstration
        addCustomer(new Customer("ID001", "Arjun Sharma", "9876543210"));
        addCustomer(new Customer("ID002", "Priya Patel", "9123456789"));
        addCustomer(new Customer("ID003", "Rahul Verma", "9988776655"));
    }

    /**
     * Adds a new customer if the customer ID doesn't already exist.
     *
     * @param customer The customer to add
     * @return true if added successfully, false if customer ID already exists
     */
    public boolean addCustomer(Customer customer) {
        if (customerMap.containsKey(customer.getCustomerId())) {
            return false; // Duplicate customer ID
        }
        customerMap.put(customer.getCustomerId(), customer);
        customerList.add(customer);
        return true;
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param customerId The customer ID to lookup
     * @return The Customer object, or null if not found
     */
    public Customer getCustomer(String customerId) {
        return customerMap.get(customerId);
    }

    /**
     * Returns the observable list of all customers for UI binding.
     */
    public ObservableList<Customer> getCustomerList() {
        return customerList;
    }

    /**
     * Returns the total number of registered customers.
     */
    public int getTotalCustomers() {
        return customerList.size();
    }
}
