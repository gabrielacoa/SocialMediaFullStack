package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.request.SendMessageRequest;
import com.socialmediaapp.backend.dto.response.ChatDto;
import com.socialmediaapp.backend.dto.response.MessageDto;
import com.socialmediaapp.backend.dto.response.UserDto;
import com.socialmediaapp.backend.exception.custom.ForbiddenException;
import com.socialmediaapp.backend.exception.custom.ResourceNotFoundException;
import com.socialmediaapp.backend.mapper.UserMapper;
import com.socialmediaapp.backend.model.Chat;
import com.socialmediaapp.backend.model.Message;
import com.socialmediaapp.backend.model.User;
import com.socialmediaapp.backend.repository.ChatRepository;
import com.socialmediaapp.backend.repository.MessageRepository;
import com.socialmediaapp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de chats.
 */
@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public MessageDto sendMessage(SendMessageRequest request, Long senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", senderId));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getReceiverId()));

        // Buscar chat existente o crear uno nuevo
        Chat chat = chatRepository.findByUsers(senderId, request.getReceiverId())
                .orElseGet(() -> {
                    Chat newChat = new Chat();
                    newChat.setUser1(sender);
                    newChat.setUser2(receiver);
                    return chatRepository.save(newChat);
                });

        // Crear mensaje
        Message message = new Message();
        message.setContent(request.getContent());
        message.setSender(sender);
        message.setChat(chat);

        Message savedMessage = messageRepository.save(message);

        // Actualizar timestamp del chat
        chat.setLastMessageAt(new Date());
        chatRepository.save(chat);

        return mapMessageToDto(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatDto> getUserChats(Long userId) {
        return chatRepository.findByUserId(userId)
                .stream()
                .map(chat -> mapChatToDto(chat, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ChatDto getChatById(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat", chatId));

        if (!chat.hasUser(userId)) {
            throw new ForbiddenException("No tienes acceso a este chat");
        }

        return mapChatToDto(chat, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getChatMessages(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat", chatId));

        if (!chat.hasUser(userId)) {
            throw new ForbiddenException("No tienes acceso a este chat");
        }

        return messageRepository.findByChatIdOrderBySentAtAsc(chatId)
                .stream()
                .map(this::mapMessageToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markMessageAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", messageId));

        if (!message.getChat().hasUser(userId)) {
            throw new ForbiddenException("No tienes acceso a este mensaje");
        }

        message.setRead(true);
        messageRepository.save(message);
    }

    @Override
    public void markChatAsRead(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat", chatId));

        if (!chat.hasUser(userId)) {
            throw new ForbiddenException("No tienes acceso a este chat");
        }

        List<Message> unreadMessages = messageRepository.findUnreadMessagesByChatAndUser(chatId, userId);
        unreadMessages.forEach(msg -> msg.setRead(true));
        messageRepository.saveAll(unreadMessages);
    }

    @Override
    public void deleteChat(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat", chatId));

        if (!chat.hasUser(userId)) {
            throw new ForbiddenException("No tienes acceso a este chat");
        }

        chatRepository.delete(chat);
    }

    private ChatDto mapChatToDto(Chat chat, Long currentUserId) {
        ChatDto dto = new ChatDto();
        dto.setId(chat.getId());
        dto.setCreatedAt(chat.getCreatedAt());
        dto.setLastMessageAt(chat.getLastMessageAt());

        // Mapear usuarios
        dto.setUser1(userMapper.toDto(chat.getUser1()));
        dto.setUser2(userMapper.toDto(chat.getUser2()));

        // Obtener último mensaje
        List<Message> messages = chat.getMessages();
        if (messages != null && !messages.isEmpty()) {
            Message lastMessage = messages.get(messages.size() - 1);
            dto.setLastMessage(mapMessageToDto(lastMessage));
        }

        // Contar mensajes no leídos
        long unreadCount = messageRepository.countUnreadMessagesByChatAndUser(chat.getId(), currentUserId);
        dto.setUnreadCount((int) unreadCount);

        return dto;
    }

    private MessageDto mapMessageToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setSentAt(message.getSentAt());
        dto.setRead(message.isRead());

        if (message.getSender() != null) {
            dto.setSenderId(message.getSender().getId());
            dto.setSenderUsername(message.getSender().getUsername());
        }

        if (message.getChat() != null) {
            dto.setChatId(message.getChat().getId());
        }

        return dto;
    }
}
