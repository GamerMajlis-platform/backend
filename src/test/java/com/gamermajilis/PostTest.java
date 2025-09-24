package com.gamermajilis;

import com.gamermajilis.controller.PostController;
import com.gamermajilis.service.PostService;
import com.gamermajilis.service.CustomUserDetailsService;
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

@Import(TestSecurityConfig.class)
@WebMvcTest(PostController.class)
public class PostTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        Mockito.when(jwtUtil.validateToken(any())).thenReturn(true); // Mock token validation
        Mockito.when(jwtUtil.getUserIdFromToken(any())).thenReturn(1L); // Mock user ID extraction
    }

    void testCreatePost() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Post created successfully");

        Mockito.when(postService.createPost(eq(1L), any())).thenReturn(mockResponse);

        mockMvc.perform(post("/posts")
                .header("Authorization", "Bearer mockToken")
                .param("title", "Test Post")
                .param("content", "This is a test post content")
                .param("type", "TEXT")
                .param("visibility", "PUBLIC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Post created successfully"));
    }

    @Test
    void testGetPostDetails() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("post", new HashMap<>());

        Mockito.when(postService.getPostDetails(eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(get("/posts/1")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.post").isEmpty());
    }

    @Test
    void testGetPostsFeed() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("posts", new HashMap<>());

        Mockito.when(postService.getPostsFeed(eq(0), eq(20), any())).thenReturn(mockResponse);

        mockMvc.perform(get("/posts")
                .param("page", "0")
                .param("size", "20")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.posts").isEmpty());
    }

    @Test
    void testUpdatePost() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Post updated successfully");

        Mockito.when(postService.updatePost(eq(1L), eq(1L), any())).thenReturn(mockResponse);

        mockMvc.perform(put("/posts/1")
                .header("Authorization", "Bearer mockToken")
                .param("title", "Updated Title")
                .param("content", "Updated Content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Post updated successfully"));
    }

    @Test
    void testDeletePost() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Post deleted successfully");

        Mockito.when(postService.deletePost(eq(1L), eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(delete("/posts/1")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Post deleted successfully"));
    }

    @Test
    void testToggleLike() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Post liked successfully");

        Mockito.when(postService.toggleLike(eq(1L), eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(post("/posts/1/like")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Post liked successfully"));
    }

    @Test
    void testAddComment() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Comment added successfully");

        Mockito.when(postService.addComment(eq(1L), eq(1L), eq("Test Comment"))).thenReturn(mockResponse);

        mockMvc.perform(post("/posts/1/comments")
                .header("Authorization", "Bearer mockToken")
                .param("content", "Test Comment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Comment added successfully"));
    }

    @Test
    void testDeleteComment() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("success", true);
        mockResponse.put("message", "Comment deleted successfully");

        Mockito.when(postService.deleteComment(eq(1L), eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(delete("/posts/comments/1")
                .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Comment deleted successfully"));
    }
}