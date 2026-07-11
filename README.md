# Warehouse Management System

A comprehensive Warehouse Management System (WMS) developed using Java and Spring Boot to streamline warehouse operations, inventory tracking,
order fulfillment, and stock movement management.

The application provides secure REST APIs for managing warehouses, products, inventory, shipments, purchase orders, sales orders, transfers, user roles, 
and permissions while implementing authentication and authorization using JWT.

---

## Features

- JWT Authentication & Authorization
- User & Role Management
- Warehouse Management
- Inventory Tracking
- Product Management
- Category & Brand Management
- Stock Movement Tracking
- Purchase Order Management
- Sales Order Management
- Shipment Management
- Picking Process
- Transfer Management
- Partner Management
- Organization Management
- Barcode Management
- Email Notifications
- Swagger API Documentation
- Request Validation
- Global Exception Handling

---

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- MapStruct
- Lombok

### Database
- PostgreSQL

### Authentication
- JWT (JSON Web Token)

### Documentation
- Swagger / OpenAPI

### Build Tool
- Gradle

---

## Project Architecture

Client
↓
REST API
↓
Spring Security (JWT)
↓
Controllers
↓
Services
↓
Repositories
↓
PostgreSQL

---

## Main Modules

- Authentication
- User Management
- Role & Permission Management
- Warehouse Management
- Product Management
- Category Management
- Inventory Management
- Purchase Orders
- Sales Orders
- Inbound Receipts
- Shipments
- Picking
- Transfers
- Partners
- Organizations
- Locations
- Zones
- Shelves
- Stock Movements
- Invoices

---

## Security

The application uses:

- JWT Authentication
- Spring Security
- Role-Based Authorization

---

## API Documentation

Swagger UI is available after running the application.

```
http://localhost:8080/swagger-ui/index.html
```

---

## Installation

```bash
git clone https://github.com/yourusername/warehouse-management-system.git

cd warehouse-management-system
```

Configure:

- PostgreSQL
- Mail credentials
- JWT Secret

Run

```bash
./gradlew bootRun
```

---

## Future Improvements

- Docker Support
- Docker Compose
- Redis Caching
- RabbitMQ Integration
- Unit & Integration Tests
- CI/CD Pipeline
- Kubernetes Deployment
- Audit Logging
- Monitoring with Prometheus & Grafana

---

## Author

Muhammad Babazada
Backend Developer
