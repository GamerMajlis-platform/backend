package com.gamermajilis.service;

import com.gamermajilis.model.AuthProvider;
import com.gamermajilis.model.User;
import com.gamermajilis.model.UserRole;
import com.gamermajilis.repository.UserRepository;
import com.gamermajilis.security.CustomUserDetails;
import com.gamermajilis.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    public Map<String, Object> signup(String email, String password, String displayName) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate input
            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return response;
            }

            if (password == null || password.length() < 6) {
                response.put("success", false);
                response.put("message", "Password must be at least 6 characters long");
                return response;
            }

            if (displayName == null || displayName.trim().length() < 3) {
                response.put("success", false);
                response.put("message", "Display name must be at least 3 characters long");
                return response;
            }

            // Check if user already exists
            if (userRepository.existsByEmail(email)) {
                response.put("success", false);
                response.put("message", "Email already exists");
                return response;
            }

            if (userRepository.existsByDisplayName(displayName.trim())) {
                response.put("success", false);
                response.put("message", "Display name already exists");
                return response;
            }

            // Generate verification token FIRST
            String verificationToken = UUID.randomUUID().toString();
            logger.info("Generated verification token: {}", verificationToken);

            // Create new user
            User user = new User();
            user.setEmail(email.toLowerCase().trim());
            user.setPassword(passwordEncoder.encode(password));
            user.setDisplayName(displayName.trim());
            user.setAuthProvider(AuthProvider.EMAIL);
            user.setEmailVerified(false);
            user.setVerificationToken(verificationToken);

            // Debug logging
            logger.info("Before save - User verification token: {}", user.getVerificationToken());
            logger.info("Before save - User email verified: {}", user.getEmailVerified());

            // Save user with token
            User savedUser = userRepository.save(user);

            // Verify the token was saved
            logger.info("After save - User ID: {}", savedUser.getId());
            logger.info("After save - User verification token: {}", savedUser.getVerificationToken());
            logger.info("After save - User email verified: {}", savedUser.getEmailVerified());

            // Double-check by fetching from database
            Optional<User> verifyUser = userRepository.findById(savedUser.getId());
            if (verifyUser.isPresent()) {
                User dbUser = verifyUser.get();
                logger.info("DB verification - Token: {}", dbUser.getVerificationToken());
                logger.info("DB verification - Email verified: {}", dbUser.getEmailVerified());

                // Use the DB user for email sending to ensure we have the correct token
                try {
                    emailService.sendVerificationEmail(dbUser.getEmail(), dbUser.getVerificationToken());
                    logger.info("Verification email sent to: {} with token: {}",
                            dbUser.getEmail(), dbUser.getVerificationToken());
                } catch (Exception e) {
                    logger.error("Failed to send verification email to: {}", dbUser.getEmail(), e);
                    // Don't fail the signup process if email fails
                }
            } else {
                logger.error("Could not retrieve saved user from database!");
            }

            response.put("success", true);
            response.put("message", "User registered successfully. Please check your email for verification.");
            response.put("userId", savedUser.getId());

        } catch (Exception e) {
            logger.error("Error during signup", e);
            response.put("success", false);
            response.put("message", "An error occurred during registration");
        }

        return response;
    }

    public Map<String, Object> login(String identifier, String password) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(identifier, password));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // Check if email is verified for email users
            if (!userDetails.isEmailVerified() && userDetails.getEmail() != null) {
                response.put("success", false);
                response.put("message", "Please verify your email before logging in");
                response.put("requiresVerification", true);
                return response;
            }

            // Update last login
            Optional<User> userOpt = userRepository.findByEmailOrDisplayNameAndActive(identifier);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);

                // Generate JWT token
                String token = jwtUtil.generateToken(user);

                response.put("success", true);
                response.put("message", "Login successful");
                response.put("token", token);
                response.put("user", createUserResponse(user));
            }

        } catch (BadCredentialsException e) {
            logger.warn("Failed login attempt for user: {}", identifier);
            response.put("success", false);
            response.put("message", "Invalid credentials");
        } catch (AuthenticationException e) {
            logger.error("Authentication error for user: {}", identifier, e);
            response.put("success", false);
            response.put("message", "Authentication failed");
        } catch (Exception e) {
            logger.error("Error during login", e);
            response.put("success", false);
            response.put("message", "An error occurred during login");
        }

        return response;
    }

    public Map<String, Object> verifyEmail(String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<User> userOpt = userRepository.findByVerificationToken(token);

            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Invalid verification token");
                return response;
            }

            User user = userOpt.get();
            if (user.getEmailVerified()) {
                response.put("success", false);
                response.put("message", "Email already verified");
                return response;
            }

            user.setEmailVerified(true);
            user.setVerificationToken(null);
            userRepository.save(user);

            response.put("success", true);
            response.put("message", "Email verified successfully");

        } catch (Exception e) {
            logger.error("Error during email verification", e);
            response.put("success", false);
            response.put("message", "An error occurred during verification");
        }

        return response;
    }

    public Map<String, Object> resendVerificationEmail(String email) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }

            User user = userOpt.get();
            if (user.getEmailVerified()) {
                response.put("success", false);
                response.put("message", "Email already verified");
                return response;
            }

            // Generate new verification token
            user.setVerificationToken(UUID.randomUUID().toString());
            userRepository.save(user);

            // Send verification email
            emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());

            response.put("success", true);
            response.put("message", "Verification email sent successfully");

        } catch (Exception e) {
            logger.error("Error resending verification email", e);
            response.put("success", false);
            response.put("message", "An error occurred while sending verification email");
        }

        return response;
    }

    public Map<String, Object> logout() {
        Map<String, Object> response = new HashMap<>();

        // Since JWT is stateless, we just return a success response
        // In a production environment, you might want to blacklist tokens
        response.put("success", true);
        response.put("message", "Logged out successfully");

        return response;
    }

    public Map<String, Object> getCurrentUser(Long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }

            User user = userOpt.get();
            response.put("success", true);
            response.put("user", createUserResponse(user));

        } catch (Exception e) {
            logger.error("Error getting current user", e);
            response.put("success", false);
            response.put("message", "An error occurred while fetching user data");
        }

        return response;
    }

    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("email", user.getEmail());
        userResponse.put("displayName", user.getDisplayName());
        userResponse.put("bio", user.getBio());
        userResponse.put("profilePictureUrl", user.getProfilePictureUrl());
        userResponse.put("roles", user.getRoles());
        userResponse.put("authProvider", user.getAuthProvider());
        userResponse.put("emailVerified", user.getEmailVerified());
        userResponse.put("createdAt", user.getCreatedAt());
        userResponse.put("lastLogin", user.getLastLogin());

        // Discord specific fields
        if (user.isDiscordUser()) {
            userResponse.put("discordId", user.getDiscordId());
            userResponse.put("discordUsername", user.getDiscordUsername());
        }

        return userResponse;
    }
}