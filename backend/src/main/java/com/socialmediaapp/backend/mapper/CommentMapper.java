package com.socialmediaapp.backend.mapper;

import com.socialmediaapp.backend.dto.response.CommentDto;
import com.socialmediaapp.backend.model.Comment;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Comment entity y CommentDto.
 */
@Component
public class CommentMapper {

    /**
     * Convierte Comment entity a CommentDto.
     */
    public CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());

        if (comment.getPost() != null) {
            dto.setPostId(comment.getPost().getId());
        }

        if (comment.getUser() != null) {
            dto.setUserId(comment.getUser().getId());
        }

        return dto;
    }

    /**
     * Actualiza un Comment entity existente con datos del CommentDto.
     */
    public void updateEntityFromDto(CommentDto dto, Comment comment) {
        if (dto == null || comment == null) {
            return;
        }

        if (dto.getContent() != null) {
            comment.setContent(dto.getContent());
        }
    }
}
