package com.gamermajilis.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AiService {

    private final Map<String, String> idToResultJson = new ConcurrentHashMap<>();

    public String submitPrompt(String prompt) {
        String id = UUID.randomUUID().toString();
        CompletableFuture.runAsync(() -> runPython(id, prompt));
        return id;
    }

    public String getResult(String id) {
        return idToResultJson.get(id);
    }

    private void runPython(String id, String prompt) {
        ProcessBuilder pb = new ProcessBuilder("/app/venv/bin/python", "/app/AI/cli.py");
        pb.redirectErrorStream(true);
        try {
            Process process = pb.start();
            process.getOutputStream().write(prompt.getBytes(StandardCharsets.UTF_8));
            process.getOutputStream().flush();
            process.getOutputStream().close();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
            }
            process.waitFor();
            idToResultJson.put(id, output.toString());
        } catch (IOException | InterruptedException e) {
            idToResultJson.put(id, "{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}


