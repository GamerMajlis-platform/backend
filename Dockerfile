# Use OpenJDK 17 as base image for building
FROM eclipse-temurin:17-jdk AS builder

# Set working directory
WORKDIR /app

# Install Maven and copy pom.xml first (for better caching)
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Use Eclipse Temurin 17 runtime for smaller final image
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the jar file from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create a non-root user for security
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Expose port 8080
EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 