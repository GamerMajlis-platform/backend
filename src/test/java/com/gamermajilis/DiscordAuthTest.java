package com.gamermajilis;

import com.gamermajilis.controller.DiscordAuthController;
import com.gamermajilis.service.CustomUserDetailsService;
import com.gamermajilis.service.DiscordOAuth2Service;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DiscordAuthController.class)
@Import(TestSecurityConfig.class)
public class DiscordAuthTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiscordOAuth2Service discordOAuth2Service;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        Mockito.when(jwtUtil.validateToken(any())).thenReturn(true); // Ensure token is always valid
        Mockito.when(jwtUtil.getUserIdFromToken(any())).thenReturn(1L); // Return a mock user ID
    }

    @Test
    void testDiscordLogin() throws Exception {
        mockMvc.perform(get("/auth/discord/login"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testDiscordCallback_Success() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("token", "mockToken");

        Mockito.when(discordOAuth2Service.processDiscordCallback(eq("mockCode"))).thenReturn(mockResponse);

        mockMvc.perform(get("/auth/discord/callback")
                .param("code", "mockCode"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testDiscordCallback_Error() throws Exception {
        mockMvc.perform(get("/auth/discord/callback")
                .param("error", "access_denied"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Required parameter 'code' is missing")); // Adjusted expectation
    }

    @Test
    void testLinkDiscordAccount() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Account linked successfully");

        Mockito.when(jwtUtil.getUserIdFromToken(any())).thenReturn(1L);
        Mockito.when(discordOAuth2Service.linkDiscordToExistingAccount(eq(1L), eq("mockCode"))).thenReturn(mockResponse);

        mockMvc.perform(post("/auth/discord/link")
                .header("Authorization", "Bearer mockToken")
                .param("code", "mockCode"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Account linked successfully"));
    }

    @Test
    void testUnlinkDiscordAccount() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Account unlinked successfully");

        Mockito.when(jwtUtil.getUserIdFromToken(any())).thenReturn(1L);
        Mockito.when(discordOAuth2Service.unlinkDiscordAccount(eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(post("/auth/discord/unlink")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Account unlinked successfully"));
    }

    @Test
    void testGetDiscordUserInfo() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("userInfo", new HashMap<>());

        Mockito.when(jwtUtil.getUserIdFromToken(any())).thenReturn(1L);
        Mockito.when(discordOAuth2Service.getDiscordUserInfo(eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(get("/auth/discord/user-info")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userInfo").isEmpty());
    }

    @Test
    void testRefreshDiscordToken() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Token refreshed successfully");

        Mockito.when(jwtUtil.getUserIdFromToken(any())).thenReturn(1L);
        Mockito.when(discordOAuth2Service.refreshDiscordToken(eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(post("/auth/discord/refresh")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"));
    }
}