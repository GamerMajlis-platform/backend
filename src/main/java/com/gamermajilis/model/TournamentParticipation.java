package com.gamermajilis.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tournament_participations", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"tournament_id", "participant_id"}))
public class TournamentParticipation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParticipationStatus status = ParticipationStatus.REGISTERED;
    
    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;
    
    @Column(name = "seed_number")
    private Integer seedNumber;
    
    @Column(name = "bracket_position")
    private Integer bracketPosition;
    
    // Results tracking
    @Column(name = "matches_played", nullable = false)
    private Integer matchesPlayed = 0;
    
    @Column(name = "matches_won", nullable = false)
    private Integer matchesWon = 0;
    
    @Column(name = "matches_lost", nullable = false)
    private Integer matchesLost = 0;
    
    @Column(name = "points")
    private Integer points = 0;
    
    @Column(name = "final_placement")
    private Integer finalPlacement;
    
    // Additional participant data
    @Column(name = "team_name")
    private String teamName;
    
    @Column(name = "participant_notes", columnDefinition = "TEXT")
    private String participantNotes;
    
    @Column(name = "checked_in", nullable = false)
    private Boolean checkedIn = false;
    
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;
    
    @Column(name = "disqualified", nullable = false)
    private Boolean disqualified = false;
    
    @Column(name = "disqualification_reason")
    private String disqualificationReason;
    
    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public TournamentParticipation() {}
    
    public TournamentParticipation(Tournament tournament, User participant) {
        this.tournament = tournament;
        this.participant = participant;
        this.registrationDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Tournament getTournament() {
        return tournament;
    }
    
    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }
    
    public User getParticipant() {
        return participant;
    }
    
    public void setParticipant(User participant) {
        this.participant = participant;
    }
    
    public ParticipationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ParticipationStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public Integer getSeedNumber() {
        return seedNumber;
    }
    
    public void setSeedNumber(Integer seedNumber) {
        this.seedNumber = seedNumber;
    }
    
    public Integer getBracketPosition() {
        return bracketPosition;
    }
    
    public void setBracketPosition(Integer bracketPosition) {
        this.bracketPosition = bracketPosition;
    }
    
    public Integer getMatchesPlayed() {
        return matchesPlayed;
    }
    
    public void setMatchesPlayed(Integer matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }
    
    public Integer getMatchesWon() {
        return matchesWon;
    }
    
    public void setMatchesWon(Integer matchesWon) {
        this.matchesWon = matchesWon;
    }
    
    public Integer getMatchesLost() {
        return matchesLost;
    }
    
    public void setMatchesLost(Integer matchesLost) {
        this.matchesLost = matchesLost;
    }
    
    public Integer getPoints() {
        return points;
    }
    
    public void setPoints(Integer points) {
        this.points = points;
    }
    
    public Integer getFinalPlacement() {
        return finalPlacement;
    }
    
    public void setFinalPlacement(Integer finalPlacement) {
        this.finalPlacement = finalPlacement;
    }
    
    public String getTeamName() {
        return teamName;
    }
    
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    
    public String getParticipantNotes() {
        return participantNotes;
    }
    
    public void setParticipantNotes(String participantNotes) {
        this.participantNotes = participantNotes;
    }
    
    public Boolean getCheckedIn() {
        return checkedIn;
    }
    
    public void setCheckedIn(Boolean checkedIn) {
        this.checkedIn = checkedIn;
    }
    
    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }
    
    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }
    
    public Boolean getDisqualified() {
        return disqualified;
    }
    
    public void setDisqualified(Boolean disqualified) {
        this.disqualified = disqualified;
    }
    
    public String getDisqualificationReason() {
        return disqualificationReason;
    }
    
    public void setDisqualificationReason(String disqualificationReason) {
        this.disqualificationReason = disqualificationReason;
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
    
    // Helper methods
    public boolean isActive() {
        return this.status == ParticipationStatus.CONFIRMED && !this.disqualified;
    }
    
    public boolean isCheckedIn() {
        return this.checkedIn;
    }
    
    public void checkIn() {
        this.checkedIn = true;
        this.checkInTime = LocalDateTime.now();
        if (this.status == ParticipationStatus.REGISTERED) {
            this.status = ParticipationStatus.CONFIRMED;
        }
    }
    
    public void disqualify(String reason) {
        this.disqualified = true;
        this.disqualificationReason = reason;
        this.status = ParticipationStatus.DISQUALIFIED;
    }
    
    public void addMatchResult(boolean won) {
        this.matchesPlayed++;
        if (won) {
            this.matchesWon++;
        } else {
            this.matchesLost++;
        }
    }
    
    public double getWinRate() {
        if (matchesPlayed == 0) {
            return 0.0;
        }
        return (double) matchesWon / matchesPlayed;
    }
} 