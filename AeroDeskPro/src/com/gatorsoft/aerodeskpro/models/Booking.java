package com.gatorsoft.aerodeskpro.models;

import com.gatorsoft.aerodeskpro.entity.BookingClass;
import com.gatorsoft.aerodeskpro.entity.BookingStatus;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a booking for a passenger on a flight.
 */
public class Booking {

    private int bookingId;
    private String bookingReference;
    private int passengerId;
    private int flightId;
    private String seatNumber;
    private BookingClass bookingClass;
    private BookingStatus bookingStatus;
    private LocalDateTime checkInTime;
    private boolean boardingPassPrinted;
    private Integer checkedInBy; // Nullable (because staff who checked-in may be unknown)
    private String specialRequests;
    private String mealPreference;
    private LocalDateTime createdAt;

    // ===== Constructors =====
    public Booking() {
    }

    public Booking(int bookingId, String bookingReference, int passengerId, int flightId, String seatNumber,
            BookingClass bookingClass, BookingStatus bookingStatus, LocalDateTime checkInTime, boolean boardingPassPrinted,
            Integer checkedInBy, String specialRequests, String mealPreference, LocalDateTime createdAt) {
        this.bookingId = bookingId;
        this.bookingReference = bookingReference;
        this.passengerId = passengerId;
        this.flightId = flightId;
        this.seatNumber = seatNumber;
        this.bookingClass = bookingClass;
        this.bookingStatus = bookingStatus;
        this.checkInTime = checkInTime;
        this.boardingPassPrinted = boardingPassPrinted;
        this.checkedInBy = checkedInBy;
        this.specialRequests = specialRequests;
        this.mealPreference = mealPreference;
        this.createdAt = createdAt;
    }

    // ===== Getters and Setters =====
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference != null ? bookingReference.trim() : null;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber != null ? seatNumber.trim() : null;
    }

    public BookingClass getBookingClass() {
        return bookingClass;
    }

    public void setBookingClass(BookingClass bookingClass) {
        this.bookingClass = bookingClass;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    // ===== New Method for Status =====
    public String getStatus() {
        return bookingStatus != null ? bookingStatus.name() : null;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public boolean isBoardingPassPrinted() {
        return boardingPassPrinted;
    }

    public void setBoardingPassPrinted(boolean boardingPassPrinted) {
        this.boardingPassPrinted = boardingPassPrinted;
    }

    public Integer getCheckedInBy() {
        return checkedInBy;
    }

    public void setCheckedInBy(Integer checkedInBy) {
        this.checkedInBy = checkedInBy;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests != null ? specialRequests.trim() : null;
    }

    public String getMealPreference() {
        return mealPreference;
    }

    public void setMealPreference(String mealPreference) {
        this.mealPreference = mealPreference != null ? mealPreference.trim() : null;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    private LocalDateTime updatedAt;

// Getter and Setter for updatedAt
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    // ===== Utility Methods =====

    /**
     * Validates booking data.
     */
    public boolean isValid() {
        return bookingReference != null && !bookingReference.isBlank()
                && passengerId > 0
                && flightId > 0
                && seatNumber != null && !seatNumber.isBlank()
                && bookingClass != null
                && bookingStatus != null;
    }

    @Override
    public String toString() {
        return "Booking{"
                + "bookingId=" + bookingId
                + ", bookingReference='" + bookingReference + '\''
                + ", passengerId=" + passengerId
                + ", flightId=" + flightId
                + ", seatNumber='" + seatNumber + '\''
                + ", bookingClass=" + bookingClass
                + ", bookingStatus=" + bookingStatus
                + ", checkInTime=" + checkInTime
                + ", boardingPassPrinted=" + boardingPassPrinted
                + ", checkedInBy=" + checkedInBy
                + ", specialRequests='" + specialRequests + '\''
                + ", mealPreference='" + mealPreference + '\''
                + ", createdAt=" + createdAt
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Booking)) {
            return false;
        }
        Booking booking = (Booking) o;
        return bookingId == booking.bookingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }
}
