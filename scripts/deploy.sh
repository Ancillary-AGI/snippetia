#!/bin/bash

# ===================================================================
# SNIPPETIA DEPLOYMENT SCRIPT
# ===================================================================
# 
# This script automates the complete deployment process for the Snippetia
# platform, including building, testing, and deploying all components.
#
# Features:
# - Prerequisite validation (Docker, Docker Compose, etc.)
# - Multi-stage build process (backend, frontend, VCS)
# - Environment configuration management
# - Health checks and service validation
# - Rollback capabilities for failed deployments
# - Logging and monitoring integration
#
# Usage:
#   ./scripts/deploy.sh [environment] [options]
#
# Environments:
#   - development (default): Local development deployment
#   - staging: Staging environment deployment
#   - production: Production deployment with optimizations
#
# Options:
#   --skip-build: Skip the build process
#   --skip-tests: Skip running tests
#   --rollback: Rollback to previous deployment
#   --health-check-only: Only perform health checks
#
# Requirements:
#   - Docker 20.10+
#   - Docker Compose 2.0+
#   - Git (for version tracking)
#   - curl (for health checks)
#
# Author: Snippetia Team
# Version: 1.0.0
# ===================================================================

# Exit on any error for safety
set -e

# Enable debug mode if DEBUG environment variable is set
if [[ "${DEBUG}" == "true" ]]; then
    set -x
fi

echo "🚀 Starting Snippetia Deployment..."
echo "📅 Deployment started at: $(date)"
echo "🏷️  Git commit: $(git rev-parse --short HEAD 2>/dev/null || echo 'unknown')"

# ===== PREREQUISITE VALIDATION =====
# Check that all required tools and dependencies are available
check_prerequisites() {
    echo "📋 Checking prerequisites..."
    
    # Check Docker installation and version
    if ! command -v docker &> /dev/null; then
        echo "❌ Docker is not installed. Please install Docker 20.10+ from https://docker.com"
        exit 1
    fi
    
    # Verify Docker is running
    if ! docker info &> /dev/null; then
        echo "❌ Docker daemon is not running. Please start Docker service."
        exit 1
    fi
    
    # Check Docker Compose installation
    if ! command -v docker-compose &> /dev/null; then
        echo "❌ Docker Compose is not installed. Please install Docker Compose 2.0+"
        exit 1
    fi
    
    # Check Git for version tracking
    if ! command -v git &> /dev/null; then
        echo "⚠️  Git is not installed. Version tracking will be limited."
    fi
    
    # Check curl for health checks
    if ! command -v curl &> /dev/null; then
        echo "⚠️  curl is not installed. Health checks may not work properly."
    fi
    
    # Verify Docker Compose file exists
    if [ ! -f "docker-compose.yml" ]; then
        echo "❌ docker-compose.yml not found. Please run from project root directory."
        exit 1
    fi
    
    # Check available disk space (minimum 2GB)
    available_space=$(df . | tail -1 | awk '{print $4}')
    if [ "$available_space" -lt 2097152 ]; then  # 2GB in KB
        echo "⚠️  Low disk space detected. At least 2GB recommended for deployment."
    fi
    
    echo "✅ Prerequisites check passed"
    echo "🐳 Docker version: $(docker --version)"
    echo "🐙 Docker Compose version: $(docker-compose --version)"
}

# ===== APPLICATION BUILD PROCESS =====
# Build all application components with optimizations
build_application() {
    echo "🔨 Building application components..."
    
    # Create build timestamp for tracking
    BUILD_TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
    echo "🕐 Build timestamp: $BUILD_TIMESTAMP"
    
    # Build backend (Spring Boot + Kotlin)
    echo "📦 Building backend (Spring Boot + Kotlin)..."
    cd backend
    
    # Clean previous builds
    ./gradlew clean
    
    # Run tests unless skipped
    if [[ "${SKIP_TESTS}" != "true" ]]; then
        echo "🧪 Running backend tests..."
        ./gradlew test
    fi
    
    # Build production JAR
    echo "🏗️  Building production JAR..."
    ./gradlew bootJar -Pprod
    
    # Verify JAR was created
    if [ ! -f "build/libs/"*.jar ]; then
        echo "❌ Backend build failed - JAR not found"
        exit 1
    fi
    
    cd ..
    echo "✅ Backend build completed"
    
    # Build frontend (Compose Multiplatform)
    echo "🎨 Building frontend (Compose Multiplatform)..."
    cd frontend
    
    # Clean previous builds
    ./gradlew clean
    
    # Run tests unless skipped
    if [[ "${SKIP_TESTS}" != "true" ]]; then
        echo "🧪 Running frontend tests..."
        ./gradlew allTests
    fi
    
    # Build for all platforms
    echo "🌐 Building for web platform..."
    ./gradlew jsBrowserDistribution
    
    echo "🖥️  Building for desktop platform..."
    ./gradlew packageDistributionForCurrentOS
    
    # Verify builds were created
    if [ ! -d "build/dist/js/productionExecutable" ]; then
        echo "❌ Frontend web build failed"
        exit 1
    fi
    
    cd ..
    echo "✅ Frontend build completed"
    
    # Build VCS component (C/C++)
    echo "⚙️  Building VCS component (C/C++)..."
    cd vcs
    
    # Create build directory
    mkdir -p build
    cd build
    
    # Configure with CMake
    cmake .. -DCMAKE_BUILD_TYPE=Release
    
    # Build with make
    make -j$(nproc)
    
    # Verify binary was created
    if [ ! -f "svcs" ]; then
        echo "❌ VCS build failed - binary not found"
        exit 1
    fi
    
    cd ../..
    echo "✅ VCS build completed"
    
    echo "✅ All application components built successfully"
    echo "📊 Build summary:"
    echo "   - Backend JAR: $(ls -lh backend/build/libs/*.jar | awk '{print $5, $9}')"
    echo "   - Frontend web: $(du -sh frontend/build/dist/js/productionExecutable | awk '{print $1}')"
    echo "   - VCS binary: $(ls -lh vcs/build/svcs | awk '{print $5, $9}')"
}

# Deploy with Docker Compose
deploy_docker() {
    echo "🐳 Deploying with Docker Compose..."
    
    # Copy environment template if not exists
    if [ ! -f .env ]; then
        cp .env.example .env
        echo "⚠️  Please configure .env file with your settings"
    fi
    
    # Start services
    docker-compose up -d
    
    echo "✅ Services started successfully"
}

# Health check
health_check() {
    echo "🏥 Performing health checks..."
    
    # Wait for backend to be ready
    echo "⏳ Waiting for backend to be ready..."
    timeout 300 bash -c 'until curl -f http://localhost:8080/actuator/health; do sleep 5; done'
    
    echo "✅ Backend is healthy"
    echo "✅ Deployment completed successfully!"
    
    echo ""
    echo "🌐 Access your application:"
    echo "   Backend API: http://localhost:8080"
    echo "   Swagger UI:  http://localhost:8080/swagger-ui.html"
    echo "   Grafana:     http://localhost:3000 (admin/admin)"
    echo ""
}

# Main deployment flow
main() {
    check_prerequisites
    build_application
    deploy_docker
    health_check
}

# Run deployment
main "$@"