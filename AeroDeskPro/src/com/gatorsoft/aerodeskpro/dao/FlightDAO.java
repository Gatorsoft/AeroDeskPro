
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
 *
 * @author User
 * Data Access Object for Flight entities. Handles all database operations for
 * flights including CRUD operations.
 */
    
public class FlightDAO {

    private static final Logger LOGGER = Logger.getLogger(FlightDAO.class.getName());

    // SQL Queries
    private static final String INSERT_FLIGHT = "INSERT INTO flights (flight_number, scheduled_departure, scheduled_arrival, origin_airport, destination_airport_airport, "
            + "gate_id, flight_status, aircraft_id, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_FLIGHTS = "SELECT flight_id, flight_number, scheduled_departure, scheduled_arrival, origin_airport, destination_airport, "
            + "gate_id, flight_status, aircraft_id, created_at FROM flights ORDER BY scheduled_departure";

    private static final String SELECT_FLIGHT_BY_ID = "SELECT flight_id, flight_number, scheduled_departure, scheduled_arrival, origin_airport, destination_airport, "
            + "gate_id, flight_status, aircraft_id, created_at FROM flights WHERE flight_id = ?";

    private static final String SELECT_FLIGHT_BY_NUMBER = "SELECT flight_id, flight_number, scheduled_departure, scheduled_arrival, origin_airport, destination_airport, "
            + "gate_id, flight_status, aircraft_id, created_at FROM flights WHERE flight_number = ?";

    private static final String UPDATE_FLIGHT = "UPDATE flights SET flight_number = ?, scheduled_departure = ?, scheduled_arrival = ?, origin_airport = ?, "
            + "destination_airport = ?, gate_id = ?, flight_status = ?, aircraft_id = ? = ? WHERE flight_id = ?";

    private static final String UPDATE_FLIGHT_STATUS = "UPDATE flights SET flight_status = ? WHERE flight_id = ?";

    private static final String UPDATE_FLIGHT_GATE = "UPDATE flights SET gate_id = ? WHERE flight_id = ?";

    private static final String DELETE_FLIGHT = "DELETE FROM flights WHERE flight_id = ?";

    private static final String SELECT_FLIGHTS_BY_DATE_RANGE = "SELECT flight_id, flight_number, scheduled_departure, scheduled_arrival, origin_airport, destination_airport, "
            + "gate_id, flight_status, aircraft_id, created_at FROM flights "
            + "WHERE scheduled_departure BETWEEN ? AND ? ORDER BY scheduled_departure";

    private static final String SELECT_FLIGHTS_BY_STATUS = "SELECT flight_id, flight_number, scheduled_departure, scheduled_arrival, origin_airport, destination_airport, "
            + "gate_id, flight_status, aircraft_id, created_at FROM flights "
            + "WHERE flight_status = ? ORDER BY scheduled_departure";

    /**
     * Inserts a new flight into the database
     * @param flight
     * @return 
     * @throws com.gatorsoft.aerodeskpro.exceptions.AeroDeskException
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
     * @return 
     * @throws com.gatorsoft.aerodeskpro.exceptions.AeroDeskException
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

            LOGGER.log(Level.INFO, "Retrieved {0} flights from database", flights.size());

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
                LOGGER.log(Level.INFO, "Flight found: {0}", flight.getFlightNumber());
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
     * @param flightNumber
     * @return 
     * @throws com.gatorsoft.aerodeskpro.exceptions.AeroDeskException
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
                LOGGER.log(Level.INFO, "Flight found by number: {0}", flight.getFlightNumber());
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
     * @param flight
     * @return 
     * @throws com.gatorsoft.aerodeskpro.exceptions.AeroDeskException
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
                LOGGER.log(Level.INFO, "Flight updated successfully: {0}", flight.getFlightNumber());
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
     * Updates only the flight_status of a flight
     * @param flightId
     * @param flight_status
     * @return 
     * @throws com.gatorsoft.aerodeskpro.exceptions.AeroDeskException 
     */
    public boolean updateFlightStatus(int flightId, FlightStatus flight_status)
            throws AeroDeskException {
        if (flight_status == null) {
            throw new AeroDeskException("Flight flight_status cannot be null",
                    ErrorCategory.DATABASE_ERROR);
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(UPDATE_FLIGHT_STATUS);
            statement.setString(1, flight_status.name());
            statement.setInt(2, flightId);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.log(Level.INFO, "Flight flight_status updated to {0} for flight ID: {1}", new Object[]{flight_status, flightId});
                return true;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating flight flight_status for ID: " + flightId,
                    e);
            throw new AeroDeskException("Failed to update flight flight_status", e);
        } finally {
            closeResources(null, statement, connection);
        }

        return false;
    }

    /**
     * Updates the gate assignment for a flight
     * @param flightId
     * @param gateNumber
     * @return 
     * @throws com.gatorsoft.aerodeskpro.exceptions.AeroDeskException 
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
                LOGGER.log(Level.INFO, "Gate updated to {0} for flight ID: {1}", new Object[]{gateNumber, flightId});
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
     * @param flightId
     * @return 
     * @throws com.gatorsoft.aerodeskpro.exceptions.AeroDeskException
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
                LOGGER.log(Level.INFO, "Flight deleted successfully with ID: {0}", flightId);
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
     * @param startDate
     * @param endDate
     * @return 
     * @throws com.gatorsoft.aerodeskpro.exceptions.AeroDeskException 
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

            LOGGER.log(Level.INFO, "Retrieved {0} flights for date range", flights.size());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving flights by date range", e);
            throw new AeroDeskException("Failed to retrieve flights by date range", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return flights;
    }

    /**
     * Retrieves flights by flight_status
     * @param flight_status
     * @return 
     * @throws com.gatorsoft.aerodeskpro.exceptions.AeroDeskException
     */
    public List<Flight> getFlightsByStatus(FlightStatus flight_status) throws AeroDeskException {
        List<Flight> flights = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(SELECT_FLIGHTS_BY_STATUS);
            statement.setString(1, flight_status.name());
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                flights.add(mapResultSetToFlight(resultSet));
            }

            LOGGER.log(Level.INFO, "Retrieved {0} flights with flight_status: {1}", new Object[]{flights.size(), flight_status});

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving flights by flight_status: " + flight_status, e);
            throw new AeroDeskException("Failed to retrieve flights by flight_status", e);
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
                resultSet.getTimestamp("scheduled_departure").toLocalDateTime());
        flight.setArrivalTime(resultSet.getTimestamp("scheduled_arrival").toLocalDateTime());
        flight.setOrigin(resultSet.getString("origin_airport"));
        flight.setDestination(resultSet.getString("destination_airport"));
        flight.setGateNumber(resultSet.getInt("gate_id"));
        flight.setStatus(FlightStatus.valueOf(resultSet.getString("flight_status")));
        flight.setAircraftType(resultSet.getInt("aircraft_id"));
        //flight.setCapacity(resultSet.getInt"));

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