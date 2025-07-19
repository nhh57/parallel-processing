# Task 4: Phát triển ExternalApiClient (Đã hoàn thành)

## Mục tiêu
Tạo một client để `Processing Service` có thể gọi API đến `Unified API Service`.

## Các bước thực hiện

- [x] Tạo lớp `ExternalApiClient.java`:
    - [x] Tạo file `src/main/java/com/example/processingservice/service/ExternalApiClient.java`.
    - [x] Sử dụng `WebClient` của Spring WebFlux (được khuyến nghị cho các cuộc gọi non-blocking) hoặc `RestTemplate` (nếu bạn sử dụng Spring MVC truyền thống). `WebClient` sẽ được sử dụng trong ví dụ này.
    - [x] Inject `WebClient.Builder` và cấu hình base URL của `Unified API Service`.
    - [x] Triển khai phương thức `callUnifiedApiService` để gửi `UnifiedApiRequestDTO` và nhận `ProcessedItemResponse`.

- [x] Cấu hình `unified.api.base-url` trong `application.properties` của `Processing Service`: