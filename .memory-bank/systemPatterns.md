# System Patterns

**System Architecture:**

The system follows a microservices architecture with two main services: `Processing Service` and `Unified API Service`. The `Processing Service` handles the parallel processing of Card and PIN objects, while the `Unified API Service` provides a single endpoint for checking the status of these objects.

**Key Technical Decisions:**

*   Using Spring Boot for both services to simplify development and deployment.
*   Using `java.util.concurrent.CompletableFuture` and `ExecutorService` for parallel processing in the `Processing Service`.
*   Defining a clear API contract between the two services using DTOs.
*   Using H2 database for persistence.

**Design Patterns in Use:**

*   **Microservices:** The system is divided into two independent services that can be deployed and scaled separately.
*   **Asynchronous Processing:** `CompletableFuture` is used to enable non-blocking asynchronous calls to the `Unified API Service`.
*   **Data Transfer Object (DTO):** DTOs are used to transfer data between the two services, decoupling them from the underlying data models.
*   **Repository Pattern:** Spring Data JPA repositories are used to abstract database access.

**Component Relationships:**

*   `Processing Service` depends on `Unified API Service` for status checks.
*   Both services use a shared set of DTOs for communication.
*   `Processing Service` uses Spring Data JPA repositories to interact with the H2 database.