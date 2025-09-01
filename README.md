# Book Store

## 📖 Introduction

A Spring Boot application that supports user accounts, allowing users to log in, browse books, add them to a shopping cart, and place orders based on the cart contents. The application also includes an administrator role who can manage books, categories, and orders in the database.
## 💡Motivation
This application was developed as part of my Java backend learning journey to simulate a real-world e-commerce experience using modern Spring technologies. The goal of this project was to gain hands-on experience in building a secure, scalable, and maintainable REST API while applying best practices in software architecture, testing, and database management.
## 🔐 Authentication & Authorization

The application uses JWT for token-based authentication. Users log in with their credentials to receive a JWT token, which must be included in the Authorization header of subsequent API requests.

- **Login Endpoint:** `POST /api/auth/login`
- **Request body example:**
  ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }
Response:

 ```json
 {
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGdtYWlsLmNvbSIsImlhdCI6MTc1Mzg3NDk2NiwiZXhwIjoxNzUzODgwOTY2fQ.Ajm5algUEId-iAUzSiJnBK224u_-DVl9zWbzJK1P1aRg_p_aDHXNaVnqbRW1Yj465ROYAu8ehAB6NVuxjxIDmg"
 }
 ```
## ✨ Key Features & Highlights

- 🔎 **Advanced Search & Filtering** – books can be queried by multiple fields (title, author, category, price range) using the **Specification pattern**, providing flexible and dynamic queries.
- 📄 **Pagination & Sorting** – all list endpoints return paginated and sortable results to ensure scalability and performance.
- ⚡ **Exception Handling** – centralized error management implemented with `@ControllerAdvice`, returning consistent error responses in JSON format.
- ✅ **DTO Validation** – request payloads are validated with **Hibernate Validator** annotations, ensuring data integrity before processing.
- 🔐 **Secure Authentication & Authorization** – powered by **Spring Security + JWT tokens**, with role-based access for `USER` and `ADMIN`.
- 🛠 **Database Migrations with Liquibase** – automatic changelog execution creates users, roles, books, and categories at startup for easy setup.
- 🧪 **Integration Testing with Testcontainers** – ensures reliability by running tests against real MySQL in Docker.
## 🎯 Role & Permissions

**👤 User (ROLE_USER) can:**

- 🔐 Log in to their account – `POST /api/auth/login`

- 📚 Browse all books – GET - `/api/books`

- 🔍 Search books by title, author, or ISBN – `GET /api/books/search`

- 📖 View details of a single book – `GET /api/books/{id}`

- 📂 Browse categories and books in a category – `GET /api/categories, GET /api/categories/{id}/books`

- 🛒 Add books to the shopping cart – `POST /api/cart`

- ✏️ Update book quantities in the shopping cart – `PUT /api/cart/cart-items/{id}`

- ❌ Remove items from the shopping cart – `DELETE /api/cart/cart-items/{id}`

- ✅ Place orders based on the shopping cart – `POST /api/orders`

- 🧾 View their order history – `GET /api/orders`

- 🔍 View order item details – `GET /api/orders/{orderId}/items`, `GET /api/orders/{orderId}/items/{itemId}`

**🛠 Administrator (ROLE_ADMIN) can additionally:**

- ➕ Add new books – `POST /api/books`

- ✏️ Edit existing books – `PUT /api/books/{id}`

- ❌ Delete books – `DELETE /api/books/{id}`

- ➕ Create new categories – `POST /api/categories`

- ✏️ Update or delete categories – `PUT /api/categories/{id}`, `DELETE /api/categories/{id}`

- 📝 Manage all orders by updating order status – `PATCH /api/orders/{orderId}`

## 🛠 Technologies Used

| Technology           | Description                      |
|----------------------|----------------------------------|
| Java 17              | Core language                    |
| Spring Boot 3.4.4    | Main framework                   |
| Spring Security      | Authentication & authorization   |
| Spring Data JPA      | Data persistence                 |
| JWT (jjwt)           | Token-based authentication       |
| MapStruct            | DTO mapping                      |
| Liquibase            | DB schema management             |
| Hibernate Validator  | Input validation                 |
| Testcontainers       | Integration tests with Docker    |
| MySQL / H2           | Databases (prod/test)            |
| Swagger (springdoc)  | API documentation                |
| Lombok               | Less boilerplate                 |
| Checkstyle           | Code quality                     |

## 🧩 Database Initialization (via Liquibase)

On startup, Liquibase automatically sets up the schema and inserts initial data into the database, including:

### 👥 Default Users

| Role        | Email             | Password   |
|-------------|-------------------|------------|
| User        | `user@gmail.com`  | `User123`  |
| Administrator | `admin@gmail.com` | `Admin123` |

### 📚 Default Books

| ID | Title                        | Author             | ISBN           | Price  | Categories        |
|----|------------------------------|--------------------|----------------|--------|-------------------|
| 1  | Dom Szeptów                  | Marta Krajewska    | 9788366347192  | 42.99  | Horror            |
| 2  | W blasku księżyca            | Julia Nowicka      | 9788375789856  | 34.90  | Romance           |
| 3  | Cień Smoka                   | Eliza Falk         | 9788396012345  | 49.50  | Fantasy, Romance  |
| 4  | Lśnienie                     | Stephen King       | 9788382157923  | 45.00  | Horror            |
| 5  | Wiedźmin Ostatnie życzenie   | Andrzej Sapkowski  | 9788375780635  | 39.99  | Fantasy           |
| 6  | Pan Lodowego Ogrodu          | Jarosław Grzędowicz| 9788375740486  | 24.90  | Fantasy           |

### 🗂 Default Categories

| ID | Name     | Description (short)                                                                                  |
|----|----------|------------------------------------------------------------------------------------------------------|
| 1  | Fantasy  | Fictional worlds with magic, mythology, or epic quests.                                              |
| 2  | Horror   | Literature focused on fear, supernatural events, and psychological thrill.                           |
| 3  | Romance  | Stories centered on emotional and intimate relationships.                                            |

Books are pre-linked with relevant categories via `books_categories`.
## 📚 Lessons Learned & Challenges

- 🔐 **JWT Authentication** – configuring Spring Security with JWT was challenging, especially handling token expiration and refresh logic. I learned how to design a clean security layer with filters and exception handling.
- 🗄 **Liquibase Migrations** – ensuring proper changelog order and rollback strategy was tricky at first. Now I understand how to structure changesets for a production-ready database.
- 🔗 **Entity Relationships** – mapping many-to-many relationships between `Books` and `Categories` required careful design to avoid recursion and performance issues in serialization.
- 🧪 **Testcontainers** – integrating Testcontainers for MySQL was a big step forward. It gave me hands-on experience with running reproducible integration tests in different environments.

💡 This project helped me strengthen my skills in **Spring Security, database versioning, and clean API design**, while also improving my ability to debug and reason about complex backend systems.

## 📖 API Documentation
You can test endpoints via Swagger UI or your favorite REST client (e.g., Postman). 

By default, when you launch the app, interactive API documentation is available via the Swagger UI at:
http://localhost:8081/api/swagger-ui/index.html#/

This documentation allows you to explore and test all available endpoints, see request/response models, and read detailed descriptions.

## ⚙️ Setup Instructions

✅ Prerequisites

- Java 17+

- Maven

- MySQL (or Docker Container)

## 🚀 How to Run
**Clone the repository:**

	git clone https://github.com/Rzarcik97/Book_Store.git

Configure your database connection in src/main/resources/application.properties

By default, the project uses MySQL for production and H2 for tests.

**Build the project using Maven:**

	mvn clean package

**Run the application:**

	docker compose up

**Access the API at**  ```http://localhost:8081```

## 🗂 System Overview

Below is a simplified architecture diagram showing how the application components interact:

![Architecture Diagram](src/docs/Application-diagram.png)

The screenshot below shows a fragment of the application in Swagger OpenAPI

![Architecture Diagram](src/docs/Swagger-screenshot.png)