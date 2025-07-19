package com.gatorsoft.aerodeskpro.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents the schedule for a gate in the airport.
 */
public class GateSchedule {

    private int scheduleId;
    private int gateId;
    private int flightId;
    private LocalDateTime scheduledStartTime;
    private LocalDateTime scheduledEndTime;
    private String status;  // e.g. "Scheduled", "Completed", "Cancelled"
    private String notes;

    // ===== Constructors =====
    public GateSchedule() {
    }

    public GateSchedule(int scheduleId, int gateId, int flightId, LocalDateTime scheduledStartTime,
            LocalDateTime scheduledEndTime, String status, String notes) {
        this.scheduleId = scheduleId;
        this.gateId = gateId;
        this.flightId = flightId;
        this.scheduledStartTime = scheduledStartTime;
        this.scheduledEndTime = scheduledEndTime;
        this.status = status;
        this.notes = notes;
    }

    // ===== Getters and Setters =====
    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getGateId() {
        return gateId;
    }

    public void setGateId(int gateId) {
        this.gateId = gateId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public LocalDateTime getScheduledStartTime() {
        return scheduledStartTime;
    }

    public void setScheduledStartTime(LocalDateTime scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }

    public LocalDateTime getScheduledEndTime() {
        return scheduledEndTime;
    }

    public void setScheduledEndTime(LocalDateTime scheduledEndTime) {
        this.scheduledEndTime = scheduledEndTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Method to set the date of the scheduled start and end times while keeping the time part intact
    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        if (this.scheduledStartTime != null) {
            this.scheduledStartTime = LocalDateTime.of(date, this.scheduledStartTime.toLocalTime());
        }

        if (this.scheduledEndTime != null) {
            this.scheduledEndTime = LocalDateTime.of(date, this.scheduledEndTime.toLocalTime());
        }
    }

    @Override
    public String toString() {
        return "GateSchedule{"
                + "scheduleId=" + scheduleId
                + ", gateId=" + gateId
                + ", flightId=" + flightId
                + ", scheduledStartTime=" + scheduledStartTime
                + ", scheduledEndTime=" + scheduledEndTime
                + ", status='" + status + '\''
                + ", notes='" + notes + '\''
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GateSchedule)) {
            return false;
        }
        GateSchedule that = (GateSchedule) o;
        return scheduleId == that.scheduleId;
    }

    @Override
    public int hashCode() {
