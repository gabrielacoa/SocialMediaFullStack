package com.socialmediaapp.backend.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request para completar login con codigo 2FA.
 */
@Data
public class TwoFactorLoginRequest {

    @NotBlank(message = "El token temporal es requerido")
    private String tempToken;

    @NotBlank(message = "El codigo 2FA es requerido")
    @Size(min = 6, max = 6, message = "El codigo debe tener 6 digitos")
    private String code;
}
