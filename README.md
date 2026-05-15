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
- Spring Security authentication & authorization
- role-based access control

---

## Features

### API Design
- public and admin endpoint separation
- role-specific DTO responses
- soft delete strategy

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

### Technical Features
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

---

## Domain Model

- User -> Orders
- Order -> OrderItems
- Product -> OrderItems
- User -> Roles

---

## Project Structure

```text
src/main/java/dev.danielpredel/userapibasic/
├── config/
├── controller/
├── filter/
├── security/
├── service/
│   └── impl/
├── repository/
├── entity/
├── dto/
├── mapper/
├── exception/
├── enums/
├── seeder/
└── UserApiBasicApplication.java
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
JWT_SECRET

# Admin user credentials
ADMIN_EMAIL
ADMIN_PASSWORD
```

### Steps

```bash
# Clone the repository
## HTTPS
git clone https://github.com/danielpredel/spring-boot-store-api.git

# Navigate into the project
cd spring-boot-store-api

# Start the database with Docker Compose
docker compose up --build -d

# Run the application
./mvnw spring-boot:run
```

App will start at:

```
http://localhost:8080
```

---

## Notes

- Current architecture is being refactored from global layered structure to domain-based modular structure
- Production concerns planned for next stage:
    - Dockerization
    - OpenAPI/Swagger documentation
    - Environment profiles
    - Automated testing
    - Improved validation/error handling