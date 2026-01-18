package com.socialmediaapp.backend.controller;

import com.socialmediaapp.backend.dto.request.SendMessageRequest;
import com.socialmediaapp.backend.dto.response.ChatDto;
import com.socialmediaapp.backend.dto.response.MessageDto;
import com.socialmediaapp.backend.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar chats y mensajes.
 */
@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * Envía un mensaje a otro usuario.
     */
    @PostMapping("/messages")
    public ResponseEntity<MessageDto> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            @RequestHeader("userId") Long senderId) {
        MessageDto message = chatService.sendMessage(request, senderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    /**
     * Obtiene todos los chats del usuario.
     */
    @GetMapping
    public ResponseEntity<List<ChatDto>> getUserChats(@RequestHeader("userId") Long userId) {
        List<ChatDto> chats = chatService.getUserChats(userId);
        return ResponseEntity.ok(chats);
    }

    /**
     * Obtiene un chat específico.
     */
    @GetMapping("/{chatId}")
    public ResponseEntity<ChatDto> getChatById(
            @PathVariable Long chatId,
            @RequestHeader("userId") Long userId) {
        ChatDto chat = chatService.getChatById(chatId, userId);
        return ResponseEntity.ok(chat);
    }

    /**
     * Obtiene todos los mensajes de un chat.
     */
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<MessageDto>> getChatMessages(
            @PathVariable Long chatId,
            @RequestHeader("userId") Long userId) {
        List<MessageDto> messages = chatService.getChatMessages(chatId, userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Marca un mensaje como leído.
     */
    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(
            @PathVariable Long messageId,
            @RequestHeader("userId") Long userId) {
        chatService.markMessageAsRead(messageId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marca todos los mensajes de un chat como leídos.
     */
    @PutMapping("/{chatId}/read")
    public ResponseEntity<Void> markChatAsRead(
            @PathVariable Long chatId,
            @RequestHeader("userId") Long userId) {
        chatService.markChatAsRead(chatId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina un chat.
     */
    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(
            @PathVariable Long chatId,
            @RequestHeader("userId") Long userId) {
        chatService.deleteChat(chatId, userId);
        return ResponseEntity.noContent().build();
    }
}
