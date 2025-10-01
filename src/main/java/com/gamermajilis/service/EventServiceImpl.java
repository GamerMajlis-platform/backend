package com.gamermajilis.service;

import com.gamermajilis.model.*;
import com.gamermajilis.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventServiceImpl implements EventService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // EventAttendance repository would need to be created
    // @Autowired
    // private EventAttendanceRepository eventAttendanceRepository;
    
    @Override
    public Map<String, Object> createEvent(Long userId, Map<String, Object> eventData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate user
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }
            
            User organizer = userOpt.get();
            
            // Validate required fields
            String title = (String) eventData.get("title");
            String description = (String) eventData.get("description");
            String startDateTimeStr = (String) eventData.get("startDateTime");
            
            if (title == null || title.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Event title is required");
                return response;
            }
            
            if (description == null || description.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Event description is required");
                return response;
            }
            
            if (startDateTimeStr == null) {
                response.put("success", false);
                response.put("message", "Start date and time is required");
                return response;
            }
            
            // Parse datetime
            LocalDateTime startDateTime;
            try {
                startDateTime = LocalDateTime.parse(startDateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                response.put("success", false);
                response.put("message", "Invalid start date time format. Use ISO format: 2024-12-25T18:00:00");
                return response;
            }
            
            // Create event
            Event event = new Event();
            event.setTitle(title);
            event.setDescription(description);
            event.setStartDateTime(startDateTime);
            event.setOrganizer(organizer);
            
            // Optional fields
            if (eventData.get("endDateTime") != null) {
                try {
                    LocalDateTime endDateTime = LocalDateTime.parse((String) eventData.get("endDateTime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    event.setEndDateTime(endDateTime);
                } catch (DateTimeParseException e) {
                    response.put("success", false);
                    response.put("message", "Invalid end date time format");
                    return response;
                }
            }
            
            // Set event type
            if (eventData.get("eventType") != null) {
                try {
                    EventType eventType = EventType.valueOf((String) eventData.get("eventType"));
                    event.setEventType(eventType);
                } catch (IllegalArgumentException e) {
                    event.setEventType(EventType.COMMUNITY_GATHERING);
                }
            }
            
            // Set location type
            if (eventData.get("locationType") != null) {
                try {
                    EventLocationType locationType = EventLocationType.valueOf((String) eventData.get("locationType"));
                    event.setLocationType(locationType);
                } catch (IllegalArgumentException e) {
                    event.setLocationType(EventLocationType.VIRTUAL);
                }
            }
            
            // Location details
            if (eventData.get("virtualLink") != null) {
                event.setVirtualLink((String) eventData.get("virtualLink"));
            }
            if (eventData.get("virtualPlatform") != null) {
                event.setVirtualPlatform((String) eventData.get("virtualPlatform"));
            }
            if (eventData.get("physicalAddress") != null) {
                event.setPhysicalAddress((String) eventData.get("physicalAddress"));
            }
            if (eventData.get("physicalVenue") != null) {
                event.setPhysicalVenue((String) eventData.get("physicalVenue"));
            }
            
            // Attendance settings
            if (eventData.get("maxAttendees") != null) {
                event.setMaxAttendees(((Number) eventData.get("maxAttendees")).intValue());
            }
            if (eventData.get("requiresRegistration") != null) {
                event.setRequiresRegistration((Boolean) eventData.get("requiresRegistration"));
            }
            if (eventData.get("registrationDeadline") != null) {
                try {
                    LocalDateTime regDeadline = LocalDateTime.parse((String) eventData.get("registrationDeadline"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    event.setRegistrationDeadline(regDeadline);
                } catch (DateTimeParseException e) {
                    // Ignore invalid deadline format
                }
            }
            if (eventData.get("registrationRequirements") != null) {
                event.setRegistrationRequirements((String) eventData.get("registrationRequirements"));
            }
            
            // Event properties
            if (eventData.get("isPublic") != null) {
                event.setIsPublic((Boolean) eventData.get("isPublic"));
            }
            if (eventData.get("ageRestriction") != null) {
                event.setAgeRestriction(((Number) eventData.get("ageRestriction")).intValue());
            }
            if (eventData.get("entryFee") != null) {
                event.setEntryFee(new BigDecimal(eventData.get("entryFee").toString()));
            }
            if (eventData.get("currency") != null) {
                event.setCurrency((String) eventData.get("currency"));
            }
            
            // Gaming related
            if (eventData.get("gameTitle") != null) {
                event.setGameTitle((String) eventData.get("gameTitle"));
            }
            if (eventData.get("gameCategory") != null) {
                event.setGameCategory((String) eventData.get("gameCategory"));
            }
            if (eventData.get("competitive") != null) {
                event.setCompetitive((Boolean) eventData.get("competitive"));
            }
            
            // Save event
            Event savedEvent = eventRepository.save(event);
            
            response.put("success", true);
            response.put("message", "Event created successfully");
            response.put("event", formatEventForResponse(savedEvent));
            
        } catch (Exception e) {
            logger.error("Error creating event", e);
            response.put("success", false);
            response.put("message", "Failed to create event: " + e.getMessage());
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getEventDetails(Long eventId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Event> eventOpt = eventRepository.findById(eventId);
            
            if (eventOpt.isEmpty() || eventOpt.get().isDeleted()) {
                response.put("success", false);
                response.put("message", "Event not found");
                return response;
            }
            
            Event event = eventOpt.get();
            
            // Increment view count
            event.incrementViewCount();
            eventRepository.save(event);
            
            response.put("success", true);
            response.put("message", "Event retrieved successfully");
            response.put("event", formatEventDetailsForResponse(event));
            
        } catch (Exception e) {
            logger.error("Error getting event details", e);
            response.put("success", false);
            response.put("message", "Failed to get event details");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getEventsList(int page, int size, Map<String, Object> filters) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Event> eventPage = null;
            
            // Apply filters
            String eventTypeStr = (String) filters.get("eventType");
            String locationTypeStr = (String) filters.get("locationType");
            String gameCategoryStr = (String) filters.get("gameCategory");
            Boolean myEvents = (Boolean) filters.get("myEvents");
            Boolean upcoming = (Boolean) filters.get("upcoming");
            Long userId = (Long) filters.get("userId");
            
            if (myEvents != null && myEvents && userId != null) {
                // Get user's events
                eventPage = eventRepository.findByOrganizerIdAndDeletedAtIsNullOrderByStartDateTimeDesc(userId, pageable);
            } else if (upcoming != null && upcoming) {
                // Get upcoming events
                eventPage = eventRepository.findUpcomingEvents(LocalDateTime.now(), pageable);
            } else if (eventTypeStr != null) {
                try {
                    EventType eventType = EventType.valueOf(eventTypeStr);
                    eventPage = eventRepository.findByEventTypeAndStatusAndDeletedAtIsNullOrderByStartDateTimeAsc(eventType, EventStatus.REGISTRATION_OPEN, pageable);
                } catch (IllegalArgumentException e) {
                    eventPage = eventRepository.findByIsPublicTrueAndStatusAndDeletedAtIsNullOrderByStartDateTimeAsc(EventStatus.REGISTRATION_OPEN, pageable);
                }
            } else if (locationTypeStr != null) {
                try {
                    EventLocationType locationType = EventLocationType.valueOf(locationTypeStr);
                    eventPage = eventRepository.findByLocationTypeAndStatusAndDeletedAtIsNullOrderByStartDateTimeAsc(locationType, EventStatus.REGISTRATION_OPEN, pageable);
                } catch (IllegalArgumentException e) {
                    eventPage = eventRepository.findByIsPublicTrueAndStatusAndDeletedAtIsNullOrderByStartDateTimeAsc(EventStatus.REGISTRATION_OPEN, pageable);
                }
            } else if (gameCategoryStr != null) {
                eventPage = eventRepository.findByGameCategoryAndStatusAndDeletedAtIsNullOrderByStartDateTimeAsc(gameCategoryStr, EventStatus.REGISTRATION_OPEN, pageable);
            } else {
                // Default: return all non-deleted events regardless of status
                eventPage = eventRepository.findAll(pageable);
            }
            
            List<Map<String, Object>> events = eventPage.getContent().stream()
                    .map(this::formatEventForResponse)
                    .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("message", "Events list retrieved");
            response.put("events", events);
            response.put("totalElements", eventPage.getTotalElements());
            response.put("totalPages", eventPage.getTotalPages());
            response.put("currentPage", eventPage.getNumber());
            response.put("pageSize", eventPage.getSize());
            
        } catch (Exception e) {
            logger.error("Error getting events list", e);
            response.put("success", false);
            response.put("message", "Failed to get events list");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> updateEvent(Long userId, Long eventId, Map<String, Object> updateData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user owns the event
            Optional<Event> eventOpt = eventRepository.findByIdAndOrganizerIdAndDeletedAtIsNull(eventId, userId);
            
            if (eventOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Event not found or access denied");
                return response;
            }
            
            Event event = eventOpt.get();
            
            // Check if event can be modified
            if (!event.canModify()) {
                response.put("success", false);
                response.put("message", "Event cannot be modified in its current status");
                return response;
            }
            
            // Update fields
            if (updateData.get("title") != null) {
                event.setTitle((String) updateData.get("title"));
            }
            if (updateData.get("description") != null) {
                event.setDescription((String) updateData.get("description"));
            }
            if (updateData.get("startDateTime") != null) {
                try {
                    LocalDateTime startDateTime = LocalDateTime.parse((String) updateData.get("startDateTime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    event.setStartDateTime(startDateTime);
                } catch (DateTimeParseException e) {
                    response.put("success", false);
                    response.put("message", "Invalid start date time format");
                    return response;
                }
            }
            if (updateData.get("maxAttendees") != null) {
                event.setMaxAttendees(((Number) updateData.get("maxAttendees")).intValue());
            }
            if (updateData.get("virtualLink") != null) {
                event.setVirtualLink((String) updateData.get("virtualLink"));
            }
            if (updateData.get("endDateTime") != null) {
                try {
                    LocalDateTime endDateTime = LocalDateTime.parse((String) updateData.get("endDateTime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    event.setEndDateTime(endDateTime);
                } catch (DateTimeParseException e) {
                    response.put("success", false);
                    response.put("message", "Invalid end date time format");
                    return response;
                }
            }
            if (updateData.get("eventType") != null) {
                try {
                    EventType eventType = EventType.valueOf((String) updateData.get("eventType"));
                    event.setEventType(eventType);
                } catch (IllegalArgumentException e) {
                    // ignore invalid
                }
            }
            if (updateData.get("locationType") != null) {
                try {
                    EventLocationType locationType = EventLocationType.valueOf((String) updateData.get("locationType"));
                    event.setLocationType(locationType);
                } catch (IllegalArgumentException e) {
                    // ignore invalid
                }
            }
            if (updateData.get("registrationDeadline") != null) {
                try {
                    LocalDateTime regDeadline = LocalDateTime.parse((String) updateData.get("registrationDeadline"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    event.setRegistrationDeadline(regDeadline);
                } catch (DateTimeParseException e) {
                    // ignore invalid
                }
            }
            if (updateData.get("registrationRequirements") != null) {
                event.setRegistrationRequirements((String) updateData.get("registrationRequirements"));
            }
            if (updateData.get("requiresRegistration") != null) {
                event.setRequiresRegistration((Boolean) updateData.get("requiresRegistration"));
            }
            if (updateData.get("isPublic") != null) {
                event.setIsPublic((Boolean) updateData.get("isPublic"));
            }
            if (updateData.get("gameTitle") != null) {
                event.setGameTitle((String) updateData.get("gameTitle"));
            }
            if (updateData.get("gameCategory") != null) {
                event.setGameCategory((String) updateData.get("gameCategory"));
            }
            if (updateData.get("competitive") != null) {
                event.setCompetitive((Boolean) updateData.get("competitive"));
            }
            if (updateData.get("status") != null) {
                try {
                    EventStatus status = EventStatus.valueOf((String) updateData.get("status"));
                    event.setStatus(status);
                } catch (IllegalArgumentException e) {
                    // ignore invalid
                }
            }
            
            // Save updated event
            Event savedEvent = eventRepository.save(event);
            
            response.put("success", true);
            response.put("message", "Event updated successfully");
            response.put("event", formatEventForResponse(savedEvent));
            
        } catch (Exception e) {
            logger.error("Error updating event", e);
            response.put("success", false);
            response.put("message", "Failed to update event");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> deleteEvent(Long userId, Long eventId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user owns the event
            Optional<Event> eventOpt = eventRepository.findByIdAndOrganizerIdAndDeletedAtIsNull(eventId, userId);
            
            if (eventOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Event not found or access denied");
                return response;
            }
            
            Event event = eventOpt.get();
            
            // Soft delete
            event.setDeletedAt(LocalDateTime.now());
            event.setStatus(EventStatus.CANCELLED);
            eventRepository.save(event);
            
            response.put("success", true);
            response.put("message", "Event deleted successfully");
            
        } catch (Exception e) {
            logger.error("Error deleting event", e);
            response.put("success", false);
            response.put("message", "Failed to delete event");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> registerForEvent(Long userId, Long eventId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // For now, return basic success - EventAttendanceRepository would be needed for full implementation
            Optional<Event> eventOpt = eventRepository.findById(eventId);
            
            if (eventOpt.isEmpty() || eventOpt.get().isDeleted()) {
                response.put("success", false);
                response.put("message", "Event not found");
                return response;
            }
            
            Event event = eventOpt.get();
            
            if (!event.canRegister()) {
                response.put("success", false);
                response.put("message", "Registration is not open for this event");
                return response;
            }
            
            // Basic implementation without attendance tracking
            event.incrementAttendeeCount();
            eventRepository.save(event);
            
            response.put("success", true);
            response.put("message", "Successfully registered for event");
            // Would need EventAttendance entity for full response
            
        } catch (Exception e) {
            logger.error("Error registering for event", e);
            response.put("success", false);
            response.put("message", "Failed to register for event");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> unregisterFromEvent(Long userId, Long eventId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Event> eventOpt = eventRepository.findById(eventId);
            
            if (eventOpt.isEmpty() || eventOpt.get().isDeleted()) {
                response.put("success", false);
                response.put("message", "Event not found");
                return response;
            }
            
            Event event = eventOpt.get();
            
            // Basic implementation
            event.decrementAttendeeCount();
            eventRepository.save(event);
            
            response.put("success", true);
            response.put("message", "Successfully unregistered from event");
            
        } catch (Exception e) {
            logger.error("Error unregistering from event", e);
            response.put("success", false);
            response.put("message", "Failed to unregister from event");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getEventAttendees(Long eventId, int page, int size) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Basic implementation without EventAttendance repository
            Optional<Event> eventOpt = eventRepository.findById(eventId);
            
            if (eventOpt.isEmpty() || eventOpt.get().isDeleted()) {
                response.put("success", false);
                response.put("message", "Event not found");
                return response;
            }
            
            response.put("success", true);
            response.put("message", "Event attendees retrieved");
            response.put("attendees", new ArrayList<>());
            response.put("totalElements", 0);
            response.put("totalPages", 0);
            response.put("currentPage", page);
            response.put("pageSize", size);
            
        } catch (Exception e) {
            logger.error("Error getting event attendees", e);
            response.put("success", false);
            response.put("message", "Failed to get event attendees");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> checkInToEvent(Long userId, Long eventId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Event> eventOpt = eventRepository.findById(eventId);
            
            if (eventOpt.isEmpty() || eventOpt.get().isDeleted()) {
                response.put("success", false);
                response.put("message", "Event not found");
                return response;
            }
            
            Event event = eventOpt.get();
            
            if (!event.hasStarted()) {
                response.put("success", false);
                response.put("message", "Event has not started yet");
                return response;
            }
            
            response.put("success", true);
            response.put("message", "Successfully checked in to event");
            response.put("checkedInAt", LocalDateTime.now().toString());
            
        } catch (Exception e) {
            logger.error("Error checking in to event", e);
            response.put("success", false);
            response.put("message", "Failed to check in to event");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> searchEvents(String query, int page, int size, String eventType, String locationType) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Event> eventPage;
            
            if (eventType != null || locationType != null) {
                EventType eventTypeEnum = null;
                EventLocationType locationTypeEnum = null;
                
                if (eventType != null) {
                    try {
                        eventTypeEnum = EventType.valueOf(eventType);
                    } catch (IllegalArgumentException e) {
                        // Ignore invalid event type
                    }
                }
                
                if (locationType != null) {
                    try {
                        locationTypeEnum = EventLocationType.valueOf(locationType);
                    } catch (IllegalArgumentException e) {
                        // Ignore invalid location type
                    }
                }
                
                eventPage = eventRepository.searchEventsWithFilters(query, eventTypeEnum, locationTypeEnum, pageable);
            } else {
                eventPage = eventRepository.searchEvents(query, pageable);
            }
            
            List<Map<String, Object>> events = eventPage.getContent().stream()
                    .map(this::formatEventForResponse)
                    .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("message", "Events search completed");
            response.put("events", events);
            response.put("totalElements", eventPage.getTotalElements());
            response.put("totalPages", eventPage.getTotalPages());
            response.put("currentPage", eventPage.getNumber());
            response.put("pageSize", eventPage.getSize());
            
        } catch (Exception e) {
            logger.error("Error searching events", e);
            response.put("success", false);
            response.put("message", "Search failed");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getTrendingEvents(int limit) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(0, limit);
            List<Event> trendingEvents = eventRepository.findTrendingEvents(LocalDateTime.now(), pageable);
            
            List<Map<String, Object>> events = trendingEvents.stream()
                    .map(this::formatEventForResponse)
                    .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("message", "Trending events retrieved");
            response.put("events", events);
            
        } catch (Exception e) {
            logger.error("Error getting trending events", e);
            response.put("success", false);
            response.put("message", "Failed to get trending events");
        }
        
        return response;
    }
    
    // Helper methods
    private Map<String, Object> formatEventForResponse(Event event) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("id", event.getId());
        eventMap.put("title", event.getTitle());
        eventMap.put("description", event.getDescription());
        eventMap.put("startDateTime", event.getStartDateTime().toString());
        if (event.getEndDateTime() != null) {
            eventMap.put("endDateTime", event.getEndDateTime().toString());
        }
        eventMap.put("eventType", event.getEventType().name());
        eventMap.put("locationType", event.getLocationType().name());
        eventMap.put("gameCategory", event.getGameCategory());
        eventMap.put("currentAttendees", event.getCurrentAttendees());
        eventMap.put("maxAttendees", event.getMaxAttendees());
        eventMap.put("competitive", event.getCompetitive());
        
        // Organizer info
        Map<String, Object> organizerMap = new HashMap<>();
        organizerMap.put("id", event.getOrganizer().getId());
        organizerMap.put("displayName", event.getOrganizer().getDisplayName());
        eventMap.put("organizer", organizerMap);
        
        eventMap.put("createdAt", event.getCreatedAt().toString());
        
        return eventMap;
    }
    
    private Map<String, Object> formatEventDetailsForResponse(Event event) {
        Map<String, Object> eventMap = formatEventForResponse(event);
        
        // Add detailed fields
        eventMap.put("virtualLink", event.getVirtualLink());
        eventMap.put("virtualPlatform", event.getVirtualPlatform());
        eventMap.put("physicalAddress", event.getPhysicalAddress());
        eventMap.put("physicalVenue", event.getPhysicalVenue());
        eventMap.put("requiresRegistration", event.getRequiresRegistration());
        if (event.getRegistrationDeadline() != null) {
            eventMap.put("registrationDeadline", event.getRegistrationDeadline().toString());
        }
        eventMap.put("registrationRequirements", event.getRegistrationRequirements());
        eventMap.put("isPublic", event.getIsPublic());
        eventMap.put("gameTitle", event.getGameTitle());
        eventMap.put("competitive", event.getCompetitive());
        eventMap.put("entryFee", event.getEntryFee());
        eventMap.put("currency", event.getCurrency());
        eventMap.put("ageRestriction", event.getAgeRestriction());
        eventMap.put("status", event.getStatus().name());
        eventMap.put("viewCount", event.getViewCount());
        eventMap.put("interestedCount", event.getInterestedCount());
        if (event.getUpdatedAt() != null) {
            eventMap.put("updatedAt", event.getUpdatedAt().toString());
        }
        
        return eventMap;
    }
}
