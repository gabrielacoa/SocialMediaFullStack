-- ============================================================================
-- MIGRACIÓN V003 - Crear tabla reels
-- Fecha: 2025-11-27
-- Descripción: Tabla para almacenar reels (videos cortos tipo Instagram Reels)
-- ============================================================================

CREATE TABLE reels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    video_url VARCHAR(500) NOT NULL COMMENT 'URL del video en Cloudinary',
    caption VARCHAR(500) NULL COMMENT 'Descripción del reel',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Almacena reels (videos cortos) de usuarios';
