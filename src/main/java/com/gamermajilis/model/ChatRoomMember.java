package com.gamermajilis.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_members")
public class ChatRoomMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ChatMemberRole role = ChatMemberRole.MEMBER;
    
    @Column(name = "is_banned", nullable = false)
    private Boolean isBanned = false;
    
    @Column(name = "is_muted", nullable = false)
    private Boolean isMuted = false;
    
    @Column(name = "mute_until")
    private LocalDateTime muteUntil;
    
    @Column(name = "ban_reason")
    private String banReason;
    
    @Column(name = "last_seen")
    private LocalDateTime lastSeen;
    
    @CreationTimestamp
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public ChatRoomMember() {}
    
    public ChatRoomMember(ChatRoom chatRoom, User user, ChatMemberRole role) {
        this.chatRoom = chatRoom;
        this.user = user;
        this.role = role;
        this.lastSeen = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public ChatRoom getChatRoom() {
        return chatRoom;
    }
    
    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public ChatMemberRole getRole() {
        return role;
    }
    
    public void setRole(ChatMemberRole role) {
        this.role = role;
    }
    
    public Boolean getIsBanned() {
        return isBanned;
    }
    
    public void setIsBanned(Boolean isBanned) {
        this.isBanned = isBanned;
    }
    
    public Boolean getIsMuted() {
        return isMuted;
    }
    
    public void setIsMuted(Boolean isMuted) {
        this.isMuted = isMuted;
    }
    
    public LocalDateTime getMuteUntil() {
        return muteUntil;
    }
    
    public void setMuteUntil(LocalDateTime muteUntil) {
        this.muteUntil = muteUntil;
    }
    
    public LocalDateTime getLastSeen() {
        return lastSeen;
    }
    
    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }
    
    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
    
    // Helper methods
    public boolean isActive() {
        return !this.isBanned && (this.muteUntil == null || this.muteUntil.isBefore(LocalDateTime.now()));
    }
    
    public boolean canSendMessages() {
        return !this.isBanned && !this.isMuted && (this.muteUntil == null || this.muteUntil.isBefore(LocalDateTime.now()));
    }
    
    public void updateLastSeen() {
        this.lastSeen = LocalDateTime.now();
    }
}