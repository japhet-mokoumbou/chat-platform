package com.example.chatplatform.dto;

import jakarta.validation.constraints.NotNull;

public class AddMemberRequest {
    @NotNull(message = "L'ID du membre est obligatoire")
    private Long userId;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
} 