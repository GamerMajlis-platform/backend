package com.gamermajilis.repository;

import com.gamermajilis.model.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    
    // Find comments by post ID
    Page<PostComment> findByPostIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long postId, Pageable pageable);
    
    // Find comment by ID and author for authorization
    Optional<PostComment> findByIdAndAuthorIdAndDeletedAtIsNull(Long id, Long authorId);
    
    // Find comment by ID (public access)
    Optional<PostComment> findByIdAndDeletedAtIsNull(Long id);
    
    // Count comments by post
    long countByPostIdAndDeletedAtIsNull(Long postId);
    
    // Count comments by author
    long countByAuthorIdAndDeletedAtIsNull(Long authorId);
    
    // Find recent comments by author
    @Query("SELECT c FROM PostComment c WHERE c.author.id = :authorId AND c.deletedAt IS NULL " +
           "ORDER BY c.createdAt DESC")
    Page<PostComment> findRecentCommentsByAuthor(@Param("authorId") Long authorId, Pageable pageable);
}
