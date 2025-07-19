# Task 1: Cấu hình Project Processing Service (Đã hoàn thành)

## Mục tiêu
Cấu hình một dự án Spring Boot mới cho `Processing Service`, bao gồm việc thêm các dependency cần thiết và cấu hình cơ sở dữ liệu H2.

## Các bước thực hiện

- [x] Tạo dự án Spring Boot mới:
    - [x] Sử dụng Spring Initializr (hoặc IDE) để tạo một dự án Maven/Gradle mới.
    - [x] Group: `com.example`
    - [x] Artifact: `processing-service`
    - [x] Java Version: 17 (hoặc phiên bản phù hợp với dự án của bạn)
    - [x] Dependencies:
        - [x] `Spring Web` (để xây dựng RESTful APIs nếu cần, hoặc chỉ để chạy ứng dụng Spring Boot)
        - [x] `Spring Data JPA` (để tương tác với cơ sở dữ liệu)
        - [x] `H2 Database` (cơ sở dữ liệu nhúng, linh động cho phát triển)
        - [x] `Lombok` (để giảm boilerplate code)
        - [x] `Spring Boot DevTools` (tùy chọn, để hỗ trợ phát triển nhanh)
- [x] Cấu hình `application.properties` (hoặc `application.yml`):
    - [ ] Thêm cấu hình cho H2 Database trong `src/main/resources/application.properties` (hoặc `application.yml`).

    ```properties
    # H2 Database Configuration
    spring.h2.console.enabled=true
    spring.h2.console.path=/h2-console
    spring.datasource.url=jdbc:h2:mem:processingdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    spring.datasource.driverClassName=org.h2.Driver
    spring.datasource.username=sa
    spring.datasource.password=
    spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
    spring.jpa.hibernate.ddl-auto=update # hoặc create để tạo schema khi khởi động
    spring.jpa.show-sql=true
    ```
    *Lưu ý:* `ddl-auto=update` sẽ cố gắng cập nhật schema dựa trên các entity. Trong môi trường production, bạn nên sử dụng các công cụ migration như Flyway hoặc Liquibase.