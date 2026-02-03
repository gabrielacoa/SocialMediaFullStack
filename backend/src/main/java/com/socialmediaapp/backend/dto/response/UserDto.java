package com.socialmediaapp.backend.dto.response;

import lombok.Data;

/**
 * DTO para la entidad User.
 */
@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String profilePictureUrl;
}
