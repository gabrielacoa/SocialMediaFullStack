package com.socialmediaapp.backend.enums;

/**
 * Estados de un post en la aplicación.
 */
public enum PostStatus {
    ACTIVE("active"),
    ARCHIVED("archived"),
    REPORTED("reported"),
    DELETED("deleted"),
    DRAFT("draft");

    private final String value;

    PostStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PostStatus fromValue(String value) {
        for (PostStatus status : PostStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid post status: " + value);
    }
}
