package com.socialmediaapp.backend.dto.response;

import lombok.Data;
import java.util.Date;

/**
 * DTO para la entidad Reel.
 */
@Data
public class ReelDto {
    private Long id;
    private String videoUrl;
    private String caption;
    private Date createdAt;
    private Long userId;
    private String username;
}
