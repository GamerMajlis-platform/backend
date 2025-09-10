package com.gamermajilis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail, String verificationToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("GamerMajilis - Verify Your Email Address");

            String verificationUrl = "http://localhost:3000/verify-email?token=" + verificationToken;
            String emailBody = "Welcome to GamerMajilis!\n\n" +
                    "Please click the link below to verify your email address:\n" +
                    verificationUrl + "\n\n" +
                    "If you didn't create an account with us, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "GamerMajilis Team";

            message.setText(emailBody);

            mailSender.send(message);
            logger.info("Verification email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send verification email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("GamerMajilis - Reset Your Password");

            String resetUrl = "http://localhost:3000/reset-password?token=" + resetToken;
            String emailBody = "Hello,\n\n" +
                    "You requested to reset your password for your GamerMajilis account.\n\n" +
                    "Please click the link below to reset your password:\n" +
                    resetUrl + "\n\n" +
                    "This link will expire in 1 hour.\n\n" +
                    "If you didn't request this password reset, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "GamerMajilis Team";

            message.setText(emailBody);

            mailSender.send(message);
            logger.info("Password reset email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void sendWelcomeEmail(String toEmail, String displayName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to GamerMajilis!");

            String emailBody = "Hello " + displayName + ",\n\n" +
                    "Welcome to GamerMajilis - the ultimate gaming community platform!\n\n" +
                    "Your account has been successfully verified and you can now:\n" +
                    "- Connect with other gamers\n" +
                    "- Participate in tournaments\n" +
                    "- Buy and sell gaming items\n" +
                    "- Share your gaming experiences\n\n" +
                    "Get started by completing your profile and exploring the community.\n\n" +
                    "Happy gaming!\n" +
                    "GamerMajilis Team";

            message.setText(emailBody);

            mailSender.send(message);
            logger.info("Welcome email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send welcome email to: {}", toEmail, e);
            // Don't throw exception for welcome email failure
        }
    }
}