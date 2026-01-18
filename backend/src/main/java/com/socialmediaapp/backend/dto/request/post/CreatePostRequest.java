package com.socialmediaapp.backend.dto.request.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para solicitud de creación de post.
 */
@Data
public class CreatePostRequest {

    @NotBlank(message = "El contenido del post es obligatorio")
    @Size(max = 2200, message = "El contenido no puede exceder 2200 caracteres")
    private String content;

    private String imageUrl;
    private String videoUrl;
}
