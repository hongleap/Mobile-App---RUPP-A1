FROM gradle:7.6-jdk17 AS build

WORKDIR /app

# Copy build files
COPY build.gradle.kts settings.gradle.kts ./
COPY shared-models ./shared-models
COPY product-service ./product-service

# Build
RUN gradle :product-service:shadowJar --no-daemon -x test && \
    ls -la /app/product-service/build/libs/

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy built jar (shadow creates product-service.jar when classifier is set to "")
COPY --from=build /app/product-service/build/libs/product-service*.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]

