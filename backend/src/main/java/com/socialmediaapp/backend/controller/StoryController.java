package com.socialmediaapp.backend.controller;

import com.socialmediaapp.backend.dto.request.CreateStoryRequest;
import com.socialmediaapp.backend.dto.response.StoryDto;
import com.socialmediaapp.backend.service.StoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar stories.
 */
@RestController
@RequestMapping("/api/stories")
public class StoryController {

    @Autowired
    private StoryService storyService;

    /**
     * Crea una nueva story.
     */
    @PostMapping
    public ResponseEntity<StoryDto> createStory(
            @Valid @RequestBody CreateStoryRequest request,
            @RequestHeader("userId") Long userId) {
        StoryDto story = storyService.createStory(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(story);
    }

    /**
     * Obtiene una story por ID.
     */
    @GetMapping("/{storyId}")
    public ResponseEntity<StoryDto> getStoryById(@PathVariable Long storyId) {
        StoryDto story = storyService.getStoryById(storyId);
        return ResponseEntity.ok(story);
    }

    /**
     * Obtiene todas las stories activas de un usuario.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<StoryDto>> getActiveStoriesByUser(@PathVariable Long userId) {
        List<StoryDto> stories = storyService.getActiveStoriesByUser(userId);
        return ResponseEntity.ok(stories);
    }

    /**
     * Obtiene todas las stories activas (feed).
     */
    @GetMapping
    public ResponseEntity<List<StoryDto>> getAllActiveStories() {
        List<StoryDto> stories = storyService.getAllActiveStories();
        return ResponseEntity.ok(stories);
    }

    /**
     * Elimina una story.
     */
    @DeleteMapping("/{storyId}")
    public ResponseEntity<Void> deleteStory(
            @PathVariable Long storyId,
            @RequestHeader("userId") Long userId) {
        storyService.deleteStory(storyId, userId);
        return ResponseEntity.noContent().build();
    }
}
