# Báo cáo Giải pháp: Xử lý Lỗi "Pending acquire queue has reached its maximum size"

## 1. Tóm tắt Vấn đề

Hệ thống gặp lỗi "Pending acquire queue has reached its maximum size of 1000", dẫn đến việc các yêu cầu xử lý thẻ (Card) và mã PIN (Pin) bị từ chối hoặc chậm trễ. Lỗi này cho thấy các hàng đợi nội bộ của ứng dụng đã đạt đến giới hạn tối đa, không thể tiếp nhận thêm yêu cầu mới.

## 2. Nguyên nhân Gốc rễ

Lỗi phát sinh do cấu hình mặc định của các tài nguyên xử lý song song và gọi API không đủ khả năng đáp ứng tải lượng cao, cụ thể:

*   **Hàng đợi kết nối WebClient (`Pending acquire queue`):**
    *   `WebClient` trong `processing-service` sử dụng một nhóm kết nối HTTP (`connection pool`) để giao tiếp với `Unified API Service`.
    *   Khi số lượng yêu cầu đồng thời vượt quá số lượng kết nối sẵn có trong nhóm, các yêu cầu mới sẽ được đưa vào một hàng đợi chờ. Lỗi xảy ra khi hàng đợi này đầy, ngăn không cho các yêu cầu mới được xử lý.

*   **Hàng đợi tác vụ của ExecutorService:**
    *   `CardPinProcessingService` sử dụng một `ExecutorService` để thực thi các tác vụ xử lý thẻ và mã PIN một cách song song.
    *   Mặc định, `ExecutorService` có thể sử dụng hàng đợi không giới hạn hoặc giới hạn nhỏ, dẫn đến việc quá tải nếu tốc độ tạo tác vụ nhanh hơn tốc độ xử lý của các luồng.

## 3. Giải pháp Đã triển khai

Để khắc phục vấn đề này, tôi đã thực hiện các thay đổi cấu hình và code sau:

### 3.1. Cấu hình WebClient để tăng khả năng chịu tải

*   **File bị thay đổi:** [`processing-service/src/main/java/com/example/processingservice/service/ExternalApiClient.java`](processing-service/src/main/java/com/example/processingservice/service/ExternalApiClient.java) và [`processing-service/src/main/resources/application.properties`](processing-service/src/main/resources/application.properties)
*   **Chi tiết thay đổi:**
    *   Trong `ExternalApiClient`, tôi đã cấu hình `WebClient` sử dụng `HttpClient` tùy chỉnh với `ConnectionProvider` có kích thước nhóm kết nối lớn hơn và thời gian chờ được điều chỉnh.
    *   Các tham số cấu hình mới đã được thêm vào `application.properties`:
        *   `unified.api.connection-pool-size=2000`: Tăng số lượng kết nối tối đa trong nhóm lên 2000 để cho phép nhiều yêu cầu HTTP được xử lý đồng thời hơn.
        *   `unified.api.connect-timeout-millis=10000`: Tăng thời gian chờ thiết lập kết nối lên 10 giây.
        *   `unified.api.read-timeout-millis=60000`: Tăng thời gian chờ đọc phản hồi lên 60 giây.
*   **Lý do:** Việc tăng kích thước nhóm kết nối giúp giảm thiểu tình trạng tắc nghẽn ở phía `WebClient`, cho phép gửi nhiều yêu cầu hơn tới `Unified API Service`. Thời gian chờ dài hơn giúp hệ thống kiên nhẫn hơn trong môi trường có độ trễ cao hoặc khi dịch vụ đích bị quá tải.

### 3.2. Cấu hình ExecutorService để quản lý tác vụ song song hiệu quả hơn

*   **File bị thay đổi:** [`processing-service/src/main/java/com/example/processingservice/service/CardPinProcessingService.java`](processing-service/src/main/java/com/example/processingservice/service/CardPinProcessingService.java) và [`processing-service/src/main/resources/application.properties`](processing-service/src/main/resources/application.properties)
*   **Chi tiết thay đổi:**
    *   Thay vì sử dụng `Executors.newFixedThreadPool` với hàng đợi không giới hạn (có thể gây tràn bộ nhớ), tôi đã chuyển sang sử dụng `ThreadPoolExecutor` với cấu hình chi tiết hơn.
    *   Các tham số cấu hình mới đã được thêm vào `application.properties`:
        *   `app.processing.thread-pool.core-size=50`: Số luồng tối thiểu luôn được giữ trong nhóm.
        *   `app.processing.thread-pool.max-size=100`: Số luồng tối đa có thể được tạo ra trong nhóm.
        *   `app.processing.thread-pool.queue-capacity=5000`: Giới hạn số lượng tác vụ có thể chờ trong hàng đợi của nhóm luồng.
    *   **Thêm `ThreadPoolExecutor.CallerRunsPolicy()`:** Đây là một chính sách xử lý khi hàng đợi tác vụ đầy. Thay vì từ chối tác vụ, luồng gọi (luồng đã gửi tác vụ) sẽ tự thực thi tác vụ đó.
*   **Lý do:**
    *   Việc giới hạn kích thước hàng đợi ngăn chặn tình trạng tràn bộ nhớ do quá nhiều tác vụ chờ xử lý.
    *   `CallerRunsPolicy` tạo ra một cơ chế áp lực ngược (backpressure): khi nhóm luồng quá tải, luồng gọi sẽ bị chặn cho đến khi tác vụ của nó được thực thi, do đó làm chậm tốc độ tạo tác vụ mới và cho phép hệ thống "thở" và xử lý các tác vụ đang tồn đọng. Điều này giúp hệ thống tự điều chỉnh và tránh sập dưới tải cao.

## 4. Kết luận

Các thay đổi này cải thiện đáng kể khả năng xử lý đồng thời và độ bền của `processing-service` khi giao tiếp với `Unified API Service`. Bằng cách tối ưu hóa các nhóm kết nối và luồng xử lý, hệ thống giờ đây có thể xử lý lượng lớn yêu cầu một cách hiệu quả hơn, giảm thiểu nguy cơ gặp lỗi "Pending acquire queue".