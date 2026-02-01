package com.socialmediaapp.backend.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request para activar 2FA con el codigo de verificacion.
 */
@Data
public class TwoFactorSetupRequest {

    @NotBlank(message = "El codigo es requerido")
    @Size(min = 6, max = 6, message = "El codigo debe tener 6 digitos")
    private String code;
}
