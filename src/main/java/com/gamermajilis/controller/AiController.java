package com.gamermajilis.controller;

import com.gamermajilis.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/message")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> body) {
        String prompt = body == null ? null : String.valueOf(body.getOrDefault("message", "")).trim();
        Map<String, Object> res = new HashMap<>();
        if (prompt == null || prompt.isEmpty()) {
            res.put("error", "message is required");
            return ResponseEntity.badRequest().body(res);
        }
        String id = aiService.submitPrompt(prompt);
        res.put("id", id);
        return ResponseEntity.accepted().body(res);
    }

    @GetMapping("/answer/{id}")
    public ResponseEntity<Map<String, Object>> getAnswer(@PathVariable String id) {
        Map<String, Object> res = new HashMap<>();
        String json = aiService.getResult(id);
        if (json == null) {
            res.put("status", "pending");
            return ResponseEntity.ok(res);
        }
        res.put("status", "done");
        res.put("result", json);
        return ResponseEntity.ok(res);
    }
}


