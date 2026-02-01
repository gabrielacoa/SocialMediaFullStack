package com.socialmediaapp.backend.mapper;

import com.socialmediaapp.backend.dto.response.PostDto;
import com.socialmediaapp.backend.model.Comment;
import com.socialmediaapp.backend.model.Post;
import com.socialmediaapp.backend.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre Post entity y PostDto.
 */
@Component
public class PostMapper {

    /**
     * Convierte Post entity a PostDto.
     */
    public PostDto toDto(Post post) {
        if (post == null) {
            return null;
        }

        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setImageUrl(post.getImageUrl());
        dto.setCreatedAt(post.getCreatedAt());

        // Mapear información del usuario
        if (post.getUser() != null) {
            dto.setUserId(post.getUser().getId());
            dto.setUser(toUserSummary(post.getUser()));
        }

        // Mapear estadísticas
        dto.setLikesCount(post.getLikes() != null ? post.getLikes().size() : 0);
        dto.setCommentsCount(post.getComments() != null ? post.getComments().size() : 0);

        // Mapear comentarios (máximo 3 para preview)
        if (post.getComments() != null && !post.getComments().isEmpty()) {
            List<PostDto.CommentDto> commentDtos = post.getComments().stream()
                    .limit(3)
                    .map(this::toCommentDto)
                    .collect(Collectors.toList());
            dto.setComments(commentDtos);
        } else {
            dto.setComments(new ArrayList<>());
        }

        // Por defecto, liked y saved son false (se actualizan según el usuario actual)
        dto.setLiked(false);
        dto.setSaved(false);

        return dto;
    }

    /**
     * Convierte User a UserSummary.
     */
    private PostDto.UserSummary toUserSummary(User user) {
        if (user == null) {
            return null;
        }

        PostDto.UserSummary summary = new PostDto.UserSummary();
        summary.setId(user.getId());
        summary.setUsername(user.getUsername());
        summary.setAvatar(user.getProfilePictureUrl() != null ?
                user.getProfilePictureUrl() : user.getProfilePicture());
        summary.setName(user.getUsername()); // Usar username como name por ahora
        return summary;
    }

    /**
     * Convierte Comment a CommentDto.
     */
    private PostDto.CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        PostDto.CommentDto dto = new PostDto.CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUser(toUserSummary(comment.getUser()));
        return dto;
    }

    /**
     * Actualiza un Post entity existente con datos del PostDto.
     */
    public void updateEntityFromDto(PostDto dto, Post post) {
        if (dto == null || post == null) {
            return;
        }

        if (dto.getContent() != null) {
            post.setContent(dto.getContent());
        }
    }
}
