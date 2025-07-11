package com.example.chatplatform.repository;

import com.example.chatplatform.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    /**
     * Récupère tous les contacts pour un utilisateur donné.
     * @param userId L'ID de l'utilisateur dont on veut les contacts.
     * @return Une liste de contacts.
     */
    List<Contact> findByUserId(Long userId);

    /**
     * Vérifie si un contact spécifique existe déjà pour un utilisateur donné.
     * @param userId L'ID de l'utilisateur propriétaire.
     * @param contactUserId L'ID de l'utilisateur qui est le contact.
     * @return Un Optional contenant le contact s'il existe.
     */
    Optional<Contact> findByUserIdAndContactUserId(Long userId, Long contactUserId);

    /**
     * Supprime un contact par son ID, en s'assurant qu'il appartient bien à l'utilisateur spécifié.
     * @param id L'ID du contact à supprimer.
     * @param userId L'ID de l'utilisateur propriétaire du contact.
     * @return Le nombre d'entités supprimées (0 ou 1).
     */
    long deleteByIdAndUserId(Long id, Long userId);
}