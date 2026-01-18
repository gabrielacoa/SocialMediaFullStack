package com.socialmediaapp.backend.exception.custom;

/**
 * Excepción lanzada cuando un usuario autenticado no tiene permisos para acceder a un recurso.
 * Se mapea a HTTP 403 Forbidden en el GlobalExceptionHandler.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException() {
        super("No tienes permisos para acceder a este recurso");
    }
}
