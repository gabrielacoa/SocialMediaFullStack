package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.request.chat.CreateChatRequest;
import com.socialmediaapp.backend.dto.request.message.SendMessageRequest;
import com.socialmediaapp.backend.dto.response.ChatDto;
import com.socialmediaapp.backend.dto.response.MessageDto;
import com.socialmediaapp.backend.exception.custom.BadRequestException;
import com.socialmediaapp.backend.exception.custom.ForbiddenException;
import com.socialmediaapp.backend.exception.custom.ResourceNotFoundException;
import com.socialmediaapp.backend.mapper.ChatMapper;
import com.socialmediaapp.backend.mapper.MessageMapper;
import com.socialmediaapp.backend.model.Chat;
import com.socialmediaapp.backend.model.Message;
import com.socialmediaapp.backend.model.User;
import com.socialmediaapp.backend.repository.ChatRepository;
import com.socialmediaapp.backend.repository.MessageRepository;
import com.socialmediaapp.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ChatService.
 * Valida la lógica de negocio para chats y mensajes entre usuarios.
 */
@DisplayName("ChatService Tests")
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatMapper chatMapper;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private ChatServiceImpl chatService;

    private User user1;
    private User user2;
    private Chat testChat;
    private Message testMessage;
    private ChatDto testChatDto;
    private MessageDto testMessageDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        testChat = new Chat();
        testChat.setId(1L);
        testChat.setUser1(user1);
        testChat.setUser2(user2);
        testChat.setCreatedAt(new Date());
        testChat.setLastMessageAt(new Date());

        testMessage = new Message();
        testMessage.setId(1L);
        testMessage.setSender(user1);
        testMessage.setChat(testChat);
        testMessage.setContent("Hello!");
        testMessage.setSentAt(new Date());
        testMessage.setRead(false);

        testChatDto = new ChatDto();
        testChatDto.setId(1L);

        testMessageDto = new MessageDto();
        testMessageDto.setId(1L);
        testMessageDto.setContent("Hello!");
    }

    @Test
    @DisplayName("Should create chat between two users successfully")
    void testCreateChat_Success() {
        // Given
        CreateChatRequest request = new CreateChatRequest();
        request.setOtherUserId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(chatRepository.findChatBetweenUsers(1L, 2L)).thenReturn(Optional.empty());
        when(chatRepository.save(any(Chat.class))).thenReturn(testChat);
        when(chatMapper.toDto(testChat)).thenReturn(testChatDto);

        // When
        ChatDto result = chatService.createChat(request, 1L);

        // Then
        assertNotNull(result);
        verify(chatRepository, times(1)).save(any(Chat.class));
    }

    @Test
    @DisplayName("Should return existing chat if already exists between users")
    void testCreateChat_AlreadyExists() {
        // Given
        CreateChatRequest request = new CreateChatRequest();
        request.setOtherUserId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(chatRepository.findChatBetweenUsers(1L, 2L)).thenReturn(Optional.of(testChat));
        when(chatMapper.toDto(testChat)).thenReturn(testChatDto);

        // When
        ChatDto result = chatService.createChat(request, 1L);

        // Then
        assertNotNull(result);
        verify(chatRepository, never()).save(any(Chat.class));
    }

    @Test
    @DisplayName("Should throw exception when trying to chat with self")
    void testCreateChat_SameUser() {
        // Given
        CreateChatRequest request = new CreateChatRequest();
        request.setOtherUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            chatService.createChat(request, 1L);
        });
        verify(chatRepository, never()).save(any(Chat.class));
    }

    @Test
    @DisplayName("Should send message successfully")
    void testSendMessage_Success() {
        // Given
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Hello!");

        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);
        when(messageMapper.toDto(testMessage)).thenReturn(testMessageDto);

        // When
        MessageDto result = chatService.sendMessage(1L, request, 1L);

        // Then
        assertNotNull(result);
        assertEquals("Hello!", result.getContent());
        verify(messageRepository, times(1)).save(any(Message.class));
        verify(chatRepository, times(1)).save(testChat); // Updates lastMessageAt
    }

    @Test
    @DisplayName("Should throw exception when user not in chat")
    void testSendMessage_UserNotInChat() {
        // Given
        User user3 = new User();
        user3.setId(3L);

        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Hello!");

        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user3));

        // When & Then
        assertThrows(ForbiddenException.class, () -> {
            chatService.sendMessage(1L, request, 3L);
        });
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    @DisplayName("Should get messages by chat id")
    void testGetMessagesByChatId_Success() {
        // Given
        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));
        when(messageRepository.findByChatIdOrderBySentAtAsc(1L))
            .thenReturn(Arrays.asList(testMessage));
        when(messageMapper.toDto(testMessage)).thenReturn(testMessageDto);

        // When
        List<MessageDto> result = chatService.getMessagesByChatId(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(messageRepository, times(1)).findByChatIdOrderBySentAtAsc(1L);
    }

    @Test
    @DisplayName("Should get chats by user id")
    void testGetChatsByUserId_Success() {
        // Given
        when(chatRepository.findByUser1IdOrUser2IdOrderByLastMessageAtDesc(1L, 1L))
            .thenReturn(Arrays.asList(testChat));
        when(chatMapper.toDto(testChat)).thenReturn(testChatDto);

        // When
        List<ChatDto> result = chatService.getChatsByUserId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should mark message as read successfully")
    void testMarkMessageAsRead_Success() {
        // Given
        when(messageRepository.findById(1L)).thenReturn(Optional.of(testMessage));
        when(messageRepository.save(testMessage)).thenReturn(testMessage);
        when(messageMapper.toDto(testMessage)).thenReturn(testMessageDto);

        // When
        MessageDto result = chatService.markMessageAsRead(1L);

        // Then
        assertNotNull(result);
        assertTrue(testMessage.isRead());
        verify(messageRepository, times(1)).save(testMessage);
    }

    @Test
    @DisplayName("Should count unread messages correctly")
    void testCountUnreadMessages_Success() {
        // Given
        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));
        when(messageRepository.countUnreadMessagesByChatAndUser(1L, 1L)).thenReturn(5L);

        // When
        long count = chatService.countUnreadMessages(1L, 1L);

        // Then
        assertEquals(5L, count);
    }

    @Test
    @DisplayName("Should delete chat successfully when user is participant")
    void testDeleteChat_Success() {
        // Given
        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));
        doNothing().when(chatRepository).delete(testChat);

        // When
        chatService.deleteChat(1L, 1L);

        // Then
        verify(chatRepository, times(1)).delete(testChat);
    }

    @Test
    @DisplayName("Should throw exception when non-participant tries to delete chat")
    void testDeleteChat_UserNotInChat() {
        // Given
        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));

        // When & Then
        assertThrows(ForbiddenException.class, () -> {
            chatService.deleteChat(1L, 999L);
        });
        verify(chatRepository, never()).delete(any(Chat.class));
    }

    @Test
    @DisplayName("Should throw exception when chat not found")
    void testGetChatById_NotFound() {
        // Given
        when(chatRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            chatService.getChatById(999L, 1L);
        });
    }
}
