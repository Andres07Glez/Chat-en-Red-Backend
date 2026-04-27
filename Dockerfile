# ETAPA 1: Compilación
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiamos archivos de configuración (Groovy usa build.gradle y settings.gradle)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew
# Descargamos dependencias primero para aprovechar el caché
RUN ./gradlew dependencies --no-daemon

# Copiamos el código y el certificado SSL
COPY src src
# Ejecutamos bootJar que es más específico para Spring Boot
RUN ./gradlew bootJar -x test --no-daemon

# ETAPA 2: Ejecución
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Ahora buscamos directamente el archivo app.jar que forzamos en el paso anterior
COPY --from=build /app/build/libs/app.jar app.jar

EXPOSE 8181

ENTRYPOINT ["java", "-jar", "app.jar"]