package com.example.chatplatform.controller;

import com.example.chatplatform.dto.UpdateProfileRequest;
import com.example.chatplatform.dto.UpdateSettingsRequest;
import com.example.chatplatform.entity.User;
import com.example.chatplatform.service.UserService;
import com.example.chatplatform.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            User user = userService.getProfile(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                           @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            User user = userService.updateProfile(userId, request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profil mis à jour avec succès");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/settings")
    public ResponseEntity<?> getSettings(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            User user = userService.getSettings(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("notificationsEnabled", user.getNotificationsEnabled());
            response.put("theme", user.getTheme());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/settings")
    public ResponseEntity<?> updateSettings(@Valid @RequestBody UpdateSettingsRequest request,
                                            @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            User user = userService.updateSettings(userId, request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Paramètres mis à jour avec succès");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
} 