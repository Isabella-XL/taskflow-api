# TaskFlow API - System Design Document

## 1. Introduction

### Purpose

TaskFlow API is a production-ready task management backend built with Java Spring Boot. The system provides secure user authentication, role-based authorization, task ownership enforcement, and RESTful CRUD operations for task management.

The application is designed as a stateless REST API and deployed to a cloud environment using automated CI/CD pipelines.

### Goals

* Secure user authentication using JWT
* Role-based access control (RBAC)
* Task ownership protection
* Clean layered architecture
* PostgreSQL persistence
* Docker containerization
* Automated deployment using GitHub Actions
* Production-ready REST API design

---

# 2. System Requirements

## Functional Requirements

### Authentication

* Register new users
* Authenticate existing users
* Generate JWT access tokens
* Refresh expired access tokens

### User Management

* Store user credentials securely
* Support role assignment
* Maintain user-task relationships

### Task Management

* Create tasks
* Retrieve tasks
* Update tasks
* Delete tasks
* Retrieve task details

### Authorization

* USER role can manage only owned tasks
* ADMIN role can manage all tasks

---

## Non-Functional Requirements

### Security

* Password encryption using BCrypt
* JWT token validation
* Stateless authentication
* Endpoint authorization

### Scalability

* Stateless API architecture
* Separation of concerns
* Database abstraction through JPA repositories

### Maintainability

* DTO-based communication
* Layered architecture
* Centralized exception handling

### Availability

* Cloud deployment on Render
* Health monitoring endpoint
* Automated deployment pipeline

---

# 3. High-Level Architecture

## Architecture Style

The application follows a layered architecture:

Client → Controller → Service → Repository → PostgreSQL

### Controller Layer

Responsibilities:

* Receive HTTP requests
* Validate incoming payloads
* Return HTTP responses
* Delegate business logic to services

Examples:

* AuthController
* TaskController

---

### Service Layer

Responsibilities:

* Implement business logic
* Perform ownership checks
* Enforce authorization rules
* Coordinate data access

Examples:

* AuthService
* TaskService
* JwtService

---

### Repository Layer

Responsibilities:

* Database interaction
* Query abstraction
* Entity persistence

Examples:

* UserRepository
* TaskRepository
* RefreshTokenRepository

---

### Database Layer

Responsibilities:

* Persistent storage
* Relational integrity
* Transaction management

Technology:

* PostgreSQL

---

# 4. Database Design

## User Entity

Represents registered system users.

Attributes:

| Field     | Type      |
| --------- | --------- |
| id        | Long      |
| username  | String    |
| password  | String    |
| role      | Enum      |
| createdAt | Timestamp |

---

## Task Entity

Represents a task owned by a user.

Attributes:

| Field       | Type      |
| ----------- | --------- |
| id          | Long      |
| title       | String    |
| description | String    |
| status      | Enum      |
| createdAt   | Timestamp |
| user        | User      |

---

## RefreshToken Entity

Stores refresh tokens for authentication renewal.

Attributes:

| Field      | Type    |
| ---------- | ------- |
| id         | Long    |
| token      | String  |
| expiryDate | Instant |
| user       | User    |

---

## Relationships

User (1) ---------> (Many) Tasks

User (1) ---------> (Many) Refresh Tokens

Each task belongs to exactly one user.

---

# 5. Security Architecture

## Authentication Model

The system uses JWT-based stateless authentication.

### Login Flow

1. User submits credentials
2. Spring Security authenticates credentials
3. JWT access token generated
4. Refresh token generated
5. Tokens returned to client

### Access Flow

1. Client sends JWT token
2. JwtAuthenticationFilter intercepts request
3. Token validated
4. SecurityContext populated
5. Request proceeds

---

## JWT Design

JWT contains:

* Username
* Issued timestamp
* Expiration timestamp

Benefits:

* Stateless architecture
* Reduced server memory usage
* Horizontal scalability

---

## Password Security

Passwords are encrypted using BCrypt before storage.

Advantages:

* Salted hashes
* Protection against rainbow table attacks
* Industry-standard password hashing

---

# 6. Authorization Design

## Roles

### USER

Permissions:

* Create tasks
* View own tasks
* Update own tasks
* Delete own tasks

Restrictions:

* Cannot access admin-only functionality
* Cannot manage tasks belonging to other users

---

### ADMIN

Permissions:

* Access all tasks
* Update any task
* Delete any task
* Perform administrative operations

---

## Authorization Strategy

Authorization is enforced using:

* Spring Security
* Method-level authorization
* Ownership validation

Example:

```
@PreAuthorize("hasRole('ADMIN')")
```

---

# 7. Ownership Protection

A key security requirement is preventing users from modifying resources they do not own.

## Ownership Validation Flow

1. Retrieve requested task
2. Retrieve authenticated user
3. Compare ownership
4. Allow or deny operation

Pseudo Logic:

```text
If task.owner == currentUser
    Allow
Else If currentUser.role == ADMIN
    Allow
Else
    Deny
```

This prevents horizontal privilege escalation.

---

# 8. API Design

## Authentication Endpoints

### Register

POST /auth/register

Purpose:

Create a new user account.

---

### Login

POST /auth/login

Purpose:

Authenticate user and generate tokens.

---

### Refresh Token

POST /auth/refresh

Purpose:

Generate a new access token.

---

## Task Endpoints

### Get Tasks

GET /api/tasks

Returns tasks visible to current user.

---

### Get Task By ID

GET /api/tasks/{id}

Returns a specific task.

---

### Create Task

POST /api/tasks

Creates a new task.

---

### Update Task

PUT /api/tasks/{id}

Updates an existing task.

---

### Delete Task

DELETE /api/tasks/{id}

Deletes a task.

---

## System Endpoints

### Root Endpoint

GET /

Returns API information.

### Health Endpoint

GET /health

Used for uptime monitoring and deployment validation.

---

# 9. DTO Architecture

The application uses DTOs to separate API contracts from persistence entities.

## Benefits

* Prevents entity exposure
* Reduces coupling
* Enables API evolution
* Improves security

Examples:

* TaskRequestDTO
* TaskResponseDTO
* LoginRequestDTO
* AuthResponseDTO

---

# 10. Exception Handling

Global exception handling is implemented using:

```
@RestControllerAdvice
```

## Supported Exceptions

### Validation Errors

HTTP 400 Bad Request

### Authentication Errors

HTTP 401 Unauthorized

### Authorization Errors

HTTP 403 Forbidden

### Resource Not Found

HTTP 404 Not Found

### Internal Errors

HTTP 500 Internal Server Error

---

# 11. Deployment Architecture

## Infrastructure

GitHub
↓
GitHub Actions
↓
Render
↓
Spring Boot Application
↓
PostgreSQL

---

## Deployment Workflow

1. Code pushed to GitHub
2. GitHub Actions pipeline triggered
3. Maven build executed
4. Docker image built
5. Render deploys latest version

---

# 12. Docker Design

## Components

### Application Container

Contains:

* Java Runtime
* Spring Boot application

### Database Container

Contains:

* PostgreSQL instance

---

## Benefits

* Environment consistency
* Simplified deployment
* Easier local development

---

# 13. CI/CD Design

## GitHub Actions Pipeline

Pipeline Responsibilities:

* Checkout source code
* Install dependencies
* Build application
* Execute tests
* Package application
* Prepare deployment artifacts

Benefits:

* Automated verification
* Reduced deployment risk
* Continuous delivery

---

# 14. Monitoring and Health Checks

## Health Endpoint

GET /health

Used by hosting infrastructure to verify application availability.

## Logging

Current implementation:

* Spring Boot logging

Future enhancements:

* Structured logging
* Centralized log aggregation

---

# 15. Testing Strategy

## Unit Tests

Purpose:

Validate business logic in isolation.

Target Components:

* TaskService
* AuthService
* JwtService

Tools:

* JUnit 5
* Mockito

---

## Integration Tests

Purpose:

Validate interactions between components.

Coverage:

* Authentication flow
* Task lifecycle
* Security configuration
* Repository operations

Tools:

* Spring Boot Test
* MockMvc
* Testcontainers

---

# 16. Tradeoffs and Design Decisions

## JWT vs Session-Based Authentication

Chosen:

JWT

Reasons:

* Stateless
* Scalable
* Cloud-friendly

Tradeoff:

* Token revocation is more complex

---

## PostgreSQL vs NoSQL

Chosen:

PostgreSQL

Reasons:

* Strong consistency
* Relational data model
* Mature tooling

Tradeoff:

* Less flexible schema evolution

---

## Layered Architecture

Chosen:

Controller → Service → Repository

Reasons:

* Separation of concerns
* Testability
* Maintainability

Tradeoff:

* More boilerplate code

---

# 17. Future Enhancements

Planned Improvements:

* Comprehensive unit testing
* Integration testing suite
* Redis caching
* API rate limiting
* Email verification
* Structured logging
* Metrics and monitoring
* Distributed tracing
* Kubernetes deployment
* API versioning
* Audit logging

---

# 18. Conclusion

TaskFlow API is a secure, production-oriented backend application demonstrating modern Spring Boot development practices, including JWT authentication, RBAC authorization, PostgreSQL persistence, Docker containerization, CI/CD automation, and cloud deployment.

The architecture prioritizes security, maintainability, scalability, and clear separation of concerns while remaining extensible for future growth.
