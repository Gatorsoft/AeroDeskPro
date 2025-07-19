package com.gatorsoft.aerodeskpro.dao;

import com.gatorsoft.aerodeskpro.database.DatabaseConnection;
import com.gatorsoft.aerodeskpro.entity.GateStatus;
import com.gatorsoft.aerodeskpro.entity.GateType;
import com.gatorsoft.aerodeskpro.models.Gate;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GateDAO {

    private static final Logger LOGGER = Logger.getLogger(GateDAO.class.getName());

    // SQL Queries
    private static final String SELECT_GATE_BY_ID = "SELECT * FROM gates WHERE gate_id = ?";
    private static final String SELECT_GATES_BY_TERMINAL = "SELECT * FROM gates WHERE terminal_id = ?";
    private static final String SELECT_AVAILABLE_GATES = "SELECT * FROM gates WHERE is_available = 1";
    private static final String SELECT_GATES_BY_STATUS = "SELECT * FROM gates WHERE status = ?";
    private static final String INSERT_GATE = "INSERT INTO gates (gate_number, terminal_id, gate_type, is_available, capacity) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_GATE = "UPDATE gates SET gate_number = ?, terminal_id = ?, gate_type = ?, is_available = ?, capacity = ? WHERE gate_id = ?";
    private static final String DELETE_GATE = "DELETE FROM gates WHERE gate_id = ?";
    private static final String SELECT_AVAILABLE_GATES_FOR_TIMESLOT = "SELECT g.* FROM gates g WHERE g.is_available = 1 AND g.gate_id NOT IN (SELECT gate_id FROM gate_assignments WHERE (estimated_start_time <= ? AND estimated_end_time >= ?))";

    /**
     * Finds a gate by its ID.
     */
    public Gate findById(int gateId) throws AeroDeskException {
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(SELECT_GATE_BY_ID)) {
            statement.setInt(1, gateId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToGate(resultSet);
                } else {
                    return null; // No gate found
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding gate by ID: " + gateId, e);
            throw new AeroDeskException("Failed to find gate by ID", e);
        }
    }

    /**
     * Finds gates by terminal ID.
     */
    public List<Gate> findByTerminal(int terminalId) throws AeroDeskException {
        List<Gate> gates = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(SELECT_GATES_BY_TERMINAL)) {
            statement.setInt(1, terminalId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    gates.add(mapResultSetToGate(resultSet));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding gates by terminal ID: " + terminalId, e);
            throw new AeroDeskException("Failed to find gates by terminal ID", e);
        }
        return gates;
    }

    /**
     * Finds all available gates.
     */
    public List<Gate> findAvailableGates() throws AeroDeskException {
        List<Gate> gates = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(SELECT_AVAILABLE_GATES)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    gates.add(mapResultSetToGate(resultSet));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding available gates", e);
            throw new AeroDeskException("Failed to find available gates", e);
        }
        return gates;
    }

    /**
     * Finds gates by their status.
     */
    public List<Gate> findByStatus(GateStatus status) throws AeroDeskException {
        List<Gate> gates = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(SELECT_GATES_BY_STATUS)) {
            statement.setString(1, status.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    gates.add(mapResultSetToGate(resultSet));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding gates by status: " + status, e);
            throw new AeroDeskException("Failed to find gates by status", e);
        }
        return gates;
    }

    public Gate save(Gate gate) throws AeroDeskException {
        if (gate == null || !gate.isValid()) {
            throw new AeroDeskException("Invalid gate data", AeroDeskException.ErrorCategory.DATABASE_ERROR);
        }

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(INSERT_GATE, Statement.RETURN_GENERATED_KEYS)) {

            // Gate Number
            statement.setString(1, gate.getGateNumber());

            // Terminal ID
            statement.setInt(2, gate.getTerminalId());

            // Gate Type - Convert GateType enum to string and ensure it is lowercase
            if (gate.getGateType() != null) {
                statement.setString(3, gate.getGateType().name().toLowerCase()); // Convert to lowercase before inserting
            } else {
                statement.setString(3, GateType.BOTH.name().toLowerCase()); // Default to "both" if null
            }

            // Gate Availability
            statement.setBoolean(4, gate.isAvailable());

            // Gate Capacity
            statement.setInt(5, gate.getCapacity());

            // Execute SQL
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve generated keys (auto-incremented gateId)
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        gate.setGateId(generatedKeys.getInt(1)); // Set the generated gateId
                    }
                }
                return gate;
            } else {
                return null; // Return null if no rows were affected
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving gate: SQLState: " + e.getSQLState() + ", ErrorCode: " + e.getErrorCode(), e);
            throw new AeroDeskException("Failed to save gate", e);
        }
    }

    /**
     * Updates an existing gate.
     */
    public Gate update(Gate gate) throws AeroDeskException {
        if (gate == null || gate.getGateId() <= 0 || !gate.isValid()) {
            throw new AeroDeskException("Invalid gate data for update", AeroDeskException.ErrorCategory.DATABASE_ERROR);
        }

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(UPDATE_GATE)) {
            statement.setString(1, gate.getGateNumber());
            statement.setInt(2, gate.getTerminalId());
            if (gate.getGateType() != null) {
                statement.setString(3, gate.getGateType().name().toLowerCase()); // Convert to lowercase before inserting
            } else {
                statement.setString(3, GateType.BOTH.name().toLowerCase()); // Default to "both" if null
            }
            statement.setBoolean(4, gate.isAvailable());
            statement.setInt(5, gate.getCapacity());
            statement.setInt(6, gate.getGateId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return gate;
            } else {
                return null;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating gate", e);
            throw new AeroDeskException("Failed to update gate", e);
        }
    }

    /**
     * Deletes a gate from the database.
     */
    public boolean delete(int gateId) throws AeroDeskException {
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(DELETE_GATE)) {
            statement.setInt(1, gateId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting gate", e);
            throw new AeroDeskException("Failed to delete gate", e);
        }
    }

    /**
     * Finds available gates for a specific time slot.
     */
    public List<Gate> findAvailableGatesForTimeSlot(LocalDateTime start, LocalDateTime end) throws AeroDeskException {
        List<Gate> gates = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(SELECT_AVAILABLE_GATES_FOR_TIMESLOT)) {
            statement.setTimestamp(1, Timestamp.valueOf(start));
            statement.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    gates.add(mapResultSetToGate(resultSet));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding available gates for time slot", e);
            throw new AeroDeskException("Failed to find available gates for time slot", e);
        }
        return gates;
    }

    /**
     * Helper method to map a ResultSet to a Gate object.
     */
    private Gate mapResultSetToGate(ResultSet resultSet) throws SQLException {
        Gate gate = new Gate();
        gate.setGateId(resultSet.getInt("gate_id"));
        gate.setGateNumber(resultSet.getString("gate_number"));
        gate.setTerminalId(resultSet.getInt("terminal_id"));

        // Get the gate_type from the database (which is a String) and convert it to GateType enum
        String gateTypeStr = resultSet.getString("gate_type");

        // Convert the string to an enum, ensuring it's case-insensitive
        if (gateTypeStr != null) {
            try {
                gate.setGateType(GateType.valueOf(gateTypeStr.toUpperCase())); // Convert to upper case to match enum names
            } catch (IllegalArgumentException e) {
                // Handle case where the value is invalid
                gate.setGateType(GateType.BOTH); // Default to GateType.BOTH if invalid
            }
        }

        gate.setAvailable(resultSet.getBoolean("is_available"));
        gate.setCapacity(resultSet.getInt("capacity"));

        // Optionally, you can also set the status and createdAt if needed.
        // gate.setStatus(GateStatus.valueOf(resultSet.getString("status")));
        // gate.setCreatedAt(resultSet.getString("created_at"));
        return gate;
    }
}
