-- ============================================================================
-- MIGRACIÓN V004 - Crear tabla stories
-- Fecha: 2025-11-27
-- Descripción: Tabla para almacenar stories (historias temporales de 24h tipo Instagram Stories)
-- ============================================================================

CREATE TABLE stories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    media_url VARCHAR(500) NOT NULL COMMENT 'URL de imagen o video en Cloudinary',
    caption VARCHAR(200) NULL COMMENT 'Texto de la story',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL COMMENT 'Fecha de expiración (24h desde created_at)',

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at DESC),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Almacena stories temporales (expiran en 24 horas)';

-- Índice compuesto para consultas de stories activas
CREATE INDEX idx_user_expires ON stories(user_id, expires_at);
