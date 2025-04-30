package com.socialmediaapp.backend.dto;

import lombok.Data;

/**
 * DTO para la entidad Comment.
 */
@Data
public class CommentDto {
    private Long id;
    private String content;
    private Long postId;
    private Long userId;
}
