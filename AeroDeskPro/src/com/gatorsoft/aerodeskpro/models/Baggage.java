package com.gatorsoft.aerodeskpro.models;

import com.gatorsoft.aerodeskpro.entity.BaggageStatus;
import com.gatorsoft.aerodeskpro.entity.BaggageType;
import com.gatorsoft.aerodeskpro.entity.Priority;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.text.ParseException;
import java.util.Objects;

/**
 * Baggage model that matches the database schema for the 'baggage' table.
 * This class represents baggage items in the airport management system.
 */
public class Baggage {

    // Fields matching database schema exactly
    private int baggageId;              // baggage_id (Primary Key, AUTO_INCREMENT)
    private String baggageTag;          // baggage_tag VARCHAR(20) NOT NULL UNIQUE
    private int bookingId;              // booking_id INT NOT NULL (Foreign Key)
    private BigDecimal weight;          // weight DECIMAL(5,2) NOT NULL
    private String dimensions;          // dimensions VARCHAR(50) NULL
    private BaggageType baggageType;    // baggage_type ENUM DEFAULT 'checked'
    private BaggageStatus status;       // status ENUM DEFAULT 'registered'
    private String currentLocation;     // current_location VARCHAR(100) NULL
    private String destinationAirport;  // destination_airport VARCHAR(10) NULL
    private Priority priority;          // priority ENUM DEFAULT 'normal'
    private LocalDateTime createdAt;    // created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    private LocalDateTime updatedAt;    // updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    private Integer handledBy;          // handled_by INT NULL (Foreign Key to staff.user_id)

    // Constructors
    public Baggage() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = BaggageStatus.REGISTERED;     // Default as per DB
        this.baggageType = BaggageType.CHECKED;     // Default as per DB
        this.priority = Priority.NORMAL;            // Default as per DB
    }

    /**
     * Constructor with required fields (matching NOT NULL constraints)
     */
    public Baggage(String baggageTag, int bookingId, BigDecimal weight) {
        this();
        this.setBaggageTag(baggageTag);  // Use setter for validation
        this.setBookingId(bookingId);    // Use setter for validation
        this.setWeight(weight);          // Use setter for validation
    }

    /**
     * Full constructor for creating baggage with all details
     */
    public Baggage(String baggageTag, int bookingId, BigDecimal weight,
                  String dimensions, BaggageType baggageType, String destinationAirport) {
        this(baggageTag, bookingId, weight);
        this.dimensions = dimensions;
        this.baggageType = baggageType != null ? baggageType : BaggageType.CHECKED;
        this.destinationAirport = destinationAirport;
    }

    // Getters and Setters with proper validation
    public int getBaggageId() {
        return baggageId;
    }

    public void setBaggageId(int baggageId) {
        this.baggageId = baggageId;
    }

    public String getBaggageTag() {
        return baggageTag;
    }

    public void setBaggageTag(String baggageTag) {
        if (baggageTag == null || baggageTag.trim().isEmpty()) {
            throw new IllegalArgumentException("Baggage tag cannot be null or empty");
        }
        if (baggageTag.length() > 20) {
            throw new IllegalArgumentException("Baggage tag cannot exceed 20 characters");
        }
        this.baggageTag = baggageTag.trim().toUpperCase();
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Weight must be a positive value");
        }
        this.weight = weight;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        if (bookingId <= 0) {
            throw new IllegalArgumentException("Booking ID must be greater than 0");
        }
        this.bookingId = bookingId;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        if (dimensions != null && dimensions.length() > 50) {
            throw new IllegalArgumentException("Dimensions cannot exceed 50 characters");
        }
        this.dimensions = dimensions;
    }

    public BaggageType getBaggageType() {
        return baggageType;
    }

    public void setBaggageType(BaggageType baggageType) {
        this.baggageType = baggageType != null ? baggageType : BaggageType.CHECKED;
    }

    public BaggageStatus getStatus() {
        return status;
    }

    public void setStatus(BaggageStatus status) {
        this.status = status != null ? status : BaggageStatus.REGISTERED;
        this.updatedAt = LocalDateTime.now(); // Update timestamp when status changes
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        if (currentLocation != null && currentLocation.length() > 100) {
            throw new IllegalArgumentException("Current location cannot exceed 100 characters");
        }
        this.currentLocation = currentLocation;
        this.updatedAt = LocalDateTime.now(); // Update timestamp when location changes
    }

    public String getDestinationAirport() {
        return destinationAirport;
    }

    public void setDestinationAirport(String destinationAirport) {
        if (destinationAirport != null && destinationAirport.length() > 10) {
            throw new IllegalArgumentException("Destination airport cannot exceed 10 characters");
        }
        this.destinationAirport = destinationAirport;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority != null ? priority : Priority.NORMAL;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(Integer handledBy) {
        this.handledBy = handledBy;
    }

    // Business logic methods
    public boolean isOverweight(BigDecimal maxWeight) {
        if (weight == null || maxWeight == null) {
            System.out.println("One of the values is null.");
            return false;
        }

        // Compare the BigDecimal weight with the maxWeight
        return weight.compareTo(maxWeight) > 0;
    }

    public boolean isPriorityBaggage() {
        return priority == Priority.PRIORITY || priority == Priority.RUSH;
    }

    public boolean isLostOrDelayed() {
        return status == BaggageStatus.LOST || status == BaggageStatus.DELAYED;
    }

    public void updateStatus(BaggageStatus newStatus, String location, Integer handledByStaff) {
        this.status = newStatus != null ? newStatus : this.status;
        this.currentLocation = location;
        this.handledBy = handledByStaff;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isValid() {
        try {
            // Check if baggageTag and other values are valid
            return baggageTag != null && !baggageTag.trim().isEmpty() &&
                   bookingId > 0 &&
                   weight != null && weight.compareTo(BigDecimal.ZERO) > 0 &&  // Ensure weight is valid
                   baggageType != null &&
                   status != null &&
                   priority != null;
        } catch (Exception e) {
            System.out.println("Error during validation: " + e.getMessage());
            return false;
        }
    }

    public String getTypeDisplayName() {
        if (baggageType == null) return "Unknown";
        return baggageType.name().replace("_", " ").toLowerCase();
    }

    @Override
    public String toString() {
        return "Baggage{" +
                "baggageId=" + baggageId +
                ", baggageTag='" + baggageTag + '\'' +
                ", bookingId=" + bookingId +
                ", weight=" + weight +
                ", baggageType=" + baggageType +
                ", status=" + status +
                ", currentLocation='" + currentLocation + '\'' +
                ", destinationAirport='" + destinationAirport + '\'' +
                ", priority=" + priority +
                ", handledBy=" + handledBy +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Baggage that = (Baggage) o;
        return baggageId == that.baggageId && 
               Objects.equals(baggageTag, that.baggageTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baggageId, baggageTag);
    }
}
