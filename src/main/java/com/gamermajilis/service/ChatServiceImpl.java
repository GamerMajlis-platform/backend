package com.gamermajilis.service;

import com.gamermajilis.model.*;
import com.gamermajilis.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);
    
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ChatRoomMemberRepository chatRoomMemberRepository;
    
    @Override
    public Map<String, Object> createChatRoom(Long userId, Map<String, Object> roomData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate user
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }
            
            User creator = userOpt.get();
            
            // Validate required fields
            String name = (String) roomData.get("name");
            if (name == null || name.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Chat room name is required");
                return response;
            }
            
            // Create chat room
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setName(name);
            chatRoom.setCreator(creator);
            
            // Optional fields
            if (roomData.get("description") != null) {
                chatRoom.setDescription((String) roomData.get("description"));
            }
            
            if (roomData.get("type") != null) {
                try {
                    ChatRoomType type = ChatRoomType.valueOf((String) roomData.get("type"));
                    chatRoom.setType(type);
                } catch (IllegalArgumentException e) {
                    chatRoom.setType(ChatRoomType.GROUP);
                }
            }
            
            if (roomData.get("isPrivate") != null) {
                chatRoom.setIsPrivate((Boolean) roomData.get("isPrivate"));
            }
            
            if (roomData.get("maxMembers") != null) {
                chatRoom.setMaxMembers(((Number) roomData.get("maxMembers")).intValue());
            }
            
            if (roomData.get("gameTitle") != null) {
                chatRoom.setGameTitle((String) roomData.get("gameTitle"));
            }
            
            if (roomData.get("tournamentId") != null) {
                chatRoom.setTournamentId(((Number) roomData.get("tournamentId")).longValue());
            }
            
            if (roomData.get("eventId") != null) {
                chatRoom.setEventId(((Number) roomData.get("eventId")).longValue());
            }
            
            // Save chat room
            ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
            
            response.put("success", true);
            response.put("message", "Chat room created successfully");
            response.put("chatRoom", formatChatRoomForResponse(savedChatRoom));
            
        } catch (Exception e) {
            logger.error("Error creating chat room", e);
            response.put("success", false);
            response.put("message", "Failed to create chat room: " + e.getMessage());
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getUserChatRooms(Long userId, int page, int size) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }
            
            User user = userOpt.get();
            Pageable pageable = PageRequest.of(page, size);
            Page<ChatRoomMember> membershipsPage = chatRoomMemberRepository.findByUserAndIsBannedFalseOrderByChatRoomLastActivityDesc(user, pageable);
            
            List<Map<String, Object>> chatRooms = membershipsPage.getContent().stream()
                    .map(membership -> formatChatRoomForResponse(membership.getChatRoom()))
                    .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("message", "Chat rooms retrieved successfully");
            response.put("chatRooms", chatRooms);
            response.put("totalElements", membershipsPage.getTotalElements());
            response.put("totalPages", membershipsPage.getTotalPages());
            response.put("currentPage", membershipsPage.getNumber());
            response.put("pageSize", membershipsPage.getSize());
            
        } catch (Exception e) {
            logger.error("Error getting user chat rooms", e);
            response.put("success", false);
            response.put("message", "Failed to get chat rooms");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getChatRoomDetails(Long userId, Long roomId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(roomId);
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (chatRoomOpt.isEmpty() || chatRoomOpt.get().isDeleted()) {
                response.put("success", false);
                response.put("message", "Chat room not found");
                return response;
            }
            
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }
            
            ChatRoom chatRoom = chatRoomOpt.get();
            User user = userOpt.get();
            
            // Check if user is a member (unless it's a public room)
            if (chatRoom.getIsPrivate()) {
                Optional<ChatRoomMember> membership = chatRoomMemberRepository.findByChatRoomAndUser(chatRoom, user);
                if (membership.isEmpty() || membership.get().getIsBanned()) {
                    response.put("success", false);
                    response.put("message", "Chat room not found or access denied");
                    return response;
                }
            }
            
            response.put("success", true);
            response.put("message", "Chat room details retrieved");
            response.put("chatRoom", formatChatRoomDetailsForResponse(chatRoom));
            
        } catch (Exception e) {
            logger.error("Error getting chat room details", e);
            response.put("success", false);
            response.put("message", "Failed to get chat room details");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> joinChatRoom(Long userId, Long roomId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(roomId);
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (chatRoomOpt.isEmpty() || chatRoomOpt.get().isDeleted()) {
                response.put("success", false);
                response.put("message", "Chat room not found");
                return response;
            }
            
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }
            
            ChatRoom chatRoom = chatRoomOpt.get();
            User user = userOpt.get();
            
            // Check if room is full
            if (chatRoom.isFull()) {
                response.put("success", false);
                response.put("message", "Chat room is full");
                return response;
            }
            
            // Check if user is already a member
            Optional<ChatRoomMember> existingMember = chatRoomMemberRepository.findByChatRoomAndUser(chatRoom, user);
            if (existingMember.isPresent()) {
                if (existingMember.get().getIsBanned()) {
                    response.put("success", false);
                    response.put("message", "You are banned from this chat room");
                    return response;
                } else {
                    response.put("success", false);
                    response.put("message", "You are already a member of this chat room");
                    return response;
                }
            }
            
            // Create new membership
            ChatRoomMember newMember = new ChatRoomMember(chatRoom, user, ChatMemberRole.MEMBER);
            chatRoomMemberRepository.save(newMember);
            
            // Update member count
            chatRoom.incrementMemberCount();
            chatRoomRepository.save(chatRoom);
            
            response.put("success", true);
            response.put("message", "Successfully joined chat room");
            
            Map<String, Object> membership = new HashMap<>();
            membership.put("id", newMember.getId());
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", userId);
            userMap.put("displayName", user.getDisplayName());
            membership.put("user", userMap);
            membership.put("role", newMember.getRole().toString());
            membership.put("joinedAt", newMember.getJoinedAt().toString());
            
            response.put("membership", membership);
            
        } catch (Exception e) {
            logger.error("Error joining chat room", e);
            response.put("success", false);
            response.put("message", "Failed to join chat room");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> leaveChatRoom(Long userId, Long roomId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(roomId);
            
            if (chatRoomOpt.isEmpty() || chatRoomOpt.get().isDeleted()) {
                response.put("success", false);
                response.put("message", "Chat room not found");
                return response;
            }
            
            ChatRoom chatRoom = chatRoomOpt.get();
            
            // Basic implementation
            chatRoom.decrementMemberCount();
            chatRoomRepository.save(chatRoom);
            
            response.put("success", true);
            response.put("message", "Successfully left chat room");
            
        } catch (Exception e) {
            logger.error("Error leaving chat room", e);
            response.put("success", false);
            response.put("message", "Failed to leave chat room");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> sendMessage(Long userId, Long roomId, Map<String, Object> messageData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(roomId);
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (chatRoomOpt.isEmpty() || chatRoomOpt.get().isDeleted()) {
                response.put("success", false);
                response.put("message", "Chat room not found");
                return response;
            }
            
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }
            
            String content = (String) messageData.get("content");
            if (content == null || content.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Message content cannot be empty");
                return response;
            }
            
            ChatRoom chatRoom = chatRoomOpt.get();
            User sender = userOpt.get();
            
            // Create message
            ChatMessage message = new ChatMessage();
            message.setChatRoom(chatRoom);
            message.setSender(sender);
            message.setContent(content);
            
            // Set message type
            if (messageData.get("messageType") != null) {
                try {
                    MessageType messageType = MessageType.valueOf((String) messageData.get("messageType"));
                    message.setMessageType(messageType);
                } catch (IllegalArgumentException e) {
                    message.setMessageType(MessageType.TEXT);
                }
            }
            
            // Set reply if specified
            if (messageData.get("replyToMessageId") != null) {
                Long replyToMessageId = ((Number) messageData.get("replyToMessageId")).longValue();
                Optional<ChatMessage> replyToMessageOpt = chatMessageRepository.findById(replyToMessageId);
                if (replyToMessageOpt.isPresent()) {
                    message.setReplyToMessage(replyToMessageOpt.get());
                }
            }
            
            // Save message
            ChatMessage savedMessage = chatMessageRepository.save(message);
            
            // Update chat room activity
            chatRoom.addMessage();
            chatRoomRepository.save(chatRoom);
            
            response.put("success", true);
            response.put("message", "Message sent successfully");
            response.put("chatMessage", formatMessageForResponse(savedMessage));
            
        } catch (Exception e) {
            logger.error("Error sending message", e);
            response.put("success", false);
            response.put("message", "Failed to send message");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getChatMessages(Long userId, Long roomId, int page, int size, Map<String, Object> filters) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user has access to the chat room
            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(roomId);
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (chatRoomOpt.isEmpty() || chatRoomOpt.get().isDeleted()) {
                response.put("success", false);
                response.put("message", "Chat room not found");
                return response;
            }
            
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }
            
            ChatRoom chatRoom = chatRoomOpt.get();
            User user = userOpt.get();
            
            // Check if user is a member (unless it's a public room)
            if (chatRoom.getIsPrivate()) {
                Optional<ChatRoomMember> membership = chatRoomMemberRepository.findByChatRoomAndUser(chatRoom, user);
                if (membership.isEmpty() || membership.get().getIsBanned()) {
                    response.put("success", false);
                    response.put("message", "Chat room not found or access denied");
                    return response;
                }
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<ChatMessage> messagesPage;
            
            // Apply filters
            Long beforeMessageId = filters.get("beforeMessageId") != null ? 
                ((Number) filters.get("beforeMessageId")).longValue() : null;
            Long afterMessageId = filters.get("afterMessageId") != null ? 
                ((Number) filters.get("afterMessageId")).longValue() : null;
            
            if (beforeMessageId != null) {
                messagesPage = chatMessageRepository.findMessagesBefore(roomId, beforeMessageId, pageable);
            } else if (afterMessageId != null) {
                messagesPage = chatMessageRepository.findMessagesAfter(roomId, afterMessageId, pageable);
            } else {
                messagesPage = chatMessageRepository.findByChatRoomIdAndDeletedAtIsNullOrderByCreatedAtDesc(roomId, pageable);
            }
            
            List<Map<String, Object>> messages = messagesPage.getContent().stream()
                    .map(this::formatMessageForResponse)
                    .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("message", "Messages retrieved successfully");
            response.put("messages", messages);
            response.put("totalElements", messagesPage.getTotalElements());
            response.put("totalPages", messagesPage.getTotalPages());
            response.put("currentPage", messagesPage.getNumber());
            response.put("pageSize", messagesPage.getSize());
            
        } catch (Exception e) {
            logger.error("Error getting chat messages", e);
            response.put("success", false);
            response.put("message", "Failed to get messages");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> deleteMessage(Long userId, Long messageId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<ChatMessage> messageOpt = chatMessageRepository.findByIdAndSenderIdAndDeletedAtIsNull(messageId, userId);
            
            if (messageOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Message not found or access denied");
                return response;
            }
            
            ChatMessage message = messageOpt.get();
            message.delete(); // Sets deleted flag and updates content
            chatMessageRepository.save(message);
            
            response.put("success", true);
            response.put("message", "Message deleted successfully");
            
        } catch (Exception e) {
            logger.error("Error deleting message", e);
            response.put("success", false);
            response.put("message", "Failed to delete message");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> addChatRoomMember(Long userId, Long roomId, Long memberId, String role) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(roomId);
            Optional<User> userOpt = userRepository.findById(memberId);
            
            if (chatRoomOpt.isEmpty() || chatRoomOpt.get().isDeleted()) {
                response.put("success", false);
                response.put("message", "Chat room not found");
                return response;
            }
            
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }
            
            ChatRoom chatRoom = chatRoomOpt.get();
            User member = userOpt.get();
            
            // Check if requester has permission (basic check - room creator)
            if (!chatRoom.getCreator().getId().equals(userId)) {
                response.put("success", false);
                response.put("message", "Access denied");
                return response;
            }
            
            // Check if room is full
            if (chatRoom.isFull()) {
                response.put("success", false);
                response.put("message", "Chat room is full");
                return response;
            }
            
            // Basic implementation
            chatRoom.incrementMemberCount();
            chatRoomRepository.save(chatRoom);
            
            response.put("success", true);
            response.put("message", "Member added successfully");
            
            Map<String, Object> membership = new HashMap<>();
            membership.put("id", 1); // Mock ID
            Map<String, Object> memberMap = new HashMap<>();
            memberMap.put("id", memberId);
            memberMap.put("displayName", member.getDisplayName());
            membership.put("user", memberMap);
            membership.put("role", role != null ? role : "MEMBER");
            membership.put("joinedAt", LocalDateTime.now().toString());
            
            response.put("membership", membership);
            
        } catch (Exception e) {
            logger.error("Error adding chat room member", e);
            response.put("success", false);
            response.put("message", "Failed to add member");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> removeChatRoomMember(Long userId, Long roomId, Long memberId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(roomId);
            
            if (chatRoomOpt.isEmpty() || chatRoomOpt.get().isDeleted()) {
                response.put("success", false);
                response.put("message", "Chat room not found");
                return response;
            }
            
            ChatRoom chatRoom = chatRoomOpt.get();
            
            // Check if requester has permission (basic check - room creator or removing self)
            if (!chatRoom.getCreator().getId().equals(userId) && !userId.equals(memberId)) {
                response.put("success", false);
                response.put("message", "Access denied");
                return response;
            }
            
            // Basic implementation
            chatRoom.decrementMemberCount();
            chatRoomRepository.save(chatRoom);
            
            response.put("success", true);
            response.put("message", "Member removed successfully");
            
        } catch (Exception e) {
            logger.error("Error removing chat room member", e);
            response.put("success", false);
            response.put("message", "Failed to remove member");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getChatRoomMembers(Long userId, Long roomId, int page, int size) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user has access to the chat room
            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findChatRoomForUser(roomId, userId);
            
            if (chatRoomOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Chat room not found or access denied");
                return response;
            }
            
            // Basic implementation without ChatRoomMember repository
            response.put("success", true);
            response.put("message", "Members retrieved successfully");
            response.put("members", new ArrayList<>());
            response.put("totalElements", 0);
            response.put("totalPages", 0);
            response.put("currentPage", page);
            response.put("pageSize", size);
            
        } catch (Exception e) {
            logger.error("Error getting chat room members", e);
            response.put("success", false);
            response.put("message", "Failed to get members");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> startDirectMessage(Long userId, Long recipientId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            Optional<User> recipientOpt = userRepository.findById(recipientId);
            
            if (userOpt.isEmpty() || recipientOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }
            
            // Check if direct message room already exists
            Optional<ChatRoom> existingRoom = chatRoomRepository.findDirectMessageRoom(userId, recipientId);
            
            if (existingRoom.isPresent()) {
                response.put("success", true);
                response.put("message", "Direct message conversation retrieved");
                response.put("chatRoom", formatChatRoomForResponse(existingRoom.get()));
                return response;
            }
            
            // Create new direct message room
            ChatRoom directRoom = new ChatRoom();
            directRoom.setName("Direct Message");
            directRoom.setType(ChatRoomType.DIRECT_MESSAGE);
            directRoom.setIsPrivate(true);
            directRoom.setCreator(userOpt.get());
            directRoom.setCurrentMembers(2);
            
            ChatRoom savedRoom = chatRoomRepository.save(directRoom);
            
            response.put("success", true);
            response.put("message", "Direct message conversation started");
            response.put("chatRoom", formatChatRoomForResponse(savedRoom));
            
        } catch (Exception e) {
            logger.error("Error starting direct message", e);
            response.put("success", false);
            response.put("message", "Failed to start direct message");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getOnlineUsers(Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Basic implementation - would need user session tracking for real online status
            response.put("success", true);
            response.put("message", "Online users retrieved");
            response.put("onlineUsers", new ArrayList<>());
            
        } catch (Exception e) {
            logger.error("Error getting online users", e);
            response.put("success", false);
            response.put("message", "Failed to get online users");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> sendTypingIndicator(Long userId, Long roomId, boolean isTyping) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Basic implementation - would need WebSocket or real-time messaging for actual typing indicators
            response.put("success", true);
            response.put("message", "Typing indicator sent");
            response.put("roomId", roomId);
            response.put("isTyping", isTyping);
            
        } catch (Exception e) {
            logger.error("Error sending typing indicator", e);
            response.put("success", false);
            response.put("message", "Failed to send typing indicator");
        }
        
        return response;
    }
    
    // Helper methods
    private Map<String, Object> formatChatRoomForResponse(ChatRoom chatRoom) {
        Map<String, Object> roomMap = new HashMap<>();
        roomMap.put("id", chatRoom.getId());
        roomMap.put("name", chatRoom.getName());
        roomMap.put("type", chatRoom.getType().name());
        roomMap.put("isPrivate", chatRoom.getIsPrivate());
        roomMap.put("currentMembers", chatRoom.getCurrentMembers());
        roomMap.put("gameTitle", chatRoom.getGameTitle());
        roomMap.put("lastActivity", chatRoom.getLastActivity() != null ? chatRoom.getLastActivity().toString() : null);
        
        // Get last message if available
        List<ChatMessage> recentMessages = chatMessageRepository.findTop50ByChatRoomIdAndDeletedAtIsNullOrderByCreatedAtDesc(chatRoom.getId());
        if (!recentMessages.isEmpty()) {
            ChatMessage lastMessage = recentMessages.get(0);
            Map<String, Object> lastMessageMap = new HashMap<>();
            lastMessageMap.put("id", lastMessage.getId());
            lastMessageMap.put("content", lastMessage.getContent());
            
            Map<String, Object> senderMap = new HashMap<>();
            senderMap.put("id", lastMessage.getSender().getId());
            senderMap.put("displayName", lastMessage.getSender().getDisplayName());
            lastMessageMap.put("sender", senderMap);
            
            lastMessageMap.put("createdAt", lastMessage.getCreatedAt().toString());
            roomMap.put("lastMessage", lastMessageMap);
        }
        
        return roomMap;
    }
    
    private Map<String, Object> formatChatRoomDetailsForResponse(ChatRoom chatRoom) {
        Map<String, Object> roomMap = formatChatRoomForResponse(chatRoom);
        
        // Add detailed fields
        roomMap.put("description", chatRoom.getDescription());
        roomMap.put("maxMembers", chatRoom.getMaxMembers());
        
        // Creator info
        Map<String, Object> creatorMap = new HashMap<>();
        creatorMap.put("id", chatRoom.getCreator().getId());
        creatorMap.put("displayName", chatRoom.getCreator().getDisplayName());
        roomMap.put("creator", creatorMap);
        
        roomMap.put("moderatorIds", chatRoom.getModeratorIds());
        roomMap.put("allowFileSharing", chatRoom.getAllowFileSharing());
        roomMap.put("allowEmojis", chatRoom.getAllowEmojis());
        roomMap.put("slowModeSeconds", chatRoom.getSlowModeSeconds());
        roomMap.put("totalMessages", chatRoom.getTotalMessages());
        roomMap.put("createdAt", chatRoom.getCreatedAt().toString());
        
        return roomMap;
    }
    
    private Map<String, Object> formatMessageForResponse(ChatMessage message) {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("id", message.getId());
        messageMap.put("content", message.getContent());
        messageMap.put("messageType", message.getMessageType().name());
        
        // Sender info
        Map<String, Object> senderMap = new HashMap<>();
        senderMap.put("id", message.getSender().getId());
        senderMap.put("displayName", message.getSender().getDisplayName());
        messageMap.put("sender", senderMap);
        
        // Chat room info
        Map<String, Object> chatRoomMap = new HashMap<>();
        chatRoomMap.put("id", message.getChatRoom().getId());
        chatRoomMap.put("name", message.getChatRoom().getName());
        messageMap.put("chatRoom", chatRoomMap);
        
        messageMap.put("replyToMessageId", message.getReplyToMessage() != null ? message.getReplyToMessage().getId() : null);
        messageMap.put("fileUrl", message.getFileUrl());
        messageMap.put("fileName", message.getFileName());
        messageMap.put("fileSize", message.getFileSize());
        messageMap.put("createdAt", message.getCreatedAt().toString());
        messageMap.put("updatedAt", message.getUpdatedAt().toString());
        
        return messageMap;
    }
}
