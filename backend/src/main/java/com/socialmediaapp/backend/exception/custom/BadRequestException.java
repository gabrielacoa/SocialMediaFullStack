package com.socialmediaapp.backend.exception.custom;

/**
 * Excepción lanzada cuando la solicitud del cliente es inválida.
 * Se mapea a HTTP 400 Bad Request en el GlobalExceptionHandler.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
