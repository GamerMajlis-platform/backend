package com.gamermajilis.controller;

import com.gamermajilis.service.ChatService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@Tag(name = "Chat System", description = "Real-time chat and messaging endpoints")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/rooms")
    @Operation(summary = "Create chat room", description = "Create a new chat room")
    public ResponseEntity<Map<String, Object>> createChatRoom(
            HttpServletRequest request,
            @RequestParam @NotBlank @Size(min = 1, max = 100) String name,
            @RequestParam(required = false) @Size(max = 500) String description,
            @RequestParam(required = false, defaultValue = "GROUP") String type,
            @RequestParam(required = false, defaultValue = "false") Boolean isPrivate,
            @RequestParam(required = false) Integer maxMembers,
            @RequestParam(required = false) String gameTitle,
            @RequestParam(required = false) Long tournamentId,
            @RequestParam(required = false) Long eventId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> roomData = new HashMap<>();
            roomData.put("name", name);
            roomData.put("description", description);
            roomData.put("type", type);
            roomData.put("isPrivate", isPrivate);
            roomData.put("maxMembers", maxMembers);
            roomData.put("gameTitle", gameTitle);
            roomData.put("tournamentId", tournamentId);
            roomData.put("eventId", eventId);

            Map<String, Object> response = chatService.createChatRoom(userId, roomData);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error creating chat room", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create chat room"));
        }
    }

    @GetMapping("/rooms")
    @Operation(summary = "Get user chat rooms", description = "Get list of chat rooms user is member of")
    public ResponseEntity<Map<String, Object>> getUserChatRooms(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = chatService.getUserChatRooms(userId, page, size);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting user chat rooms", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get chat rooms"));
        }
    }

    @GetMapping("/rooms/{roomId}")
    @Operation(summary = "Get chat room details", description = "Get detailed information about a chat room")
    public ResponseEntity<Map<String, Object>> getChatRoomDetails(
            HttpServletRequest request,
            @PathVariable Long roomId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = chatService.getChatRoomDetails(userId, roomId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting chat room details", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Chat room not found or access denied"));
        }
    }

    @PostMapping("/rooms/{roomId}/join")
    @Operation(summary = "Join chat room", description = "Join a chat room")
    public ResponseEntity<Map<String, Object>> joinChatRoom(
            HttpServletRequest request,
            @PathVariable Long roomId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = chatService.joinChatRoom(userId, roomId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error joining chat room", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to join chat room"));
        }
    }

    @PostMapping("/rooms/{roomId}/leave")
    @Operation(summary = "Leave chat room", description = "Leave a chat room")
    public ResponseEntity<Map<String, Object>> leaveChatRoom(
            HttpServletRequest request,
            @PathVariable Long roomId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = chatService.leaveChatRoom(userId, roomId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error leaving chat room", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to leave chat room"));
        }
    }

    @PostMapping("/rooms/{roomId}/messages")
    @Operation(summary = "Send message", description = "Send a message to a chat room")
    public ResponseEntity<Map<String, Object>> sendMessage(
            HttpServletRequest request,
            @PathVariable Long roomId,
            @RequestParam @NotBlank @Size(max = 1000) String content,
            @RequestParam(required = false, defaultValue = "TEXT") String messageType,
            @RequestParam(required = false) Long replyToMessageId,
            @RequestParam(required = false) MultipartFile file) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            // Validate file if provided
            if (file != null && !file.isEmpty()) {
                if (!isValidChatFile(file)) {
                    return ResponseEntity.badRequest().body(createErrorResponse("Invalid file type"));
                }
                if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
                    return ResponseEntity.badRequest().body(createErrorResponse("File size must not exceed 10MB"));
                }
            }

            Map<String, Object> messageData = new HashMap<>();
            messageData.put("content", content);
            messageData.put("messageType", messageType);
            messageData.put("replyToMessageId", replyToMessageId);
            messageData.put("file", file);

            Map<String, Object> response = chatService.sendMessage(userId, roomId, messageData);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error sending message", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to send message"));
        }
    }

    @GetMapping("/rooms/{roomId}/messages")
    @Operation(summary = "Get chat messages", description = "Get messages from a chat room")
    public ResponseEntity<Map<String, Object>> getChatMessages(
            HttpServletRequest request,
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) Long beforeMessageId,
            @RequestParam(required = false) Long afterMessageId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> filters = new HashMap<>();
            if (beforeMessageId != null) filters.put("beforeMessageId", beforeMessageId);
            if (afterMessageId != null) filters.put("afterMessageId", afterMessageId);

            Map<String, Object> response = chatService.getChatMessages(userId, roomId, page, size, filters);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting chat messages", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get messages"));
        }
    }

    @DeleteMapping("/messages/{messageId}")
    @Operation(summary = "Delete message", description = "Delete a chat message")
    public ResponseEntity<Map<String, Object>> deleteMessage(
            HttpServletRequest request,
            @PathVariable Long messageId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = chatService.deleteMessage(userId, messageId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error deleting message", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to delete message"));
        }
    }

    @PostMapping("/rooms/{roomId}/members/{memberId}")
    @Operation(summary = "Add chat room member", description = "Add a member to a chat room")
    public ResponseEntity<Map<String, Object>> addChatRoomMember(
            HttpServletRequest request,
            @PathVariable Long roomId,
            @PathVariable Long memberId,
            @RequestParam(required = false, defaultValue = "MEMBER") String role) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = chatService.addChatRoomMember(userId, roomId, memberId, role);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error adding chat room member", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to add member"));
        }
    }

    @DeleteMapping("/rooms/{roomId}/members/{memberId}")
    @Operation(summary = "Remove chat room member", description = "Remove a member from a chat room")
    public ResponseEntity<Map<String, Object>> removeChatRoomMember(
            HttpServletRequest request,
            @PathVariable Long roomId,
            @PathVariable Long memberId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = chatService.removeChatRoomMember(userId, roomId, memberId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error removing chat room member", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to remove member"));
        }
    }

    @GetMapping("/rooms/{roomId}/members")
    @Operation(summary = "Get chat room members", description = "Get list of chat room members")
    public ResponseEntity<Map<String, Object>> getChatRoomMembers(
            HttpServletRequest request,
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = chatService.getChatRoomMembers(userId, roomId, page, size);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting chat room members", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get members"));
        }
    }

    @PostMapping("/direct")
    @Operation(summary = "Start direct message", description = "Start a direct message conversation")
    public ResponseEntity<Map<String, Object>> startDirectMessage(
            HttpServletRequest request,
            @RequestParam Long recipientId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            if (userId.equals(recipientId)) {
                return ResponseEntity.badRequest().body(createErrorResponse("Cannot start direct message with yourself"));
            }

            Map<String, Object> response = chatService.startDirectMessage(userId, recipientId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error starting direct message", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to start direct message"));
        }
    }

    @GetMapping("/online-users")
    @Operation(summary = "Get online users", description = "Get list of currently online users")
    public ResponseEntity<Map<String, Object>> getOnlineUsers(
            HttpServletRequest request) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = chatService.getOnlineUsers(userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting online users", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get online users"));
        }
    }

    @PostMapping("/typing")
    @Operation(summary = "Send typing indicator", description = "Send typing indicator to a chat room")
    public ResponseEntity<Map<String, Object>> sendTypingIndicator(
            HttpServletRequest request,
            @RequestParam Long roomId,
            @RequestParam(defaultValue = "true") boolean isTyping) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = chatService.sendTypingIndicator(userId, roomId, isTyping);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error sending typing indicator", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to send typing indicator"));
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

    private boolean isValidChatFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
            contentType.startsWith("image/") ||
            contentType.startsWith("video/") ||
            contentType.startsWith("audio/") ||
            contentType.equals("application/pdf") ||
            contentType.equals("text/plain")
        );
    }
}
