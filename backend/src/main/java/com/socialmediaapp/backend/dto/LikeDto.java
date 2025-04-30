package com.socialmediaapp.backend.dto;

import lombok.Data;

/**
 * DTO para la entidad Like.
 */
@Data
public class LikeDto {
    private Long id;
    private Long postId;
    private Long userId;
}
