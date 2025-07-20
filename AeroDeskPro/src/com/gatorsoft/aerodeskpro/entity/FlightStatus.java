package com.gatorsoft.aerodeskpro.entity;

public enum FlightStatus {
    scheduled("Scheduled"),
    delayed("Delayed"),
    boarding("Boarding"),
    departed("Departed"),
    in_flight("In Flight"),
    arrived("Arrived"),
    completed("Completed"),
    cancelled("Cancelled");

    private final String displayName;

    // Constructor with display name
    FlightStatus(String displayName) {
        this.displayName = displayName;
    }

    // Getter for the display name
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
