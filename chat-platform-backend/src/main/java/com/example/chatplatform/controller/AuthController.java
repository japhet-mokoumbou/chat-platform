package com.example.chatplatform.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.chatplatform.dto.AuthResponse;
import com.example.chatplatform.dto.LoginRequest;
import com.example.chatplatform.dto.RegisterRequest;
import com.example.chatplatform.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Endpoint d'inscription
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        System.out.println("=== REGISTER REQUEST RECEIVED ===");
        System.out.println("Username: " + registerRequest.getUsername());
        System.out.println("Email: " + registerRequest.getEmail());
        
        try {
            AuthResponse response = userService.registerUser(registerRequest);
            System.out.println("Registration successful for: " + registerRequest.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            System.err.println("Registration error: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            System.err.println("Internal server error: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur interne du serveur");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint de connexion
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("=== LOGIN REQUEST RECEIVED ===");
        System.out.println("Username/Email: " + loginRequest.getUsernameOrEmail());
        
        try {
            AuthResponse response = userService.loginUser(loginRequest);
            System.out.println("Login successful for: " + loginRequest.getUsernameOrEmail());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("Login error: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            System.err.println("Internal server error: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur interne du serveur");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint pour vérifier le token (optionnel)
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Token valide");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de déconnexion (côté client principalement)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Déconnexion réussie");
        return ResponseEntity.ok(response);
    }
}
