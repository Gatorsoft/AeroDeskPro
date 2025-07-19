package com.gatorsoft.aerodeskpro.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a passenger on a flight.
 */
public class Passenger {

    public enum Gender {
        M, F, Other
    }

    private int passengerId;
    private String firstName;
    private String lastName;
    private String passportNumber;
    private String nationality;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String email;
    private String phone;
    private String frequentFlyerNumber;
    private LocalDateTime createdAt;

    public Passenger() {}

    public Passenger(int passengerId, String firstName, String lastName, String passportNumber,
                     String nationality, LocalDate dateOfBirth, Gender gender, String email,
                     String phone, String frequentFlyerNumber, LocalDateTime createdAt) {
        this.passengerId = passengerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passportNumber = passportNumber;
        this.nationality = nationality;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.frequentFlyerNumber = frequentFlyerNumber;
        this.createdAt = createdAt;
    }

    // ===== Getters and Setters =====

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName != null ? firstName.trim() : null;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName != null ? lastName.trim() : null;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber != null ? passportNumber.trim() : null;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality != null ? nationality.trim() : null;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone != null ? phone.trim() : null;
    }

    public String getFrequentFlyerNumber() {
        return frequentFlyerNumber;
    }

    public void setFrequentFlyerNumber(String frequentFlyerNumber) {
        this.frequentFlyerNumber = frequentFlyerNumber != null ? frequentFlyerNumber.trim() : null;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ===== Validation =====

    public boolean isValid() {
        return firstName != null && !firstName.isBlank()
            && lastName != null && !lastName.isBlank()
            && isValidEmail(email)
            && isValidPhone(phone);
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        return Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$").matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return Pattern.compile("^\\+?[0-9]{7,15}$").matcher(phone).matches();
    }

    // ===== Utility =====

    @Override
    public String toString() {
        return "Passenger{" +
                "passengerId=" + passengerId +
                ", name='" + firstName + " " + lastName + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                ", nationality='" + nationality + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender=" + gender +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", frequentFlyerNumber='" + frequentFlyerNumber + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Passenger)) return false;
        Passenger that = (Passenger) o;
        return passengerId == that.passengerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(passengerId);
    }
}
