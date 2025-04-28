# Utilizamos la imagen de OpenJDK para crear el contenedor
FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR de la aplicación (de la carpeta target)
COPY target/KartingRM-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto donde la aplicación escuchará (por defecto, Spring Boot usa el puerto 8080)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
