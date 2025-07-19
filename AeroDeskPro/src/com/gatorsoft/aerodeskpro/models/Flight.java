package com.gatorsoft.aerodeskpro.models;

import java.time.LocalDateTime;
import java.util.Objects;

import com.gatorsoft.aerodeskpro.entity.FlightStatus;

/**
 * Flight entity representing a flight record in the airport management system.
 * This class encapsulates all flight-related data and provides validation.
 */
public class Flight {

    // Primary key
    private int flightId;

    // Flight details
    private String flightNumber;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String origin;
    private String destination;
    private int gateNumber;
    private FlightStatus status;
    private int aircraftType;
    private int capacity;
    private LocalDateTime createdAt;

    // Constructors
    public Flight() {
        this.createdAt = LocalDateTime.now();
        this.status = FlightStatus.SCHEDULED;
    }

    public Flight(String flightNumber, LocalDateTime departureTime,
            LocalDateTime arrivalTime, String origin, String destination,
            int aircraftType, int capacity) {
        this();
        this.flightNumber = flightNumber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.origin = origin;
        this.destination = destination;
        this.aircraftType = aircraftType;
        this.capacity = capacity;
    }

    // Getters and Setters
    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        if (flightNumber == null || flightNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Flight number cannot be null or empty");
        }
        this.flightNumber = flightNumber.trim().toUpperCase();
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getGateNumber() {
        return gateNumber;
    }

    public int setGateNumber(int gateNumber) {
        this.gateNumber = gateNumber;
        return gateNumber;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public int getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(int aircraftType) {
        this.aircraftType = aircraftType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.capacity = capacity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Business logic methods
    public boolean isActive() {
        return status != FlightStatus.CANCELLED && status != FlightStatus.COMPLETED;
    }

    public boolean canBeModified() {
        return status == FlightStatus.SCHEDULED || status == FlightStatus.DELAYED;
    }

    public boolean requiresGate() {
        return status == FlightStatus.BOARDING || status == FlightStatus.SCHEDULED;
    }

    public long getDurationMinutes() {
        if (departureTime != null && arrivalTime != null) {
            return java.time.Duration.between(departureTime, arrivalTime).toMinutes();
        }
        return 0;
    }

public boolean isValid() {
    return isFlightNumberValid() && isTimeValid() && isLocationValid() && isCapacityValid();
}

private boolean isFlightNumberValid() {
    return flightNumber != null && !flightNumber.trim().isEmpty();
}

private boolean isTimeValid() {
    return departureTime != null && arrivalTime != null;
}

private boolean isLocationValid() {
    return origin != null && destination != null;
}

private boolean isCapacityValid() {
    return capacity > 0;
}



    @Override
    public String toString() {
        return String.format(
                "Flight{id=%d, number='%s', from='%s', to='%s', status=%s, gate='%s'}",
                flightId, flightNumber, origin, destination, status, gateNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Flight flight = (Flight) o;
        return flightId == flight.flightId
                && Objects.equals(flightNumber, flight.flightNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightId, flightNumber);
    }
}

/**
 * Enumeration representing the possible states of a flight
 */
