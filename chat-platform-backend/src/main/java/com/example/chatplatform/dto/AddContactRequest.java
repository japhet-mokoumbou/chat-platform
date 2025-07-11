package com.example.chatplatform.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AddContactRequest {

    @NotNull(message = "L'ID de l'utilisateur contact est obligatoire")
    private Long contactUserId;

    @Size(max = 50, message = "L'alias ne doit pas dépasser 50 caractères")
    private String alias; // Optionnel

    // Getters et Setters
    public Long getContactUserId() {
        return contactUserId;
    }

    public void setContactUserId(Long contactUserId) {
        this.contactUserId = contactUserId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}