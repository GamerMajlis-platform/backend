package com.gamermajilis.service;

import java.util.Map;

public interface ChatService {
    
    Map<String, Object> createChatRoom(Long userId, Map<String, Object> roomData);
    
    Map<String, Object> getUserChatRooms(Long userId, int page, int size);
    
    Map<String, Object> getChatRoomDetails(Long userId, Long roomId);
    
    Map<String, Object> joinChatRoom(Long userId, Long roomId);
    
    Map<String, Object> leaveChatRoom(Long userId, Long roomId);
    
    Map<String, Object> sendMessage(Long userId, Long roomId, Map<String, Object> messageData);
    
    Map<String, Object> getChatMessages(Long userId, Long roomId, int page, int size, Map<String, Object> filters);
    
    Map<String, Object> deleteMessage(Long userId, Long messageId);
    
    Map<String, Object> addChatRoomMember(Long userId, Long roomId, Long memberId, String role);
    
    Map<String, Object> removeChatRoomMember(Long userId, Long roomId, Long memberId);
    
    Map<String, Object> getChatRoomMembers(Long userId, Long roomId, int page, int size);
    
    Map<String, Object> startDirectMessage(Long userId, Long recipientId);
    
    Map<String, Object> getOnlineUsers(Long userId);
    
    Map<String, Object> sendTypingIndicator(Long userId, Long roomId, boolean isTyping);
}
