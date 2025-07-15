package com.example.chatplatform.repository;

import com.example.chatplatform.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverId(Long receiverId);
    List<Message> findByGroupId(Long groupId);
    List<Message> findBySenderId(Long senderId);
} 