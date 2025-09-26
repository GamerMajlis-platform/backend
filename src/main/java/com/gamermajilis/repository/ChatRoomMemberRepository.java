package com.gamermajilis.repository;

import com.gamermajilis.model.ChatRoom;
import com.gamermajilis.model.ChatRoomMember;
import com.gamermajilis.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    
    // Find membership by chat room and user
    Optional<ChatRoomMember> findByChatRoomAndUser(ChatRoom chatRoom, User user);
    
    // Find membership by IDs
    Optional<ChatRoomMember> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);
    
    // Find all members of a chat room
    Page<ChatRoomMember> findByChatRoomOrderByJoinedAtAsc(ChatRoom chatRoom, Pageable pageable);
    
    // Find all members of a chat room (list version)
    List<ChatRoomMember> findByChatRoomOrderByJoinedAtAsc(ChatRoom chatRoom);
    
    // Find all chat rooms for a user
    @Query("SELECT crm FROM ChatRoomMember crm WHERE crm.user = :user AND crm.isBanned = false ORDER BY crm.chatRoom.lastActivity DESC")
    Page<ChatRoomMember> findByUserAndIsBannedFalseOrderByChatRoomLastActivityDesc(@Param("user") User user, Pageable pageable);
    
    // Check if user is member of chat room
    boolean existsByChatRoomAndUserAndIsBannedFalse(ChatRoom chatRoom, User user);
    
    // Count active members in a chat room
    @Query("SELECT COUNT(crm) FROM ChatRoomMember crm WHERE crm.chatRoom = :chatRoom AND crm.isBanned = false")
    long countByChatRoomAndIsBannedFalse(@Param("chatRoom") ChatRoom chatRoom);
    
    // Find admins and moderators of a chat room
    @Query("SELECT crm FROM ChatRoomMember crm WHERE crm.chatRoom = :chatRoom AND crm.role IN ('ADMIN', 'MODERATOR') AND crm.isBanned = false")
    List<ChatRoomMember> findAdminsAndModerators(@Param("chatRoom") ChatRoom chatRoom);
    
    // Find all active members of a chat room
    @Query("SELECT crm FROM ChatRoomMember crm WHERE crm.chatRoom = :chatRoom AND crm.isBanned = false ORDER BY crm.role DESC, crm.joinedAt ASC")
    List<ChatRoomMember> findActiveMembersByChatRoom(@Param("chatRoom") ChatRoom chatRoom);
    
    // Delete membership
    void deleteByChatRoomAndUser(ChatRoom chatRoom, User user);
    
    // Find members by chat room ID
    Page<ChatRoomMember> findByChatRoomIdOrderByJoinedAtAsc(Long chatRoomId, Pageable pageable);
}
