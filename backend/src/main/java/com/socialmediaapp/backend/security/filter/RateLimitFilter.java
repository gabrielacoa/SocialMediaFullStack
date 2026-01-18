package com.socialmediaapp.backend.security.filter;

import com.socialmediaapp.backend.security.service.RateLimitService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro para aplicar rate limiting a las peticiones.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);

    @Autowired
    private RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String key = getClientIP(request) + ":" + request.getRequestURI();
        Bucket bucket;

        // Aplicar rate limiting más estricto para endpoints de autenticación
        if (request.getRequestURI().startsWith("/api/authenticate") ||
            request.getRequestURI().startsWith("/api/register") ||
            request.getRequestURI().startsWith("/api/login")) {
            bucket = rateLimitService.resolveBucket(key);
        } else {
            bucket = rateLimitService.resolveGeneralBucket(key);
        }

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // Agregar headers informativos
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            logger.warn("Rate limit excedido para IP: {} en endpoint: {}", getClientIP(request), request.getRequestURI());

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.setContentType("application/json");
            response.getWriter().write(
                String.format("{\"error\": \"Too many requests\", \"message\": \"Rate limit exceeded. Try again in %d seconds\"}", waitForRefill)
            );
        }
    }

    /**
     * Obtiene la IP real del cliente considerando proxies y load balancers.
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
