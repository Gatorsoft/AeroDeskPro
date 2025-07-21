/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gatorsoft.aerodeskpro.utils;

import com.gatorsoft.aerodeskpro.dao.PassengerDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.gatorsoft.aerodeskpro.models.Passenger;
import com.gatorsoft.aerodeskpro.services.PassengerService;
import com.gatorsoft.aerodeskpro.exceptions.AeroDeskException;

public class PassengerTableFrame extends JFrame {
    
    private final JTable passengerTable;        // JTable to display passenger data
    private final PassengerService passengerService; // Service for fetching passenger data
    
    // Constructor for the JFrame
    public PassengerTableFrame() {
        // Initialize the passengerService (assumed to be properly instantiated elsewhere)
        passengerService = new PassengerService(new PassengerDAO());
        
        // Set JFrame properties
        setTitle("Passenger Details");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        
        // Initialize the table
        passengerTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(passengerTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Call method to load passenger data into the table
        loadPassengerData();
        setLocationRelativeTo(null);
        // Make the window visible
        setVisible(true);
    }

    // Method to load passenger data into the JTable
    private void loadPassengerData() {
        try {
            // Fetch the list of passengers from the PassengerService
            List<Passenger> passengers = passengerService.getAllPassengers();
            
            // Define the column names for the table
            String[] columnNames = {
                "Passenger ID", "First Name", "Last Name", "Email", "Passport Number", "Date of Birth", "Phone Number", "Gender", "Nationality", "Created At"
            };
            
            // Prepare the data for the table in 2D array format
            Object[][] data = new Object[passengers.size()][columnNames.length];
            for (int i = 0; i < passengers.size(); i++) {
                Passenger passenger = passengers.get(i);
                data[i][0] = passenger.getPassengerId();
                data[i][1] = passenger.getFirstName();
                data[i][2] = passenger.getLastName();
                data[i][3] = passenger.getEmail();
                data[i][4] = passenger.getPassportNumber();
                data[i][5] = passenger.getDateOfBirth();
                data[i][6] = passenger.getPhoneNumber();
                data[i][7] = passenger.getGender();
                data[i][8] = passenger.getNationality();
                data[i][9] = passenger.getCreatedAt();
            }

            // Create a DefaultTableModel with the data and column names
            DefaultTableModel model = new DefaultTableModel(data, columnNames);
            passengerTable.setModel(model);

        } catch (Exception e) {
            // Handle error during data loading
            JOptionPane.showMessageDialog(this, "Error loading passenger data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Launch the JFrame
        SwingUtilities.invokeLater(() -> new PassengerTableFrame());
        Logger.info("This is an info message from TestLogger.");
        Logger.error("This is an error message from TestLogger.", new RuntimeException("Test exception"));

    }
}
