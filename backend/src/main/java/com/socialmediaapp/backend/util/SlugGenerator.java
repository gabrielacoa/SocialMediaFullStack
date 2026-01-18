package com.socialmediaapp.backend.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Generador de slugs para URLs amigables.
 * Convierte texto con espacios y caracteres especiales en URLs válidas.
 */
public final class SlugGenerator {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");

    // Prevenir instanciación
    private SlugGenerator() {
        throw new AssertionError("Cannot instantiate SlugGenerator class");
    }

    /**
     * Genera un slug a partir de un texto.
     * Ejemplo: "Hola Mundo 123!" -> "hola-mundo-123"
     */
    public static String generate(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = EDGESDHASHES.matcher(slug).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Genera un slug con límite de caracteres.
     */
    public static String generate(String input, int maxLength) {
        String slug = generate(input);
        if (slug.length() > maxLength) {
            slug = slug.substring(0, maxLength);
            // Asegurar que no termina con guión
            slug = EDGESDHASHES.matcher(slug).replaceAll("");
        }
        return slug;
    }

    /**
     * Genera un slug único agregando un timestamp.
     * Útil para nombres de archivos o identificadores únicos.
     */
    public static String generateUnique(String input) {
        String slug = generate(input);
        long timestamp = System.currentTimeMillis();
        return slug + "-" + timestamp;
    }

    /**
     * Valida si un string es un slug válido.
     */
    public static boolean isValidSlug(String slug) {
        if (slug == null || slug.isEmpty()) {
            return false;
        }
        return slug.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    }
}
