package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.request.CreateStoryRequest;
import com.socialmediaapp.backend.dto.response.StoryDto;
import com.socialmediaapp.backend.exception.custom.ForbiddenException;
import com.socialmediaapp.backend.exception.custom.ResourceNotFoundException;
import com.socialmediaapp.backend.model.Story;
import com.socialmediaapp.backend.model.User;
import com.socialmediaapp.backend.repository.StoryRepository;
import com.socialmediaapp.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de stories.
 */
@Service
@Transactional
public class StoryServiceImpl implements StoryService {

    private static final Logger logger = LoggerFactory.getLogger(StoryServiceImpl.class);

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public StoryDto createStory(CreateStoryRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Story story = new Story();
        story.setMediaUrl(request.getMediaUrl());
        story.setCaption(request.getCaption());
        story.setUser(user);

        Story savedStory = storyRepository.save(story);
        return mapToDto(savedStory);
    }

    @Override
    @Transactional(readOnly = true)
    public StoryDto getStoryById(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story", storyId));

        if (!story.isActive()) {
            throw new ResourceNotFoundException("Story expirada o no encontrada");
        }

        return mapToDto(story);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoryDto> getActiveStoriesByUser(Long userId) {
        Date now = new Date();
        return storyRepository.findActiveStoriesByUserId(userId, now)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoryDto> getAllActiveStories() {
        Date now = new Date();
        return storyRepository.findAllActiveStories(now)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteStory(Long storyId, Long userId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story", storyId));

        if (!story.getUser().getId().equals(userId)) {
            throw new ForbiddenException("No tienes permiso para eliminar esta story");
        }

        storyRepository.delete(story);
    }

    @Override
    public void cleanExpiredStories() {
        Date now = new Date();
        List<Story> expiredStories = storyRepository.findExpiredStories(now);

        if (!expiredStories.isEmpty()) {
            storyRepository.deleteAll(expiredStories);
            logger.info("Eliminadas {} stories expiradas", expiredStories.size());
        }
    }

    private StoryDto mapToDto(Story story) {
        StoryDto dto = new StoryDto();
        dto.setId(story.getId());
        dto.setMediaUrl(story.getMediaUrl());
        dto.setCaption(story.getCaption());
        dto.setCreatedAt(story.getCreatedAt());
        dto.setExpiresAt(story.getExpiresAt());
        dto.setActive(story.isActive());

        if (story.getUser() != null) {
            dto.setUserId(story.getUser().getId());
            dto.setUsername(story.getUser().getUsername());
        }

        return dto;
    }
}
