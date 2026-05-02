FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="profeco-team"

WORKDIR /app

COPY target/ms-multas-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8097 9097

ENTRYPOINT ["java", "-jar", "app.jar"]
