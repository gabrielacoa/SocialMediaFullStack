package com.socialmediaapp.backend.repository;

import com.socialmediaapp.backend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repositorio para la entidad Notification.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Encuentra todas las notificaciones para un usuario específico.
     * @param receiverId ID del usuario receptor
     * @return Lista de notificaciones
     */
    List<Notification> findByReceiverId(Long receiverId);

    /**
     * Encuentra todas las notificaciones para un usuario específico ordenadas por fecha de creación descendente.
     * @param receiverId ID del usuario receptor
     * @return Lista de notificaciones
     */
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    /**
     * Encuentra notificaciones por usuario receptor y estado de lectura.
     * @param receiverId ID del usuario receptor
     * @param isRead Estado de lectura
     * @return Lista de notificaciones
     */
    List<Notification> findByReceiverIdAndIsRead(Long receiverId, boolean isRead);

    /**
     * Cuenta notificaciones por usuario receptor y estado de lectura.
     * @param receiverId ID del usuario receptor
     * @param isRead Estado de lectura
     * @return Cantidad de notificaciones
     */
    long countByReceiverIdAndIsRead(Long receiverId, boolean isRead);

    /**
     * Cuenta el número de notificaciones no leídas para un usuario específico.
     * @param receiverId ID del usuario receptor
     * @return Número de notificaciones no leídas
     */
    Long countByReceiverIdAndIsReadFalse(Long receiverId);

    /**
     * Marca todas las notificaciones de un usuario como leídas.
     * @param receiverId ID del usuario receptor
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.receiver.id = ?1 AND n.isRead = false")
    void markAllAsReadByReceiverId(Long receiverId);
}