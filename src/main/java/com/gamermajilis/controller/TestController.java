package com.gamermajilis.controller;

import com.gamermajilis.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-email")
    public ResponseEntity<Map<String, Object>> testEmail(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();

        try {
            emailService.sendVerificationEmail(email, "test-token-123");
            response.put("success", true);
            response.put("message", "Test email sent successfully");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send email: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}