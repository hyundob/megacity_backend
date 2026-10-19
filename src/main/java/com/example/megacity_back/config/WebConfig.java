package com.example.megacity_back.config;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig {
    @org.springframework.beans.factory.annotation.Value("${megacity.cors-origins:http://localhost:3000,http://localhost:5173}")
    private String[] origins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/hydrogen/**")
                        .allowedOrigins(origins)
                        .allowedMethods("GET", "POST")
                        .allowedHeaders("Content-Type", "X-Hydrogen-Command-Key");
                registry.addMapping("/**")
                        .allowedOrigins(origins)
                        .allowedMethods("GET");

            }
        };
    }
}
