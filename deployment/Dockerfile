FROM gradle:8.4.0-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test -x validateStructure --no-daemon
# Replace with a non-root user to avoid running the container with excessive privileges
# Fase runtime
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/applications/app-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT [ "java", "-jar", "app.jar" ]
