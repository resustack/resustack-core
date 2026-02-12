FROM gradle:9.0-jdk21 AS build
WORKDIR /app

COPY gradle.properties settings.gradle ./
COPY build.gradle ./
COPY resustack-common ./resustack-common
COPY resustack-auth ./resustack-auth
RUN mkdir -p resustack-api

RUN gradle :resustack-auth:clean :resustack-auth:build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/resustack-auth/build/libs/*.jar app.jar

EXPOSE 10010

ENTRYPOINT ["java", "-jar", "app.jar"]
