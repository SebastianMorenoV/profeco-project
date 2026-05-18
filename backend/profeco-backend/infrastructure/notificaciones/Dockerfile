# ==========================================
# DOCKERFILE: NOTIFICACIONES
# ==========================================
# Multi-stage build
# Build context: backend/profeco-backend/
# ==========================================

# Etapa 1: Construcción
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build

# Copiar el código fuente
COPY . .

# Compilar usando el POM principal
RUN mvn clean package -pl infrastructure/notificaciones -am -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="profeco-team"

WORKDIR /app

COPY --from=build /build/infrastructure/notificaciones/target/notificaciones-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]
