package com.example.chatplatform.dto;

import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {
    @Size(max = 50, message = "Le nom affiché ne doit pas dépasser 50 caractères")
    private String displayName;

    @Size(max = 255, message = "La bio ne doit pas dépasser 255 caractères")
    private String bio;

    private String profilePicture; // Chemin du fichier ou base64

    // Getters et Setters
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
} 