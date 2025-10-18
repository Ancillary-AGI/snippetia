# ===================================================================
# SNIPPETIA BACKEND DOCKERFILE
# ===================================================================
# 
# Multi-stage Docker build for the Snippetia Spring Boot backend application.
# This Dockerfile creates an optimized, secure, and production-ready container.
#
# Build Stages:
# 1. Builder: Compiles the application using Gradle
# 2. Runtime: Creates minimal runtime container with JRE
#
# Security Features:
# - Non-root user execution
# - Minimal base image (JRE slim)
# - Security updates and package cleanup
# - Proper file permissions and ownership
#
# Performance Optimizations:
# - Multi-stage build to reduce image size
# - Gradle build cache utilization
# - Minimal runtime dependencies
# - Health check integration
#
# Usage:
#   docker build -t snippetia-backend .
#   docker run -p 8080:8080 snippetia-backend
# ===================================================================

# ===== BUILD STAGE =====
# Use official Gradle image with JDK 17 for building the application
FROM gradle:8.5-jdk17 AS builder

# Set working directory for build process
WORKDIR /app

# Copy Gradle configuration files first for better layer caching
# This allows Docker to cache dependencies if only source code changes
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle

# Copy source code
COPY src src

# Build the application
# - clean: Remove previous build artifacts
# - build: Compile and package the application
# - -x test: Skip tests in Docker build (run separately in CI/CD)
# - --no-daemon: Don't start Gradle daemon (not needed in container)
RUN gradle clean build -x test --no-daemon

# ===== RUNTIME STAGE =====
# Use minimal JRE image for running the application
FROM openjdk:17-jre-slim

# Update package lists and install essential tools
# curl: Required for health checks and debugging
# Clean up package cache to reduce image size
RUN apt-get update && apt-get install -y \
    curl \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

# ===== SECURITY CONFIGURATION =====
# Create non-root user for running the application
# This follows security best practices by avoiding root execution
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set application working directory
WORKDIR /app

# ===== APPLICATION SETUP =====
# Copy the compiled JAR from the builder stage
# This copies only the final artifact, keeping the runtime image minimal
COPY --from=builder /app/build/libs/*.jar app.jar

# Create necessary directories for application data
# uploads: Directory for user-uploaded files and attachments
# logs: Directory for application logs (if file logging is enabled)
RUN mkdir -p /app/uploads /app/logs && \
    chown -R appuser:appuser /app

# Switch to non-root user for security
# All subsequent commands and the application will run as this user
USER appuser

# ===== CONTAINER CONFIGURATION =====
# Expose the application port
# This is the port the Spring Boot application listens on
EXPOSE 8080

# Configure health check for container orchestration
# This allows Docker/Kubernetes to monitor application health
# - interval: How often to run the health check
# - timeout: How long to wait for health check response
# - start-period: Grace period before starting health checks
# - retries: Number of consecutive failures before marking unhealthy
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# ===== JVM OPTIMIZATION =====
# Set JVM options for optimal performance and monitoring
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom"

# Run the Spring Boot application
# Use exec form to ensure proper signal handling
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]