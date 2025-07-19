package com.gatorsoft.aerodeskpro.services;

import com.gatorsoft.aerodeskpro.dao.BookingDAO;
import com.gatorsoft.aerodeskpro.dao.PassengerDAO;
import com.gatorsoft.aerodeskpro.dao.FlightDAO;
import com.gatorsoft.aerodeskpro.entity.BookingStatus;
import com.gatorsoft.aerodeskpro.models.Booking;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException.ErrorCategory;
import com.gatorsoft.aerodeskpro.models.BoardingPass;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookingService {

    private BookingDAO bookingDAO;
    private PassengerDAO passengerDAO;
    private FlightDAO flightDAO;
    private Logger logger;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.passengerDAO = new PassengerDAO();  // Assuming PassengerDAO exists
        this.flightDAO = new FlightDAO();        // Assuming FlightDAO exists
        this.logger = Logger.getLogger(BookingService.class.getName());
    }

    // Create a new booking
    public Booking createBooking(Booking booking) throws AeroDeskException {
        try {
            validateBooking(booking);  // Validate booking data

            // Save the booking in the database using DAO
            Booking savedBooking = bookingDAO.save(booking);
            if (savedBooking == null) {
                throw new AeroDeskException("Failed to create booking", ErrorCategory.DATABASE_ERROR);
            }

            logger.info("Booking created successfully: " + savedBooking.getBookingReference());
            return savedBooking;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating booking", e);
            throw new AeroDeskException("Failed to create booking", e, ErrorCategory.DATABASE_ERROR);
        }
    }

    // Check-in passenger by booking reference
    public Booking checkInPassenger(String bookingReference, int staffId) throws AeroDeskException {
        try {
            if (bookingReference == null || bookingReference.trim().isEmpty()) {
                throw new AeroDeskException("Booking reference cannot be null or empty", ErrorCategory.VALIDATION_ERROR);
            }

            // Find the booking by reference
            Booking booking = bookingDAO.findByReference(bookingReference);
            if (booking == null) {
                throw new AeroDeskException("Booking not found with reference: " + bookingReference, ErrorCategory.RESOURCE_NOT_FOUND);
            }

            // Update the booking status to CHECKED_IN
            booking.setBookingStatus(BookingStatus.CHECKED_IN);
            bookingDAO.update(booking);

            // Assuming a method to log the check-in staff details, which can be added
            logger.info("Passenger checked in successfully: " + bookingReference + " by staff ID: " + staffId);
            return booking;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking in passenger", e);
            throw new AeroDeskException("Failed to check in passenger", e, ErrorCategory.DATABASE_ERROR);
        }
    }

    // Cancel a booking
    public void cancelBooking(int bookingId, String reason) throws AeroDeskException {
        try {
            // Validate input
            if (reason == null || reason.trim().isEmpty()) {
                throw new AeroDeskException("Cancellation reason cannot be null or empty", ErrorCategory.VALIDATION_ERROR);
            }

            // Find the booking by ID
            Booking booking = bookingDAO.findById(bookingId);
            if (booking == null) {
                throw new AeroDeskException("Booking not found with ID: " + bookingId, ErrorCategory.RESOURCE_NOT_FOUND);
            }

            // Update booking status to CANCELLED
            booking.setBookingStatus(BookingStatus.CANCELLED);
            bookingDAO.update(booking);

            // Optionally, log cancellation reason and any other relevant info
            logger.info("Booking cancelled: " + bookingId + " Reason: " + reason);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error cancelling booking", e);
            throw new AeroDeskException("Failed to cancel booking", e, ErrorCategory.DATABASE_ERROR);
        }
    }

    // Get all bookings for a flight
    public List<Booking> getFlightBookings(int flightId) throws AeroDeskException {
        try {
            List<Booking> bookings = bookingDAO.findByFlight(flightId);
            logger.info("Retrieved " + bookings.size() + " bookings for flight ID: " + flightId);
            return bookings;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving flight bookings", e);
            throw new AeroDeskException("Failed to retrieve bookings for flight", e, ErrorCategory.DATABASE_ERROR);
        }
    }

    // Assign a seat to a booking
    public void assignSeat(int bookingId, String seatNumber) throws AeroDeskException {
        try {
            if (seatNumber == null || seatNumber.trim().isEmpty()) {
                throw new AeroDeskException("Seat number cannot be null or empty", ErrorCategory.VALIDATION_ERROR);
            }

            // Find the booking by ID
            Booking booking = bookingDAO.findById(bookingId);
            if (booking == null) {
                throw new AeroDeskException("Booking not found with ID: " + bookingId, ErrorCategory.RESOURCE_NOT_FOUND);
            }

            // Assuming there's a method to assign the seat in the Booking model
            booking.setSeatNumber(seatNumber); // You might need a method in Booking class to set seat number
            bookingDAO.update(booking);

            logger.info("Seat " + seatNumber + " assigned to booking ID: " + bookingId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error assigning seat", e);
            throw new AeroDeskException("Failed to assign seat", e, ErrorCategory.DATABASE_ERROR);
        }
    }

    // Generate boarding pass for a booking
    public BoardingPass generateBoardingPass(int bookingId) throws AeroDeskException {
        try {
            // Find the booking by ID
            Booking booking = bookingDAO.findById(bookingId);
            if (booking == null) {
                throw new AeroDeskException("Booking not found with ID: " + bookingId, ErrorCategory.RESOURCE_NOT_FOUND);
            }

            // Check if booking is checked-in
            if (booking.getBookingStatus() != BookingStatus.CHECKED_IN) {
                throw new AeroDeskException("Booking must be checked-in before generating a boarding pass", ErrorCategory.VALIDATION_ERROR);
            }

            // Generate the boarding pass (basic example, you can expand it)
            BoardingPass boardingPass = new BoardingPass(booking);
            logger.info("Boarding pass generated for booking ID: " + bookingId);
            return boardingPass;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generating boarding pass", e);
            throw new AeroDeskException("Failed to generate boarding pass", e, ErrorCategory.SYSTEM_ERROR);
        }
    }

    // Validate booking data
    public void validateBooking(Booking booking) throws AeroDeskException {
        if (booking == null) {
            throw new AeroDeskException("Booking cannot be null", ErrorCategory.VALIDATION_ERROR);
        }

        if (booking.getBookingReference() == null || booking.getBookingReference().trim().isEmpty()) {
            throw new AeroDeskException("Booking reference is required", ErrorCategory.VALIDATION_ERROR);
        }

        if (booking.getFlightId() <= 0 || booking.getPassengerId() <= 0) {
            throw new AeroDeskException("Flight ID and Passenger ID must be valid", ErrorCategory.VALIDATION_ERROR);
        }

        // Other validation logic can be added based on your business needs

        logger.info("Booking validated successfully: " + booking.getBookingReference());
    }
}
