# SmartShop - Commercial Management System

SmartShop is a REST API backend application for managing B2B commercial operations for MicroTech Maroc, a computer hardware distributor based in Casablanca.

## 📋 Project Overview

This application manages:
- **650+ active clients** with a progressive loyalty system
- **Multi-payment system** supporting fractional payments per invoice
- **Complete financial traceability** with immutable event history
- **Inventory management** with stock tracking
- **Order processing** with automatic discount calculations

## 🛠️ Technologies Used

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** (Hibernate)
- **MySQL** Database
- **MapStruct** for DTO mapping
- **Lombok** for code simplification
- **Maven** for dependency management

## 📦 Features

### 1. Client Management
- CRUD operations for clients
- Automatic loyalty tier calculation (BASIC, SILVER, GOLD, PLATINUM)
- Client statistics tracking (total orders, total spent)
- Order history per client

### 2. Loyalty System
**Tier Calculation:**
- **BASIC**: Default (0 orders)
- **SILVER**: 3+ orders OR 1,000+ DH spent
- **GOLD**: 10+ orders OR 5,000+ DH spent
- **PLATINUM**: 20+ orders OR 15,000+ DH spent

**Discount Application:**
- **SILVER**: 5% if subtotal ≥ 500 DH
- **GOLD**: 10% if subtotal ≥ 800 DH
- **PLATINUM**: 15% if subtotal ≥ 1,200 DH

### 3. Product Management
- CRUD operations for products
- Soft delete (products used in orders remain visible in history)
- Pagination and search functionality
- Stock management

### 4. Order Management
- Multi-product order creation
- Automatic price calculations (subtotal, discounts, tax, total)
- Promo code support (PROMO-XXXX format, +5% discount)
- Order status management (PENDING, CONFIRMED, CANCELED, REJECTED)
- Stock validation and updates

### 5. Multi-Payment System
- Support for 3 payment methods: CASH, CHECK, BANK TRANSFER
- Fractional payments (multiple payments per order)
- Cash limit: 20,000 DH per payment (Moroccan law)
- Payment status tracking (PENDING, COLLECTED, REJECTED)
- Automatic remaining amount calculation

### 6. Authentication & Authorization
- HTTP Session-based authentication (no JWT, no Spring Security)
- Two roles: ADMIN and CLIENT
- Role-based access control via interceptors

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6+
- Postman or similar API testing tool

### Database Setup

1. Create MySQL database:
```sql
CREATE DATABASE smartshop_db;
```

2. Update `application.properties` if needed:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smartshop_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### Running the Application

1. Clone the repository:
```bash
git clone <repository-url>
cd smartshop
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

### Default Users

The application comes with pre-configured users:

| Username | Password   | Role   |
|----------|------------|--------|
| admin    | admin123   | ADMIN  |
| client   | client123  | CLIENT |

### Sample Data

The application automatically initializes with:
- 2 users (admin and client)
- 5 sample clients
- 5 sample products

## 📚 API Documentation

See [API_DOCUMENTATION.md](API_DOCUMENTATION.md) for detailed endpoint documentation with request/response examples.

## 🏗️ Project Structure

```
smartshop/
├── src/main/java/com/ayadi/smartshop/
│   ├── config/          # Configuration classes
│   ├── controller/      # REST controllers
│   ├── dto/
│   │   ├── request/     # Request DTOs
│   │   └── response/    # Response DTOs
│   ├── entity/          # JPA entities
│   ├── enums/           # Enumerations
│   ├── exception/       # Custom exceptions
│   ├── interceptor/     # Authentication/Authorization interceptors
│   ├── mapper/          # MapStruct mappers
│   ├── repository/      # JPA repositories
│   └── service/         # Business logic
└── src/main/resources/
    └── application.properties
```

## 🧪 Testing

Use Postman or Swagger to test the API endpoints.

### Basic Workflow:

1. **Login as ADMIN**:
   ```
   POST /api/auth/login
   Body: {"username": "admin", "password": "admin123"}
   ```

2. **Create an Order**:
   ```
   POST /api/orders
   Body: {
     "clientId": 1,
     "promoCode": "PROMO-2024",
     "items": [
       {"productId": 1, "quantity": 2}
     ]
   }
   ```

3. **Add Payment**:
   ```
   POST /api/payments
   Body: {
     "orderId": 1,
     "amount": 5000,
     "paymentType": "ESPECES"
   }
   ```

4. **Confirm Order** (after full payment):
   ```
   POST /api/orders/1/confirm
   ```

## 📝 Business Rules

- Orders must be fully paid before confirmation
- Stock is validated before order creation
- Stock is decremented only after order confirmation
- Client tier is recalculated after each confirmed order
- Products used in orders cannot be hard-deleted (soft delete only)
- Cash payments cannot exceed 20,000 DH
- Promo codes must follow format: PROMO-XXXX
- Tax rate is 20% (configurable)

## 🔒 Security

- Session-based authentication
- Role-based access control
- Password stored in plain text (for demo purposes only - use encryption in production)
- Session timeout: 30 minutes

## 📄 License

This project is developed as part of a training program at YouCode.

## 👥 Author

**Mohamed Ayadi**
- Training: YouCode
- Date: November 2025

## 📞 Support

For questions or issues, please contact the development team.
