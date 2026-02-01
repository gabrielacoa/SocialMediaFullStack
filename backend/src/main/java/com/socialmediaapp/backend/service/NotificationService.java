package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.response.NotificationDto;

import java.util.List;

/**
 * Servicio para gestionar notificaciones.
 */
public interface NotificationService {

    /**
     * Crea una notificación y la envía en tiempo real via Socket.IO.
     * @param type Tipo de notificación (LIKE, COMMENT, FOLLOW, etc.)
     * @param message Mensaje de la notificación
     * @param link URL relacionada (opcional)
     * @param senderId ID del usuario que genera la notificación
     * @param receiverId ID del usuario que recibe la notificación
     * @return DTO de la notificación creada
     */
    NotificationDto createNotification(String type, String message, String link, Long senderId, Long receiverId);

    /**
     * Obtiene todas las notificaciones de un usuario.
     */
    List<NotificationDto> getNotificationsByUserId(Long userId);

    /**
     * Cuenta el número de notificaciones no leídas para un usuario.
     */
    Long countUnreadNotifications(Long userId);

    /**
     * Marca una notificación específica como leída.
     */
    NotificationDto markNotificationAsRead(Long notificationId);

    /**
     * Marca todas las notificaciones de un usuario como leídas.
     */
    void markAllNotificationsAsRead(Long userId);

    /**
     * Elimina una notificación.
     */
    void deleteNotification(Long notificationId);
}
