package com.socialmediaapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para solicitud de envío de mensaje.
 */
@Data
public class SendMessageRequest {

    @NotNull(message = "El ID del destinatario es obligatorio")
    private Long receiverId;

    @NotBlank(message = "El contenido del mensaje es obligatorio")
    @Size(max = 1000, message = "El mensaje no puede exceder 1000 caracteres")
    private String content;
}
