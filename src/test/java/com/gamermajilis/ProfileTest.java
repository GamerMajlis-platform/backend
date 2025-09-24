package com.gamermajilis;

import com.gamermajilis.controller.ProfileController;
import com.gamermajilis.service.CustomUserDetailsService;
import com.gamermajilis.service.ProfileService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@Import(TestSecurityConfig.class)
public class ProfileTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

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
    void testGetMyProfile() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("user", new HashMap<>());

        Mockito.when(profileService.getUserProfile(eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(get("/profile/me")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.user").isEmpty());
    }

    @Test
    void testGetUserProfile() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("user", new HashMap<>());

        Mockito.when(profileService.getPublicUserProfile(eq(2L))).thenReturn(mockResponse);

        mockMvc.perform(get("/profile/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.user").isEmpty());
    }

    @Test
    void testUpdateProfile() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Profile updated successfully");

        Mockito.when(profileService.updateUserProfile(eq(1L), any())).thenReturn(mockResponse);

        mockMvc.perform(put("/profile/me")
                .header("Authorization", "Bearer mockToken")
                .param("displayName", "Updated Name")
                .param("bio", "Updated Bio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Profile updated successfully"));
    }

    @Test
    void testUploadProfilePicture() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("profilePictureUrl", "/uploads/profile-pictures/test.jpg");

        Mockito.when(profileService.uploadProfilePicture(eq(1L), any())).thenReturn(mockResponse);

        mockMvc.perform(multipart("/profile/me/profile-picture")
                .file(file)
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.profilePictureUrl").value("/uploads/profile-pictures/test.jpg"));
    }

    @Test
    void testRemoveProfilePicture() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Profile picture removed successfully");

        Mockito.when(profileService.removeProfilePicture(eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(delete("/profile/me/profile-picture")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Profile picture removed successfully"));
    }

    @Test
    void testSearchProfiles() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("profiles", new HashMap<>());

        Mockito.when(profileService.searchProfiles(eq("test"), eq(0), eq(20))).thenReturn(mockResponse);

        mockMvc.perform(get("/profile/search")
                .param("query", "test")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.profiles").isEmpty());
    }

    @Test
    void testGetProfileSuggestions() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("suggestions", new HashMap<>());

        Mockito.when(profileService.getProfileSuggestions(eq(1L), eq(10))).thenReturn(mockResponse);

        mockMvc.perform(get("/profile/suggestions")
                .header("Authorization", "Bearer mockToken")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.suggestions").isEmpty());
    }
}