package com.gamermajilis.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "media")
public class Media {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 255)
    @Column(name = "title", nullable = false)
    private String title;
    
    @Size(max = 1000)
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @NotBlank
    @Column(name = "original_filename", nullable = false)
    private String originalFilename;
    
    @NotBlank
    @Column(name = "stored_filename", nullable = false, unique = true)
    private String storedFilename;
    
    @NotBlank
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @NotBlank
    @Column(name = "content_type", nullable = false)
    private String contentType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;
    
    @NotNull
    @Column(name = "file_size", nullable = false)
    private Long fileSize;
    
    @Column(name = "compressed_size")
    private Long compressedSize;
    
    @Column(name = "compression_ratio")
    private Double compressionRatio;
    
    @Column(name = "thumbnail_path")
    private String thumbnailPath;
    
    // Video specific fields
    @Column(name = "duration")
    private Long duration; // in seconds
    
    @Column(name = "resolution")
    private String resolution; // e.g., "1920x1080"
    
    // Image specific fields
    @Column(name = "width")
    private Integer width;
    
    @Column(name = "height")
    private Integer height;
    
    // Content moderation
    @Column(name = "is_nsfw", nullable = false)
    private Boolean isNsfw = false;
    
    @Column(name = "toxicity_score")
    private Double toxicityScore;
    
    @Column(name = "moderation_status", nullable = false)
    private String moderationStatus = "PENDING"; // PENDING, APPROVED, REJECTED
    
    @Column(name = "moderation_reason")
    private String moderationReason;
    
    // Tags and categorization
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags; // JSON array of tags
    
    @Column(name = "game_category")
    private String gameCategory;
    
    // Visibility and privacy
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private MediaVisibility visibility = MediaVisibility.PUBLIC;
    
    // Ownership
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;
    
    // Usage statistics
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;
    
    @Column(name = "download_count", nullable = false)
    private Long downloadCount = 0L;
    
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
    public Media() {}
    
    public Media(String title, String originalFilename, String storedFilename, 
                 String filePath, String contentType, MediaType mediaType, 
                 Long fileSize, User uploader) {
        this.title = title;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.filePath = filePath;
        this.contentType = contentType;
        this.mediaType = mediaType;
        this.fileSize = fileSize;
        this.uploader = uploader;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getOriginalFilename() {
        return originalFilename;
    }
    
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
    
    public String getStoredFilename() {
        return storedFilename;
    }
    
    public void setStoredFilename(String storedFilename) {
        this.storedFilename = storedFilename;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public MediaType getMediaType() {
        return mediaType;
    }
    
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public Long getCompressedSize() {
        return compressedSize;
    }
    
    public void setCompressedSize(Long compressedSize) {
        this.compressedSize = compressedSize;
    }
    
    public Double getCompressionRatio() {
        return compressionRatio;
    }
    
    public void setCompressionRatio(Double compressionRatio) {
        this.compressionRatio = compressionRatio;
    }
    
    public String getThumbnailPath() {
        return thumbnailPath;
    }
    
    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }
    
    public Long getDuration() {
        return duration;
    }
    
    public void setDuration(Long duration) {
        this.duration = duration;
    }
    
    public String getResolution() {
        return resolution;
    }
    
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
    
    public Integer getWidth() {
        return width;
    }
    
    public void setWidth(Integer width) {
        this.width = width;
    }
    
    public Integer getHeight() {
        return height;
    }
    
    public void setHeight(Integer height) {
        this.height = height;
    }
    
    public Boolean getIsNsfw() {
        return isNsfw;
    }
    
    public void setIsNsfw(Boolean isNsfw) {
        this.isNsfw = isNsfw;
    }
    
    public Double getToxicityScore() {
        return toxicityScore;
    }
    
    public void setToxicityScore(Double toxicityScore) {
        this.toxicityScore = toxicityScore;
    }
    
    public String getModerationStatus() {
        return moderationStatus;
    }
    
    public void setModerationStatus(String moderationStatus) {
        this.moderationStatus = moderationStatus;
    }
    
    public String getModerationReason() {
        return moderationReason;
    }
    
    public void setModerationReason(String moderationReason) {
        this.moderationReason = moderationReason;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public String getGameCategory() {
        return gameCategory;
    }
    
    public void setGameCategory(String gameCategory) {
        this.gameCategory = gameCategory;
    }
    
    public MediaVisibility getVisibility() {
        return visibility;
    }
    
    public void setVisibility(MediaVisibility visibility) {
        this.visibility = visibility;
    }
    
    public User getUploader() {
        return uploader;
    }
    
    public void setUploader(User uploader) {
        this.uploader = uploader;
    }
    
    public Long getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }
    
    public Long getDownloadCount() {
        return downloadCount;
    }
    
    public void setDownloadCount(Long downloadCount) {
        this.downloadCount = downloadCount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
    
    public boolean isApproved() {
        return "APPROVED".equals(this.moderationStatus);
    }
    
    public boolean isVideo() {
        return this.mediaType == MediaType.VIDEO;
    }
    
    public boolean isImage() {
        return this.mediaType == MediaType.IMAGE;
    }
    
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    public void incrementDownloadCount() {
        this.downloadCount++;
    }
} 