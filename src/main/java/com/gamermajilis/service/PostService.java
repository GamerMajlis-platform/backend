package com.gamermajilis.service;

import java.util.Map;

public interface PostService {
    
    Map<String, Object> createPost(Long userId, Map<String, Object> postData);
    
    Map<String, Object> getPostDetails(Long postId);
    
    Map<String, Object> getPostsFeed(int page, int size, Map<String, Object> filters);
    
    Map<String, Object> updatePost(Long userId, Long postId, Map<String, Object> updateData);
    
    Map<String, Object> deletePost(Long userId, Long postId);
    
    Map<String, Object> toggleLike(Long userId, Long postId);
    
    Map<String, Object> addComment(Long userId, Long postId, String content);
    
    Map<String, Object> getPostComments(Long postId, int page, int size);
    
    Map<String, Object> deleteComment(Long userId, Long commentId);
    
    Map<String, Object> sharePost(Long userId, Long postId);
    
    Map<String, Object> getTrendingPosts(int limit, int days);
    
    Map<String, Object> searchPosts(String query, int page, int size, String gameCategory);
}
