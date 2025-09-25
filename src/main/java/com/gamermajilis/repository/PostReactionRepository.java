package com.gamermajilis.repository;

import com.gamermajilis.model.PostReaction;
import com.gamermajilis.model.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {
    
    // Find reaction by post and user
    Optional<PostReaction> findByPostIdAndUserId(Long postId, Long userId);
    
    // Count reactions by post and type
    long countByPostIdAndReactionType(Long postId, ReactionType reactionType);
    
    // Count total reactions by post
    long countByPostId(Long postId);
    
    // Delete reaction by post and user
    void deleteByPostIdAndUserId(Long postId, Long userId);
    
    // Check if user has reacted to post
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    
    // Get reaction type by user and post
    @Query("SELECT pr.reactionType FROM PostReaction pr WHERE pr.post.id = :postId AND pr.user.id = :userId")
    Optional<ReactionType> findReactionTypeByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
}
