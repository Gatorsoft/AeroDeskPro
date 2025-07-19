/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gatorsoft.aerodeskpro.dao;
// ========================================

// FILE 2: FlightDAO.java (Data Access Object)
// Package: com.aerodesk.pro.dao
// ========================================
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gatorsoft.aerodeskpro.entity.FlightStatus;
import com.gatorsoft.aerodeskpro.database.DatabaseConnection;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException.ErrorCategory;
import com.gatorsoft.aerodeskpro.models.Flight;

/**
 * Data Access Object for Flight entities. Handles all database operations for
 * flights including CRUD operations.
 */
public class FlightDAO {

    private static final Logger LOGGER = Logger.getLogger(FlightDAO.class.getName());

    // SQL Queries
    private static final String INSERT_FLIGHT = "INSERT INTO flights (flight_number, departure_time, arrival_time, origin, destination, "
            + "gate_number, status, aircraft_type, capacity, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_FLIGHTS = "SELECT flight_id, flight_number, departure_time, arrival_time, origin, destination, "
            + "gate_number, status, aircraft_type, capacity, created_at FROM flights ORDER BY departure_time";

    private static final String SELECT_FLIGHT_BY_ID = "SELECT flight_id, flight_number, departure_time, arrival_time, origin, destination, "
            + "gate_number, status, aircraft_type, capacity, created_at FROM flights WHERE flight_id = ?";

    private static final String SELECT_FLIGHT_BY_NUMBER = "SELECT flight_id, flight_number, departure_time, arrival_time, origin, destination, "
            + "gate_number, status, aircraft_type, capacity, created_at FROM flights WHERE flight_number = ?";

    private static final String UPDATE_FLIGHT = "UPDATE flights SET flight_number = ?, departure_time = ?, arrival_time = ?, origin = ?, "
            + "destination = ?, gate_number = ?, status = ?, aircraft_type = ?, capacity = ? WHERE flight_id = ?";

    private static final String UPDATE_FLIGHT_STATUS = "UPDATE flights SET status = ? WHERE flight_id = ?";

    private static final String UPDATE_FLIGHT_GATE = "UPDATE flights SET gate_number = ? WHERE flight_id = ?";

    private static final String DELETE_FLIGHT = "DELETE FROM flights WHERE flight_id = ?";

    private static final String SELECT_FLIGHTS_BY_DATE_RANGE = "SELECT flight_id, flight_number, departure_time, arrival_time, origin, destination, "
            + "gate_number, status, aircraft_type, capacity, created_at FROM flights "
            + "WHERE departure_time BETWEEN ? AND ? ORDER BY departure_time";

    private static final String SELECT_FLIGHTS_BY_STATUS = "SELECT flight_id, flight_number, departure_time, arrival_time, origin, destination, "
            + "gate_number, status, aircraft_type, capacity, created_at FROM flights "
            + "WHERE status = ? ORDER BY departure_time";

    /**
     * Inserts a new flight into the database
     */
    public boolean insertFlight(Flight flight) throws AeroDeskException {
        if (flight == null || !flight.isValid()) {
            throw new AeroDeskException("Invalid flight data",
                    ErrorCategory.DATABASE_ERROR);
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(INSERT_FLIGHT,
                    Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, flight.getFlightNumber());
            statement.setTimestamp(2, Timestamp.valueOf(flight.getDepartureTime()));
            statement.setTimestamp(3, Timestamp.valueOf(flight.getArrivalTime()));
            statement.setString(4, flight.getOrigin());
            statement.setString(5, flight.getDestination());
            statement.setInt(6, flight.getGateNumber());
            statement.setString(7, flight.getStatus().name());
            statement.setInt(8, flight.getAircraftType());
            statement.setInt(9, flight.getCapacity());
            statement.setTimestamp(10, Timestamp.valueOf(flight.getCreatedAt()));

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    flight.setFlightId(generatedKeys.getInt(1));
                }
                LOGGER.info("Flight inserted successfully: " + flight.getFlightNumber());
                return true;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "Error inserting flight: " + flight.getFlightNumber(), e);
            throw new AeroDeskException("Failed to insert flight", e);
        } finally {
            closeResources(generatedKeys, statement, connection);
        }

        return false;
    }

    /**
     * Retrieves all flights from the database
     */
    public List<Flight> getAllFlights() throws AeroDeskException {
        List<Flight> flights = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(SELECT_ALL_FLIGHTS);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                flights.add(mapResultSetToFlight(resultSet));
            }

            LOGGER.info("Retrieved " + flights.size() + " flights from database");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all flights", e);
            throw new AeroDeskException("Failed to retrieve flights", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return flights;
    }

    /**
     * Finds a flight by its ID
     * @param flightId
     * @return 
     * @throws com.gatorsoft.aerodeskpro.exceptions.AeroDeskException
     */
    public Flight getFlightById(int flightId) throws AeroDeskException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(SELECT_FLIGHT_BY_ID);
            statement.setInt(1, flightId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Flight flight = mapResultSetToFlight(resultSet);
                LOGGER.info("Flight found: " + flight.getFlightNumber());
                return flight;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding flight by ID: " + flightId, e);
            throw new AeroDeskException("Failed to find flight by ID", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return null;
    }

    /**
     * Finds a flight by its flight number
     */
    public Flight getFlightByNumber(String flightNumber) throws AeroDeskException {
        if (flightNumber == null || flightNumber.trim().isEmpty()) {
            throw new AeroDeskException("Flight number cannot be null or empty",
                    ErrorCategory.DATABASE_ERROR);
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(SELECT_FLIGHT_BY_NUMBER);
            statement.setString(1, flightNumber.trim().toUpperCase());
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Flight flight = mapResultSetToFlight(resultSet);
                LOGGER.info("Flight found by number: " + flight.getFlightNumber());
                return flight;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding flight by number: " + flightNumber,
                    e);
            throw new AeroDeskException("Failed to find flight by number", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return null;
    }

    /**
     * Updates an existing flight
     */
    public boolean updateFlight(Flight flight) throws AeroDeskException {
        if (flight == null || !flight.isValid() || flight.getFlightId() <= 0) {
            throw new AeroDeskException("Invalid flight data for update",
                    ErrorCategory.RESOURCE_NOT_FOUND);
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(UPDATE_FLIGHT);

            statement.setString(1, flight.getFlightNumber());
            statement.setTimestamp(2, Timestamp.valueOf(flight.getDepartureTime()));
            statement.setTimestamp(3, Timestamp.valueOf(flight.getArrivalTime()));
            statement.setString(4, flight.getOrigin());
            statement.setString(5, flight.getDestination());
            statement.setInt(6, flight.getGateNumber());
            statement.setString(7, flight.getStatus().name());
            statement.setInt(8, flight.getAircraftType());
            statement.setInt(9, flight.getCapacity());
            statement.setInt(10, flight.getFlightId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Flight updated successfully: " + flight.getFlightNumber());
                return true;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating flight: " + flight.getFlightNumber(),
                    e);
            throw new AeroDeskException("Failed to update flight", e);
        } finally {
            closeResources(null, statement, connection);
        }

        return false;
    }

    /**
     * Updates only the status of a flight
     */
    public boolean updateFlightStatus(int flightId, FlightStatus status)
            throws AeroDeskException {
        if (status == null) {
            throw new AeroDeskException("Flight status cannot be null",
                    ErrorCategory.DATABASE_ERROR);
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(UPDATE_FLIGHT_STATUS);
            statement.setString(1, status.name());
            statement.setInt(2, flightId);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Flight status updated to " + status + " for flight ID: "
                        + flightId);
                return true;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating flight status for ID: " + flightId,
                    e);
            throw new AeroDeskException("Failed to update flight status", e);
        } finally {
            closeResources(null, statement, connection);
        }

        return false;
    }

    /**
     * Updates the gate assignment for a flight
     */
    public boolean updateFlightGate(int flightId, String gateNumber)
            throws AeroDeskException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(UPDATE_FLIGHT_GATE);
            statement.setString(1, gateNumber);
            statement.setInt(2, flightId);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info(
                        "Gate updated to " + gateNumber + " for flight ID: " + flightId);
                return true;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating flight gate for ID: " + flightId, e);
            throw new AeroDeskException("Failed to update flight gate", e);
        } finally {
            closeResources(null, statement, connection);
        }

        return false;
    }

    /**
     * Deletes a flight from the database
     */
    public boolean deleteFlight(int flightId) throws AeroDeskException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(DELETE_FLIGHT);
            statement.setInt(1, flightId);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Flight deleted successfully with ID: " + flightId);
                return true;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting flight with ID: " + flightId, e);
            throw new AeroDeskException("Failed to delete flight", e);
        } finally {
            closeResources(null, statement, connection);
        }

        return false;
    }

    /**
     * Retrieves flights within a date range
     */
    public List<Flight> getFlightsByDateRange(LocalDateTime startDate,
            LocalDateTime endDate) throws AeroDeskException {
        List<Flight> flights = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(SELECT_FLIGHTS_BY_DATE_RANGE);
            statement.setTimestamp(1, Timestamp.valueOf(startDate));
            statement.setTimestamp(2, Timestamp.valueOf(endDate));
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                flights.add(mapResultSetToFlight(resultSet));
            }

            LOGGER.info("Retrieved " + flights.size() + " flights for date range");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving flights by date range", e);
            throw new AeroDeskException("Failed to retrieve flights by date range", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return flights;
    }

    /**
     * Retrieves flights by status
     */
    public List<Flight> getFlightsByStatus(FlightStatus status) throws AeroDeskException {
        List<Flight> flights = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(SELECT_FLIGHTS_BY_STATUS);
            statement.setString(1, status.name());
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                flights.add(mapResultSetToFlight(resultSet));
            }

            LOGGER.info(
                    "Retrieved " + flights.size() + " flights with status: " + status);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving flights by status: " + status, e);
            throw new AeroDeskException("Failed to retrieve flights by status", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return flights;
    }

    /**
     * Maps a ResultSet row to a Flight object
     */
    private Flight mapResultSetToFlight(ResultSet resultSet) throws SQLException {
        Flight flight = new Flight();

        flight.setFlightId(resultSet.getInt("flight_id"));
        flight.setFlightNumber(resultSet.getString("flight_number"));
        flight.setDepartureTime(
                resultSet.getTimestamp("departure_time").toLocalDateTime());
        flight.setArrivalTime(resultSet.getTimestamp("arrival_time").toLocalDateTime());
        flight.setOrigin(resultSet.getString("origin"));
        flight.setDestination(resultSet.getString("destination"));
        flight.setGateNumber(resultSet.getInt("gate_number"));
        flight.setStatus(FlightStatus.valueOf(resultSet.getString("status")));
        flight.setAircraftType(resultSet.getInt("aircraft_type"));
        flight.setCapacity(resultSet.getInt("capacity"));

        Timestamp createdTimestamp = resultSet.getTimestamp("created_at");
        if (createdTimestamp != null) {
            flight.setCreatedAt(createdTimestamp.toLocalDateTime());
        }

        return flight;
    }

    /**
     * Closes database resources safely
     */
    private void closeResources(ResultSet resultSet, PreparedStatement statement,
            Connection connection) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing database resources", e);
        }
    }
}
