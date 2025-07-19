/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gatorsoft.aerodeskpro.main;


import com.gatorsoft.aerodeskpro.dao.StaffDAO;
import com.gatorsoft.aerodeskpro.model.Staff;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        StaffDAO dao = new StaffDAO();

        try {
            List<Staff> staffList = dao.getAllStaff();
            if (staffList.isEmpty()) {
                System.out.println("No staff records found.");
            } else {
                for (Staff staff : staffList) {
                    System.out.println(staff);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching staff data:");
            e.printStackTrace();
        } finally {
            // Close DB connection explicitly if needed
            com.gatorsoft.aerodeskpro.database.DatabaseConnection.closeConnection();
        }
    }
}
