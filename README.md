# Book Store

A Spring Boot application that supports user accounts, allowing users to log in, browse books, add them to a shopping cart, and place orders based on the cart contents. The application also includes an administrator role who can manage books, categories, and orders in the database.

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

## 📖 API Documentation
After Starting an aplication interactive API documentation is available via Swagger UI at:

http://localhost:8081/api/swagger-ui/index.html#/

This documentation allows you to explore and test all available endpoints, see request/response models, and read detailed descriptions.

## ⚙️ Setup Instructions

✅ Prerequisites

- Java 17+

- Maven

- MySQL (lub kontener Docker)

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
