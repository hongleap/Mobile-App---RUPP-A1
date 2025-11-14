FROM gradle:7.6-jdk17 AS build

WORKDIR /app

# Copy build files
COPY build.gradle.kts settings.gradle.kts ./
COPY shared-models ./shared-models
COPY api-gateway ./api-gateway

# Build
RUN gradle :api-gateway:shadowJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy built jar
COPY --from=build /app/api-gateway/build/libs/api-gateway*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

