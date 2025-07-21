package com.gatorsoft.aerodeskpro.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class AirportManagementSystem extends JFrame {
    private JTabbedPane tabbedPane;
    private DefaultTableModel flightTableModel;
    private DefaultTableModel passengerTableModel;
    private DefaultTableModel aircraftTableModel;
    private DefaultTableModel staffTableModel;
    
    // Data storage
    private List<Flight> flights = new ArrayList<>();
    private List<Passenger> passengers = new ArrayList<>();
    private List<Aircraft> aircrafts = new ArrayList<>();
    private List<Staff> staffMembers = new ArrayList<>();
    
    public AirportManagementSystem() {
        initializeData();
        setupGUI();
    }
    
    private void initializeData() {
        // Sample data
        flights.add(new Flight("AI101", "Mumbai", "Delhi", "09:00", "11:30", "On Time"));
        flights.add(new Flight("SG202", "Delhi", "Bangalore", "14:15", "17:00", "Delayed"));
        flights.add(new Flight("UK303", "Chennai", "Kolkata", "19:30", "21:45", "Boarding"));
        
        passengers.add(new Passenger("P001", "John Doe", "AI101", "A15", "Confirmed"));
        passengers.add(new Passenger("P002", "Jane Smith", "SG202", "B22", "Checked-in"));
        passengers.add(new Passenger("P003", "Mike Johnson", "UK303", "C10", "Waiting"));
        
        aircrafts.add(new Aircraft("AI-101", "Boeing 737", 180, "Available"));
        aircrafts.add(new Aircraft("SG-202", "Airbus A320", 150, "In Flight"));
        aircrafts.add(new Aircraft("UK-303", "Boeing 777", 300, "Maintenance"));
        
        staffMembers.add(new Staff("S001", "Captain Smith", "Pilot", "Active"));
        staffMembers.add(new Staff("S002", "Sarah Wilson", "Flight Attendant", "Active"));
        staffMembers.add(new Staff("S003", "David Brown", "Ground Crew", "Off Duty"));
    }
    
    private void setupGUI() {
        setTitle("Airport Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Add tabs
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Flight Management", createFlightManagementPanel());
        tabbedPane.addTab("Passenger Management", createPassengerManagementPanel());
        tabbedPane.addTab("Aircraft Management", createAircraftManagementPanel());
        tabbedPane.addTab("Staff Management", createStaffManagementPanel());
        
        add(tabbedPane);
        
        // Set look and feel
        try {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    SwingUtilities.updateComponentTreeUI(this);
} catch (Exception e) {
    e.printStackTrace();
}
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Statistics panels
        panel.add(createStatPanel("Total Flights", String.valueOf(flights.size()), Color.BLUE));
        panel.add(createStatPanel("Total Passengers", String.valueOf(passengers.size()), Color.GREEN));
        panel.add(createStatPanel("Total Aircraft", String.valueOf(aircrafts.size()), Color.ORANGE));
        panel.add(createStatPanel("Total Staff", String.valueOf(staffMembers.size()), Color.RED));
        
        return panel;
    }
    
    private JPanel createStatPanel(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(color, 2), title, 
            TitledBorder.CENTER, TitledBorder.TOP));
        panel.setBackground(Color.WHITE);
        
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 48));
        valueLabel.setForeground(color);
        
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createFlightManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table
        String[] columns = {"Flight No", "From", "To", "Departure", "Arrival", "Status"};
        flightTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(flightTableModel);
        refreshFlightTable();
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = createFlightFormPanel();
        panel.add(formPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createFlightFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Flight Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField flightNoField = new JTextField(10);
        JTextField fromField = new JTextField(10);
        JTextField toField = new JTextField(10);
        JTextField departureField = new JTextField(10);
        JTextField arrivalField = new JTextField(10);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"On Time", "Delayed", "Cancelled", "Boarding"});
        
        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Flight No:"), gbc);
        gbc.gridx = 1;
        panel.add(flightNoField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(new JLabel("From:"), gbc);
        gbc.gridx = 3;
        panel.add(fromField, gbc);
        
        gbc.gridx = 4; gbc.gridy = 0;
        panel.add(new JLabel("To:"), gbc);
        gbc.gridx = 5;
        panel.add(toField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Departure:"), gbc);
        gbc.gridx = 1;
        panel.add(departureField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(new JLabel("Arrival:"), gbc);
        gbc.gridx = 3;
        panel.add(arrivalField, gbc);
        
        gbc.gridx = 4; gbc.gridy = 1;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 5;
        panel.add(statusCombo, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Flight");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        
        addBtn.addActionListener(e -> {
            if (validateFlightFields(flightNoField, fromField, toField, departureField, arrivalField)) {
                flights.add(new Flight(
                    flightNoField.getText(),
                    fromField.getText(),
                    toField.getText(),
                    departureField.getText(),
                    arrivalField.getText(),
                    statusCombo.getSelectedItem().toString()
                ));
                refreshFlightTable();
                clearFlightFields(flightNoField, fromField, toField, departureField, arrivalField, statusCombo);
                JOptionPane.showMessageDialog(this, "Flight added successfully!");
            }
        });
        
        clearBtn.addActionListener(e -> {
            clearFlightFields(flightNoField, fromField, toField, departureField, arrivalField, statusCombo);
        });
        
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 6;
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createPassengerManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Passenger ID", "Name", "Flight No", "Seat", "Status"};
        passengerTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(passengerTableModel);
        refreshPassengerTable();
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = createPassengerFormPanel();
        panel.add(formPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createPassengerFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Passenger Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField idField = new JTextField(10);
        JTextField nameField = new JTextField(15);
        JTextField flightField = new JTextField(10);
        JTextField seatField = new JTextField(10);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Confirmed", "Checked-in", "Waiting", "Cancelled"});
        
        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Passenger ID:"), gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 3;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Flight No:"), gbc);
        gbc.gridx = 1;
        panel.add(flightField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(new JLabel("Seat:"), gbc);
        gbc.gridx = 3;
        panel.add(seatField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        panel.add(statusCombo, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Passenger");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        
        addBtn.addActionListener(e -> {
            if (validatePassengerFields(idField, nameField, flightField, seatField)) {
                passengers.add(new Passenger(
                    idField.getText(),
                    nameField.getText(),
                    flightField.getText(),
                    seatField.getText(),
                    statusCombo.getSelectedItem().toString()
                ));
                refreshPassengerTable();
                clearPassengerFields(idField, nameField, flightField, seatField, statusCombo);
                JOptionPane.showMessageDialog(this, "Passenger added successfully!");
            }
        });
        
        clearBtn.addActionListener(e -> {
            clearPassengerFields(idField, nameField, flightField, seatField, statusCombo);
        });
        
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createAircraftManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Aircraft ID", "Model", "Capacity", "Status"};
        aircraftTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(aircraftTableModel);
        refreshAircraftTable();
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = createAircraftFormPanel();
        panel.add(formPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createAircraftFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Aircraft Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField idField = new JTextField(10);
        JTextField modelField = new JTextField(15);
        JTextField capacityField = new JTextField(10);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "In Flight", "Maintenance", "Out of Service"});
        
        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Aircraft ID:"), gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(new JLabel("Model:"), gbc);
        gbc.gridx = 3;
        panel.add(modelField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1;
        panel.add(capacityField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        panel.add(statusCombo, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Aircraft");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        
        addBtn.addActionListener(e -> {
            if (validateAircraftFields(idField, modelField, capacityField)) {
                aircrafts.add(new Aircraft(
                    idField.getText(),
                    modelField.getText(),
                    Integer.parseInt(capacityField.getText()),
                    statusCombo.getSelectedItem().toString()
                ));
                refreshAircraftTable();
                clearAircraftFields(idField, modelField, capacityField, statusCombo);
                JOptionPane.showMessageDialog(this, "Aircraft added successfully!");
            }
        });
        
        clearBtn.addActionListener(e -> {
            clearAircraftFields(idField, modelField, capacityField, statusCombo);
        });
        
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createStaffManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Staff ID", "Name", "Position", "Status"};
        staffTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(staffTableModel);
        refreshStaffTable();
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = createStaffFormPanel();
        panel.add(formPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStaffFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Staff Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField idField = new JTextField(10);
        JTextField nameField = new JTextField(15);
        JComboBox<String> positionCombo = new JComboBox<>(new String[]{"Pilot", "Flight Attendant", "Ground Crew", "Security", "Maintenance"});
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Off Duty", "On Leave", "Terminated"});
        
        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Staff ID:"), gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 3;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1;
        panel.add(positionCombo, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        panel.add(statusCombo, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Staff");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        
        addBtn.addActionListener(e -> {
            if (validateStaffFields(idField, nameField)) {
                staffMembers.add(new Staff(
                    idField.getText(),
                    nameField.getText(),
                    positionCombo.getSelectedItem().toString(),
                    statusCombo.getSelectedItem().toString()
                ));
                refreshStaffTable();
                clearStaffFields(idField, nameField, positionCombo, statusCombo);
                JOptionPane.showMessageDialog(this, "Staff member added successfully!");
            }
        });
        
        clearBtn.addActionListener(e -> {
            clearStaffFields(idField, nameField, positionCombo, statusCombo);
        });
        
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    // Validation methods
    private boolean validateFlightFields(JTextField... fields) {
        for (JTextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return false;
            }
        }
        return true;
    }
    
    private boolean validatePassengerFields(JTextField... fields) {
        for (JTextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return false;
            }
        }
        return true;
    }
    
    private boolean validateAircraftFields(JTextField idField, JTextField modelField, JTextField capacityField) {
        if (idField.getText().trim().isEmpty() || modelField.getText().trim().isEmpty() || capacityField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return false;
        }
        try {
            Integer.parseInt(capacityField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacity must be a number!");
            return false;
        }
        return true;
    }
    
    private boolean validateStaffFields(JTextField idField, JTextField nameField) {
        if (idField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields!");
            return false;
        }
        return true;
    }
    
    // Clear methods
    private void clearFlightFields(JTextField flightNo, JTextField from, JTextField to, JTextField departure, JTextField arrival, JComboBox<String> status) {
        flightNo.setText("");
        from.setText("");
        to.setText("");
        departure.setText("");
        arrival.setText("");
        status.setSelectedIndex(0);
    }
    
    private void clearPassengerFields(JTextField id, JTextField name, JTextField flight, JTextField seat, JComboBox<String> status) {
        id.setText("");
        name.setText("");
        flight.setText("");
        seat.setText("");
        status.setSelectedIndex(0);
    }
    
    private void clearAircraftFields(JTextField id, JTextField model, JTextField capacity, JComboBox<String> status) {
        id.setText("");
        model.setText("");
        capacity.setText("");
        status.setSelectedIndex(0);
    }
    
    private void clearStaffFields(JTextField id, JTextField name, JComboBox<String> position, JComboBox<String> status) {
        id.setText("");
        name.setText("");
        position.setSelectedIndex(0);
        status.setSelectedIndex(0);
    }
    
    // Refresh table methods
    private void refreshFlightTable() {
        flightTableModel.setRowCount(0);
        for (Flight flight : flights) {
            flightTableModel.addRow(new Object[]{
                flight.getFlightNo(), flight.getFrom(), flight.getTo(),
                flight.getDeparture(), flight.getArrival(), flight.getStatus()
            });
        }
    }
    
    private void refreshPassengerTable() {
        passengerTableModel.setRowCount(0);
        for (Passenger passenger : passengers) {
            passengerTableModel.addRow(new Object[]{
                passenger.getId(), passenger.getName(), passenger.getFlightNo(),
                passenger.getSeat(), passenger.getStatus()
            });
        }
    }
    
    private void refreshAircraftTable() {
        aircraftTableModel.setRowCount(0);
        for (Aircraft aircraft : aircrafts) {
            aircraftTableModel.addRow(new Object[]{
                aircraft.getId(), aircraft.getModel(),
                aircraft.getCapacity(), aircraft.getStatus()
            });
        }
    }
    
    private void refreshStaffTable() {
        staffTableModel.setRowCount(0);
        for (Staff staff : staffMembers) {
            staffTableModel.addRow(new Object[]{
                staff.getId(), staff.getName(),
                staff.getPosition(), staff.getStatus()
            });
        }
    }
    
    // Data classes
    class Flight {
        private String flightNo, from, to, departure, arrival, status;
        
        public Flight(String flightNo, String from, String to, String departure, String arrival, String status) {
            this.flightNo = flightNo;
            this.from = from;
            this.to = to;
            this.departure = departure;
            this.arrival = arrival;
            this.status = status;
        }
        
        // Getters
        public String getFlightNo() { return flightNo; }
        public String getFrom() { return from; }
        public String getTo() { return to; }
        public String getDeparture() { return departure; }
        public String getArrival() { return arrival; }
        public String getStatus() { return status; }
    }
    
    class Passenger {
        private String id, name, flightNo, seat, status;
        
        public Passenger(String id, String name, String flightNo, String seat, String status) {
            this.id = id;
            this.name = name;
            this.flightNo = flightNo;
            this.seat = seat;
            this.status = status;
        }
        
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getFlightNo() { return flightNo; }
        public String getSeat() { return seat; }
        public String getStatus() { return status; }
    }
    
    class Aircraft {
        private String id, model, status;
        private int capacity;
        
        public Aircraft(String id, String model, int capacity, String status) {
            this.id = id;
            this.model = model;
            this.capacity = capacity;
            this.status = status;
        }
        
        // Getters
        public String getId() { return id; }
        public String getModel() { return model; }
        public int getCapacity() { return capacity; }
        public String getStatus() { return status; }
    }
    
    class Staff {
        private String id, name, position, status;
        
        public Staff(String id, String name, String position, String status) {
            this.id = id;
            this.name = name;
            this.position = position;
            this.status = status;
        }
        
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getPosition() { return position; }
        public String getStatus() { return status; }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AirportManagementSystem().setVisible(true);
        });
    }
}