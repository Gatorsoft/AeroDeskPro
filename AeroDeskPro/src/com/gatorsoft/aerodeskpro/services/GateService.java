package com.gatorsoft.aerodeskpro.services;

import com.gatorsoft.aerodeskpro.dao.GateDAO;
import com.gatorsoft.aerodeskpro.entity.GateStatus;
import com.gatorsoft.aerodeskpro.entity.GateType;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException;
import com.gatorsoft.aerodeskpro.models.Gate;
import com.gatorsoft.aerodeskpro.models.GateSchedule;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GateService {
    
    private GateDAO gateDAO;
    private Logger logger = Logger.getLogger(GateService.class.getName());
    
    // Constructor
    public GateService(GateDAO gateDAO) {
        this.gateDAO = gateDAO;
    }

    /**
     * Returns a list of available gates.
     * 
     * @return List of available gates.
     */
    public List<Gate> getAvailableGates() throws AeroDeskException {
        try {
            return gateDAO.findAvailableGates();
        } catch (AeroDeskException e) {
            logger.log(Level.SEVERE, "Error fetching available gates", e);
            throw new AeroDeskException("Failed to fetch available gates", AeroDeskException.ErrorCategory.DATABASE_ERROR);
        }
    }

    /**
     * Assign a gate to a flight based on gate type.
     * 
     * @param flightId The ID of the flight.
     * @param gateType The type of gate (Domestic, International, etc.).
     * @return Assigned Gate.
     */
    public Gate assignGateToFlight(int flightId, GateType gateType) throws AeroDeskException {
        try {
            // Use gateDAO to find available gates of the specified type
            List<Gate> availableGates = gateDAO.findAvailableGates(); // You could also filter this based on gateType
            
            if (availableGates.isEmpty()) {
                throw new AeroDeskException("No available gates of type " + gateType, AeroDeskException.ErrorCategory.BUSINESS_RULE_VIOLATION);
            }

            // Assign the first available gate (this can be customized further)
            Gate gate = availableGates.get(0);
            gate.setAvailable(false); // Mark gate as occupied
            gateDAO.update(gate); // Update the gate's availability in the database
            
            // Log assignment
            logger.log(Level.INFO, "Assigned gate " + gate.getGateNumber() + " to flight " + flightId);
            
            return gate;
        } catch (AeroDeskException e) {
            logger.log(Level.SEVERE, "Error assigning gate to flight", e);
            throw new AeroDeskException("Failed to assign gate to flight", e, AeroDeskException.ErrorCategory.SYSTEM_ERROR, "ASSIGN_GATE_ERROR");
        }
    }

    /**
     * Updates the status of a specific gate.
     * 
     * @param gateId The ID of the gate to update.
     * @param status The new status of the gate (Available, Occupied, Maintenance, etc.).
     */
    public void updateGateStatus(int gateId, GateStatus status) throws AeroDeskException {
        try {
            // Use gateDAO to find the gate by its ID
            Gate gate = gateDAO.findById(gateId);
            if (gate == null) {
                throw new AeroDeskException("Gate not found with ID: " + gateId, AeroDeskException.ErrorCategory.RESOURCE_NOT_FOUND);
            }

            gate.setStatus(status);
            gateDAO.update(gate); // Update the gate status in the database
            
            // Log the status update
            logger.log(Level.INFO, "Updated gate " + gate.getGateNumber() + " status to " + status);
        } catch (AeroDeskException e) {
            logger.log(Level.SEVERE, "Error updating gate status", e);
            throw new AeroDeskException("Failed to update gate status", e, AeroDeskException.ErrorCategory.SYSTEM_ERROR, "UPDATE_GATE_STATUS");
        }
    }

    /**
     * Fetch all gates by terminal ID.
     * 
     * @param terminalId The terminal ID to filter gates.
     * @return List of gates in the terminal.
     */
    public List<Gate> getGatesByTerminal(int terminalId) throws AeroDeskException {
        try {
            // Use gateDAO to fetch gates by terminal
            return gateDAO.findByTerminal(terminalId);
        } catch (AeroDeskException e) {
            logger.log(Level.SEVERE, "Error fetching gates by terminal", e);
            throw new AeroDeskException("Failed to fetch gates by terminal", e, AeroDeskException.ErrorCategory.SYSTEM_ERROR, "FETCH_GATES_BY_TERMINAL");
        }
    }

    /**
     * Get the schedule of a gate for a specific date.
     * 
     * @param gateId The ID of the gate.
     * @param date The date to retrieve the schedule for.
     * @return GateSchedule object containing gate availability for the given date.
     */
    public GateSchedule getGateSchedule(int gateId, LocalDate date) throws AeroDeskException {
        try {
            // Use gateDAO to get the schedule
            return gateDAO.findGateSchedule(gateId, date);
        } catch (AeroDeskException e) {
            logger.log(Level.SEVERE, "Error fetching gate schedule", e);
            throw new AeroDeskException("Failed to fetch gate schedule", e, AeroDeskException.ErrorCategory.SYSTEM_ERROR, "FETCH_GATE_SCHEDULE");
        }
    }

    /**
     * Validate that a gate can be assigned to a flight.
     * 
     * @param gateId The gate ID.
     * @param flightId The flight ID.
     */
    public void validateGateAssignment(int gateId, int flightId) throws AeroDeskException {
        try {
            // Use gateDAO to find the gate by its ID
            Gate gate = gateDAO.findById(gateId);
            if (gate == null) {
                throw new AeroDeskException("Gate not found for ID: " + gateId, AeroDeskException.ErrorCategory.RESOURCE_NOT_FOUND);
            }

            // Check if the gate is available
            if (!gate.isAvailable()) {
                throw new AeroDeskException("Gate " + gate.getGateNumber() + " is not available.", AeroDeskException.ErrorCategory.BUSINESS_RULE_VIOLATION);
            }

            // Additional validation logic can be added here, such as ensuring gate capacity isn't exceeded, etc.
        } catch (AeroDeskException e) {
            logger.log(Level.SEVERE, "Error validating gate assignment", e);
            throw new AeroDeskException("Failed to validate gate assignment", e, AeroDeskException.ErrorCategory.SYSTEM_ERROR, "VALIDATE_GATE_ASSIGNMENT");
        }
    }
}
