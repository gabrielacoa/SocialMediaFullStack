package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.request.SendMessageRequest;
import com.socialmediaapp.backend.dto.response.ChatDto;
import com.socialmediaapp.backend.dto.response.MessageDto;

import java.util.List;

/**
 * Servicio para gestionar chats y mensajes.
 */
public interface ChatService {

    /**
     * Envía un mensaje a otro usuario (crea chat si no existe).
     */
    MessageDto sendMessage(SendMessageRequest request, Long senderId);

    /**
     * Obtiene todos los chats de un usuario.
     */
    List<ChatDto> getUserChats(Long userId);

    /**
     * Obtiene un chat por ID.
     */
    ChatDto getChatById(Long chatId, Long userId);

    /**
     * Obtiene todos los mensajes de un chat.
     */
    List<MessageDto> getChatMessages(Long chatId, Long userId);

    /**
     * Marca un mensaje como leído.
     */
    void markMessageAsRead(Long messageId, Long userId);

    /**
     * Marca todos los mensajes de un chat como leídos.
     */
    void markChatAsRead(Long chatId, Long userId);

    /**
     * Elimina un chat.
     */
    void deleteChat(Long chatId, Long userId);
}
