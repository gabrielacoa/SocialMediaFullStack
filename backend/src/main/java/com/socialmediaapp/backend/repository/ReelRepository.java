package com.socialmediaapp.backend.repository;

import com.socialmediaapp.backend.model.Reel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Reel.
 */
@Repository
public interface ReelRepository extends JpaRepository<Reel, Long> {

    /**
     * Encuentra todos los reels de un usuario específico.
     */
    List<Reel> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Encuentra todos los reels ordenados por fecha de creación (más recientes primero).
     */
    List<Reel> findAllByOrderByCreatedAtDesc();
}
