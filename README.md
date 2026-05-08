# Spring Boot Store API

## Overview
RESTful e-commerce/store API built with Spring Boot.

The project demonstrates:
- layered architecture
- DTO pattern
- JPA/Hibernate persistence
- transactional business logic
- validation
- global exception handling
- entity relationships
- order processing workflows

---

## Features

### Users
- CRUD operations
- validation
- DTO-based responses

### Products
- CRUD operations
- stock management

### Orders
- create orders
- order preview before purchase
- transactional order processing
- automatic stock updates
- order status management

### Technical Features
- Spring Data JPA
- PostgreSQL persistence
- Manual mapping
- global exception handling
- entity auditing timestamps
- LAZY relationship loading
- transactional operations

---

## Domain Model

- User -> Orders
- Order -> OrderItems
- Product -> OrderItems

---

## Project Structure

```
src/main/java/danielpredel.dev/userapibasic/
├── controller/
├── service/
│   └── impl/
├── repository/
├── entity/
├── dto/
├── mapper/
├── exception/
├── enums/
└── UserApiBasicApplication.java
```

---

## API Endpoints

### Users

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/users` | Create user |
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

---

### Products

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/products` | Create product |
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get product by ID |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |

---

### Orders

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders` | Create order |
| POST | `/api/orders/preview` | Preview order before purchase |
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/{id}` | Get order by ID |
| PATCH | `/api/orders/{id}/cancel` | Cancel order |
| PATCH | `/api/orders/{id}/deliver` | Mark order as delivered |

---

## Example Requests

### Create User

```bash
curl -i -X POST http://localhost:8080/api/users \
-H "Content-Type: application/json" \
-d '{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "12345678",
  "address": "Fake Street #104"
}'
```

### Get All Users

Query params:
- `page` (default: 0)
- `size` (1-50, default: 10)
- `sortBy` (`id`, `name`, `email`)
- `direction` (`asc`, `desc`)

```bash
curl "http://localhost:8080/api/users?page=0&size=10&sortBy=name&direction=asc"
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

### Create Product

```bash
curl -X POST http://localhost:8080/api/products \
-H "Content-Type: application/json" \
-d '{
  "name": "Mechanical Keyboard",
  "price": 129.99,
  "stock": 15,
  "imageUrl": "https://example.com/keyboard.jpg",
  "active": true
}'
```

### Create Order

```bash
curl -X POST http://localhost:8080/api/orders \
-H "Content-Type: application/json" \
-d '{
  "userId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ]
}'
```

### Preview Order

```bash
curl -X POST http://localhost:8080/api/orders/preview \
-H "Content-Type: application/json" \
-d '{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ]
}'
```

### Cancel Order

```bash
curl -X PATCH http://localhost:8080/api/orders/1/cancel
```

### Deliver Order

```bash
curl -X PATCH http://localhost:8080/api/orders/1/deliver
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
git clone https://github.com/danielpredel/spring-boot-store-api.git

# Navigate into the project
cd spring-boot-store-api

# Run the application
./mvnw spring-boot:run
```

App will start at:

```
http://localhost:8080
```

---

## Notes

- Authentication/authorization planned for next stage
- Future security implementation will use JWT + role-based access control
