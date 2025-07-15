package com.example.chatplatform.repository;

import com.example.chatplatform.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverId(Long receiverId);
    List<Message> findByGroupId(Long groupId);
    List<Message> findBySenderId(Long senderId);
    java.util.Optional<Message> findByIdAndReceiverId(Long id, Long receiverId);

    // Pagination et soft delete
    Page<Message> findByReceiverIdAndDeletedFalseOrderBySentAtDesc(Long receiverId, Pageable pageable);
    Page<Message> findByGroupIdAndDeletedFalseOrderBySentAtDesc(Long groupId, Pageable pageable);
    Page<Message> findBySenderIdAndDeletedFalseOrderBySentAtDesc(Long senderId, Pageable pageable);

    // Récupérer tous les messages entre deux utilisateurs (dans les deux sens)
    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverId(Long senderId1, Long receiverId1, Long senderId2, Long receiverId2);

    // Récupérer tous les messages privés non supprimés entre deux utilisateurs, triés par date
    @Query("SELECT m FROM Message m WHERE ((m.senderId = :user1 AND m.receiverId = :user2) OR (m.senderId = :user2 AND m.receiverId = :user1)) AND m.groupId IS NULL AND m.deleted = false ORDER BY m.sentAt ASC")
    List<Message> findPrivateConversation(@Param("user1") Long user1, @Param("user2") Long user2);
} 