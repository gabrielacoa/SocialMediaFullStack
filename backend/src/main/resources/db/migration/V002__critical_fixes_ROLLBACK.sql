-- ============================================================================
-- ROLLBACK V002: Revertir Correcciones Críticas
-- Fecha: 2025-11-20
-- IMPORTANTE: Ejecutar SOLO si la migración V002 falla
-- ============================================================================

-- 9. ELIMINAR TABLA users_saved_post
-- ============================================================================
DROP TABLE IF EXISTS users_saved_post;

-- 8. ELIMINAR TABLA user_followings
-- ============================================================================
DROP TABLE IF EXISTS user_followings;

-- 7. ELIMINAR TABLA user_followers
-- ============================================================================
DROP TABLE IF EXISTS user_followers;

-- 6. REVERTIR COLUMNA en notifications: receiver_id → user_id
-- ============================================================================
ALTER TABLE notifications
CHANGE COLUMN receiver_id user_id BIGINT NOT NULL;

-- 5. REVERTIR NOMBRE DE TABLA: notifications → notification
-- ============================================================================
RENAME TABLE notifications TO notification;

-- 4. ELIMINAR COLUMNA created_at de comments
-- ============================================================================
ALTER TABLE comments
DROP COLUMN IF EXISTS created_at;

-- 3. ELIMINAR COLUMNA liked_at de post_liked
-- ============================================================================
ALTER TABLE post_liked
DROP COLUMN IF EXISTS liked_at;

-- 2. ELIMINAR CONSTRAINT UNIQUE de post_liked
-- ============================================================================
ALTER TABLE post_liked
DROP INDEX IF EXISTS uk_post_liked_user_post;

-- 1. REVERTIR NOMBRE DE TABLA: post_liked → likes
-- ============================================================================
RENAME TABLE post_liked TO likes;

-- ============================================================================
-- FIN DE ROLLBACK V002
-- ============================================================================
