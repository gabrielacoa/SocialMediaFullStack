package com.socialmediaapp.backend.exception.custom;

/**
 * Excepción lanzada cuando una cuenta está bloqueada por demasiados intentos fallidos.
 * Se mapea a HTTP 423 Locked en el GlobalExceptionHandler.
 */
public class AccountLockedException extends RuntimeException {

    private final long remainingSeconds;

    public AccountLockedException(String message, long remainingSeconds) {
        super(message);
        this.remainingSeconds = remainingSeconds;
    }

    public AccountLockedException(long remainingSeconds) {
        super(String.format("Cuenta bloqueada. Intenta nuevamente en %d segundos", remainingSeconds));
        this.remainingSeconds = remainingSeconds;
    }

    public long getRemainingSeconds() {
        return remainingSeconds;
    }
}
