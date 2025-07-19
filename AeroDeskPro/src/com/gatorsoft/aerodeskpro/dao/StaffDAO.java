package com.gatorsoft.aerodeskpro.dao;

import com.gatorsoft.aerodeskpro.database.DatabaseConnection;
import com.gatorsoft.aerodeskpro.model.Staff;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    public List<Staff> getAllStaff() throws SQLException {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Staff staff = mapResultSetToStaff(rs);
                staffList.add(staff);
            }
        }
        return staffList;
    }

    public Staff getStaffById(int userId) throws SQLException {
        String sql = "SELECT * FROM staff WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaff(rs);
                }
            }
        }
        return null;
    }

    public boolean addStaff(Staff staff) throws SQLException {
        String sql = "INSERT INTO staff (employee_id, username, password_hash, email, first_name, last_name, phone, is_active, vcode) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, staff.getEmployeeId());
            stmt.setString(2, staff.getUsername());
            stmt.setString(3, staff.getPasswordHash());
            stmt.setString(4, staff.getEmail());
            stmt.setString(5, staff.getFirstName());
            stmt.setString(6, staff.getLastName());
            stmt.setString(7, staff.getPhone());
            stmt.setBoolean(8, staff.isActive());
            stmt.setString(9, staff.getVcode());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    staff.setUserId(generatedKeys.getInt(1));
                }
            }

            return true;
        }
    }

    public boolean updateStaff(Staff staff) throws SQLException {
        String sql = "UPDATE staff SET employee_id=?, username=?, password_hash=?, email=?, first_name=?, last_name=?, phone=?, is_active=?, vcode=? WHERE user_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, staff.getEmployeeId());
            stmt.setString(2, staff.getUsername());
            stmt.setString(3, staff.getPasswordHash());
            stmt.setString(4, staff.getEmail());
            stmt.setString(5, staff.getFirstName());
            stmt.setString(6, staff.getLastName());
            stmt.setString(7, staff.getPhone());
            stmt.setBoolean(8, staff.isActive());
            stmt.setString(9, staff.getVcode());
            stmt.setInt(10, staff.getUserId());

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        }
    }

    public boolean deleteStaff(int userId) throws SQLException {
        String sql = "DELETE FROM staff WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        }
    }

    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();

        staff.setUserId(rs.getInt("user_id"));
        staff.setEmployeeId(rs.getString("employee_id"));
        staff.setUsername(rs.getString("username"));
        staff.setPasswordHash(rs.getString("password_hash"));
        staff.setEmail(rs.getString("email"));
        staff.setFirstName(rs.getString("first_name"));
        staff.setLastName(rs.getString("last_name"));
        staff.setPhone(rs.getString("phone"));
        staff.setIsActive(rs.getBoolean("is_active"));

        Timestamp lastLoginTS = rs.getTimestamp("last_login");
        if (lastLoginTS != null) {
            staff.setLastLogin(lastLoginTS.toLocalDateTime());
        }

        Timestamp createdAtTS = rs.getTimestamp("created_at");
        if (createdAtTS != null) {
            staff.setCreatedAt(createdAtTS.toLocalDateTime());
        }

        Timestamp updatedAtTS = rs.getTimestamp("updated_at");
        if (updatedAtTS != null) {
            staff.setUpdatedAt(updatedAtTS.toLocalDateTime());
        }

        staff.setVcode(rs.getString("vcode"));

        return staff;
    }
}
