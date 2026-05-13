# TaskFlow API

A Spring Boot backend project with JWT authentication, PostgreSQL database, and RESTful API design.

---

## 🚀 Tech Stack

- Java 21
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- PostgreSQL
- JPA / Hibernate
- Swagger / OpenAPI

---

## 🔐 Features

- User authentication with JWT
- Protected REST APIs
- Task CRUD operations
- Role-based security (ready for upgrade)
- Swagger API documentation
- Clean layered architecture (Controller → Service → Repository)

---

## 📦 API Endpoints

### Auth
- POST `/auth/login` → login and get JWT token

### Tasks
- GET `/api/tasks` → get all tasks (secured)
- POST `/api/tasks` → create task
- PUT `/api/tasks/{id}` → update task
- DELETE `/api/tasks/{id}` → delete task

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

## 🔑 Authentication Flow

1. Call `/auth/login`
2. Get JWT token
3. Add token in Swagger: Bearer <your-token>


---

## 📌 Project Status

✔ JWT authentication implemented  
✔ REST API completed  
✔ Security enabled  
⏳ Role-based access (next step)  
⏳ Refresh token system (future upgrade)

---

## 👨‍💻 Author

Xiaoxu  
Spring Boot Software Developer