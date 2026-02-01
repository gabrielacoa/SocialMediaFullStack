package com.socialmediaapp.backend.controller;

import com.socialmediaapp.backend.dto.request.post.CreatePostRequest;
import com.socialmediaapp.backend.dto.request.post.UpdatePostRequest;
import com.socialmediaapp.backend.dto.response.PostDto;
import com.socialmediaapp.backend.model.User;
import com.socialmediaapp.backend.repository.UserRepository;
import com.socialmediaapp.backend.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para manejar las solicitudes relacionadas con las publicaciones.
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Obtiene el ID del usuario autenticado desde el contexto de seguridad.
     */
    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }

    /**
     * Crea un nuevo post.
     */
    @PostMapping
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody CreatePostRequest request) {
        Long userId = getAuthenticatedUserId();
        PostDto post = postService.createPost(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    /**
     * Obtiene un post por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        PostDto post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    /**
     * Obtiene todos los posts.
     */
    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        List<PostDto> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    /**
     * Obtiene todos los posts de un usuario.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDto>> getPostsByUserId(@PathVariable Long userId) {
        List<PostDto> posts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    /**
     * Actualiza un post.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePostRequest request) {
        Long userId = getAuthenticatedUserId();
        PostDto post = postService.updatePost(id, request, userId);
        return ResponseEntity.ok(post);
    }

    /**
     * Elimina un post.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        Long userId = getAuthenticatedUserId();
        postService.deletePost(id, userId);
        return ResponseEntity.noContent().build();
    }
}
