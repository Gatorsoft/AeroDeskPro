package com.gatorsoft.aerodeskpro.services;

import com.gatorsoft.aerodeskpro.dao.PassengerDAO;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException;
import com.gatorsoft.aerodeskpro.models.Passenger;

import java.util.List;
import java.util.logging.Logger;

public class PassengerService {

    private static final Logger LOGGER = Logger.getLogger(PassengerService.class.getName());

    private PassengerDAO passengerDAO; // Data Access Object for Passenger

    // Constructor that injects the PassengerDAO dependency
    public PassengerService(PassengerDAO passengerDAO) {
        this.passengerDAO = passengerDAO;
    }

    /**
     * Registers a new Passenger
     */
    public Passenger registerPassenger(Passenger passenger) throws AeroDeskException {
        try {
            if (passenger == null || !passenger.isValid()) {
                throw new AeroDeskException("Invalid passenger data.", AeroDeskException.ErrorCategory.VALIDATION_ERROR);
            }

            // Call DAO to insert passenger into the database
            boolean success = passengerDAO.insertPassenger(passenger);
            if (success) {
                LOGGER.info("Successfully registered passenger: " + passenger.getFirstName() + " " + passenger.getLastName());
                return passenger;
            } else {
                throw new AeroDeskException("Failed to register passenger.", AeroDeskException.ErrorCategory.DATABASE_ERROR);
            }
        } catch (AeroDeskException e) {
            LOGGER.severe("Error registering passenger: " + e.getDetailedMessage());
            throw e; // Rethrow custom exception
        } catch (Exception e) {
            LOGGER.severe("Unexpected error while registering passenger: " + e.getMessage());
            throw new AeroDeskException("Unexpected error while registering passenger.", e, AeroDeskException.ErrorCategory.SYSTEM_ERROR, "REGISTRATION_ERROR", passenger);
        }
    }

    /**
     * Updates an existing Passenger
     */
    public Passenger updatePassenger(Passenger passenger) throws AeroDeskException {
        try {
            if (passenger == null || !passenger.isValid() || passenger.getPassengerId() <= 0) {
                throw new AeroDeskException("Invalid passenger data for update.", AeroDeskException.ErrorCategory.VALIDATION_ERROR);
            }

            boolean success = passengerDAO.updatePassenger(passenger);
            if (success) {
                LOGGER.info("Successfully updated passenger: " + passenger.getFirstName() + " " + passenger.getLastName());
                return passenger;
            } else {
                throw new AeroDeskException("Failed to update passenger.", AeroDeskException.ErrorCategory.DATABASE_ERROR);
            }
        } catch (AeroDeskException e) {
            LOGGER.severe("Error updating passenger: " + e.getDetailedMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error while updating passenger: " + e.getMessage());
            throw new AeroDeskException("Unexpected error while updating passenger.", e, AeroDeskException.ErrorCategory.SYSTEM_ERROR, "UPDATE_ERROR", passenger);
        }
    }

    /**
     * Retrieves a Passenger by their ID
     */
    public Passenger getPassengerById(int passengerId) throws AeroDeskException {
        try {
            if (passengerId <= 0) {
                throw new AeroDeskException("Invalid passenger ID.", AeroDeskException.ErrorCategory.VALIDATION_ERROR);
            }

            Passenger passenger = passengerDAO.getPassengerById(passengerId);
            if (passenger != null) {
                LOGGER.info("Passenger found by ID: " + passenger.getFirstName() + " " + passenger.getLastName());
                return passenger;
            } else {
                throw new AeroDeskException("Passenger not found with ID: " + passengerId, AeroDeskException.ErrorCategory.RESOURCE_NOT_FOUND);
            }
        } catch (AeroDeskException e) {
            LOGGER.severe("Error fetching passenger by ID: " + e.getDetailedMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error while fetching passenger by ID: " + e.getMessage());
            throw new AeroDeskException("Unexpected error while fetching passenger by ID.", e, AeroDeskException.ErrorCategory.SYSTEM_ERROR, "FETCH_ERROR", passengerId);
        }
    }

    /**
     * Retrieves a Passenger by their passport number
     */
    public Passenger getPassengerByPassport(String passportNumber) throws AeroDeskException {
        try {
            if (passportNumber == null || passportNumber.trim().isEmpty()) {
                throw new AeroDeskException("Passport number is required.", AeroDeskException.ErrorCategory.VALIDATION_ERROR);
            }

            Passenger passenger = passengerDAO.getPassengerByPassport(passportNumber);
            if (passenger != null) {
                LOGGER.info("Passenger found by passport: " + passenger.getFirstName() + " " + passenger.getLastName());
                return passenger;
            } else {
                throw new AeroDeskException("Passenger not found with passport number: " + passportNumber, AeroDeskException.ErrorCategory.RESOURCE_NOT_FOUND);
            }
        } catch (AeroDeskException e) {
            LOGGER.severe("Error fetching passenger by passport number: " + e.getDetailedMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error while fetching passenger by passport number: " + e.getMessage());
            throw new AeroDeskException("Unexpected error while fetching passenger by passport number.", e, AeroDeskException.ErrorCategory.SYSTEM_ERROR, "FETCH_PASSPORT_ERROR", passportNumber);
        }
    }

    /**
     * Retrieves a Passenger by their email
     */
    public Passenger getPassengerByEmail(String email) throws AeroDeskException {
        try {
            if (email == null || email.trim().isEmpty()) {
                throw new AeroDeskException("Email is required.", AeroDeskException.ErrorCategory.VALIDATION_ERROR);
            }

            Passenger passenger = passengerDAO.getPassengerByEmail(email);
            if (passenger != null) {
                LOGGER.info("Passenger found by email: " + passenger.getFirstName() + " " + passenger.getLastName());
                return passenger;
            } else {
                throw new AeroDeskException("Passenger not found with email: " + email, AeroDeskException.ErrorCategory.RESOURCE_NOT_FOUND);
            }
        } catch (AeroDeskException e) {
            LOGGER.severe("Error fetching passenger by email: " + e.getDetailedMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error while fetching passenger by email: " + e.getMessage());
            throw new AeroDeskException("Unexpected error while fetching passenger by email.", e, AeroDeskException.ErrorCategory.SYSTEM_ERROR, "FETCH_EMAIL_ERROR", email);
        }
    }

    /**
     * Deletes a Passenger by their ID
     */
    public boolean deletePassenger(int passengerId) throws AeroDeskException {
        try {
            if (passengerId <= 0) {
                throw new AeroDeskException("Invalid passenger ID for deletion.", AeroDeskException.ErrorCategory.VALIDATION_ERROR);
            }

            boolean success = passengerDAO.deletePassenger(passengerId);
            if (success) {
                LOGGER.info("Passenger deleted successfully with ID: " + passengerId);
                return true;
            } else {
                throw new AeroDeskException("Failed to delete passenger with ID: " + passengerId, AeroDeskException.ErrorCategory.DATABASE_ERROR);
            }
        } catch (AeroDeskException e) {
            LOGGER.severe("Error deleting passenger by ID: " + e.getDetailedMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error while deleting passenger: " + e.getMessage());
            throw new AeroDeskException("Unexpected error while deleting passenger.", e, AeroDeskException.ErrorCategory.SYSTEM_ERROR, "DELETE_ERROR", passengerId);
        }
    }

    /**
     * Searches for passengers based on a search term (name, passport number, etc.)
     */
    public List<Passenger> searchPassengers(String searchTerm) throws AeroDeskException {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                throw new AeroDeskException("Search term cannot be empty.", AeroDeskException.ErrorCategory.VALIDATION_ERROR);
            }

            List<Passenger> passengers = passengerDAO.searchPassengers(searchTerm);
            LOGGER.info("Found " + passengers.size() + " passengers for search term: " + searchTerm);
            return passengers;
        } catch (AeroDeskException e) {
            LOGGER.severe("Error searching passengers: " + e.getDetailedMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Unexpected error while searching passengers: " + e.getMessage());
            throw new AeroDeskException("Unexpected error while searching passengers.", e, AeroDeskException.ErrorCategory.SYSTEM_ERROR, "SEARCH_ERROR", searchTerm);
        }
    }

    /**
     * Validates a passenger’s data (checks if data is not null or empty)
     */
    public void validatePassengerData(Passenger passenger) throws AeroDeskException {
        if (passenger == null) {
            throw new AeroDeskException("Passenger data is null.", AeroDeskException.ErrorCategory.VALIDATION_ERROR);
        }

        if (passenger.getFirstName() == null || passenger.getFirstName().trim().isEmpty()) {
            throw new AeroDeskException("First name is required.", AeroDeskException.ErrorCategory.VALIDATION_ERROR);
        }

        if (passenger.getLastName() == null || passenger.getLastName().trim().isEmpty()) {
            throw new AeroDeskException("Last name is required.", AeroDeskException.ErrorCategory.VALIDATION_ERROR);
        }

        if (passenger.getEmail() == null || passenger.getEmail().trim().isEmpty()) {
            throw new AeroDeskException("Email is required.", AeroDeskException.ErrorCategory.VALIDATION_ERROR);
        }
    }
}
