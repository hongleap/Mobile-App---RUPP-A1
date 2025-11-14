FROM gradle:7.6-jdk17 AS build

WORKDIR /app

# Copy build files
COPY build.gradle.kts settings.gradle.kts ./
COPY shared-models ./shared-models
COPY order-service ./order-service

# Build
RUN gradle :order-service:build --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy built jar
COPY --from=build /app/order-service/build/libs/order-service-1.0.0.jar app.jar

EXPOSE 8082

CMD ["java", "-jar", "app.jar"]

