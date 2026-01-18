package com.socialmediaapp.backend.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO para la entidad Notification.
 */
@Data
public class NotificationDto {
    private Long id;
    private String type;
    private String message;
    private String link;
    private boolean isRead;
    private LocalDateTime createdAt;
    private Long receiverId;
    private String receiverUsername;
    private Long senderId;
    private String senderUsername;
    private String senderProfilePicture;
}
