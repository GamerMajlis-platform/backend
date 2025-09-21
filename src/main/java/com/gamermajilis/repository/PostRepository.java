package com.gamermajilis.repository;

import com.gamermajilis.model.Post;
import com.gamermajilis.model.PostType;
import com.gamermajilis.model.PostVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // Find posts for feed (public, approved posts)
    Page<Post> findByVisibilityAndModerationStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
        PostVisibility visibility, String moderationStatus, Pageable pageable);
    
    // Find posts by author
    Page<Post> findByAuthorIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long authorId, Pageable pageable);
    
    // Find posts by game category
    Page<Post> findByGameCategoryAndVisibilityAndModerationStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
        String gameCategory, PostVisibility visibility, String moderationStatus, Pageable pageable);
    
    // Find posts by type
    Page<Post> findByTypeAndVisibilityAndModerationStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
        PostType type, PostVisibility visibility, String moderationStatus, Pageable pageable);
    
    // Find post by ID and author for authorization
    Optional<Post> findByIdAndAuthorIdAndDeletedAtIsNull(Long id, Long authorId);
    
    // Find public post by ID
    Optional<Post> findByIdAndVisibilityAndModerationStatusAndDeletedAtIsNull(
        Long id, PostVisibility visibility, String moderationStatus);
    
    // Search posts by title, content, or tags
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL AND p.visibility = :visibility " +
           "AND p.moderationStatus = 'APPROVED' AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.tags) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.hashtags) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY p.createdAt DESC")
    Page<Post> searchPosts(@Param("query") String query, 
                          @Param("visibility") PostVisibility visibility, 
                          Pageable pageable);
    
    // Search posts by game category
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL AND p.visibility = :visibility " +
           "AND p.moderationStatus = 'APPROVED' AND p.gameCategory = :gameCategory AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.tags) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.hashtags) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY p.createdAt DESC")
    Page<Post> searchPostsByGameCategory(@Param("query") String query, 
                                        @Param("gameCategory") String gameCategory,
                                        @Param("visibility") PostVisibility visibility, 
                                        Pageable pageable);
    
    // Find trending posts (most engagement in recent days)
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL AND p.visibility = 'PUBLIC' " +
           "AND p.moderationStatus = 'APPROVED' AND p.createdAt >= :since " +
           "ORDER BY (p.likeCount + p.commentCount + p.shareCount) DESC, p.createdAt DESC")
    List<Post> findTrendingPosts(@Param("since") LocalDateTime since, Pageable pageable);
    
    // Find featured posts
    List<Post> findByIsFeaturedTrueAndVisibilityAndModerationStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
        PostVisibility visibility, String moderationStatus, Pageable pageable);
    
    // Find pinned posts
    List<Post> findByIsPinnedTrueAndVisibilityAndModerationStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
        PostVisibility visibility, String moderationStatus);
    
    // Count posts by author
    long countByAuthorIdAndDeletedAtIsNull(Long authorId);
    
    // Find posts by tournament or event
    Page<Post> findByTournamentIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long tournamentId, Pageable pageable);
    Page<Post> findByEventIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long eventId, Pageable pageable);
}
