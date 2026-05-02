FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="profeco-team"

WORKDIR /app

COPY target/ms-ofertas-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8095 9095

ENTRYPOINT ["java", "-jar", "app.jar"]
