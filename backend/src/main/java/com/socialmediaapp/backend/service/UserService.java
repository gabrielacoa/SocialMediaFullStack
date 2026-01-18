package com.socialmediaapp.backend.service;

import com.socialmediaapp.backend.dto.request.user.UpdateProfileRequest;
import com.socialmediaapp.backend.dto.response.UserDto;

import java.util.List;

/**
 * Interfaz para el servicio de la entidad User.
 */
public interface UserService {
    UserDto getUserById(Long id);
    UserDto getUserByUsername(String username);
    List<UserDto> getAllUsers();
    UserDto updateProfile(Long id, UpdateProfileRequest request);
    void deleteUser(Long id);
}
