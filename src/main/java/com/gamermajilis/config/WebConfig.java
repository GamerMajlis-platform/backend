package com.gamermajilis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    // Removed addResourceHandlers to avoid conflicting with StaticFileController
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Enable CORS for React frontend
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:3001") // React dev servers
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
        
        logger.info("✅ CORS configured for React frontend");
    }
}
