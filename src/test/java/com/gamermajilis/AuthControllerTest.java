package com.gamermajilis;

import com.gamermajilis.controller.AuthController;
import com.gamermajilis.service.AuthService;
import com.gamermajilis.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignup() {
        String email = "test@example.com";
        String password = "password123";
        String displayName = "TestUser";

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "User registered successfully");

        when(authService.signup(email, password, displayName)).thenReturn(mockResponse);

        ResponseEntity<Map<String, Object>> response = authController.signup(email, password, displayName);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testLogin() {
        String identifier = "test@example.com";
        String password = "password123";

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Login successful");

        when(authService.login(identifier, password)).thenReturn(mockResponse);

        ResponseEntity<Map<String, Object>> response = authController.login(identifier, password);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testLogout() {
        String token = "Bearer testToken";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.getUsernameFromToken("testToken")).thenReturn("TestUser");

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Logged out successfully");

        when(authService.logout()).thenReturn(mockResponse);

        ResponseEntity<Map<String, Object>> response = authController.logout(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testVerifyEmail() {
        String token = "verificationToken";

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Email verified successfully");

        when(authService.verifyEmail(token)).thenReturn(mockResponse);

        ResponseEntity<Map<String, Object>> response = authController.verifyEmail(token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testGetCurrentUser() {
        String token = "Bearer testToken";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.validateToken("testToken")).thenReturn(true);
        when(jwtUtil.getUserIdFromToken("testToken")).thenReturn(1L);

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("user", new HashMap<>());

        when(authService.getCurrentUser(1L)).thenReturn(mockResponse);

        ResponseEntity<Map<String, Object>> response = authController.getCurrentUser(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
    }
}