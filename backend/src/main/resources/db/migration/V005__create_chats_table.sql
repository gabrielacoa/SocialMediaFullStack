-- ============================================================================
-- MIGRACIÓN V005 - Crear tabla chats
-- Fecha: 2025-11-27
-- Descripción: Tabla para almacenar conversaciones entre dos usuarios
-- ============================================================================

CREATE TABLE chats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id BIGINT NOT NULL COMMENT 'Primer usuario del chat',
    user2_id BIGINT NOT NULL COMMENT 'Segundo usuario del chat',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_message_at TIMESTAMP NULL COMMENT 'Fecha del último mensaje enviado',

    FOREIGN KEY (user1_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (user2_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Asegurar que no haya chats duplicados entre los mismos usuarios
    UNIQUE KEY uk_users_chat (LEAST(user1_id, user2_id), GREATEST(user1_id, user2_id)),

    INDEX idx_user1_id (user1_id),
    INDEX idx_user2_id (user2_id),
    INDEX idx_last_message_at (last_message_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Almacena conversaciones (chats) entre dos usuarios';
