package com.example.chatplatform.dto;

import jakarta.validation.constraints.Size;

public class UpdateContactRequest {

    // L'ID du contactUserId ne devrait pas être modifiable directement via cette requête
    // car il identifie le contact. Si on veut changer le contact, on supprime et on ajoute.
    // On peut permettre de modifier l'alias.

    @Size(max = 50, message = "L'alias ne doit pas dépasser 50 caractères")
    private String alias;

    // Getters et Setters
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}