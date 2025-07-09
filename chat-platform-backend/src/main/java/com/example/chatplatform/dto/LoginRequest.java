package com.example.chatplatform.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

  @NotBlank(message = "Le nom d'utilisateur ou l'adresse email est requis.")
  private String usernameOrEmail;

  @NotBlank(message = "Le mot de passe est requis.")
  private String password;

  // Constructeurs
  public LoginRequest() {
  }

  public LoginRequest(String usernameOrEmail, String password) {
    this.usernameOrEmail = usernameOrEmail;
    this.password = password;
  }

  // Getters et Setters
  public String getUsernameOrEmail() {
    return usernameOrEmail;
  }

  public void setUsernameOrEmail(String usernameOrEmail) {
    this.usernameOrEmail = usernameOrEmail;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}