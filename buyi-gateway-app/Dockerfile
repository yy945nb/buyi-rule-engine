# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY buyi-gateway-sdk/pom.xml buyi-gateway-sdk/
COPY buyi-gateway-mcp/pom.xml buyi-gateway-mcp/
COPY buyi-gateway-app/pom.xml buyi-gateway-app/

RUN mvn dependency:go-offline -B -pl buyi-gateway-app -am

COPY buyi-gateway-sdk/src buyi-gateway-sdk/src
COPY buyi-gateway-mcp/src buyi-gateway-mcp/src
COPY buyi-gateway-app/src buyi-gateway-app/src

RUN mvn package -pl buyi-gateway-app -am -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/buyi-gateway-app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
    "-XX:+UseZGC", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar"]
