package com.gamermajilis.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(min = 3, max = 200)
    @Column(name = "title", nullable = false)
    private String title;
    
    @NotBlank
    @Size(max = 5000)
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @NotNull
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;
    
    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType = EventType.COMMUNITY_GATHERING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false)
    private EventLocationType locationType = EventLocationType.VIRTUAL;
    
    // Location details
    @Column(name = "virtual_link", length = 500)
    private String virtualLink;
    
    @Column(name = "virtual_platform")
    private String virtualPlatform; // Discord, Zoom, etc.
    
    @Column(name = "physical_address", length = 500)
    private String physicalAddress;
    
    @Column(name = "physical_venue")
    private String physicalVenue;
    
    @Column(name = "timezone")
    private String timezone;
    
    // Attendance settings
    @Column(name = "max_attendees")
    private Integer maxAttendees;
    
    @Column(name = "current_attendees", nullable = false)
    private Integer currentAttendees = 0;
    
    @Column(name = "requires_registration", nullable = false)
    private Boolean requiresRegistration = true;
    
    @Column(name = "registration_deadline")
    private LocalDateTime registrationDeadline;
    
    @Size(max = 2000)
    @Column(name = "registration_requirements", columnDefinition = "TEXT")
    private String registrationRequirements;
    
    // Event properties
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;
    
    @Column(name = "age_restriction")
    private Integer ageRestriction;
    
    @Column(name = "entry_fee", precision = 10, scale = 2)
    private java.math.BigDecimal entryFee;
    
    @Column(name = "currency", length = 3)
    private String currency = "USD";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status = EventStatus.DRAFT;
    
    // Gaming related
    @Column(name = "game_title")
    private String gameTitle;
    
    @Column(name = "game_category")
    private String gameCategory;
    
    @Column(name = "competitive", nullable = false)
    private Boolean competitive = false;
    
    // Organizer information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;
    
    @ElementCollection
    @CollectionTable(name = "event_moderators", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "moderator_id")
    private List<Long> moderatorIds = new ArrayList<>();
    
    // Attendees
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventAttendance> attendances = new ArrayList<>();
    
    // Media and content
    @Column(name = "banner_image_url")
    private String bannerImageUrl;
    
    @Column(name = "gallery_images", columnDefinition = "TEXT")
    private String galleryImages; // JSON array of image URLs
    
    @Column(name = "live_stream_url")
    private String liveStreamUrl;
    
    @Column(name = "recording_enabled", nullable = false)
    private Boolean recordingEnabled = false;
    
    // Notifications and reminders
    @Column(name = "reminder_sent_24h", nullable = false)
    private Boolean reminderSent24h = false;
    
    @Column(name = "reminder_sent_1h", nullable = false)
    private Boolean reminderSent1h = false;
    
    @Column(name = "notification_settings", columnDefinition = "TEXT")
    private String notificationSettings; // JSON format
    
    // Event settings and metadata
    @Column(name = "settings", columnDefinition = "TEXT")
    private String settings; // JSON format for additional settings
    
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags; // JSON array of tags
    
    // Statistics
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;
    
    @Column(name = "interested_count", nullable = false)
    private Long interestedCount = 0L;
    
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
    public Event() {}
    
    public Event(String title, String description, LocalDateTime startDateTime, User organizer) {
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.organizer = organizer;
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
    
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }
    
    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }
    
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }
    
    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
    
    public EventType getEventType() {
        return eventType;
    }
    
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
    
    public EventLocationType getLocationType() {
        return locationType;
    }
    
    public void setLocationType(EventLocationType locationType) {
        this.locationType = locationType;
    }
    
    public String getVirtualLink() {
        return virtualLink;
    }
    
    public void setVirtualLink(String virtualLink) {
        this.virtualLink = virtualLink;
    }
    
    public String getVirtualPlatform() {
        return virtualPlatform;
    }
    
    public void setVirtualPlatform(String virtualPlatform) {
        this.virtualPlatform = virtualPlatform;
    }
    
    public String getPhysicalAddress() {
        return physicalAddress;
    }
    
    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }
    
    public String getPhysicalVenue() {
        return physicalVenue;
    }
    
    public void setPhysicalVenue(String physicalVenue) {
        this.physicalVenue = physicalVenue;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public Integer getMaxAttendees() {
        return maxAttendees;
    }
    
    public void setMaxAttendees(Integer maxAttendees) {
        this.maxAttendees = maxAttendees;
    }
    
    public Integer getCurrentAttendees() {
        return currentAttendees;
    }
    
    public void setCurrentAttendees(Integer currentAttendees) {
        this.currentAttendees = currentAttendees;
    }
    
    public Boolean getRequiresRegistration() {
        return requiresRegistration;
    }
    
    public void setRequiresRegistration(Boolean requiresRegistration) {
        this.requiresRegistration = requiresRegistration;
    }
    
    public LocalDateTime getRegistrationDeadline() {
        return registrationDeadline;
    }
    
    public void setRegistrationDeadline(LocalDateTime registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }
    
    public String getRegistrationRequirements() {
        return registrationRequirements;
    }
    
    public void setRegistrationRequirements(String registrationRequirements) {
        this.registrationRequirements = registrationRequirements;
    }
    
    public Boolean getIsPublic() {
        return isPublic;
    }
    
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public Integer getAgeRestriction() {
        return ageRestriction;
    }
    
    public void setAgeRestriction(Integer ageRestriction) {
        this.ageRestriction = ageRestriction;
    }
    
    public java.math.BigDecimal getEntryFee() {
        return entryFee;
    }
    
    public void setEntryFee(java.math.BigDecimal entryFee) {
        this.entryFee = entryFee;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public EventStatus getStatus() {
        return status;
    }
    
    public void setStatus(EventStatus status) {
        this.status = status;
    }
    
    public String getGameTitle() {
        return gameTitle;
    }
    
    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }
    
    public String getGameCategory() {
        return gameCategory;
    }
    
    public void setGameCategory(String gameCategory) {
        this.gameCategory = gameCategory;
    }
    
    public Boolean getCompetitive() {
        return competitive;
    }
    
    public void setCompetitive(Boolean competitive) {
        this.competitive = competitive;
    }
    
    public User getOrganizer() {
        return organizer;
    }
    
    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }
    
    public List<Long> getModeratorIds() {
        return moderatorIds;
    }
    
    public void setModeratorIds(List<Long> moderatorIds) {
        this.moderatorIds = moderatorIds;
    }
    
    public List<EventAttendance> getAttendances() {
        return attendances;
    }
    
    public void setAttendances(List<EventAttendance> attendances) {
        this.attendances = attendances;
    }
    
    public String getBannerImageUrl() {
        return bannerImageUrl;
    }
    
    public void setBannerImageUrl(String bannerImageUrl) {
        this.bannerImageUrl = bannerImageUrl;
    }
    
    public String getGalleryImages() {
        return galleryImages;
    }
    
    public void setGalleryImages(String galleryImages) {
        this.galleryImages = galleryImages;
    }
    
    public String getLiveStreamUrl() {
        return liveStreamUrl;
    }
    
    public void setLiveStreamUrl(String liveStreamUrl) {
        this.liveStreamUrl = liveStreamUrl;
    }
    
    public Boolean getRecordingEnabled() {
        return recordingEnabled;
    }
    
    public void setRecordingEnabled(Boolean recordingEnabled) {
        this.recordingEnabled = recordingEnabled;
    }
    
    public Boolean getReminderSent24h() {
        return reminderSent24h;
    }
    
    public void setReminderSent24h(Boolean reminderSent24h) {
        this.reminderSent24h = reminderSent24h;
    }
    
    public Boolean getReminderSent1h() {
        return reminderSent1h;
    }
    
    public void setReminderSent1h(Boolean reminderSent1h) {
        this.reminderSent1h = reminderSent1h;
    }
    
    public String getNotificationSettings() {
        return notificationSettings;
    }
    
    public void setNotificationSettings(String notificationSettings) {
        this.notificationSettings = notificationSettings;
    }
    
    public String getSettings() {
        return settings;
    }
    
    public void setSettings(String settings) {
        this.settings = settings;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public Long getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }
    
    public Long getInterestedCount() {
        return interestedCount;
    }
    
    public void setInterestedCount(Long interestedCount) {
        this.interestedCount = interestedCount;
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
    
    public boolean isActive() {
        return this.status == EventStatus.ACTIVE;
    }
    
    public boolean hasStarted() {
        return LocalDateTime.now().isAfter(this.startDateTime);
    }
    
    public boolean hasEnded() {
        LocalDateTime now = LocalDateTime.now();
        return this.endDateTime != null && now.isAfter(this.endDateTime);
    }
    
    public boolean isRegistrationOpen() {
        LocalDateTime now = LocalDateTime.now();
        return this.status == EventStatus.REGISTRATION_OPEN 
               && (registrationDeadline == null || now.isBefore(registrationDeadline))
               && (maxAttendees == null || currentAttendees < maxAttendees);
    }
    
    public boolean canRegister() {
        return isRegistrationOpen() && !hasStarted();
    }
    
    public boolean isFull() {
        return maxAttendees != null && currentAttendees >= maxAttendees;
    }
    
    public boolean isVirtual() {
        return this.locationType == EventLocationType.VIRTUAL;
    }
    
    public boolean isPhysical() {
        return this.locationType == EventLocationType.PHYSICAL;
    }
    
    public boolean isHybrid() {
        return this.locationType == EventLocationType.HYBRID;
    }
    
    public void incrementAttendeeCount() {
        this.currentAttendees++;
    }
    
    public void decrementAttendeeCount() {
        if (this.currentAttendees > 0) {
            this.currentAttendees--;
        }
    }
    
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    public void incrementInterestedCount() {
        this.interestedCount++;
    }
    
    public boolean needsReminder24h() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminder24h = this.startDateTime.minusHours(24);
        return !this.reminderSent24h && now.isAfter(reminder24h) && now.isBefore(this.startDateTime);
    }
    
    public boolean needsReminder1h() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminder1h = this.startDateTime.minusHours(1);
        return !this.reminderSent1h && now.isAfter(reminder1h) && now.isBefore(this.startDateTime);
    }
    
    public boolean canModify() {
        return this.status == EventStatus.DRAFT || this.status == EventStatus.REGISTRATION_OPEN;
    }
} 