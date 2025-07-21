/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gatorsoft.aerodeskpro.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gatorsoft.aerodeskpro.database.DatabaseConnection;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException.ErrorCategory;
import com.gatorsoft.aerodeskpro.models.Passenger;

/**
 * Data Access Object for Passenger entities. Handles all database operations
 * for passengers including CRUD operations.
 */
public class PassengerDAO {

    private static final Logger LOGGER = Logger.getLogger(PassengerDAO.class.getName());

    // SQL Queries
    private static final String INSERT_PASSENGER = "INSERT INTO passengers (first_name, last_name, passport_number, "
            + "nationality, date_of_birth, gender, email, phone, frequent_flyer_number, created_at) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_PASSENGERS = "SELECT passenger_id, first_name, last_name, passport_number, "
            + "nationality, date_of_birth, gender, email, phone, frequent_flyer_number, created_at "
            + "FROM passengers ORDER BY last_name, first_name";

    private static final String SELECT_PASSENGER_BY_ID = "SELECT passenger_id, first_name, last_name, passport_number, "
            + "nationality, date_of_birth, gender, email, phone, frequent_flyer_number, created_at "
            + "FROM passengers WHERE passenger_id = ?";

    private static final String SELECT_PASSENGER_BY_PASSPORT = "SELECT passenger_id, first_name, last_name, passport_number, "
            + "nationality, date_of_birth, gender, email, phone, frequent_flyer_number, created_at "
            + "FROM passengers WHERE passport_number = ?";

    private static final String SELECT_PASSENGER_BY_EMAIL = "SELECT passenger_id, first_name, last_name, passport_number, "
            + "nationality, date_of_birth, gender, email, phone, frequent_flyer_number, created_at "
            + "FROM passengers WHERE email = ?";

    private static final String UPDATE_PASSENGER = "UPDATE passengers SET first_name = ?, last_name = ?, passport_number = ?, "
            + "nationality = ?, date_of_birth = ?, gender = ?, email = ?, phone = ?, frequent_flyer_number = ? "
            + "WHERE passenger_id = ?";

    private static final String DELETE_PASSENGER = "DELETE FROM passengers WHERE passenger_id = ?";

    private static final String SEARCH_PASSENGERS = "SELECT passenger_id, first_name, last_name, passport_number, "
            + "nationality, date_of_birth, gender, email, phone, frequent_flyer_number, created_at "
            + "FROM passengers WHERE CONCAT(first_name, ' ', last_name) LIKE ? OR passport_number LIKE ? "
            + "ORDER BY last_name, first_name";

    /**
     * Inserts a new passenger into the database
     */
    public boolean insertPassenger(Passenger passenger) throws AeroDeskException {
        if (passenger == null || !passenger.isValid()) {
            throw new AeroDeskException("Invalid passenger data", ErrorCategory.DATABASE_ERROR);
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(INSERT_PASSENGER, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, passenger.getFirstName());
            statement.setString(2, passenger.getLastName());
            statement.setString(3, passenger.getPassportNumber());
            statement.setString(4, passenger.getNationality());
            statement.setDate(5, passenger.getDateOfBirth() != null ? Date.valueOf(passenger.getDateOfBirth()) : null);
            statement.setString(6, passenger.getGender() != null ? passenger.getGender().name() : null);
            statement.setString(7, passenger.getEmail());
            statement.setString(8, passenger.getPhone());
            statement.setString(9, passenger.getFrequentFlyerNumber());
            statement.setTimestamp(10, java.sql.Timestamp.valueOf(passenger.getCreatedAt()));

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    passenger.setPassengerId(generatedKeys.getInt(1));
                }
                LOGGER.info("Passenger inserted successfully: " + passenger.getFirstName() + " " + passenger.getLastName());
                return true;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting passenger: " + passenger.getFirstName() + " " + passenger.getLastName(), e);
            throw new AeroDeskException("Failed to insert passenger", e);
        } finally {
            closeResources(generatedKeys, statement, connection);
        }

        return false;
    }

    /**
     * Retrieves all passengers from the database
     * @return 
     * @throws com.gatorsoft.aerodeskpro.exceptions.AeroDeskException
     */
    public List<Passenger> getAllPassengersNum() throws AeroDeskException {
        List<Passenger> passengers = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(SELECT_ALL_PASSENGERS);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                passengers.add(mapResultSetToPassenger(resultSet));
            }

            LOGGER.info("Retrieved " + passengers.size() + " passengers from database");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all passengers", e);
            throw new AeroDeskException("Failed to retrieve passengers", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return passengers;
    }

    /**
     * Finds a passenger by their ID
     */
    public Passenger getPassengerById(int passengerId) throws AeroDeskException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(SELECT_PASSENGER_BY_ID);
            statement.setInt(1, passengerId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Passenger passenger = mapResultSetToPassenger(resultSet);
                LOGGER.info("Passenger found: " + passenger.getFirstName() + " " + passenger.getLastName());
                return passenger;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding passenger by ID: " + passengerId, e);
            throw new AeroDeskException("Failed to find passenger by ID", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return null;
    }

    /**
     * Finds a passenger by passport number
     */
    public Passenger getPassengerByPassport(String passportNumber) throws AeroDeskException {
        if (passportNumber == null || passportNumber.trim().isEmpty()) {
            throw new AeroDeskException("Passport number cannot be null or empty", ErrorCategory.DATABASE_ERROR);
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(SELECT_PASSENGER_BY_PASSPORT);
            statement.setString(1, passportNumber.trim());
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Passenger passenger = mapResultSetToPassenger(resultSet);
                LOGGER.info("Passenger found by passport: " + passenger.getFirstName() + " " + passenger.getLastName());
                return passenger;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding passenger by passport: " + passportNumber, e);
            throw new AeroDeskException("Failed to find passenger by passport", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return null;
    }

    /**
     * Finds a passenger by email
     */
    public Passenger getPassengerByEmail(String email) throws AeroDeskException {
        if (email == null || email.trim().isEmpty()) {
            throw new AeroDeskException("Email cannot be null or empty", ErrorCategory.DATABASE_ERROR);
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(SELECT_PASSENGER_BY_EMAIL);
            statement.setString(1, email.trim());
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Passenger passenger = mapResultSetToPassenger(resultSet);
                LOGGER.info("Passenger found by email: " + passenger.getFirstName() + " " + passenger.getLastName());
                return passenger;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding passenger by email: " + email, e);
            throw new AeroDeskException("Failed to find passenger by email", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return null;
    }

    /**
     * Updates an existing passenger
     */
    public boolean updatePassenger(Passenger passenger) throws AeroDeskException {
        if (passenger == null || !passenger.isValid() || passenger.getPassengerId() <= 0) {
            throw new AeroDeskException("Invalid passenger data for update", ErrorCategory.RESOURCE_NOT_FOUND);
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(UPDATE_PASSENGER);

            statement.setString(1, passenger.getFirstName());
            statement.setString(2, passenger.getLastName());
            statement.setString(3, passenger.getPassportNumber());
            statement.setString(4, passenger.getNationality());
            statement.setDate(5, passenger.getDateOfBirth() != null ? Date.valueOf(passenger.getDateOfBirth()) : null);
            statement.setString(6, passenger.getGender() != null ? passenger.getGender().name() : null);
            statement.setString(7, passenger.getEmail());
            statement.setString(8, passenger.getPhone());
            statement.setString(9, passenger.getFrequentFlyerNumber());
            statement.setInt(10, passenger.getPassengerId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Passenger updated successfully: " + passenger.getFirstName() + " " + passenger.getLastName());
                return true;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating passenger: " + passenger.getFirstName() + " " + passenger.getLastName(), e);
            throw new AeroDeskException("Failed to update passenger", e);
        } finally {
            closeResources(null, statement, connection);
        }

        return false;
    }

    /**
     * Deletes a passenger from the database
     */
    public boolean deletePassenger(int passengerId) throws AeroDeskException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(DELETE_PASSENGER);
            statement.setInt(1, passengerId);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Passenger deleted successfully with ID: " + passengerId);
                return true;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting passenger with ID: " + passengerId, e);
            throw new AeroDeskException("Failed to delete passenger", e);
        } finally {
            closeResources(null, statement, connection);
        }

        return false;
    }

    /**
     * Searches passengers by name or passport number
     */
    public List<Passenger> searchPassengers(String searchTerm) throws AeroDeskException {
        List<Passenger> passengers = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(SEARCH_PASSENGERS);
            String searchPattern = "%" + searchTerm + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                passengers.add(mapResultSetToPassenger(resultSet));
            }

            LOGGER.info("Found " + passengers.size() + " passengers for search term: " + searchTerm);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching passengers with term: " + searchTerm, e);
            throw new AeroDeskException("Failed to search passengers", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return passengers;
    }

    /**
     * Maps a ResultSet row to a Passenger object
     */
    private Passenger mapResultSetToPassenger(ResultSet resultSet) throws SQLException {
        Passenger passenger = new Passenger();

        passenger.setPassengerId(resultSet.getInt("passenger_id"));
        passenger.setFirstName(resultSet.getString("first_name"));
        passenger.setLastName(resultSet.getString("last_name"));
        passenger.setPassportNumber(resultSet.getString("passport_number"));
        passenger.setNationality(resultSet.getString("nationality"));

        Date dateOfBirth = resultSet.getDate("date_of_birth");
        if (dateOfBirth != null) {
            passenger.setDateOfBirth(dateOfBirth.toLocalDate());
        }

        String gender = resultSet.getString("gender");
        if (gender != null) {
            passenger.setGender(Passenger.Gender.valueOf(gender));
        }

        passenger.setEmail(resultSet.getString("email"));
        passenger.setPhone(resultSet.getString("phone"));
        passenger.setFrequentFlyerNumber(resultSet.getString("frequent_flyer_number"));

        java.sql.Timestamp createdTimestamp = resultSet.getTimestamp("created_at");
        if (createdTimestamp != null) {
            passenger.setCreatedAt(createdTimestamp.toLocalDateTime());
        }

        return passenger;
    }

    public List<Passenger> findByName(String firstName, String lastName) throws AeroDeskException {
        List<Passenger> passengers = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String sql = "SELECT passenger_id, first_name, last_name, passport_number, nationality, "
                + "date_of_birth, gender, email, phone, frequent_flyer_number, created_at "
                + "FROM passengers WHERE first_name = ? AND last_name = ? ORDER BY last_name, first_name";

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                passengers.add(mapResultSetToPassenger(resultSet));
            }

            LOGGER.info("Found " + passengers.size() + " passengers with name: " + firstName + " " + lastName);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding passengers by name", e);
            throw new AeroDeskException("Failed to find passengers by name", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return passengers;
    }

    public List<Passenger> findByFrequentFlyerNumber(String ffNumber) throws AeroDeskException {
        List<Passenger> passengers = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String sql = "SELECT passenger_id, first_name, last_name, passport_number, nationality, "
                + "date_of_birth, gender, email, phone, frequent_flyer_number, created_at "
                + "FROM passengers WHERE frequent_flyer_number = ?";

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, ffNumber);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                passengers.add(mapResultSetToPassenger(resultSet));
            }

            LOGGER.info("Found " + passengers.size() + " passengers with frequent flyer number: " + ffNumber);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding passengers by frequent flyer number", e);
            throw new AeroDeskException("Failed to find passengers by frequent flyer number", e);
        } finally {
            closeResources(resultSet, statement, connection);
        }

        return passengers;
    }

    /**
     * Closes database resources safely
     */
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
    
    /**
     * Retrieves all passengers from the database.
     */
/*
@return
 */
   public List<Passenger> getAllPassengers() throws AeroDeskException {
    List<Passenger> passengers = new ArrayList<>();
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    String sql = "SELECT passenger_id, first_name, last_name, passport_number, nationality, "
            + "date_of_birth, gender, email, phone, frequent_flyer_number, created_at "
            + "FROM passengers";

    try {
        // Establish the database connection
        connection = DatabaseConnection.getConnection();

        // Prepare the SQL statement (no parameters needed here)
        statement = connection.prepareStatement(sql);

        // Execute the query and get the result set
        resultSet = statement.executeQuery();

        // Iterate over the result set and map the result to a list of Passenger objects
        while (resultSet.next()) {
            passengers.add(mapResultSetToPassenger(resultSet));
        }

        // Log success
        LOGGER.info("Found " + passengers.size() + " passengers.");

    } catch (SQLException e) {
        // Handle any SQL exceptions
        LOGGER.log(Level.SEVERE, "Error retrieving all passengers", e);
        throw new AeroDeskException("Failed to retrieve all passengers", e);
    } finally {
        // Ensure resources are closed after use
        closeResources(resultSet, statement, connection);
    }

    return passengers;
}


}
