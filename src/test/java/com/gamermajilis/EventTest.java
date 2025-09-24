package com.gamermajilis;

import com.gamermajilis.controller.EventController;
import com.gamermajilis.service.CustomUserDetailsService;
import com.gamermajilis.service.EventService;
import com.gamermajilis.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestSecurityConfig.class)
@WebMvcTest(EventController.class)
public class EventTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        Mockito.when(jwtUtil.validateToken(any())).thenReturn(true); // Mock token validation
        Mockito.when(jwtUtil.getUserIdFromToken(any())).thenReturn(1L); // Mock user ID extraction
    }

    @Test
    void testCreateEvent() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Event created successfully");

        Mockito.when(eventService.createEvent(eq(1L), any())).thenReturn(mockResponse);

        mockMvc.perform(post("/events")
                .header("Authorization", "Bearer mockToken")
                .param("title", "Test Event")
                .param("description", "Test Description")
                .param("startDateTime", "2023-12-01T10:00:00")
                .param("eventType", "TOURNAMENT")
                .param("locationType", "VIRTUAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Event created successfully"));
    }

    @Test
    void testGetEventDetails() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("event", new HashMap<>());

        Mockito.when(eventService.getEventDetails(eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(get("/events/1")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.event").isEmpty());
    }

    @Test
    void testGetEventsList() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("events", new HashMap<>());

        Mockito.when(eventService.getEventsList(eq(0), eq(20), any())).thenReturn(mockResponse);

        mockMvc.perform(get("/events")
                .header("Authorization", "Bearer mockToken")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.events").isEmpty());
    }

    @Test
    void testUpdateEvent() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Event updated successfully");

        Mockito.when(eventService.updateEvent(eq(1L), eq(1L), any())).thenReturn(mockResponse);

        mockMvc.perform(put("/events/1")
                .header("Authorization", "Bearer mockToken")
                .param("title", "Updated Event Title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Event updated successfully"));
    }

    @Test
    void testDeleteEvent() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Event deleted successfully");

        Mockito.when(eventService.deleteEvent(eq(1L), eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(delete("/events/1")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Event deleted successfully"));
    }

    @Test
    void testRegisterForEvent() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Registered for event successfully");

        Mockito.when(eventService.registerForEvent(eq(1L), eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(post("/events/1/register")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registered for event successfully"));
    }

    @Test
    void testUnregisterFromEvent() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Unregistered from event successfully");

        Mockito.when(eventService.unregisterFromEvent(eq(1L), eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(post("/events/1/unregister")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Unregistered from event successfully"));
    }
}