package com.gamermajilis.controller;

import com.gamermajilis.service.PostService;
import com.gamermajilis.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@Tag(name = "Post Management", description = "Social media post management endpoints")
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService postService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "Create new post", description = "Create a new social media post")
    public ResponseEntity<Map<String, Object>> createPost(
            HttpServletRequest request,
            @RequestParam @NotBlank @Size(min = 1, max = 500) String title,
            @RequestParam @NotBlank @Size(min = 1, max = 10000) String content,
            @RequestParam(required = false, defaultValue = "TEXT") String type,
            @RequestParam(required = false) String gameTitle,
            @RequestParam(required = false) String gameCategory,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String hashtags,
            @RequestParam(required = false) List<Long> mediaIds,
            @RequestParam(required = false, defaultValue = "PUBLIC") String visibility) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> postData = new HashMap<>();
            postData.put("title", title);
            postData.put("content", content);
            postData.put("type", type);
            postData.put("gameTitle", gameTitle);
            postData.put("gameCategory", gameCategory);
            postData.put("platform", platform);
            postData.put("tags", tags);
            postData.put("hashtags", hashtags);
            postData.put("mediaIds", mediaIds);
            postData.put("visibility", visibility);

            Map<String, Object> response = postService.createPost(userId, postData);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error creating post", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create post"));
        }
    }

    @GetMapping("/{postId}")
    @Operation(summary = "Get post details", description = "Get detailed information about a specific post")
    public ResponseEntity<Map<String, Object>> getPost(@PathVariable Long postId) {
        try {
            Map<String, Object> response = postService.getPostDetails(postId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting post details for ID: " + postId, e);
            return ResponseEntity.badRequest().body(createErrorResponse("Post not found"));
        }
    }

    @GetMapping
    @Operation(summary = "Get posts feed", description = "Get paginated list of posts for the feed")
    public ResponseEntity<Map<String, Object>> getPostsFeed(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String gameCategory,
            @RequestParam(required = false) String type,
            @RequestParam(required = false, defaultValue = "false") boolean myPosts) {
        
        try {
            Long userId = null;
            if (myPosts) {
                userId = getUserIdFromRequest(request);
                if (userId == null) {
                    return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
                }
            }

            Map<String, Object> filters = new HashMap<>();
            if (gameCategory != null) filters.put("gameCategory", gameCategory);
            if (type != null) filters.put("type", type);
            if (userId != null) filters.put("authorId", userId);

            Map<String, Object> response = postService.getPostsFeed(page, size, filters);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting posts feed", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get posts feed"));
        }
    }

    @PutMapping("/{postId}")
    @Operation(summary = "Update post", description = "Update an existing post")
    public ResponseEntity<Map<String, Object>> updatePost(
            HttpServletRequest request,
            @PathVariable Long postId,
            @RequestParam(required = false) @Size(min = 1, max = 500) String title,
            @RequestParam(required = false) @Size(min = 1, max = 10000) String content,
            @RequestParam(required = false) String gameTitle,
            @RequestParam(required = false) String gameCategory,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String hashtags,
            @RequestParam(required = false) String visibility) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> updateData = new HashMap<>();
            if (title != null) updateData.put("title", title);
            if (content != null) updateData.put("content", content);
            if (gameTitle != null) updateData.put("gameTitle", gameTitle);
            if (gameCategory != null) updateData.put("gameCategory", gameCategory);
            if (platform != null) updateData.put("platform", platform);
            if (tags != null) updateData.put("tags", tags);
            if (hashtags != null) updateData.put("hashtags", hashtags);
            if (visibility != null) updateData.put("visibility", visibility);

            Map<String, Object> response = postService.updatePost(userId, postId, updateData);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error updating post", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to update post"));
        }
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete post", description = "Delete a post")
    public ResponseEntity<Map<String, Object>> deletePost(
            HttpServletRequest request,
            @PathVariable Long postId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = postService.deletePost(userId, postId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error deleting post", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to delete post"));
        }
    }

    @PostMapping("/{postId}/like")
    @Operation(summary = "Like post", description = "Like or unlike a post")
    public ResponseEntity<Map<String, Object>> toggleLike(
            HttpServletRequest request,
            @PathVariable Long postId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = postService.toggleLike(userId, postId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error toggling like on post", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to toggle like"));
        }
    }

    @PostMapping("/{postId}/comments")
    @Operation(summary = "Add comment", description = "Add a comment to a post")
    public ResponseEntity<Map<String, Object>> addComment(
            HttpServletRequest request,
            @PathVariable Long postId,
            @RequestParam @NotBlank @Size(min = 1, max = 1000) String content) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = postService.addComment(userId, postId, content);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error adding comment to post", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to add comment"));
        }
    }

    @GetMapping("/{postId}/comments")
    @Operation(summary = "Get post comments", description = "Get comments for a specific post")
    public ResponseEntity<Map<String, Object>> getPostComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Map<String, Object> response = postService.getPostComments(postId, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting post comments", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get comments"));
        }
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "Delete comment", description = "Delete a comment")
    public ResponseEntity<Map<String, Object>> deleteComment(
            HttpServletRequest request,
            @PathVariable Long commentId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = postService.deleteComment(userId, commentId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error deleting comment", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to delete comment"));
        }
    }

    @PostMapping("/{postId}/share")
    @Operation(summary = "Share post", description = "Share a post")
    public ResponseEntity<Map<String, Object>> sharePost(
            HttpServletRequest request,
            @PathVariable Long postId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = postService.sharePost(userId, postId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error sharing post", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to share post"));
        }
    }

    @GetMapping("/trending")
    @Operation(summary = "Get trending posts", description = "Get trending posts based on engagement")
    public ResponseEntity<Map<String, Object>> getTrendingPosts(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "7") int days) {
        
        try {
            Map<String, Object> response = postService.getTrendingPosts(limit, days);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting trending posts", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get trending posts"));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search posts", description = "Search posts by title, content, or tags")
    public ResponseEntity<Map<String, Object>> searchPosts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String gameCategory) {
        
        try {
            Map<String, Object> response = postService.searchPosts(query, page, size, gameCategory);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error searching posts", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Search failed"));
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
