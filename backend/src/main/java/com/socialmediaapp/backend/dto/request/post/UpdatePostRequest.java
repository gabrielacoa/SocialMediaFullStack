package com.socialmediaapp.backend.dto.request.post;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para solicitud de actualización de post.
 */
@Data
public class UpdatePostRequest {

    @Size(max = 2200, message = "El contenido no puede exceder 2200 caracteres")
    private String content;
}
