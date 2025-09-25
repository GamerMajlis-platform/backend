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
public class PostServiceImpl implements PostService {
    
    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private PostCommentRepository postCommentRepository;
    
    @Autowired
    private PostReactionRepository postReactionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MediaRepository mediaRepository;
    
    @Override
    public Map<String, Object> createPost(Long userId, Map<String, Object> postData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get author
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }
            
            User author = userOpt.get();
            
            // Create post
            Post post = new Post();
            post.setTitle((String) postData.get("title"));
            post.setContent((String) postData.get("content"));
            post.setAuthor(author);
            
            // Set post type
            String typeStr = (String) postData.get("type");
            if (typeStr != null) {
                try {
                    PostType type = PostType.valueOf(typeStr.toUpperCase());
                    post.setType(type);
                } catch (IllegalArgumentException e) {
                    post.setType(PostType.TEXT);
                }
            } else {
                post.setType(PostType.TEXT);
            }
            
            // Set optional fields
            String gameTitle = (String) postData.get("gameTitle");
            if (gameTitle != null && !gameTitle.trim().isEmpty()) {
                post.setGameTitle(gameTitle);
            }
            
            String gameCategory = (String) postData.get("gameCategory");
            if (gameCategory != null && !gameCategory.trim().isEmpty()) {
                post.setGameCategory(gameCategory);
            }
            
            String platform = (String) postData.get("platform");
            if (platform != null && !platform.trim().isEmpty()) {
                post.setPlatform(platform);
            }
            
            String tags = (String) postData.get("tags");
            if (tags != null && !tags.trim().isEmpty()) {
                post.setTags(tags);
            }
            
            String hashtags = (String) postData.get("hashtags");
            if (hashtags != null && !hashtags.trim().isEmpty()) {
                post.setHashtags(hashtags);
            }
            
            // Set visibility
            String visibilityStr = (String) postData.get("visibility");
            if (visibilityStr != null) {
                try {
                    PostVisibility visibility = PostVisibility.valueOf(visibilityStr.toUpperCase());
                    post.setVisibility(visibility);
                } catch (IllegalArgumentException e) {
                    post.setVisibility(PostVisibility.PUBLIC);
                }
            } else {
                post.setVisibility(PostVisibility.PUBLIC);
            }
            
            // Handle media attachments
            @SuppressWarnings("unchecked")
            List<Long> mediaIds = (List<Long>) postData.get("mediaIds");
            if (mediaIds != null && !mediaIds.isEmpty()) {
                List<Media> attachedMedia = mediaRepository.findAllById(mediaIds);
                post.setAttachedMedia(attachedMedia);
            }
            
            // Set moderation status (auto-approve for now)
            post.setModerationStatus("APPROVED");
            post.setPublishedAt(LocalDateTime.now());
            
            // Save post
            Post savedPost = postRepository.save(post);
            
            response.put("success", true);
            response.put("message", "Post created successfully");
            response.put("post", formatPostForResponse(savedPost));
            
        } catch (Exception e) {
            logger.error("Error creating post", e);
            response.put("success", false);
            response.put("message", "Failed to create post: " + e.getMessage());
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getPostDetails(Long postId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Post> postOpt = postRepository.findByIdAndVisibilityAndModerationStatusAndDeletedAtIsNull(
                postId, PostVisibility.PUBLIC, "APPROVED");
            
            if (postOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Post not found");
                return response;
            }
            
            Post post = postOpt.get();
            
            // Increment view count
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
            
            response.put("success", true);
            response.put("message", "Post retrieved successfully");
            response.put("post", formatPostDetailsForResponse(post));
            
        } catch (Exception e) {
            logger.error("Error getting post details", e);
            response.put("success", false);
            response.put("message", "Failed to get post details");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getPostsFeed(int page, int size, Map<String, Object> filters) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> postsPage;
            
            Long authorId = (Long) filters.get("authorId");
            String gameCategory = (String) filters.get("gameCategory");
            String type = (String) filters.get("type");
            
            if (authorId != null) {
                // Get user's posts
                postsPage = postRepository.findByAuthorIdAndDeletedAtIsNullOrderByCreatedAtDesc(
                    authorId, pageable);
            } else {
                // Get public approved posts with filters
                PostVisibility visibility = PostVisibility.PUBLIC;
                String moderationStatus = "APPROVED";
                
                if (gameCategory != null && type != null) {
                    PostType postType = PostType.valueOf(type.toUpperCase());
                    postsPage = postRepository.findByTypeAndVisibilityAndModerationStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
                        postType, visibility, moderationStatus, pageable);
                } else if (gameCategory != null) {
                    postsPage = postRepository.findByGameCategoryAndVisibilityAndModerationStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
                        gameCategory, visibility, moderationStatus, pageable);
                } else if (type != null) {
                    PostType postType = PostType.valueOf(type.toUpperCase());
                    postsPage = postRepository.findByTypeAndVisibilityAndModerationStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
                        postType, visibility, moderationStatus, pageable);
                } else {
                    postsPage = postRepository.findByVisibilityAndModerationStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
                        visibility, moderationStatus, pageable);
                }
            }
            
            List<Map<String, Object>> postsList = postsPage.getContent().stream()
                .map(this::formatPostForResponse)
                .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("message", "Posts feed retrieved");
            response.put("posts", postsList);
            response.put("totalElements", postsPage.getTotalElements());
            response.put("totalPages", postsPage.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);
            
        } catch (Exception e) {
            logger.error("Error getting posts feed", e);
            response.put("success", false);
            response.put("message", "Failed to get posts feed");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> updatePost(Long userId, Long postId, Map<String, Object> updateData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Post> postOpt = postRepository.findByIdAndAuthorIdAndDeletedAtIsNull(postId, userId);
            
            if (postOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Post not found or access denied");
                return response;
            }
            
            Post post = postOpt.get();
            
            // Update fields if provided
            if (updateData.containsKey("title")) {
                post.setTitle((String) updateData.get("title"));
            }
            if (updateData.containsKey("content")) {
                post.setContent((String) updateData.get("content"));
            }
            if (updateData.containsKey("gameTitle")) {
                post.setGameTitle((String) updateData.get("gameTitle"));
            }
            if (updateData.containsKey("gameCategory")) {
                post.setGameCategory((String) updateData.get("gameCategory"));
            }
            if (updateData.containsKey("platform")) {
                post.setPlatform((String) updateData.get("platform"));
            }
            if (updateData.containsKey("tags")) {
                post.setTags((String) updateData.get("tags"));
            }
            if (updateData.containsKey("hashtags")) {
                post.setHashtags((String) updateData.get("hashtags"));
            }
            if (updateData.containsKey("visibility")) {
                String visibilityStr = (String) updateData.get("visibility");
                try {
                    PostVisibility visibility = PostVisibility.valueOf(visibilityStr.toUpperCase());
                    post.setVisibility(visibility);
                } catch (IllegalArgumentException e) {
                    // Keep current visibility if invalid
                }
            }
            
            Post updatedPost = postRepository.save(post);
            
            response.put("success", true);
            response.put("message", "Post updated successfully");
            response.put("post", formatPostForResponse(updatedPost));
            
        } catch (Exception e) {
            logger.error("Error updating post", e);
            response.put("success", false);
            response.put("message", "Failed to update post");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> deletePost(Long userId, Long postId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Post> postOpt = postRepository.findByIdAndAuthorIdAndDeletedAtIsNull(postId, userId);
            
            if (postOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Post not found or access denied");
                return response;
            }
            
            Post post = postOpt.get();
            
            // Soft delete
            post.setDeletedAt(LocalDateTime.now());
            postRepository.save(post);
            
            response.put("success", true);
            response.put("message", "Post deleted successfully");
            
        } catch (Exception e) {
            logger.error("Error deleting post", e);
            response.put("success", false);
            response.put("message", "Failed to delete post");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> toggleLike(Long userId, Long postId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if post exists and is public
            Optional<Post> postOpt = postRepository.findByIdAndVisibilityAndModerationStatusAndDeletedAtIsNull(
                postId, PostVisibility.PUBLIC, "APPROVED");
            
            if (postOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Post not found");
                return response;
            }
            
            Post post = postOpt.get();
            
            // Get user
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }
            
            User user = userOpt.get();
            
            // Check if user already liked the post
            Optional<PostReaction> existingReaction = postReactionRepository.findByPostIdAndUserId(postId, userId);
            
            boolean liked;
            if (existingReaction.isPresent()) {
                // Remove like
                postReactionRepository.delete(existingReaction.get());
                post.setLikeCount(post.getLikeCount() - 1);
                liked = false;
            } else {
                // Add like
                PostReaction reaction = new PostReaction(post, user, ReactionType.LIKE);
                postReactionRepository.save(reaction);
                post.setLikeCount(post.getLikeCount() + 1);
                liked = true;
            }
            
            postRepository.save(post);
            
            response.put("success", true);
            response.put("message", liked ? "Post liked successfully" : "Post unliked successfully");
            response.put("liked", liked);
            response.put("newLikeCount", post.getLikeCount());
            
        } catch (Exception e) {
            logger.error("Error toggling like on post", e);
            response.put("success", false);
            response.put("message", "Failed to toggle like");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> addComment(Long userId, Long postId, String content) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if post exists and is public
            Optional<Post> postOpt = postRepository.findByIdAndVisibilityAndModerationStatusAndDeletedAtIsNull(
                postId, PostVisibility.PUBLIC, "APPROVED");
            
            if (postOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Post not found");
                return response;
            }
            
            Post post = postOpt.get();
            
            // Get user
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }
            
            User user = userOpt.get();
            
            // Create comment
            PostComment comment = new PostComment();
            comment.setPost(post);
            comment.setAuthor(user);
            comment.setContent(content);
            
            PostComment savedComment = postCommentRepository.save(comment);
            
            // Update comment count on post
            post.setCommentCount(post.getCommentCount() + 1);
            postRepository.save(post);
            
            response.put("success", true);
            response.put("message", "Comment added successfully");
            response.put("comment", formatCommentForResponse(savedComment));
            
        } catch (Exception e) {
            logger.error("Error adding comment", e);
            response.put("success", false);
            response.put("message", "Failed to add comment");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getPostComments(Long postId, int page, int size) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if post exists and is public
            Optional<Post> postOpt = postRepository.findByIdAndVisibilityAndModerationStatusAndDeletedAtIsNull(
                postId, PostVisibility.PUBLIC, "APPROVED");
            
            if (postOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Post not found");
                return response;
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<PostComment> commentsPage = postCommentRepository.findByPostIdAndDeletedAtIsNullOrderByCreatedAtDesc(
                postId, pageable);
            
            List<Map<String, Object>> commentsList = commentsPage.getContent().stream()
                .map(this::formatCommentForResponse)
                .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("message", "Comments retrieved successfully");
            response.put("comments", commentsList);
            response.put("totalElements", commentsPage.getTotalElements());
            response.put("totalPages", commentsPage.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);
            
        } catch (Exception e) {
            logger.error("Error getting post comments", e);
            response.put("success", false);
            response.put("message", "Failed to get comments");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> deleteComment(Long userId, Long commentId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<PostComment> commentOpt = postCommentRepository.findByIdAndAuthorIdAndDeletedAtIsNull(
                commentId, userId);
            
            if (commentOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Comment not found or access denied");
                return response;
            }
            
            PostComment comment = commentOpt.get();
            Post post = comment.getPost();
            
            // Soft delete comment
            comment.setDeletedAt(LocalDateTime.now());
            postCommentRepository.save(comment);
            
            // Update comment count on post
            if (post.getCommentCount() > 0) {
                post.setCommentCount(post.getCommentCount() - 1);
                postRepository.save(post);
            }
            
            response.put("success", true);
            response.put("message", "Comment deleted successfully");
            
        } catch (Exception e) {
            logger.error("Error deleting comment", e);
            response.put("success", false);
            response.put("message", "Failed to delete comment");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> sharePost(Long userId, Long postId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if post exists and is public
            Optional<Post> postOpt = postRepository.findByIdAndVisibilityAndModerationStatusAndDeletedAtIsNull(
                postId, PostVisibility.PUBLIC, "APPROVED");
            
            if (postOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Post not found");
                return response;
            }
            
            Post post = postOpt.get();
            
            // Increment share count
            post.setShareCount(post.getShareCount() + 1);
            postRepository.save(post);
            
            response.put("success", true);
            response.put("message", "Post shared successfully");
            response.put("newShareCount", post.getShareCount());
            
        } catch (Exception e) {
            logger.error("Error sharing post", e);
            response.put("success", false);
            response.put("message", "Failed to share post");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getTrendingPosts(int limit, int days) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDateTime since = LocalDateTime.now().minusDays(days);
            Pageable pageable = PageRequest.of(0, limit);
            
            List<Post> trendingPosts = postRepository.findTrendingPosts(since, pageable);
            
            List<Map<String, Object>> postsList = trendingPosts.stream()
                .map(this::formatPostForResponse)
                .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("message", "Trending posts retrieved");
            response.put("posts", postsList);
            
        } catch (Exception e) {
            logger.error("Error getting trending posts", e);
            response.put("success", false);
            response.put("message", "Failed to get trending posts");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> searchPosts(String query, int page, int size, String gameCategory) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> postsPage;
            
            if (gameCategory != null && !gameCategory.trim().isEmpty()) {
                postsPage = postRepository.searchPostsByGameCategory(
                    query, gameCategory, PostVisibility.PUBLIC, pageable);
            } else {
                postsPage = postRepository.searchPosts(
                    query, PostVisibility.PUBLIC, pageable);
            }
            
            List<Map<String, Object>> postsList = postsPage.getContent().stream()
                .map(this::formatPostForResponse)
                .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("message", "Posts search completed");
            response.put("posts", postsList);
            response.put("totalElements", postsPage.getTotalElements());
            response.put("totalPages", postsPage.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);
            
        } catch (Exception e) {
            logger.error("Error searching posts", e);
            response.put("success", false);
            response.put("message", "Search failed");
        }
        
        return response;
    }
    
    // Helper methods
    private Map<String, Object> formatPostForResponse(Post post) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", post.getId());
        response.put("title", post.getTitle());
        response.put("content", post.getContent());
        response.put("type", post.getType());
        response.put("gameTitle", post.getGameTitle());
        response.put("gameCategory", post.getGameCategory());
        response.put("platform", post.getPlatform());
        response.put("tags", post.getTags());
        response.put("hashtags", post.getHashtags());
        response.put("visibility", post.getVisibility());
        response.put("viewCount", post.getViewCount());
        response.put("likeCount", post.getLikeCount());
        response.put("commentCount", post.getCommentCount());
        response.put("shareCount", post.getShareCount());
        response.put("createdAt", post.getCreatedAt());
        
        // Include author info
        if (post.getAuthor() != null) {
            Map<String, Object> authorInfo = new HashMap<>();
            authorInfo.put("id", post.getAuthor().getId());
            authorInfo.put("displayName", post.getAuthor().getDisplayName());
            authorInfo.put("profilePictureUrl", post.getAuthor().getProfilePictureUrl());
            response.put("author", authorInfo);
        }
        
        // Include attached media info
        if (post.getAttachedMedia() != null && !post.getAttachedMedia().isEmpty()) {
            List<Map<String, Object>> mediaList = post.getAttachedMedia().stream()
                .map(media -> {
                    Map<String, Object> mediaInfo = new HashMap<>();
                    mediaInfo.put("id", media.getId());
                    mediaInfo.put("title", media.getTitle());
                    mediaInfo.put("thumbnailPath", media.getThumbnailPath());
                    mediaInfo.put("mediaType", media.getMediaType());
                    return mediaInfo;
                })
                .collect(Collectors.toList());
            response.put("attachedMedia", mediaList);
        } else {
            response.put("attachedMedia", new ArrayList<>());
        }
        
        return response;
    }
    
    private Map<String, Object> formatPostDetailsForResponse(Post post) {
        Map<String, Object> response = formatPostForResponse(post);
        // Add additional details for full post view
        response.put("updatedAt", post.getUpdatedAt());
        response.put("publishedAt", post.getPublishedAt());
        response.put("moderationStatus", post.getModerationStatus());
        return response;
    }
    
    private Map<String, Object> formatCommentForResponse(PostComment comment) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", comment.getId());
        response.put("content", comment.getContent());
        response.put("createdAt", comment.getCreatedAt());
        
        // Include author info
        if (comment.getAuthor() != null) {
            Map<String, Object> authorInfo = new HashMap<>();
            authorInfo.put("id", comment.getAuthor().getId());
            authorInfo.put("displayName", comment.getAuthor().getDisplayName());
            authorInfo.put("profilePictureUrl", comment.getAuthor().getProfilePictureUrl());
            response.put("author", authorInfo);
        }
        
        return response;
    }
}
