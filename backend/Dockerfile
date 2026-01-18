# Usar una imagen oficial de Maven para compilar el proyecto
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app
# Copiar archivos POM y de origen
COPY pom.xml ./
COPY src ./src
# Compilar y empaquetar la aplicación
RUN mvn clean package -DskipTests

# Usar una imagen base de OpenJDK para la aplicación
FROM openjdk:17-jdk-slim

# Información sobre el mantenedor
LABEL maintainer="Tu Nombre <tu.email@ejemplo.com>"

# Configurar variables de entorno por defecto
ENV PORT=8080

# Crear un directorio para la aplicación
WORKDIR /app

# Copiar el JAR desde la etapa de compilación
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto de la aplicación
EXPOSE ${PORT}

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]
