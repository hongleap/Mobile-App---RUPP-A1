FROM gradle:7.6-jdk17 AS build

WORKDIR /app

# Copy build files
COPY build.gradle.kts settings.gradle.kts ./
COPY shared-models ./shared-models
COPY notification-service ./notification-service

# Build
RUN gradle :notification-service:shadowJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy built jar
COPY --from=build /app/notification-service/build/libs/notification-service*.jar app.jar

EXPOSE 8083

CMD ["java", "-jar", "app.jar"]

