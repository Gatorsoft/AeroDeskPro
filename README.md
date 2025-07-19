// In your UI or main application
FlightService flightService = new FlightService();

// Schedule a new flight
Flight newFlight = new Flight("AA101", departureTime, arrivalTime, "JFK", "LAX", "Boeing 737", 180);
flightService.scheduleNewFlight(newFlight);

// Update flight status
flightService.updateFlightStatus(flightId, FlightStatus.BOARDING);

// Get today's flights
List<Flight> todaysFlights = flightService.getTodaysFlights();
