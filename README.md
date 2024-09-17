# E-Commerce API Example

This is an example of an E-Commerce API built with Java, Spring Boot, and Maven. It provides functionalities for user authentication, product management, file storage using AWS S3, and payment processing using Stripe.

## Technologies Used

- Java
- Spring Boot
- Maven
- AWS S3
- SQL
- Stripe
- Docker
- Docker Compose

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven
- Docker
- Docker Compose
- AWS account with S3 bucket
- Stripe account with API key
- Ngrok (for local webhook testing)

### Installation

1. **Clone the repository:**

    ```sh
    git clone https://github.com/GustavoLyra23/e_commerce_api.git
    cd e_commerce_api
    ```

2. **Configure the database:**

    Update the `application.properties` file with your database configuration.

3. **Configure AWS S3:**

    Update the `application.properties` file with your AWS S3 configuration.

4. **Configure Stripe:**

    Update the `application.properties` file with your Stripe API key.

5. **Run the SQL script:**

    Execute the SQL script located at `src/main/resources/import.sql` to set up the initial database schema and data.

6. **Build the project:**

    ```sh
    mvn clean install
    ```

7. **Run the application using Docker Compose:**

    ```sh
    docker-compose up --build
    ```

8. **Set up Ngrok for local webhook testing:**

    ```sh
    ngrok http 8080
    ```

    Copy the generated Ngrok URL and add it to your Stripe webhook dashboard.

## Project Structure

- `src/main/java/com/gustavolyra/e_commerce_api/` - Main application code
- `src/main/resources/` - Configuration files and SQL scripts
- `src/main/resources/banner.txt` - Custom banner displayed on application startup

## Key Components

### SecurityFilter

Handles authentication by validating tokens and setting the security context.

### S3Service

Manages file uploads to AWS S3.

### StripeService

Handles payment processing and webhook events from Stripe.

## Usage

### Endpoints

- **User Authentication:**
  - `POST /login` - Authenticate user and return a token
  - `POST /register` - Register a new user

- **Product Management:**
  - `GET /products` - List all products
  - `POST /products` - Add a new product (Admin only)
  - `PUT /products/{id}` - Update a product (Admin only)
  - `DELETE /products/{id}` - Delete a product (Admin only)

- **File Upload:**
  - `POST /upload` - Upload a file to AWS S3

- **Payment Processing:**
  - `POST /create-payment-link` - Create a payment link using Stripe
  - `POST /webhook` - Handle webhook events from Stripe

### Swagger Documentation

This API uses Swagger for API documentation. Once the application is running, you can access the Swagger UI at:
