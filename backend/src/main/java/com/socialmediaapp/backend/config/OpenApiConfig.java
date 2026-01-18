package com.socialmediaapp.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI (Swagger) para documentación automática de la API.
 *
 * La documentación estará disponible en:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        // Nombre del esquema de seguridad
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // Información general de la API
                .info(new Info()
                        .title("Social Media API")
                        .version("2.0.0")
                        .description(
                            "API REST completa para una aplicación de red social tipo Instagram. " +
                            "Incluye autenticación JWT, gestión de usuarios, posts, comentarios, likes, " +
                            "mensajería en tiempo real, stories temporales, reels y notificaciones.\n\n" +
                            "**Características principales:**\n" +
                            "- Autenticación y autorización con JWT\n" +
                            "- CRUD completo de usuarios, posts, comentarios\n" +
                            "- Sistema de likes y guardados\n" +
                            "- Chat en tiempo real entre usuarios\n" +
                            "- Stories temporales (24h)\n" +
                            "- Reels (videos cortos)\n" +
                            "- Sistema de notificaciones\n" +
                            "- Seguimiento entre usuarios\n" +
                            "- Integración con Cloudinary para multimedia\n" +
                            "- Rate limiting para seguridad"
                        )
                        .contact(new Contact()
                                .name("Social Media Team")
                                .email("contact@socialmediaapp.com")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                )
                // Servidores
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor de desarrollo local"),
                        new Server()
                                .url("https://api.socialmediaapp.com")
                                .description("Servidor de producción")
                ))
                // Configuración de seguridad JWT
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description(
                                    "Ingresa el token JWT obtenido del endpoint de login o registro.\n\n" +
                                    "Formato: `Bearer {token}`\n\n" +
                                    "Ejemplo: `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`"
                                )
                        )
                );
    }
}
