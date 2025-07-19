/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gatorsoft.aerodeskpro.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.gatorsoft.aerodeskpro.database.DatabaseConnection;
import com.gatorsoft.aerodeskpro.entity.BookingStatus;
import com.gatorsoft.aerodeskpro.models.Booking;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException;

public class BookingDAO {

    private static final Logger LOGGER = Logger.getLogger(BookingDAO.class.getName());

    // SQL Queries
    private static final String SELECT_BOOKING_BY_ID = "SELECT * FROM bookings WHERE booking_id = ?";
    private static final String SELECT_BOOKING_BY_REFERENCE = "SELECT * FROM bookings WHERE booking_reference = ?";
    private static final String SELECT_BOOKINGS_BY_PASSENGER = "SELECT * FROM bookings WHERE passenger_id = ?";
    private static final String SELECT_BOOKINGS_BY_FLIGHT = "SELECT * FROM bookings WHERE flight_id = ?";
    private static final String SELECT_BOOKINGS_BY_STATUS = "SELECT * FROM bookings WHERE status = ?";
    private static final String INSERT_BOOKING = "INSERT INTO bookings (booking_reference, flight_id, passenger_id, status, created_at) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_BOOKING = "UPDATE bookings SET booking_reference = ?, flight_id = ?, passenger_id = ?, status = ?, updated_at = ? WHERE booking_id = ?";
    private static final String DELETE_BOOKING = "DELETE FROM bookings WHERE booking_id = ?";
    private static final String SELECT_CHECKED_IN_BOOKINGS = "SELECT * FROM bookings WHERE flight_id = ? AND status = 'CHECKED_IN'";
    private static final String SELECT_PASSENGER_COUNT = "SELECT COUNT(*) FROM bookings WHERE flight_id = ?";

    /**
     * Finds a booking by its ID.
     */
    public Booking findById(int bookingId) throws AeroDeskException {
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(SELECT_BOOKING_BY_ID)) {

            statement.setInt(1, bookingId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToBooking(resultSet);
                } else {
                    return null; // No booking found
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding booking by ID: " + bookingId, e);
            throw new AeroDeskException("Failed to find booking by ID", e);
        }
    }

    /**
     * Finds a booking by its booking reference.
     */
    public Booking findByReference(String bookingReference) throws AeroDeskException {
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(SELECT_BOOKING_BY_REFERENCE)) {

            statement.setString(1, bookingReference);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToBooking(resultSet);
                } else {
                    return null; // No booking found
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding booking by reference: " + bookingReference, e);
            throw new AeroDeskException("Failed to find booking by reference", e);
        }
    }

    /**
     * Finds all bookings by passenger ID.
     */
    public List<Booking> findByPassenger(int passengerId) throws AeroDeskException {
        List<Booking> bookings = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(SELECT_BOOKINGS_BY_PASSENGER)) {

            statement.setInt(1, passengerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bookings.add(mapResultSetToBooking(resultSet));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding bookings by passenger ID: " + passengerId, e);
            throw new AeroDeskException("Failed to find bookings by passenger ID", e);
        }
        return bookings;
    }

    /**
     * Finds all bookings by flight ID.
     */
    public List<Booking> findByFlight(int flightId) throws AeroDeskException {
        List<Booking> bookings = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(SELECT_BOOKINGS_BY_FLIGHT)) {

            statement.setInt(1, flightId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bookings.add(mapResultSetToBooking(resultSet));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding bookings by flight ID: " + flightId, e);
            throw new AeroDeskException("Failed to find bookings by flight ID", e);
        }
        return bookings;
    }

    /**
     * Finds all bookings by status.
     */
    public List<Booking> findByStatus(BookingStatus status) throws AeroDeskException {
        List<Booking> bookings = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(SELECT_BOOKINGS_BY_STATUS)) {

            statement.setString(1, status.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bookings.add(mapResultSetToBooking(resultSet));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding bookings by status: " + status, e);
            throw new AeroDeskException("Failed to find bookings by status", e);
        }
        return bookings;
    }

    /**
     * Saves a new booking to the database.
     */
    public Booking save(Booking booking) throws AeroDeskException {
        // Validate booking object
        if (booking == null || !booking.isValid()) {
            throw new AeroDeskException("Invalid booking data", AeroDeskException.ErrorCategory.DATABASE_ERROR);
        }

        // Ensure booking status is not null
        if (booking.getBookingStatus() == null) {
            booking.setBookingStatus(BookingStatus.CONFIRMED); // Default to "CONFIRMED"
        }

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(INSERT_BOOKING, Statement.RETURN_GENERATED_KEYS)) {

            // Debug logs
            System.out.println("Booking Reference: " + booking.getBookingReference());
            System.out.println("Flight ID: " + booking.getFlightId());
            System.out.println("Passenger ID: " + booking.getPassengerId());
            System.out.println("Booking Status: " + booking.getBookingStatus()); // Enum value

            // Ensure status is properly set
            String statusString = (booking.getStatus() != null) ? booking.getStatus() : BookingStatus.CONFIRMED.name();
            System.out.println("Setting Status: " + statusString);

            // Set parameters
            statement.setString(1, booking.getBookingReference());
            statement.setInt(2, booking.getFlightId());
            statement.setInt(3, booking.getPassengerId());
            statement.setString(4, statusString); // Convert status to string
            statement.setTimestamp(5, Timestamp.valueOf(booking.getCreatedAt()));

            // Execute and handle the result
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        booking.setBookingId(generatedKeys.getInt(1));
                    }
                }
                return booking;
            } else {
                return null; // No rows affected
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving booking: SQLState: " + e.getSQLState() + ", ErrorCode: " + e.getErrorCode(), e);
            throw new AeroDeskException("Failed to save booking", e);
        }
    }

    /**
     * Updates an existing booking.
     */
    public Booking update(Booking booking) throws AeroDeskException {
        if (booking == null || booking.getBookingId() <= 0 || !booking.isValid()) {
            throw new AeroDeskException("Invalid booking data for update", AeroDeskException.ErrorCategory.DATABASE_ERROR);
        }

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(UPDATE_BOOKING)) {

            statement.setString(1, booking.getBookingReference());
            statement.setInt(2, booking.getFlightId());
            statement.setInt(3, booking.getPassengerId());
            // Ensure status is properly set
            String statusString = (booking.getStatus() != null) ? booking.getStatus() : BookingStatus.CONFIRMED.name();
            System.out.println("Setting Status: " + statusString);
            statement.setTimestamp(5, Timestamp.valueOf(booking.getUpdatedAt()));
            statement.setInt(6, booking.getBookingId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return booking;
            } else {
                return null;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating booking", e);
            throw new AeroDeskException("Failed to update booking", e);
        }
    }

    /**
     * Deletes a booking from the database.
     */
    public boolean delete(int bookingId) throws AeroDeskException {
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(DELETE_BOOKING)) {

            statement.setInt(1, bookingId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting booking", e);
            throw new AeroDeskException("Failed to delete booking", e);
        }
    }

    /**
     * Finds all checked-in bookings for a flight.
     */
    public List<Booking> findCheckedInBookings(int flightId) throws AeroDeskException {
        List<Booking> bookings = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(SELECT_CHECKED_IN_BOOKINGS)) {

            statement.setInt(1, flightId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bookings.add(mapResultSetToBooking(resultSet));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding checked-in bookings for flight: " + flightId, e);
            throw new AeroDeskException("Failed to find checked-in bookings", e);
        }
        return bookings;
    }

    /**
     * Gets the count of passengers for a given flight.
     */
    public int getPassengerCount(int flightId) throws AeroDeskException {
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(SELECT_PASSENGER_COUNT)) {

            statement.setInt(1, flightId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting passenger count for flight: " + flightId, e);
            throw new AeroDeskException("Failed to get passenger count", e);
        }
        return 0;
    }

    /**
     * Helper method to map a ResultSet to a Booking object.
     */
    private Booking mapResultSetToBooking(ResultSet resultSet) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(resultSet.getInt("booking_id"));
        booking.setBookingReference(resultSet.getString("booking_reference"));
        booking.setFlightId(resultSet.getInt("flight_id"));
        booking.setPassengerId(resultSet.getInt("passenger_id"));
        // Ensure status is properly set
        String statusString = (booking.getStatus() != null) ? booking.getStatus() : BookingStatus.CONFIRMED.name();
        System.out.println("Setting Status: " + statusString);
        booking.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        booking.setUpdatedAt(resultSet.getTimestamp("updated_at") != null ? resultSet.getTimestamp("updated_at").toLocalDateTime() : null);
        return booking;
    }
}
