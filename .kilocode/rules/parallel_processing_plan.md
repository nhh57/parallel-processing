# Kế hoạch Thiết kế Hệ thống Xử lý Song song Card & Pin

## 1. Diễn giải lại & Xác nhận

Dựa trên các trao đổi trước, tôi hiểu nhiệm vụ của chúng ta là:

*   **Đầu vào:** Một danh sách tổng hợp chứa cả đối tượng `Card` và `Pin`.
*   **Xử lý:**
    *   Tách danh sách đầu vào thành hai danh sách riêng biệt: một cho `Card` và một cho `Pin`.
    *   Thực hiện xử lý song song trên hai danh sách này.
    *   Mỗi `Card` hoặc `Pin` sẽ được gửi đến **một API endpoint duy nhất** của `Unified API Service`. Payload gửi đi sẽ chứa một trường `type` để định danh đối tượng (`CARD` hoặc `PIN`).
    *   Dựa trên phản hồi từ `Unified API Service`, `Processing Service` sẽ cập nhật trạng thái hoặc thông tin vào cơ sở dữ liệu của chính nó.
*   **Đầu ra:** Hai service chính được thiết kế:
    1.  **`Processing Service`**: Đây là dịch vụ chính của chúng ta, chịu trách nhiệm nhận dữ liệu, tách Card/Pin, gọi API đến `Unified API Service` và cập nhật cơ sở dữ liệu của nó.
    2.  **`Unified API Service`**: Đây là dịch vụ do chúng ta xây dựng, sẽ cung cấp một API duy nhất để `Processing Service` gọi đến nhằm kiểm tra trạng thái của Card/Pin và phản hồi lại.

## 2. Phân rã Yêu cầu & Xác định Thành phần

*   **Thực thể (Entities):**
    *   `Card`: Đại diện cho thông tin thẻ.
    *   `Pin`: Đại diện cho thông tin PIN.
    *   `ProcessedItemResponse`: Đối tượng chứa phản hồi từ API, bao gồm trạng thái xử lý và các thông tin cần thiết để cập nhật DB.
*   **Hành động (Actions):**
    *   Tách danh sách (`splitAndCategorize`).
    *   Gọi API (`callUnifiedApi`).
    *   Xử lý song song (`processInParallel`).
    *   Cập nhật cơ sở dữ liệu (`updateDatabase`).
*   **Kết quả mong đợi (Outcomes):**
    *   Các đối tượng `Card` và `Pin` được xử lý thông qua API chung.
    *   Thông tin tương ứng trong cơ sở dữ liệu được cập nhật chính xác.
    *   Quá trình xử lý diễn ra hiệu quả nhờ cơ chế song song.

## 3. Xây dựng Tiêu chí Chấp nhận (Acceptance Criteria - AC)

Để chức năng này được coi là hoàn thành, các điều kiện sau cần đúng. Vui lòng xác nhận:

*   [ ] Hệ thống có khả năng nhận một danh sách hỗn hợp `Card` và `Pin` làm đầu vào cho `CardPinProcessingService` trong `Processing Service`.
*   [ ] `CardPinProcessingService` tách thành công danh sách hỗn hợp thành hai danh sách riêng biệt (`List<Card>` và `List<Pin>`).
*   [ ] `CardPinProcessingService` khởi tạo và quản lý các tác vụ song song để xử lý `Card` và `Pin`.
*   [ ] Mỗi `Card` và `Pin` được gửi đến cùng một endpoint API của `Unified API Service` với payload chứa trường `type` (`CARD` hoặc `PIN`).
*   [ ] `Unified API Service` có khả năng phân biệt và thực hiện kiểm tra trạng thái đúng loại đối tượng (`Card` hoặc `Pin`) dựa vào trường `type` trong payload, và phản hồi lại kết quả (ví dụ: `cardType = CR`).
*   [ ] Sau khi `Processing Service` nhận phản hồi từ `Unified API Service`, nó thực hiện cập nhật **cơ sở dữ liệu của chính nó** tương ứng cho từng `Card` và `Pin`.
*   [ ] Cơ chế xử lý lỗi được thiết kế để xử lý các trường hợp API thất bại hoặc cập nhật DB thất bại.
*   [ ] Các lớp `Card`, `Pin`, `UnifiedApiRequestDTO`, `ProcessedItemResponse` được định nghĩa rõ ràng.

## 4. Nêu rõ các Giả định (Surface Assumptions)

Tôi đang đưa ra các giả định sau. Xin hãy điều chỉnh nếu cần:

*   **Ngôn ngữ/Framework:** Chúng ta sẽ sử dụng Java (có thể là Spring Boot để xây dựng REST API và quản lý dependency).
*   **Cơ chế song song:** Sẽ sử dụng `java.util.concurrent.CompletableFuture` hoặc `ExecutorService` để quản lý các tác vụ song song, cho phép thực thi không chặn (non-blocking).
*   **Cơ sở dữ liệu:** Giả định chúng ta sẽ sử dụng cơ sở dữ liệu H2 (in-memory hoặc file-based) để linh động và đơn giản hóa môi trường phát triển. Chúng ta sẽ sử dụng Spring Data JPA để tương tác. `Processing Service` sẽ có cơ sở dữ liệu của riêng nó. `Unified API Service` không nhất thiết phải có DB riêng cho chức năng này mà chỉ cần trả về trạng thái.
*   **API Endpoint:** `Unified API Service` sẽ cung cấp một RESTful API (ví dụ: `POST /check-status`) nhận `POST` request từ `Processing Service`.
*   **API Response:** Phản hồi từ `Unified API Service` sẽ chứa thông tin trạng thái cần thiết (ví dụ: `{"cardType": "CR", "id": "..."}`).

## 5. Đề xuất Kế hoạch Thiết kế

### 5.1. Cấu trúc Tổng quan

```mermaid
graph TD
    subgraph Client/External System
        A[Initial Request] --> B[Processing Service];
    end

    subgraph Processing Service (Application 1)
        B -- List<Object> [Card/Pin] --> C{CardPinProcessingService};
        C -- Split --> D1[List of Cards];
        C -- Split --> D2[List of Pins];
        D1 -- Parallel Call for each Card --> E[ApiClient to Unified API Service];
        D2 -- Parallel Call for each Pin --> E;
        E -- HTTP Request [UnifiedApiRequestDTO] --> F[Unified API Service];
        F -- HTTP Response [ProcessedItemResponse] --> E;
        E -- Process Response & Update --> G[Processing Service Database];
    end

    subgraph Unified API Service (Application 2)
        F -- Receive Request --> H[UnifiedProcessingController];
        H -- Delegate --> I[ItemStatusCheckerService];
        I -- Check Status Logic --> J[Internal Logic];
        J -- Return Status --> I;
        I -- Return Response --> H;
    end
```

### 5.2. Mô tả các Thành phần

1.  **Service 1: Processing Service**
    *   **Data Models:**
        *   `Card.java`: Lớp đại diện cho đối tượng thẻ.
        *   `Pin.java`: Lớp đại diện cho đối tượng PIN.
    *   **DTOs (để giao tiếp với Service 2):**
        *   `UnifiedApiRequestDTO.java`:
            *   Đây là DTO mà `Processing Service` sẽ gửi đến `Unified API Service`.
            *   Nó sẽ có một trường `type` (Enum: `CARD`, `PIN`) và các trường dữ liệu chung hoặc có thể là một `Map<String, Object>` để chứa dữ liệu cụ thể của `Card` hoặc `Pin`.
            *   *Ví dụ:*
                ```java
                public class UnifiedApiRequestDTO {
                    private ItemType type; // Enum: CARD, PIN
                    private Map<String, Object> data; // Chứa dữ liệu của Card hoặc Pin
                    // Getters, Setters
                }
                ```
        *   `ProcessedItemResponse.java`:
            *   DTO đại diện cho phản hồi từ `Unified API Service`.
            *   Nó sẽ chứa ID của đối tượng đã xử lý, trạng thái ( thành công/thất bại), và bất kỳ thông tin cần thiết nào khác để cập nhật DB của chúng ta.
    *   **`ExternalApiClient`:**
        *   `ExternalApiClient.java`:
            *   Đây là một component (ví dụ: `@Component` hoặc `@Service` trong Spring) chịu trách nhiệm gọi đến `Unified API Service`.
            *   Sẽ sử dụng `WebClient` (hoặc `RestTemplate`) để gửi `UnifiedApiRequestDTO` và nhận `ProcessedItemResponse`.
            *   Chứa phương thức `callUnifiedApiService(UnifiedApiRequestDTO requestDTO)`.
    *   **`CardPinProcessingService`:**
        *   `CardPinProcessingService.java`:
            *   Đây là service chính điều phối toàn bộ quá trình.
            *   Có một phương thức chính (ví dụ: `processMixedList(List<Object> mixedItems)`).
            *   **Bước 1: Tách danh sách:**
                *   Duyệt qua `mixedItems` và phân loại chúng vào `List<Card>` và `List<Pin>`.
            *   **Bước 2: Xử lý song song:**
                *   Sử dụng `ExecutorService` (ví dụ: `ThreadPoolTaskExecutor` trong Spring) và `CompletableFuture` để tạo các tác vụ song song cho từng `Card` và `Pin`.
                *   Mỗi tác vụ sẽ:
                    *   Chuyển đổi `Card`/`Pin` thành `UnifiedApiRequestDTO`.
                    *   Gọi `ExternalApiClient.callUnifiedApiService()` để gửi `UnifiedApiRequestDTO` đến `Unified API Service`.
                    *   Xử lý phản hồi từ `Unified API Service`.
                    *   Cập nhật **cơ sở dữ liệu của chúng ta**.
            *   **Bước 3: Tổng hợp kết quả và xử lý lỗi:**
                *   `CompletableFuture.allOf()` có thể được sử dụng để chờ tất cả các tác vụ song song hoàn thành.
                *   Thu thập kết quả hoặc thông tin lỗi từ mỗi tác vụ.
    *   **Database Interaction:**
        *   `CardRepository.java`: Interface repository cho `Card` (ví dụ: `JpaRepository`).
        *   `PinRepository.java`: Interface repository cho `Pin` (ví dụ: `JpaRepository`).
        *   Các phương thức cập nhật sẽ được gọi từ `CardPinProcessingService` sau khi nhận phản hồi API.

2.  **Service 2: Unified API Service**
    *   **Data Models (nếu cần cho lưu trữ nội bộ của Service 2):**
        *   `ProcessedItem.java` (ví dụ): Nếu Service 2 cần lưu trữ trạng thái của các item đã xử lý.
    *   **DTOs (nhận từ Service 1):**
        *   Sử dụng lại `UnifiedApiRequestDTO.java` và `ProcessedItemResponse.java` như đã định nghĩa ở Service 1.
    *   **`UnifiedProcessingController`:**
        *   `UnifiedProcessingController.java`:
            *   Là một REST Controller (ví dụ: `@RestController` trong Spring).
            *   Có một endpoint duy nhất (ví dụ: `POST /check-status`).
            *   Endpoint này sẽ nhận `UnifiedApiRequestDTO`.
            *   Bên trong controller, nó sẽ ủy quyền xử lý cho một service khác (ví dụ: `ItemStatusCheckerService`) dựa trên trường `type` trong DTO.
    *   **`ItemStatusCheckerService`:**
        *   `ItemStatusCheckerService.java`:
            *   Chứa logic để kiểm tra trạng thái của `Card` hoặc `Pin` dựa trên `type` trong request.
            *   Có thể có các phương thức riêng biệt như `checkCardStatus(CardData)` và `checkPinStatus(PinData)`, hoặc một phương thức chung với logic điều kiện.
            *   Sau khi kiểm tra, trả về `ProcessedItemResponse` chứa trạng thái (ví dụ: `cardType = CR`).
        *   *Lưu ý*: Service này không cần tương tác với DB bên ngoài cho luồng này, nhưng có thể có DB nội bộ để lưu trữ cấu hình hoặc log.

### 5.3. Cơ chế Song song (`CompletableFuture`)

*   Đối với mỗi `Card` trong `List<Card>` và `Pin` trong `List<Pin>`, chúng ta sẽ tạo một `CompletableFuture`.
*   Mỗi `CompletableFuture` sẽ gói gọn quá trình:
    1.  Tạo `UnifiedApiRequestDTO`.
    2.  Gọi API.
    3.  Xử lý phản hồi và cập nhật DB.
*   Sử dụng `CompletableFuture.supplyAsync()` để chạy các tác vụ trong một `ExecutorService` tùy chỉnh (để kiểm soát số lượng luồng).
*   Sử dụng `CompletableFuture.whenComplete()` hoặc `exceptionally()` để xử lý lỗi cho từng tác vụ con.
*   Sử dụng `CompletableFuture.allOf()` để chờ tất cả các `CompletableFuture` hoàn thành trước khi `processMixedList` trả về.

### 5.4. Xử lý lỗi

*   **API Call Failure (từ Processing Service đến Unified API Service):**
    *   Trong `CompletableFuture`, nếu cuộc gọi đến `Unified API Service` thất bại, `Processing Service` có thể log lỗi và đánh dấu đối tượng đó là "không xử lý được" trong DB của nó hoặc ghi vào một danh sách lỗi.
    *   `Processing Service` có thể tích hợp cơ chế retry nếu cần.
*   **Database Update Failure (trong Processing Service):**
    *   Nếu cập nhật DB của `Processing Service` thất bại, log lỗi và có thể đưa vào hàng đợi để xử lý lại sau (ví dụ: Kafka, RabbitMQ) hoặc ghi vào bảng lỗi.
*   **Transaction Management:**
    *   Mỗi thao tác cập nhật DB cho từng `Card` hoặc `Pin` trong `Processing Service` sẽ là một giao dịch riêng biệt. Nếu cần đảm bảo tính toàn vẹn cao hơn cho toàn bộ batch, cần xem xét cơ chế bù trừ (compensation) hoặc kiến trúc dựa trên sự kiện (event-driven).

### 5.5. Các Vấn đề Phi Chức Năng (Non-Functional Considerations)

Để đảm bảo hệ thống hoạt động ổn định và hiệu quả, cần chú trọng các khía cạnh sau:

1.  **Deadlock và Thread Lock:**
    *   **Nguyên nhân:** Xảy ra khi nhiều luồng cùng cố gắng giành quyền truy cập vào các tài nguyên chung (ví dụ: database connection, shared memory) theo trình tự không hợp lý.
    *   **Phòng tránh:**
        *   **Tránh sử dụng `synchronized` blocks/methods không cần thiết:** Đặc biệt là trong các đoạn code xử lý I/O hoặc gọi API.
        *   **Sử dụng `CompletableFuture`:** Cơ chế này giúp xử lý bất đồng bộ mà không cần quản lý khóa thủ công, giảm thiểu nguy cơ deadlock.
        *   **Thứ tự khóa (Lock Ordering):** Nếu phải sử dụng khóa, luôn đảm bảo các luồng giành quyền truy cập vào các khóa theo cùng một thứ tự.
        *   **Timeout cho khóa:** Sử dụng `tryLock(timeout)` thay vì `lock()` để tránh luồng bị kẹt vô thời hạn.
        *   **Giới hạn tài nguyên:** Sử dụng Thread Pool (ví dụ: `ExecutorService` được cấu hình đúng) để kiểm soát số lượng luồng, tránh tạo quá nhiều luồng gây tranh chấp tài nguyên.

2.  **Memory Leak:**
    *   **Nguyên nhân:** Xảy ra khi các đối tượng không còn được sử dụng nhưng vẫn được giữ trong bộ nhớ, ngăn Garbage Collector giải phóng chúng.
    *   **Phòng tránh:**
        *   **Quản lý tài nguyên `ExecutorService`:** Đảm bảo `ExecutorService` được tắt đúng cách khi không còn cần thiết (`shutdown()` và `awaitTermination()`). Nếu không, các luồng có thể tiếp tục chạy và giữ các đối tượng tham chiếu.
        *   **Sử dụng `try-with-resources`:** Đảm bảo các tài nguyên như `InputStream`, `OutputStream`, `Connection` được đóng đúng cách.
        *   **Tránh giữ tham chiếu không cần thiết:** Cẩn thận với các `static` fields, `cache` hoặc `collection` có thể giữ tham chiếu đến các đối tượng không còn dùng.
        *   **Monitoring:** Sử dụng các công cụ JVM monitoring (JConsole, VisualVM) để theo dõi việc sử dụng bộ nhớ và phát hiện sớm các dấu hiệu memory leak.

3.  **Thread Starvation (Bị bỏ đói):**
    *   **Nguyên nhân:** Xảy ra khi một hoặc nhiều luồng không bao giờ nhận được CPU time để thực thi, thường là do các luồng khác chiếm dụng tài nguyên quá lâu hoặc do ưu tiên luồng không đúng.
    *   **Phòng tránh:**
        *   **Cấu hình Thread Pool hợp lý:** Đảm bảo kích thước của thread pool đủ lớn để xử lý workload, nhưng không quá lớn để gây overhead.
        *   **Tránh các tác vụ chặn (Blocking Tasks) trong thread pool:** Các tác vụ gọi API hoặc tương tác DB nên được xử lý bất đồng bộ (ví dụ: với `CompletableFuture` và `ForkJoinPool.commonPool()` hoặc `ExecutorService` riêng cho I/O-bound tasks) để không chặn các worker thread.
        *   **Ưu tiên luồng:** Tránh thay đổi ưu tiên luồng trừ khi thực sự cần thiết, vì nó có thể dẫn đến các vấn đề không mong muốn.
        *   **Fair Lock:** Sử dụng các khóa công bằng (fair locks) nếu cần đảm bảo các luồng được cấp quyền truy cập theo thứ tự yêu cầu.

4.  **Kiểm tra và Giám sát:**
    *   **Logging:** Triển khai logging chi tiết ở các điểm quan trọng trong luồng xử lý song song và tương tác API để dễ dàng debug và theo dõi. **Đặc biệt, sử dụng `requestId` (hoặc `correlationId`) để theo dõi xuyên suốt một yêu cầu từ đầu đến cuối qua các service và các luồng.**
        *   **Cách thức:**
            *   Tạo một `requestId` duy nhất khi yêu cầu ban đầu được nhận bởi `Processing Service`.
            *   Truyền `requestId` này trong payload của mọi cuộc gọi API từ `Processing Service` đến `Unified API Service`.
            *   `Unified API Service` nhận `requestId` và sử dụng nó trong tất cả các log liên quan đến yêu cầu đó.
            *   Sử dụng MDC (Mapped Diagnostic Context) trong Logback/Log4j2 để tự động thêm `requestId` vào mỗi dòng log của luồng hiện tại.
    *   **Metrics:** Thu thập các metrics về hiệu suất (thời gian phản hồi API, thời gian xử lý tác vụ, số lượng tác vụ trong queue) và tài nguyên (CPU, memory, số lượng luồng) để phát hiện sớm các vấn đề.
    *   **Health Checks:** Đảm bảo cả `Processing Service` và `Unified API Service` đều có các endpoint health check để kiểm tra trạng thái hoạt động.

5.  **Thiết kế API Resilience:**
    *   **Retry Mechanism:** Triển khai cơ chế retry cho các cuộc gọi API từ `Processing Service` đến `Unified API Service` với backoff strategy (ví dụ: exponential backoff) để xử lý các lỗi tạm thời.
    *   **Circuit Breaker:** Sử dụng Circuit Breaker pattern (ví dụ: Resilience4j, Hystrix) để ngăn chặn các cuộc gọi liên tục đến một service đang gặp sự cố, giúp service đó có thời gian phục hồi và tránh làm sập service gọi.
    *   **Timeout:** Đặt timeout hợp lý cho các cuộc gọi API để tránh các luồng bị chặn vô thời hạn nếu service khác phản hồi chậm hoặc không phản hồi.

### 5.7. Cấu trúc thư mục dự kiến (ví dụ với Spring Boot Multi-Module Project)

Nếu là một dự án multi-module:

```
.
├── pom.xml (root)
├── processing-service
│   ├── pom.xml
│   └── src/main/java/com/example/processingservice
│       ├── dto
│       │   ├── ProcessedItemResponse.java
│       │   └── UnifiedApiRequestDTO.java
│       ├── model
│       │   ├── Card.java
│       │   ├── Pin.java
│       │   └── ItemType.java
│       ├── repository
│       │   ├── CardRepository.java
│       │   └── PinRepository.java
│       ├── service
│       │   ├── CardPinProcessingService.java
│       │   └── ExternalApiClient.java
│       └── ProcessingServiceApplication.java
└── unified-api-service
    ├── pom.xml
    └── src/main/java/com/example/unifiedapiservice
        ├── controller
        │   └── UnifiedProcessingController.java
        ├── dto (có thể tái sử dụng từ processing-service nếu dùng shared module)
        │   ├── ProcessedItemResponse.java
        │   └── UnifiedApiRequestDTO.java
        ├── model (nếu có model riêng cho service này)
        │   └── ProcessedItem.java
        ├── service
        │   └── ItemStatusCheckerService.java
        └── UnifiedApiServiceApplication.java
```

Hoặc, nếu là hai dự án độc lập:

```
/path/to/your/projects
├── processing-service
│   ├── pom.xml
│   └── src/... (như trên)
└── unified-api-service
    ├── pom.xml
    └── src/... (như trên)