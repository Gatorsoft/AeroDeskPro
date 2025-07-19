## ✈️ Using `FlightService` in Your Application

Below is an example of how you can use the `FlightService` class in your Java application to manage flights.

```java
// In your UI or main application
FlightService flightService = new FlightService();

// Schedule a new flight
Flight newFlight = new Flight(
    "AA101", 
    departureTime, 
    arrivalTime, 
    "JFK", 
    "LAX", 
    "Boeing 737", 
    180
);
flightService.scheduleNewFlight(newFlight);

// Update flight status
flightService.updateFlightStatus(flightId, FlightStatus.BOARDING);

// Get today's flights
List<Flight> todaysFlights = flightService.getTodaysFlights();
