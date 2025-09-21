package com.gamermajilis.repository;

import com.gamermajilis.model.Event;
import com.gamermajilis.model.EventStatus;
import com.gamermajilis.model.EventType;
import com.gamermajilis.model.EventLocationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    // Find events by organizer
    Page<Event> findByOrganizerIdAndDeletedAtIsNullOrderByStartDateTimeDesc(Long organizerId, Pageable pageable);
    
    // Find active events
    Page<Event> findByStatusAndDeletedAtIsNullOrderByStartDateTimeAsc(EventStatus status, Pageable pageable);
    
    // Find public events
    Page<Event> findByIsPublicTrueAndStatusAndDeletedAtIsNullOrderByStartDateTimeAsc(
        EventStatus status, Pageable pageable);
    
    // Find events by type
    Page<Event> findByEventTypeAndStatusAndDeletedAtIsNullOrderByStartDateTimeAsc(
        EventType eventType, EventStatus status, Pageable pageable);
    
    // Find events by location type
    Page<Event> findByLocationTypeAndStatusAndDeletedAtIsNullOrderByStartDateTimeAsc(
        EventLocationType locationType, EventStatus status, Pageable pageable);
    
    // Find events by game category
    Page<Event> findByGameCategoryAndStatusAndDeletedAtIsNullOrderByStartDateTimeAsc(
        String gameCategory, EventStatus status, Pageable pageable);
    
    // Find upcoming events
    @Query("SELECT e FROM Event e WHERE e.deletedAt IS NULL AND e.status IN ('REGISTRATION_OPEN', 'ACTIVE') " +
           "AND e.startDateTime > :now ORDER BY e.startDateTime ASC")
    Page<Event> findUpcomingEvents(@Param("now") LocalDateTime now, Pageable pageable);
    
    // Find events happening today
    @Query("SELECT e FROM Event e WHERE e.deletedAt IS NULL AND e.status = 'ACTIVE' " +
           "AND DATE(e.startDateTime) = DATE(:date) ORDER BY e.startDateTime ASC")
    List<Event> findEventsToday(@Param("date") LocalDateTime date);
    
    // Find events in date range
    @Query("SELECT e FROM Event e WHERE e.deletedAt IS NULL AND e.status IN ('REGISTRATION_OPEN', 'ACTIVE') " +
           "AND e.startDateTime BETWEEN :startDate AND :endDate ORDER BY e.startDateTime ASC")
    Page<Event> findEventsInRange(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate, 
                                 Pageable pageable);
    
    // Find event by ID and organizer for authorization
    Optional<Event> findByIdAndOrganizerIdAndDeletedAtIsNull(Long id, Long organizerId);
    
    // Find public event by ID
    Optional<Event> findByIdAndIsPublicTrueAndDeletedAtIsNull(Long id);
    
    // Search events by title, description, or game
    @Query("SELECT e FROM Event e WHERE e.deletedAt IS NULL AND e.isPublic = true " +
           "AND e.status IN ('REGISTRATION_OPEN', 'ACTIVE') AND " +
           "(LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.gameTitle) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.gameCategory) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY e.startDateTime ASC")
    Page<Event> searchEvents(@Param("query") String query, Pageable pageable);
    
    // Search events with filters
    @Query("SELECT e FROM Event e WHERE e.deletedAt IS NULL AND e.isPublic = true " +
           "AND e.status IN ('REGISTRATION_OPEN', 'ACTIVE') " +
           "AND (:eventType IS NULL OR e.eventType = :eventType) " +
           "AND (:locationType IS NULL OR e.locationType = :locationType) " +
           "AND (LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY e.startDateTime ASC")
    Page<Event> searchEventsWithFilters(@Param("query") String query,
                                       @Param("eventType") EventType eventType,
                                       @Param("locationType") EventLocationType locationType,
                                       Pageable pageable);
    
    // Find trending events (most registrations)
    @Query("SELECT e FROM Event e WHERE e.deletedAt IS NULL AND e.isPublic = true " +
           "AND e.status IN ('REGISTRATION_OPEN', 'ACTIVE') AND e.startDateTime > :now " +
           "ORDER BY e.currentAttendees DESC, e.interestedCount DESC")
    List<Event> findTrendingEvents(@Param("now") LocalDateTime now, Pageable pageable);
    
    // Find events that need reminders
    @Query("SELECT e FROM Event e WHERE e.deletedAt IS NULL AND e.status = 'ACTIVE' " +
           "AND e.reminderSent24h = false AND e.startDateTime <= :reminder24h")
    List<Event> findEventsNeedingReminder24h(@Param("reminder24h") LocalDateTime reminder24h);
    
    @Query("SELECT e FROM Event e WHERE e.deletedAt IS NULL AND e.status = 'ACTIVE' " +
           "AND e.reminderSent1h = false AND e.startDateTime <= :reminder1h")
    List<Event> findEventsNeedingReminder1h(@Param("reminder1h") LocalDateTime reminder1h);
    
    // Count events by organizer
    long countByOrganizerIdAndDeletedAtIsNull(Long organizerId);
    
    // Find competitive events
    Page<Event> findByCompetitiveTrueAndStatusAndDeletedAtIsNullOrderByStartDateTimeAsc(
        EventStatus status, Pageable pageable);
}
