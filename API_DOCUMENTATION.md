# SmartShop API Documentation

Base URL: `http://localhost:8080/api`

## Authentication

All endpoints (except `/auth/login`) require authentication via HTTP Session.

### Login
```http
POST /auth/login
```

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "username": "admin",
  "role": "ADMIN"
}
```

### Logout
```http
POST /auth/logout
```

**Response:** `200 OK`
```json
"Logged out successfully"
```

### Get Current User
```http
GET /auth/me
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "username": "admin",
  "role": "ADMIN"
}
```

---

## Clients

### Create Client
```http
POST /clients
```
**Role Required:** ADMIN

**Request Body:**
```json
{
  "name": "Tech Solutions SARL",
  "email": "contact@techsolutions.ma"
}
```

**Response:** `201 CREATED`
```json
{
  "id": 1,
  "name": "Tech Solutions SARL",
  "email": "contact@techsolutions.ma",
  "tier": "BASIC",
  "totalOrders": 0,
  "totalSpent": 0.00,
  "firstOrderDate": null,
  "lastOrderDate": null
}
```

### Get Client
```http
GET /clients/{id}
```
**Role Required:** ADMIN

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Tech Solutions SARL",
  "email": "contact@techsolutions.ma",
  "tier": "SILVER",
  "totalOrders": 5,
  "totalSpent": 12500.00,
  "firstOrderDate": "2025-01-15T10:30:00",
  "lastOrderDate": "2025-03-20T14:45:00"
}
```

### Get All Clients
```http
GET /clients
```
**Role Required:** ADMIN

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "Tech Solutions SARL",
    "email": "contact@techsolutions.ma",
    "tier": "SILVER",
    "totalOrders": 5,
    "totalSpent": 12500.00
  }
]
```

### Update Client
```http
PUT /clients/{id}
```
**Role Required:** ADMIN

**Request Body:**
```json
{
  "name": "Tech Solutions Updated",
  "email": "new@techsolutions.ma"
}
```

**Response:** `200 OK`

### Delete Client
```http
DELETE /clients/{id}
```
**Role Required:** ADMIN

**Response:** `200 OK`
```json
"Client deleted successfully"
```

### Get Client Orders
```http
GET /clients/{id}/orders
```
**Role Required:** ADMIN

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "clientId": 1,
    "clientName": "Tech Solutions SARL",
    "createdAt": "2025-03-20T14:45:00",
    "subtotal": 10000.00,
    "discountAmount": 500.00,
    "subtotalAfterDiscount": 9500.00,
    "taxAmount": 1900.00,
    "totalAmount": 11400.00,
    "promoCode": "PROMO-2024",
    "status": "CONFIRMED",
    "remainingAmount": 0.00
  }
]
```

---

## Products

### Create Product
```http
POST /products
```
**Role Required:** ADMIN

**Request Body:**
```json
{
  "name": "Laptop Dell XPS 15",
  "price": 15000.00,
  "stock": 50
}
```

**Response:** `201 CREATED`
```json
{
  "id": 1,
  "name": "Laptop Dell XPS 15",
  "price": 15000.00,
  "stock": 50
}
```

### Get Product
```http
GET /products/{id}
```
**Role Required:** ADMIN

**Response:** `200 OK`

### Get All Products (Paginated)
```http
GET /products?page=0&size=10
```
**Role Required:** ADMIN

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "name": "Laptop Dell XPS 15",
      "price": 15000.00,
      "stock": 50
    }
  ],
  "totalElements": 5,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

### Search Products
```http
GET /products/search?name=laptop&page=0&size=10
```
**Role Required:** ADMIN

**Response:** `200 OK`

### Update Product
```http
PUT /products/{id}
```
**Role Required:** ADMIN

**Request Body:**
```json
{
  "name": "Laptop Dell XPS 15 Updated",
  "price": 14500.00,
  "stock": 45
}
```

**Response:** `200 OK`

### Delete Product
```http
DELETE /products/{id}
```
**Role Required:** ADMIN

**Response:** `200 OK`
```json
"Product deleted successfully"
```

---

## Orders

### Create Order
```http
POST /orders
```
**Role Required:** ADMIN

**Request Body:**
```json
{
  "clientId": 1,
  "promoCode": "PROMO-2024",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 5
    }
  ]
}
```

**Response:** `201 CREATED`
```json
{
  "id": 1,
  "clientId": 1,
  "clientName": "Tech Solutions SARL",
  "createdAt": "2025-03-20T14:45:00",
  "subtotal": 34250.00,
  "discountAmount": 1712.50,
  "subtotalAfterDiscount": 32537.50,
  "taxAmount": 6507.50,
  "totalAmount": 39045.00,
  "promoCode": "PROMO-2024",
  "status": "PENDING",
  "remainingAmount": 39045.00,
  "orderItems": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Laptop Dell XPS 15",
      "quantity": 2,
      "price": 15000.00,
      "lineTotal": 30000.00
    },
    {
      "id": 2,
      "productId": 3,
      "productName": "Logitech Mouse MX Master",
      "quantity": 5,
      "price": 850.00,
      "lineTotal": 4250.00
    }
  ]
}
```

### Get Order
```http
GET /orders/{id}
```
**Role Required:** ADMIN

**Response:** `200 OK`

### Confirm Order
```http
POST /orders/{id}/confirm
```
**Role Required:** ADMIN

**Note:** Order must be fully paid (remainingAmount = 0)

**Response:** `200 OK`
```json
{
  "id": 1,
  "status": "CONFIRMED",
  ...
}
```

### Cancel Order
```http
POST /orders/{id}/cancel
```
**Role Required:** ADMIN

**Note:** Only PENDING orders can be canceled

**Response:** `200 OK`
```json
{
  "id": 1,
  "status": "CANCELED",
  ...
}
```

---

## Payments

### Add Payment
```http
POST /payments
```
**Role Required:** ADMIN

**Request Body (Cash):**
```json
{
  "orderId": 1,
  "amount": 10000.00,
  "paymentType": "ESPECES",
  "reference": "RECU-001"
}
```

**Request Body (Check):**
```json
{
  "orderId": 1,
  "amount": 15000.00,
  "paymentType": "CHEQUE",
  "reference": "CHQ-7894561",
  "bank": "BMCE Bank",
  "dueDate": "2025-04-15"
}
```

**Request Body (Bank Transfer):**
```json
{
  "orderId": 1,
  "amount": 14045.00,
  "paymentType": "VIREMENT",
  "reference": "VIR-2025-03-20-4521",
  "bank": "Attijariwafa Bank"
}
```

**Response:** `201 CREATED`
```json
{
  "id": 1,
  "orderId": 1,
  "paymentNumber": 1,
  "amount": 10000.00,
  "paymentType": "ESPECES",
  "paymentDate": "2025-03-20T15:00:00",
  "collectionDate": null,
  "status": "ENCAISSE",
  "reference": "RECU-001",
  "bank": null,
  "dueDate": null
}
```

### Get Order Payments
```http
GET /payments/order/{orderId}
```
**Role Required:** ADMIN

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "orderId": 1,
    "paymentNumber": 1,
    "amount": 10000.00,
    "paymentType": "ESPECES",
    "status": "ENCAISSE"
  },
  {
    "id": 2,
    "orderId": 1,
    "paymentNumber": 2,
    "amount": 15000.00,
    "paymentType": "CHEQUE",
    "status": "EN_ATTENTE"
  }
]
```

---

## Error Responses

### 400 Bad Request (Validation Error)
```json
{
  "timestamp": "2025-03-20T15:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Email must be valid",
  "path": "/api/clients"
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2025-03-20T15:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication required",
  "path": "/api/clients"
}
```

### 403 Forbidden
```json
{
  "timestamp": "2025-03-20T15:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Admin access required",
  "path": "/api/clients"
}
```

### 404 Not Found
```json
{
  "timestamp": "2025-03-20T15:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Client not found with id: 999",
  "path": "/api/clients/999"
}
```

### 422 Unprocessable Entity (Business Rule Violation)
```json
{
  "timestamp": "2025-03-20T15:30:00",
  "status": 422,
  "error": "Business Rule Violation",
  "message": "Insufficient stock for product: Laptop Dell XPS 15",
  "path": "/api/orders"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2025-03-20T15:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/orders"
}
```

---

## Complete Workflow Example

### Scenario: Create and Process an Order

1. **Login as ADMIN**
```http
POST /api/auth/login
Body: {"username": "admin", "password": "admin123"}
```

2. **Create Order for Client**
```http
POST /api/orders
Body: {
  "clientId": 1,
  "promoCode": "PROMO-2024",
  "items": [
    {"productId": 1, "quantity": 1}
  ]
}
Response: Order created with totalAmount = 15390 DH, remainingAmount = 15390 DH
```

3. **Add First Payment (Cash)**
```http
POST /api/payments
Body: {
  "orderId": 1,
  "amount": 10000,
  "paymentType": "ESPECES",
  "reference": "RECU-001"
}
Response: Payment added, remainingAmount = 5390 DH
```

4. **Add Second Payment (Bank Transfer)**
```http
POST /api/payments
Body: {
  "orderId": 1,
  "amount": 5390,
  "paymentType": "VIREMENT",
  "reference": "VIR-2025-001",
  "bank": "Attijariwafa Bank"
}
Response: Payment added, remainingAmount = 0 DH
```

5. **Confirm Order**
```http
POST /api/orders/1/confirm
Response: Order confirmed, stock updated, client tier recalculated
```

6. **Check Client Statistics**
```http
GET /api/clients/1
Response: Client tier updated based on new order
```

---

## Notes

- All monetary amounts are in Moroccan Dirhams (DH)
- All dates are in ISO 8601 format
- Session cookie is automatically managed by the browser/Postman
- Tax rate is 20% (configurable in application.properties)
- Cash payment limit: 20,000 DH per payment
- Promo code format: PROMO-XXXX (4 alphanumeric characters)
