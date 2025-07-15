package com.example.chatplatform.dto;

import java.util.List;

public class GroupResponse {
    private Long id;
    private String name;
    private Long creatorId;
    private String creatorUsername;
    private List<Long> members;
    private List<String> memberUsernames;

    public GroupResponse(Long id, String name, Long creatorId, String creatorUsername, List<Long> members, List<String> memberUsernames) {
        this.id = id;
        this.name = name;
        this.creatorId = creatorId;
        this.creatorUsername = creatorUsername;
        this.members = members;
        this.memberUsernames = memberUsernames;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public String getCreatorUsername() { return creatorUsername; }
    public void setCreatorUsername(String creatorUsername) { this.creatorUsername = creatorUsername; }
    public List<Long> getMembers() { return members; }
    public void setMembers(List<Long> members) { this.members = members; }
    public List<String> getMemberUsernames() { return memberUsernames; }
    public void setMemberUsernames(List<String> memberUsernames) { this.memberUsernames = memberUsernames; }
} 