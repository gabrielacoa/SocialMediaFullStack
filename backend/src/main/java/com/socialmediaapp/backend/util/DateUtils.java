package com.socialmediaapp.backend.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Utilidades para manejo de fechas y tiempos.
 */
public final class DateUtils {

    // Prevenir instanciación
    private DateUtils() {
        throw new AssertionError("Cannot instantiate DateUtils class");
    }

    /**
     * Convierte Date a LocalDateTime.
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * Convierte LocalDateTime a Date.
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Verifica si una fecha está dentro de las últimas N horas.
     */
    public static boolean isWithinLastHours(Date date, long hours) {
        if (date == null) {
            return false;
        }
        long hoursAgo = System.currentTimeMillis() - (hours * 60 * 60 * 1000);
        return date.getTime() >= hoursAgo;
    }

    /**
     * Verifica si una story es vigente (últimas 24 horas).
     */
    public static boolean isStoryValid(Date createdAt) {
        return isWithinLastHours(createdAt, 24);
    }

    /**
     * Calcula la diferencia en minutos entre dos fechas.
     */
    public static long getDifferenceInMinutes(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        long diffInMillis = Math.abs(date1.getTime() - date2.getTime());
        return diffInMillis / (60 * 1000);
    }

    /**
     * Calcula la diferencia en días entre dos fechas.
     */
    public static long getDifferenceInDays(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        long diffInMillis = Math.abs(date1.getTime() - date2.getTime());
        return diffInMillis / (24 * 60 * 60 * 1000);
    }

    /**
     * Genera texto relativo de tiempo (ej: "hace 2 horas", "hace 3 días").
     */
    public static String getRelativeTime(Date date) {
        if (date == null) {
            return "";
        }

        Instant now = Instant.now();
        Instant then = date.toInstant();
        Duration duration = Duration.between(then, now);

        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "ahora";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return "hace " + minutes + (minutes == 1 ? " minuto" : " minutos");
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return "hace " + hours + (hours == 1 ? " hora" : " horas");
        } else if (seconds < 604800) {
            long days = seconds / 86400;
            return "hace " + days + (days == 1 ? " día" : " días");
        } else if (seconds < 2592000) {
            long weeks = seconds / 604800;
            return "hace " + weeks + (weeks == 1 ? " semana" : " semanas");
        } else if (seconds < 31536000) {
            long months = seconds / 2592000;
            return "hace " + months + (months == 1 ? " mes" : " meses");
        } else {
            long years = seconds / 31536000;
            return "hace " + years + (years == 1 ? " año" : " años");
        }
    }

    /**
     * Obtiene la fecha actual como Date.
     */
    public static Date now() {
        return new Date();
    }

    /**
     * Agrega días a una fecha.
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = toLocalDateTime(date);
        return toDate(localDateTime.plusDays(days));
    }

    /**
     * Agrega horas a una fecha.
     */
    public static Date addHours(Date date, int hours) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = toLocalDateTime(date);
        return toDate(localDateTime.plusHours(hours));
    }
}
