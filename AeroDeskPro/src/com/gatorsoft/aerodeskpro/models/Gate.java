package com.gatorsoft.aerodeskpro.models;

import com.gatorsoft.aerodeskpro.entity.GateStatus;
import com.gatorsoft.aerodeskpro.entity.GateType;
import java.util.Objects;

/**
 * Represents a Gate at an airport.
 */
public class Gate {

    private int gateId;
    private String gateNumber;
    private int terminalId;
    private GateType gateType;
    private boolean isAvailable;
    private int capacity;
    private GateStatus status;
    private String createdAt;

   /* // Enum for Gate Type
    public enum GateType {
        DOMESTIC, INTERNATIONAL, BOTH
    }

    // Enum for Gate Status
    public enum GateStatus {
        AVAILABLE, OCCUPIED, MAINTENANCE, CLOSED
    }
*/
    // ===== Constructors =====

    public Gate() {}

    public Gate(int gateId, String gateNumber, int terminalId, GateType gateType, boolean isAvailable, 
                int capacity, GateStatus status, String createdAt) {
        this.gateId = gateId;
        this.gateNumber = gateNumber;
        this.terminalId = terminalId;
        this.gateType = gateType;
        this.isAvailable = isAvailable;
        this.capacity = capacity;
        this.status = status;
        this.createdAt = createdAt;
    }

    // ===== Getters and Setters =====

    public int getGateId() {
        return gateId;
    }

    public void setGateId(int gateId) {
        this.gateId = gateId;
    }

    public String getGateNumber() {
        return gateNumber;
    }

    public void setGateNumber(String gateNumber) {
        this.gateNumber = gateNumber != null ? gateNumber.trim() : null;
    }

    public int getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(int terminalId) {
        this.terminalId = terminalId;
    }

    public GateType getGateType() {
        return gateType;
    }

    public void setGateType(GateType gateType) {
        this.gateType = gateType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public GateStatus getStatus() {
        return status;
    }

    public void setStatus(GateStatus status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // ===== Utility Methods =====

    public boolean isValid() {
        return gateNumber != null && !gateNumber.isBlank() && terminalId > 0;
    }

    @Override
    public String toString() {
        return "Gate{" +
               "gateId=" + gateId +
               ", gateNumber='" + gateNumber + '\'' +
               ", terminalId=" + terminalId +
               ", gateType=" + gateType +
               ", isAvailable=" + isAvailable +
               ", capacity=" + capacity +
               ", status=" + status +
               ", createdAt='" + createdAt + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gate)) return false;
        Gate gate = (Gate) o;
        return gateId == gate.gateId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gateId);
    }
}
