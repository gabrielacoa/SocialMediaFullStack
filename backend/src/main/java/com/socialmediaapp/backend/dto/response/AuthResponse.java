package com.socialmediaapp.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de autenticación (login/register).
 * Contiene el token JWT y la información del usuario.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private UserDto user;
    private String message;

    // Campos para 2FA
    private boolean requiresTwoFactor;
    private String tempToken; // Token temporal para completar 2FA

    public AuthResponse(String token, UserDto user) {
        this.token = token;
        this.user = user;
        this.requiresTwoFactor = false;
    }

    public AuthResponse(String token, UserDto user, String message) {
        this.token = token;
        this.user = user;
        this.message = message;
        this.requiresTwoFactor = false;
    }

    /**
     * Constructor para respuesta que requiere 2FA.
     */
    public static AuthResponse requiresTwoFactor(String tempToken) {
        AuthResponse response = new AuthResponse();
        response.setRequiresTwoFactor(true);
        response.setTempToken(tempToken);
        response.setMessage("Se requiere codigo 2FA");
        return response;
    }
}
