FROM gradle:7.6-jdk17 AS build

WORKDIR /app

# Copy build files
COPY build.gradle.kts settings.gradle.kts ./
COPY shared-models ./shared-models
COPY transaction-service ./transaction-service

# Build
RUN gradle :transaction-service:shadowJar --no-daemon -x test && \
    ls -la /app/transaction-service/build/libs/

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy built jar
COPY --from=build /app/transaction-service/build/libs/transaction-service*.jar app.jar
COPY firebase-service-account.json /app/firebase-service-account.json

EXPOSE 8084

CMD ["java", "-jar", "app.jar"]
