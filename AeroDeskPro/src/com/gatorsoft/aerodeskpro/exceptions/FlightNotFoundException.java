
package com.gatorsoft.aerodeskpro.exceptions;

/**
 * Specific exception for when flights are not found
 */
public class FlightNotFoundException extends AeroDeskException {
    
    private static final long serialVersionUID = 1L;
    
    private final Object searchCriteria;
    
    public FlightNotFoundException(String message, Object searchCriteria) {
        super(message, null, ErrorCategory.RESOURCE_NOT_FOUND, "FLIGHT_NOT_FOUND");
        this.searchCriteria = searchCriteria;
    }
    
    public FlightNotFoundException(int flightId) {
        this("Flight not found with ID: " + flightId, flightId);
    }
    
    public FlightNotFoundException(String flightNumber) {
        this("Flight not found with number: " + flightNumber, flightNumber);
    }
    
    public Object getSearchCriteria() {
        return searchCriteria;
    }
}
