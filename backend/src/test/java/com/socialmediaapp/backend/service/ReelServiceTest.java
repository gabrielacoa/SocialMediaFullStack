package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.request.reel.CreateReelRequest;
import com.socialmediaapp.backend.dto.response.ReelDto;
import com.socialmediaapp.backend.exception.custom.ResourceNotFoundException;
import com.socialmediaapp.backend.mapper.ReelMapper;
import com.socialmediaapp.backend.model.Reel;
import com.socialmediaapp.backend.model.User;
import com.socialmediaapp.backend.repository.ReelRepository;
import com.socialmediaapp.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ReelService.
 * Valida la lógica de negocio para la creación, consulta y eliminación de reels.
 */
@DisplayName("ReelService Tests")
class ReelServiceTest {

    @Mock
    private ReelRepository reelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReelMapper reelMapper;

    @InjectMocks
    private ReelServiceImpl reelService;

    private User testUser;
    private Reel testReel;
    private ReelDto testReelDto;
    private CreateReelRequest createRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testReel = new Reel();
        testReel.setId(1L);
        testReel.setUser(testUser);
        testReel.setVideoUrl("https://cloudinary.com/video123.mp4");
        testReel.setCaption("Amazing reel!");
        testReel.setCreatedAt(new Date());

        testReelDto = new ReelDto();
        testReelDto.setId(1L);
        testReelDto.setVideoUrl("https://cloudinary.com/video123.mp4");
        testReelDto.setCaption("Amazing reel!");

        createRequest = new CreateReelRequest();
        createRequest.setVideoUrl("https://cloudinary.com/video123.mp4");
        createRequest.setCaption("Amazing reel!");
    }

    @Test
    @DisplayName("Should create reel successfully")
    void testCreateReel_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reelRepository.save(any(Reel.class))).thenReturn(testReel);
        when(reelMapper.toDto(testReel)).thenReturn(testReelDto);

        // When
        ReelDto result = reelService.createReel(createRequest, 1L);

        // Then
        assertNotNull(result);
        assertEquals("https://cloudinary.com/video123.mp4", result.getVideoUrl());
        assertEquals("Amazing reel!", result.getCaption());
        verify(userRepository, times(1)).findById(1L);
        verify(reelRepository, times(1)).save(any(Reel.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found during creation")
    void testCreateReel_UserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            reelService.createReel(createRequest, 999L);
        });
        verify(reelRepository, never()).save(any(Reel.class));
    }

    @Test
    @DisplayName("Should get reel by id successfully")
    void testGetReelById_Success() {
        // Given
        when(reelRepository.findById(1L)).thenReturn(Optional.of(testReel));
        when(reelMapper.toDto(testReel)).thenReturn(testReelDto);

        // When
        ReelDto result = reelService.getReelById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(reelRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when reel not found")
    void testGetReelById_NotFound() {
        // Given
        when(reelRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            reelService.getReelById(999L);
        });
    }

    @Test
    @DisplayName("Should get all reels by user id")
    void testGetReelsByUserId_Success() {
        // Given
        Reel reel2 = new Reel();
        reel2.setId(2L);
        reel2.setUser(testUser);

        ReelDto reelDto2 = new ReelDto();
        reelDto2.setId(2L);

        List<Reel> reels = Arrays.asList(testReel, reel2);

        when(reelRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(reels);
        when(reelMapper.toDto(testReel)).thenReturn(testReelDto);
        when(reelMapper.toDto(reel2)).thenReturn(reelDto2);

        // When
        List<ReelDto> result = reelService.getReelsByUserId(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(reelRepository, times(1)).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    @DisplayName("Should get all reels ordered by creation date")
    void testGetAllReels_Success() {
        // Given
        List<Reel> reels = Arrays.asList(testReel);
        when(reelRepository.findAllByOrderByCreatedAtDesc()).thenReturn(reels);
        when(reelMapper.toDto(testReel)).thenReturn(testReelDto);

        // When
        List<ReelDto> result = reelService.getAllReels();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reelRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should delete reel successfully when user owns it")
    void testDeleteReel_Success() {
        // Given
        when(reelRepository.findById(1L)).thenReturn(Optional.of(testReel));
        doNothing().when(reelRepository).delete(testReel);

        // When
        reelService.deleteReel(1L, 1L);

        // Then
        verify(reelRepository, times(1)).findById(1L);
        verify(reelRepository, times(1)).delete(testReel);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent reel")
    void testDeleteReel_NotFound() {
        // Given
        when(reelRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            reelService.deleteReel(999L, 1L);
        });
        verify(reelRepository, never()).delete(any(Reel.class));
    }

    @Test
    @DisplayName("Should return empty list when user has no reels")
    void testGetReelsByUserId_EmptyList() {
        // Given
        when(reelRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(Arrays.asList());

        // When
        List<ReelDto> result = reelService.getReelsByUserId(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
