package com.gamermajilis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournaments")
public class Tournament {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(min = 3, max = 100)
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @NotBlank
    @Size(max = 2000)
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @NotBlank
    @Column(name = "game_title", nullable = false)
    private String gameTitle;
    
    @Column(name = "game_category")
    private String gameCategory;
    
    @NotNull
    @Positive
    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;
    
    @Column(name = "current_participants", nullable = false)
    private Integer currentParticipants = 0;
    
    @Column(name = "prize_pool", precision = 10, scale = 2)
    private BigDecimal prizePool;
    
    @Column(name = "prize_currency", length = 3)
    private String prizeCurrency = "USD";
    
    @Column(name = "entry_fee", precision = 10, scale = 2)
    private BigDecimal entryFee;
    
    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "registration_deadline")
    private LocalDateTime registrationDeadline;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Size(max = 5000)
    @Column(name = "rules", columnDefinition = "TEXT")
    private String rules;
    
    @Size(max = 5000)
    @Column(name = "regulations", columnDefinition = "TEXT")
    private String regulations;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TournamentStatus status = TournamentStatus.DRAFT;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tournament_type", nullable = false)
    private TournamentType tournamentType = TournamentType.SINGLE_ELIMINATION;
    
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;
    
    @Column(name = "requires_approval", nullable = false)
    private Boolean requiresApproval = false;
    
    @Column(name = "age_restriction")
    private Integer ageRestriction;
    
    @Column(name = "region_restriction")
    private String regionRestriction;
    
    // Tournament brackets and results
    @Column(name = "bracket_data", columnDefinition = "TEXT")
    private String bracketData; // JSON format for tournament bracket
    
    @Column(name = "current_round")
    private Integer currentRound = 0;
    
    @Column(name = "total_rounds")
    private Integer totalRounds;
    
    // Winner information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "verificationToken", "emailVerified", "banned", "banReason", "privacySettings", "authProvider", "updatedAt"})
    private User winner;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "runner_up_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "verificationToken", "emailVerified", "banned", "banReason", "privacySettings", "authProvider", "updatedAt"})
    private User runnerUp;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "third_place_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "verificationToken", "emailVerified", "banned", "banReason", "privacySettings", "authProvider", "updatedAt"})
    private User thirdPlace;
    
    // Organizer information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "verificationToken", "emailVerified", "banned", "banReason", "privacySettings", "authProvider", "updatedAt"})
    private User organizer;
    
    @ElementCollection
    @CollectionTable(name = "tournament_moderators", joinColumns = @JoinColumn(name = "tournament_id"))
    @Column(name = "moderator_id")
    private List<Long> moderatorIds = new ArrayList<>();
    
    // Participants
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"tournament", "hibernateLazyInitializer", "handler"})
    private List<TournamentParticipation> participations = new ArrayList<>();
    
    // Tournament settings
    @Column(name = "settings", columnDefinition = "TEXT")
    private String settings; // JSON format for additional settings
    
    // Statistics
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;
    
    @Column(name = "spectator_count", nullable = false)
    private Long spectatorCount = 0L;
    
    // Streaming and media
    @Column(name = "stream_url")
    private String streamUrl;
    
    @Column(name = "recording_enabled", nullable = false)
    private Boolean recordingEnabled = false;
    
    @Column(name = "live_updates_enabled", nullable = false)
    private Boolean liveUpdatesEnabled = true;
    
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
    public Tournament() {}
    
    public Tournament(String name, String description, String gameTitle, 
                     Integer maxParticipants, LocalDateTime startDate, User organizer) {
        this.name = name;
        this.description = description;
        this.gameTitle = gameTitle;
        this.maxParticipants = maxParticipants;
        this.startDate = startDate;
        this.organizer = organizer;
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
    
    public Integer getMaxParticipants() {
        return maxParticipants;
    }
    
    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
    
    public Integer getCurrentParticipants() {
        return currentParticipants;
    }
    
    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants;
    }
    
    public BigDecimal getPrizePool() {
        return prizePool;
    }
    
    public void setPrizePool(BigDecimal prizePool) {
        this.prizePool = prizePool;
    }
    
    public String getPrizeCurrency() {
        return prizeCurrency;
    }
    
    public void setPrizeCurrency(String prizeCurrency) {
        this.prizeCurrency = prizeCurrency;
    }
    
    public BigDecimal getEntryFee() {
        return entryFee;
    }
    
    public void setEntryFee(BigDecimal entryFee) {
        this.entryFee = entryFee;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getRegistrationDeadline() {
        return registrationDeadline;
    }
    
    public void setRegistrationDeadline(LocalDateTime registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public String getRules() {
        return rules;
    }
    
    public void setRules(String rules) {
        this.rules = rules;
    }
    
    public String getRegulations() {
        return regulations;
    }
    
    public void setRegulations(String regulations) {
        this.regulations = regulations;
    }
    
    public TournamentStatus getStatus() {
        return status;
    }
    
    public void setStatus(TournamentStatus status) {
        this.status = status;
    }
    
    public TournamentType getTournamentType() {
        return tournamentType;
    }
    
    public void setTournamentType(TournamentType tournamentType) {
        this.tournamentType = tournamentType;
    }
    
    public Boolean getIsPublic() {
        return isPublic;
    }
    
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public Boolean getRequiresApproval() {
        return requiresApproval;
    }
    
    public void setRequiresApproval(Boolean requiresApproval) {
        this.requiresApproval = requiresApproval;
    }
    
    public Integer getAgeRestriction() {
        return ageRestriction;
    }
    
    public void setAgeRestriction(Integer ageRestriction) {
        this.ageRestriction = ageRestriction;
    }
    
    public String getRegionRestriction() {
        return regionRestriction;
    }
    
    public void setRegionRestriction(String regionRestriction) {
        this.regionRestriction = regionRestriction;
    }
    
    public String getBracketData() {
        return bracketData;
    }
    
    public void setBracketData(String bracketData) {
        this.bracketData = bracketData;
    }
    
    public Integer getCurrentRound() {
        return currentRound;
    }
    
    public void setCurrentRound(Integer currentRound) {
        this.currentRound = currentRound;
    }
    
    public Integer getTotalRounds() {
        return totalRounds;
    }
    
    public void setTotalRounds(Integer totalRounds) {
        this.totalRounds = totalRounds;
    }
    
    public User getWinner() {
        return winner;
    }
    
    public void setWinner(User winner) {
        this.winner = winner;
    }
    
    public User getRunnerUp() {
        return runnerUp;
    }
    
    public void setRunnerUp(User runnerUp) {
        this.runnerUp = runnerUp;
    }
    
    public User getThirdPlace() {
        return thirdPlace;
    }
    
    public void setThirdPlace(User thirdPlace) {
        this.thirdPlace = thirdPlace;
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
    
    public List<TournamentParticipation> getParticipations() {
        return participations;
    }
    
    public void setParticipations(List<TournamentParticipation> participations) {
        this.participations = participations;
    }
    
    public String getSettings() {
        return settings;
    }
    
    public void setSettings(String settings) {
        this.settings = settings;
    }
    
    public Long getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }
    
    public Long getSpectatorCount() {
        return spectatorCount;
    }
    
    public void setSpectatorCount(Long spectatorCount) {
        this.spectatorCount = spectatorCount;
    }
    
    public String getStreamUrl() {
        return streamUrl;
    }
    
    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }
    
    public Boolean getRecordingEnabled() {
        return recordingEnabled;
    }
    
    public void setRecordingEnabled(Boolean recordingEnabled) {
        this.recordingEnabled = recordingEnabled;
    }
    
    public Boolean getLiveUpdatesEnabled() {
        return liveUpdatesEnabled;
    }
    
    public void setLiveUpdatesEnabled(Boolean liveUpdatesEnabled) {
        this.liveUpdatesEnabled = liveUpdatesEnabled;
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
        return this.status == TournamentStatus.ACTIVE;
    }
    
    public boolean isRegistrationOpen() {
        LocalDateTime now = LocalDateTime.now();
        return this.status == TournamentStatus.REGISTRATION_OPEN 
               && (registrationDeadline == null || now.isBefore(registrationDeadline))
               && currentParticipants < maxParticipants;
    }
    
    public boolean canParticipate() {
        return isRegistrationOpen() && this.status != TournamentStatus.CANCELLED;
    }
    
    public boolean hasStarted() {
        return LocalDateTime.now().isAfter(this.startDate);
    }
    
    public boolean isFinished() {
        return this.status == TournamentStatus.COMPLETED;
    }
    
    public void incrementParticipantCount() {
        this.currentParticipants++;
    }
    
    public void decrementParticipantCount() {
        if (this.currentParticipants > 0) {
            this.currentParticipants--;
        }
    }
    
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    public boolean isFull() {
        return this.currentParticipants >= this.maxParticipants;
    }
    
    public boolean canModify() {
        return this.status == TournamentStatus.DRAFT || this.status == TournamentStatus.REGISTRATION_OPEN;
    }
} 