package com.socialmediaapp.backend.repository;

import com.socialmediaapp.backend.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repositorio para la entidad Story.
 */
@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {

    /**
     * Encuentra todas las stories activas (no expiradas) de un usuario.
     */
    @Query("SELECT s FROM Story s WHERE s.user.id = :userId AND s.expiresAt > :now ORDER BY s.createdAt DESC")
    List<Story> findActiveStoriesByUserId(Long userId, Date now);

    /**
     * Encuentra todas las stories activas (no expiradas).
     */
    @Query("SELECT s FROM Story s WHERE s.expiresAt > :now ORDER BY s.createdAt DESC")
    List<Story> findAllActiveStories(Date now);

    /**
     * Encuentra stories expiradas para limpieza.
     */
    @Query("SELECT s FROM Story s WHERE s.expiresAt <= :now")
    List<Story> findExpiredStories(Date now);

    /**
     * Elimina stories expiradas.
     */
    void deleteByExpiresAtBefore(Date date);
}
