package com.socialmediaapp.backend.controller;

import com.socialmediaapp.backend.model.Like;
import com.socialmediaapp.backend.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para manejar las solicitudes relacionadas con los "me gusta".
 */
@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping
    public ResponseEntity<Like> addLike(@RequestBody Like like) {
        Like addedLike = likeService.addLike(like);
        return ResponseEntity.ok(addedLike);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Like>> getLikesByPostId(@PathVariable Long postId) {
        List<Like> likes = likeService.getLikesByPostId(postId);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Like>> getLikesByUserId(@PathVariable Long userId) {
        List<Like> likes = likeService.getLikesByUserId(userId);
        return ResponseEntity.ok(likes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeLike(@PathVariable Long id) {
        likeService.removeLike(id);
        return ResponseEntity.noContent().build();
    }
}
