package com.gamermajilis.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Discord Integration
    @Column(name = "discord_id", unique = true)
    private String discordId;
    
    @Column(name = "discord_username")
    private String discordUsername;
    
    @Column(name = "discord_discriminator")
    private String discordDiscriminator;
    
    // Email Authentication
    @Email
    @Column(name = "email", unique = true)
    private String email;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;
    
    @Column(name = "verification_token")
    private String verificationToken;
    
    // Profile Information
    @NotBlank
    @Size(min = 3, max = 30)
    @Column(name = "display_name", nullable = false)
    private String displayName;
    
    @Size(max = 500)
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
    
    @Column(name = "gaming_preferences", columnDefinition = "TEXT")
    private String gamingPreferences;
    
    @Column(name = "social_links", columnDefinition = "TEXT")
    private String socialLinks; // JSON format
    
    @Column(name = "gaming_statistics", columnDefinition = "TEXT")
    private String gamingStatistics; // JSON format
    
    // User Roles
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<UserRole> roles = new HashSet<>();
    
    // Account Status
    @Column(name = "active", nullable = false)
    private Boolean active = true;
    
    @Column(name = "banned", nullable = false)
    private Boolean banned = false;
    
    @Column(name = "ban_reason")
    private String banReason;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    // Privacy Settings
    @Column(name = "privacy_settings", columnDefinition = "TEXT")
    private String privacySettings; // JSON format
    
    // Authentication Provider
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false)
    private AuthProvider authProvider;
    
    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public User() {
        this.roles.add(UserRole.REGULAR_GAMER);
    }
    
    public User(String email, String displayName, AuthProvider authProvider) {
        this();
        this.email = email;
        this.displayName = displayName;
        this.authProvider = authProvider;
    }
    
    public User(String discordId, String discordUsername, String email, String displayName) {
        this();
        this.discordId = discordId;
        this.discordUsername = discordUsername;
        this.email = email;
        this.displayName = displayName;
        this.authProvider = AuthProvider.DISCORD;
        this.emailVerified = true; // Discord emails are considered verified
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDiscordId() {
        return discordId;
    }
    
    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }
    
    public String getDiscordUsername() {
        return discordUsername;
    }
    
    public void setDiscordUsername(String discordUsername) {
        this.discordUsername = discordUsername;
    }
    
    public String getDiscordDiscriminator() {
        return discordDiscriminator;
    }
    
    public void setDiscordDiscriminator(String discordDiscriminator) {
        this.discordDiscriminator = discordDiscriminator;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Boolean getEmailVerified() {
        return emailVerified;
    }
    
    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    public String getVerificationToken() {
        return verificationToken;
    }
    
    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
    
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
    
    public String getGamingPreferences() {
        return gamingPreferences;
    }
    
    public void setGamingPreferences(String gamingPreferences) {
        this.gamingPreferences = gamingPreferences;
    }
    
    public String getSocialLinks() {
        return socialLinks;
    }
    
    public void setSocialLinks(String socialLinks) {
        this.socialLinks = socialLinks;
    }
    
    public String getGamingStatistics() {
        return gamingStatistics;
    }
    
    public void setGamingStatistics(String gamingStatistics) {
        this.gamingStatistics = gamingStatistics;
    }
    
    public Set<UserRole> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public Boolean getBanned() {
        return banned;
    }
    
    public void setBanned(Boolean banned) {
        this.banned = banned;
    }
    
    public String getBanReason() {
        return banReason;
    }
    
    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public String getPrivacySettings() {
        return privacySettings;
    }
    
    public void setPrivacySettings(String privacySettings) {
        this.privacySettings = privacySettings;
    }
    
    public AuthProvider getAuthProvider() {
        return authProvider;
    }
    
    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Helper methods
    public boolean hasRole(UserRole role) {
        return this.roles.contains(role);
    }
    
    public void addRole(UserRole role) {
        this.roles.add(role);
    }
    
    public void removeRole(UserRole role) {
        this.roles.remove(role);
    }
    
    public boolean isDiscordUser() {
        return this.authProvider == AuthProvider.DISCORD && this.discordId != null;
    }
    
    public boolean isEmailUser() {
        return this.authProvider == AuthProvider.EMAIL;
    }
} 