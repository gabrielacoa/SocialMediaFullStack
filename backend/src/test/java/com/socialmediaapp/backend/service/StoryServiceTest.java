package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.request.story.CreateStoryRequest;
import com.socialmediaapp.backend.dto.response.StoryDto;
import com.socialmediaapp.backend.exception.custom.ResourceNotFoundException;
import com.socialmediaapp.backend.mapper.StoryMapper;
import com.socialmediaapp.backend.model.Story;
import com.socialmediaapp.backend.model.User;
import com.socialmediaapp.backend.repository.StoryRepository;
import com.socialmediaapp.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para StoryService.
 * Valida la lógica de negocio para stories temporales (24h).
 */
@DisplayName("StoryService Tests")
class StoryServiceTest {

    @Mock
    private StoryRepository storyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoryMapper storyMapper;

    @InjectMocks
    private StoryServiceImpl storyService;

    private User testUser;
    private Story testStory;
    private StoryDto testStoryDto;
    private CreateStoryRequest createRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.HOUR, 24);
        Date expiresAt = cal.getTime();

        testStory = new Story();
        testStory.setId(1L);
        testStory.setUser(testUser);
        testStory.setMediaUrl("https://cloudinary.com/image123.jpg");
        testStory.setCaption("My story!");
        testStory.setCreatedAt(now);
        testStory.setExpiresAt(expiresAt);

        testStoryDto = new StoryDto();
        testStoryDto.setId(1L);
        testStoryDto.setMediaUrl("https://cloudinary.com/image123.jpg");
        testStoryDto.setCaption("My story!");

        createRequest = new CreateStoryRequest();
        createRequest.setMediaUrl("https://cloudinary.com/image123.jpg");
        createRequest.setCaption("My story!");
    }

    @Test
    @DisplayName("Should create story successfully with 24h expiration")
    void testCreateStory_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(storyRepository.save(any(Story.class))).thenReturn(testStory);
        when(storyMapper.toDto(testStory)).thenReturn(testStoryDto);

        // When
        StoryDto result = storyService.createStory(createRequest, 1L);

        // Then
        assertNotNull(result);
        assertEquals("https://cloudinary.com/image123.jpg", result.getMediaUrl());
        verify(storyRepository, times(1)).save(any(Story.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testCreateStory_UserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            storyService.createStory(createRequest, 999L);
        });
        verify(storyRepository, never()).save(any(Story.class));
    }

    @Test
    @DisplayName("Should get active stories only")
    void testGetActiveStories_Success() {
        // Given
        Date now = new Date();
        when(storyRepository.findByExpiresAtAfterOrderByCreatedAtDesc(any(Date.class)))
            .thenReturn(Arrays.asList(testStory));
        when(storyMapper.toDto(testStory)).thenReturn(testStoryDto);

        // When
        List<StoryDto> result = storyService.getActiveStories();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(storyRepository, times(1)).findByExpiresAtAfterOrderByCreatedAtDesc(any(Date.class));
    }

    @Test
    @DisplayName("Should get active stories by user id")
    void testGetActiveStoriesByUserId_Success() {
        // Given
        when(storyRepository.findByUserIdAndExpiresAtAfterOrderByCreatedAtDesc(eq(1L), any(Date.class)))
            .thenReturn(Arrays.asList(testStory));
        when(storyMapper.toDto(testStory)).thenReturn(testStoryDto);

        // When
        List<StoryDto> result = storyService.getActiveStoriesByUserId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(storyRepository, times(1)).findByUserIdAndExpiresAtAfterOrderByCreatedAtDesc(eq(1L), any(Date.class));
    }

    @Test
    @DisplayName("Should delete story successfully when user owns it")
    void testDeleteStory_Success() {
        // Given
        when(storyRepository.findById(1L)).thenReturn(Optional.of(testStory));
        doNothing().when(storyRepository).delete(testStory);

        // When
        storyService.deleteStory(1L, 1L);

        // Then
        verify(storyRepository, times(1)).findById(1L);
        verify(storyRepository, times(1)).delete(testStory);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent story")
    void testDeleteStory_NotFound() {
        // Given
        when(storyRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            storyService.deleteStory(999L, 1L);
        });
        verify(storyRepository, never()).delete(any(Story.class));
    }

    @Test
    @DisplayName("Should return empty list when no active stories exist")
    void testGetActiveStories_EmptyList() {
        // Given
        when(storyRepository.findByExpiresAtAfterOrderByCreatedAtDesc(any(Date.class)))
            .thenReturn(Arrays.asList());

        // When
        List<StoryDto> result = storyService.getActiveStories();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should exclude expired stories")
    void testGetActiveStories_ExcludesExpired() {
        // Given
        Story expiredStory = new Story();
        expiredStory.setId(2L);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -25); // 25 hours ago (expired)
        expiredStory.setCreatedAt(cal.getTime());
        expiredStory.setExpiresAt(new Date(cal.getTimeInMillis() + 24 * 60 * 60 * 1000));

        // Active story only
        when(storyRepository.findByExpiresAtAfterOrderByCreatedAtDesc(any(Date.class)))
            .thenReturn(Arrays.asList(testStory)); // Solo story activa
        when(storyMapper.toDto(testStory)).thenReturn(testStoryDto);

        // When
        List<StoryDto> result = storyService.getActiveStories();

        // Then
        assertEquals(1, result.size());
        assertFalse(result.stream().anyMatch(s -> s.getId().equals(2L)));
    }
}
