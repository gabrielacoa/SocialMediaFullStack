package com.socialmediaapp.backend.mapper;

import com.socialmediaapp.backend.dto.response.NotificationDto;
import com.socialmediaapp.backend.model.Notification;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Notification entity y NotificationDto.
 */
@Component
public class NotificationMapper {

    /**
     * Convierte Notification entity a NotificationDto.
     */
    public NotificationDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setType(notification.getType() != null ? notification.getType().name() : null);
        dto.setMessage(notification.getMessage());
        dto.setLink(notification.getLink());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());

        if (notification.getReceiver() != null) {
            dto.setReceiverId(notification.getReceiver().getId());
            dto.setReceiverUsername(notification.getReceiver().getUsername());
        }

        if (notification.getSender() != null) {
            dto.setSenderId(notification.getSender().getId());
            dto.setSenderUsername(notification.getSender().getUsername());
            // Asumiendo que User tiene profilePicture, si no existe, comentar esta línea
            // dto.setSenderProfilePicture(notification.getSender().getProfilePicture());
        }

        return dto;
    }
}
