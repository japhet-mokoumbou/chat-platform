// src/main/java/com/example/chatplatform/controller/AuthController.java
package com.example.chatplatform.controller;

import com.example.chatplatform.dto.RegisterRequest; // Renommé de DTO si c'est ce que vous utilisez pour le login
import com.example.chatplatform.entity.User; // Si vous avez un User model
import com.example.chatplatform.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager; // Pour gérer l'authentification
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;
  private final AuthenticationManager authenticationManager; // Ajoutez cette injection

  // Assurez-vous d'avoir ce bean dans SecurityConfig si vous voulez l'utiliser
  // C'est un peu plus avancé, mais c'est le chemin pour une API de login
  /*
   * @Bean
   * public AuthenticationManager
   * authenticationManager(AuthenticationConfiguration
   * authenticationConfiguration) throws Exception {
   * return authenticationConfiguration.getAuthenticationManager();
   * }
   */

  public AuthController(UserService userService, AuthenticationManager authenticationManager) {
    this.userService = userService;
    this.authenticationManager = authenticationManager; // Injectez l'AuthenticationManager
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
    try {
      userService.registerUser(registerRequest);
      return ResponseEntity.ok("User registered successfully!");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed: " + e.getMessage());
    }
  }

  // NOUVEL ENDPOINT POUR LE LOGIN API (utilisant AuthenticationManager)
  @PostMapping("/login") // L'endpoint réel pour l'authentification API
  public ResponseEntity<?> authenticateUser(@RequestBody RegisterRequest loginRequest) { // Utilisez RegisterRequest ou
                                                                                         // créez LoginRequest
    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);

      // Si l'authentification réussit, vous pouvez retourner un jeton JWT, etc.
      // Pour l'instant, juste une confirmation
      return ResponseEntity.ok("User logged in successfully!");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials: " + e.getMessage());
    }
  }
}