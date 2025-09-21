package com.gamermajilis.repository;

import com.gamermajilis.model.ChatMessage;
import com.gamermajilis.model.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    // Find messages by chat room
    Page<ChatMessage> findByChatRoomIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long chatRoomId, Pageable pageable);
    
    // Find messages by sender
    Page<ChatMessage> findBySenderIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long senderId, Pageable pageable);
    
    // Find messages by chat room and sender
    Page<ChatMessage> findByChatRoomIdAndSenderIdAndDeletedAtIsNullOrderByCreatedAtDesc(
        Long chatRoomId, Long senderId, Pageable pageable);
    
    // Find messages after a specific message (for pagination)
    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = :roomId AND m.id > :messageId " +
           "AND m.deletedAt IS NULL ORDER BY m.createdAt ASC")
    Page<ChatMessage> findMessagesAfter(@Param("roomId") Long roomId, @Param("messageId") Long messageId, Pageable pageable);
    
    // Find messages before a specific message (for pagination)
    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = :roomId AND m.id < :messageId " +
           "AND m.deletedAt IS NULL ORDER BY m.createdAt DESC")
    Page<ChatMessage> findMessagesBefore(@Param("roomId") Long roomId, @Param("messageId") Long messageId, Pageable pageable);
    
    // Find message by ID and sender for authorization
    Optional<ChatMessage> findByIdAndSenderIdAndDeletedAtIsNull(Long id, Long senderId);
    
    // Find recent messages in chat room
    List<ChatMessage> findTop50ByChatRoomIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long chatRoomId);
    
    // Count messages in chat room
    long countByChatRoomIdAndDeletedAtIsNull(Long chatRoomId);
    
    // Count messages by sender
    long countBySenderIdAndDeletedAtIsNull(Long senderId);
    
    // Find messages by type
    Page<ChatMessage> findByChatRoomIdAndMessageTypeAndDeletedAtIsNullOrderByCreatedAtDesc(
        Long chatRoomId, MessageType messageType, Pageable pageable);
    
    // Find messages containing file attachments
    Page<ChatMessage> findByChatRoomIdAndFileUrlIsNotNullAndDeletedAtIsNullOrderByCreatedAtDesc(
        Long chatRoomId, Pageable pageable);
    
    // Search messages in chat room
    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = :roomId AND m.deletedAt IS NULL " +
           "AND LOWER(m.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY m.createdAt DESC")
    Page<ChatMessage> searchMessagesInRoom(@Param("roomId") Long roomId, @Param("query") String query, Pageable pageable);
    
    // Find messages in date range
    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = :roomId AND m.deletedAt IS NULL " +
           "AND m.createdAt BETWEEN :startDate AND :endDate ORDER BY m.createdAt DESC")
    Page<ChatMessage> findMessagesInDateRange(@Param("roomId") Long roomId, 
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate,
                                            Pageable pageable);
    
    // Find messages older than specified date (for cleanup)
    List<ChatMessage> findByChatRoomIdAndCreatedAtBefore(Long chatRoomId, LocalDateTime cutoffDate);
}
