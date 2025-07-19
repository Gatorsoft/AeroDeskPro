package com.gatorsoft.aerodeskpro.models;

import java.time.LocalDateTime;

public class UserSession {
    private int sessionId;
    private int userId;
    private String sessionToken;
    private LocalDateTime loginTime;
    private LocalDateTime lastActivity;
    private String ipAddress;
    private String userAgent;
    private boolean isActive;

    // Constructors
    public UserSession() {
        this.loginTime = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        this.isActive = true;
    }

    public UserSession(int userId, String sessionToken, String ipAddress, String userAgent) {
        this();
        this.userId = userId;
        this.sessionToken = sessionToken;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    // Getters and Setters
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }

    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }

    public LocalDateTime getLastActivity() { return lastActivity; }
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "UserSession{" +
                "sessionId=" + sessionId +
                ", userId=" + userId +
                ", loginTime=" + loginTime +
                ", lastActivity=" + lastActivity +
                ", ipAddress='" + ipAddress + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}