package com.socialmediaapp.backend.enums;

/**
 * Tipos de medios que se pueden subir en la aplicación.
 */
public enum MediaType {
    IMAGE("image"),
    VIDEO("video"),
    GIF("gif");

    private final String value;

    MediaType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MediaType fromValue(String value) {
        for (MediaType type : MediaType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid media type: " + value);
    }

    public static MediaType fromMimeType(String mimeType) {
        if (mimeType == null) {
            throw new IllegalArgumentException("MIME type cannot be null");
        }

        if (mimeType.startsWith("image/gif")) {
            return GIF;
        } else if (mimeType.startsWith("image/")) {
            return IMAGE;
        } else if (mimeType.startsWith("video/")) {
            return VIDEO;
        }

        throw new IllegalArgumentException("Unsupported MIME type: " + mimeType);
    }
}
