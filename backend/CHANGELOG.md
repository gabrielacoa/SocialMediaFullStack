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

**Archivos Java:** ~100 archivos
**Tests:** 38 test cases
**Compilación:** ✅ BUILD SUCCESS (con -Dmaven.test.skip)
**Documentación:** ✅ Swagger UI disponible
**Integración Frontend:** ✅ React conectado

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
- POST `/api/likes/post/{postId}` - Dar like
- DELETE `/api/likes/post/{postId}` - Quitar like
- GET `/api/posts/{postId}/likes` - Listar likes

**Saved Posts:**
- POST `/api/saved/post/{postId}` - Guardar post
- DELETE `/api/saved/post/{postId}` - Quitar de guardados
- GET `/api/saved/post/{postId}` - Verificar si está guardado

**Upload:**
- POST `/api/upload/image` - Subir imagen a Cloudinary

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
- [x] Conectar React con endpoints del backend
- [x] Implementar autenticación JWT en frontend
- [x] Integración de likes, comentarios y guardar posts
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
- [x] Integración con Cloudinary para imágenes

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
**Última actualización:** 2026-02-01
**Versión actual:** V006

## [V006] - 2026-02-01

### 🔧 Integración Frontend-Backend & Correcciones Críticas

**Tipo:** Bug Fixes / Integration
**Estado:** ✅ Completado

#### Resumen

Sesión de debugging intensiva para conectar el frontend React con el backend Spring Boot. Se corrigieron múltiples problemas de integración, CORS, autenticación JWT y funcionalidades sociales (likes, comentarios, guardar posts).

#### Cambios Implementados

##### 1. Configuración CORS en Spring Security

**Problema:**
- Frontend recibía error 403 Forbidden en todas las peticiones API
- Spring Security bloqueaba requests cross-origin

**Solución:**
- Creado `corsConfigurationSource()` bean en `SecurityConfig.java`
- Configurados headers permitidos: Authorization, Content-Type, Accept, Origin, X-Requested-With
- Habilitado `allowCredentials` para cookies/JWT
- Integrado con `http.cors(cors -> cors.configurationSource(corsConfigurationSource()))`

##### 2. PostDto y PostMapper - Información de Usuario

**Problema:**
- Error: "Cannot read properties of undefined (reading 'avatar')"
- PostDto no incluía información del usuario (avatar, username)

**Solución:**
- Expandido `PostDto.java` con clase interna `UserSummary` (id, username, avatar, name)
- Agregados campos: likesCount, commentsCount, liked, saved, comments
- Actualizado `PostMapper.java` para mapear todos los campos incluyendo user summary

##### 3. Mapeo de Campos Frontend

**Problema:**
- Posts se publicaban vacíos (sin contenido ni imagen)
- Frontend usaba `post.description`, backend retornaba `post.content`

**Solución:**
- Corregido `PostCard.js`: `post.description` → `post.content`
- Corregido contador: `post.likeCount` → `post.likesCount`

##### 4. Integración Cloudinary para Imágenes

**Problema:**
- Imágenes no se subían al crear posts

**Solución:**
- Creado `UploadController.java` con endpoint `POST /api/upload/image`
- Configuradas credenciales Cloudinary en `application.properties`
- Actualizado `postSlice.js` para subir imagen primero, luego crear post con URL

##### 5. Extensión de Expiración JWT

**Problema:**
- Token JWT expiraba en 15 minutos, sesión se cerraba constantemente

**Solución:**
- Cambiado `JWT_EXPIRATION` de 900000ms (15 min) a 86400000ms (24 horas)

##### 6. Funcionalidad de Likes

**Problema:**
- Like no funcionaba, contador siempre mostraba 0
- Endpoints frontend/backend no coincidían

**Solución:**
- Frontend: `/posts/${postId}/like` → `/likes/post/${postId}`
- Actualizado `LikeController.java` para obtener userId del JWT en lugar de header
- Agregado método `getAuthenticatedUserId()` usando SecurityContext

##### 7. Funcionalidad de Comentarios

**Problema:**
- Comentarios no se guardaban

**Solución:**
- Frontend: endpoint corregido a `POST /api/comments` con body `{ content, postId }`
- Actualizado `CommentController.java` para usar autenticación JWT

##### 8. Funcionalidad de Guardar Posts (Bookmarks)

**Problema:**
- No existía endpoint para guardar/quitar posts guardados
- StackOverflowError al intentar guardar (referencias circulares)

**Solución:**
- Creado `SavedPostController.java` con endpoints:
  - `POST /api/saved/post/{postId}` - Guardar post
  - `DELETE /api/saved/post/{postId}` - Quitar de guardados
  - `GET /api/saved/post/{postId}` - Verificar si está guardado
- Actualizado `postSlice.js` con thunks `savePost` y `unsavePost`

##### 9. Fix StackOverflowError en Entidades JPA

**Problema:**
- Error de recursión infinita al guardar posts
- Lombok @Data generaba hashCode incluyendo relaciones bidireccionales
- User → savedPosts → Post → user → ... (loop infinito)

**Solución:**
- Agregado `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` a `User.java` y `Post.java`
- Marcado solo campo `id` con `@EqualsAndHashCode.Include`
- Evita recursión en equals/hashCode manteniendo funcionalidad de colecciones

##### 10. Corrección Layout MainLayout

**Problema:**
- Contenido central se escondía detrás del sidebar

**Solución:**
- Sidebar: agregado `position: fixed`, `height: 100vh`, `left: 0`, `top: 0`
- Main content: agregado `width: calc(100% - 240px)` en desktop

#### Archivos Modificados

**Backend:**
- `SecurityConfig.java` - CORS configuration
- `PostDto.java` - Expanded with user info and counts
- `PostMapper.java` - Full mapping implementation
- `LikeController.java` - JWT authentication
- `CommentController.java` - JWT authentication
- `PostController.java` - JWT authentication
- `User.java` - EqualsAndHashCode fix
- `Post.java` - EqualsAndHashCode fix
- `application.properties` - Cloudinary config

**Backend (Nuevos):**
- `UploadController.java` - Image upload endpoint
- `SavedPostController.java` - Bookmark functionality

**Frontend:**
- `postSlice.js` - Updated endpoints and thunks
- `PostCard.js` - Fixed field mappings, delete functionality
- `MainLayout.js` - Fixed sidebar/content layout

#### Impacto

**Funcionalidades Verificadas:**
- ✅ Login/Registro funcionando
- ✅ Crear posts con imagen y texto
- ✅ Ver feed de posts
- ✅ Like/Unlike posts (contador actualiza)
- ✅ Comentar en posts
- ✅ Guardar/Quitar posts guardados
- ✅ Eliminar posts propios
- ✅ Navegación entre páginas

**Endpoints Actualizados:**
- `POST /api/upload/image` - Subir imagen a Cloudinary
- `POST /api/likes/post/{postId}` - Dar like (JWT auth)
- `DELETE /api/likes/post/{postId}` - Quitar like (JWT auth)
- `POST /api/saved/post/{postId}` - Guardar post
- `DELETE /api/saved/post/{postId}` - Quitar de guardados

---

## [V005] - 2026-01-17

### 🚀 Infraestructura & CI/CD - Preparación para Producción
**Tipo:** DevOps / Deployment
**Estado:** ✅ Completado

#### Objetivos de la versión:
- ✅ Configuración de `.cursorrules` para desarrollo de alta velocidad (Vibe Coding).
- ✅ Dockerización completa de la arquitectura (Spring Boot + React).
- ✅ Configuración para despliegue en **Render** (Backend) y **Vercel** (Frontend).
- ✅ Sincronización de repositorio local con GitHub.

---
