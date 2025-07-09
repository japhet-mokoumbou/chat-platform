package com.example.chatplatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

  @NotBlank(message = "Le nom d'utilisateur est requis.")
  private String username;

  @NotBlank(message = "L'adresse email est requise.")
  @Email(message = "Le format de l'adresse email est invalide.")
  private String email;

  @NotBlank(message = "Le mot de passe est requis.")
  @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères.")
  private String password;

  // Constructeurs
  public RegisterRequest() {
  }

  public RegisterRequest(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  // Getters et Setters
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}