# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY buyi-rule-engine-core/pom.xml buyi-rule-engine-core/
COPY buyi-rule-engine-compute/pom.xml buyi-rule-engine-compute/
COPY buyi-gateway-sdk/pom.xml buyi-gateway-sdk/

RUN mvn dependency:go-offline -B -pl buyi-rule-engine-compute -am

COPY buyi-rule-engine-core/src buyi-rule-engine-core/src
COPY buyi-rule-engine-compute/src buyi-rule-engine-compute/src
COPY buyi-gateway-sdk/src buyi-gateway-sdk/src

RUN mvn package -pl buyi-rule-engine-compute -am -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/buyi-rule-engine-compute/target/*.jar app.jar

EXPOSE 8011

ENTRYPOINT ["java", \
    "-XX:+UseZGC", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar"]
