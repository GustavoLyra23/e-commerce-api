# E-Commerce API

This is an example of an E-Commerce API built with Java, Spring Boot, and Maven. It provides a comprehensive set of functionalities for user authentication, product management, file(product image) storage using AWS S3, payment processing using Stripe, logging with SLF4J, and caching with Redis. The API is designed to be a robust and scalable solution for e-commerce platforms, inspired by leading e-commerce giants like Amazon and Mercado Libre.

The API supports secure user authentication and authorization, allowing users to register, log in, and manage their profiles. It also includes features for managing products, such as adding,post comments, reply comments, product updating, and deleting products, as well as listing all available products. File storage is handled through AWS S3, enabling efficient and secure storage of user profile pictures and product images.

For payment processing, the API integrates with Stripe, providing seamless payment link creation and webhook handling for real-time payment updates. Logging is implemented using SLF4J, ensuring that all application activities are properly logged for monitoring and debugging purposes. Additionally, Redis is used for caching to improve the performance and scalability of the application.

This API serves as a practical example for building a modern e-commerce platform, leveraging best practices and technologies to deliver a high-quality solution.

## Technologies Used
- H2 In Memory Database
- Java
- Spring Boot
- Maven
- AWS S3
- SQL
- Stripe
- Docker
- Docker Compose
- SLF4J
- Redis

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven
- Docker
- Docker Compose
- AWS account with S3 bucket
- Stripe account with API key
- Redis
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

5. **Configure Redis:**

    Update the `application.properties` file with your Redis configuration.

6. **Run the SQL script:**

    Execute the SQL script located at `src/main/resources/import.sql` to set up the initial database schema and data.

7. **Build the project:**

    ```sh
    mvn clean install
    ```

8. **Run the application using Docker Compose:**

    ```sh
    docker-compose up --build
    ```

9. **Set up Ngrok for local webhook testing:**

    ```sh
    ngrok http 8080
    ```

    Copy the generated Ngrok URL and add it to your Stripe webhook dashboard.

### Postman Collection

A Postman collection is provided in the `src/main/resources` directory. You can import this collection into Postman to easily test the API endpoints. The collection includes pre-configured requests for user authentication, product management, file uploads, and payment processing.

## Project Structure

- `src/main/java/com/gustavolyra/e_commerce_api/` - Main application code
- `src/main/resources/` - Configuration files and SQL scripts
- `src/main/resources/banner.txt` - Custom banner displayed on application startup
- `src/main/resources/e_commerce_api.postman_collection.json` - Postman collection for testing API endpoints

### Swagger Documentation

This API uses Swagger for API documentation(Endpoint consult). Once the application is running, you can access the Swagger UI at:

```bash
http://localhost:8080/swagger-ui.html
```
