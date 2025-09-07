package com.gamermajilis.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_attendances", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "attendee_id"}))
public class EventAttendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendee_id", nullable = false)
    private User attendee;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceStatus status = AttendanceStatus.REGISTERED;
    
    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;
    
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;
    
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;
    
    @Column(name = "attendance_notes", columnDefinition = "TEXT")
    private String attendanceNotes;
    
    @Column(name = "interested_only", nullable = false)
    private Boolean interestedOnly = false;
    
    @Column(name = "reminder_sent", nullable = false)
    private Boolean reminderSent = false;
    
    @Column(name = "feedback_rating")
    private Integer feedbackRating; // 1-5 rating
    
    @Column(name = "feedback_comment", columnDefinition = "TEXT")
    private String feedbackComment;
    
    @Column(name = "special_requirements")
    private String specialRequirements;
    
    @Column(name = "dietary_restrictions")
    private String dietaryRestrictions;
    
    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public EventAttendance() {}
    
    public EventAttendance(Event event, User attendee) {
        this.event = event;
        this.attendee = attendee;
        this.registrationDate = LocalDateTime.now();
    }
    
    public EventAttendance(Event event, User attendee, boolean interestedOnly) {
        this(event, attendee);
        this.interestedOnly = interestedOnly;
        if (interestedOnly) {
            this.status = AttendanceStatus.INTERESTED;
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Event getEvent() {
        return event;
    }
    
    public void setEvent(Event event) {
        this.event = event;
    }
    
    public User getAttendee() {
        return attendee;
    }
    
    public void setAttendee(User attendee) {
        this.attendee = attendee;
    }
    
    public AttendanceStatus getStatus() {
        return status;
    }
    
    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }
    
    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }
    
    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }
    
    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
    
    public String getAttendanceNotes() {
        return attendanceNotes;
    }
    
    public void setAttendanceNotes(String attendanceNotes) {
        this.attendanceNotes = attendanceNotes;
    }
    
    public Boolean getInterestedOnly() {
        return interestedOnly;
    }
    
    public void setInterestedOnly(Boolean interestedOnly) {
        this.interestedOnly = interestedOnly;
    }
    
    public Boolean getReminderSent() {
        return reminderSent;
    }
    
    public void setReminderSent(Boolean reminderSent) {
        this.reminderSent = reminderSent;
    }
    
    public Integer getFeedbackRating() {
        return feedbackRating;
    }
    
    public void setFeedbackRating(Integer feedbackRating) {
        this.feedbackRating = feedbackRating;
    }
    
    public String getFeedbackComment() {
        return feedbackComment;
    }
    
    public void setFeedbackComment(String feedbackComment) {
        this.feedbackComment = feedbackComment;
    }
    
    public String getSpecialRequirements() {
        return specialRequirements;
    }
    
    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
    }
    
    public String getDietaryRestrictions() {
        return dietaryRestrictions;
    }
    
    public void setDietaryRestrictions(String dietaryRestrictions) {
        this.dietaryRestrictions = dietaryRestrictions;
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
    public boolean isAttending() {
        return this.status == AttendanceStatus.CONFIRMED || this.status == AttendanceStatus.CHECKED_IN;
    }
    
    public boolean isCheckedIn() {
        return this.checkInTime != null;
    }
    
    public void checkIn() {
        this.checkInTime = LocalDateTime.now();
        this.status = AttendanceStatus.CHECKED_IN;
    }
    
    public void checkOut() {
        this.checkOutTime = LocalDateTime.now();
        this.status = AttendanceStatus.ATTENDED;
    }
    
    public void markAsInterested() {
        this.interestedOnly = true;
        this.status = AttendanceStatus.INTERESTED;
    }
    
    public void confirmAttendance() {
        this.interestedOnly = false;
        this.status = AttendanceStatus.CONFIRMED;
    }
    
    public void cancel() {
        this.status = AttendanceStatus.CANCELLED;
    }
    
    public boolean hasGivenFeedback() {
        return this.feedbackRating != null || this.feedbackComment != null;
    }
    
    public Long getAttendanceDurationMinutes() {
        if (checkInTime != null && checkOutTime != null) {
            return java.time.Duration.between(checkInTime, checkOutTime).toMinutes();
        }
        return null;
    }
} 