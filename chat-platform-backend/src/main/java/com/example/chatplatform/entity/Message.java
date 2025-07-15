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

    @Column(nullable = false)
    @XmlElement
    private LocalDateTime sentAt;

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
} 