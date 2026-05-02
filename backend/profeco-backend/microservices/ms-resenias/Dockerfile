FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="profeco-team"

WORKDIR /app

COPY target/ms-resenias-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8094 9094

ENTRYPOINT ["java", "-jar", "app.jar"]
