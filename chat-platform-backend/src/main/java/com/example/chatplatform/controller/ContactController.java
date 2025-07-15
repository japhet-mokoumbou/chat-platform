package com.example.chatplatform.controller;

import com.example.chatplatform.dto.AddContactRequest;
import com.example.chatplatform.dto.ContactResponse;
import com.example.chatplatform.entity.Contact;
import com.example.chatplatform.entity.User;
import com.example.chatplatform.repository.UserRepository;
import com.example.chatplatform.service.ContactService;
import com.example.chatplatform.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    /**
     * Ajouter un nouveau contact
     */
    @PostMapping
    public ResponseEntity<?> addContact(
            @Valid @RequestBody AddContactRequest request,
            @RequestHeader("Authorization") String token) {
        
        try {
            System.out.println("=== ADD CONTACT REQUEST RECEIVED ===");
            System.out.println("Contact User ID: " + request.getContactUserId());
            System.out.println("Alias: " + request.getAlias());
            
            // Extraire l'ID de l'utilisateur depuis le token JWT
            String jwtToken = token.substring(7); // Enlever "Bearer "
            Long userId = jwtUtil.extractUserId(jwtToken);
            
            System.out.println("Current User ID: " + userId);
            
            Contact newContact = contactService.addContact(request, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Contact ajouté avec succès");
            response.put("contact", newContact);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            System.err.println("Add contact error: " + e.getMessage());
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
     * Lister tous les contacts de l'utilisateur connecté
     */
    @GetMapping
    public ResponseEntity<?> listContacts(@RequestHeader("Authorization") String token) {
        
        try {
            System.out.println("=== LIST CONTACTS REQUEST RECEIVED ===");
            
            // Extraire l'ID de l'utilisateur depuis le token JWT
            String jwtToken = token.substring(7); // Enlever "Bearer "
            Long userId = jwtUtil.extractUserId(jwtToken);
            
            System.out.println("Current User ID: " + userId);
            
            List<Contact> contacts = contactService.listContacts(userId);
            
            // Enrichir avec email/username
            List<ContactResponse> contactResponses = contacts.stream().map(c -> {
                User u = userRepository.findById(c.getContactUserId()).orElse(null);
                return new ContactResponse(
                    c.getId(),
                    c.getContactUserId(),
                    c.getAlias(),
                    u != null ? u.getEmail() : null,
                    u != null ? u.getUsername() : null
                );
            }).toList();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Contacts récupérés avec succès");
            response.put("contacts", contactResponses);
            response.put("count", contactResponses.size());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            System.err.println("List contacts error: " + e.getMessage());
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
     * Supprimer un contact
     */
    @DeleteMapping("/{contactId}")
    public ResponseEntity<?> deleteContact(
            @PathVariable Long contactId,
            @RequestHeader("Authorization") String token) {
        
        try {
            System.out.println("=== DELETE CONTACT REQUEST RECEIVED ===");
            System.out.println("Contact ID to delete: " + contactId);
            
            // Extraire l'ID de l'utilisateur depuis le token JWT
            String jwtToken = token.substring(7); // Enlever "Bearer "
            Long userId = jwtUtil.extractUserId(jwtToken);
            
            System.out.println("Current User ID: " + userId);
            
            contactService.deleteContact(contactId, userId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Contact supprimé avec succès");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            System.err.println("Delete contact error: " + e.getMessage());
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
     * Obtenir un contact spécifique
     */
    @GetMapping("/{contactId}")
    public ResponseEntity<?> getContact(
            @PathVariable Long contactId,
            @RequestHeader("Authorization") String token) {
        
        try {
            System.out.println("=== GET CONTACT REQUEST RECEIVED ===");
            System.out.println("Contact ID: " + contactId);
            
            // Extraire l'ID de l'utilisateur depuis le token JWT
            String jwtToken = token.substring(7); // Enlever "Bearer "
            Long userId = jwtUtil.extractUserId(jwtToken);
            
            System.out.println("Current User ID: " + userId);
            
            // Récupérer tous les contacts de l'utilisateur et filtrer
            List<Contact> contacts = contactService.listContacts(userId);
            Contact contact = contacts.stream()
                    .filter(c -> c.getId().equals(contactId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Contact non trouvé"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Contact récupéré avec succès");
            response.put("contact", contact);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            System.err.println("Get contact error: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            System.err.println("Internal server error: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur interne du serveur");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{contactId}")
    public ResponseEntity<?> updateContactAlias(@PathVariable Long contactId, @RequestBody Map<String, String> body, @RequestHeader("Authorization") String token) {
        try {
            String alias = body.get("alias");
            if (alias == null) throw new RuntimeException("Alias manquant");
            String jwtToken = token.substring(7);
            Long userId = jwtUtil.extractUserId(jwtToken);
            Contact updated = contactService.updateContactAlias(contactId, userId, alias);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Alias modifié avec succès");
            response.put("contact", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
} 