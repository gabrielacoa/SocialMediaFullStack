package com.socialmediaapp.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración web para CORS y otras configuraciones MVC.
 */
@Configuration
public class WebConfig {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Divide los orígenes permitidos por coma para soportar múltiples orígenes
                String[] origins = allowedOrigins.split(",");

                registry.addMapping("/**")
                        .allowedOrigins(origins)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        // SEGURIDAD: Headers explícitos en lugar de "*"
                        .allowedHeaders(
                            "Authorization",
                            "Content-Type",
                            "Accept",
                            "Origin",
                            "X-Requested-With",
                            "X-CSRF-Token",
                            "X-XSRF-TOKEN",
                            "userId"
                        )
                        .exposedHeaders(
                            "X-Rate-Limit-Remaining",
                            "X-Rate-Limit-Retry-After-Seconds"
                        )
                        .allowCredentials(true)
                        .maxAge(3600); // Cache preflight request por 1 hora
            }
        };
    }
}
