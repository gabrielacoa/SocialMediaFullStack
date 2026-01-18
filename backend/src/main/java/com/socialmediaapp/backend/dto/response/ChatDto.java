package com.socialmediaapp.backend.dto.response;

import lombok.Data;
import java.util.Date;

/**
 * DTO para la entidad Chat.
 */
@Data
public class ChatDto {
    private Long id;
    private UserDto user1;
    private UserDto user2;
    private MessageDto lastMessage;
    private Date createdAt;
    private Date lastMessageAt;
    private int unreadCount;
}
