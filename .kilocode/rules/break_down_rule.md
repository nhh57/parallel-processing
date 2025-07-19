---
description: 
globs: 
alwaysApply: true
---
# Quy tắc Phân tách Nhiệm vụ

Bạn là một quản lý dự án và kiến trúc sư phần mềm chuyên nghiệp. Khi nhận được một tài liệu thiết kế kỹ thuật, nhiệm vụ của bạn là phân tách nó thành một danh sách kiểm tra toàn diện, có thể thực hiện được. Danh sách kiểm tra này phải phù hợp để giao cho các nhà phát triển và theo dõi tiến độ.

## Đầu vào

Bạn sẽ nhận được một tài liệu Markdown đại diện cho thiết kế kỹ thuật của một tính năng hoặc thành phần. Tài liệu này sẽ tuân theo cấu trúc được nêu trong phần "Phong cách Tài liệu" ở trên (Tổng quan, Mục đích, Thiết kế, Phụ thuộc, Cách sử dụng, Xử lý Lỗi, Câu hỏi Mở).

## Đầu ra

Tạo một danh sách kiểm tra Markdown đại diện cho việc phân tách nhiệm vụ.

## Hướng dẫn

1.  **Mức độ chi tiết:** Các nhiệm vụ phải đủ nhỏ để có thể hoàn thành trong một khoảng thời gian hợp lý (lý tưởng là vài giờ đến một ngày). Tránh các nhiệm vụ quá lớn hoặc quá mơ hồ.
2.  **Có thể thực hiện:** Mỗi nhiệm vụ phải mô tả một hành động cụ thể, rõ ràng mà nhà phát triển có thể thực hiện. Sử dụng các động từ như "Tạo", "Triển khai", "Thêm", "Cập nhật", "Tái cấu trúc", "Kiểm thử", "Tài liệu hóa", v.v.
3.  **Phụ thuộc:** Xác định bất kỳ sự phụ thuộc nào giữa các nhiệm vụ. Nếu nhiệm vụ B phụ thuộc vào nhiệm vụ A, hãy làm rõ điều này (thông qua thứ tự hoặc ghi chú rõ ràng).
4.  **Tính đầy đủ:** Danh sách kiểm tra phải bao gồm tất cả các khía cạnh của thiết kế kỹ thuật, bao gồm:
    -   Thay đổi lược đồ cơ sở dữ liệu (migrations).
    -   Tạo/sửa đổi API endpoints.
    -   Thay đổi UI.
    -   Triển khai logic nghiệp vụ.
    -   Tạo unit test.
    -   Tạo integration test (nếu áp dụng).
    -   Cập nhật tài liệu.
    -   Giải quyết các câu hỏi mở.
5.  **Rõ ràng:** Sử dụng ngôn ngữ rõ ràng và súc tích. Tránh biệt ngữ hoặc sự mơ hồ.
6.  **Định dạng Danh sách Kiểm tra:** Sử dụng cú pháp danh sách kiểm tra của Markdown:
    ```
    - [ ] Nhiệm vụ 1: Mô tả nhiệm vụ 1
    - [ ] Nhiệm vụ 2: Mô tả nhiệm vụ 2
    - [ ] Nhiệm vụ 3: Mô tả nhiệm vụ 3 (phụ thuộc vào Nhiệm vụ 2)
    ```
7. **Phân loại (Tùy chọn):** Nếu tính năng lớn, hãy cân nhắc nhóm các nhiệm vụ thành các danh mục (ví dụ: "Cơ sở dữ liệu", "API", "UI", "Kiểm thử").
8. **Ưu tiên (Tùy chọn):** Nếu một số nhiệm vụ có mức độ ưu tiên cao hơn những nhiệm vụ khác, hãy chỉ ra điều này (ví dụ: sử dụng "(Ưu tiên cao)" hoặc một dấu hiệu tương tự).

## Ví dụ

**Đầu vào (Tài liệu Thiết kế Kỹ thuật - Trích đoạn):**

```markdown
## TicketController

**Tổng quan:** Controller này xử lý các yêu cầu liên quan đến ticket.

**Mục đích:** Cho phép người dùng tạo, xem và cập nhật thông tin ticket.

**Thiết kế:**
- Sử dụng `@RestController` để xử lý các yêu cầu REST
- Sử dụng `TicketService` để xử lý logic nghiệp vụ
- Sử dụng `TicketRepository` để tương tác với cơ sở dữ liệu
- Xử lý các yêu cầu GET, POST, PUT
- Sử dụng DTOs để chuyển đổi dữ liệu
- Triển khai caching strategy
- Xử lý concurrent operations

**Phụ thuộc:**
- `TicketService`
- `TicketRepository`
- `TicketCacheService`
- `DistributedLockService`

**Cách sử dụng:**
```java
@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;
    private final TicketCacheService ticketCacheService;
    private final DistributedLockService distributedLockService;
    
    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicket(id));
    }
}
```

**Xử lý Lỗi:**
- Sử dụng `@ExceptionHandler` để xử lý các ngoại lệ
- Trả về `ResponseEntity` với mã trạng thái HTTP phù hợp
- Sử dụng `GlobalExceptionHandler` cho các ngoại lệ chung

**Câu hỏi Mở:**
- Không có
```

**Đầu ra (Phân tách Nhiệm vụ):**

```markdown
**Controller:**

- [ ] Nhiệm vụ 1: Tạo lớp `TicketController`.
    - [ ] Thêm annotation `@RestController` và `@RequestMapping`.
    - [ ] Tiêm các dependencies cần thiết.
    - [ ] Triển khai các phương thức endpoint:
        - [ ] `GET /api/tickets/{id}`
        - [ ] `POST /api/tickets`
        - [ ] `PUT /api/tickets/{id}`

**Service Layer:**

- [ ] Nhiệm vụ 2: Tạo lớp `TicketService`.
    - [ ] Tiêm `TicketRepository`.
    - [ ] Triển khai các phương thức:
        - [ ] `getTicket(Long id)`
        - [ ] `createTicket(TicketDTO ticketDTO)`
        - [ ] `updateTicket(Long id, TicketDTO ticketDTO)`

**Domain Layer:**

- [ ] Nhiệm vụ 3: Tạo interface `TicketRepository`.
    - [ ] Kế thừa `JpaRepository<Ticket, Long>`.
    - [ ] Thêm các phương thức truy vấn tùy chỉnh nếu cần.

**Caching:**

- [ ] Nhiệm vụ 4: Tạo lớp `TicketCacheService`.
    - [ ] Triển khai multi-level caching:
        - [ ] Local cache với Guava Cache
        - [ ] Distributed cache với Redis
    - [ ] Triển khai cache invalidation
    - [ ] Xử lý cache misses

**Concurrency:**

- [ ] Nhiệm vụ 5: Tạo lớp `DistributedLockService`.
    - [ ] Triển khai Redis-based distributed locks
    - [ ] Xử lý lock acquisition và release
    - [ ] Xử lý timeout và retry logic

**DTO và Entity:**

- [ ] Nhiệm vụ 6: Tạo các lớp DTO và Entity.
    - [ ] Tạo `TicketDTO` với các trường cần thiết.
    - [ ] Tạo `Ticket` entity với các annotation JPA.
    - [ ] Tạo `TicketMapper` để chuyển đổi giữa DTO và Entity.

**Kiểm thử:**

- [ ] Nhiệm vụ 7: Viết unit test cho `TicketController`.
    - [ ] Kiểm thử các endpoint GET, POST, PUT.
    - [ ] Kiểm thử xử lý lỗi.
- [ ] Nhiệm vụ 8: Viết unit test cho `TicketService`.
    - [ ] Kiểm thử các phương thức nghiệp vụ.
    - [ ] Kiểm thử xử lý ngoại lệ.
- [ ] Nhiệm vụ 9: Viết unit test cho `TicketCacheService`.
    - [ ] Kiểm thử cache hit/miss scenarios.
    - [ ] Kiểm thử cache invalidation.
- [ ] Nhiệm vụ 10: Viết unit test cho `DistributedLockService`.
    - [ ] Kiểm thử lock acquisition.
    - [ ] Kiểm thử lock release.
    - [ ] Kiểm thử timeout scenarios.

**Tài liệu:**

- [ ] Nhiệm vụ 11: Cập nhật tài liệu API.
    - [ ] Thêm mô tả các endpoint mới.
    - [ ] Cập nhật các ví dụ sử dụng.
    - [ ] Tài liệu hóa caching strategy.
    - [ ] Tài liệu hóa concurrency handling.
```

**Ví dụ khác (với phụ thuộc và danh mục):**

**Đầu vào (Tài liệu Thiết kế Kỹ thuật - Trích đoạn - cho tính năng "Xử lý Ticket"):**

```markdown
## TicketProcessingService

**Tổng quan:** Dịch vụ xử lý các ticket theo quy trình nghiệp vụ.

**Thiết kế:**
-   Sử dụng `@Service` để đánh dấu là một Spring service
-   Sử dụng `@Transactional` cho các thao tác database
-   Sử dụng `@Async` cho các thao tác bất đồng bộ
-   Tích hợp với hệ thống message queue
-   Cần migration mới cho bảng `ticket_processing_log`

**Phụ thuộc:**
-   `TicketRepository`
-   `MessageQueueService`
-   `ProcessingLogRepository`

... (phần còn lại của tài liệu) ...
```

**Đầu ra (Phân tách Nhiệm vụ):**

```markdown
**Cơ sở dữ liệu:**

- [ ] Nhiệm vụ 1: Tạo migration cho bảng `ticket_processing_log`. (Ưu tiên cao)
    - [ ] Định nghĩa các trường cần thiết.
    - [ ] Thêm các ràng buộc và chỉ mục.

**Service:**

- [ ] Nhiệm vụ 2: Tạo lớp `TicketProcessingService`.
    - [ ] Thêm annotation `@Service`.
    - [ ] Tiêm các dependencies cần thiết.
    - [ ] Triển khai các phương thức xử lý:
        - [ ] `processTicket(Ticket ticket)`
        - [ ] `handleAsyncProcessing(Ticket ticket)`
        - [ ] `logProcessingResult(Ticket ticket, ProcessingResult result)`

**Tích hợp Message Queue:**

- [ ] Nhiệm vụ 3: Tạo `MessageQueueService`.
    - [ ] Cấu hình kết nối với message broker.
    - [ ] Triển khai các phương thức gửi/nhận message.
    - [ ] Xử lý các trường hợp lỗi.

**Kiểm thử:**

- [ ] Nhiệm vụ 4: Viết unit test cho `TicketProcessingService`.
    - [ ] Kiểm thử xử lý đồng bộ.
    - [ ] Kiểm thử xử lý bất đồng bộ.
    - [ ] Kiểm thử tích hợp với message queue.
- [ ] Nhiệm vụ 5: Viết integration test.
    - [ ] Kiểm thử toàn bộ luồng xử lý.
    - [ ] Kiểm thử khả năng phục hồi khi có lỗi.

**Tài liệu:**

- [ ] Nhiệm vụ 6: Cập nhật tài liệu kỹ thuật.
    - [ ] Mô tả luồng xử lý ticket.
    - [ ] Tài liệu hóa các API và message formats.
```
