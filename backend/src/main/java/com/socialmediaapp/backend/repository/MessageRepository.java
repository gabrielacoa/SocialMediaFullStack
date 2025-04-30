package com.socialmediaapp.backend.repository;

import com.socialmediaapp.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Message.
 */

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderId(Long senderId);
    List<Message> findByReceiverId(Long receiverId);
}
