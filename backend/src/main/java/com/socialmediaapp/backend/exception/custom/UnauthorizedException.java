package com.socialmediaapp.backend.exception.custom;

/**
 * Excepción lanzada cuando un usuario intenta realizar una acción sin autorización.
 * Se mapea a HTTP 401 Unauthorized en el GlobalExceptionHandler.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException() {
        super("No tienes autorización para realizar esta acción");
    }
}
