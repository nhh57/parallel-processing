# Task 2: Định nghĩa Data Models (Card, Pin, DTOs) (Đã hoàn thành)

## Mục tiêu
Định nghĩa các lớp Java đại diện cho dữ liệu `Card`, `Pin` và các Data Transfer Objects (DTOs) để giao tiếp giữa `Processing Service` và `Unified API Service`.

## Các bước thực hiện

- [x] Tạo lớp `Card.java`:
    - [x] Tạo file `src/main/java/com/example/processingservice/model/Card.java` (hoặc `com/example/unifiedapiservice/model/Card.java` nếu bạn quyết định shared module).
    - [x] Định nghĩa các trường dữ liệu cần thiết cho một thẻ (ví dụ: `id`, `cardNumber`, `status`, `processingRequestId`).
    - [x] Sử dụng `@Entity` và `@Table` của JPA nếu `Card` là một entity được lưu trữ trong DB.
    - [x] Sử dụng Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`) để giảm boilerplate code.

- [x] Tạo lớp `Pin.java`:
    - [x] Tạo file `src/main/java/com/example/processingservice/model/Pin.java`.
    - [x] Định nghĩa các trường dữ liệu cần thiết cho một PIN (ví dụ: `id`, `pinCode`, `status`, `processingRequestId`).
    - [x] Sử dụng `@Entity` và `@Table` của JPA nếu `Pin` là một entity được lưu trữ trong DB.
    - [x] Sử dụng Lombok.

- [x] Tạo Enum `ItemType.java`:
    - [x] Tạo file `src/main/java/com/example/processingservice/dto/ItemType.java`.
    - [x] Enum này sẽ được sử dụng trong `UnifiedApiRequestDTO` để phân biệt loại đối tượng.

- [x] Tạo lớp `UnifiedApiRequestDTO.java`:
    - [x] Tạo file `src/main/java/com/example/processingservice/dto/UnifiedApiRequestDTO.java`.
    - [x] DTO này sẽ được gửi từ `Processing Service` đến `Unified API Service`. Nó sẽ chứa `ItemType` và dữ liệu cụ thể của item.

- [x] Tạo lớp `ProcessedItemResponse.java`:
    - [x] Tạo file `src/main/java/com/example/processingservice/dto/ProcessedItemResponse.java`.
    - [x] DTO này sẽ là phản hồi từ `Unified API Service` về `Processing Service`.