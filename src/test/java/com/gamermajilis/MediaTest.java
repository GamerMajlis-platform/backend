package com.gamermajilis;

import com.gamermajilis.controller.MediaController;
import com.gamermajilis.service.CustomUserDetailsService;
import com.gamermajilis.service.MediaService;
import com.gamermajilis.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(MediaController.class)
@Import(TestSecurityConfig.class)
public class MediaTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaService mediaService;

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
    void testUploadMedia() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Media uploaded successfully");

        Mockito.when(mediaService.uploadMedia(eq(1L), any(), any())).thenReturn(mockResponse);

        mockMvc.perform(multipart("/media/upload")
                .file(file)
                .param("title", "Test Media")
                .param("description", "Test Description")
                .param("tags", "tag1,tag2")
                .param("gameCategory", "Test Game")
                .param("visibility", "PUBLIC")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Media uploaded successfully"));
    }

    @Test
    void testGetMediaDetails() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("media", new HashMap<>());

        Mockito.when(mediaService.getMediaDetails(eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(get("/media/1")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.media").isEmpty());
    }

    @Test
    void testGetMediaList() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("mediaList", new HashMap<>());

        Mockito.when(mediaService.getMediaList(eq(0), eq(20), any())).thenReturn(mockResponse);

        mockMvc.perform(get("/media")
                .param("page", "0")
                .param("size", "20")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.mediaList").isEmpty());
    }

    @Test
    void testUpdateMedia() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Media updated successfully");

        Mockito.when(mediaService.updateMedia(eq(1L), eq(1L), any())).thenReturn(mockResponse);

        mockMvc.perform(put("/media/1")
                .param("title", "Updated Title")
                .param("description", "Updated Description")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Media updated successfully"));
    }

    @Test
    void testDeleteMedia() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Media deleted successfully");

        Mockito.when(mediaService.deleteMedia(eq(1L), eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(delete("/media/1")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Media deleted successfully"));
    }

    @Test
    void testIncrementViewCount() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "View count incremented");

        Mockito.when(mediaService.incrementViewCount(eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(post("/media/1/view"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("View count incremented"));
    }
}