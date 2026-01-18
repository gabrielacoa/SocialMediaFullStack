package com.socialmediaapp.backend.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para solicitud de actualización de perfil de usuario.
 */
@Data
public class UpdateProfileRequest {

    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String username;

    @Email(message = "El email debe ser válido")
    private String email;

    @Size(max = 200, message = "La biografía no puede exceder 200 caracteres")
    private String bio;

    private String profilePicture;
}
