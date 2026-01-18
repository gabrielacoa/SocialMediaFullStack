package com.socialmediaapp.backend.controller;

import com.socialmediaapp.backend.dto.response.NotificationDto;
import com.socialmediaapp.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para las operaciones relacionadas con notificaciones.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Obtiene todas las notificaciones de un usuario.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDto>> getNotificationsByUserId(@PathVariable Long userId) {
        List<NotificationDto> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Cuenta las notificaciones no leídas de un usuario.
     */
    @GetMapping("/unread/count/{userId}")
    public ResponseEntity<Long> getUnreadNotificationsCount(@PathVariable Long userId) {
        Long count = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * Marca una notificación como leída.
     */
    @PutMapping("/read/{id}")
    public ResponseEntity<NotificationDto> markNotificationAsRead(@PathVariable Long id) {
        NotificationDto notification = notificationService.markNotificationAsRead(id);
        return ResponseEntity.ok(notification);
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas.
     */
    @PutMapping("/read/all/{userId}")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable Long userId) {
        notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina una notificación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
