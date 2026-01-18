package com.socialmediaapp.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validador de fortaleza de contraseñas.
 *
 * Requisitos:
 * - Mínimo 8 caracteres
 * - Al menos una letra mayúscula
 * - Al menos una letra minúscula
 * - Al menos un número
 * - Al menos un carácter especial (!@#$%^&*()_+-=[]{}|;:,.<>?)
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final int MIN_LENGTH = 8;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?]");

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isEmpty()) {
            setCustomMessage(context, "La contraseña es obligatoria");
            return false;
        }

        if (password.length() < MIN_LENGTH) {
            setCustomMessage(context, "La contraseña debe tener al menos " + MIN_LENGTH + " caracteres");
            return false;
        }

        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            setCustomMessage(context, "La contraseña debe contener al menos una letra mayúscula");
            return false;
        }

        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            setCustomMessage(context, "La contraseña debe contener al menos una letra minúscula");
            return false;
        }

        if (!DIGIT_PATTERN.matcher(password).find()) {
            setCustomMessage(context, "La contraseña debe contener al menos un número");
            return false;
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            setCustomMessage(context, "La contraseña debe contener al menos un carácter especial (!@#$%^&*()_+-=[]{}|;:,.<>?)");
            return false;
        }

        // Verificar contraseñas comunes
        if (isCommonPassword(password)) {
            setCustomMessage(context, "La contraseña es demasiado común. Por favor, elija una contraseña más segura");
            return false;
        }

        return true;
    }

    /**
     * Establece un mensaje de error personalizado.
     */
    private void setCustomMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    /**
     * Verifica si la contraseña está en la lista de contraseñas comunes.
     */
    private boolean isCommonPassword(String password) {
        String lowerPassword = password.toLowerCase();
        String[] commonPasswords = {
            "password", "password123", "12345678", "qwerty", "abc123",
            "password1", "12341234", "111111", "1234567890", "letmein",
            "admin", "admin123", "welcome", "monkey", "dragon"
        };

        for (String common : commonPasswords) {
            if (lowerPassword.equals(common)) {
                return true;
            }
        }

        return false;
    }
}
