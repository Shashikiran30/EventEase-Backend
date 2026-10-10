# Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom and source code
COPY pom.xml .
COPY src ./src

# Package application (skipping tests for build speed)
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Render/Cloud providers dynamically provide PORT (defaults to 8080)
ENV PORT=8080
EXPOSE 8080

# Execute Spring Boot application with dynamic PORT binding
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
