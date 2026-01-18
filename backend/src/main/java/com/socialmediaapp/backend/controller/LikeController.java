package com.socialmediaapp.backend.controller;

import com.socialmediaapp.backend.dto.response.PostLikedDto;
import com.socialmediaapp.backend.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para manejar las solicitudes relacionadas con los "me gusta" (likes).
 */
@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    /**
     * Agrega un like a un post.
     */
    @PostMapping("/post/{postId}")
    public ResponseEntity<PostLikedDto> addLike(
            @PathVariable Long postId,
            @RequestHeader("userId") Long userId) {
        PostLikedDto like = likeService.addLike(postId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(like);
    }

    /**
     * Obtiene todos los likes de un post.
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<PostLikedDto>> getLikesByPostId(@PathVariable Long postId) {
        List<PostLikedDto> likes = likeService.getLikesByPostId(postId);
        return ResponseEntity.ok(likes);
    }

    /**
     * Obtiene todos los likes de un usuario.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostLikedDto>> getLikesByUserId(@PathVariable Long userId) {
        List<PostLikedDto> likes = likeService.getLikesByUserId(userId);
        return ResponseEntity.ok(likes);
    }

    /**
     * Verifica si un usuario le ha dado like a un post.
     */
    @GetMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<Boolean> hasUserLikedPost(
            @PathVariable Long postId,
            @PathVariable Long userId) {
        boolean hasLiked = likeService.hasUserLikedPost(postId, userId);
        return ResponseEntity.ok(hasLiked);
    }

    /**
     * Elimina un like de un post.
     */
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> removeLike(
            @PathVariable Long postId,
            @RequestHeader("userId") Long userId) {
        likeService.removeLike(postId, userId);
        return ResponseEntity.noContent().build();
    }
}
