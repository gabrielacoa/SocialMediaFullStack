package com.socialmediaapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para solicitud de creación de reel.
 */
@Data
public class CreateReelRequest {

    @NotBlank(message = "La URL del video es obligatoria")
    private String videoUrl;

    @Size(max = 500, message = "El caption no puede exceder 500 caracteres")
    private String caption;
}
