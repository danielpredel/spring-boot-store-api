# Spring Boot Store API

## Overview
RESTful e-commerce/store API built with Spring Boot.

The project demonstrates:
- domain-based modular architecture
- DTO pattern
- JPA/Hibernate persistence
- transactional business logic
- validation
- global exception handling
- entity relationships
- order processing workflows
- Spring Security authentication & authorization
- role-based access control

---

## Features

### API Design
- public and admin endpoint separation
- role-specific DTO responses
- soft delete strategy
- standardized ApiResponse wrapper
- centralized error response structure

### Authentication & Security
- Spring Security integration
- role-based authentication & authorization
- ADMIN and USER roles
- endpoint protection based on roles
- DTO-level response visibility
- custom security configuration
- request filtering
- automatic admin user seeding
- soft delete user handling
- stateless JWT authentication
- JWT role and identity claims
- custom JWT validation pipeline

### Users
- CRUD operations
- validation
- DTO-based responses
- role-based authorization
- soft delete support

### Products
- CRUD operations
- stock management
- protected admin operations

### Orders
- create orders
- order preview before purchase
- transactional order processing
- automatic stock updates
- order status management

## Technical Features

- Spring Data JPA
- Spring Security
- PostgreSQL persistence
- Manual mapping
- global exception handling
- entity auditing timestamps
- LAZY relationship loading
- transactional operations
- custom security filters
- startup data seeding
- OpenAPI/Swagger documentation
- Flyway database migrations
- structured logging
- Dockerized environment
- profile-based configuration
- health monitoring with Spring Boot Actuator
- standardized API responses
- unit and integration testing
- Testcontainers integration testing

---

## Domain Model

- User -> Orders
- Order -> OrderItems
- Product -> OrderItems
- User -> Roles

---

## Project Structure

```text
src/main/java/dev.danielpredel/storeapi/
├── auth/
├── common/
├── config/
├── order/
├── product/
├── user/
└── StoreApiApplication.java
```

---

## Security

The API uses Spring Security with role-based authorization.

### Roles
- `ADMIN`
- `USER`

### Admin Seeder
An `AdminSeeder` runs during application startup to create or verify the default admin account.

This ensures an administrator user is always available for protected operations.

---

## Testing

The project includes:
- unit testing with JUnit and Mockito
- integration testing with Testcontainers
- authentication/security testing
- service-layer testing
- controller integration testing

Testing was developed with AI-assisted support to accelerate repetitive test setup and improve coverage exploration while maintaining manual implementation, debugging and validation of the application's business logic and architecture.

---

## Health Monitoring

Spring Boot Actuator health endpoint:

```text
http://localhost:8080/api/v1/actuator/health
```

---

## API Versioning

The API uses URI versioning.

Current version:

```text
/api/v1
```

Example:

```text
/api/v1/products
/api/v1/auth/login
```

---

## API Documentation

Swagger UI:

```text
http://localhost:8080/api/v1/swagger-ui/index.html
```

---

## API Endpoints

### Auth

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/auth/register` | Public | Register new user |
| POST | `/auth/login` | Public | Authenticate active user |

---

### Products

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/products` | Public | Get active products |
| GET | `/products/{id}` | Public | Get active product by ID |

#### Public Product Response
Visible fields:
- id
- name
- price
- stock
- imageUrl

---

### Users

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/users/{id}` | USER | Get own profile |
| PUT | `/users/{id}` | USER | Update own profile |
| DELETE | `/users/{id}` | USER | Soft delete own account |

#### User Response
Visible fields:
- id
- name
- email
- address

---

### Orders

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/orders` | USER | Get own orders |
| GET | `/orders/{id}` | USER | Get own order by ID |
| POST | `/orders` | USER | Create order |
| POST | `/orders/preview` | USER | Preview order |
| PATCH | `/orders/{id}/cancel` | USER | Cancel CREATED order |

#### User Order Response
Visible fields:
- id
- orderItems
- totalAmount
- purchaseDate
- status

---

# Admin Endpoints

### Admin Products

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/admin/products` | ADMIN | Create product |
| GET | `/admin/products` | ADMIN | Get all products |
| GET | `/admin/products/{id}` | ADMIN | Get product by ID |
| PUT | `/admin/products/{id}` | ADMIN | Update product |

#### Admin Product Response
Visible fields:
- id
- name
- price
- stock
- imageUrl
- active

---

### Admin Users

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/admin/users` | ADMIN | Get all users |
| GET | `/admin/users/{id}` | ADMIN | Get user by ID |

#### Admin User Response
Visible fields:
- id
- name
- email
- active
- role

---

### Admin Orders

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/admin/orders` | ADMIN | Get all orders |
| GET | `/admin/orders/{id}` | ADMIN | Get order by ID |
| PATCH | `/admin/orders/{id}/deliver` | ADMIN | Deliver CREATED order |

#### Admin Order Response
Visible fields:
- id
- userId
- orderItems
- totalAmount
- purchaseDate
- status

---

## Example Requests

### Register User

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
-H "Content-Type: application/json" \
-d '{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "12345678",
  "address": "Fake Street #104"
}'
```

### Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
-H "Content-Type: application/json" \
-d '{
  "email": "john@example.com",
  "password": "12345678"
}'
```

---

### Get Products

```bash
curl http://localhost:8080/api/v1/products
```

### Get Product By ID

```bash
curl http://localhost:8080/api/v1/products/1
```

---

### Get Own User Profile

```bash
curl http://localhost:8080/api/v1/users/1 \
-H "Authorization: Bearer YOUR_TOKEN"
```

### Update User

```bash
curl -X PUT http://localhost:8080/api/v1/users/1 \
-H "Authorization: Bearer YOUR_TOKEN" \
-H "Content-Type: application/json" \
-d '{
  "name": "Updated Name",
  "address": "Updated Address #102"
}'
```

---

### Create Order

```bash
curl -X POST http://localhost:8080/api/v1/orders \
-H "Authorization: Bearer YOUR_TOKEN" \
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

### Preview Order

```bash
curl -X POST http://localhost:8080/api/v1/orders/preview \
-H "Authorization: Bearer YOUR_TOKEN" \
-H "Content-Type: application/json" \
-d '{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}'
```

### Cancel Order

```bash
curl -X PATCH http://localhost:8080/api/v1/orders/1/cancel \
-H "Authorization: Bearer YOUR_TOKEN"
```

---

## Admin Requests

### Create Product

```bash
curl -X POST http://localhost:8080/api/v1/admin/products \
-H "Authorization: Bearer ADMIN_TOKEN" \
-H "Content-Type: application/json" \
-d '{
  "name": "Mechanical Keyboard",
  "price": 129.99,
  "stock": 15,
  "imageUrl": "https://example.com/keyboard.jpg",
  "active": true
}'
```

### Get All Users

```bash
curl http://localhost:8080/api/v1/admin/users \
-H "Authorization: Bearer ADMIN_TOKEN"
```

### Deliver Order

```bash
curl -X PATCH http://localhost:8080/api/v1/admin/orders/1/deliver \
-H "Authorization: Bearer ADMIN_TOKEN"
```

---

## How to Run

### Prerequisites

* Java 21+
* Maven or Gradle
* Docker

### Environment Variables

Create a `.env` file in the project root with the following variables:

```env
# Database configuration
DB_NAME=your_database_name
DB_USER=your_database_user
DB_PASSWORD=your_database_password

# JWT configuration
# JWT_SECRET must be a cryptographically random string with at least 32 bytes (256 bits) of entropy; use a long hex or base64 value.
JWT_SECRET=your_secure_jwt_secret

# Admin user credentials
ADMIN_EMAIL=your_admin@mail.com
ADMIN_PASSWORD=your_admin_password
```

### Spring Profiles

The project uses profile-based configuration:

- `application.yaml`
- `application-dev.yaml`
- `application-prod.yaml`
- `application-test.yaml`

### Steps

```bash
# Clone the repository
## HTTPS
git clone https://github.com/danielpredel/store-api.git

# Navigate into the project
cd store-api

# Development database
docker compose up -d

# Export .env variables
export $(grep -v '^#' .env | xargs)

# Run application locally (dev profile)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production-like Environment

Run the full containerized setup:

```bash
# Build app
./mvnw clean package

# Run full app with docker
docker compose -f docker-compose.yml -f docker-compose.prod.yml up --build
```

Flyway migrations execute automatically during application startup in production profile environments.

App will start at:

```
http://localhost:8080
```

---

## Next Steps

Planned improvements:
- CI/CD pipeline
- cloud deployment
- refresh token support
- rate limiting
- password change endpoint
