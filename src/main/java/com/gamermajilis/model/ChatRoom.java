package com.gamermajilis.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(min = 1, max = 100)
    @Column(name = "name", nullable = false)
    private String name;
    
    @Size(max = 500)
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ChatRoomType type = ChatRoomType.GROUP;
    
    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate = false;
    
    @Column(name = "max_members")
    private Integer maxMembers;
    
    @Column(name = "current_members", nullable = false)
    private Integer currentMembers = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
    
    @ElementCollection
    @CollectionTable(name = "chat_room_moderators", joinColumns = @JoinColumn(name = "chat_room_id"))
    @Column(name = "moderator_id")
    private List<Long> moderatorIds = new ArrayList<>();
    
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> messages = new ArrayList<>();
    
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatRoomMember> members = new ArrayList<>();
    
    // Chat settings
    @Column(name = "message_history_days", nullable = false)
    private Integer messageHistoryDays = 30;
    
    @Column(name = "allow_file_sharing", nullable = false)
    private Boolean allowFileSharing = true;
    
    @Column(name = "allow_emojis", nullable = false)
    private Boolean allowEmojis = true;
    
    @Column(name = "slow_mode_seconds")
    private Integer slowModeSeconds;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    // Gaming related
    @Column(name = "game_title")
    private String gameTitle;
    
    @Column(name = "tournament_id")
    private Long tournamentId;
    
    @Column(name = "event_id")
    private Long eventId;
    
    // Statistics
    @Column(name = "total_messages", nullable = false)
    private Long totalMessages = 0L;
    
    @Column(name = "last_activity")
    private LocalDateTime lastActivity;
    
    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Constructors
    public ChatRoom() {}
    
    public ChatRoom(String name, ChatRoomType type, User creator) {
        this.name = name;
        this.type = type;
        this.creator = creator;
        this.lastActivity = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ChatRoomType getType() {
        return type;
    }
    
    public void setType(ChatRoomType type) {
        this.type = type;
    }
    
    public Boolean getIsPrivate() {
        return isPrivate;
    }
    
    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
    
    public User getCreator() {
        return creator;
    }
    
    public void setCreator(User creator) {
        this.creator = creator;
    }
    
    public List<ChatMessage> getMessages() {
        return messages;
    }
    
    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
    
    public List<ChatRoomMember> getMembers() {
        return members;
    }
    
    public void setMembers(List<ChatRoomMember> members) {
        this.members = members;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
    
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
    
    public Integer getMaxMembers() {
        return maxMembers;
    }
    
    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }
    
    public Integer getCurrentMembers() {
        return currentMembers;
    }
    
    public void setCurrentMembers(Integer currentMembers) {
        this.currentMembers = currentMembers;
    }
    
    public List<Long> getModeratorIds() {
        return moderatorIds;
    }
    
    public void setModeratorIds(List<Long> moderatorIds) {
        this.moderatorIds = moderatorIds;
    }
    
    public Integer getMessageHistoryDays() {
        return messageHistoryDays;
    }
    
    public void setMessageHistoryDays(Integer messageHistoryDays) {
        this.messageHistoryDays = messageHistoryDays;
    }
    
    public Boolean getAllowFileSharing() {
        return allowFileSharing;
    }
    
    public void setAllowFileSharing(Boolean allowFileSharing) {
        this.allowFileSharing = allowFileSharing;
    }
    
    public Boolean getAllowEmojis() {
        return allowEmojis;
    }
    
    public void setAllowEmojis(Boolean allowEmojis) {
        this.allowEmojis = allowEmojis;
    }
    
    public Integer getSlowModeSeconds() {
        return slowModeSeconds;
    }
    
    public void setSlowModeSeconds(Integer slowModeSeconds) {
        this.slowModeSeconds = slowModeSeconds;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getGameTitle() {
        return gameTitle;
    }
    
    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }
    
    public Long getTournamentId() {
        return tournamentId;
    }
    
    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }
    
    public Long getEventId() {
        return eventId;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    public Long getTotalMessages() {
        return totalMessages;
    }
    
    public void setTotalMessages(Long totalMessages) {
        this.totalMessages = totalMessages;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    // Helper methods
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
    
    public boolean isDirectMessage() {
        return this.type == ChatRoomType.DIRECT_MESSAGE;
    }
    
    public boolean isGroupChat() {
        return this.type == ChatRoomType.GROUP;
    }
    
    public boolean isFull() {
        return this.maxMembers != null && this.currentMembers >= this.maxMembers;
    }
    
    public void incrementMemberCount() {
        this.currentMembers++;
    }
    
    public void decrementMemberCount() {
        if (this.currentMembers > 0) {
            this.currentMembers--;
        }
    }
    
    public void addMessage() {
        this.totalMessages++;
        this.lastActivity = LocalDateTime.now();
    }
    
    public boolean hasSlowMode() {
        return this.slowModeSeconds != null && this.slowModeSeconds > 0;
    }
} 