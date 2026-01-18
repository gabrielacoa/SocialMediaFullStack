package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.response.NotificationDto;

import java.util.List;

/**
 * Servicio para gestionar notificaciones.
 */
public interface NotificationService {

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
