package com.socialmediaapp.backend.controller;

import com.socialmediaapp.backend.dto.request.CreateStoryRequest;
import com.socialmediaapp.backend.dto.response.StoryDto;
import com.socialmediaapp.backend.model.User;
import com.socialmediaapp.backend.repository.UserRepository;
import com.socialmediaapp.backend.service.CloudinaryService;
import com.socialmediaapp.backend.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controlador REST para gestionar stories.
 */
@RestController
@RequestMapping("/api/stories")
public class StoryController {

    @Autowired
    private StoryService storyService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private UserRepository userRepository;

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        return user != null ? user.getId() : null;
    }

    /**
     * Crea una nueva story con imagen subida a Cloudinary.
     */
    @PostMapping
    public ResponseEntity<StoryDto> createStory(
            @RequestParam("media") MultipartFile media,
            @RequestParam(value = "caption", required = false) String caption) {
        Long userId = getAuthenticatedUserId();
        String mediaUrl = cloudinaryService.uploadImage(media, "social-media/stories");
        CreateStoryRequest request = new CreateStoryRequest();
        request.setMediaUrl(mediaUrl);
        request.setCaption(caption);
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
