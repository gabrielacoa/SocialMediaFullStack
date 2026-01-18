-- ============================================================================
-- MIGRACIÓN V002 - Agregar columnas bio y profile_picture a users
-- Fecha: 2025-11-27
-- Descripción: Extiende la tabla users con campos para biografía y foto de perfil adicional
-- ============================================================================

ALTER TABLE users
ADD COLUMN bio VARCHAR(200) NULL COMMENT 'Biografía del usuario',
ADD COLUMN profile_picture VARCHAR(500) NULL COMMENT 'URL de foto de perfil adicional';

-- Índice para búsquedas de usuarios con bio
CREATE INDEX idx_users_bio ON users(bio(100));
