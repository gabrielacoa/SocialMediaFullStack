package com.socialmediaapp.backend.dto.response;

import lombok.Data;
import java.util.Date;

/**
 * DTO para la entidad PostLiked (likes).
 */
@Data
public class PostLikedDto {
    private Long id;
    private Long postId;
    private Long userId;
    private Date likedAt;
}
