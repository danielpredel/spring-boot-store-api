# Spring Boot User API

## Overview

A simple RESTful User API built with Spring Boot.
This project demonstrates clean layered architecture, DTO usage, validation, and global exception handling using in-memory data storage (no database).

---

## Features

* CRUD operations for users
* Input validation
* DTO pattern (no sensitive data exposure)
* Global exception handling
* In-memory storage (ConcurrentHashMap)

---

## Project Structure

```
src/main/java/com/example/userapi/
├── controller/      # Handles HTTP requests
├── service/         # Business logic
├── service/impl/    # Service implementation
├── model/           # Internal data models
├── dto/             # Request/Response objects
├── mapper/          # Entity <-> DTO mapping
├── exception/       # Custom exceptions + global handler
└── UserApiApplication.java
```

---

## API Endpoints

### Create User

* **POST** `/api/users`

### Get All Users

* **GET** `/api/users`

### Get User by ID

* **GET** `/api/users/{id}`

### Update User

* **PUT** `/api/users/{id}`

### Delete User

* **DELETE** `/api/users/{id}`

---

## Example Requests

### Create User

```bash
curl -X POST http://localhost:8080/api/users \
-H "Content-Type: application/json" \
-d '{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "12345678",
  "address": "Fake Street #104"
}'
```

### Get All Users

```bash
curl http://localhost:8080/api/users
```

### Get User by ID

```bash
curl http://localhost:8080/api/users/1
```

### Update User

```bash
curl -X PUT http://localhost:8080/api/users/1 \
-H "Content-Type: application/json" \
-d '{
  "name": "Updated Name",
  "email": "updated@example.com",
  "password": "qwerty12345",
  "address": "Not a Fake Street #102"
}'
```

### Delete User

```bash
curl -X DELETE http://localhost:8080/api/users/1
```

---

## How to Run

### Prerequisites

* Java 21+
* Maven or Gradle

### Steps

```bash
# Clone the repository
## HTTPS
git clone https://github.com/danielpredel/spring-boot-user-api.git

# Navigate into the project
cd spring-boot-user-api

# Run the application
./mvnw spring-boot:run
```

App will start at:

```
http://localhost:8080
```

---

## Notes

* Data is stored in memory (resets on restart)
* No authentication implemented (planned for future versions)
