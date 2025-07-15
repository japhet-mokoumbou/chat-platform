package com.example.chatplatform.entity;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement
    private Long id;

    @Column(nullable = false)
    @XmlElement
    private Long senderId;

    @Column
    @XmlElement
    private Long receiverId; // Pour les messages privés

    @Column
    @XmlElement
    private Long groupId; // Pour les messages de groupe

    @Column(nullable = false)
    @XmlElement
    private String content;

    @Column(nullable = false)
    @XmlElement
    private String type; // "text" ou "file"

    @Column
    @XmlElement
    private String filePath; // Chemin du fichier si type = file

    @Column(name = "mime_type")
    @XmlElement
    private String mimeType; // Type MIME réel du fichier

    @Column(name = "file_size")
    @XmlElement
    private Long fileSize; // Taille du fichier en octets

    @Column(name = "width")
    @XmlElement
    private Integer width; // Largeur (pour images/vidéos)

    @Column(name = "height")
    @XmlElement
    private Integer height; // Hauteur (pour images/vidéos)

    @Column(name = "duration")
    @XmlElement
    private Double duration; // Durée en secondes (audio/vidéo)

    @Column(name = "thumbnail_path")
    @XmlElement
    private String thumbnailPath; // Chemin de la miniature

    @Column(nullable = false)
    @XmlElement
    private LocalDateTime sentAt;

    @Column(name = "delivered")
    @XmlElement
    private Boolean delivered = false;

    @Column(name = "read")
    @XmlElement
    private Boolean read = false;

    @Column(name = "delivered_at")
    @XmlElement
    private java.time.LocalDateTime deliveredAt;

    @Column(name = "read_at")
    @XmlElement
    private java.time.LocalDateTime readAt;

    @Column(name = "deleted")
    @XmlElement
    private Boolean deleted = false;

    @Column(name = "edited_at")
    @XmlElement
    private java.time.LocalDateTime editedAt;

    public Message() {
        this.sentAt = LocalDateTime.now();
    }

    public Message(Long senderId, Long receiverId, Long groupId, String content, String type, String filePath) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.groupId = groupId;
        this.content = content;
        this.type = type;
        this.filePath = filePath;
        this.sentAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public Boolean getDelivered() { return delivered; }
    public void setDelivered(Boolean delivered) { this.delivered = delivered; }
    public Boolean getRead() { return read; }
    public void setRead(Boolean read) { this.read = read; }
    public java.time.LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(java.time.LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
    public java.time.LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(java.time.LocalDateTime readAt) { this.readAt = readAt; }

    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
    public java.time.LocalDateTime getEditedAt() { return editedAt; }
    public void setEditedAt(java.time.LocalDateTime editedAt) { this.editedAt = editedAt; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    public Double getDuration() { return duration; }
    public void setDuration(Double duration) { this.duration = duration; }
    public String getThumbnailPath() { return thumbnailPath; }
    public void setThumbnailPath(String thumbnailPath) { this.thumbnailPath = thumbnailPath; }
} 