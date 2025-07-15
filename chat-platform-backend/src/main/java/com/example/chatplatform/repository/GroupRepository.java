package com.example.chatplatform.repository;

import com.example.chatplatform.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByCreatorId(Long creatorId);
    List<Group> findByMembersContaining(Long userId);
} 