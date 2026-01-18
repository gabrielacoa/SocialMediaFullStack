package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.request.CreateReelRequest;
import com.socialmediaapp.backend.dto.response.ReelDto;

import java.util.List;

/**
 * Servicio para gestionar reels.
 */
public interface ReelService {

    /**
     * Crea un nuevo reel.
     */
    ReelDto createReel(CreateReelRequest request, Long userId);

    /**
     * Obtiene un reel por ID.
     */
    ReelDto getReelById(Long reelId);

    /**
     * Obtiene todos los reels de un usuario.
     */
    List<ReelDto> getReelsByUser(Long userId);

    /**
     * Obtiene todos los reels (feed).
     */
    List<ReelDto> getAllReels();

    /**
     * Elimina un reel.
     */
    void deleteReel(Long reelId, Long userId);
}
