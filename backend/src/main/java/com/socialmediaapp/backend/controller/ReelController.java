package com.socialmediaapp.backend.controller;

import com.socialmediaapp.backend.dto.request.CreateReelRequest;
import com.socialmediaapp.backend.dto.response.ReelDto;
import com.socialmediaapp.backend.service.ReelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar reels.
 */
@RestController
@RequestMapping("/api/reels")
public class ReelController {

    @Autowired
    private ReelService reelService;

    /**
     * Crea un nuevo reel.
     */
    @PostMapping
    public ResponseEntity<ReelDto> createReel(
            @Valid @RequestBody CreateReelRequest request,
            @RequestHeader("userId") Long userId) {
        ReelDto reel = reelService.createReel(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reel);
    }

    /**
     * Obtiene un reel por ID.
     */
    @GetMapping("/{reelId}")
    public ResponseEntity<ReelDto> getReelById(@PathVariable Long reelId) {
        ReelDto reel = reelService.getReelById(reelId);
        return ResponseEntity.ok(reel);
    }

    /**
     * Obtiene todos los reels de un usuario.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReelDto>> getReelsByUser(@PathVariable Long userId) {
        List<ReelDto> reels = reelService.getReelsByUser(userId);
        return ResponseEntity.ok(reels);
    }

    /**
     * Obtiene todos los reels (feed).
     */
    @GetMapping
    public ResponseEntity<List<ReelDto>> getAllReels() {
        List<ReelDto> reels = reelService.getAllReels();
        return ResponseEntity.ok(reels);
    }

    /**
     * Elimina un reel.
     */
    @DeleteMapping("/{reelId}")
    public ResponseEntity<Void> deleteReel(
            @PathVariable Long reelId,
            @RequestHeader("userId") Long userId) {
        reelService.deleteReel(reelId, userId);
        return ResponseEntity.noContent().build();
    }
}
