package com.example.chatplatform.controller;

import com.example.chatplatform.dto.AddContactRequest;
import com.example.chatplatform.dto.UpdateContactRequest;
import com.example.chatplatform.entity.Contact;
import com.example.chatplatform.service.ContactService;
import com.example.chatplatform.service.UserService; // Pour récupérer l'ID de l'utilisateur authentifié
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contacts") // Tous les endpoints de ce contrôleur commenceront par /contacts
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService; // Utilisé pour obtenir l'ID de l'utilisateur authentifié

    /**
     * Récupère l'ID de l'utilisateur authentifié à partir du contexte de sécurité.
     * Nécessite que le filtre JWT ait déjà authentifié l'utilisateur.
     * @return L'ID de l'utilisateur authentifié.
     * @throws RuntimeException si l'utilisateur n'est pas authentifié ou si l'ID ne peut pas être récupéré.
     */
    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Utilisateur non authentifié.");
        }
        // Le principal est généralement le nom d'utilisateur (username)
        String username = authentication.getName();
        // Récupérer l'ID de l'utilisateur à partir du service utilisateur
        // Assurez-vous que votre UserService a une méthode pour cela, par exemple getUserByUsername
        return userService.getUserByUsername(username).getId();
    }

    /**
     * Endpoint pour ajouter un nouveau contact.
     * Requiert une authentification JWT.
     * @param request Les détails du contact à ajouter (contactUserId, alias).
     * @return Le contact ajouté avec un statut 201 Created.
     */
    @PostMapping
    public ResponseEntity<?> addContact(@Valid @RequestBody AddContactRequest request) {
        try {
            Long currentUserId = getAuthenticatedUserId();
            System.out.println("Requête d'ajout de contact reçue pour l'utilisateur ID: " + currentUserId);
            System.out.println("Contact à ajouter ID: " + request.getContactUserId() + ", Alias: " + request.getAlias());

            Contact newContact = contactService.addContact(request, currentUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newContact);
        } catch (RuntimeException e) {
            System.err.println("Erreur lors de l'ajout du contact: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error); // 400 Bad Request pour les erreurs métier
        } catch (Exception e) {
            System.err.println("Erreur interne du serveur lors de l'ajout du contact: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur interne du serveur lors de l'ajout du contact.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error); // 500 Internal Server Error
        }
    }

    /**
     * Endpoint pour lister tous les contacts de l'utilisateur authentifié.
     * Requiert une authentification JWT.
     * @return Une liste de contacts.
     */
    @GetMapping
    public ResponseEntity<?> listContacts() {
        try {
            Long currentUserId = getAuthenticatedUserId();
            System.out.println("Requête de liste des contacts reçue pour l'utilisateur ID: " + currentUserId);

            List<Contact> contacts = contactService.listContacts(currentUserId);
            return ResponseEntity.ok(contacts); // 200 OK
        } catch (RuntimeException e) {
            System.err.println("Erreur lors de la récupération des contacts: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error); // 404 Not Found si l'utilisateur n'existe pas
        } catch (Exception e) {
            System.err.println("Erreur interne du serveur lors de la récupération des contacts: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur interne du serveur lors de la récupération des contacts.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint pour supprimer un contact par son ID.
     * Requiert une authentification JWT.
     * @param contactId L'ID du contact à supprimer.
     * @return Une réponse vide avec un statut 204 No Content si la suppression est réussie.
     */
    @DeleteMapping("/{contactId}")
    public ResponseEntity<?> deleteContact(@PathVariable Long contactId) {
        try {
            Long currentUserId = getAuthenticatedUserId();
            System.out.println("Requête de suppression de contact reçue pour l'utilisateur ID: " + currentUserId + ", Contact ID: " + contactId);

            contactService.deleteContact(contactId, currentUserId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
        } catch (RuntimeException e) {
            System.err.println("Erreur lors de la suppression du contact: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            // 404 Not Found si le contact n'existe pas
            // 403 Forbidden si le contact n'appartient pas à l'utilisateur
            HttpStatus status = e.getMessage().contains("non trouvé") ? HttpStatus.NOT_FOUND : HttpStatus.FORBIDDEN;
            return ResponseEntity.status(status).body(error);
        } catch (Exception e) {
            System.err.println("Erreur interne du serveur lors de la suppression du contact: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur interne du serveur lors de la suppression du contact.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint pour modifier un contact existant.
     * Requiert une authentification JWT.
     * @param contactId L'ID du contact à modifier.
     * @param request Les données de mise à jour (actuellement, seul l'alias).
     * @return Le contact mis à jour avec un statut 200 OK.
     */
    @PutMapping("/{contactId}")
    public ResponseEntity<?> updateContact(@PathVariable Long contactId, @Valid @RequestBody UpdateContactRequest request) {
        try {
            Long currentUserId = getAuthenticatedUserId();
            System.out.println("Requête de modification de contact reçue pour l'utilisateur ID: " + currentUserId + ", Contact ID: " + contactId);
            System.out.println("Nouvel alias: " + request.getAlias());

            Contact updatedContact = contactService.updateContact(contactId, request, currentUserId);
            return ResponseEntity.ok(updatedContact); // 200 OK
        } catch (RuntimeException e) {
            System.err.println("Erreur lors de la modification du contact: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            // 404 Not Found si le contact n'existe pas
            // 403 Forbidden si le contact n'appartient pas à l'utilisateur
            HttpStatus status = e.getMessage().contains("non trouvé") ? HttpStatus.NOT_FOUND : HttpStatus.FORBIDDEN;
            return ResponseEntity.status(status).body(error);
        } catch (Exception e) {
            System.err.println("Erreur interne du serveur lors de la modification du contact: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur interne du serveur lors de la modification du contact.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}