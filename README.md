# 📱 Social Media App - Aplicación Fullstack

![Insignias](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![Insignias](https://img.shields.io/badge/Redux-593D88?style=for-the-badge&logo=redux&logoColor=white)
![Insignias](https://img.shields.io/badge/Material--UI-0081CB?style=for-the-badge&logo=material-ui&logoColor=white)
![Insignias](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)
![Insignias](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![Insignias](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)

## 📝 Descripción

Aplicación de red social moderna y completa con funcionalidades similares a Instagram, desarrollada con React y Spring Boot. Este proyecto fullstack incluye características como feed de noticias, chat en tiempo real, sistema de stories, perfil de usuario, y más.

![Demo Screenshot](screenshots/main-feed.png)

## 🎥 Demo en video

[Ver demostración en YouTube](https://youtube.com/link-a-tu-video) - *Haz clic para ver una demostración completa de la aplicación*

## ✨ Características

- **🔐 Autenticación completa**: Registro, inicio de sesión, recuperación de contraseña
- **📰 Feed de publicaciones**: Ver, crear, comentar y dar me gusta a publicaciones
- **💬 Chat en tiempo real**: Comunicación instantánea entre usuarios
- **🖼️ Stories**: Compartir momentos con imágenes que duran 24 horas
- **👤 Perfil de usuario**: Personalización y visualización del perfil
- **🔔 Notificaciones en tiempo real**: Alertas sobre interacciones
- **⚙️ Configuraciones avanzadas**: Privacidad, cuenta y preferencias
- **📱 Diseño responsivo**: Adaptable a dispositivos móviles y escritorio

## 🛠️ Tecnologías

### Frontend
- **React**: Biblioteca para construir interfaces de usuario
- **Redux Toolkit**: Manejo del estado de la aplicación
- **Material UI**: Componentes de diseño modernos
- **Socket.io-client**: Comunicación en tiempo real
- **Axios**: Cliente HTTP para peticiones a la API

### Backend
- **Spring Boot**: Framework para desarrollo de aplicaciones Java
- **Spring Security + JWT**: Autenticación y autorización
- **Spring Data JPA**: Acceso a datos con Hibernate
- **WebSocket**: Comunicación bidireccional en tiempo real
- **MySQL**: Base de datos relacional

### DevOps
- **Docker**: Contenedorización de la aplicación
- **Maven**: Gestión de dependencias para Java

## 📋 Requisitos previos

- Java 17 o superior
- Node.js 16 o superior
- MySQL 8.0
- Docker y Docker Compose (opcional)

## 🚀 Instalación y ejecución

### Método 1: Usando Docker (recomendado)

```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/SocialMediaFullStack.git
cd SocialMediaFullStack

# Iniciar los servicios con Docker Compose
docker-compose up
```

### Método 2: Instalación manual

#### Backend
```bash
cd backend

# Construir el proyecto con Maven
./mvnw clean package -DskipTests

# Ejecutar la aplicación Spring Boot
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

#### Frontend
```bash
cd frontend

# Instalar dependencias
npm install

# Iniciar la aplicación React
npm start
```

## 📚 Estructura del proyecto

```
SocialMediaFullStack/
├── backend/            # Código del servidor Spring Boot
│   ├── src/main/java/  # Código fuente Java
│   │   └── com/socialmediaapp/backend/
│   │       ├── config/       # Configuraciones
│   │       ├── controller/   # Controladores REST
│   │       ├── model/        # Entidades
│   │       ├── repository/   # Repositorios JPA
│   │       ├── security/     # Configuración de seguridad
│   │       └── service/      # Lógica de negocio
│   └── src/main/resources/   # Archivos de configuración
├── frontend/           # Código de la interfaz React
│   ├── public/         # Archivos estáticos
│   └── src/            # Código fuente JavaScript
│       ├── components/ # Componentes reutilizables
│       ├── pages/      # Componentes de página
│       ├── services/   # Servicios de API
│       └── store/      # Estado global Redux
```

## 📸 Capturas de pantalla

<div style="display: flex; flex-wrap: wrap; gap: 10px; margin-bottom: 20px;">
    <img src="screenshots/login.png" width="200" alt="Pantalla de login">
    <img src="screenshots/feed.png" width="200" alt="Feed de noticias">
    <img src="screenshots/chat.png" width="200" alt="Chat">
    <img src="screenshots/profile.png" width="200" alt="Perfil">
</div>

## 💭 Decisiones técnicas

- **Separación frontend/backend**: Arquitectura que permite escalabilidad independiente
- **WebSockets**: Elegidos para funciones en tiempo real como chat y notificaciones
- **JWT**: Implementado para autenticación stateless y segura
- **Redux**: Utilizado para un manejo de estado predecible y centralizado

## 🔄 API Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | /api/auth/register | Registro de usuario |
| POST | /api/auth/login | Autenticación |
| GET | /api/posts | Obtener publicaciones |
| POST | /api/posts | Crear publicación |
| GET | /api/users/{id} | Obtener perfil de usuario |
| POST | /api/stories | Crear story |

## 💡 Funcionalidades a futuro

- Llamadas de vídeo en el chat
- Sistema de hashtags y búsqueda avanzada
- Modo oscuro/claro personalizable
- Integración con redes sociales externas

## 👤 Autor

**Tu Nombre**
- GitHub: [@tu-usuario](https://github.com/tu-usuario)
- LinkedIn: [Tu Perfil](https://linkedin.com/in/tu-perfil)

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

---

⭐️ Si te gusta este proyecto, ¡no dudes en darle una estrella!
