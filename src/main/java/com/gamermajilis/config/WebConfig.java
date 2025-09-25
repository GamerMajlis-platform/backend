package com.gamermajilis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        logger.info("🔧 Configuring static file serving for uploads...");
        
        // Serve uploaded files from /tmp/uploads/ directory
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/tmp/uploads/")
                .setCachePeriod(3600) // Cache for 1 hour
                .resourceChain(true);
        
        logger.info("✅ Static file serving configured:");
        logger.info("   URL Pattern: /uploads/**");
        logger.info("   Physical Location: file:/tmp/uploads/");
        logger.info("   Cache Period: 3600 seconds");
    }
    
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
