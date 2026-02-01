package com.socialmediaapp.backend.controller;

import com.socialmediaapp.backend.dto.request.comment.CreateCommentRequest;
import com.socialmediaapp.backend.dto.response.CommentDto;
import com.socialmediaapp.backend.model.User;
import com.socialmediaapp.backend.repository.UserRepository;
import com.socialmediaapp.backend.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para manejar las solicitudes relacionadas con los comentarios.
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }

    /**
     * Crea un nuevo comentario.
     */
    @PostMapping
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CreateCommentRequest request) {
        Long userId = getAuthenticatedUserId();
        CommentDto comment = commentService.createComment(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    /**
     * Obtiene un comentario por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id) {
        CommentDto comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    /**
     * Obtiene todos los comentarios de un post.
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable Long postId) {
        List<CommentDto> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Elimina un comentario.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        Long userId = getAuthenticatedUserId();
        commentService.deleteComment(id, userId);
        return ResponseEntity.noContent().build();
    }
}
