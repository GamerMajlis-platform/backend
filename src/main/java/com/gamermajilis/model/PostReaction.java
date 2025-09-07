package com.gamermajilis.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_reactions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
public class PostReaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private ReactionType reactionType;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public PostReaction() {}
    
    public PostReaction(Post post, User user, ReactionType reactionType) {
        this.post = post;
        this.user = user;
        this.reactionType = reactionType;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public Post getPost() {
        return post;
    }
    
    public User getUser() {
        return user;
    }
    
    public ReactionType getReactionType() {
        return reactionType;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
} 