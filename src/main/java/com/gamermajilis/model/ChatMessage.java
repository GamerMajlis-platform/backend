package com.gamermajilis.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @NotBlank
    @Size(max = 1000)
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType = MessageType.TEXT;
    
    // File attachments
    @Column(name = "file_url")
    private String fileUrl;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    // Message status and moderation
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    @Column(name = "is_edited", nullable = false)
    private Boolean isEdited = false;
    
    @Column(name = "toxicity_score")
    private Double toxicityScore;
    
    @Column(name = "is_flagged", nullable = false)
    private Boolean isFlagged = false;
    
    @Column(name = "flagged_reason")
    private String flaggedReason;
    
    // Reply functionality
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_message_id")
    private ChatMessage replyToMessage;
    
    // Reactions (stored as JSON)
    @Column(name = "reactions", columnDefinition = "TEXT")
    private String reactions; // JSON format: {"😀": ["userId1", "userId2"], "👍": ["userId3"]}
    
    @Column(name = "reaction_count", nullable = false)
    private Integer reactionCount = 0;
    
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
    public ChatMessage() {}
    
    public ChatMessage(ChatRoom chatRoom, User sender, String content, MessageType messageType) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.content = content;
        this.messageType = messageType;
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
    
    public User getSender() {
        return sender;
    }
    
    public void setSender(User sender) {
        this.sender = sender;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
        this.isEdited = true;
    }
    
    public MessageType getMessageType() {
        return messageType;
    }
    
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
        if (isDeleted) {
            this.deletedAt = LocalDateTime.now();
        }
    }
    
    public Double getToxicityScore() {
        return toxicityScore;
    }
    
    public void setToxicityScore(Double toxicityScore) {
        this.toxicityScore = toxicityScore;
    }
    
    public Boolean getIsFlagged() {
        return isFlagged;
    }
    
    public void setIsFlagged(Boolean isFlagged) {
        this.isFlagged = isFlagged;
    }
    
    public ChatMessage getReplyToMessage() {
        return replyToMessage;
    }
    
    public void setReplyToMessage(ChatMessage replyToMessage) {
        this.replyToMessage = replyToMessage;
    }
    
    public String getReactions() {
        return reactions;
    }
    
    public void setReactions(String reactions) {
        this.reactions = reactions;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public String getFileUrl() {
        return fileUrl;
    }
    
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public Boolean getIsEdited() {
        return isEdited;
    }
    
    public void setIsEdited(Boolean isEdited) {
        this.isEdited = isEdited;
    }
    
    public String getFlaggedReason() {
        return flaggedReason;
    }
    
    public void setFlaggedReason(String flaggedReason) {
        this.flaggedReason = flaggedReason;
    }
    
    public Integer getReactionCount() {
        return reactionCount;
    }
    
    public void setReactionCount(Integer reactionCount) {
        this.reactionCount = reactionCount;
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
    public boolean isReply() {
        return this.replyToMessage != null;
    }
    
    public boolean hasAttachment() {
        return this.fileUrl != null && !this.fileUrl.trim().isEmpty();
    }
    
    public boolean isToxic() {
        return this.toxicityScore != null && this.toxicityScore > 0.7;
    }
    
    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.content = "[Message deleted]";
    }
    
    public void flag(String reason) {
        this.isFlagged = true;
        this.flaggedReason = reason;
    }
} 