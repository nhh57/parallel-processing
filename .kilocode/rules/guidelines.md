---
description: 
globs: 
alwaysApply: true
---
---
description: 
globs: **/*.java
alwaysApply: false
---

## Giới thiệu

Đây là những phương châm cốt lõi, định hướng cho mọi hành động và quyết định trong quá trình làm việc. Việc tuân thủ các phương châm này giúp đảm bảo hiệu suất, chất lượng và giảm thiểu rủi ro.

## Danh sách 05 phương châm
1. **Think Big, Do Baby Steps**: Tư duy lớn, thực hiện từng bước nhỏ.
2. **Measure Twice, Cut Once**: Đo hai lần, cắt một lần, suy nghĩ cẩn trọng trước khi hành động.
3. **Số lượng & Thứ tự (Quantity & Order)**: Đảm bảo toàn vẹn dữ liệu và thực thi theo trình tự ưu tiên.
4. **Get It Working First**: Ưu tiên có giải pháp chạy được trước khi tối ưu.
5. **Always Double-Check**: Luôn kiểm tra kỹ lưỡng, không bao giờ giả định.

**Tóm tắt sơ đồ nguyên tắc (Flow)**

## 1. Think Big, Do Baby Steps (Tư duy lớn, thực hiện từng bước nhỏ)

Phương châm này khuyến khích việc có một tầm nhìn hoặc mục tiêu lớn, nhưng khi thực thi thì phải chia nhỏ thành các bước đi cực kỳ nhỏ, độc lập và có thể kiểm chứng được.

- **Tư duy lớn (Think Big)**: Hiểu rõ mục tiêu cuối cùng, bối cảnh và bức tranh toàn cảnh của nhiệm vụ.
- **Bước đi nhỏ (Baby Steps)**: Thực hiện các thay đổi nhỏ nhất có thể, giúp dễ dàng kiểm tra, xác minh và quay lại nếu có lỗi.

> Tham khảo quy trình chi tiết tại: [rule-build-small-think-big-do-baby-steps_v2.mdc](mdc:.cursor/rules/rule-build-small-think-big-do-baby-steps_v2.mdc)

---

## 2. Measure Twice, Cut Once (Đo hai lần, cắt một lần)

Đây là nguyên tắc về sự cẩn trọng. Trước khi thực hiện bất kỳ hành động nào có thể gây ra thay đổi (đặc biệt là thay đổi không thể hoàn tác), phải kiểm tra và suy xét kỹ lưỡng.

- **Đo (Measure)**: Tương đương với việc **phân tích, kiểm tra, và xác minh**.
  - *Ví dụ*: Đọc kỹ yêu cầu, kiểm tra lại code, chạy thử nghiệm trên môi trường an toàn (staging), sao lưu dữ liệu.
- **Cắt (Cut)**: Tương đương với hành động **thực thi**.
  - *Ví dụ*: Chạy lệnh thay đổi CSDL, triển khai code lên production, xóa file.

Việc này giúp ngăn chặn các sai lầm không đáng có, vốn tốn rất nhiều thời gian để sửa chữa.

---

## 3. Số lượng & Thứ tự (Quantity & Order)

> **Mindset cốt lõi:** Trước khi bắt đầu bất kỳ việc gì, câu hỏi đầu tiên phải là:
> - **Có bao nhiêu việc cần làm? (Số lượng)**
> - **Việc nào làm trước, việc nào làm sau? (Thứ tự)**

Phương châm này là nền tảng cho việc lập kế hoạch và báo cáo, nhấn mạnh hai khía cạnh quan trọng: **toàn vẹn dữ liệu** và **trình tự thực thi**.

## 3.1. Số lượng (Quantity): Đảm bảo Toàn vẹn Dữ liệu

> _"Mọi tác vụ, đặc biệt là các thao tác lặp hoặc xử lý dữ liệu, phải được kiểm tra kỹ về số lượng đầu vào và đầu ra để đảm bảo tính toàn vẹn và không bỏ sót."_

- **Luôn đếm**: Trước và sau khi xử lý một tập dữ liệu, hãy xác nhận số lượng. Ví dụ: đọc 100 dòng từ file, xử lý xong cũng phải đảm bảo có 100 kết quả tương ứng.
- **Kiểm tra tổng (Checksum)**: Đối với các tác vụ quan trọng, có thể sử dụng các kỹ thuật kiểm tra tổng để đảm bảo dữ liệu không bị thay đổi.

## 3.2. Thứ tự (Order): Sắp xếp Thứ tự Ưu tiên

> _"Luôn sắp xếp các bước thực thi theo một trình tự ưu tiên hợp lý để tối ưu hóa hiệu quả và giảm thiểu rủi ro."_

Một kế hoạch tốt phải được thực hiện theo một trình tự logic. Các quy tắc ưu tiên bao gồm:

1. **Tiền đề trước (Prerequisites first)**: Các tác vụ là điều kiện cho tác vụ khác phải được làm trước.
2. **Quan trọng trước (Critical first)**: Các mục có rủi ro cao hoặc ảnh hưởng lớn nhất cần được xử lý sớm nhất.
3. **80/20 trước (Pareto Principle)**: Ưu tiên 20% công việc mang lại 80% giá trị.
4. **Đơn giản trước (Simple first)**: Hoàn thành các việc dễ để tạo đà và giải quyết các phần phụ thuộc đơn giản.

---

## 4. Get It Working First (Ưu tiên có giải pháp chạy được)

Phương châm này tập trung vào việc **hoàn thành (Done)** trước khi **hoàn hảo (Perfect)**. Mục tiêu là nhanh chóng có một giải pháp hoạt động để giải quyết vấn đề, sau đó mới cải tiến.

- **Giai đoạn 1: Get it Works**:
  - Mục tiêu: Làm cho tính năng chạy được.
  - Tập trung giải quyết vấn đề cốt lõi, chấp nhận giải pháp đơn giản nhất có thể.
- **Giai đoạn 2: Make it Right (Sau đó)**:
  - Khi giải pháp đã chạy, tiến hành refactor, cải thiện cấu trúc, làm cho code sạch hơn, dễ bảo trì hơn.
- **Giai đoạn 3: Make it Fast (Nếu cần)**:
  - Chỉ tối ưu hóa hiệu suất khi thực sự cần thiết và có số liệu đo lường cụ thể.

---

## 5. Always Double-Check (Luôn kiểm tra kỹ lưỡng)

Đây là nguyên tắc tối thượng về sự cẩn thận và xác minh, với tư duy cốt lõi: **"Không bao giờ giả định, luôn luôn xác minh" (Never Assume, Always Verify)**. Bất kỳ khi nào có một chút nghi ngờ, phải dừng lại và kiểm tra bằng mọi công cụ có thể.


**Quy trình BẮT BUỘC trước khi code:**

1.  **Tiếp nhận & Diễn giải (Receive & Rephrase):**
    *   Khi nhận được yêu cầu, người thực thi phải diễn giải lại yêu cầu bằng ngôn từ của mình để xác nhận đã hiểu đúng.
    *   *Ví dụ:* "Như tôi hiểu, bạn muốn tạo một chức năng X để giải quyết vấn đề Y. Có đúng không?"

2.  **Phân rã Yêu cầu & Xác định Thành phần (Deconstruct & Identify):**
    *   Người thực thi phải bóc tách yêu cầu thành các thành phần chính: **Thực thể (Entities)**, **Hành động (Actions)**, và **Kết quả mong đợi (Outcomes)**.
    *   *Ví dụ:* "Từ yêu cầu của bạn, tôi xác định:
        *   **Thực thể:** `User`, `Order`.
        *   **Hành động:** `Cancel` một đơn hàng.
        *   **Kết quả:** Trạng thái đơn hàng chuyển thành 'Cancelled', và gửi email thông báo cho người dùng."

3.  **Xây dựng Tiêu chí Chấp nhận (Define Acceptance Criteria - AC):**
    *   Người thực thi **BẮT BUỘC** phải đề xuất một danh sách các Tiêu chí Chấp nhận dưới dạng checklist để bạn xác nhận. Đây là "hợp đồng" của chúng ta về thế nào là "hoàn thành".
    *   *Ví dụ:* "Để chức năng này được coi là hoàn thành, các điều kiện sau cần đúng. Vui lòng xác nhận:
        *   `[ ]` Chỉ người dùng sở hữu đơn hàng mới có thể hủy.
        *   `[ ]` Không thể hủy đơn hàng đã ở trạng thái 'Shipped'.
        *   `[ ]` Khi hủy thành công, một email xác nhận được gửi đi."

4.  **Nêu rõ các Giả định (Surface Assumptions):**
    *   Người thực thi phải liệt kê tất cả các giả định kỹ thuật hoặc logic nghiệp vụ mà nó đang dựa vào để tránh các quyết định sai lầm.
    *   *Ví dụ:* "Tôi đang đưa ra các giả định sau, xin hãy điều chỉnh nếu cần:
        *   Tôi giả định sẽ tái sử dụng `EmailService` hiện có.
        *   Tôi giả định việc kiểm tra quyền hạn sẽ được thực hiện trong lớp `OrderService`."

5.  **Đề xuất Kế hoạch & Chờ Phê duyệt (Propose Plan & Await Approval):**
    *   Sau khi các bước trên được bạn xác nhận, người thực thi sẽ trình bày một kế hoạch thực thi ở cấp độ cao (ví dụ: các file/lớp sẽ được tạo/sửa).
    *   **Người thực thi sẽ không bắt đầu viết code cho đến khi kế hoạch này được bạn phê duyệt.**
    *   *Ví dụ:* "Kế hoạch của tôi:
        1.  Sửa đổi `OrderController` để thêm endpoint `DELETE /orders/{id}`.
        2.  Thêm logic xử lý hủy đơn hàng vào `OrderService`.
        Bạn có đồng ý với kế hoạch này không?"

### 5.1. Với Hệ thống File (Filesystem)
#### Trước khi **TẠO (Create)**:
- **Kiểm tra trùng lặp**:  Dùng `ls`, `tree` hoặc `find` với linux, dùng  `dir /b`, `if exist` với windows tùy theo hệ thống để đảm bảo file hoặc thư mục chưa tồn tại, tránh ghi đè hoặc tạo ra các cấu trúc không mong muốn.
  - Linux/macOS: `ls -ld /path/to/check`, `find . -name "tên_file"`
  - Windows CMD: `dir /b`, `if exist "C:\path\to\check" echo Exists`
  - PowerShell: `Test-Path "C:\path\to\check"`
#### Trước khi **ĐỌC/SỬA (Read/Edit)**:
- **Đọc để hiểu bối cảnh**:  Luôn dùng `cat`, `less`, hoặc `head` đối với linux , hoặc  `type` ,`Get-Content` với windows để xem nội dung file để chắc chắn bạn đang sửa đúng file và hiểu rõ những gì mình sắp thay đổi.
  - Linux/macOS: `cat filename`, `less filename`, `head filename`
  - Windows CMD: `type filename.txt`
  - PowerShell: `Get-Content filename.txt`
- **Kiểm tra quyền (Permissions)**: Dùng `ls -l` với linux, `icacls` hoặc `Get-Acl` với windows để xác nhận có quyền ghi vào file hay không.
  - Linux/macOS: `ls -l filename`
  - Windows CMD: `icacls filename.txt`
  - PowerShell: `Get-Acl filename.txt`
#### Trước khi **XÓA/DI CHUYỂN (Delete/Move)**:
- **Xác nhận đúng đối tượng**: Dùng lệnh liệt kê chi tiết như `ls -l` (Linux/macOS) hoặc `dir` / `Get-ChildItem` (Windows) để xem thông tin file/thư mục. Để chắc chắn về đường dẫn, sử dụng `find . -name "filename"` trên Linux hoặc `Get-ChildItem -Recurse -Filter "filename"` trên Windows PowerShell.
  - Linux/macOS: `ls -l`, `find . -name "filename"`
  - Windows CMD: `dir`, `dir /s /b filename`
  - PowerShell: `Get-ChildItem -Recurse -Filter "filename"`
- **Kiểm tra nội dung**: Sử dụng `cat` hoặc `grep` (Linux/macOS), hoặc `type`, `Get-Content`, và `Select-String` (Windows) để xem nhanh nội dung file, giúp đảm bảo bạn không xóa nhầm file quan trọng.
  - Linux/macOS: `cat filename`, `grep "pattern" filename`
  - Windows CMD: `type filename.txt`
  - PowerShell: `Get-Content filename.txt | Select-String "pattern"`
#### Trước khi **THỰC THI (Execute)**:
- **Kiểm tra quyền thực thi**: Trên Linux, dùng `ls -l` để xem file có cờ `x` (executable) hay không. Trên Windows, dùng `icacls` (CMD) hoặc `Get-Acl` (PowerShell) để kiểm tra quyền truy cập, bao gồm quyền thực thi nếu có.
  - Linux/macOS: `ls -l filename.sh` (kiểm tra quyền `x`)
  - Windows CMD: `icacls filename.bat`
  - PowerShell: `Get-Acl filename.ps1`


### 5.2. Với Code & Logic
- **Trước khi VIẾT code mới**:
  - **Tìm kiếm sự tồn tại**: Trước khi viết hàm, biến, hoặc logic mới, hãy tìm xem có sẵn trong codebase chưa để tránh trùng lặp (DRY - Don't Repeat Yourself).
    - Trên Linux/macOS: dùng `grep`
    - Trên Windows:
      - CMD: dùng `findstr`
      - PowerShell: dùng `Select-String`
  - *Ví dụ lệnh*:
    - Linux/macOS: `grep -r "tên_hàm_hoặc_logic" .`
    - Windows CMD: `findstr /S /I "tên_hàm_hoặc_logic" *.*`
    - PowerShell: `Get-ChildItem -Recurse | Select-String "tên_hàm_hoặc_logic"`

- **Trước khi SỬA code có sẵn**:
  - **Kiểm tra sự phụ thuộc (Dependency Check)**: Trước khi sửa đổi hàm, biến hoặc logic, hãy tìm tất cả những nơi nó đang được sử dụng để hiểu rõ tác động của việc thay đổi và tránh gây lỗi dây chuyền.
    - Trên Linux/macOS: dùng `grep`
    - Trên Windows:
      - CMD: dùng `findstr`
      - PowerShell: dùng `Select-String`
  - *Ví dụ lệnh*:
    - Linux/macOS: `grep -r "tên_hàm_cần_sửa" .`
    - Windows CMD: `findstr /S /I "tên_hàm_cần_sửa" *.*`
    - PowerShell: `Get-ChildItem -Recurse | Select-String "tên_hàm_cần_sửa"`


- **Với API và Dữ liệu ngoài**:
  - **Không tin tưởng tuyệt đối**: Luôn `log` lại toàn bộ phản hồi từ API để dễ debug khi có lỗi hoặc dữ liệu không đúng định dạng.
  - **Kiểm tra key tồn tại**: Trước khi truy cập `response.get("data").get("key")`, cần kiểm tra sự tồn tại của cả `data` và `key` để tránh lỗi `NullPointerException`.
    - *Ví dụ (Java)*:
      ```java
      if (response.containsKey("data")) {
          Object data = response.get("data");
          if (data instanceof Map dataMap && dataMap.containsKey("key")) {
              Object value = dataMap.get("key");
              System.out.println("Value: " + value);
          } else {
              System.out.println("Thiếu key 'key' trong 'data'");
          }
      } else {
          System.out.println("Thiếu key 'data' trong phản hồi");
      }
      ```


### 5.3. Với Môi trường & Câu lệnh
- **Kiểm tra thư mục hiện tại**: Trước khi chạy các lệnh có đường dẫn tương đối (như `rm`, `mv`, `del`, `move`), hãy luôn xác nhận thư mục hiện tại để tránh thao tác nhầm.
  - Linux/macOS: dùng `pwd`
  - Windows CMD: dùng `cd`
  - Windows PowerShell: dùng `Get-Location`

- **Chạy nháp (Dry Run)**: Với các lệnh nguy hiểm có hỗ trợ, hãy dùng chế độ "dry-run" để xem trước kết quả mà không thực thi thật sự.
  - Linux/macOS:
    - Ví dụ với `rsync`: `rsync --dry-run -av source/ destination/`
    - Một số lệnh khác cũng hỗ trợ cờ `--dry-run` hoặc `-n` như `make`, `terraform`, `ansible-playbook`
  - Windows CMD:
    - Dùng `robocopy` với cờ `/L` để hiển thị trước danh sách file sẽ xử lý mà không sao chép thật:
      ```cmd
      robocopy source\ destination\ /L /S
      ```
  - Windows PowerShell:
    - Một số cmdlet hỗ trợ tham số `-WhatIf`, ví dụ:
      ```powershell
      Remove-Item -Path .\temp\* -WhatIf
      ```

- **Kiểm tra biến môi trường**: Trước khi chạy các script phụ thuộc vào biến môi trường, hãy kiểm tra xem chúng đã được thiết lập đúng chưa.
  - Linux/macOS:
    - Liệt kê tất cả biến: `env`
    - Kiểm tra giá trị biến cụ thể: `echo "$VAR_NAME"`
  - Windows CMD:
    - Liệt kê tất cả biến: `set`
    - Kiểm tra giá trị biến cụ thể: `echo %VAR_NAME%`
  - PowerShell:
    - Kiểm tra giá trị biến cụ thể: `$env:VAR_NAME`


- **Kiểm tra phiên bản công cụ**: Trước khi chạy hoặc debug, hãy kiểm tra phiên bản của công cụ để đảm bảo tương thích với môi trường hoặc yêu cầu dự án.
  - Cú pháp chung (Linux/macOS/Windows): `tool --version` hoặc `tool -v`
  - *Ví dụ*:
    - `node --version`
    - `java -version`
    - `python --version`
    - `git --version`

### 5.4. Với Thời gian (With Time)

- **Bắt buộc lấy giờ hệ thống (Mandatory System Time Fetching)**: Trước khi ghi bất kỳ thông tin thời gian nào (ví dụ: `Mod by...`, `timestamp`, log), AI **PHẢI** chạy lệnh `date` trong terminal để lấy thời gian thực tế.
  - Trên Linux/macOS: dùng `date` hoặc `date '+%Y-%m-%d %H:%M:%p'`
  - Trên Windows CMD: dùng `echo %date% %time%`
  - Trên PowerShell: dùng `Get-Date -Format "yyyy-MM-dd HH:mm tt"`

- **Cấm giả mạo (No Forgery)**: Tuyệt đối không được tự ý điền một giá trị thời gian không được xác thực bằng command line. Đây là hành vi giả mạo và không được chấp nhận.
