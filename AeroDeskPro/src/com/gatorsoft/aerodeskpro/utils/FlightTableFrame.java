/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gatorsoft.aerodeskpro.utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import com.gatorsoft.aerodeskpro.models.Flight;
import com.gatorsoft.aerodeskpro.services.FlightService;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException;
import java.awt.BorderLayout;

public class FlightTableFrame extends JFrame {
    private JTable flightTable;
    private FlightService flightService;

    // Constructor for the JFrame
    public FlightTableFrame() {
        // Initialize the flightService
        flightService = new FlightService();

        // Set JFrame properties
        setTitle("Flight Details");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // Initialize the table
        flightTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(flightTable);
        add(scrollPane, BorderLayout.CENTER);

        // Call method to load flight data into the table
        loadFlightData();

        // Set the window to be visible
        setVisible(true);
    }

    // Method to load flight data into the JTable
    private void loadFlightData() {
        try {
            // Fetch the list of flights from the FlightService
            List<Flight> flights = flightService.getAllFlights();

            // Define the column names for the table
            String[] columnNames = {
                "Flight Number", "Departure Time", "Arrival Time", 
                "Origin", "Destination", "Gate Number", "Status", 
                "Aircraft Type", "Capacity", "Created At"
            };

            // Prepare the data for the table in 2D array format
            Object[][] data = new Object[flights.size()][columnNames.length];
            
            for (int i = 0; i < flights.size(); i++) {
                Flight flight = flights.get(i);
                data[i][0] = flight.getFlightNumber();
                data[i][1] = flight.getDepartureTime();
                data[i][2] = flight.getArrivalTime();
                data[i][3] = flight.getOrigin();
                data[i][4] = flight.getDestination();
                data[i][5] = flight.getGateNumber();
                data[i][6] = flight.getStatus();
                data[i][7] = flight.getAircraftType();
                data[i][8] = flight.getCapacity();
                data[i][9] = flight.getCreatedAt();
            }

            // Create a DefaultTableModel with the data and column names
            DefaultTableModel model = new DefaultTableModel(data, columnNames);
            flightTable.setModel(model);  // Set the model for the JTable

        } catch (AeroDeskException e) {
            JOptionPane.showMessageDialog(this, "Error loading flight data: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Launch the JFrame
        SwingUtilities.invokeLater(() -> new FlightTableFrame());
    }
}
