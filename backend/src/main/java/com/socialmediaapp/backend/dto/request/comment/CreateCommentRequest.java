package com.socialmediaapp.backend.dto.request.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para solicitud de creación de comentario.
 */
@Data
public class CreateCommentRequest {

    @NotBlank(message = "El contenido del comentario es obligatorio")
    @Size(max = 500, message = "El comentario no puede exceder 500 caracteres")
    private String content;

    @NotNull(message = "El ID del post es obligatorio")
    private Long postId;
}
