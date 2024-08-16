# Exchange Rate Service

## Overview

The Exchange Rate Service is a Spring Boot application that fetches and stores exchange rates from a third-party API. The service uses a PostgreSQL database to store exchange rates and snapshots of the exchange rates at specific times. It also features a scheduled task to periodically update the exchange rates.

## Technologies Used

- **Java 21**: The core programming language used for the application.
- **Spring Boot**: Provides the framework for building the microservice.
- **Spring Data JPA**: Used for data access and ORM functionality.
- **Spring Cloud OpenFeign**: Simplifies the process of consuming RESTful web services.
- **MapStruct**: Used for mapping between DTOs and entities.
- **Lombok**: Reduces boilerplate code by generating getters, setters, and other common methods.
- **PostgreSQL**: The relational database used to store exchange rates and snapshots.
- **Flyway**: Manages database migrations.
- **ShedLock**: Ensures that scheduled tasks do not run concurrently across multiple instances.
- **Docker**: Containerizes the application for consistent runtime environments.
- **Docker Compose**: Orchestrates multi-container Docker applications, including the application and the PostgreSQL database.
- **JUnit**: For unit testing.
- **Testcontainers**: Provides a way to run tests with real database instances in Docker containers.
- **Maven**: Builds and manages the Java project.

## Running the Application

### Prerequisites

- Docker and Docker Compose installed.

### Building and Running with Docker

1. **Go to docker folder**
   ```bash
   cd docker/
   ```
2. **Open .env file and change EXCHANGE_RATE_API_KEY**
   ```env
   EXCHANGE_RATE_API_KEY=
   ```  

3. **Build the Docker Image**:
   ```bash
   docker-compose build
   ```
4. **Run the Application with DB**:
   ```bash
   docker-compose up -d
   ```
5. **(Optional) Run DB ONLY**:
   ```bash
   docker-compose -f docker-compose-db-only.yml up -d
   ```   
### Building and Running with Docker

1. **Go to docker folder**
   ```bash
   cd docker/
   ```
2. **Run DB**:
   ```bash
   docker-compose -f docker-compose-db-only.yml up -d
   ```   
3. **You need to add environment variables:**
   ```env
   EXCHANGE_RATE_API_KEY=
   ```   
4. **Set dev profile for spring app:**
   ```env
   -Dspring-boot.run.profiles=dev
   ```   
   

The service will be available at `http://localhost:8080`.

### Endpoints

- **GET /exchange-rates/pair/{from}/{to}/latest**: Fetches the latest exchange rate between two currencies.
- **POST /exchange-rates/fetch**: Triggers an immediate fetch of the latest exchange rates from the API.


## Database Setup

### SQL Migration

The application uses Flyway for database migrations. The migration scripts are located in the `./resources/db/migration/` directory. The main scripts include:

- `V1_20240815_182305__create_table_exchange_rate.sql`: Creates the `t_exchange_rate` table and associated sequence.
- `V1_20240815_182300__create_table_exchange_rate_snapshot.sql`: Creates the `t_exchange_rate_snapshot` table and associated sequence.
- `V1_20240815_182310__create_table_shedlock.sql`: Creates the `shedlock` table for distributed locking.

### Database Tables

- **t_exchange_rate**: Stores exchange rate data between different currencies.
- **t_exchange_rate_snapshot**: Stores snapshots of exchange rates at specific times.
- **shedlock**: Used by ShedLock to ensure that scheduled tasks are not executed concurrently across multiple instances.

## License

This project is licensed under the MIT License.
