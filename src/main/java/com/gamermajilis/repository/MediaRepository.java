package com.gamermajilis.repository;

import com.gamermajilis.model.Media;
import com.gamermajilis.model.MediaType;
import com.gamermajilis.model.MediaVisibility;
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
public interface MediaRepository extends JpaRepository<Media, Long> {
    
    // Find by uploader
    Page<Media> findByUploaderIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long uploaderId, Pageable pageable);
    
    // Find by visibility and moderation status
    Page<Media> findByVisibilityAndModerationStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
        MediaVisibility visibility, String moderationStatus, Pageable pageable);
    
    // Find by media type
    Page<Media> findByMediaTypeAndVisibilityAndModerationStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
        MediaType mediaType, MediaVisibility visibility, String moderationStatus, Pageable pageable);
    
    // Find by game category
    Page<Media> findByGameCategoryAndVisibilityAndModerationStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
        String gameCategory, MediaVisibility visibility, String moderationStatus, Pageable pageable);
    
    // Search by title or tags
    @Query("SELECT m FROM Media m WHERE m.deletedAt IS NULL AND m.visibility = :visibility " +
           "AND m.moderationStatus = 'APPROVED' AND " +
           "(LOWER(m.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.tags) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.gameCategory) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY m.createdAt DESC")
    Page<Media> searchMedia(@Param("query") String query, 
                           @Param("visibility") MediaVisibility visibility, 
                           Pageable pageable);
    
    // Search by type
    @Query("SELECT m FROM Media m WHERE m.deletedAt IS NULL AND m.visibility = :visibility " +
           "AND m.moderationStatus = 'APPROVED' AND m.mediaType = :mediaType AND " +
           "(LOWER(m.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.tags) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.gameCategory) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY m.createdAt DESC")
    Page<Media> searchMediaByType(@Param("query") String query, 
                                 @Param("mediaType") MediaType mediaType,
                                 @Param("visibility") MediaVisibility visibility, 
                                 Pageable pageable);
    
    // Find trending media (most viewed in recent days)
    @Query("SELECT m FROM Media m WHERE m.deletedAt IS NULL AND m.visibility = 'PUBLIC' " +
           "AND m.moderationStatus = 'APPROVED' AND m.createdAt >= :since " +
           "ORDER BY m.viewCount DESC, m.createdAt DESC")
    List<Media> findTrendingMedia(@Param("since") LocalDateTime since, Pageable pageable);
    
    // Find by uploader and ID for authorization checks
    Optional<Media> findByIdAndUploaderIdAndDeletedAtIsNull(Long id, Long uploaderId);
    
    // Find public media by ID
    Optional<Media> findByIdAndVisibilityAndModerationStatusAndDeletedAtIsNull(
        Long id, MediaVisibility visibility, String moderationStatus);
        
    // Count media by uploader
    long countByUploaderIdAndDeletedAtIsNull(Long uploaderId);
    
    // Find recent uploads by uploader
    List<Media> findTop10ByUploaderIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long uploaderId);
}
