package com.gamermajilis.controller;

import com.gamermajilis.service.EventService;
import com.gamermajilis.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/events")
@Tag(name = "Event Management", description = "Event creation and management endpoints")
@CrossOrigin(origins = "http://localhost:3000")
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventService eventService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "Create new event", description = "Create a new event")
    public ResponseEntity<Map<String, Object>> createEvent(
            HttpServletRequest request,
            @RequestParam @NotBlank @Size(min = 3, max = 200) String title,
            @RequestParam @NotBlank @Size(max = 5000) String description,
            @RequestParam @NotNull String startDateTime,
            @RequestParam(required = false) String endDateTime,
            @RequestParam(required = false, defaultValue = "COMMUNITY_GATHERING") String eventType,
            @RequestParam(required = false, defaultValue = "VIRTUAL") String locationType,
            @RequestParam(required = false) String virtualLink,
            @RequestParam(required = false) String virtualPlatform,
            @RequestParam(required = false) String physicalAddress,
            @RequestParam(required = false) String physicalVenue,
            @RequestParam(required = false) Integer maxAttendees,
            @RequestParam(required = false, defaultValue = "true") Boolean requiresRegistration,
            @RequestParam(required = false) String registrationDeadline,
            @RequestParam(required = false) String registrationRequirements,
            @RequestParam(required = false, defaultValue = "true") Boolean isPublic,
            @RequestParam(required = false) String gameTitle,
            @RequestParam(required = false) String gameCategory,
            @RequestParam(required = false, defaultValue = "false") Boolean competitive,
            @RequestParam(required = false) String entryFee,
            @RequestParam(required = false) Integer ageRestriction) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> eventData = new HashMap<>();
            eventData.put("title", title);
            eventData.put("description", description);
            eventData.put("startDateTime", startDateTime);
            eventData.put("endDateTime", endDateTime);
            eventData.put("eventType", eventType);
            eventData.put("locationType", locationType);
            eventData.put("virtualLink", virtualLink);
            eventData.put("virtualPlatform", virtualPlatform);
            eventData.put("physicalAddress", physicalAddress);
            eventData.put("physicalVenue", physicalVenue);
            eventData.put("maxAttendees", maxAttendees);
            eventData.put("requiresRegistration", requiresRegistration);
            eventData.put("registrationDeadline", registrationDeadline);
            eventData.put("registrationRequirements", registrationRequirements);
            eventData.put("isPublic", isPublic);
            eventData.put("gameTitle", gameTitle);
            eventData.put("gameCategory", gameCategory);
            eventData.put("competitive", competitive);
            eventData.put("entryFee", entryFee);
            eventData.put("ageRestriction", ageRestriction);

            Map<String, Object> response = eventService.createEvent(userId, eventData);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error creating event", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create event"));
        }
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Get event details", description = "Get detailed information about a specific event")
    public ResponseEntity<Map<String, Object>> getEvent(@PathVariable Long eventId) {
        try {
            Map<String, Object> response = eventService.getEventDetails(eventId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting event details for ID: " + eventId, e);
            return ResponseEntity.badRequest().body(createErrorResponse("Event not found"));
        }
    }

    @GetMapping
    @Operation(summary = "Get events list", description = "Get paginated list of events")
    public ResponseEntity<Map<String, Object>> getEventsList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String gameCategory,
            @RequestParam(required = false) String locationType,
            @RequestParam(required = false, defaultValue = "false") boolean myEvents,
            @RequestParam(required = false, defaultValue = "false") boolean upcoming) {
        
        try {
            Long userId = null;
            if (myEvents) {
                userId = getUserIdFromRequest(request);
                if (userId == null) {
                    return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
                }
            }

            Map<String, Object> filters = new HashMap<>();
            if (eventType != null) filters.put("eventType", eventType);
            if (gameCategory != null) filters.put("gameCategory", gameCategory);
            if (locationType != null) filters.put("locationType", locationType);
            if (userId != null) filters.put("organizerId", userId);
            filters.put("upcoming", upcoming);

            Map<String, Object> response = eventService.getEventsList(page, size, filters);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting events list", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get events list"));
        }
    }

    @PutMapping("/{eventId}")
    @Operation(summary = "Update event", description = "Update an existing event")
    public ResponseEntity<Map<String, Object>> updateEvent(
            HttpServletRequest request,
            @PathVariable Long eventId,
            @RequestParam(required = false) @Size(min = 3, max = 200) String title,
            @RequestParam(required = false) @Size(max = 5000) String description,
            @RequestParam(required = false) String startDateTime,
            @RequestParam(required = false) String endDateTime,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String locationType,
            @RequestParam(required = false) String virtualLink,
            @RequestParam(required = false) String virtualPlatform,
            @RequestParam(required = false) String physicalAddress,
            @RequestParam(required = false) String physicalVenue,
            @RequestParam(required = false) Integer maxAttendees,
            @RequestParam(required = false) Boolean requiresRegistration,
            @RequestParam(required = false) String registrationDeadline,
            @RequestParam(required = false) String registrationRequirements,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(required = false) String gameTitle,
            @RequestParam(required = false) String gameCategory,
            @RequestParam(required = false) Boolean competitive) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> updateData = new HashMap<>();
            if (title != null) updateData.put("title", title);
            if (description != null) updateData.put("description", description);
            if (startDateTime != null) updateData.put("startDateTime", startDateTime);
            if (endDateTime != null) updateData.put("endDateTime", endDateTime);
            if (eventType != null) updateData.put("eventType", eventType);
            if (locationType != null) updateData.put("locationType", locationType);
            if (virtualLink != null) updateData.put("virtualLink", virtualLink);
            if (virtualPlatform != null) updateData.put("virtualPlatform", virtualPlatform);
            if (physicalAddress != null) updateData.put("physicalAddress", physicalAddress);
            if (physicalVenue != null) updateData.put("physicalVenue", physicalVenue);
            if (maxAttendees != null) updateData.put("maxAttendees", maxAttendees);
            if (requiresRegistration != null) updateData.put("requiresRegistration", requiresRegistration);
            if (registrationDeadline != null) updateData.put("registrationDeadline", registrationDeadline);
            if (registrationRequirements != null) updateData.put("registrationRequirements", registrationRequirements);
            if (isPublic != null) updateData.put("isPublic", isPublic);
            if (gameTitle != null) updateData.put("gameTitle", gameTitle);
            if (gameCategory != null) updateData.put("gameCategory", gameCategory);
            if (competitive != null) updateData.put("competitive", competitive);

            Map<String, Object> response = eventService.updateEvent(userId, eventId, updateData);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error updating event", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to update event"));
        }
    }

    @DeleteMapping("/{eventId}")
    @Operation(summary = "Delete event", description = "Delete an event")
    public ResponseEntity<Map<String, Object>> deleteEvent(
            HttpServletRequest request,
            @PathVariable Long eventId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = eventService.deleteEvent(userId, eventId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error deleting event", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to delete event"));
        }
    }

    @PostMapping("/{eventId}/register")
    @Operation(summary = "Register for event", description = "Register to attend an event")
    public ResponseEntity<Map<String, Object>> registerForEvent(
            HttpServletRequest request,
            @PathVariable Long eventId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = eventService.registerForEvent(userId, eventId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error registering for event", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to register for event"));
        }
    }

    @PostMapping("/{eventId}/unregister")
    @Operation(summary = "Unregister from event", description = "Cancel event registration")
    public ResponseEntity<Map<String, Object>> unregisterFromEvent(
            HttpServletRequest request,
            @PathVariable Long eventId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = eventService.unregisterFromEvent(userId, eventId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error unregistering from event", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to unregister from event"));
        }
    }

    @GetMapping("/{eventId}/attendees")
    @Operation(summary = "Get event attendees", description = "Get list of event attendees")
    public ResponseEntity<Map<String, Object>> getEventAttendees(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Map<String, Object> response = eventService.getEventAttendees(eventId, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting event attendees", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get event attendees"));
        }
    }

    @PostMapping("/{eventId}/check-in")
    @Operation(summary = "Check-in to event", description = "Check-in as an attendee when event starts")
    public ResponseEntity<Map<String, Object>> checkInToEvent(
            HttpServletRequest request,
            @PathVariable Long eventId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = eventService.checkInToEvent(userId, eventId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error checking in to event", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to check-in to event"));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search events", description = "Search events by title, description, or game")
    public ResponseEntity<Map<String, Object>> searchEvents(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String locationType) {
        
        try {
            Map<String, Object> response = eventService.searchEvents(query, page, size, eventType, locationType);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error searching events", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Search failed"));
        }
    }

    @GetMapping("/trending")
    @Operation(summary = "Get trending events", description = "Get trending events based on registrations")
    public ResponseEntity<Map<String, Object>> getTrendingEvents(
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            Map<String, Object> response = eventService.getTrendingEvents(limit);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting trending events", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get trending events"));
        }
    }

    // Helper methods
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        try {
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return null;
            }
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            logger.warn("Invalid token in request", e);
            return null;
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}
