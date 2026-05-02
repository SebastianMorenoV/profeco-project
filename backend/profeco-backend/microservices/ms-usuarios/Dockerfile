FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="profeco-team"

WORKDIR /app

COPY target/ms-usuarios-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8096 9096

ENTRYPOINT ["java", "-jar", "app.jar"]
