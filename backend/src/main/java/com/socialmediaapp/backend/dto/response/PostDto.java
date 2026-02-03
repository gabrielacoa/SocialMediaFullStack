package com.socialmediaapp.backend.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * DTO para la entidad Post.
 */
@Data
public class PostDto {
    private Long id;
    private String content;
    private String imageUrl;
    private String image;  // Alias para frontend
    private String videoUrl;
    private String location;
    private Date createdAt;
    private Long userId;

    // Información del usuario
    private UserSummary user;

    // Estadísticas e interacciones
    private int likesCount;
    private int commentsCount;
    private boolean liked;
    private boolean saved;
    private List<CommentDto> comments;

    @Data
    public static class UserSummary {
        private Long id;
        private String username;
        private String avatar;
        private String name;
    }

    @Data
    public static class CommentDto {
        private Long id;
        private String content;
        private Date createdAt;
        private UserSummary user;
    }
}
