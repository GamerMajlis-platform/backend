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
@Table(name = "posts")
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(min = 1, max = 500)
    @Column(name = "title", nullable = false)
    private String title;
    
    @NotBlank
    @Size(min = 1, max = 10000)
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PostType type = PostType.TEXT;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    // Media attachments
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "post_media",
               joinColumns = @JoinColumn(name = "post_id"),
               inverseJoinColumns = @JoinColumn(name = "media_id"))
    private List<Media> attachedMedia = new ArrayList<>();
    
    // Gaming related
    @Column(name = "game_title")
    private String gameTitle;
    
    @Column(name = "game_category")
    private String gameCategory;
    
    @Column(name = "platform")
    private String platform;
    
    // Tags and categorization
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags; // JSON array of tags
    
    @Column(name = "hashtags", columnDefinition = "TEXT")
    private String hashtags; // JSON array of hashtags
    
    @Column(name = "mentions", columnDefinition = "TEXT")
    private String mentions; // JSON array of mentioned user IDs
    
    // Visibility and moderation
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private PostVisibility visibility = PostVisibility.PUBLIC;
    
    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;
    
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;
    
    @Column(name = "moderation_status", nullable = false)
    private String moderationStatus = "PENDING"; // PENDING, APPROVED, REJECTED
    
    @Column(name = "moderation_reason")
    private String moderationReason;
    
    @Column(name = "toxicity_score")
    private Double toxicityScore;
    
    @Column(name = "is_nsfw", nullable = false)
    private Boolean isNsfw = false;
    
    // Engagement metrics
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;
    
    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;
    
    @Column(name = "dislike_count", nullable = false)
    private Long dislikeCount = 0L;
    
    @Column(name = "comment_count", nullable = false)
    private Long commentCount = 0L;
    
    @Column(name = "share_count", nullable = false)
    private Long shareCount = 0L;
    
    // Comments
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostComment> comments = new ArrayList<>();
    
    // Reactions
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostReaction> reactions = new ArrayList<>();
    
    // Location and event association
    @Column(name = "location")
    private String location;
    
    @Column(name = "tournament_id")
    private Long tournamentId;
    
    @Column(name = "event_id")
    private Long eventId;
    
    // Scheduling
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
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
    public Post() {}
    
    public Post(String title, String content, User author, PostType type) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.type = type;
        this.publishedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public PostType getType() {
        return type;
    }
    
    public void setType(PostType type) {
        this.type = type;
    }
    
    public User getAuthor() {
        return author;
    }
    
    public void setAuthor(User author) {
        this.author = author;
    }
    
    public List<Media> getAttachedMedia() {
        return attachedMedia;
    }
    
    public void setAttachedMedia(List<Media> attachedMedia) {
        this.attachedMedia = attachedMedia;
    }
    
    public PostVisibility getVisibility() {
        return visibility;
    }
    
    public void setVisibility(PostVisibility visibility) {
        this.visibility = visibility;
    }
    
    public Long getViewCount() {
        return viewCount;
    }
    
    public Long getLikeCount() {
        return likeCount;
    }
    
    public List<PostComment> getComments() {
        return comments;
    }
    
    public void setComments(List<PostComment> comments) {
        this.comments = comments;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
    
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
    
    // Helper methods
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
    
    public boolean isPublished() {
        return this.publishedAt != null && this.publishedAt.isBefore(LocalDateTime.now());
    }
    
    public boolean isScheduled() {
        return this.scheduledAt != null && this.scheduledAt.isAfter(LocalDateTime.now());
    }
    
    public boolean isApproved() {
        return "APPROVED".equals(this.moderationStatus);
    }
    
    public boolean hasMedia() {
        return !this.attachedMedia.isEmpty();
    }
    
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    public void incrementLikeCount() {
        this.likeCount++;
    }
    
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
    
    public void incrementDislikeCount() {
        this.dislikeCount++;
    }
    
    public void decrementDislikeCount() {
        if (this.dislikeCount > 0) {
            this.dislikeCount--;
        }
    }
    
    public void incrementCommentCount() {
        this.commentCount++;
    }
    
    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }
    
    public void incrementShareCount() {
        this.shareCount++;
    }
    
    public void pin() {
        this.isPinned = true;
    }
    
    public void unpin() {
        this.isPinned = false;
    }
    
    public void feature() {
        this.isFeatured = true;
    }
    
    public void unfeature() {
        this.isFeatured = false;
    }
    
    public boolean isToxic() {
        return this.toxicityScore != null && this.toxicityScore > 0.7;
    }
    
    public void addMedia(Media media) {
        this.attachedMedia.add(media);
    }
    
    public void removeMedia(Media media) {
        this.attachedMedia.remove(media);
    }
} 