package com.example.chatplatform.entity;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@Entity
@Table(name = "contacts", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "contact_user_id"}) // Empêche un utilisateur d'ajouter le même contact deux fois
})
@XmlRootElement(name = "contact") // Annotation JAXB pour l'élément racine XML
@XmlAccessorType(XmlAccessType.FIELD) // Accède aux champs directement pour la sérialisation JAXB
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement // Pour inclure dans le XML
    private Long id;

    @Column(name = "user_id", nullable = false)
    @XmlElement(name = "userId") // L'ID de l'utilisateur qui possède ce contact
    private Long userId;

    @Column(name = "contact_user_id", nullable = false)
    @XmlElement(name = "contactUserId") // L'ID de l'utilisateur qui est le contact
    private Long contactUserId;

    @Column(name = "alias")
    @XmlElement // Un alias optionnel pour le contact
    private String alias;

    @Column(name = "added_at", nullable = false, updatable = false)
    @XmlElement(name = "addedAt") // Date et heure d'ajout du contact
    private LocalDateTime addedAt;

    // Constructeur par défaut requis par JPA et JAXB
    public Contact() {
        this.addedAt = LocalDateTime.now(); // Initialise la date d'ajout
    }

    // Constructeur avec paramètres (utile pour la création)
    public Contact(Long userId, Long contactUserId, String alias) {
        this(); // Appelle le constructeur par défaut pour initialiser addedAt
        this.userId = userId;
        this.contactUserId = contactUserId;
        this.alias = alias;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getContactUserId() {
        return contactUserId;
    }

    public void setContactUserId(Long contactUserId) {
        this.contactUserId = contactUserId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    @Override
    public String toString() {
        return "Contact{" +
               "id=" + id +
               ", userId=" + userId +
               ", contactUserId=" + contactUserId +
               ", alias='" + alias + '\'' +
               ", addedAt=" + addedAt +
               '}';
    }
}