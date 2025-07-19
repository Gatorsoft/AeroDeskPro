package com.gatorsoft.aerodeskpro.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.gatorsoft.aerodeskpro.dao.FlightDAO;
import com.gatorsoft.aerodeskpro.entity.FlightStatus;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException.ErrorCategory;
import com.gatorsoft.aerodeskpro.exceptions.FlightNotFoundException;
import com.gatorsoft.aerodeskpro.models.Flight;

/**
 * Service class for Flight operations. Contains business logic and coordinates
 * between UI and DAO layers.
 */
public class FlightService {

    private static final Logger LOGGER = Logger.getLogger(FlightService.class.getName());
    private final FlightDAO flightDAO;

    // Business constants
    private static final int MIN_FLIGHT_DURATION_MINUTES = 30;
    private static final int MAX_ADVANCE_BOOKING_DAYS = 365;

    public FlightService() {
        this.flightDAO = new FlightDAO();
    }

    // For testing - constructor injection
    public FlightService(FlightDAO flightDAO) {
        this.flightDAO = flightDAO;
    }

    /**
     * Schedules a new flight with business validation
     */
    public boolean scheduleNewFlight(Flight flight) throws AeroDeskException {
        LOGGER.info("Attempting to schedule new flight: " + flight.getFlightNumber());

        // Business validation
        validateNewFlight(flight);

        // Check for duplicate flight numbers on the same day
        if (isFlightNumberConflict(flight)) {
            throw new AeroDeskException("Flight number already exists for the same day:",
                    ErrorCategory.RESOURCE_NOT_FOUND);
        }

        // Set initial status
        flight.setStatus(FlightStatus.SCHEDULED);

        // Save to database
        boolean success = flightDAO.insertFlight(flight);

        if (success) {
            LOGGER.info("Flight scheduled successfully: " + flight.getFlightNumber());
            // Here you could trigger notifications to other systems
            notifyFlightScheduled(flight);
        }

        return success;
    }

    /**
     * Updates flight information with business rules
     */
    public boolean updateFlight(Flight flight) throws AeroDeskException {
        LOGGER.info("Updating flight: " + flight.getFlightNumber());

        // Get current flight to check if updates are allowed
        Flight currentFlight = flightDAO.getFlightById(flight.getFlightId());
        if (currentFlight == null) {
            throw new AeroDeskException(
                    "Flight not found for update: " + flight.getFlightId(),
                    AeroDeskException.ErrorCategory.RESOURCE_NOT_FOUND);

        }

        // Business rule: Can't modify departed or completed flights
        if (!currentFlight.canBeModified()) {
            throw new AeroDeskException(
                    "Cannot modify flight in status: " + currentFlight.getStatus(),
                    AeroDeskException.ErrorCategory.BUSINESS_RULE_VIOLATION);

        }

        // Validate the updated flight
        validateFlightUpdate(flight, currentFlight);

        boolean success = flightDAO.updateFlight(flight);

        if (success) {
            LOGGER.info("Flight updated successfully: " + flight.getFlightNumber());
            notifyFlightUpdated(flight, currentFlight);
        }

        return success;
    }

    /**
     * Updates flight status with business logic
     */
    public boolean updateFlightStatus(int flightId, FlightStatus newStatus)
            throws AeroDeskException {
        LOGGER.info(
                "Updating flight status to " + newStatus + " for flight ID: " + flightId);

        Flight currentFlight = flightDAO.getFlightById(flightId);
        if (currentFlight == null) {
            throw new AeroDeskException(
                    "Flight not found for update: " + currentFlight.getFlightId(),
                    AeroDeskException.ErrorCategory.RESOURCE_NOT_FOUND);
        }

        // Validate status transition
        if (!isValidStatusTransition(currentFlight.getStatus(), newStatus)) {
            throw new AeroDeskException(
                    "Invalid status transition from " + currentFlight.getStatus() + " to "
                    + newStatus,
                    AeroDeskException.ErrorCategory.BUSINESS_RULE_VIOLATION);

        }

        boolean success = flightDAO.updateFlightStatus(flightId, newStatus);

        if (success) {
            LOGGER.info("Flight status updated successfully: "
                    + currentFlight.getFlightNumber());
            // Trigger status-specific business logic
            handleStatusChange(currentFlight, newStatus);
        }

        return success;
    }

    /**
     * Cancels a flight
     */
    public boolean cancelFlight(int flightId, String reason) throws AeroDeskException {
        LOGGER.info("Cancelling flight with ID: " + flightId);

        Flight flight = flightDAO.getFlightById(flightId);
        if (flight == null) {
            throw new FlightNotFoundException(flightId);

        }

        // Business rule: Can only cancel scheduled or delayed flights
        if (flight.getStatus() != FlightStatus.SCHEDULED
                && flight.getStatus() != FlightStatus.DELAYED) {
            throw new AeroDeskException(
                    "Cannot cancel flight in status: " + flight.getStatus(),
                    ErrorCategory.BUSINESS_RULE_VIOLATION);
        }

        boolean success = flightDAO.updateFlightStatus(flightId, FlightStatus.CANCELLED);

        if (success) {
            LOGGER.info("Flight cancelled successfully: " + flight.getFlightNumber());
            // Here you would typically:
            // - Notify passengers
            // - Release gate assignment
            // - Handle refunds
            // - Update crew schedules
            handleFlightCancellation(flight, reason);
        }

        return success;
    }

    /**
     * Assigns a gate to a flight
     */
    public boolean assignGate(int flightId, String gateNumber) throws AeroDeskException {
        LOGGER.info("Assigning gate " + gateNumber + " to flight ID: " + flightId);

        Flight flight = flightDAO.getFlightById(flightId);
        if (flight == null) {
            throw new FlightNotFoundException(flightId);
        }

        // Business rule: Only assign gates to flights that need them
        if (!flight.requiresGate()) {
            throw new AeroDeskException(
                    "Flight does not require gate assignment in status:"
                    + flight.getStatus(),
                    ErrorCategory.BUSINESS_RULE_VIOLATION);
        }

        // Here you would typically check gate availability
        // For now, we'll assume the gate is available
        boolean success = flightDAO.updateFlightGate(flightId, gateNumber);

        if (success) {
            LOGGER.info("Gate assigned successfully: " + gateNumber + " to flight "
                    + flight.getFlightNumber());
            notifyGateAssignment(flight, gateNumber);
        }

        return success;
    }

    /**
     * Retrieves all flights
     */
    public List<Flight> getAllFlights() throws AeroDeskException {
        return flightDAO.getAllFlights();
    }

    /**
     * Finds flight by ID
     */
    public Flight getFlightById(int flightId) throws AeroDeskException {
        return flightDAO.getFlightById(flightId);
    }

    /**
     * Finds flight by number
     */
    public Flight getFlightByNumber(String flightNumber) throws AeroDeskException {
        return flightDAO.getFlightByNumber(flightNumber);
    }

    /**
     * Gets flights for today
     */
    public List<Flight> getTodaysFlights() throws AeroDeskException {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        return flightDAO.getFlightsByDateRange(startOfDay, endOfDay);
    }

    /**
     * Gets flights by status
     */
    public List<Flight> getFlightsByStatus(FlightStatus status) throws AeroDeskException {
        return flightDAO.getFlightsByStatus(status);
    }

    /**
     * Gets active flights (not cancelled or completed)
     */
    public List<Flight> getActiveFlights() throws AeroDeskException {
        return getAllFlights().stream().filter(Flight::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Gets delayed flights
     */
    public List<Flight> getDelayedFlights() throws AeroDeskException {
        return getFlightsByStatus(FlightStatus.DELAYED);
    }

    /**
     * Delays a flight
     */
    public boolean delayFlight(int flightId, LocalDateTime newDepartureTime)
            throws AeroDeskException {
        LOGGER.info("Delaying flight ID: " + flightId);

        Flight flight = flightDAO.getFlightById(flightId);
        if (flight == null) {
            throw new FlightNotFoundException(flightId);
        }

        // Business rule: Can only delay scheduled flights
        if (flight.getStatus() != FlightStatus.SCHEDULED) {
            throw new AeroDeskException(
                    "Can only delay scheduled flights. Current status: "
                    + "flight.getStatus() ",
                    ErrorCategory.BUSINESS_RULE_VIOLATION);
        }

        // Update flight times
        flight.setDepartureTime(newDepartureTime);
        // Assume arrival time is adjusted proportionally
        long originalDuration = flight.getDurationMinutes();
        flight.setArrivalTime(newDepartureTime.plusMinutes(originalDuration));
        flight.setStatus(FlightStatus.DELAYED);

        boolean success = flightDAO.updateFlight(flight);

        if (success) {
            LOGGER.info("Flight delayed successfully: " + flight.getFlightNumber());
            notifyFlightDelayed(flight);
        }

        return success;
    }

    // ===== PRIVATE VALIDATION METHODS =====
    /**
     * Validates a new flight before scheduling
     */
    private void validateNewFlight(Flight flight) throws AeroDeskException {
        if (flight == null) {
            throw new AeroDeskException("Flight cannot be null",
                    ErrorCategory.DATABASE_ERROR);
        }

        if (!flight.isValid()) {
            throw new AeroDeskException("Flight data is incomplete or invalid",
                    ErrorCategory.DATABASE_ERROR);
        }

        // Business validation rules
        validateFlightTimes(flight);
        validateFlightRoute(flight);
        validateCapacity(flight);
        validateAdvanceBooking(flight);
    }

    /**
     * Validates flight update
     */
    private void validateFlightUpdate(Flight newFlight, Flight currentFlight)
            throws AeroDeskException {
        validateNewFlight(newFlight);

        // Additional validation for updates
        if (!newFlight.getFlightNumber().equals(currentFlight.getFlightNumber())) {
            // If changing flight number, check for conflicts
            if (isFlightNumberConflict(newFlight)) {
                throw new AeroDeskException(
                        "New flight number conflicts with existing flight",
                        ErrorCategory.BUSINESS_RULE_VIOLATION);
            }
        }
    }

    /**
     * Validates flight times
     */
    private void validateFlightTimes(Flight flight) throws AeroDeskException {
        LocalDateTime now = LocalDateTime.now();

        // Departure time must be in the future
        if (flight.getDepartureTime().isBefore(now.plusMinutes(30))) {
            throw new AeroDeskException(
                    "Departure time must be at least 30 minutes in the future",
                    ErrorCategory.BUSINESS_RULE_VIOLATION);
        }

        // Arrival time must be after departure time
        if (flight.getArrivalTime().isBefore(flight.getDepartureTime())) {
            throw new AeroDeskException("Arrival time must be after departure time",
                    ErrorCategory.BUSINESS_RULE_VIOLATION);
        }

        // Minimum flight duration
        if (flight.getDurationMinutes() < MIN_FLIGHT_DURATION_MINUTES) {
            throw new AeroDeskException("Flight duration must be at least "
                    + MIN_FLIGHT_DURATION_MINUTES + " minutes",
                    ErrorCategory.BUSINESS_RULE_VIOLATION);
        }
    }

    /**
     * Validates flight route
     */
    private void validateFlightRoute(Flight flight) throws AeroDeskException {
        if (flight.getOrigin().equalsIgnoreCase(flight.getDestination())) {
            throw new AeroDeskException("Origin and destination cannot be the same",
                    ErrorCategory.DATABASE_ERROR);
        }
    }

    /**
     * Validates aircraft capacity
     */
    private void validateCapacity(Flight flight) throws AeroDeskException {
        if (flight.getCapacity() < 1 || flight.getCapacity() > 850) { // Max for A380
            throw new AeroDeskException(
                    "Invalid aircraft capacity: " + flight.getCapacity(),
                    ErrorCategory.DATABASE_ERROR);
        }
    }

    /**
     * Validates advance booking window
     */
    private void validateAdvanceBooking(Flight flight) throws AeroDeskException {
        LocalDateTime maxAdvanceDate = LocalDateTime.now()
                .plusDays(MAX_ADVANCE_BOOKING_DAYS);
        if (flight.getDepartureTime().isAfter(maxAdvanceDate)) {
            throw new AeroDeskException("Cannot schedule flights more than "
                    + MAX_ADVANCE_BOOKING_DAYS + " days in advance",
                    ErrorCategory.BUSINESS_RULE_VIOLATION);
        }
    }

    /**
     * Checks for flight number conflicts on the same day
     */
    private boolean isFlightNumberConflict(Flight flight) throws AeroDeskException {
        Flight existingFlight = flightDAO.getFlightByNumber(flight.getFlightNumber());
        if (existingFlight != null
                && existingFlight.getFlightId() != flight.getFlightId()) {
            LocalDate flightDate = flight.getDepartureTime().toLocalDate();
            LocalDate existingDate = existingFlight.getDepartureTime().toLocalDate();
            return flightDate.equals(existingDate);
        }
        return false;
    }

    /**
     * Validates status transition
     */
    private boolean isValidStatusTransition(FlightStatus current, FlightStatus target) {
        switch (current) {
            case SCHEDULED:
                return target == FlightStatus.DELAYED || target == FlightStatus.BOARDING
                        || target == FlightStatus.CANCELLED;
            case DELAYED:
                return target == FlightStatus.BOARDING || target == FlightStatus.CANCELLED
                        || target == FlightStatus.SCHEDULED;
            case BOARDING:
                return target == FlightStatus.DEPARTED || target == FlightStatus.DELAYED
                        || target == FlightStatus.CANCELLED;
            case DEPARTED:
                return target == FlightStatus.IN_FLIGHT;
            case IN_FLIGHT:
                return target == FlightStatus.ARRIVED;
            case ARRIVED:
                return target == FlightStatus.COMPLETED;
            case COMPLETED:
            case CANCELLED:
                return false; // Terminal states
            default:
                return false;
        }
    }

    // ===== NOTIFICATION METHODS =====
    /**
     * Handles flight scheduled notification
     */
    private void notifyFlightScheduled(Flight flight) {
        LOGGER.info("Notification: Flight scheduled - " + flight.getFlightNumber());
        // Here you would integrate with notification systems
        // - Send emails to relevant parties
        // - Update display boards
        // - Notify crew scheduling system
    }

    /**
     * Handles flight updated notification
     */
    private void notifyFlightUpdated(Flight newFlight, Flight oldFlight) {
        LOGGER.info("Notification: Flight updated - " + newFlight.getFlightNumber());
        // Compare changes and notify relevant parties
    }

    /**
     * Handles flight delayed notification
     */
    private void notifyFlightDelayed(Flight flight) {
        LOGGER.info("Notification: Flight delayed - " + flight.getFlightNumber());
        // Notify passengers, crew, ground services
    }

    /**
     * Handles gate assignment notification
     */
    private void notifyGateAssignment(Flight flight, String gateNumber) {
        LOGGER.info("Notification: Gate assigned - " + flight.getFlightNumber()
                + " to gate " + gateNumber);
        // Update display boards, notify ground crew
    }

    /**
     * Handles flight cancellation
     */
    private void handleFlightCancellation(Flight flight, String reason) {
        LOGGER.info("Handling cancellation: " + flight.getFlightNumber() + " - Reason: "
                + reason);
        // Business logic for cancellation:
        // - Release gate
        // - Notify passengers
        // - Handle refunds
        // - Update crew schedules
    }

    /**
     * Handles status change business logic
     */
    private void handleStatusChange(Flight flight, FlightStatus newStatus) {
        switch (newStatus) {
            case BOARDING:
                LOGGER.info("Flight boarding started: " + flight.getFlightNumber());
                // Notify ground crew, update passenger displays
                break;
            case DEPARTED:
                LOGGER.info("Flight departed: " + flight.getFlightNumber());
                // Release gate, notify air traffic control
                break;
            case ARRIVED:
                LOGGER.info("Flight arrived: " + flight.getFlightNumber());
                // Assign baggage claim, notify ground services
                break;
            case COMPLETED:
                LOGGER.info("Flight completed: " + flight.getFlightNumber());
                // Final cleanup, generate reports
                break;
            default:
                // Handle other status changes
                break;
        }
    }
}
