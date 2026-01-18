package com.socialmediaapp.backend.repository;

import com.socialmediaapp.backend.model.PostLiked;
import com.socialmediaapp.backend.model.Post;
import com.socialmediaapp.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad PostLiked.
 */
@Repository
public interface PostLikedRepository extends JpaRepository<PostLiked, Long> {

    /**
     * Busca un like específico de un usuario en un post.
     * Útil para verificar si ya existe antes de crear uno nuevo.
     */
    Optional<PostLiked> findByUserAndPost(User user, Post post);

    /**
     * Verifica si un usuario ya dio like a un post.
     */
    boolean existsByUserAndPost(User user, Post post);

    /**
     * Cuenta cuántos likes tiene un post.
     */
    long countByPost(Post post);

    /**
     * Obtiene todos los likes de un post específico.
     */
    java.util.List<PostLiked> findByPostId(Long postId);

    /**
     * Obtiene todos los likes de un usuario específico.
     */
    java.util.List<PostLiked> findByUserId(Long userId);

    /**
     * Busca un like específico por IDs de post y usuario.
     */
    Optional<PostLiked> findByPostIdAndUserId(Long postId, Long userId);
}
