package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.response.PostLikedDto;

import java.util.List;

/**
 * Interfaz para el servicio de la entidad PostLiked (likes).
 */
public interface LikeService {
    PostLikedDto addLike(Long postId, Long userId);
    List<PostLikedDto> getLikesByPostId(Long postId);
    List<PostLikedDto> getLikesByUserId(Long userId);
    void removeLike(Long postId, Long userId);
    boolean hasUserLikedPost(Long postId, Long userId);
}
