package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.request.post.CreatePostRequest;
import com.socialmediaapp.backend.dto.request.post.UpdatePostRequest;
import com.socialmediaapp.backend.dto.response.PostDto;

import java.util.List;

/**
 * Interfaz para el servicio de la entidad Post.
 */
public interface PostService {
    PostDto createPost(CreatePostRequest request, Long userId);
    PostDto getPostById(Long id);
    List<PostDto> getAllPosts();
    List<PostDto> getPostsByUserId(Long userId);
    PostDto updatePost(Long id, UpdatePostRequest request, Long userId);
    void deletePost(Long id, Long userId);
}
