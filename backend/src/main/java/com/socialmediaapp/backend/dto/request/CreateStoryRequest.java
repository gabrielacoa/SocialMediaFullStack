package com.socialmediaapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para solicitud de creación de story.
 */
@Data
public class CreateStoryRequest {

    private String mediaUrl;

    @Size(max = 200, message = "El caption no puede exceder 200 caracteres")
    private String caption;
}
