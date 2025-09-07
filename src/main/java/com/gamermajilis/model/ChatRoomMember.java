package com.gamermajilis.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_members", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"chat_room_id", "user_id"}))
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
    
    @Column(name = "is_muted", nullable = false)
    private Boolean isMuted = false;
    
    @Column(name = "muted_until")
    private LocalDateTime mutedUntil;
    
    @Column(name = "is_banned", nullable = false)
    private Boolean isBanned = false;
    
    @Column(name = "banned_until")
    private LocalDateTime bannedUntil;
    
    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;
    
    @Column(name = "unread_count", nullable = false)
    private Integer unreadCount = 0;
    
    @Column(name = "notification_enabled", nullable = false)
    private Boolean notificationEnabled = true;
    
    @CreationTimestamp
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
    
    @Column(name = "left_at")
    private LocalDateTime leftAt;
    
    // Constructors
    public ChatRoomMember() {}
    
    public ChatRoomMember(ChatRoom chatRoom, User user, ChatMemberRole role) {
        this.chatRoom = chatRoom;
        this.user = user;
        this.role = role;
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
    
    public Boolean getIsMuted() {
        return isMuted;
    }
    
    public void setIsMuted(Boolean isMuted) {
        this.isMuted = isMuted;
    }
    
    public LocalDateTime getMutedUntil() {
        return mutedUntil;
    }
    
    public void setMutedUntil(LocalDateTime mutedUntil) {
        this.mutedUntil = mutedUntil;
    }
    
    public Boolean getIsBanned() {
        return isBanned;
    }
    
    public void setIsBanned(Boolean isBanned) {
        this.isBanned = isBanned;
    }
    
    public Integer getUnreadCount() {
        return unreadCount;
    }
    
    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }
    
    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
    
    // Helper methods
    public boolean isActive() {
        return this.leftAt == null && !this.isBanned;
    }
    
    public boolean canSendMessages() {
        LocalDateTime now = LocalDateTime.now();
        return this.isActive() 
               && (!this.isMuted || (this.mutedUntil != null && now.isAfter(this.mutedUntil)));
    }
    
    public boolean isModerator() {
        return this.role == ChatMemberRole.MODERATOR || this.role == ChatMemberRole.ADMIN;
    }
    
    public boolean isAdmin() {
        return this.role == ChatMemberRole.ADMIN;
    }
    
    public void leave() {
        this.leftAt = LocalDateTime.now();
    }
    
    public void mute(LocalDateTime until) {
        this.isMuted = true;
        this.mutedUntil = until;
    }
    
    public void unmute() {
        this.isMuted = false;
        this.mutedUntil = null;
    }
    
    public void ban(LocalDateTime until) {
        this.isBanned = true;
        this.bannedUntil = until;
    }
    
    public void unban() {
        this.isBanned = false;
        this.bannedUntil = null;
    }
    
    public void incrementUnreadCount() {
        this.unreadCount++;
    }
    
    public void markAsRead() {
        this.unreadCount = 0;
    }
} 