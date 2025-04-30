package com.socialmediaapp.backend.dto;

import lombok.Data;

import java.util.Date;

/**
 * DTO para la entidad Post.
 */
@Data
public class PostDto {
    private Long id;
    private String content;
    private Date createdAt;
    private Long userId;
}
