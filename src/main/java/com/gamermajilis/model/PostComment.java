package com.gamermajilis.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_comments")
public class PostComment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    @NotBlank
    @Size(max = 2000)
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private PostComment parentComment;
    
    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    public PostComment() {}
    
    public PostComment(Post post, User author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
    }
    
    // Getters and Setters (basic ones for brevity)
    public Long getId() {
        return id;
    }
    
    public Post getPost() {
        return post;
    }
    
    public User getAuthor() {
        return author;
    }
    
    public String getContent() {
        return content;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
} 