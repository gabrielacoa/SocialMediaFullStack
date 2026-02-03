package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.request.post.CreatePostRequest;
import com.socialmediaapp.backend.dto.request.post.UpdatePostRequest;
import com.socialmediaapp.backend.dto.response.PostDto;
import com.socialmediaapp.backend.exception.custom.ForbiddenException;
import com.socialmediaapp.backend.exception.custom.ResourceNotFoundException;
import com.socialmediaapp.backend.mapper.PostMapper;
import com.socialmediaapp.backend.model.Post;
import com.socialmediaapp.backend.model.User;
import com.socialmediaapp.backend.repository.PostRepository;
import com.socialmediaapp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para la entidad Post.
 */
@Service
@Transactional
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostMapper postMapper;

    @Override
    public PostDto createPost(CreatePostRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Post post = new Post();
        post.setContent(request.getContent());
        post.setImageUrl(request.getImageUrl());
        post.setCreatedAt(new Date());
        post.setUser(user);

        Post savedPost = postRepository.save(post);
        return postMapper.toDto(savedPost);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto getPostById(Long id) {
        return getPostById(id, null);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto getPostById(Long id, Long currentUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", id));
        PostDto dto = postMapper.toDto(post, currentUserId);

        // Verificar si el post está guardado por el usuario actual
        if (currentUserId != null) {
            User currentUser = userRepository.findById(currentUserId).orElse(null);
            if (currentUser != null && currentUser.getSavedPosts() != null) {
                dto.setSaved(currentUser.getSavedPosts().contains(post));
            }
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getAllPosts() {
        return getAllPosts(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getAllPosts(Long currentUserId) {
        User currentUser = currentUserId != null ? userRepository.findById(currentUserId).orElse(null) : null;

        return postRepository.findAll()
                .stream()
                .map(post -> {
                    PostDto dto = postMapper.toDto(post, currentUserId);
                    // Verificar si está guardado
                    if (currentUser != null && currentUser.getSavedPosts() != null) {
                        dto.setSaved(currentUser.getSavedPosts().contains(post));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getPostsByUserId(Long userId) {
        return getPostsByUserId(userId, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getPostsByUserId(Long userId, Long currentUserId) {
        User currentUser = currentUserId != null ? userRepository.findById(currentUserId).orElse(null) : null;

        return postRepository.findByUserId(userId)
                .stream()
                .map(post -> {
                    PostDto dto = postMapper.toDto(post, currentUserId);
                    // Verificar si está guardado
                    if (currentUser != null && currentUser.getSavedPosts() != null) {
                        dto.setSaved(currentUser.getSavedPosts().contains(post));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public PostDto updatePost(Long id, UpdatePostRequest request, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", id));

        if (!post.getUser().getId().equals(userId)) {
            throw new ForbiddenException("No tienes permiso para editar este post");
        }

        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }

        Post updatedPost = postRepository.save(post);
        return postMapper.toDto(updatedPost);
    }

    @Override
    public void deletePost(Long id, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", id));

        if (!post.getUser().getId().equals(userId)) {
            throw new ForbiddenException("No tienes permiso para eliminar este post");
        }

        postRepository.delete(post);
    }
}
