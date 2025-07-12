package com.example.chatplatform.service;

import com.example.chatplatform.dto.AddContactRequest;
import com.example.chatplatform.dto.UpdateContactRequest;
import com.example.chatplatform.entity.Contact;
import com.example.chatplatform.entity.User;
import com.example.chatplatform.repository.ContactRepository;
import com.example.chatplatform.repository.UserRepository;
import com.example.chatplatform.util.XmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Pour la gestion des transactions

import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository; // Pour vérifier l'existence des utilisateurs

    @Autowired
    private XmlUtils xmlUtils;

    private static final String CONTACTS_XML_FILE = "contacts.xml";

    /**
     * Ajoute un nouveau contact pour un utilisateur donné.
     * @param request Les détails du contact à ajouter.
     * @param userId L'ID de l'utilisateur qui ajoute le contact.
     * @return Le contact ajouté.
     * @throws RuntimeException si l'utilisateur ou le contact n'existe pas, ou si le contact est déjà ajouté.
     */
    @Transactional // Assure que l'opération est atomique
    public Contact addContact(AddContactRequest request, Long userId) {
        // 1. Vérifier que l'utilisateur qui ajoute le contact existe
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

        // 2. Vérifier que l'utilisateur à ajouter comme contact existe
        User contactUser = userRepository.findById(request.getContactUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur contact non trouvé avec l'ID: " + request.getContactUserId()));

        // 3. Empêcher un utilisateur de s'ajouter lui-même comme contact
        if (userId.equals(request.getContactUserId())) {
            throw new RuntimeException("Vous ne pouvez pas vous ajouter vous-même comme contact.");
        }

        // 4. Vérifier si le contact existe déjà pour cet utilisateur
        if (contactRepository.findByUserIdAndContactUserId(userId, request.getContactUserId()).isPresent()) {
            throw new RuntimeException("Ce contact existe déjà pour cet utilisateur.");
        }

        // 5. Créer et sauvegarder le contact en base de données
        Contact newContact = new Contact(userId, request.getContactUserId(), request.getAlias());
        Contact savedContact = contactRepository.save(newContact);

        // 6. Sauvegarder tous les contacts en XML
        saveContactsToXml();

        return savedContact;
    }

    /**
     * Liste tous les contacts pour un utilisateur donné.
     * @param userId L'ID de l'utilisateur dont on veut lister les contacts.
     * @return Une liste de contacts.
     */
    public List<Contact> listContacts(Long userId) {
        // Optionnel: Vérifier que l'utilisateur existe avant de lister ses contacts
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId);
        }
        return contactRepository.findByUserId(userId);
    }

    /**
     * Supprime un contact pour un utilisateur donné.
     * @param contactId L'ID du contact à supprimer.
     * @param userId L'ID de l'utilisateur qui supprime le contact.
     * @throws RuntimeException si le contact n'existe pas ou n'appartient pas à l'utilisateur.
     */
    @Transactional
    public void deleteContact(Long contactId, Long userId) {
        // 1. Vérifier que le contact existe et appartient à l'utilisateur
        Contact contactToDelete = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact non trouvé avec l'ID: " + contactId));

        if (!contactToDelete.getUserId().equals(userId)) {
            throw new RuntimeException("Accès non autorisé: Ce contact n'appartient pas à l'utilisateur.");
        }

        // 2. Supprimer le contact de la base de données
        contactRepository.delete(contactToDelete);
        // Ou utiliser contactRepository.deleteByIdAndUserId(contactId, userId); si vous voulez une suppression plus directe et vérifiée par le repository.
        // long deletedCount = contactRepository.deleteByIdAndUserId(contactId, userId);
        // if (deletedCount == 0) { throw new RuntimeException("Contact non trouvé ou non autorisé."); }


        // 3. Sauvegarder tous les contacts en XML après la suppression
        saveContactsToXml();
    }

    /**
     * Méthode privée pour sauvegarder tous les contacts de la base de données en XML.
     * Cette méthode est appelée après chaque modification (ajout, suppression) de contact.
     */
    private void saveContactsToXml() {
        try {
            System.out.println("=== DEBUT SAUVEGARDE XML CONTACTS ===");
            List<Contact> allContacts = contactRepository.findAll(); // Récupère TOUS les contacts de la DB
            System.out.println("Nombre de contacts à sauvegarder en XML: " + allContacts.size());
            xmlUtils.saveToXml(allContacts, CONTACTS_XML_FILE, Contact.class);
            System.out.println("Sauvegarde XML des contacts réussie: " + CONTACTS_XML_FILE);
            System.out.println("=== FIN SAUVEGARDE XML CONTACTS ===");
        } catch (Exception e) {
            System.err.println("=== ERREUR SAUVEGARDE XML CONTACTS ===");
            System.err.println("Erreur lors de la sauvegarde XML des contacts: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== FIN ERREUR XML CONTACTS ===");
            // Il est important de ne pas relancer l'exception ici si la persistance XML est secondaire,
            // afin de ne pas faire échouer l'opération de base de données.
            // Si la persistance XML est critique, vous pouvez relancer une RuntimeException.
        }
    }


    /**
     * Met à jour un contact existant pour un utilisateur donné.
     * Actuellement, seul l'alias peut être modifié.
     * @param contactId L'ID du contact à modifier.
     * @param request Les nouvelles données du contact.
     * @param userId L'ID de l'utilisateur qui modifie le contact.
     * @return Le contact mis à jour.
     * @throws RuntimeException si le contact n'existe pas ou n'appartient pas à l'utilisateur.
     */
    @Transactional
    public Contact updateContact(Long contactId, UpdateContactRequest request, Long userId) {
        // 1. Trouver le contact existant
        Contact existingContact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact non trouvé avec l'ID: " + contactId));

        // 2. Vérifier que le contact appartient bien à l'utilisateur qui tente de le modifier
        if (!existingContact.getUserId().equals(userId)) {
            throw new RuntimeException("Accès non autorisé: Ce contact n'appartient pas à l'utilisateur.");
        }

        // 3. Appliquer les modifications (ici, seulement l'alias)
        if (request.getAlias() != null) {
            existingContact.setAlias(request.getAlias());
        }

        // 4. Sauvegarder le contact mis à jour en base de données
        Contact updatedContact = contactRepository.save(existingContact);

        // 5. Sauvegarder tous les contacts en XML après la modification
        saveContactsToXml();

        return updatedContact;
    }
}