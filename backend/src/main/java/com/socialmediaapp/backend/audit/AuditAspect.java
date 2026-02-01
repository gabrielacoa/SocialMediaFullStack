package com.socialmediaapp.backend.audit;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Aspecto para auditar acciones en la aplicacion.
 * Registra quien hizo que, cuando y desde donde.
 */
@Aspect
@Component
public class AuditAspect {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Registra antes de ejecutar un metodo auditado.
     */
    @Before("@annotation(auditable)")
    public void auditBefore(JoinPoint joinPoint, Auditable auditable) {
        String timestamp = LocalDateTime.now().format(formatter);
        String action = auditable.action();
        String description = auditable.description();
        String method = joinPoint.getSignature().toShortString();
        String clientIP = getClientIP();
        String username = getCurrentUsername();

        // NO registrar argumentos sensibles (passwords, tokens)
        String args = auditable.logSensitive()
            ? Arrays.toString(joinPoint.getArgs())
            : "[REDACTED]";

        auditLogger.info("AUDIT | {} | ACTION: {} | USER: {} | IP: {} | METHOD: {} | DESC: {} | ARGS: {}",
                timestamp, action, username, clientIP, method, description, args);
    }

    /**
     * Registra despues de una ejecucion exitosa.
     */
    @AfterReturning(pointcut = "@annotation(auditable)", returning = "result")
    public void auditAfterSuccess(JoinPoint joinPoint, Auditable auditable, Object result) {
        String timestamp = LocalDateTime.now().format(formatter);
        String action = auditable.action();
        String username = getCurrentUsername();

        auditLogger.info("AUDIT | {} | ACTION: {} | USER: {} | STATUS: SUCCESS",
                timestamp, action, username);
    }

    /**
     * Registra cuando ocurre una excepcion.
     */
    @AfterThrowing(pointcut = "@annotation(auditable)", throwing = "exception")
    public void auditAfterFailure(JoinPoint joinPoint, Auditable auditable, Throwable exception) {
        String timestamp = LocalDateTime.now().format(formatter);
        String action = auditable.action();
        String username = getCurrentUsername();
        String clientIP = getClientIP();

        auditLogger.warn("AUDIT | {} | ACTION: {} | USER: {} | IP: {} | STATUS: FAILED | ERROR: {}",
                timestamp, action, username, clientIP, exception.getMessage());
    }

    /**
     * Obtiene la IP del cliente desde el request actual.
     */
    private String getClientIP() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String xfHeader = request.getHeader("X-Forwarded-For");
                if (xfHeader != null && !xfHeader.isEmpty()) {
                    return xfHeader.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            // Ignorar errores al obtener IP
        }
        return "UNKNOWN";
    }

    /**
     * Obtiene el username del contexto de seguridad.
     */
    private String getCurrentUsername() {
        try {
            org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                return auth.getName();
            }
        } catch (Exception e) {
            // Ignorar errores al obtener username
        }
        return "ANONYMOUS";
    }
}
