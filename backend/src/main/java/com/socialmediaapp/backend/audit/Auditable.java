package com.socialmediaapp.backend.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotacion para marcar metodos que deben ser auditados.
 * El aspecto AuditAspect interceptara estos metodos y registrara la actividad.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    /**
     * Accion que se esta realizando (ej: "LOGIN", "CREATE_POST", "DELETE_USER")
     */
    String action();

    /**
     * Descripcion adicional de la accion
     */
    String description() default "";

    /**
     * Indica si se debe registrar informacion sensible
     */
    boolean logSensitive() default false;
}
