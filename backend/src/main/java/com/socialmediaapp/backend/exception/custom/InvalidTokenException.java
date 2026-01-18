package com.socialmediaapp.backend.exception.custom;

/**
 * Excepción lanzada cuando un token JWT es inválido o ha expirado.
 * Se mapea a HTTP 401 Unauthorized en el GlobalExceptionHandler.
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTokenException() {
        super("Token inválido o expirado");
    }
}
