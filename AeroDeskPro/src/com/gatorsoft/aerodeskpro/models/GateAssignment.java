package com.gatorsoft.aerodeskpro.models;

import java.time.LocalDateTime;
//
public class GateAssignment {
    private int assignmentId;               // assignment_id (Primary Key)
    private int flightId;                   // flight_id (Foreign Key)
    private int gateId;                     // gate_id (Foreign Key)
    private LocalDateTime assignedAt;       // assigned_at
    private LocalDateTime estimatedStartTime; // estimated_start_time
    private LocalDateTime estimatedEndTime;   // estimated_end_time
    private LocalDateTime actualStartTime;    // actual_start_time
    private LocalDateTime actualEndTime;      // actual_end_time
    private String assignmentStatus;        // assignment_status
    private String remarks;                 // remarks

    // Constructors
    public GateAssignment() {
        this.assignedAt = LocalDateTime.now();
        this.assignmentStatus = "ASSIGNED";
    }

    public GateAssignment(int flightId, int gateId, LocalDateTime estimatedStartTime, 
                         LocalDateTime estimatedEndTime) {
        this();
        this.flightId = flightId;
        this.gateId = gateId;
        this.estimatedStartTime = estimatedStartTime;
        this.estimatedEndTime = estimatedEndTime;
    }

    // Getters and Setters
    public int getAssignmentId() { 
        return assignmentId; 
    }
    
    public void setAssignmentId(int assignmentId) { 
        this.assignmentId = assignmentId; 
    }

    public int getFlightId() { 
        return flightId; 
    }
    
    public void setFlightId(int flightId) { 
        this.flightId = flightId; 
    }

    public int getGateId() { 
        return gateId; 
    }
    
    public void setGateId(int gateId) { 
        this.gateId = gateId; 
    }

    public LocalDateTime getAssignedAt() { 
        return assignedAt; 
    }
    
    public void setAssignedAt(LocalDateTime assignedAt) { 
        this.assignedAt = assignedAt; 
    }

    public LocalDateTime getEstimatedStartTime() { 
        return estimatedStartTime; 
    }
    
    public void setEstimatedStartTime(LocalDateTime estimatedStartTime) { 
        this.estimatedStartTime = estimatedStartTime; 
    }

    public LocalDateTime getEstimatedEndTime() { 
        return estimatedEndTime; 
    }
    
    public void setEstimatedEndTime(LocalDateTime estimatedEndTime) { 
        this.estimatedEndTime = estimatedEndTime; 
    }

    public LocalDateTime getActualStartTime() { 
        return actualStartTime; 
    }
    
    public void setActualStartTime(LocalDateTime actualStartTime) { 
        this.actualStartTime = actualStartTime; 
    }

    public LocalDateTime getActualEndTime() { 
        return actualEndTime; 
    }
    
    public void setActualEndTime(LocalDateTime actualEndTime) { 
        this.actualEndTime = actualEndTime; 
    }

    public String getAssignmentStatus() { 
        return assignmentStatus; 
    }
    
    public void setAssignmentStatus(String assignmentStatus) { 
        this.assignmentStatus = assignmentStatus; 
    }

    public String getRemarks() { 
        return remarks; 
    }
    
    public void setRemarks(String remarks) { 
        this.remarks = remarks; 
    }

    // Business logic methods
    public boolean isActive() {
        return "ASSIGNED".equals(assignmentStatus) || "IN_USE".equals(assignmentStatus);
    }

    public boolean isCompleted() {
        return actualEndTime != null;
    }

    public long getDurationMinutes() {
        if (actualStartTime != null && actualEndTime != null) {
            return java.time.Duration.between(actualStartTime, actualEndTime).toMinutes();
        } else if (estimatedStartTime != null && estimatedEndTime != null) {
            return java.time.Duration.between(estimatedStartTime, estimatedEndTime).toMinutes();
        }
        return 0;
    }

    @Override
    public String toString() {
        return "GateAssignment{" +
                "assignmentId=" + assignmentId +
                ", flightId=" + flightId +
                ", gateId=" + gateId +
                ", assignmentStatus='" + assignmentStatus + '\'' +
                ", estimatedStartTime=" + estimatedStartTime +
                ", estimatedEndTime=" + estimatedEndTime +
                '}';
    }
}