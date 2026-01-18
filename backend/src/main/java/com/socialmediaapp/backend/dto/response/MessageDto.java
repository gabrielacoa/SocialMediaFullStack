package com.socialmediaapp.backend.dto.response;

import lombok.Data;
import java.util.Date;

/**
 * DTO para la entidad Message.
 */
@Data
public class MessageDto {
    private Long id;
    private String content;
    private Date sentAt;
    private boolean isRead;
    private Long senderId;
    private String senderUsername;
    private Long chatId;
}
