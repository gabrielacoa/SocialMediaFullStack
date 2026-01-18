package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.request.CreateStoryRequest;
import com.socialmediaapp.backend.dto.response.StoryDto;

import java.util.List;

/**
 * Servicio para gestionar stories.
 */
public interface StoryService {

    /**
     * Crea una nueva story.
     */
    StoryDto createStory(CreateStoryRequest request, Long userId);

    /**
     * Obtiene una story por ID si está activa.
     */
    StoryDto getStoryById(Long storyId);

    /**
     * Obtiene todas las stories activas de un usuario.
     */
    List<StoryDto> getActiveStoriesByUser(Long userId);

    /**
     * Obtiene todas las stories activas (feed).
     */
    List<StoryDto> getAllActiveStories();

    /**
     * Elimina una story.
     */
    void deleteStory(Long storyId, Long userId);

    /**
     * Limpia stories expiradas (puede ser llamado por un scheduled job).
     */
    void cleanExpiredStories();
}
