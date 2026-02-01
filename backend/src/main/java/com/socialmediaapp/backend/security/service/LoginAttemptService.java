package com.socialmediaapp.backend.security.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Servicio para rastrear y bloquear intentos de login fallidos.
 * Implementa protección contra ataques de fuerza bruta.
 */
@Service
public class LoginAttemptService {

    private static final Logger logger = LoggerFactory.getLogger(LoginAttemptService.class);

    @Value("${security.login.max-attempts:5}")
    private int maxAttempts;

    @Value("${security.login.lock-duration-minutes:15}")
    private int lockDurationMinutes;

    // Cache para rastrear intentos fallidos por IP/usuario
    private final Cache<String, Integer> attemptsCache;

    // Cache para rastrear cuentas bloqueadas
    private final Cache<String, Long> blockedCache;

    public LoginAttemptService() {
        this.attemptsCache = Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(10000)
                .build();

        this.blockedCache = Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(10000)
                .build();
    }

    /**
     * Registra un intento de login fallido.
     * @param key Identificador único (IP:username o solo username)
     */
    public void loginFailed(String key) {
        Integer attempts = attemptsCache.getIfPresent(key);
        if (attempts == null) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);

        logger.warn("Intento de login fallido para: {}. Intentos: {}/{}", key, attempts, maxAttempts);

        if (attempts >= maxAttempts) {
            blockAccount(key);
        }
    }

    /**
     * Registra un login exitoso, limpiando los intentos fallidos.
     * @param key Identificador único
     */
    public void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
        blockedCache.invalidate(key);
        logger.info("Login exitoso para: {}. Contador de intentos reiniciado.", key);
    }

    /**
     * Verifica si una cuenta está bloqueada.
     * @param key Identificador único
     * @return true si la cuenta está bloqueada
     */
    public boolean isBlocked(String key) {
        Long blockedUntil = blockedCache.getIfPresent(key);
        if (blockedUntil == null) {
            return false;
        }

        if (System.currentTimeMillis() > blockedUntil) {
            // El bloqueo ha expirado
            blockedCache.invalidate(key);
            attemptsCache.invalidate(key);
            return false;
        }

        return true;
    }

    /**
     * Obtiene el tiempo restante de bloqueo en segundos.
     * @param key Identificador único
     * @return Segundos restantes o 0 si no está bloqueado
     */
    public long getRemainingBlockTime(String key) {
        Long blockedUntil = blockedCache.getIfPresent(key);
        if (blockedUntil == null) {
            return 0;
        }

        long remaining = (blockedUntil - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }

    /**
     * Obtiene el número de intentos fallidos restantes.
     * @param key Identificador único
     * @return Número de intentos restantes
     */
    public int getRemainingAttempts(String key) {
        Integer attempts = attemptsCache.getIfPresent(key);
        if (attempts == null) {
            return maxAttempts;
        }
        return Math.max(0, maxAttempts - attempts);
    }

    /**
     * Bloquea una cuenta por el tiempo configurado.
     * @param key Identificador único
     */
    private void blockAccount(String key) {
        long blockedUntil = System.currentTimeMillis() + (lockDurationMinutes * 60 * 1000L);
        blockedCache.put(key, blockedUntil);
        logger.warn("Cuenta bloqueada por {} minutos: {}", lockDurationMinutes, key);
    }
}
