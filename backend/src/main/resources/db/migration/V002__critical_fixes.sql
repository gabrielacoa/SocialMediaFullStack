-- ============================================================================
-- MIGRACIÓN V002: Correcciones Críticas de Base de Datos
-- Fecha: 2025-11-20
-- Descripción: Corrige problemas críticos detectados en auditoría
-- ============================================================================

-- 1. RENOMBRAR TABLA: likes → post_liked
-- ============================================================================
RENAME TABLE likes TO post_liked;

-- 2. AGREGAR CONSTRAINT UNIQUE para prevenir likes duplicados
-- ============================================================================
ALTER TABLE post_liked
ADD CONSTRAINT uk_post_liked_user_post UNIQUE (user_id, post_id);

-- 3. AGREGAR TIMESTAMP a post_liked
-- ============================================================================
ALTER TABLE post_liked
ADD COLUMN liked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- 4. AGREGAR TIMESTAMP a comments
-- ============================================================================
ALTER TABLE comments
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- 5. RENOMBRAR TABLA: notification → notifications (consistencia)
-- ============================================================================
RENAME TABLE notification TO notifications;

-- 6. RENOMBRAR COLUMNA en notifications: user_id → receiver_id
-- ============================================================================
ALTER TABLE notifications
CHANGE COLUMN user_id receiver_id BIGINT NOT NULL;

-- 7. CREAR TABLA user_followers (relación many-to-many)
-- ============================================================================
CREATE TABLE IF NOT EXISTS user_followers (
    user_id BIGINT NOT NULL,
    follower_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, follower_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_follower_id (follower_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. CREAR TABLA user_followings (relación many-to-many)
-- ============================================================================
CREATE TABLE IF NOT EXISTS user_followings (
    user_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, following_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_following_id (following_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. CREAR TABLA users_saved_post (relación many-to-many)
-- ============================================================================
CREATE TABLE IF NOT EXISTS users_saved_post (
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    saved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    INDEX idx_post_id (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- FIN DE MIGRACIÓN V002
-- ============================================================================
