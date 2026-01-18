package com.socialmediaapp.backend.dto.response;

import lombok.Data;
import java.util.Date;

/**
 * DTO para la entidad Story.
 */
@Data
public class StoryDto {
    private Long id;
    private String mediaUrl;
    private String caption;
    private Date createdAt;
    private Date expiresAt;
    private Long userId;
    private String username;
    private boolean active;
}
