package com.socialmediaapp.backend.mapper;

import com.socialmediaapp.backend.dto.response.PostDto;
import com.socialmediaapp.backend.model.Post;
import org.springframework.stereotype.Component;

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
        dto.setCreatedAt(post.getCreatedAt());

        if (post.getUser() != null) {
            dto.setUserId(post.getUser().getId());
        }

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
