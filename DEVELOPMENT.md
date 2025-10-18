# Snippetia Development Guide

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Development Setup](#development-setup)
- [Code Structure](#code-structure)
- [Development Workflow](#development-workflow)
- [Testing Strategy](#testing-strategy)
- [Deployment](#deployment)
- [Contributing](#contributing)

## 🚀 Project Overview

Snippetia is an enterprise-grade code sharing platform built with modern technologies and best practices. The project consists of multiple components working together to provide a comprehensive developer experience.

### Key Components

1. **Backend (Spring Boot + Kotlin)**: RESTful API server with enterprise features
2. **Frontend (Compose Multiplatform)**: Cross-platform UI for Web, Android, iOS, Desktop
3. **VCS (C/C++)**: Custom version control system optimized for code snippets
4. **Infrastructure**: Docker, Kubernetes, monitoring, and CI/CD

## 🏗️ Architecture

### Backend Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
├─────────────────────────────────────────────────────────────┤
│  Controllers  │  Security  │  WebSocket  │  GraphQL API    │
├─────────────────────────────────────────────────────────────┤
│                    Application Layer                         │
├─────────────────────────────────────────────────────────────┤
│   Services    │  Use Cases │  Event Handlers │  Schedulers │
├─────────────────────────────────────────────────────────────┤
│                     Domain Layer                            │
├─────────────────────────────────────────────────────────────┤
│   Entities    │  Value Objects │  Domain Events │  Policies │
├─────────────────────────────────────────────────────────────┤
│                  Infrastructure Layer                       │
├─────────────────────────────────────────────────────────────┤
│ Repositories  │  External APIs │  File Storage │  Messaging │
└─────────────────────────────────────────────────────────────┘
```

### Frontend Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
├─────────────────────────────────────────────────────────────┤
│   Screens     │  Components │  ViewModels │  Navigation    │
├─────────────────────────────────────────────────────────────┤
│                     Domain Layer                            │
├─────────────────────────────────────────────────────────────┤
│   Use Cases   │  Entities  │  Repositories (Interfaces)    │
├─────────────────────────────────────────────────────────────┤
│                      Data Layer                             │
├─────────────────────────────────────────────────────────────┤
│ API Services  │  Local Storage │  Repositories (Impl)      │
└─────────────────────────────────────────────────────────────┘
```

## 🛠️ Development Setup

### Prerequisites

- **Java 17+** (OpenJDK recommended)
- **Kotlin 1.9.20+**
- **Node.js 18+** (for web development)
- **Docker & Docker Compose**
- **Git**
- **IDE**: IntelliJ IDEA (recommended) or VS Code

### Backend Setup

```bash
# Navigate to backend directory
cd backend

# Run with development profile
./gradlew bootRun --args='--spring.profiles.active=dev'

# Run tests
./gradlew test

# Generate test coverage report
./gradlew jacocoTestReport

# Build production JAR
./gradlew bootJar
```

### Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Web development
./gradlew jsRun

# Desktop development
./gradlew runDesktop

# Android development
./gradlew assembleDebug

# iOS development (macOS only)
./gradlew iosSimulatorArm64Test

# Run tests
./gradlew allTests
```

### Full Stack Development

```bash
# Start all services with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

## 📁 Code Structure

### Backend Structure

```
backend/
├── src/main/kotlin/com/snippetia/
│   ├── config/              # Configuration classes
│   ├── controller/          # REST controllers
│   ├── dto/                 # Data Transfer Objects
│   ├── exception/           # Custom exceptions
│   ├── model/               # JPA entities
│   ├── repository/          # Data access layer
│   ├── security/            # Security configuration
│   └── service/             # Business logic
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/        # Flyway migrations
└── src/test/                # Test classes
```

### Frontend Structure

```
frontend/src/
├── commonMain/kotlin/com/snippetia/
│   ├── data/                # Data layer
│   │   ├── api/             # API services
│   │   ├── repository/      # Repository implementations
│   │   └── storage/         # Local storage
│   ├── domain/              # Domain layer
│   │   ├── model/           # Domain models
│   │   └── usecase/         # Use cases
│   ├── presentation/        # Presentation layer
│   │   ├── component/       # Reusable components
│   │   ├── screen/          # Screen composables
│   │   ├── theme/           # Theme and styling
│   │   └── viewmodel/       # ViewModels
│   └── di/                  # Dependency injection
├── androidMain/             # Android-specific code
├── iosMain/                 # iOS-specific code
├── desktopMain/             # Desktop-specific code
└── jsMain/                  # Web-specific code
```

## 🔄 Development Workflow

### Git Workflow

1. **Feature Branches**: Create feature branches from `develop`
2. **Naming Convention**: `feature/TICKET-short-description`
3. **Pull Requests**: All changes must go through PR review
4. **Code Review**: At least one approval required
5. **Testing**: All tests must pass before merge

### Code Standards

#### Backend (Kotlin)

```kotlin
/**
 * Service class documentation with comprehensive description
 * 
 * @param dependency Injected dependency description
 * @author Developer Name
 * @since Version number
 */
@Service
@Transactional
class ExampleService(
    private val repository: ExampleRepository
) {
    
    /**
     * Method documentation with parameters and return value
     * 
     * @param id The unique identifier
     * @return The found entity or null
     * @throws EntityNotFoundException if entity not found
     */
    fun findById(id: Long): ExampleEntity? {
        return repository.findById(id)
            .orElseThrow { EntityNotFoundException("Entity not found") }
    }
}
```

#### Frontend (Compose)

```kotlin
/**
 * Composable function documentation
 * 
 * @param title The screen title
 * @param onNavigateBack Callback for navigation
 * @param modifier Modifier for styling
 */
@Composable
fun ExampleScreen(
    title: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Implementation with clear structure
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // UI components
    }
}
```

### Documentation Standards

1. **Class Documentation**: Every class must have comprehensive documentation
2. **Method Documentation**: All public methods must be documented
3. **Parameter Documentation**: All parameters must be explained
4. **Return Value Documentation**: Return values must be documented
5. **Exception Documentation**: Thrown exceptions must be documented

## 🧪 Testing Strategy

### Backend Testing

```bash
# Unit tests
./gradlew test

# Integration tests
./gradlew integrationTest

# Contract tests
./gradlew contractTest

# Performance tests
./gradlew performanceTest

# Security tests
./gradlew securityTest
```

### Frontend Testing

```bash
# Unit tests
./gradlew commonTest

# UI tests
./gradlew androidConnectedTest

# Cross-platform tests
./gradlew allTests
```

### Test Categories

1. **Unit Tests**: Test individual components in isolation
2. **Integration Tests**: Test component interactions
3. **End-to-End Tests**: Test complete user workflows
4. **Performance Tests**: Test system performance under load
5. **Security Tests**: Test security vulnerabilities

## 🚀 Deployment

### Development Deployment

```bash
# Local development
docker-compose up -d

# Development environment
docker-compose -f docker-compose.dev.yml up -d
```

### Production Deployment

```bash
# Production deployment
docker-compose -f docker-compose.prod.yml up -d

# Kubernetes deployment
kubectl apply -f k8s/

# Scaling
kubectl scale deployment snippetia-backend --replicas=5
```

### Environment Configuration

1. **Development**: Local development with hot reload
2. **Staging**: Production-like environment for testing
3. **Production**: Live environment with monitoring and scaling

## 🤝 Contributing

### Code Review Checklist

- [ ] Code follows project conventions
- [ ] All tests pass
- [ ] Documentation is updated
- [ ] Security considerations addressed
- [ ] Performance impact assessed
- [ ] Accessibility requirements met

### Pull Request Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing completed

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] Tests pass locally
```

### Commit Message Format

```
type(scope): subject

body

footer
```

Example:
```
feat(auth): add OAuth2 integration

Implement OAuth2 authentication with GitHub, Google, and Discord providers.
Includes token refresh and user profile synchronization.

Closes #123
```

## 📊 Monitoring and Observability

### Metrics Collection

- **Application Metrics**: Business KPIs and user engagement
- **System Metrics**: CPU, memory, disk, network usage
- **API Metrics**: Request rates, response times, error rates
- **Security Metrics**: Authentication attempts, security events

### Logging Strategy

- **Structured Logging**: JSON format for easy parsing
- **Log Levels**: DEBUG, INFO, WARN, ERROR, FATAL
- **Correlation IDs**: Track requests across services
- **Security Logging**: Audit trail for security events

### Alerting Rules

- **High Error Rate**: >5% error rate for 5 minutes
- **High Response Time**: >2s average response time
- **Database Issues**: Connection pool exhaustion
- **Security Events**: Multiple failed login attempts

## 🔒 Security Considerations

### Authentication & Authorization

- **JWT Tokens**: Secure token-based authentication
- **OAuth2**: Integration with external providers
- **RBAC**: Role-based access control
- **MFA**: Multi-factor authentication support

### Data Protection

- **Encryption**: AES-256 encryption for sensitive data
- **HTTPS**: TLS 1.3 for all communications
- **Input Validation**: Comprehensive input sanitization
- **SQL Injection**: Parameterized queries and ORM

### Security Scanning

- **Static Analysis**: Code security scanning
- **Dependency Scanning**: Vulnerability detection
- **Container Scanning**: Docker image security
- **Runtime Protection**: Real-time threat detection

## 📈 Performance Optimization

### Backend Performance

- **Database Optimization**: Query optimization and indexing
- **Caching**: Multi-level caching strategy
- **Connection Pooling**: Optimized database connections
- **Async Processing**: Background job processing

### Frontend Performance

- **Code Splitting**: Lazy loading of components
- **Image Optimization**: WebP format and compression
- **Bundle Optimization**: Tree shaking and minification
- **Caching**: Service worker and browser caching

### Infrastructure Performance

- **Load Balancing**: Intelligent request distribution
- **Auto-scaling**: Horizontal and vertical scaling
- **CDN**: Global content delivery network
- **Database Sharding**: Horizontal database scaling

---

**Happy Coding! 🚀**

For questions or support, please reach out to the development team or create an issue in the repository.