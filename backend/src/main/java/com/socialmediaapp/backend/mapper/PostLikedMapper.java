package com.socialmediaapp.backend.mapper;

import com.socialmediaapp.backend.dto.response.PostLikedDto;
import com.socialmediaapp.backend.model.PostLiked;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre PostLiked entity y PostLikedDto.
 */
@Component
public class PostLikedMapper {

    /**
     * Convierte PostLiked entity a PostLikedDto.
     */
    public PostLikedDto toDto(PostLiked postLiked) {
        if (postLiked == null) {
            return null;
        }

        PostLikedDto dto = new PostLikedDto();
        dto.setId(postLiked.getId());
        dto.setLikedAt(postLiked.getLikedAt());

        if (postLiked.getPost() != null) {
            dto.setPostId(postLiked.getPost().getId());
        }

        if (postLiked.getUser() != null) {
            dto.setUserId(postLiked.getUser().getId());
        }

        return dto;
    }
}
