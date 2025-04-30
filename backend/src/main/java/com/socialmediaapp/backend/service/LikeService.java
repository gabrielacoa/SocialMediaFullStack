package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.model.Like;

import java.util.List;

/**
 * Interfaz para el servicio de la entidad Like.
 */
public interface LikeService {
    Like addLike(Like like);
    List<Like> getLikesByPostId(Long postId);
    List<Like> getLikesByUserId(Long userId);
    void removeLike(Long id);
}
