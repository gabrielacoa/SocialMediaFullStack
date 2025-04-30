package com.socialmediaapp.backend.dto;

import lombok.Data;

/**
 * DTO para la entidad User.
 */
@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
}
