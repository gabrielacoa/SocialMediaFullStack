package com.socialmediaapp.backend.repository;

import com.socialmediaapp.backend.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Chat.
 */
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    /**
     * Encuentra todos los chats de un usuario ordenados por último mensaje.
     */
    @Query("SELECT c FROM Chat c WHERE c.user1.id = :userId OR c.user2.id = :userId ORDER BY c.lastMessageAt DESC")
    List<Chat> findByUserId(@Param("userId") Long userId);

    /**
     * Encuentra un chat entre dos usuarios específicos.
     */
    @Query("SELECT c FROM Chat c WHERE (c.user1.id = :user1Id AND c.user2.id = :user2Id) OR (c.user1.id = :user2Id AND c.user2.id = :user1Id)")
    Optional<Chat> findByUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}
