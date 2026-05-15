# TaskFlow API

TaskFlow is a Spring Boot backend application that provides secure task management APIs with JWT authentication, role-based authorization, PostgreSQL integration, and clean layered architecture.

---

# 🚀 Tech Stack

- Java 21
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- PostgreSQL
- Spring Data JPA / Hibernate
- Maven
- Swagger / OpenAPI

---

# 🔐 Features

## Authentication & Security
- JWT-based authentication
- Secure REST APIs
- Password encryption with BCrypt
- Stateless authentication flow

## Authorization
- Role-Based Access Control (RBAC)
- USER and ADMIN roles
- Task ownership protection
- Method-level security with `@PreAuthorize`

## Task Management
- Create tasks
- Update tasks
- Delete tasks
- View personal tasks
- Admin access to all tasks

## API Architecture
- DTO-based request/response design
- Global exception handling
- Standardized API response wrapper
- Validation with `@Valid`
- Layered architecture:
  Controller → Service → Repository

## Documentation
- Swagger/OpenAPI integration

---

# 📦 API Endpoints

## Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login and receive JWT token |

---

## Tasks

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/tasks` | Get tasks |
| GET | `/api/tasks/{id}` | Get task by ID |
| POST | `/api/tasks` | Create task |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |

---

# 👥 Roles

## USER
- Can manage own tasks only

## ADMIN
- Can access all tasks
- Can update/delete any task

---

# 🔑 Authentication Flow

1. Register or login
2. Receive JWT token
3. Add token to Authorization header: Bearer <your-token>
4. Access secured endpoints

---

# 🏗️ Project Architecture

## Application Layer Flow

Controller → Service → Repository → Database

## Security Flow

JWT Token → JwtFilter → SecurityContextHolder → Authorization

---

## 🧪 How to Run

### 1. Setup database
Create PostgreSQL database: taskflow
### 2. Configure application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskflow
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

### 3. Run project 
mvn spring-boot:run

---
## Swagger UI

http://localhost:8080/swagger-ui/index.html

---


## 📌 Project Status

### Completed
- JWT authentication
- RBAC authorization
- CRUD APIs
- PostgreSQL integration
- DTO architecture
- Validation
- Global exception handling
- Ownership authorization
### Planned Improvements
- Pagination & sorting
- Refresh token system
- Docker deployment
- Unit & integration testing
- CI/CD pipeline
---

## 👨‍💻 Author

Xiaoxu  
Spring Boot Software Developer