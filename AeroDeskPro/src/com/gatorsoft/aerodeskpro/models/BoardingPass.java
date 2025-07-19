package com.gatorsoft.aerodeskpro.models;

import com.gatorsoft.aerodeskpro.models.Booking;
import java.time.LocalDateTime;

public class BoardingPass {

    private String bookingReference;
    private int flightId;
    private int passengerId;
    private String passengerName;
    private String seatNumber;
    private LocalDateTime boardingTime;
    private String gate;
    private String flightDetails;

    // Constructor to generate Boarding Pass from Booking
    public BoardingPass(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }

        this.bookingReference = booking.getBookingReference();
        this.flightId = booking.getFlightId();
        this.passengerId = booking.getPassengerId();
        this.seatNumber = booking.getSeatNumber(); // Assumes the Booking class has a seatNumber field
        this.boardingTime = LocalDateTime.now(); // Just using current time as boarding time
        this.gate = "A1"; // Default gate (you can replace with actual gate info)
        this.passengerName = "John Doe"; // Placeholder, you can replace with actual passenger name
        this.flightDetails = "Flight details for flight ID: " + flightId; // Placeholder details
    }

    // Getters and Setters
    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public LocalDateTime getBoardingTime() {
        return boardingTime;
    }

    public void setBoardingTime(LocalDateTime boardingTime) {
        this.boardingTime = boardingTime;
    }

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }

    public String getFlightDetails() {
        return flightDetails;
    }

    public void setFlightDetails(String flightDetails) {
        this.flightDetails = flightDetails;
    }

    // Method to display boarding pass details
    public String displayBoardingPass() {
        return "Boarding Pass: \n" +
                "Booking Reference: " + bookingReference + "\n" +
                "Passenger Name: " + passengerName + "\n" +
                "Flight ID: " + flightId + "\n" +
                "Seat: " + seatNumber + "\n" +
                "Gate: " + gate + "\n" +
                "Boarding Time: " + boardingTime + "\n" +
                "Flight Details: " + flightDetails;
    }
}
