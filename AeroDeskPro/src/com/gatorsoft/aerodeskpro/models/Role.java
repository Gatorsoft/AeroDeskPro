package com.gatorsoft.aerodeskpro.models;

import java.time.LocalDateTime;

public class Role {
    private int roleId;
    private String roleName;
    private String description;
    private LocalDateTime createdAt;

    // Constructors
    public Role() {
        this.createdAt = LocalDateTime.now();
    }

    public Role(String roleName, String description) {
        this();
        this.roleName = roleName;
        this.description = description;
    }

    // Getters and Setters
    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}