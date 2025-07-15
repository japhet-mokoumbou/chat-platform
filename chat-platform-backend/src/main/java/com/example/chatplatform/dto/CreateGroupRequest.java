package com.example.chatplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public class CreateGroupRequest {
    @NotBlank(message = "Le nom du groupe est obligatoire")
    private String name;

    @NotEmpty(message = "La liste des membres ne peut pas être vide")
    private Set<Long> memberIds;

    // Getters et Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<Long> getMemberIds() { return memberIds; }
    public void setMemberIds(Set<Long> memberIds) { this.memberIds = memberIds; }
} 