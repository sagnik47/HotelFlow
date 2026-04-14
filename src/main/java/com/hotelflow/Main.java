package com.hotelflow;

/**
 * Main launcher class required for packaging JavaFX applications into Fat JARs.
 * If you run a class that extends Application directly from a JAR in Java 11+,
 * it will throw a JavaFX module missing error. This wrapper bypasses it.
 */
public class Main {
    public static void main(String[] args) {
        HotelFlowApp.main(args);
    }
}
