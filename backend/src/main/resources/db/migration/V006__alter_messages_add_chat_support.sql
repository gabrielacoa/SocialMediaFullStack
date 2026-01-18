-- ============================================================================
-- MIGRACIÓN V006 - Modificar messages para soportar chats
-- Fecha: 2025-11-27
-- Descripción: Actualiza la tabla messages para asociarla con chats en lugar de receiver_id directo
-- ============================================================================

-- Paso 1: Agregar columna chat_id (nullable temporalmente para permitir migración)
ALTER TABLE messages
ADD COLUMN chat_id BIGINT NULL COMMENT 'ID del chat al que pertenece el mensaje',
ADD CONSTRAINT fk_messages_chat FOREIGN KEY (chat_id) REFERENCES chats(id) ON DELETE CASCADE;

-- Paso 2: Crear índice para mejorar rendimiento de queries
CREATE INDEX idx_messages_chat_id ON messages(chat_id);
CREATE INDEX idx_messages_chat_sent ON messages(chat_id, sent_at);

-- NOTA IMPORTANTE:
-- Si hay datos existentes en la tabla messages con receiver_id, se debe ejecutar un script
-- de migración de datos para crear los chats correspondientes y actualizar chat_id.
-- Por ahora dejamos receiver_id para compatibilidad.
--
-- Para producción, después de migrar datos, ejecutar:
-- ALTER TABLE messages DROP FOREIGN KEY messages_ibfk_2;
-- ALTER TABLE messages DROP COLUMN receiver_id;
-- ALTER TABLE messages MODIFY COLUMN chat_id BIGINT NOT NULL;
