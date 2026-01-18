package com.socialmediaapp.backend.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para solicitud de login.
 */
@Data
public class LoginRequest {

    @NotBlank(message = "El email o username es obligatorio")
    private String emailOrUsername;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
