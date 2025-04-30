package com.socialmediaapp.backend.exception;

/**
 * Excepción personalizada para la aplicación.
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
