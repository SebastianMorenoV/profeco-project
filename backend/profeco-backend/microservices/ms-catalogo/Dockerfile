FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="profeco-team"

WORKDIR /app

COPY target/ms-catalogo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8091 9091

ENTRYPOINT ["java", "-jar", "app.jar"]
