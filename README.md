# Vending Machine API

A RESTful API for a vending machine system built with Spring Boot, featuring user authentication, product management, and coin-based transactions.

## Features

- **User Management**: Registration, authentication, and role-based access control
- **Product Management**: CRUD operations for products (seller-only)
- **Coin Deposits**: Accept 5, 10, 20, 50, and 100 cent coins
- **Purchase System**: Buy products with deposited coins and receive change
- **Security**: JWT-based authentication and authorization
- **Comprehensive Testing**: Full API test coverage

## Technologies Used

- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Security 6.x**
- **Spring Data JPA**
- **MySQL Database** (requires MySQL connection in the application.properties)
- **JWT Authentication**
- **Maven**
- **JUnit 5**
- **MockMvc**

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Users
- `GET /api/users` - Get all users (paginated)
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `GET /api/users/me` - Get current user

### Products
- `GET /api/products` - Get all products (paginated, public)
- `GET /api/products/{id}` - Get product by ID (public)
- `POST /api/products` - Create product (seller only)
- `PUT /api/products/{id}` - Update product (seller only, own products)
- `DELETE /api/products/{id}` - Delete product (seller only, own products)
- `GET /api/products/seller/{sellerId}` - Get products by seller
- `GET /api/products/seller` - Get current seller's products

### Vending Machine Operations
- `POST /api/vending/deposit` - Deposit coins (buyer only)
- `POST /api/vending/buy` - Buy products (buyer only)
- `POST /api/vending/reset` - Reset deposit (buyer only)

### Additional Endpoints
- `GET /api/products/available` - Get products with available stock
- `GET /api/users/me` - Get current user (includes deposit balance)

## User Roles

### BUYER
- Can deposit coins (5, 10, 20, 50, 100 cents)
- Can buy products
- Can reset their deposit
- Can view their balance

### SELLER
- Can create, update, and delete their own products
- Cannot access vending machine operations

### ADMIN
- Can manage all users
- Full access to user operations

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Installation

1. Clone the repository
```bash
git clone <repository-url>
cd vending-machine-api
```

2. Build the project
```bash
mvn clean install
```

3. Run the application
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### Database Console
A MySQL connection is required, with user and password connection on their own.
You can access the connection through MySQL Workbench or Shell.

## API Usage Examples

### 1. Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "buyer1",
    "password": "password123",
    "role": "BUYER"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "buyer1",
    "password": "password123"
  }'
```

### 3. Create a Product (Seller)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <seller-token>" \
  -d '{
    "productName": "Coca Cola",
    "cost": 150,
    "amountAvailable": 10
  }'
```

### 4. Deposit Coins (Buyer)
```bash
curl -X POST http://localhost:8080/api/vending/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <buyer-token>" \
  -d '{
    "amount": 50
  }'
```

### 5. Buy a Product
```bash
curl -X POST http://localhost:8080/api/vending/buy \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <buyer-token>" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

### 6. Reset Deposit
```bash
curl -X POST http://localhost:8080/api/vending/reset \
  -H "Authorization: Bearer <buyer-token>"
```

## Data Models

### User
- `id`: Long (auto-generated)
- `username`: String (unique, 3-20 characters)
- `password`: String (encoded, min 6 characters)
- `role`: Enum (BUYER, SELLER, ADMIN)
- `deposit`: Integer (cents)

### Product
- `id`: Long (auto-generated)
- `productName`: String (required, 1-50 characters)
- `cost`: Integer (cents, positive)
- `amountAvailable`: Integer (non-negative)
- `sellerId`: Long (foreign key to User)

### Accepted Coins
- 5 cents
- 10 cents
- 20 cents
- 50 cents
- 100 cents

## Security Features

- JWT-based authentication
- Role-based access control
- Password encryption using BCrypt
- CORS configuration
- Input validation
- SQL injection prevention

## Error Handling

The API uses standard HTTP status codes and returns detailed error messages:

- `400 Bad Request` - Invalid input or business logic errors
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Username already exists
- `500 Internal Server Error` - Server errors

Example error response:
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid coin denomination. Only 5, 10, 20, 50, and 100 cent coins are accepted",
  "path": "/api/vending/deposit"
}
```

## Testing

Run all tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=VendingMachineIntegrationTest
```

### Test Coverage
- Authentication endpoints
- User CRUD operations
- Product CRUD operations
- Vending machine operations
- Security and authorization
- Edge cases and error scenarios

## Edge Cases Handled

1. **Insufficient Funds**: Prevent purchases when deposit is insufficient
2. **Out of Stock**: Prevent purchases when product quantity is insufficient
3. **Invalid Coins**: Only accept 5, 10, 20, 50, and 100 cent coins
4. **Change Calculation**: Return optimal change using available coin denominations
5. **Authorization**: Sellers can only modify their own products
6. **Concurrent Access**: Handle multiple users accessing the same product
7. **Input Validation**: Comprehensive validation for all inputs

## Production Considerations

- **Database**: Replace H2 with PostgreSQL/MySQL for production
- **Security**: Use environment variables for JWT secret
- **Logging**: Configured with appropriate log levels
- **Monitoring**: Health checks and metrics endpoints included
- **Deployment**: Docker support can be added
- **Caching**: Redis can be integrated for better performance

## Future Enhancements

- Product categories and search functionality
- Purchase history and analytics
- Email notifications
- Product images
- Inventory management
- Multiple payment methods
- Admin dashboard

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request


## Generative AI Usage

This project was developed with assistance from Claude (Anthropic) for code generation, architecture design, and documentation. The AI helped with:
- API design and structure
- Spring Boot configuration
- Security implementation
- Test case generation
- Documentation creation

**AI Chat Link**: [[Claude conversation link here](https://claude.ai/chat/a9df7432-6463-4355-a373-2f39e4daedc8)]
