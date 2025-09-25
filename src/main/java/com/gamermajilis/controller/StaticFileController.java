package com.gamermajilis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class StaticFileController {

    private static final Logger logger = LoggerFactory.getLogger(StaticFileController.class);
    private static final String UPLOAD_BASE_PATH = "/tmp/uploads";

    @GetMapping("/uploads/test")
    public ResponseEntity<String> testEndpoint() {
        logger.info("🧪 StaticFileController test endpoint invoked!");
        return ResponseEntity.ok("StaticFileController is working! ✅");
    }

    @GetMapping("/uploads/**")
    public ResponseEntity<Resource> serveFile(HttpServletRequest request) {
        
        logger.info("🔍 StaticFileController invoked for: {}", request.getRequestURI());
        
        try {
            // Extract the file path from the request URI
            String requestPath = request.getRequestURI();
            String contextPath = request.getContextPath(); // e.g. "/api"
            String prefix = (contextPath == null ? "" : contextPath) + "/uploads/";
            if (!requestPath.startsWith(prefix)) {
                logger.warn("Request path does not start with expected prefix. contextPath={}, requestPath={}, prefix={}", contextPath, requestPath, prefix);
                return ResponseEntity.notFound().build();
            }
            String filePath = requestPath.substring(prefix.length());
            
            // Construct the full file path
            Path fullPath = Paths.get(UPLOAD_BASE_PATH, filePath);
            File file = fullPath.toFile();

            logger.info("🔍 Serving static file request:");
            logger.info("   Request URI: {}", requestPath);
            logger.info("   File path: {}", filePath);
            logger.info("   Full path: {}", fullPath.toString());
            logger.info("   File exists: {}", file.exists());
            logger.info("   File readable: {}", file.canRead());

            if (!file.exists() || !file.canRead()) {
                logger.warn("❌ File not found or not readable: {}", fullPath);
                return ResponseEntity.notFound().build();
            }

            // Security check - prevent directory traversal
            String canonicalPath = file.getCanonicalPath();
            if (!canonicalPath.startsWith(new File(UPLOAD_BASE_PATH).getCanonicalPath())) {
                logger.warn("🚫 Security violation - directory traversal attempt: {}", canonicalPath);
                return ResponseEntity.notFound().build();
            }

            // Create resource
            Resource resource = new FileSystemResource(file);
            
            // Determine content type
            String contentType = Files.probeContentType(fullPath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            logger.info("✅ Serving file: {} ({})", file.getName(), contentType);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                    .body(resource);

        } catch (IOException e) {
            logger.error("💥 Error serving file: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
