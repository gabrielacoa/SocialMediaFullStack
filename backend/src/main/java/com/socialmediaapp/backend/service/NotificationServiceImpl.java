package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.response.NotificationDto;
import com.socialmediaapp.backend.exception.custom.ResourceNotFoundException;
import com.socialmediaapp.backend.mapper.NotificationMapper;
import com.socialmediaapp.backend.model.Notification;
import com.socialmediaapp.backend.model.User;
import com.socialmediaapp.backend.repository.NotificationRepository;
import com.socialmediaapp.backend.repository.UserRepository;
import com.socialmediaapp.backend.socketio.SocketIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para la entidad Notification.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SocketIOService socketIOService;

    @Override
    public NotificationDto createNotification(String type, String message, String link, Long senderId, Long receiverId) {
        // No crear notificación si el sender y receiver son el mismo usuario
        if (senderId.equals(receiverId)) {
            return null;
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User", receiverId));

        Notification notification = new Notification();
        notification.setType(Notification.NotificationType.valueOf(type));
        notification.setMessage(message);
        notification.setLink(link);
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setRead(false);

        Notification saved = notificationRepository.save(notification);
        NotificationDto dto = notificationMapper.toDto(saved);

        // Enviar notificación en tiempo real via Socket.IO
        socketIOService.sendNotificationToUser(receiverId, dto);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationDto markNotificationAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));
        notification.setRead(true);
        Notification updated = notificationRepository.save(notification);
        return notificationMapper.toDto(updated);
    }

    @Override
    public void markAllNotificationsAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByReceiverIdAndIsRead(userId, false);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUnreadNotifications(Long userId) {
        return notificationRepository.countByReceiverIdAndIsRead(userId, false);
    }

    @Override
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));
        notificationRepository.delete(notification);
    }
}
