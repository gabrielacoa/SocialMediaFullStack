package com.socialmediaapp.backend.util;

/**
 * Constantes globales de la aplicación.
 * Centraliza valores mágicos y configuraciones reutilizables.
 */
public final class Constants {

    // Prevenir instanciación
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }

    // Límites de archivos
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024; // 100MB
    public static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB

    // Formatos permitidos
    public static final String[] ALLOWED_IMAGE_FORMATS = {"jpg", "jpeg", "png", "gif", "webp"};
    public static final String[] ALLOWED_VIDEO_FORMATS = {"mp4", "mov", "avi", "mkv"};

    // Límites de contenido
    public static final int MAX_POST_CONTENT_LENGTH = 2200;
    public static final int MAX_COMMENT_CONTENT_LENGTH = 500;
    public static final int MAX_BIO_LENGTH = 200;
    public static final int MAX_USERNAME_LENGTH = 50;
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 100;

    // Tiempos de expiración (milisegundos)
    public static final long JWT_EXPIRATION = 3600000; // 1 hora
    public static final long REFRESH_TOKEN_EXPIRATION = 604800000; // 7 días
    public static final long STORY_EXPIRATION = 86400000; // 24 horas

    // Paginación
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // Rate limiting
    public static final int AUTH_RATE_LIMIT = 5; // intentos por minuto
    public static final int GENERAL_RATE_LIMIT = 100; // requests por minuto

    // API Endpoints base
    public static final String API_BASE_PATH = "/api";
    public static final String AUTH_PATH = API_BASE_PATH + "/auth";
    public static final String USERS_PATH = API_BASE_PATH + "/users";
    public static final String POSTS_PATH = API_BASE_PATH + "/posts";
    public static final String COMMENTS_PATH = API_BASE_PATH + "/comments";
    public static final String LIKES_PATH = API_BASE_PATH + "/likes";
    public static final String NOTIFICATIONS_PATH = API_BASE_PATH + "/notifications";
    public static final String REELS_PATH = API_BASE_PATH + "/reels";
    public static final String STORIES_PATH = API_BASE_PATH + "/stories";
    public static final String CHATS_PATH = API_BASE_PATH + "/chats";

    // Mensajes de error comunes
    public static final String INVALID_CREDENTIALS = "Email o contraseña incorrectos";
    public static final String USER_NOT_FOUND = "Usuario no encontrado";
    public static final String POST_NOT_FOUND = "Post no encontrado";
    public static final String COMMENT_NOT_FOUND = "Comentario no encontrado";
    public static final String UNAUTHORIZED_ACTION = "No tienes autorización para realizar esta acción";
    public static final String RESOURCE_ALREADY_EXISTS = "El recurso ya existe";

    // Cloudinary folders
    public static final String CLOUDINARY_POSTS_FOLDER = "social-media/posts";
    public static final String CLOUDINARY_PROFILES_FOLDER = "social-media/profiles";
    public static final String CLOUDINARY_STORIES_FOLDER = "social-media/stories";
    public static final String CLOUDINARY_REELS_FOLDER = "social-media/reels";
}
