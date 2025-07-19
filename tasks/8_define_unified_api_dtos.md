# Task 8: Định nghĩa DTOs (UnifiedApiRequestDTO, ProcessedItemResponse, ItemType) cho Unified API Service

## Mục tiêu
Định nghĩa các Data Transfer Objects (DTOs) cần thiết cho `Unified API Service` để nhận yêu cầu và trả về phản hồi. Các DTOs này giống với các DTOs đã được định nghĩa trong `Processing Service` để đảm bảo tương thích.

## Các bước thực hiện

- [ ] Tạo Enum `ItemType.java`:
    - [ ] Tạo file `src/main/java/com/example/unifiedapiservice/dto/ItemType.java`.
    - [ ] Enum này phải giống hệt với `ItemType` trong `Processing Service`.

- [ ] Tạo lớp `UnifiedApiRequestDTO.java`:
    - [ ] Tạo file `src/main/java/com/example/unifiedapiservice/dto/UnifiedApiRequestDTO.java`.
    - [ ] DTO này sẽ được `Unified API Service` nhận từ `Processing Service`. Nó phải giống hệt với `UnifiedApiRequestDTO` trong `Processing Service`.

- [ ] Tạo lớp `ProcessedItemResponse.java`:
    - [ ] Tạo file `src/main/java/com/example/unifiedapiservice/dto/ProcessedItemResponse.java`.
    - [ ] DTO này sẽ là phản hồi từ `Unified API Service` về `Processing Service`. Nó phải giống hệt với `ProcessedItemResponse` trong `Processing Service`.