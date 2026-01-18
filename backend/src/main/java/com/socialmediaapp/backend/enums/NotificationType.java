package com.socialmediaapp.backend.enums;

/**
 * Tipos de notificaciones en la aplicación.
 */
public enum NotificationType {
    LIKE("like"),
    COMMENT("comment"),
    FOLLOW("follow"),
    MENTION("mention"),
    REPLY("reply"),
    STORY_LIKE("story_like"),
    REEL_LIKE("reel_like");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static NotificationType fromValue(String value) {
        for (NotificationType type : NotificationType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid notification type: " + value);
    }
}
