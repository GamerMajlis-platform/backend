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
    
    @Query("SELECT u FROM User u WHERE (u.email = :identifier OR u.displayName = :identifier) AND u.active = true")
    Optional<User> findByEmailOrDisplayNameAndActive(@Param("identifier") String identifier);
}