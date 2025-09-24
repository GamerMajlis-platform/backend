package com.gamermajilis;

import com.gamermajilis.controller.ChatController;
import com.gamermajilis.service.ChatService;
import com.gamermajilis.service.CustomUserDetailsService;
import com.gamermajilis.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
@Import(TestSecurityConfig.class)
public class ChatTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        Mockito.when(jwtUtil.validateToken(any())).thenReturn(true);
        Mockito.when(jwtUtil.getUserIdFromToken(any())).thenReturn(1L);
    }

    @Test
    void testCreateChatRoom() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Chat room created successfully");
        Mockito.when(chatService.createChatRoom(eq(1L), any())).thenReturn(mockResponse);

        mockMvc.perform(post("/chat/rooms")
                .header("Authorization", "Bearer mockToken")
                .param("name", "Test Room")
                .param("description", "Test Description")
                .param("type", "GROUP")
                .param("isPrivate", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Chat room created successfully"));
    }

    @Test
    void testGetUserChatRooms() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("rooms", new HashMap<>());
        Mockito.when(chatService.getUserChatRooms(eq(1L), eq(0), eq(20))).thenReturn(mockResponse);

        mockMvc.perform(get("/chat/rooms")
                .header("Authorization", "Bearer mockToken")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.rooms").isEmpty());
    }

    @Test
    void testSendMessage() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Message sent successfully");
        Mockito.when(chatService.sendMessage(eq(1L), eq(1L), any())).thenReturn(mockResponse);

        mockMvc.perform(post("/chat/rooms/1/messages")
                .header("Authorization", "Bearer mockToken")
                .param("content", "Hello World")
                .param("messageType", "TEXT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Message sent successfully"));
    }

    @Test
    void testJoinChatRoom() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Joined chat room successfully");
        Mockito.when(chatService.joinChatRoom(eq(1L), eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(post("/chat/rooms/1/join")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Joined chat room successfully"));
    }

    @Test
    void testLeaveChatRoom() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Left chat room successfully");
        Mockito.when(chatService.leaveChatRoom(eq(1L), eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(post("/chat/rooms/1/leave")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Left chat room successfully"));
    }
}