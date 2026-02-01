package com.socialmediaapp.backend.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Configuración del servidor Socket.IO para comunicación en tiempo real.
 * Compatible con socket.io-client del frontend.
 */
@org.springframework.context.annotation.Configuration
public class SocketIOConfig {

    @Value("${socketio.host:0.0.0.0}")
    private String host;

    @Value("${socketio.port:9092}")
    private int port;

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    private SocketIOServer server;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);

        // Configuración de CORS para Socket.IO
        config.setOrigin(allowedOrigins);

        // Configuración de ping/pong para mantener conexión activa
        config.setPingInterval(25000);
        config.setPingTimeout(60000);

        // Permitir upgrade de transporte
        config.setAllowCustomRequests(true);

        server = new SocketIOServer(config);
        return server;
    }

    @PreDestroy
    public void stopSocketIOServer() {
        if (server != null) {
            server.stop();
        }
    }
}
