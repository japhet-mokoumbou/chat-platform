package com.example.chatplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.chatplatform.entity.User;
import com.example.chatplatform.repository.UserRepository;
import com.example.chatplatform.service.UserService;
import com.example.chatplatform.util.JwtUtil;
import com.example.chatplatform.util.XmlUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired(required = false)
    private UserService userService;

    @Autowired(required = false)
    private JwtUtil jwtUtil;

    @Autowired(required = false)
    private XmlUtils xmlUtils;

    @Autowired(required = false)
    private UserRepository userRepository;

    /**
     * Endpoint de test public
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> test() {
        System.out.println("=== TEST ENDPOINT CALLED ===");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Backend is running successfully!");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "OK");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de test protégé
     */
    @GetMapping("/protected")
    public ResponseEntity<Map<String, Object>> testProtected() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Accès autorisé à l'endpoint protégé");
        response.put("username", username);
        response.put("timestamp", LocalDateTime.now());
        response.put("authorities", authentication.getAuthorities());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour obtenir les informations de l'utilisateur connecté
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            if (jwtUtil == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "JWT service non disponible");
                return ResponseEntity.badRequest().body(error);
            }
            
            String jwtToken = token.substring(7); // Enlever "Bearer "
            String username = jwtUtil.extractUsername(jwtToken);
            Long userId = jwtUtil.extractUserId(jwtToken);
            String email = jwtUtil.extractEmail(jwtToken);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", userId);
            response.put("username", username);
            response.put("email", email);
            response.put("message", "Informations utilisateur récupérées");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Token invalide");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Endpoint pour tester la sauvegarde XML
     */
    @PostMapping("/save-xml")
    public ResponseEntity<Map<String, Object>> testSaveXml() {
        try {
            System.out.println("=== TEST SAUVEGARDE XML MANUELLE ===");
            
            if (userRepository == null || xmlUtils == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Services non disponibles");
                return ResponseEntity.badRequest().body(error);
            }

            List<User> users = userRepository.findAll();
            System.out.println("Nombre d'utilisateurs trouvés: " + users.size());

            xmlUtils.saveToXml(users, "users.xml", User.class);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Sauvegarde XML réussie");
            response.put("usersCount", users.size());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Erreur lors du test XML: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la sauvegarde XML: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Endpoint pour vérifier l'existence du fichier XML
     */
    @GetMapping("/check-xml")
    public ResponseEntity<Map<String, Object>> checkXmlFile() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (xmlUtils == null) {
                response.put("error", "XmlUtils non disponible");
                return ResponseEntity.badRequest().body(response);
            }

            boolean exists = xmlUtils.xmlFileExists("users.xml");
            response.put("fileExists", exists);
            
            // Vérifier aussi le chemin absolu
            String xmlPath = "src/main/resources/data/users.xml";
            File file = new File(xmlPath);
            response.put("absolutePath", file.getAbsolutePath());
            response.put("fileExistsAbsolute", file.exists());
            
            if (file.exists()) {
                response.put("fileSize", file.length());
                response.put("lastModified", file.lastModified());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

