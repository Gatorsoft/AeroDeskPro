package com.gatorsoft.aerodeskpro.services;

import com.gatorsoft.aerodeskpro.dao.BaggageDAO;
import com.gatorsoft.aerodeskpro.dao.BookingDAO;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException.ErrorCategory;
import com.gatorsoft.aerodeskpro.models.Baggage;
import com.gatorsoft.aerodeskpro.entity.BaggageStatus;
import com.gatorsoft.aerodeskpro.models.BaggageReport;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaggageService {

    private BaggageDAO baggageDAO;
    private BookingDAO bookingDAO;
    private Logger logger;

    public BaggageService() {
        this.baggageDAO = new BaggageDAO();
        this.bookingDAO = new BookingDAO();
        this.logger = Logger.getLogger(BaggageService.class.getName());
    }

    // Register a new baggage
    public Baggage registerBaggage(Baggage baggage) throws AeroDeskException {
        try {
            validateBaggageData(baggage);
            boolean isInserted = baggageDAO.insertBaggage(baggage);
            if (!isInserted) {
                throw new AeroDeskException("Failed to register baggage", ErrorCategory.DATABASE_ERROR);
            }
            logger.info("Baggage registered successfully: " + baggage.getBaggageTag());
            return baggage;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error registering baggage", e);
            throw new AeroDeskException("Failed to register baggage", e, ErrorCategory.DATABASE_ERROR);
        }
    }

    // Update baggage status
    public void updateBaggageStatus(String baggageTag, BaggageStatus newStatus, String location) throws AeroDeskException {
        try {
            if (baggageTag == null || baggageTag.trim().isEmpty()) {
                throw new AeroDeskException("Baggage tag cannot be null or empty", ErrorCategory.VALIDATION_ERROR);
            }
            boolean isUpdated = baggageDAO.updateBaggageStatus(baggageTag, newStatus, location);
            if (!isUpdated) {
                throw new AeroDeskException("Failed to update baggage status", ErrorCategory.DATABASE_ERROR);
            }
            logger.info("Baggage status updated successfully for tag: " + baggageTag);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating baggage status", e);
            throw new AeroDeskException("Failed to update baggage status", e, ErrorCategory.DATABASE_ERROR);
        }
    }

    // Track baggage
    public void trackBaggage(int baggageId, String location, String notes) throws AeroDeskException {
        try {
            if (location == null || location.trim().isEmpty()) {
                throw new AeroDeskException("Location cannot be null or empty", ErrorCategory.VALIDATION_ERROR);
            }
            boolean isTracked = baggageDAO.trackBaggage(baggageId, location, notes);
            if (!isTracked) {
                throw new AeroDeskException("Failed to track baggage", ErrorCategory.DATABASE_ERROR);
            }
            logger.info("Baggage tracked successfully with ID: " + baggageId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error tracking baggage", e);
            throw new AeroDeskException("Failed to track baggage", e, ErrorCategory.DATABASE_ERROR);
        }
    }

    // Get lost baggage list
    public List<Baggage> getLostBaggage() throws AeroDeskException {
        try {
            List<Baggage> lostBaggage = baggageDAO.getLostBaggage();
            logger.info("Retrieved " + lostBaggage.size() + " lost baggage records.");
            return lostBaggage;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving lost baggage", e);
            throw new AeroDeskException("Failed to retrieve lost baggage", e, ErrorCategory.DATABASE_ERROR);
        }
    }

    // Get delayed baggage list
    public List<Baggage> getDelayedBaggage() throws AeroDeskException {
        try {
            List<Baggage> delayedBaggage = baggageDAO.getDelayedBaggage();
            logger.info("Retrieved " + delayedBaggage.size() + " delayed baggage records.");
            return delayedBaggage;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving delayed baggage", e);
            throw new AeroDeskException("Failed to retrieve delayed baggage", e, ErrorCategory.DATABASE_ERROR);
        }
    }

    // Generate baggage report for a specific date
public BaggageReport generateBaggageReport(LocalDate date) throws AeroDeskException {
    try {
        // Validation for the input date
        if (date == null) {
            throw new AeroDeskException("Date cannot be null", ErrorCategory.VALIDATION_ERROR);
        }

        // Call the DAO method to generate the report
        BaggageReport report = baggageDAO.generateBaggageReport(date);

        // If no report was returned, throw an exception
        if (report == null) {
            throw new AeroDeskException("Failed to generate baggage report", ErrorCategory.SYSTEM_ERROR);
        }

        // Log the successful generation of the report
        logger.info("Baggage report generated for date: " + date);

        // Return the generated report
        return report;

    } catch (Exception e) {
        // Log the error and rethrow the exception
        logger.log(Level.SEVERE, "Error generating baggage report", e);
        throw new AeroDeskException("Failed to generate baggage report", e, ErrorCategory.SYSTEM_ERROR);
    }
}


    // Validate baggage data
public void validateBaggageData(Baggage baggage) throws AeroDeskException {
    // Check if baggage is null
    if (baggage == null) {
        throw new AeroDeskException("Baggage cannot be null", ErrorCategory.VALIDATION_ERROR);
    }

    // Check if baggage tag is null or empty
    if (baggage.getBaggageTag() == null || baggage.getBaggageTag().trim().isEmpty()) {
        throw new AeroDeskException("Baggage tag is required", ErrorCategory.VALIDATION_ERROR);
    }

    BigDecimal weight = baggage.getWeight();  // assuming weight is a BigDecimal
    if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
        throw new AeroDeskException("Baggage weight must be greater than 0", ErrorCategory.VALIDATION_ERROR);
    }

    // Log the validation success
    logger.info("Baggage validated successfully: " + baggage.getBaggageTag());
}

    // Generate a baggage tag
    public String generateBaggageTag() throws AeroDeskException {
        try {
            String baggageTag = baggageDAO.generateBaggageTag();
            if (baggageTag == null || baggageTag.trim().isEmpty()) {
                throw new AeroDeskException("Failed to generate baggage tag", ErrorCategory.SYSTEM_ERROR);
            }
            logger.info("Generated baggage tag: " + baggageTag);
            return baggageTag;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generating baggage tag", e);
            throw new AeroDeskException("Failed to generate baggage tag", e, ErrorCategory.SYSTEM_ERROR);
        }
    }
}
