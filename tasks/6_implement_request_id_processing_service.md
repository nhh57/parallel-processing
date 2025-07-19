# Task 6: Triển khai RequestId cho Processing Service (Đã hoàn thành)

## Mục tiêu
Đảm bảo `requestId` được tạo, truyền đi và sử dụng trong logging xuyên suốt `Processing Service`.

## Các bước thực hiện

- [x] Tạo một Filter để tạo và quản lý `requestId`:
    - [x] Tạo lớp `RequestIdFilter.java` trong `src/main/java/com/example/processingservice/config`.
    - [x] Sử dụng `MDC` (Mapped Diagnostic Context) để gắn `requestId` vào luồng hiện tại.

- [x] Cấu hình `logback-spring.xml` để hiển thị `requestId` trong log:
    - [x] Tạo file `src/main/resources/logback-spring.xml`.
    - [x] Thêm `%X{requestId}` vào pattern của appender.

- [x] Đảm bảo `requestId` được truyền đi trong `UnifiedApiRequestDTO`:
    - [x] Trong `CardPinProcessingService`, khi tạo `UnifiedApiRequestDTO`, hãy truyền `requestId` hiện tại từ MDC.