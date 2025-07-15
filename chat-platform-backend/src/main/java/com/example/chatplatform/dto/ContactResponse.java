package com.example.chatplatform.dto;

public class ContactResponse {
    private Long id;
    private Long contactUserId;
    private String alias;
    private String email;
    private String username;

    public ContactResponse(Long id, Long contactUserId, String alias, String email, String username) {
        this.id = id;
        this.contactUserId = contactUserId;
        this.alias = alias;
        this.email = email;
        this.username = username;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getContactUserId() { return contactUserId; }
    public void setContactUserId(Long contactUserId) { this.contactUserId = contactUserId; }
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
} 