FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="profeco-team"

WORKDIR /app

COPY target/ms-comercio-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8092 9092

ENTRYPOINT ["java", "-jar", "app.jar"]
