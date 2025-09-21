package com.gamermajilis.service;

import java.util.Map;

public interface EventService {
    
    Map<String, Object> createEvent(Long userId, Map<String, Object> eventData);
    
    Map<String, Object> getEventDetails(Long eventId);
    
    Map<String, Object> getEventsList(int page, int size, Map<String, Object> filters);
    
    Map<String, Object> updateEvent(Long userId, Long eventId, Map<String, Object> updateData);
    
    Map<String, Object> deleteEvent(Long userId, Long eventId);
    
    Map<String, Object> registerForEvent(Long userId, Long eventId);
    
    Map<String, Object> unregisterFromEvent(Long userId, Long eventId);
    
    Map<String, Object> getEventAttendees(Long eventId, int page, int size);
    
    Map<String, Object> checkInToEvent(Long userId, Long eventId);
    
    Map<String, Object> searchEvents(String query, int page, int size, String eventType, String locationType);
    
    Map<String, Object> getTrendingEvents(int limit);
}
