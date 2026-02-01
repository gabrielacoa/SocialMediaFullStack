package com.socialmediaapp.backend.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.socialmediaapp.backend.dto.response.MessageDto;
import com.socialmediaapp.backend.dto.response.NotificationDto;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio que maneja los eventos de Socket.IO para comunicación en tiempo real.
 * Gestiona conexiones, mensajes, notificaciones y estado de usuarios.
 */
@Service
public class SocketIOService {

    private static final Logger logger = LoggerFactory.getLogger(SocketIOService.class);

    private final SocketIOServer server;

    // Mapeo de userId -> sessionId para rastrear usuarios conectados
    private final Map<Long, UUID> userSessions = new ConcurrentHashMap<>();

    // Mapeo de sessionId -> userId para búsqueda inversa
    private final Map<UUID, Long> sessionUsers = new ConcurrentHashMap<>();

    @Autowired
    public SocketIOService(SocketIOServer server) {
        this.server = server;
    }

    @PostConstruct
    public void init() {
        // Listener de conexión
        server.addConnectListener(onConnected());

        // Listener de desconexión
        server.addDisconnectListener(onDisconnected());

        // Eventos de mensajería
        server.addEventListener("send_message", SocketMessage.class, onMessageReceived());
        server.addEventListener("join_chat", Long.class, onJoinChat());
        server.addEventListener("leave_chat", Long.class, onLeaveChat());

        // Eventos de typing
        server.addEventListener("user_typing", TypingEvent.class, onUserTyping());

        // Iniciar servidor
        server.start();
        logger.info("Socket.IO server started on port {}", server.getConfiguration().getPort());
    }

    /**
     * Listener para nuevas conexiones.
     * Extrae el userId del query parameter y registra la sesión.
     */
    private ConnectListener onConnected() {
        return client -> {
            String userIdParam = client.getHandshakeData().getSingleUrlParam("userId");
            if (userIdParam != null) {
                try {
                    Long userId = Long.parseLong(userIdParam);
                    UUID sessionId = client.getSessionId();

                    // Registrar sesión
                    userSessions.put(userId, sessionId);
                    sessionUsers.put(sessionId, userId);

                    // Unir al room del usuario
                    client.joinRoom("user_" + userId);

                    // Notificar a otros usuarios que este usuario está online
                    server.getBroadcastOperations().sendEvent("user_online", userId);

                    logger.info("User {} connected with session {}", userId, sessionId);
                } catch (NumberFormatException e) {
                    logger.error("Invalid userId parameter: {}", userIdParam);
                }
            } else {
                logger.warn("Connection without userId parameter from {}",
                    client.getRemoteAddress());
            }
        };
    }

    /**
     * Listener para desconexiones.
     * Limpia las sesiones y notifica a otros usuarios.
     */
    private DisconnectListener onDisconnected() {
        return client -> {
            UUID sessionId = client.getSessionId();
            Long userId = sessionUsers.remove(sessionId);

            if (userId != null) {
                userSessions.remove(userId);

                // Notificar a otros usuarios que este usuario está offline
                server.getBroadcastOperations().sendEvent("user_offline", userId);

                logger.info("User {} disconnected", userId);
            }
        };
    }

    /**
     * Listener para mensajes enviados desde el cliente.
     */
    private DataListener<SocketMessage> onMessageReceived() {
        return (client, data, ackSender) -> {
            logger.debug("Message received: {} -> {}", data.getSenderId(), data.getReceiverId());

            // Enviar mensaje al destinatario si está conectado
            UUID receiverSession = userSessions.get(data.getReceiverId());
            if (receiverSession != null) {
                SocketIOClient receiverClient = server.getClient(receiverSession);
                if (receiverClient != null) {
                    receiverClient.sendEvent("new_message", data);
                }
            }

            // También enviar al room del chat si existe
            if (data.getChatId() != null) {
                server.getRoomOperations("chat_" + data.getChatId())
                    .sendEvent("new_message", data);
            }
        };
    }

    /**
     * Listener para unirse a un chat.
     */
    private DataListener<Long> onJoinChat() {
        return (client, chatId, ackSender) -> {
            client.joinRoom("chat_" + chatId);
            logger.debug("Client {} joined chat {}", client.getSessionId(), chatId);
        };
    }

    /**
     * Listener para salir de un chat.
     */
    private DataListener<Long> onLeaveChat() {
        return (client, chatId, ackSender) -> {
            client.leaveRoom("chat_" + chatId);
            logger.debug("Client {} left chat {}", client.getSessionId(), chatId);
        };
    }

    /**
     * Listener para eventos de typing.
     */
    private DataListener<TypingEvent> onUserTyping() {
        return (client, data, ackSender) -> {
            // Enviar evento de typing a todos en el chat excepto al remitente
            server.getRoomOperations("chat_" + data.getChatId())
                .sendEvent("user_typing", client, data);
        };
    }

    // ============ Métodos públicos para enviar eventos desde otros servicios ============

    /**
     * Envía un mensaje a un usuario específico.
     * @param userId ID del usuario destinatario
     * @param message DTO del mensaje
     */
    public void sendMessageToUser(Long userId, MessageDto message) {
        UUID sessionId = userSessions.get(userId);
        if (sessionId != null) {
            SocketIOClient client = server.getClient(sessionId);
            if (client != null) {
                client.sendEvent("new_message", message);
                logger.debug("Message sent to user {}", userId);
            }
        }
    }

    /**
     * Envía una notificación a un usuario específico.
     * @param userId ID del usuario destinatario
     * @param notification DTO de la notificación
     */
    public void sendNotificationToUser(Long userId, NotificationDto notification) {
        UUID sessionId = userSessions.get(userId);
        if (sessionId != null) {
            SocketIOClient client = server.getClient(sessionId);
            if (client != null) {
                client.sendEvent("notification", notification);
                logger.debug("Notification sent to user {}", userId);
            }
        }
    }

    /**
     * Envía un mensaje a un chat específico.
     * @param chatId ID del chat
     * @param message DTO del mensaje
     */
    public void sendMessageToChat(Long chatId, MessageDto message) {
        server.getRoomOperations("chat_" + chatId).sendEvent("new_message", message);
        logger.debug("Message broadcast to chat {}", chatId);
    }

    /**
     * Verifica si un usuario está conectado.
     * @param userId ID del usuario
     * @return true si el usuario está online
     */
    public boolean isUserOnline(Long userId) {
        return userSessions.containsKey(userId);
    }

    /**
     * Obtiene el número de usuarios conectados.
     * @return número de usuarios online
     */
    public int getOnlineUsersCount() {
        return userSessions.size();
    }

    // ============ Clases internas para los datos de eventos ============

    /**
     * DTO para mensajes de socket.
     */
    public static class SocketMessage {
        private Long senderId;
        private Long receiverId;
        private Long chatId;
        private String content;
        private String senderUsername;

        // Getters y Setters
        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        public Long getReceiverId() { return receiverId; }
        public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
        public Long getChatId() { return chatId; }
        public void setChatId(Long chatId) { this.chatId = chatId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getSenderUsername() { return senderUsername; }
        public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
    }

    /**
     * DTO para eventos de typing.
     */
    public static class TypingEvent {
        private Long chatId;
        private boolean isTyping;
        private String username;

        // Getters y Setters
        public Long getChatId() { return chatId; }
        public void setChatId(Long chatId) { this.chatId = chatId; }
        public boolean isTyping() { return isTyping; }
        public void setTyping(boolean typing) { isTyping = typing; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }
}
