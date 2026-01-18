package com.socialmediaapp.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialmediaapp.backend.dto.request.auth.ChangePasswordRequest;
import com.socialmediaapp.backend.dto.request.auth.LoginRequest;
import com.socialmediaapp.backend.dto.request.auth.RegisterRequest;
import com.socialmediaapp.backend.dto.response.AuthResponse;
import com.socialmediaapp.backend.dto.response.UserDto;
import com.socialmediaapp.backend.exception.custom.BadRequestException;
import com.socialmediaapp.backend.exception.custom.DuplicateResourceException;
import com.socialmediaapp.backend.exception.custom.UnauthorizedException;
import com.socialmediaapp.backend.mapper.UserMapper;
import com.socialmediaapp.backend.model.User;
import com.socialmediaapp.backend.repository.UserRepository;
import com.socialmediaapp.backend.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para AuthController.
 * Valida endpoints de autenticación (registro, login, cambio de contraseña).
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("SecurePass123!");

        loginRequest = new LoginRequest();
        loginRequest.setEmailOrUsername("testuser@example.com");
        loginRequest.setPassword("SecurePass123!");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("$2a$10$encodedPassword");

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("testuser@example.com");
    }

    @Test
    @DisplayName("Should register new user successfully")
    void testRegister_Success() throws Exception {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(anyString())).thenReturn("jwt-token-123");
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.user.username").value("testuser"));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should return 409 when email already exists")
    void testRegister_EmailExists() throws Exception {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return 409 when username already exists")
    void testRegister_UsernameExists() throws Exception {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return 400 when registration data is invalid")
    void testRegister_InvalidData() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername("a"); // Too short
        invalidRequest.setEmail("invalid-email"); // Invalid format
        invalidRequest.setPassword("123"); // Too weak

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should login with email successfully")
    void testLogin_WithEmail_Success() throws Exception {
        // Given
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(auth);
        when(userRepository.findByEmail(loginRequest.getEmailOrUsername()))
            .thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(testUser.getUsername())).thenReturn("jwt-token-123");
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    @DisplayName("Should login with username successfully")
    void testLogin_WithUsername_Success() throws Exception {
        // Given
        loginRequest.setEmailOrUsername("testuser");
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(auth);
        when(userRepository.findByEmail("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(testUser.getUsername())).thenReturn("jwt-token-123");
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"));
    }

    @Test
    @DisplayName("Should return 401 when credentials are invalid")
    void testLogin_InvalidCredentials() throws Exception {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should change password successfully")
    void testChangePassword_Success() throws Exception {
        // Given
        ChangePasswordRequest changeRequest = new ChangePasswordRequest();
        changeRequest.setOldPassword("OldPass123!");
        changeRequest.setNewPassword("NewPass123!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("OldPass123!", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("NewPass123!")).thenReturn("$2a$10$newEncodedPassword");
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When & Then
        mockMvc.perform(put("/api/auth/change-password")
                .header("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Contraseña actualizada exitosamente"));

        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should return 400 when old password is incorrect")
    void testChangePassword_WrongOldPassword() throws Exception {
        // Given
        ChangePasswordRequest changeRequest = new ChangePasswordRequest();
        changeRequest.setOldPassword("WrongPass123!");
        changeRequest.setNewPassword("NewPass123!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("WrongPass123!", testUser.getPassword())).thenReturn(false);

        // When & Then
        mockMvc.perform(put("/api/auth/change-password")
                .header("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeRequest)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return 404 when user not found during password change")
    void testChangePassword_UserNotFound() throws Exception {
        // Given
        ChangePasswordRequest changeRequest = new ChangePasswordRequest();
        changeRequest.setOldPassword("OldPass123!");
        changeRequest.setNewPassword("NewPass123!");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/auth/change-password")
                .header("userId", "999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeRequest)))
                .andExpect(status().isNotFound());
    }
}
