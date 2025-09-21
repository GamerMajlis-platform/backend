package com.gamermajilis.repository;

import com.gamermajilis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByDiscordId(String discordId);
    
    Optional<User> findByDisplayName(String displayName);
    
    Optional<User> findByVerificationToken(String verificationToken);
    
    boolean existsByEmail(String email);
    
    boolean existsByDiscordId(String discordId);
    
    boolean existsByDisplayName(String displayName);
    
    // Check if display name exists excluding specific user
    boolean existsByDisplayNameAndIdNot(String displayName, Long id);
    
    // Find by email or display name (for login)
    Optional<User> findByEmailOrDisplayName(String email, String displayName);
    
    @Query("SELECT u FROM User u WHERE (u.email = :identifier OR u.displayName = :identifier) AND u.active = true")
    Optional<User> findByEmailOrDisplayNameAndActive(@Param("identifier") String identifier);
    
    // Search users by display name or discord username
    @Query("SELECT u FROM User u WHERE u.active = true AND u.banned = false AND " +
           "(LOWER(u.displayName) LIKE LOWER(CONCAT('%', :displayName, '%')) OR " +
           "LOWER(u.discordUsername) LIKE LOWER(CONCAT('%', :discordUsername, '%')))")
    org.springframework.data.domain.Page<User> findByDisplayNameContainingIgnoreCaseOrDiscordUsernameContainingIgnoreCase(
        @Param("displayName") String displayName, 
        @Param("discordUsername") String discordUsername, 
        org.springframework.data.domain.Pageable pageable);
    
    // Find active users excluding specific user
    @Query("SELECT u FROM User u WHERE u.id != :excludeUserId AND u.active = true AND u.banned = false " +
           "AND u.lastLogin IS NOT NULL ORDER BY u.lastLogin DESC")
    java.util.List<User> findActiveUsersExcluding(@Param("excludeUserId") Long excludeUserId, org.springframework.data.domain.Pageable pageable);
}