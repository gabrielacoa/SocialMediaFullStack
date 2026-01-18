-- ============================================================================
-- MIGRACIÓN V007 - Datos de ejemplo para desarrollo
-- Fecha: 2025-11-27
-- Descripción: Datos de prueba para desarrollo local (NO ejecutar en producción)
-- NOTA: Este archivo puede ser eliminado o comentado en ambientes de producción
-- ============================================================================

-- IMPORTANTE: Solo ejecutar en ambiente de desarrollo
-- Para producción, renombrar este archivo agregando .disabled al final

-- Usuario de prueba 1
-- Password: Test123!
INSERT INTO users (username, email, password, bio, profile_picture_url) VALUES
('johndoe', 'john@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'Photographer & Travel enthusiast 📷✈️', 'https://i.pravatar.cc/300?img=12');

-- Usuario de prueba 2
-- Password: Test123!
INSERT INTO users (username, email, password, bio, profile_picture_url) VALUES
('janedoe', 'jane@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'Designer | Coffee lover ☕', 'https://i.pravatar.cc/300?img=45');

-- Usuario de prueba 3
-- Password: Test123!
INSERT INTO users (username, email, password, bio, profile_picture_url) VALUES
('alexsmith', 'alex@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'Software Engineer 💻', 'https://i.pravatar.cc/300?img=33');

-- Posts de ejemplo
INSERT INTO posts (user_id, content, image_url) VALUES
(1, '¡Increíble atardecer en la playa! 🌅', 'https://picsum.photos/800/600?random=1'),
(1, 'Mi primer post en esta red social', NULL),
(2, 'Nuevo diseño terminado 🎨', 'https://picsum.photos/800/600?random=2'),
(3, 'Aprendiendo Spring Boot', NULL);

-- Relaciones de seguidores
INSERT INTO user_followers (user_id, follower_id) VALUES
(1, 2), -- jane sigue a john
(1, 3), -- alex sigue a john
(2, 1), -- john sigue a jane
(3, 1); -- john sigue a alex

INSERT INTO user_followings (user_id, following_id) VALUES
(2, 1), -- jane sigue a john
(3, 1), -- alex sigue a john
(1, 2), -- john sigue a jane
(1, 3); -- john sigue a alex

-- Likes en posts
INSERT INTO post_liked (user_id, post_id) VALUES
(2, 1), -- jane le dio like al post 1 de john
(3, 1), -- alex le dio like al post 1 de john
(1, 3); -- john le dio like al post 3 de jane

-- Comentarios
INSERT INTO comments (post_id, user_id, content) VALUES
(1, 2, '¡Qué hermosa foto!'),
(1, 3, 'Me encanta 👍'),
(3, 1, 'Buen trabajo!');

-- Notificaciones
INSERT INTO notifications (receiver_id, sender_id, message, type, link) VALUES
(1, 2, 'le dio like a tu publicación', 'LIKE', '/posts/1'),
(1, 3, 'comentó tu publicación', 'COMMENT', '/posts/1'),
(2, 1, 'comentó tu publicación', 'COMMENT', '/posts/3');

-- Chat de ejemplo
INSERT INTO chats (user1_id, user2_id, last_message_at) VALUES
(1, 2, NOW()),
(1, 3, NOW());

-- Mensajes de ejemplo
INSERT INTO messages (sender_id, content, chat_id, is_read) VALUES
(1, 'Hola Jane! ¿Cómo estás?', 1, TRUE),
(2, 'Hola John! Todo bien, gracias. ¿Y tú?', 1, TRUE),
(1, 'Muy bien, trabajando en un nuevo proyecto', 1, FALSE),
(1, 'Hey Alex!', 2, TRUE),
(3, '¡Hola! ¿Qué tal?', 2, FALSE);

-- Stories de ejemplo (expiran en 24h)
INSERT INTO stories (user_id, media_url, caption, expires_at) VALUES
(1, 'https://picsum.photos/400/700?random=10', 'Un día increíble', DATE_ADD(NOW(), INTERVAL 24 HOUR)),
(2, 'https://picsum.photos/400/700?random=11', 'Working on new designs', DATE_ADD(NOW(), INTERVAL 24 HOUR));

-- Reels de ejemplo
INSERT INTO reels (user_id, video_url, caption) VALUES
(1, 'https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4', 'Mi primer reel 🎥'),
(3, 'https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4', 'Coding session timelapse ⚡');

-- Posts guardados
INSERT INTO users_saved_post (user_id, post_id) VALUES
(2, 1),
(3, 3);
