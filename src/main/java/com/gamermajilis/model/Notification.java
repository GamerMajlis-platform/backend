package com.gamermajilis.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender; // Can be null for system notifications
    
    @NotBlank
    @Size(max = 255)
    @Column(name = "title", nullable = false)
    private String title;
    
    @NotBlank
    @Size(max = 1000)
    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;
    
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    // Related entity references
    @Column(name = "related_entity_type")
    private String relatedEntityType; // Post, Tournament, Event, etc.
    
    @Column(name = "related_entity_id")
    private Long relatedEntityId;
    
    @Column(name = "action_url")
    private String actionUrl; // URL to navigate to when notification is clicked
    
    // Notification settings
    @Column(name = "is_push_sent", nullable = false)
    private Boolean isPushSent = false;
    
    @Column(name = "is_email_sent", nullable = false)
    private Boolean isEmailSent = false;
    
    @Column(name = "priority", nullable = false)
    private Integer priority = 1; // 1=Low, 2=Medium, 3=High, 4=Critical
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    // Additional data (JSON format)
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // Additional context data in JSON format
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Constructors
    public Notification() {}
    
    public Notification(User recipient, String title, String message, NotificationType type) {
        this.recipient = recipient;
        this.title = title;
        this.message = message;
        this.type = type;
    }
    
    public Notification(User recipient, User sender, String title, String message, NotificationType type) {
        this(recipient, title, message, type);
        this.sender = sender;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getRecipient() {
        return recipient;
    }
    
    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }
    
    public User getSender() {
        return sender;
    }
    
    public void setSender(User sender) {
        this.sender = sender;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
        if (isRead && this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public String getRelatedEntityType() {
        return relatedEntityType;
    }
    
    public void setRelatedEntityType(String relatedEntityType) {
        this.relatedEntityType = relatedEntityType;
    }
    
    public Long getRelatedEntityId() {
        return relatedEntityId;
    }
    
    public void setRelatedEntityId(Long relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }
    
    public String getActionUrl() {
        return actionUrl;
    }
    
    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    // Helper methods
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
    
    public boolean isExpired() {
        return this.expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt);
    }
    
    public boolean isSystemNotification() {
        return this.sender == null;
    }
    
    public boolean isHighPriority() {
        return this.priority >= 3;
    }
    
    public boolean isCritical() {
        return this.priority == 4;
    }
    
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
    
    public void markAsPushSent() {
        this.isPushSent = true;
    }
    
    public void markAsEmailSent() {
        this.isEmailSent = true;
    }
    
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
    
    public void setRelatedEntity(String entityType, Long entityId) {
        this.relatedEntityType = entityType;
        this.relatedEntityId = entityId;
    }
} 