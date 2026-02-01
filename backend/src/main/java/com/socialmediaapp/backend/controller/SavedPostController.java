package com.socialmediaapp.backend.controller;

import com.socialmediaapp.backend.model.Post;
import com.socialmediaapp.backend.model.User;
import com.socialmediaapp.backend.repository.PostRepository;
import com.socialmediaapp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para manejar posts guardados.
 */
@RestController
@RequestMapping("/api/saved")
public class SavedPostController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /**
     * Guarda un post.
     */
    @PostMapping("/post/{postId}")
    public ResponseEntity<Map<String, Object>> savePost(@PathVariable Long postId) {
        User user = getAuthenticatedUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post no encontrado"));

        user.getSavedPosts().add(post);
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Post guardado exitosamente");
        response.put("postId", postId);
        response.put("saved", true);

        return ResponseEntity.ok(response);
    }

    /**
     * Quita un post de guardados.
     */
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Map<String, Object>> unsavePost(@PathVariable Long postId) {
        User user = getAuthenticatedUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post no encontrado"));

        user.getSavedPosts().remove(post);
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Post removido de guardados");
        response.put("postId", postId);
        response.put("saved", false);

        return ResponseEntity.ok(response);
    }

    /**
     * Verifica si un post está guardado.
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<Map<String, Object>> isPostSaved(@PathVariable Long postId) {
        User user = getAuthenticatedUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post no encontrado"));

        boolean isSaved = user.getSavedPosts().contains(post);

        Map<String, Object> response = new HashMap<>();
        response.put("postId", postId);
        response.put("saved", isSaved);

        return ResponseEntity.ok(response);
    }
}
