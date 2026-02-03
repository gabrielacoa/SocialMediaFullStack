package com.socialmediaapp.backend.mapper;

import com.socialmediaapp.backend.dto.response.UserDto;
import com.socialmediaapp.backend.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre User entity y UserDto.
 * Separa la lógica de mapeo del Service layer siguiendo Single Responsibility Principle.
 */
@Component
public class UserMapper {

    /**
     * Convierte User entity a UserDto.
     */
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setBio(user.getBio());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        return dto;
    }

    /**
     * Actualiza un User entity existente con datos del UserDto.
     * No mapea el ID ni campos sensibles como password.
     */
    public void updateEntityFromDto(UserDto dto, User user) {
        if (dto == null || user == null) {
            return;
        }

        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getBio() != null) {
            user.setBio(dto.getBio());
        }
        if (dto.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(dto.getProfilePictureUrl());
        }
    }
}
