package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.request.CreateReelRequest;
import com.socialmediaapp.backend.dto.response.ReelDto;
import com.socialmediaapp.backend.exception.custom.ForbiddenException;
import com.socialmediaapp.backend.exception.custom.ResourceNotFoundException;
import com.socialmediaapp.backend.model.Reel;
import com.socialmediaapp.backend.model.User;
import com.socialmediaapp.backend.repository.ReelRepository;
import com.socialmediaapp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de reels.
 */
@Service
@Transactional
public class ReelServiceImpl implements ReelService {

    @Autowired
    private ReelRepository reelRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ReelDto createReel(CreateReelRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Reel reel = new Reel();
        reel.setVideoUrl(request.getVideoUrl());
        reel.setCaption(request.getCaption());
        reel.setUser(user);

        Reel savedReel = reelRepository.save(reel);
        return mapToDto(savedReel);
    }

    @Override
    @Transactional(readOnly = true)
    public ReelDto getReelById(Long reelId) {
        Reel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new ResourceNotFoundException("Reel", reelId));
        return mapToDto(reel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReelDto> getReelsByUser(Long userId) {
        return reelRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReelDto> getAllReels() {
        return reelRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReel(Long reelId, Long userId) {
        Reel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new ResourceNotFoundException("Reel", reelId));

        if (!reel.getUser().getId().equals(userId)) {
            throw new ForbiddenException("No tienes permiso para eliminar este reel");
        }

        reelRepository.delete(reel);
    }

    private ReelDto mapToDto(Reel reel) {
        ReelDto dto = new ReelDto();
        dto.setId(reel.getId());
        dto.setVideoUrl(reel.getVideoUrl());
        dto.setCaption(reel.getCaption());
        dto.setCreatedAt(reel.getCreatedAt());

        if (reel.getUser() != null) {
            dto.setUserId(reel.getUser().getId());
            dto.setUsername(reel.getUser().getUsername());
        }

        return dto;
    }
}
