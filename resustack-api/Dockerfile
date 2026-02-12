FROM gradle:9.0-jdk21 AS build
WORKDIR /app

COPY gradle.properties settings.gradle ./
COPY build.gradle ./
COPY resustack-common ./resustack-common
COPY resustack-api ./resustack-api

RUN gradle :resustack-api:clean :resustack-api:build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/resustack-api/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
