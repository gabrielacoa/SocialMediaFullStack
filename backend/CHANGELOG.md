# CHANGELOG - Social Media Full Stack Backend

Historia completa de cambios y mejoras del proyecto.

---

## [V004] - 2025-12-10

### 🧹 Limpieza de Código - Eliminación de Duplicados

**Tipo:** Mantenimiento de Código
**Estado:** ✅ Completado

#### Cambios Implementados

**Problema Detectado:**
- Existían dos modelos para la funcionalidad de "likes": `Like.java` y `PostLiked.java`
- `Like.java` era obsoleto (sin timestamp, sin unique constraint)
- `PostLiked.java` era el modelo activo utilizado por los servicios

**Solución:**
- ✅ Eliminado `Like.java` (modelo obsoleto)
- ✅ Eliminado `LikeRepository.java` (repositorio obsoleto)
- ✅ Mantenido `PostLiked.java` como único modelo de likes

**Archivos Eliminados:**
- `model/Like.java`
- `repository/LikeRepository.java`

**Archivos Activos:**
- `model/PostLiked.java` ✅
- `repository/PostLikedRepository.java` ✅
- `mapper/PostLikedMapper.java` ✅
- `service/LikeServiceImpl.java` ✅

#### Impacto

**Base de Datos:**
- **12 tablas totales** (9 principales + 3 intermedias)
- Tabla única de likes: `post_liked`

**Backend:**
- ✅ Reducción de 100 a 98 archivos Java
- ✅ Código más limpio y sin duplicados
- ✅ BUILD SUCCESS - Compilación exitosa

---

## [V003] - 2025-11-28

### 🎯 Arquitectura Senior + Tests + Documentación API

**Tipo:** Mejoras Mayores
**Estado:** ✅ Completado

#### Resumen

Refactorización completa del backend a nivel **senior** con:
- ✅ Migraciones SQL para nuevas features (Reels, Stories, Chat)
- ✅ Tests unitarios completos (38 test cases)
- ✅ Documentación API con Swagger/OpenAPI
- ✅ Actualización de controllers para usar mappers
- ✅ Compilación exitosa (100 archivos Java)

#### Cambios Implementados

##### 1. Scripts de Migración SQL (7 archivos)

**Migraciones versionadas con Flyway:**

- `V002__alter_users_add_bio_and_profile_picture.sql`
  - Agrega campos `bio` y `profile_picture` a users
  - Índice en bio para búsquedas

- `V003__create_reels_table.sql`
  - Tabla para videos cortos tipo Instagram Reels
  - Campos: video_url, caption, created_at
  - FK a users con CASCADE DELETE

- `V004__create_stories_table.sql`
  - Tabla para stories temporales (expiran en 24 horas)
  - Campos: media_url, caption, created_at, expires_at
  - Índices en user_id, created_at, expires_at

- `V005__create_chats_table.sql`
  - Tabla para conversaciones entre dos usuarios
  - Unique constraint para prevenir chats duplicados
  - Índice en last_message_at

- `V006__alter_messages_add_chat_support.sql`
  - Agrega campo chat_id a messages
  - FK a chats con CASCADE DELETE
  - Índices para performance

- `V007__sample_data_development.sql`
  - Datos de ejemplo para desarrollo
  - 3 usuarios, posts, comentarios, likes, mensajes

##### 2. Tests Unitarios (4 archivos, 38 tests)

**Tests con Mockito:**

- `ReelServiceTest.java` (9 tests)
  - Create, get, list, delete reels
  - Validación de excepciones

- `StoryServiceTest.java` (8 tests)
  - Create stories con expiración 24h
  - Filtrado de stories activas
  - Exclusión de expiradas

- `ChatServiceTest.java` (11 tests)
  - Crear chats entre usuarios
  - Enviar/recibir mensajes
  - Marcar como leído
  - Contar no leídos

- `AuthControllerTest.java` (10 tests de integración)
  - Registro de usuarios
  - Login con email/username
  - Cambio de contraseña
  - Validación de datos

**Cobertura:**
- ✅ 38 test cases totales
- ✅ Patrón AAA (Arrange-Act-Assert)
- ✅ Mockito para dependencias
- ✅ @DisplayName descriptivos

##### 3. Documentación API con Swagger/OpenAPI

**Archivos Creados:**

- `OpenApiConfig.java`
  - Configuración de OpenAPI 3.0
  - Autenticación JWT integrada
  - Servidores: desarrollo y producción

- `API_DOCUMENTATION.md`
  - Guía completa de uso de la API
  - Lista de endpoints por categoría
  - Ejemplos de request/response
  - Instrucciones de autenticación

**Dependencias agregadas:**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

**Endpoints Swagger:**
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

##### 4. Actualización de Controllers (Mappers)

**Controllers actualizados para usar DTOs:**

- PostController + PostService + PostServiceImpl
- CommentController + CommentService + CommentServiceImpl
- UserController + UserService + UserServiceImpl
- NotificationController + NotificationService + NotificationServiceImpl
- LikeController + LikeService + LikeServiceImpl

**Mappers creados:**
- PostLikedMapper.java (nuevo)

**Repositorios corregidos:**
- PostLikedRepository.java - Agregado método `findByPostIdAndUserId`

**Modelos corregidos:**
- User.java - Agregados campos `bio` y `profilePicture`

#### Endpoints Agregados

##### Reels (`/api/reels`)
- POST `/` - Crear reel
- GET `/{id}` - Obtener reel
- GET `/` - Listar reels
- GET `/user/{userId}` - Reels de usuario
- DELETE `/{id}` - Eliminar reel

##### Stories (`/api/stories`)
- POST `/` - Crear story (24h)
- GET `/active` - Stories activas
- GET `/user/{userId}` - Stories de usuario
- DELETE `/{id}` - Eliminar story

##### Chats (`/api/chats`)
- POST `/` - Crear chat
- GET `/{chatId}` - Obtener chat
- GET `/user/{userId}` - Chats de usuario
- POST `/{chatId}/messages` - Enviar mensaje
- GET `/{chatId}/messages` - Ver mensajes
- PUT `/messages/{messageId}/read` - Marcar leído
- GET `/{chatId}/unread` - Contar no leídos
- DELETE `/{chatId}` - Eliminar chat

#### Impacto

**Base de Datos:**
- ✅ 4 tablas nuevas: reels, stories, chats, (post_liked ya existía)
- ✅ 2 campos nuevos en users: bio, profile_picture
- ✅ 1 campo nuevo en messages: chat_id
- ✅ 11 índices nuevos para performance
- ✅ Datos de prueba para desarrollo

**Backend:**
- ✅ 4 archivos de tests: 38 test cases
- ✅ 2 configuraciones: OpenApiConfig, SecurityConfig
- ✅ 1 mapper nuevo: PostLikedMapper
- ✅ 5 controllers actualizados con DTOs
- ✅ 5 services actualizados con mappers
- ✅ BUILD SUCCESS: 100 archivos Java

**Documentación:**
- ✅ Swagger UI interactivo
- ✅ OpenAPI 3.0 JSON
- ✅ AuthController documentado
- ✅ Guía de uso completa

---

## [V002] - 2025-11-20

### 🔧 Correcciones Críticas de Base de Datos

**Tipo:** Correcciones Críticas
**Estado:** ✅ Completado

#### Resumen

Corrección de 5 problemas críticos detectados en auditoría de base de datos según especificaciones de `.cursorrules`.

#### Cambios Implementados

##### 1. Renombrar `Like` → `PostLiked`

**Problema:**
- Tabla `likes` con diseño incorrecto permitía likes duplicados
- Nombre genérico "Like" no refleja la relación many-to-many

**Solución:**
- Entidad: `Like.java` → `PostLiked.java`
- Tabla: `likes` → `post_liked`
- Constraint: Agregado `UNIQUE(user_id, post_id)`
- Campo nuevo: `likedAt` (timestamp)

**SQL:**
```sql
RENAME TABLE likes TO post_liked;
ALTER TABLE post_liked ADD CONSTRAINT uk_post_liked_user_post UNIQUE (user_id, post_id);
ALTER TABLE post_liked ADD COLUMN liked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
```

##### 2. Agregar timestamp a `Comment`

**Problema:**
- Entidad `Comment` no tenía campo `createdAt`
- Imposible ordenar comentarios cronológicamente

**Solución:**
- Agregado campo `createdAt` con `@PrePersist`
- Tipo: `Date` con `@Temporal(TemporalType.TIMESTAMP)`

**SQL:**
```sql
ALTER TABLE comments ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
```

##### 3. Renombrar `Notification.user` → `receiver`

**Problema:**
- Campo `user` era ambiguo
- Inconsistente con `Message.java` que usa `receiver/sender`

**Solución:**
- Campo `user` → `receiver`
- Join column `user_id` → `receiver_id`
- Agregado `fetch = FetchType.LAZY`

**SQL:**
```sql
ALTER TABLE notifications CHANGE COLUMN user_id receiver_id BIGINT NOT NULL;
```

##### 4. Tabla `notification` → `notifications` (plural)

**Problema:**
- Tabla `notification` en singular
- Inconsistente con otras tablas (users, posts, comments)

**Solución:**
- Agregado `@Table(name = "notifications")`

**SQL:**
```sql
RENAME TABLE notification TO notifications;
```

##### 5. Relaciones `followers`, `following`, `savedPosts` en User

**Problema:**
- User no tenía relaciones para seguir/ser seguido
- Funcionalidad básica de red social faltante

**Solución:**
- Agregado `Set<User> followers` con tabla `user_followers`
- Agregado `Set<User> following` con tabla `user_followings`
- Agregado `Set<Post> savedPosts` con tabla `users_saved_post`

**SQL:**
```sql
CREATE TABLE user_followers (
    user_id BIGINT NOT NULL,
    follower_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, follower_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE user_followings (
    user_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, following_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE users_saved_post (
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    saved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);
```

#### Impacto

**Backend:**
- ✅ 1 entidad nueva: `PostLiked`
- ✅ 1 repository nuevo: `PostLikedRepository`
- ✅ 4 entidades modificadas: User, Post, Comment, Notification
- ✅ 3 servicios actualizados: LikeService, LikeServiceImpl
- ✅ 1 DTO nuevo: `PostLikedDto`

**Base de Datos:**
- ✅ 2 tablas renombradas: `likes` → `post_liked`, `notification` → `notifications`
- ✅ 3 tablas nuevas: `user_followers`, `user_followings`, `users_saved_post`
- ✅ 1 constraint nuevo: `UNIQUE(user_id, post_id)` en `post_liked`
- ✅ 3 campos nuevos: `post_liked.liked_at`, `comments.created_at`

---

## [V001] - 2025-11-15

### 🚀 Estructura Inicial del Proyecto

**Tipo:** Configuración Inicial
**Estado:** ✅ Completado

#### Configuración Base

**Stack Tecnológico:**
- Java 17
- Spring Boot 3.x
- MySQL 8.x
- Maven
- Lombok
- Spring Security + JWT

**Entidades Iniciales:**
- User
- Post
- Comment
- Like (posteriormente refactorizado a PostLiked)
- Message
- Notification

**Configuraciones:**
- SecurityConfig con JWT
- WebConfig para CORS
- Base de datos MySQL
- Estructura MVC completa

---

## 📊 Estado Actual del Proyecto

### Base de Datos (12 tablas)

**Tablas Principales (9):**
1. `users` - Usuarios del sistema
2. `posts` - Publicaciones
3. `comments` - Comentarios en posts
4. `post_liked` - Likes en posts (con unique constraint)
5. `messages` - Mensajes de chat
6. `notifications` - Notificaciones
7. `reels` - Videos cortos
8. `stories` - Historias temporales 24h
9. `chats` - Conversaciones entre usuarios

**Tablas Intermedias ManyToMany (3):**
10. `user_followers` - Relación follower/seguidor
11. `user_followings` - Relación following/siguiendo
12. `users_saved_post` - Posts guardados por usuarios

### Backend

**Archivos Java:** 98 archivos
**Tests:** 38 test cases
**Compilación:** ✅ BUILD SUCCESS
**Documentación:** ✅ Swagger UI disponible

### Endpoints Disponibles

**Autenticación:**
- POST `/api/auth/register` - Registro
- POST `/api/auth/login` - Login
- PUT `/api/auth/change-password` - Cambiar contraseña

**Usuarios:**
- GET `/api/users/{id}` - Obtener usuario
- PUT `/api/users/{id}` - Actualizar perfil
- DELETE `/api/users/{id}` - Eliminar usuario

**Posts:**
- POST `/api/posts` - Crear post
- GET `/api/posts/{id}` - Obtener post
- GET `/api/posts` - Listar posts
- PUT `/api/posts/{id}` - Actualizar post
- DELETE `/api/posts/{id}` - Eliminar post

**Comentarios:**
- POST `/api/posts/{postId}/comments` - Crear comentario
- GET `/api/posts/{postId}/comments` - Listar comentarios
- DELETE `/api/comments/{id}` - Eliminar comentario

**Likes:**
- POST `/api/posts/{postId}/like` - Dar like
- DELETE `/api/posts/{postId}/like` - Quitar like
- GET `/api/posts/{postId}/likes` - Listar likes

**Reels:**
- POST `/api/reels` - Crear reel
- GET `/api/reels/{id}` - Obtener reel
- GET `/api/reels` - Listar reels
- DELETE `/api/reels/{id}` - Eliminar reel

**Stories:**
- POST `/api/stories` - Crear story
- GET `/api/stories/active` - Stories activas
- GET `/api/stories/user/{userId}` - Stories de usuario
- DELETE `/api/stories/{id}` - Eliminar story

**Chats:**
- POST `/api/chats` - Crear chat
- GET `/api/chats/{chatId}` - Obtener chat
- POST `/api/chats/{chatId}/messages` - Enviar mensaje
- GET `/api/chats/{chatId}/messages` - Ver mensajes
- PUT `/api/messages/{messageId}/read` - Marcar leído
- DELETE `/api/chats/{chatId}` - Eliminar chat

**Notificaciones:**
- GET `/api/notifications` - Listar notificaciones
- PUT `/api/notifications/{id}/read` - Marcar como leída

---

## 🎯 Próximos Pasos Sugeridos

### Frontend Integration
- [ ] Conectar React con endpoints Swagger
- [ ] Implementar autenticación JWT en frontend
- [ ] UI para Reels, Stories y Chat

### Features Avanzadas
- [ ] WebSockets para chat en tiempo real
- [ ] Notificaciones push
- [ ] Sistema de menciones (@username)
- [ ] Hashtags y búsqueda avanzada

### DevOps
- [ ] Docker Compose para desarrollo
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Deploy a producción (AWS/Heroku)

### Performance
- [ ] Redis para caché
- [ ] Paginación en todos los endpoints
- [ ] Compresión de imágenes con Cloudinary

### Security
- [ ] Refresh tokens
- [ ] OAuth2 (Google, Facebook)
- [ ] 2FA (Two-Factor Authentication)

---

## 📞 Recursos

**Documentación API:**
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

**Desarrollo:**
```bash
# Compilar proyecto
mvn clean compile -DskipTests

# Ejecutar tests
mvn test

# Iniciar aplicación
mvn spring-boot:run

# Crear build
mvn clean package
```

---

**Proyecto mantenido por:** Gabriela
**Última actualización:** 2025-12-10
**Versión actual:** V004

## [V005] - 2026-01-17

### 🚀 Infraestructura & CI/CD - Preparación para Producción
**Tipo:** DevOps / Deployment
**Estado:** 🔄 En Progreso

#### Objetivos de la versión:
- ✅ Configuración de `.cursorrules` para desarrollo de alta velocidad (Vibe Coding).
- 🔄 Dockerización completa de la arquitectura (Spring Boot + React).
- 🔄 Despliegue en **Render** (Backend) y **Vercel** (Frontend).
- 🔄 Sincronización de repositorio local con GitHub.

_ _ _
