package com.gatorsoft.aerodeskpro.models;

import java.time.LocalDateTime;

public class SystemLog {
    private int logId;
    private int userId;
    private String logLevel;
    private String action;
    private String module;
    private String description;
    private String ipAddress;
    private LocalDateTime timestamp;

    // Constructors
    public SystemLog() {
        this.timestamp = LocalDateTime.now();
    }

    public SystemLog(int userId, String logLevel, String action, String module, 
                    String description, String ipAddress) {
        this();
        this.userId = userId;
        this.logLevel = logLevel;
        this.action = action;
        this.module = module;
        this.description = description;
        this.ipAddress = ipAddress;
    }

    // Getters and Setters
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getLogLevel() { return logLevel; }
    public void setLogLevel(String logLevel) { this.logLevel = logLevel; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "SystemLog{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", logLevel='" + logLevel + '\'' +
                ", action='" + action + '\'' +
                ", module='" + module + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}