# Multi-stage build for Slema Backend
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy pom.xml and source code
COPY backend/pom.xml .
COPY backend/src ./src

# Build (skip tests for faster build)
RUN mvn clean package -DskipTests

# Runtime stage: use smaller base image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/api/health/ || exit 1

# Start application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
