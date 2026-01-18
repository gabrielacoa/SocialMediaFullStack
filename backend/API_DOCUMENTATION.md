# Documentación API - Social Media App

## Acceso a la Documentación Swagger

La API está completamente documentada con **OpenAPI 3.0** (Swagger). Una vez que el servidor esté corriendo, puedes acceder a la documentación interactiva en:

### Swagger UI (Interfaz Interactiva)
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON (Especificación completa)
```
http://localhost:8080/v3/api-docs
```

## Características de Swagger UI

La interfaz de Swagger te permite:

1. **Ver todos los endpoints disponibles** organizados por categorías
2. **Probar endpoints directamente** desde el navegador
3. **Ver ejemplos de request/response**
4. **Autenticarte con JWT** para probar endpoints protegidos
5. **Ver todos los modelos de datos** (DTOs) con sus campos y validaciones

## Cómo Usar la Documentación

### 1. Iniciar el Servidor

```bash
mvn spring-boot:run
```

O ejecutar el JAR compilado:
```bash
java -jar target/SocialMediaApp_backend-0.0.1-SNAPSHOT.jar
```

### 2. Abrir Swagger UI

Navega a `http://localhost:8080/swagger-ui.html` en tu navegador.

### 3. Autenticación con JWT

Para probar endpoints protegidos:

1. **Registrarte**: Usa el endpoint `POST /api/auth/register`
   - Clic en "Try it out"
   - Completa el formulario con tus datos
   - Clic en "Execute"
   - Copia el `token` de la respuesta

2. **Autenticarte en Swagger**:
   - Clic en el botón "Authorize" en la parte superior derecha
   - Pega el token en el campo (Swagger agregará "Bearer " automáticamente)
   - Clic en "Authorize"

3. **Ahora puedes probar cualquier endpoint protegido**

### 4. Probar Endpoints

Ejemplo con `POST /api/posts`:

1. Clic en el endpoint para expandirlo
2. Clic en "Try it out"
3. Edita el JSON de ejemplo con tus datos:
   ```json
   {
     "content": "Mi primer post!",
     "imageUrl": "https://example.com/image.jpg"
   }
   ```
4. Clic en "Execute"
5. Ver la respuesta en la sección "Server response"

## Categorías de Endpoints

### 🔐 Autenticación (`/api/auth`)
- **POST** `/register` - Registrar nuevo usuario
- **POST** `/login` - Iniciar sesión
- **PUT** `/change-password` - Cambiar contraseña
- **GET** `/validate` - Validar token JWT
- **POST** `/logout` - Cerrar sesión

### 👤 Usuarios (`/api/users`)
- **GET** `/{id}` - Obtener usuario por ID
- **GET** `/username/{username}` - Obtener usuario por username
- **GET** `/` - Listar todos los usuarios
- **PUT** `/{id}` - Actualizar perfil
- **DELETE** `/{id}` - Eliminar usuario

### 📝 Posts (`/api/posts`)
- **POST** `/` - Crear post
- **GET** `/{id}` - Obtener post por ID
- **GET** `/` - Listar todos los posts
- **GET** `/user/{userId}` - Posts de un usuario
- **PUT** `/{id}` - Actualizar post
- **DELETE** `/{id}` - Eliminar post

### 💬 Comentarios (`/api/comments`)
- **POST** `/` - Crear comentario
- **GET** `/post/{postId}` - Comentarios de un post
- **PUT** `/{id}` - Actualizar comentario
- **DELETE** `/{id}` - Eliminar comentario

### ❤️ Likes (`/api/likes`)
- **POST** `/post/{postId}` - Dar like a un post
- **DELETE** `/post/{postId}` - Quitar like
- **GET** `/post/{postId}` - Ver likes de un post
- **GET** `/user/{userId}` - Posts que le gustan a un usuario

### 💬 Chats y Mensajes (`/api/chats`)
- **POST** `/` - Crear chat
- **GET** `/{chatId}` - Obtener chat
- **GET** `/user/{userId}` - Chats de un usuario
- **POST** `/{chatId}/messages` - Enviar mensaje
- **GET** `/{chatId}/messages` - Ver mensajes
- **PUT** `/messages/{messageId}/read` - Marcar como leído
- **DELETE** `/{chatId}` - Eliminar chat

### 🎬 Reels (`/api/reels`)
- **POST** `/` - Crear reel
- **GET** `/{id}` - Obtener reel
- **GET** `/` - Listar todos los reels
- **GET** `/user/{userId}` - Reels de un usuario
- **DELETE** `/{id}` - Eliminar reel

### 📖 Stories (`/api/stories`)
- **POST** `/` - Crear story (24h)
- **GET** `/active` - Ver stories activas
- **GET** `/user/{userId}` - Stories activas de un usuario
- **DELETE** `/{id}` - Eliminar story

### 🔔 Notificaciones (`/api/notifications`)
- **GET** `/user/{userId}` - Notificaciones de un usuario
- **GET** `/unread/count/{userId}` - Contar no leídas
- **PUT** `/read/{id}` - Marcar como leída
- **PUT** `/read/all/{userId}` - Marcar todas como leídas
- **DELETE** `/{id}` - Eliminar notificación

## Modelos de Datos (DTOs)

Todos los modelos están documentados en Swagger con:
- Campos requeridos vs opcionales
- Tipos de datos
- Validaciones (min, max, pattern, etc.)
- Ejemplos de valores

### Ejemplos principales:

#### RegisterRequest
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

#### CreatePostRequest
```json
{
  "content": "¡Increíble atardecer! 🌅",
  "imageUrl": "https://cloudinary.com/image123.jpg"
}
```

#### SendMessageRequest
```json
{
  "content": "Hola! ¿Cómo estás?"
}
```

## Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| 200 | OK - Solicitud exitosa |
| 201 | Created - Recurso creado exitosamente |
| 204 | No Content - Eliminación exitosa |
| 400 | Bad Request - Datos inválidos |
| 401 | Unauthorized - No autenticado |
| 403 | Forbidden - No autorizado |
| 404 | Not Found - Recurso no encontrado |
| 409 | Conflict - Recurso duplicado |
| 429 | Too Many Requests - Rate limit excedido |

## Rate Limiting

La API implementa rate limiting para seguridad:
- **100 requests por minuto** por IP
- Si se excede, recibirás un error `429 Too Many Requests`

## Seguridad

- Todos los endpoints (excepto `/auth/login` y `/auth/register`) requieren autenticación JWT
- Los tokens JWT expiran después de 24 horas
- Las contraseñas se almacenan con BCrypt hash
- Implementa headers de seguridad (CSP, XSS Protection, etc.)

## Exportar Especificación OpenAPI

Para integración con otras herramientas (Postman, Insomnia, etc.):

1. Descarga el JSON:
   ```
   http://localhost:8080/v3/api-docs
   ```

2. Importa en tu herramienta favorita

## Variables de Entorno

Para configurar Swagger en diferentes ambientes, usa `application.properties`:

```properties
# Configuración OpenAPI
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true

# En producción, puedes deshabilitarlo:
# springdoc.swagger-ui.enabled=false
```

## Soporte

Para reportar problemas o sugerencias sobre la documentación:
- Email: contact@socialmediaapp.com
- GitHub Issues: https://github.com/socialmediaapp/backend/issues
