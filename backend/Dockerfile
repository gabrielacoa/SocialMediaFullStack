# Usa una imagen base de OpenJDK
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR de tu aplicación al contenedor
COPY target/tu-aplicacion.jar app.jar

# Expone el puerto en el que tu aplicación se ejecuta
EXPOSE 8080

# Comando para ejecutar tu aplicación
CMD ["java", "-jar", "app.jar"]
