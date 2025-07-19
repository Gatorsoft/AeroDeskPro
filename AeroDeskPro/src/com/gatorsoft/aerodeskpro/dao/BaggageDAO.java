/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gatorsoft.aerodeskpro.dao;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gatorsoft.aerodeskpro.database.DatabaseConnection;
import com.gatorsoft.aerodeskpro.entity.BaggageStatus;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException.ErrorCategory;
import com.gatorsoft.aerodeskpro.models.Baggage;
import com.gatorsoft.aerodeskpro.models.BaggageReport;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Access Object for Baggage entities. Handles all database operations for
 * baggage including CRUD operations.
 */
public class BaggageDAO {

    private static final Logger LOGGER = Logger.getLogger(BaggageDAO.class.getName());

    // SQL Queries
    private static final String INSERT_BAGGAGE = "INSERT INTO baggage (baggage_tag, booking_id, status, weight, "
            + "created_at) VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_BAGGAGE = "SELECT baggage_id, baggage_tag, booking_id, status, weight, "
            + "created_at FROM baggage ORDER BY baggage_tag";

    private static final String SELECT_BAGGAGE_BY_ID = "SELECT baggage_id, baggage_tag, booking_id, status, weight, "
            + "created_at FROM baggage WHERE baggage_id = ?";

    private static final String SELECT_BAGGAGE_BY_TAG = "SELECT baggage_id, baggage_tag, booking_id, status, weight, "
            + "created_at FROM baggage WHERE baggage_tag = ?";

    private static final String SELECT_BAGGAGE_BY_BOOKING = "SELECT baggage_id, baggage_tag, booking_id, status, weight, "
            + "created_at FROM baggage WHERE booking_id = ?";

    private static final String UPDATE_BAGGAGE = "UPDATE baggage SET baggage_tag = ?, booking_id = ?, status = ?, "
            + "weight = ?, created_at = ? WHERE baggage_id = ?";

    private static final String DELETE_BAGGAGE = "DELETE FROM baggage WHERE baggage_id = ?";

    private static final String UPDATE_BAGGAGE_STATUS = "UPDATE baggage SET status = ?, location = ? WHERE baggage_tag = ?";
    private static final String TRACK_BAGGAGE = "INSERT INTO baggage_tracking (baggage_id, location, notes, created_at) VALUES (?, ?, ?, ?)";
    private static final String SELECT_LOST_BAGGAGE = "SELECT * FROM baggage WHERE status = 'LOST'";
    private static final String SELECT_DELAYED_BAGGAGE = "SELECT * FROM baggage WHERE status = 'DELAYED'";
    private static final String GENERATE_BAGGAGE_REPORT = "SELECT COUNT(*) AS total_baggage, SUM(weight) AS total_weight FROM baggage WHERE created_at = ?";
    private static final String GENERATE_BAGGAGE_TAG = "SELECT CONCAT('BAG', LPAD(FLOOR(RAND() * 10000), 4, '0')) AS baggage_tag";
//private static final String SELECT_DELAYED_BAGGAGE = "SELECT baggage_id, baggage_tag, booking_id, status, weight, created_at " +
 //       "FROM baggage WHERE status = 'DELAYED' ORDER BY created_at";
    /**
     * Inserts a new baggage record into the database
     */
    public boolean insertBaggage(Baggage baggage) throws AeroDeskException {
        if (baggage == null || !baggage.isValid()) {
            throw new AeroDeskException("Invalid baggage data", ErrorCategory.DATABASE_ERROR);
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(INSERT_BAGGAGE, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, baggage.getBaggageTag());
            statement.setInt(2, baggage.getBookingId());
            statement.setString(3, baggage.getStatus().name());
            statement.setBigDecimal(4, baggage.getWeight());
            statement.setTimestamp(5, java.sql.Timestamp.valueOf(baggage.getCreatedAt()));

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    baggage.setBaggageId(generatedKeys.getInt(1));
                }
                LOGGER.info("Baggage inserted successfully: " + baggage.getBaggageTag());
                return true;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting baggage: " + baggage.getBaggageTag(), e);
            throw new AeroDeskException("Failed to insert baggage", e);
        } finally {
            closeResources(generatedKeys, statement, connection);
        }

        return false;
    }

    /**
     * Retrieves all baggage records from the database
     */
    public List<Baggage> getAllBaggage() throws AeroDeskException {
        List<Baggage> baggageList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(SELECT_ALL_BAGGAGE);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                baggageList.add(mapResultSetToBaggage(resultSet));
            }

            LOGGER.info("Retrieved " + baggageList.size() + " baggage records from database");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all baggage", e);
            throw new AeroDeskException("Failed to retrieve baggage", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return baggageList;
    }

    /**
     * Finds a baggage record by its ID
     */
    public Baggage getBaggageById(int baggageId) throws AeroDeskException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(SELECT_BAGGAGE_BY_ID);
            statement.setInt(1, baggageId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Baggage baggage = mapResultSetToBaggage(resultSet);
                LOGGER.info("Baggage found: " + baggage.getBaggageTag());
                return baggage;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding baggage by ID: " + baggageId, e);
            throw new AeroDeskException("Failed to find baggage by ID", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return null;
    }

    /**
     * Finds a baggage record by its tag
     */
    public Baggage getBaggageByTag(String baggageTag) throws AeroDeskException {
        if (baggageTag == null || baggageTag.trim().isEmpty()) {
            throw new AeroDeskException("Baggage tag cannot be null or empty", ErrorCategory.DATABASE_ERROR);
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(SELECT_BAGGAGE_BY_TAG);
            statement.setString(1, baggageTag.trim());
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Baggage baggage = mapResultSetToBaggage(resultSet);
                LOGGER.info("Baggage found by tag: " + baggage.getBaggageTag());
                return baggage;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding baggage by tag: " + baggageTag, e);
            throw new AeroDeskException("Failed to find baggage by tag", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return null;
    }

    /**
     * Finds a baggage record by booking ID
     */
    public List<Baggage> getBaggageByBooking(int bookingId) throws AeroDeskException {
        List<Baggage> baggageList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(SELECT_BAGGAGE_BY_BOOKING);
            statement.setInt(1, bookingId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                baggageList.add(mapResultSetToBaggage(resultSet));
            }

            LOGGER.info("Retrieved " + baggageList.size() + " baggage records for booking ID: " + bookingId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding baggage by booking ID: " + bookingId, e);
            throw new AeroDeskException("Failed to find baggage by booking ID", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return baggageList;
    }

    /**
     * Updates an existing baggage record
     */
    public boolean updateBaggage(Baggage baggage) throws AeroDeskException {
        if (baggage == null || !baggage.isValid() || baggage.getBaggageId() <= 0) {
            throw new AeroDeskException("Invalid baggage data for update", ErrorCategory.RESOURCE_NOT_FOUND);
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(UPDATE_BAGGAGE);

            statement.setString(1, baggage.getBaggageTag());
            statement.setInt(2, baggage.getBookingId());
            // Assuming getStatus() returns a String or Enum, use .toString() if it's an Enum
            statement.setString(3, baggage.getStatus().toString());  // This is safe if it's an Enum, otherwise just use baggage.getStatus() if it's already String
            statement.setBigDecimal(4, baggage.getWeight());  // Use this if weight is BigDecimal

            statement.setTimestamp(5, java.sql.Timestamp.valueOf(baggage.getCreatedAt()));
            statement.setInt(6, baggage.getBaggageId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Baggage updated successfully: " + baggage.getBaggageTag());
                return true;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating baggage: " + baggage.getBaggageTag(), e);
            throw new AeroDeskException("Failed to update baggage", e);
        } finally {
            closeResources(null, statement, connection);
        }

        return false;
    }

    /**
     * Deletes a baggage record from the database
     */
    public boolean deleteBaggage(int baggageId) throws AeroDeskException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(DELETE_BAGGAGE);
            statement.setInt(1, baggageId);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Baggage deleted successfully with ID: " + baggageId);
                return true;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting baggage with ID: " + baggageId, e);
            throw new AeroDeskException("Failed to delete baggage", e);
        } finally {
            closeResources(null, statement, connection);
        }

        return false;
    }

    /**
     * Maps a ResultSet row to a Baggage object
     */
    private Baggage mapResultSetToBaggage(ResultSet resultSet) throws SQLException {
        Baggage baggage = new Baggage();

        baggage.setBaggageId(resultSet.getInt("baggage_id"));
        baggage.setBaggageTag(resultSet.getString("baggage_tag"));
        baggage.setBookingId(resultSet.getInt("booking_id"));

        // Handle BaggageStatus Enum (using BaggageStatus enum)
        String statusString = resultSet.getString("status");
        if (statusString != null) {
            try {
                baggage.setStatus(BaggageStatus.valueOf(statusString.toUpperCase())); // Assuming the DB stores the status in uppercase
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status value: " + statusString);  // Optional: log or handle invalid status values
                baggage.setStatus(BaggageStatus.REGISTERED); // Default value
            }
        } else {
            baggage.setStatus(BaggageStatus.REGISTERED); // Default if status is null
        }

        // Set Weight as BigDecimal (handle it properly from ResultSet)
        BigDecimal weight = resultSet.getBigDecimal("weight");
        baggage.setWeight(weight != null ? weight : BigDecimal.ZERO); // Handle null by setting default value

        baggage.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());

        return baggage;
    }

    /**
     * Closes database resources safely
     */
    // Update baggage status
    public boolean updateBaggageStatus(String baggageTag, BaggageStatus newStatus, String location) throws AeroDeskException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(UPDATE_BAGGAGE_STATUS);

            statement.setString(1, newStatus.name());
            statement.setString(2, location);
            statement.setString(3, baggageTag);

            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating baggage status for tag: " + baggageTag, e);
            throw new AeroDeskException("Failed to update baggage status", e);
        } finally {
            closeResources(null, statement, connection);
        }
    }

    // Track baggage (add tracking info)
    public boolean trackBaggage(int baggageId, String location, String notes) throws AeroDeskException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(TRACK_BAGGAGE);

            statement.setInt(1, baggageId);
            statement.setString(2, location);
            statement.setString(3, notes);
            statement.setTimestamp(4, Timestamp.valueOf(LocalDate.now().atStartOfDay()));

            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error tracking baggage with ID: " + baggageId, e);
            throw new AeroDeskException("Failed to track baggage", e);
        } finally {
            closeResources(null, statement, connection);
        }
    }

    // Get lost baggage
    public List<Baggage> getLostBaggage() throws AeroDeskException {
        List<Baggage> lostBaggage = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(SELECT_LOST_BAGGAGE);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                lostBaggage.add(mapResultSetToBaggage(resultSet));
            }

            return lostBaggage;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving lost baggage", e);
            throw new AeroDeskException("Failed to retrieve lost baggage", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

   // Query to fetch delayed baggage based on status or other criteria


public List<Baggage> getDelayedBaggage() throws AeroDeskException {
    List<Baggage> delayedBaggage = new ArrayList<>();
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try {
        connection = DatabaseConnection.getConnection();
        statement = connection.prepareStatement(SELECT_DELAYED_BAGGAGE);
        resultSet = statement.executeQuery();

        while (resultSet.next()) {
            delayedBaggage.add(mapResultSetToBaggage(resultSet));
        }

        return delayedBaggage;

    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Error retrieving delayed baggage", e);
        throw new AeroDeskException("Failed to retrieve delayed baggage", e);
    } finally {
        closeResources(resultSet, statement, connection);
    }
}

    // Generate baggage tag (simple random tag generator)
    public String generateBaggageTag() throws AeroDeskException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(GENERATE_BAGGAGE_TAG);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("baggage_tag");
            }

            throw new AeroDeskException("Failed to generate baggage tag", AeroDeskException.ErrorCategory.SYSTEM_ERROR);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating baggage tag", e);
            throw new AeroDeskException("Failed to generate baggage tag", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    // Helper method to map ResultSet to Baggage object
  /*  private Baggage mapResultSetToBaggage(ResultSet resultSet) throws SQLException {
        Baggage baggage = new Baggage();
        baggage.setBaggageId(resultSet.getInt("baggage_id"));
        baggage.setBaggageTag(resultSet.getString("baggage_tag"));
        baggage.setBookingId(resultSet.getInt("booking_id"));
        baggage.setStatus(BaggageStatus.valueOf(resultSet.getString("status")));
        baggage.setWeight(resultSet.getBigDecimal("weight"));
        baggage.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        return baggage;
    }

  */
/*   private static final String GENERATE_BAGGAGE_REPORT = 
    "SELECT COUNT(*) AS total_baggage, SUM(weight) AS total_weight " +
    "FROM baggage WHERE created_at >= ? AND created_at < ?";
*/
public BaggageReport generateBaggageReport(LocalDate date) throws AeroDeskException {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try {
        // Get a database connection
        connection = DatabaseConnection.getConnection();

        // Prepare the SQL query with a date range
        statement = connection.prepareStatement(GENERATE_BAGGAGE_REPORT);
        
        // Set the start and end date range for the report (from the start of the day to the next day)
        statement.setTimestamp(1, Timestamp.valueOf(date.atStartOfDay())); // Start of the day
        statement.setTimestamp(2, Timestamp.valueOf(date.plusDays(1).atStartOfDay())); // Start of the next day

        // Execute the query
        resultSet = statement.executeQuery();

        // If the result set has a row, map the results to the BaggageReport object
        if (resultSet.next()) {
            int totalBaggage = resultSet.getInt("total_baggage");
            BigDecimal totalWeight = resultSet.getBigDecimal("total_weight");
            int lostBaggage = resultSet.getInt("lost_baggage");
            int delayedBaggage = resultSet.getInt("delayed_baggage");

            // Create and return a new BaggageReport object
            return new BaggageReport(totalBaggage, totalWeight, lostBaggage, delayedBaggage);
        }

        // Return null if no data is found
        return null;

    } catch (SQLException e) {
        // Log the error and throw an exception
        LOGGER.log(Level.SEVERE, "Error generating baggage report in DAO", e);
        throw new AeroDeskException("Failed to generate baggage report in DAO", e);
    } finally {
        // Ensure resources are closed properly
        closeResources(resultSet, statement, connection);
    }
}


    private void closeResources(ResultSet resultSet, PreparedStatement statement, Connection connection) {
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
