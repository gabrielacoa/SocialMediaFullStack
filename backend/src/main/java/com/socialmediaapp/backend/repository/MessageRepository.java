package com.socialmediaapp.backend.repository;

import com.socialmediaapp.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Message.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Encuentra todos los mensajes de un chat ordenados por fecha de envío.
     */
    List<Message> findByChatIdOrderBySentAtAsc(Long chatId);

    /**
     * Encuentra mensajes no leídos de un chat para un usuario específico.
     */
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND m.sender.id != :userId AND m.isRead = false")
    List<Message> findUnreadMessagesByChatAndUser(@Param("chatId") Long chatId, @Param("userId") Long userId);

    /**
     * Cuenta mensajes no leídos de un chat para un usuario específico.
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat.id = :chatId AND m.sender.id != :userId AND m.isRead = false")
    long countUnreadMessagesByChatAndUser(@Param("chatId") Long chatId, @Param("userId") Long userId);
}
