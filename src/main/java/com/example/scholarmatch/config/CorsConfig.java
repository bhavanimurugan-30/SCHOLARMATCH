package com.example.scholarmatch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS configuration so the ScholarMatch frontend (served from a
 * different origin - e.g. a static file server on :5500 or file://) can
 * call the Spring Boot API on :8080.
 *
 * Added for frontend/backend integration - was previously only present
 * on ScholarshipMatchScoreController (@CrossOrigin), which meant every
 * other controller rejected cross-origin requests from the browser.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
