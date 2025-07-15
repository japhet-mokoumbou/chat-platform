package com.example.chatplatform.dto;

import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;

public class SendMessageRequest {
    // @NotNull(message = "L'ID du destinataire ou du groupe est obligatoire")
    private Long receiverId; // Peut être null si message de groupe

    private Long groupId; // Peut être null si message privé

    @NotBlank(message = "Le contenu du message est obligatoire")
    private String content;

    @NotBlank(message = "Le type de message est obligatoire")
    private String type; // "text" ou "file"

    private String filePath; // Optionnel, pour les fichiers

    // Getters et Setters
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
} 