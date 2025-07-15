package com.example.chatplatform.entity;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "groups")
@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.FIELD)
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement
    private Long id;

    @Column(nullable = false)
    @XmlElement
    private String name;

    @Column(nullable = false)
    @XmlElement
    private Long creatorId;

    @Column(nullable = false)
    @XmlElement
    private LocalDateTime createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "group_members", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "member_id")
    @XmlElement(name = "memberId")
    private Set<Long> members = new HashSet<>();

    public Group() {
        this.createdAt = LocalDateTime.now();
    }

    public Group(String name, Long creatorId, Set<Long> members) {
        this.name = name;
        this.creatorId = creatorId;
        this.createdAt = LocalDateTime.now();
        this.members = members;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Set<Long> getMembers() { return members; }
    public void setMembers(Set<Long> members) { this.members = members; }
} 