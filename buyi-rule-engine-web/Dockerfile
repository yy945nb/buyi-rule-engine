# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy POM files first for dependency caching
COPY pom.xml .
COPY buyi-rule-engine-core/pom.xml buyi-rule-engine-core/
COPY buyi-rule-engine-compute/pom.xml buyi-rule-engine-compute/
COPY buyi-rule-engine-web/pom.xml buyi-rule-engine-web/
COPY buyi-gateway-sdk/pom.xml buyi-gateway-sdk/
COPY buyi-gateway-mcp/pom.xml buyi-gateway-mcp/
COPY buyi-gateway-app/pom.xml buyi-gateway-app/

RUN mvn dependency:go-offline -B -pl buyi-rule-engine-web -am

# Copy source and build
COPY buyi-rule-engine-core/src buyi-rule-engine-core/src
COPY buyi-rule-engine-compute/src buyi-rule-engine-compute/src
COPY buyi-rule-engine-web/src buyi-rule-engine-web/src
COPY buyi-gateway-sdk/src buyi-gateway-sdk/src
COPY buyi-gateway-mcp/src buyi-gateway-mcp/src

RUN mvn package -pl buyi-rule-engine-web -am -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/buyi-rule-engine-web/target/*.jar app.jar

EXPOSE 20888

ENTRYPOINT ["java", \
    "-XX:+UseZGC", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar"]
