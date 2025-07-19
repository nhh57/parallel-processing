# Task 7: Cấu hình Project Unified API Service

## Mục tiêu
Cấu hình một dự án Spring Boot mới cho `Unified API Service`, bao gồm việc thêm các dependency cần thiết.

## Các bước thực hiện

- [ ] Tạo dự án Spring Boot mới:
    - [ ] Sử dụng Spring Initializr (hoặc IDE) để tạo một dự án Maven/Gradle mới.
    - [ ] Group: `com.example`
    - [ ] Artifact: `unified-api-service`
    - [ ] Java Version: 17 (hoặc phiên bản phù hợp với dự án của bạn)
    - [ ] Dependencies:
        - [ ] `Spring Web` (để xây dựng RESTful APIs)
        - [ ] `Lombok` (để giảm boilerplate code)
        - [ ] `Spring Boot DevTools` (tùy chọn, để hỗ trợ phát triển nhanh)
- [ ] Cấu hình `application.properties` (hoặc `application.yml`):
    - [ ] Thêm cấu hình cổng cho `Unified API Service` trong `src/main/resources/application.properties` (hoặc `application.yml`).
    - [ ] Đảm bảo cổng khác với `Processing Service` (ví dụ: 8081).

    ```properties
    # Server Port
    server.port=8081