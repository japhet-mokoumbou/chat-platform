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
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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

    @PostMapping("/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file,
                                                  @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            User user = userService.getProfile(userId);

            // Vérifier le type MIME
            String contentType = file.getContentType();
            if (contentType == null ||
                !(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/webp"))) {
                return ResponseEntity.badRequest().body(Map.of("error", "Format d'image non supporté (jpg, png, webp)"));
            }

            // Créer le dossier si besoin
            String uploadDir = System.getProperty("user.dir") + "/uploads/profile-pictures/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom unique (écrase l'ancienne image si existante)
            String extension = switch (contentType) {
                case "image/jpeg" -> ".jpg";
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                default -> "";
            };
            String filename = "user-" + user.getId() + extension;
            Path filePath = uploadPath.resolve(filename);
            Files.write(filePath, file.getBytes());

            // Mettre à jour le champ profilePicture (URL relative)
            String fileUrl = "/uploads/profile-pictures/" + filename;
            user.setProfilePicture(fileUrl);
            userService.saveUser(user);

            return ResponseEntity.ok(Map.of("message", "Image de profil mise à jour", "url", fileUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur lors de l'upload: " + e.getMessage()));
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