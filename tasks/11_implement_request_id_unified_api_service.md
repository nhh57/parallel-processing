# Task 11: Triển khai RequestId cho Unified API Service

## Mục tiêu
Đảm bảo `requestId` được nhận từ yêu cầu đến và sử dụng trong logging xuyên suốt `Unified API Service`.

## Các bước thực hiện

- [ ] Cấu hình `logback-spring.xml` để hiển thị `requestId` trong log:
    - [ ] Tạo file `src/main/resources/logback-spring.xml` trong `unified-api-service`.
    - [ ] Thêm `%X{requestId}` vào pattern của appender.

- [ ] Đảm bảo `requestId` được nhận và đặt vào MDC trong `UnifiedProcessingController`:
    - [ ] `UnifiedProcessingController` đã được cập nhật trong Task 9 để xử lý `requestId` từ header `X-Request-ID` hoặc từ `requestDTO` và đặt vào MDC.
    - [ ] Đảm bảo `ItemStatusCheckerService` cũng lấy `requestId` từ MDC hoặc từ `requestDTO` để sử dụng trong log của nó. (Đã thực hiện trong Task 10).