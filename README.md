#  🛒 SB-Ecom — Spring Boot E-Commerce Backend

A full-featured RESTful e-commerce backend built with **Spring Boot 4**, featuring JWT-based authentication, role-based access control, product management, cart, orders, and more.

---

## 🚀 Tech Stack

| Technology | Details |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.1 |
| Security | Spring Security + JWT (jjwt 0.13.0) |
| Database | H2 (in-memory) |
| ORM | Spring Data JPA / Hibernate |
| Mapping | ModelMapper 3.2.4 |
| Validation | Spring Boot Validation |
| Build Tool | Maven |
| Utilities | Lombok, Jackson |

---

## 📁 Project Structure

```
src/main/java/com/ecommerce/project/
├── config/          # App constants & configuration
├── controller/      # REST controllers (Auth, Category, Product, Cart, Order, Address)
├── exceptions/      # Global exception handler & custom exceptions
├── model/           # JPA entities (User, Product, Cart, Order, Payment, etc.)
├── payload/         # DTOs for request/response
├── repositories/    # Spring Data JPA repositories
├── security/        # JWT utils, filters, WebSecurityConfig
├── service/         # Business logic (interfaces + implementations)
└── util/            # Auth utility
```

---

## ⚙️ Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+

### Run the Application

```bash
# Clone the repository
git clone https://github.com/your-username/sb-ecom.git
cd sb-ecom

# Build and run
./mvnw spring-boot:run
```

The server starts at: `http://localhost:8080`

H2 Console (dev): `http://localhost:8080/h2-console`

---

## 🔐 Authentication

This project uses **JWT stored in HTTP-only cookies**. After signing in, the token is automatically attached to every subsequent request via cookie.

### Roles

| Role | Access |
|---|---|
| `ROLE_USER` | Browse products, manage own cart, place orders |
| `ROLE_SELLER` | Manage products |
| `ROLE_ADMIN` | Full access — manage categories, products, users |

---

## 📡 API Endpoints

### 🔑 Auth — `/api/auth`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/signup` | Public | Register a new user |
| POST | `/api/auth/signin` | Public | Login and receive JWT cookie |
| POST | `/api/auth/signout` | Authenticated | Logout and clear JWT cookie |
| GET | `/api/auth/username` | Authenticated | Get logged-in username |
| GET | `/api/auth/user` | Authenticated | Get logged-in user details |

#### Signup Request Body
```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret123",
  "role": ["user"]
}
```

#### Signin Request Body
```json
{
  "username": "john",
  "password": "secret123"
}
```

---

### 📦 Categories — `/api/...category`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/public/category` | Public | Get all categories (paginated) |
| POST | `/api/admin/category` | Admin | Create a new category |
| PUT | `/api/admin/category/{categoryId}` | Admin | Update a category |
| DELETE | `/api/admin/category/{categoryId}` | Admin | Delete a category |

#### Query Parameters (GET)
| Param | Default | Description |
|---|---|---|
| `pageNumber` | 0 | Page index |
| `pageSize` | 10 | Items per page |
| `sortBy` | `categoryName` | Sort field |
| `sortOrder` | `asc` | Sort direction |

#### Category Request Body
```json
{
  "categoryName": "Electronics"
}
```

---

### 🛍️ Products — `/api/...products`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/public/products` | Public | Get all products (paginated) |
| GET | `/api/public/categories/{categoryId}/products` | Public | Products by category |
| GET | `/api/public/categories/keyword/{keyword}` | Public | Search products by keyword |
| POST | `/api/admin/categories/{categoryId}/product` | Admin | Add a new product |
| PUT | `/api/admin/products/{productId}` | Admin | Update product details |
| PUT | `/api/admin/products/{productId}/image` | Admin | Upload product image (multipart) |
| DELETE | `/api/admin/products/{productId}` | Admin | Delete a product |

#### Product Request Body
```json
{
  "productName": "Laptop",
  "description": "High-performance laptop",
  "price": 75000.00,
  "discount": 10,
  "quantity": 50
}
```

---

### 🛒 Cart — `/api/carts`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/carts/products/{productId}/quantity/{quantity}` | User | Add product to cart |
| GET | `/api/carts` | Admin | Get all carts |
| GET | `/api/users/carts` | User | Get logged-in user's cart |
| PUT | `/api/carts/products/{productId}/quantity/{operation}` | User | Update quantity (`increment` / `delete`) |
| DELETE | `/api/carts/{cartId}/product/{productId}` | User | Remove product from cart |

---

### 📬 Address — `/api/addresses`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/addresses` | User | Add a new address |
| GET | `/api/addresses` | Admin | Get all addresses |
| GET | `/api/addresses/{addressId}` | User | Get address by ID |
| GET | `/api/users/addresses` | User | Get logged-in user's addresses |
| PUT | `/api/addresses/{addressId}` | User | Update an address |
| DELETE | `/api/addresses/{addressId}` | User | Delete an address |

#### Address Request Body
```json
{
  "street": "123 MG Road",
  "buildingName": "Tech Plaza",
  "city": "Bengaluru",
  "state": "Karnataka",
  "country": "India",
  "pincode": "560001"
}
```

---

### 📋 Orders — `/api/order`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/order/users/payment/{paymentMethod}` | User | Place an order |

#### Order Request Body
```json
{
  "addressId": 1,
  "pgName": "Razorpay",
  "pgPaymentId": "pay_abc123",
  "pgStatus": "SUCCESS",
  "pgResponseMessage": "Payment successful"
}
```

---

## 📸 API Screenshots (Postman)

> Add your Postman screenshots in the `images/` folder and reference them below.

### Signup
![Signup](images/signup.png)

### Signin
![Signin](images/signin.png)

### Get All Products
![Get Products](images/get-products.png)

### Add to Cart
![Add to Cart](images/add-to-cart.png)

### Place Order
![Place Order](images/place-order.png)

> 💡 **Tip:** Export your Postman collection as a `.json` file and commit it to the repo — other developers can import it and start testing instantly without setting up requests manually.

---

## 🧩 Features

- ✅ JWT Authentication with HTTP-only cookies
- ✅ Role-based authorization (Admin, Seller, User)
- ✅ Category management with pagination & sorting
- ✅ Product management with image upload support
- ✅ Shopping cart with quantity management
- ✅ Address management per user
- ✅ Order placement with payment gateway fields
- ✅ Global exception handling
- ✅ DTO-based request/response mapping
- ✅ Input validation with Bean Validation

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
