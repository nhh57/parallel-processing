# Giải thích Nguyên lý Hoạt động các Thành phần Cốt lõi của Project

Tài liệu này tổng hợp các giải thích về các thành phần và nguyên lý hoạt động chính trong project xử lý song song Card & Pin, nhằm giúp bạn nắm vững kiến thức cốt lõi.

---

## 1. Reactive Programming, Mono, Flux và CompletableFuture (Giải thích sâu sắc)

Để thực sự hiểu về `Mono`, `Flux` và `CompletableFuture`, chúng ta cần đặt chúng vào bối cảnh của các mô hình lập trình xử lý các tác vụ bất đồng bộ (Asynchronous) và không chặn (Non-blocking).

### 1.1. Lập trình Bất đồng bộ (Asynchronous Programming) và Không chặn (Non-blocking)

*   **Lập trình Bất đồng bộ (Asynchronous Programming):**
    Là một mô hình cho phép một chương trình **khởi tạo một tác vụ và tiếp tục thực hiện các tác vụ khác mà không cần đợi tác vụ ban đầu hoàn thành**. Khi tác vụ ban đầu hoàn tất (dù thành công hay thất bại), chương trình sẽ nhận được một thông báo (callback, future, promise, event). Điều này đặc biệt hữu ích cho các tác vụ tốn thời gian như I/O (gọi API, truy cập cơ sở dữ liệu, đọc/ghi file) để tránh việc ứng dụng bị "treo" hay "đứng hình".

*   **Lập trình Không chặn (Non-blocking Programming):**
    Đây là một khái niệm liên quan chặt chẽ đến bất đồng bộ. Một hoạt động không chặn có nghĩa là **nó không làm dừng việc thực thi của luồng gọi (calling thread) khi chờ đợi một kết quả**. Thay vào đó, nó sẽ trả về ngay lập tức (có thể là một `Future`, `Promise`, `Mono`, `Flux` hoặc `null`) và thông báo cho luồng gọi khi kết quả sẵn sàng. Mục tiêu chính là tối đa hóa việc sử dụng tài nguyên CPU bằng cách không để các luồng bị nhàn rỗi trong khi chờ đợi I/O.

### 1.2. Reactive Programming (Lập trình phản ứng)

Reactive Programming là một **mô hình lập trình bất đồng bộ** tập trung vào **luồng dữ liệu (data streams)** và **sự truyền bá thay đổi (propagation of change)**. Nó cung cấp một cách tiếp cận mạnh mẽ để xử lý các sự kiện và dữ liệu theo thời gian một cách hiệu quả, đặc biệt trong các hệ thống phân tán, có tính tương tác cao, hoặc nơi có lượng dữ liệu lớn cần được xử lý liên tục (streams).

*   **Các nguyên tắc cốt lõi của Reactive Programming (dựa trên Reactive Streams Specification):**
    1.  **Asynchronous & Non-blocking:** Như đã giải thích ở trên, các hoạt động không chặn và không đồng bộ là nền tảng.
    2.  **Event-driven:** Mọi thứ được coi là một chuỗi các sự kiện (dữ liệu, lỗi, hoàn thành).
    3.  **Push-based:** Thay vì client chủ động "kéo" dữ liệu khi cần (pull-based), server/publisher sẽ "đẩy" dữ liệu đến client/subscriber khi dữ liệu có sẵn.
    4.  **Backpressure:** Đây là một cơ chế quan trọng cho phép Subscriber thông báo cho Publisher về khả năng xử lý của mình. Nếu Subscriber không thể xử lý dữ liệu nhanh bằng Publisher tạo ra, nó có thể yêu cầu Publisher làm chậm lại hoặc gửi ít dữ liệu hơn, tránh tình trạng quá tải bộ nhớ (out-of-memory) hoặc tắc nghẽn đường ống.

*   **Các khái niệm chính trong Reactive Streams:**
    *   **Publisher:** Một nhà cung cấp các phần tử dữ liệu tuần tự.
    *   **Subscriber:** Một người tiêu thụ các phần tử dữ liệu do Publisher cung cấp.
    *   **Subscription:** Đại diện cho mối quan hệ giữa Publisher và Subscriber, cho phép Subscriber kiểm soát dòng chảy dữ liệu (backpressure).
    *   **Processor:** Một thành phần vừa là Subscriber vừa là Publisher, cho phép chuyển đổi và chuyển tiếp luồng dữ liệu.

### 1.3. Mono và Flux (trong Project Reactor)

Project Reactor là một framework triển khai Reactive Programming (tuân thủ Reactive Streams Specification) trong hệ sinh thái Java, đặc biệt phổ biến trong Spring WebFlux. `Mono` và `Flux` là hai kiểu dữ liệu chính của Reactor để đại diện cho các luồng dữ liệu.

*   **Mono:**
    *   **Đại diện:** Một luồng dữ liệu bất đồng bộ có thể phát ra **0 hoặc 1 phần tử** duy nhất, sau đó là tín hiệu hoàn thành (`onComplete`) hoặc một tín hiệu lỗi (`onError`).
    *   **Khi sử dụng:** Lý tưởng cho các tác vụ trả về một kết quả đơn lẻ (ví dụ: tìm kiếm một bản ghi theo ID, xóa một đối tượng, cập nhật một trạng thái) hoặc các tác vụ không trả về kết quả nào (`void`).
    *   **Ví dụ:**
        *   `Mono<User>`: Tìm một người dùng duy nhất.
        *   `Mono<Void>`: Thực hiện một thao tác không trả về gì.
        *   `Mono.just("Hello")`: Tạo một Mono phát ra "Hello".
        *   `Mono.empty()`: Tạo một Mono hoàn thành mà không phát ra phần tử nào.

*   **Flux:**
    *   **Đại diện:** Một luồng dữ liệu bất đồng bộ có thể phát ra **0 đến N phần tử** (có thể là một số lượng không giới hạn các phần tử) theo thời gian, sau đó là tín hiệu hoàn thành (`onComplete`) hoặc một tín hiệu lỗi (`onError`).
    *   **Khi sử dụng:** Thích hợp cho các tác vụ trả về nhiều kết quả (ví dụ: tìm kiếm tất cả sản phẩm, lấy danh sách các sự kiện từ một nguồn dữ liệu liên tục, xử lý các bản ghi từ một file lớn).
    *   **Ví dụ:**
        *   `Flux<Product>`: Lấy danh sách tất cả sản phẩm.
        *   `Flux<ClickEvent>`: Stream các sự kiện click từ UI.
        *   `Flux.just(1, 2, 3)`: Tạo một Flux phát ra 1, 2, 3.
        *   `Flux.fromIterable(myList)`: Tạo Flux từ một Iterable.

*   **Đặc điểm chung của Mono/Flux:**
    *   **Composable (Có thể kết hợp):** Cung cấp rất nhiều toán tử (`map`, `filter`, `flatMap`, `zip`, `merge`, v.v.) cho phép bạn biến đổi, kết hợp và xử lý các luồng dữ liệu một cách linh hoạt và khai báo (declarative).
    *   **Lazy (Lười biếng):** Luồng dữ liệu chỉ bắt đầu thực thi khi có một `Subscriber` đăng ký vào nó. Nếu không có ai đăng ký, không có gì xảy ra.
    *   **Immutability (Bất biến):** Các toán tử không thay đổi `Mono`/`Flux` gốc mà trả về một `Mono`/`Flux` mới.

### 1.4. CompletableFuture (trong Java)

`CompletableFuture` (ra mắt từ Java 8) là một công cụ mạnh mẽ trong Java để thực hiện **lập trình bất đồng bộ** và xử lý các kết quả của các tác vụ không đồng bộ. Nó thuộc về một mô hình lập trình gọi là **Futures/Promises**.

*   **CompletableFuture là gì?**
    *   Nó là một `Future` có thể được "hoàn thành" (completed) một cách thủ công với một giá trị hoặc một ngoại lệ.
    *   Nó cho phép bạn tạo ra một **chuỗi các tác vụ (chaining operations)** mà không cần chặn luồng chính (non-blocking).
    *   Nó có thể được dùng để thực hiện các tác vụ song song, chờ đợi nhiều tác vụ hoàn thành, và kết hợp kết quả của chúng.

*   **Cách hoạt động của CompletableFuture:**
    1.  Bạn khởi tạo một `CompletableFuture` cho một tác vụ (ví dụ: `CompletableFuture.supplyAsync(() -> longRunningOperation())`).
    2.  Tác vụ đó chạy trên một luồng khác (thường là từ một `ExecutorService`).
    3.  Khi tác vụ hoàn thành, `CompletableFuture` được đánh dấu là "completed" và kết quả (hoặc lỗi) được lưu trữ.
    4.  Các tác vụ phụ thuộc (callback functions) đã được đăng ký với `CompletableFuture` (ví dụ: `thenApply`, `thenAccept`, `thenCompose`, `thenCombine`, `exceptionally`) sẽ được thực thi khi nó hoàn thành mà không cần phải gọi `.get()` và chặn luồng.
    *   `thenApply(Function)`: Xử lý kết quả của `CompletableFuture` và trả về một kết quả mới.
    *   `thenAccept(Consumer)`: Xử lý kết quả nhưng không trả về gì.
    *   `thenCompose(Function)`: Kết hợp hai `CompletableFuture` nối tiếp nhau (tương tự `flatMap` trong Reactive).
    *   `thenCombine(CompletionStage, BiFunction)`: Kết hợp kết quả của hai `CompletableFuture` độc lập.
    *   `allOf(CompletableFuture...)`: Chờ tất cả các `CompletableFuture` hoàn thành.
    *   `anyOf(CompletableFuture...)`: Hoàn thành khi một trong các `CompletableFuture` hoàn thành.

### 1.5. Mối liên hệ và sự khác biệt giữa CompletableFuture và Mono/Flux

Cả `CompletableFuture` và `Mono`/`Flux` đều là các công cụ tuyệt vời cho lập trình bất đồng bộ và không chặn trong Java, nhưng chúng thuộc về các mô hình khác nhau và có các trường hợp sử dụng tối ưu khác nhau:

| Đặc điểm            | CompletableFuture                                          | Mono/Flux (Project Reactor)                               |
| :------------------ | :--------------------------------------------------------- | :-------------------------------------------------------- |
| **Mô hình lập trình** | Futures/Promises (dành cho kết quả đơn lẻ)                | Reactive Streams (dành cho luồng dữ liệu)                 |
| **Số lượng phần tử** | Luôn là **0 hoặc 1** kết quả duy nhất.                     | **Mono:** 0 hoặc 1 phần tử.<br>**Flux:** 0 đến N phần tử. |
| **Backpressure**    | Không có cơ chế backpressure tích hợp.                     | **Có** cơ chế backpressure tích hợp (quan trọng cho stream). |
| **Composable**      | Có các phương thức để nối chuỗi và kết hợp (`thenApply`, `thenCompose`, `thenCombine`, `allOf`). | Cung cấp một bộ toán tử phong phú và linh hoạt hơn nhiều để biến đổi và kết hợp luồng. |
| **Trường hợp sử dụng** |
| **Tối ưu cho**      | Các tác vụ bất đồng bộ đơn lẻ, chuỗi tác vụ tuần tự, kết hợp một số ít tác vụ độc lập. | Ứng dụng hướng sự kiện, xử lý luồng dữ liệu liên tục, các API phản ứng (WebFlux), microservices giao tiếp phản ứng. |
| **Khi nào dùng**    | Khi bạn chỉ mong đợi một kết quả duy nhất từ một phép tính bất đồng bộ. | Khi bạn cần xử lý một chuỗi các sự kiện hoặc một tập hợp lớn/vô hạn các phần tử theo thời gian. |
| **Phạm vi**         | Chỉ là một phần của Java SDK.                              | Thư viện bên ngoài (Project Reactor), tuân thủ Reactive Streams Spec. |
| **Tương tác**       | Có thể chuyển đổi qua lại: `Mono.fromFuture(completableFuture)`, `completableFuture.toFuture()`. |

**Kết luận:**

*   **`CompletableFuture`** là lựa chọn tuyệt vời khi bạn chỉ cần xử lý một kết quả bất đồng bộ duy nhất và muốn xây dựng một chuỗi các thao tác trên kết quả đó. Nó đơn giản và hiệu quả cho các tác vụ I/O đơn lẻ hoặc tính toán.
*   **`Mono` và `Flux`** cung cấp một mô hình mạnh mẽ hơn cho các hệ thống hướng sự kiện và xử lý luồng dữ liệu. Chúng đặc biệt hữu ích khi bạn cần xử lý một chuỗi các phần tử theo thời gian, cần backpressure để quản lý dòng chảy dữ liệu, hoặc khi bạn đang xây dựng các ứng dụng phản ứng hoàn chỉnh (như với Spring WebFlux).

Trong project hiện tại của chúng ta, việc sử dụng `CompletableFuture` là phù hợp vì chúng ta xử lý từng `Card` và `Pin` như các tác vụ độc lập và chờ đợi kết quả của từng tác vụ đó. Mặc dù có nhiều đối tượng, nhưng mỗi đối tượng được xử lý như một "đơn vị" hoàn chỉnh, và `CompletableFuture.allOf()` được dùng để tổng hợp các kết quả đơn lẻ này.

---

## 2. Mô tả tổng quan kiến trúc project (Processing Service, Unified API Service)

Project được thiết kế theo kiến trúc microservices với hai service chính:

### 1. `Processing Service` (Dịch vụ Xử lý)
*   **Vai trò chính:** Dịch vụ "đầu não", nhận dữ liệu đầu vào hỗn hợp (Card/Pin), điều phối tách biệt và xử lý song song, giao tiếp với `Unified API Service`, và cập nhật DB nội bộ.
*   **Các thành phần cốt lõi:**
    *   **Input/Entry Point:** Nơi nhận danh sách hỗn hợp.
    *   **Data Models:** `Card.java`, `Pin.java` (cấu trúc dữ liệu thẻ/PIN).
    *   **`CardPinProcessingService.java`:** Service chính, tách danh sách, điều phối xử lý song song, gọi API, cập nhật DB.
    *   **`ExternalApiClient.java`:** Client HTTP gửi yêu cầu đến `Unified API Service`.
    *   **Repository:** `CardRepository.java`, `PinRepository.java` (giao tiếp DB nội bộ).
    *   **DTOs:** `UnifiedApiRequestDTO.java`, `ProcessedItemResponse.java` (truyền dữ liệu giữa các service).

### 2. `Unified API Service` (Dịch vụ API Hợp nhất)
*   **Vai trò chính:** Cung cấp một API endpoint duy nhất để `Processing Service` gửi yêu cầu kiểm tra trạng thái hoặc xử lý sơ bộ cho cả `Card` và `Pin`.
*   **Các thành phần cốt lõi:**
    *   **REST Controller (`UnifiedProcessingController.java`):** Cung cấp API endpoint duy nhất (ví dụ: `POST /check-status`).
    *   **Service Layer (`ItemStatusCheckerService.java`):** Chứa logic nghiệp vụ, phân biệt và xử lý `Card`/`Pin` dựa vào `type` trong DTO, trả về phản hồi.
    *   **DTOs:** Tái sử dụng `UnifiedApiRequestDTO.java`, `ProcessedItemResponse.java`.

### Luồng hoạt động tổng thể:
1.  Danh sách hỗn hợp gửi đến `Processing Service`.
2.  `Processing Service` tách danh sách, khởi tạo tác vụ song song (`CompletableFuture`).
3.  Mỗi tác vụ chuyển đổi thành `UnifiedApiRequestDTO` và gọi API của `Unified API Service`.
4.  `Unified API Service` nhận yêu cầu, xử lý logic, trả về `ProcessedItemResponse`.
5.  `Processing Service` nhận phản hồi và cập nhật DB nội bộ.

---

## 3. Luồng dữ liệu và vai trò của các DTO

Việc truyền tải dữ liệu giữa `Processing Service` và `Unified API Service` được thực hiện thông qua các Data Transfer Objects (DTOs). DTOs là các đối tượng đơn giản, chứa dữ liệu và không chứa logic nghiệp vụ.

Các DTOs chính:

### 1. `ItemType` (Enum)
*   **Vai trò:** Dùng để định danh loại đối tượng đang được xử lý (`CARD` hoặc `PIN`).
*   **Luồng dữ liệu:** Nhúng vào `UnifiedApiRequestDTO` để `Unified API Service` phân biệt loại dữ liệu.

### 2. `UnifiedApiRequestDTO`
*   **Vai trò:** DTO chính dùng để gửi yêu cầu từ `Processing Service` đến `Unified API Service`. Đóng gói thông tin cần thiết về một `Card` hoặc `Pin`.
*   **Cấu trúc dự kiến:**
    *   `private ItemType type;`: Loại đối tượng (`CARD` hoặc `PIN`).
    *   `private Map<String, Object> data;`: Chứa dữ liệu cụ thể của `Card` hoặc `Pin`.
*   **Luồng dữ liệu:**
    1.  `Card`/`Pin` được chuyển đổi thành `UnifiedApiRequestDTO` trong `Processing Service`.
    2.  `ExternalApiClient` gửi `UnifiedApiRequestDTO` đến `Unified API Service`.
    3.  `UnifiedProcessingController` nhận và truyền DTO đến `ItemStatusCheckerService`.

### 3. `ProcessedItemResponse`
*   **Vai trò:** DTO được `Unified API Service` sử dụng để gửi phản hồi trở lại `Processing Service` sau khi xử lý xong một `Card` hoặc `Pin`.
*   **Cấu trúc dự kiến:**
    *   `private String itemId;`: ID của đối tượng đã xử lý.
    *   `private ItemType type;`: Loại đối tượng đã xử lý.
    *   `private String status;`: Trạng thái xử lý (ví dụ: "SUCCESS", "FAILED").
    *   `private String message;`: Thông điệp bổ sung.
*   **Luồng dữ liệu:**
    1.  `ItemStatusCheckerService` tạo ra một `ProcessedItemResponse`.
    2.  `UnifiedProcessingController` trả về DTO này làm phản hồi HTTP.
    3.  `ExternalApiClient` nhận phản hồi.
    4.  `CardPinProcessingService` sử dụng thông tin để cập nhật DB nội bộ.

---

## 4. Cơ chế xử lý song song bằng CompletableFuture

Mục tiêu là xử lý danh sách `Card` và `Pin` hiệu quả bằng cách thực thi song song.

### Nguyên lý chung:
1.  **Phân tách:** Danh sách hỗn hợp tách thành `List<Card>` và `List<Pin>`.
2.  **Tạo tác vụ bất đồng bộ:** Mỗi `Card`/`Pin` được gán một tác vụ bất đồng bộ riêng.
3.  **Thực thi song song:** Các tác vụ chạy đồng thời trên các luồng khác nhau.
4.  **Tổng hợp kết quả:** Chờ tất cả tác vụ hoàn thành.

### Các bước triển khai với `CompletableFuture` (trong `CardPinProcessingService`):
1.  **Chấp nhận danh sách hỗn hợp:** `processMixedList(List<Object> mixedItems)`.
2.  **Tách danh sách:** Phân loại `mixedItems` thành `List<Card>` và `List<Pin>`.
3.  **Tạo ExecutorService:** Định nghĩa `ThreadPoolTaskExecutor` để kiểm soát số luồng và tài nguyên.
    *   Cấu hình `corePoolSize`, `maxPoolSize`, `queueCapacity`.
4.  **Tạo các `CompletableFuture` cho từng đối tượng:**
    *   Đối với mỗi `Card` và `Pin`, tạo một `CompletableFuture` gói gọn:
        *   Chuyển đổi thành `UnifiedApiRequestDTO`.
        *   Gọi `externalApiClient.callUnifiedApiService(requestDTO)`.
        *   Xử lý phản hồi (`ProcessedItemResponse`).
        *   Cập nhật cơ sở dữ liệu.
    *   Sử dụng `CompletableFuture.supplyAsync(supplier, taskExecutor)` để chạy tác vụ trên `Executor` đã định nghĩa.
    *   Sử dụng `thenAccept` để xử lý kết quả và `exceptionally` để xử lý lỗi cho từng `CompletableFuture` con.
5.  **Chờ tất cả các `CompletableFuture` hoàn thành:**
    *   Sử dụng `CompletableFuture.allOf(futures.toArray(...))` để chờ tất cả các tác vụ con.
    *   Sử dụng `.join()` hoặc `.get()` để chặn và đợi kết quả cuối cùng.

### Lợi ích:
*   **Hiệu suất:** Tăng tốc độ xử lý tổng thể.
*   **Bất đồng bộ:** Không chặn luồng chính.
*   **Quản lý lỗi:** Dễ dàng xử lý lỗi độc lập cho từng tác vụ con.
*   **Kết hợp tác vụ:** Xây dựng chuỗi các thao tác (`thenAccept`, `thenApply`).
*   **Kiểm soát luồng:** Kiểm soát tài nguyên hệ thống thông qua `ExecutorService`.

---

## 5. Các cân nhắc phi chức năng (Deadlock, Memory Leak, Thread Starvation)

Khi xây dựng hệ thống song song, cần chú ý các vấn đề sau:

### 1. Deadlock (Tắc nghẽn)
*   **Khái niệm:** Hai hoặc nhiều luồng bị kẹt vô thời hạn, chờ đợi tài nguyên mà luồng khác đang giữ.
*   **Nguyên nhân:** Mutual exclusion, hold and wait, no preemption, circular wait.
*   **Phòng tránh:**
    *   Tránh `synchronized` blocks/methods không cần thiết.
    *   Đảm bảo thứ tự khóa nếu phải dùng.
    *   Sử dụng timeout cho khóa (`tryLock(timeout)`).
    *   Kiểm soát số luồng qua `ExecutorService`.

### 2. Memory Leak (Rò rỉ bộ nhớ)
*   **Khái niệm:** Đối tượng không còn dùng nhưng vẫn được tham chiếu, ngăn GC giải phóng, dẫn đến tăng bộ nhớ và `OutOfMemoryError`.
*   **Nguyên nhân:** Tham chiếu không đúng cách, quản lý tài nguyên kém, listener/callback không được gỡ bỏ.
*   **Phòng tránh:**
    *   Tắt `ExecutorService` đúng cách (`shutdown()`, `awaitTermination()`).
    *   Sử dụng `try-with-resources` cho tài nguyên cần đóng.
    *   Kiểm tra và gỡ bỏ tham chiếu không cần thiết trong cache/collection.
    *   Monitoring bộ nhớ bằng JConsole, VisualVM.

### 3. Thread Starvation (Luồng bị bỏ đói)
*   **Khái niệm:** Một hoặc nhiều luồng không nhận được CPU time, bị trì hoãn vô thời hạn.
*   **Nguyên nhân:** Luồng ưu tiên thấp, tác vụ chặn kéo dài, thread pool quá nhỏ.
*   **Phòng tránh:**
    *   Cấu hình `ThreadPoolTaskExecutor` hợp lý.
    *   Tránh các tác vụ chặn trong thread pool chính (sử dụng `CompletableFuture` và `ExecutorService` riêng cho I/O-bound tasks).
    *   Tránh thay đổi ưu tiên luồng không cần thiết.
    *   Sử dụng fair locks nếu cần đảm bảo công bằng.

---