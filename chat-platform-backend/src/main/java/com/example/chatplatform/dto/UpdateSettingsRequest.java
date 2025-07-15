package com.example.chatplatform.dto;

public class UpdateSettingsRequest {
    private Boolean notificationsEnabled;
    private String theme; // "light" ou "dark"

    // Getters et Setters
    public Boolean getNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(Boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
} 