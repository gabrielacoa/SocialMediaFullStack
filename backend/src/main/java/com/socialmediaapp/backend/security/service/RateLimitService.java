package com.socialmediaapp.backend.security.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio de Rate Limiting para proteger contra ataques de fuerza bruta.
 */
@Service
public class RateLimitService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    /**
     * Resuelve un bucket para una clave específica (ej: IP + endpoint).
     * Límite: 5 intentos por minuto para autenticación.
     */
    public Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> createNewBucket());
    }

    /**
     * Crea un nuevo bucket con límite de 5 tokens que se rellenan a razón de 5 por minuto.
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Resuelve un bucket para endpoints generales.
     * Límite: 100 peticiones por minuto.
     */
    public Bucket resolveGeneralBucket(String key) {
        return cache.computeIfAbsent(key, k -> createGeneralBucket());
    }

    private Bucket createGeneralBucket() {
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
