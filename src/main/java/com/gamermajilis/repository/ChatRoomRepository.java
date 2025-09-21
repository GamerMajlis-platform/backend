package com.gamermajilis.repository;

import com.gamermajilis.model.ChatRoom;
import com.gamermajilis.model.ChatRoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    // Find chat rooms by creator
    Page<ChatRoom> findByCreatorIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long creatorId, Pageable pageable);
    
    // Find chat rooms that user is member of
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m WHERE m.user.id = :userId AND cr.deletedAt IS NULL ORDER BY cr.lastActivity DESC")
    Page<ChatRoom> findChatRoomsForUser(@Param("userId") Long userId, Pageable pageable);
    
    // Find active chat rooms
    Page<ChatRoom> findByIsActiveTrueAndDeletedAtIsNullOrderByLastActivityDesc(Pageable pageable);
    
    // Find public chat rooms
    Page<ChatRoom> findByIsPrivateFalseAndIsActiveTrueAndDeletedAtIsNullOrderByLastActivityDesc(Pageable pageable);
    
    // Find chat rooms by type
    Page<ChatRoom> findByTypeAndIsActiveTrueAndDeletedAtIsNullOrderByLastActivityDesc(ChatRoomType type, Pageable pageable);
    
    // Find chat rooms by game
    Page<ChatRoom> findByGameTitleAndIsActiveTrueAndDeletedAtIsNullOrderByLastActivityDesc(String gameTitle, Pageable pageable);
    
    // Find chat rooms by tournament
    Page<ChatRoom> findByTournamentIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long tournamentId, Pageable pageable);
    
    // Find chat rooms by event
    Page<ChatRoom> findByEventIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long eventId, Pageable pageable);
    
    // Find direct message room between two users
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m1 JOIN cr.members m2 " +
           "WHERE cr.type = 'DIRECT_MESSAGE' AND cr.deletedAt IS NULL " +
           "AND m1.user.id = :user1Id AND m2.user.id = :user2Id")
    Optional<ChatRoom> findDirectMessageRoom(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
    
    // Search chat rooms
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.deletedAt IS NULL AND cr.isPrivate = false AND cr.isActive = true " +
           "AND (LOWER(cr.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(cr.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(cr.gameTitle) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY cr.lastActivity DESC")
    Page<ChatRoom> searchPublicChatRooms(@Param("query") String query, Pageable pageable);
    
    // Find chat room by ID and check if user is member
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m WHERE cr.id = :roomId AND m.user.id = :userId AND cr.deletedAt IS NULL")
    Optional<ChatRoom> findChatRoomForUser(@Param("roomId") Long roomId, @Param("userId") Long userId);
    
    // Count chat rooms by creator
    long countByCreatorIdAndDeletedAtIsNull(Long creatorId);
}
