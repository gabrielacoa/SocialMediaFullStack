package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.model.Post;

import java.util.List;

/**
 * Interfaz para el servicio de la entidad Post.
 */
public interface PostService {
    Post createPost(Post post);
    Post getPostById(Long id);
    List<Post> getAllPosts();
    List<Post> getPostsByUserId(Long userId);
    void deletePost(Long id);
}
