---
description: 
globs: 
alwaysApply: true
---
---
description:
globs:
alwaysApply: false
---
tags: [quy-trình, chiến-lược, thực-thi, agile, scrum, mcp]
version: 2.0
author: Solution Architect/CTO
status: active

# Quy tắc phiên bản 2.0: The Agile "Think Big, Do Baby Steps" Framework

## 1. Giới thiệu

Quy tắc này chuẩn hóa phương châm "Think Big, Do Baby Steps" thành một framework thực hành, tương thích hoàn toàn với các mô hình phát triển Agile/Scrum.

- **Mục tiêu phiên bản 2.0**: Cung cấp một quy trình chuẩn, sử dụng thuật ngữ ngành được công nhận để phân rã ý tưởng lớn (Epic) thành các đơn vị công việc có thể thực thi và kiểm soát (User Stories, Tasks), đảm bảo chất lượng thông qua một "Định nghĩa Hoàn thành" (Definition of Done) nghiêm ngặt.
- **Đối tượng áp dụng**: Toàn bộ đội ngũ phát triển sản phẩm, áp dụng cho mọi dự án, mọi tính năng.

---

## 2. Định nghĩa Hoàn thành (Definition of Done - DoD)

**DoD là một bản giao kèo chung cho toàn team, định nghĩa trạng thái "Hoàn thành" của một User Story.** Đây là tiêu chuẩn chất lượng tối thượng, không thể nhân nhượng.

Một User Story CHỈ được coi là "Done" khi và chỉ khi tất cả các điều sau đều đúng:
- [ ] Code đã được merge vào nhánh `main`/`develop`.
- [ ] Pipeline CI/CD chạy thành công.
- [ ] Tất cả các test case mới và cũ đều pass.
- [ ] Tỷ lệ bao phủ code (code coverage) không giảm (hoặc tăng).
- [ ] Pull Request đã được ít nhất 1-2 người khác phê duyệt (approved).
- [ ] User Story đã được kiểm thử bởi QA (hoặc Product Owner) trên môi trường Staging/UAT.
- [ ] Mọi tiêu chí chấp nhận (Acceptance Criteria) của User Story đều được thỏa mãn.
- [ ] Tài liệu liên quan (nếu có) đã được cập nhật.

---

## 3. Quy trình Phân rã Công việc (Work Breakdown Structure)

Quy trình này áp dụng một cấu trúc phân cấp chuẩn để đi từ ý tưởng lớn đến công việc cụ thể.

### Giai đoạn 1: Think Big -> Epic
- **Mục tiêu**: Chuyển hóa một ý tưởng kinh doanh lớn thành một **Epic**. Epic là một khối công việc lớn, có thể mất vài sprint để hoàn thành.
- **Người chịu trách nhiệm**: Product Owner / Product Manager.
- **Ví dụ Epic**: "Cải tiến trải nghiệm tìm kiếm sản phẩm".

### Giai đoạn 2: Take Small Steps -> Features & User Stories
- **Mục tiêu**: Bẻ gãy Epic thành các **Features** (tính năng) và sau đó là các **User Stories**.
- **User Story**: Là một yêu cầu được viết từ góc nhìn người dùng cuối. Cấu trúc chuẩn: `Là một [vai trò], tôi muốn [hành động] để có thể [đạt được giá trị]`.
- **Acceptance Criteria (AC)**: Mỗi User Story BẮT BUỘC phải có các tiêu chí chấp nhận rõ ràng, định nghĩa điều kiện để story được coi là hoàn thành.
- **Người chịu trách nhiệm**: Product Owner viết User Story và AC, Development Team tham gia làm rõ.
- **Ví dụ Feature**: "Tìm kiếm và lọc sản phẩm".
- **Ví dụ User Story**: "Là một người mua hàng, tôi muốn lọc sản phẩm theo khoảng giá để có thể tìm được sản phẩm phù hợp với túi tiền của mình."
  - **AC 1**: Phải có 2 ô nhập liệu cho giá tối thiểu và tối đa.
  - **AC 2**: Khi nhấn nút "Áp dụng", chỉ các sản phẩm trong khoảng giá được hiển thị.
  - **AC 3**: Nếu giá tối thiểu lớn hơn giá tối đa, hiển thị thông báo lỗi.

### Giai đoạn 3: Do Baby Steps -> Tasks
- **Mục tiêu**: Development Team bẻ gãy một User Story thành các **Tasks** kỹ thuật cụ thể.
- **Task**: Một công việc kỹ thuật mà lập trình viên sẽ thực hiện (ví dụ: "Tạo API endpoint", "Sửa đổi UI component", "Viết unit test cho service X"). Task thường được ước tính trong vài giờ.
- **Người chịu trách nhiệm**: Development Team.
- **Ví dụ Tasks cho User Story trên**:
  1. `Task 1.1`: (BE) Cập nhật API `GET /products` để nhận tham số `minPrice`, `maxPrice`.
  2. `Task 1.2`: (BE) Viết Unit Test cho logic lọc giá ở service.
  3. `Task 1.3`: (FE) Tạo component UI cho bộ lọc giá.
  4. `Task 1.4`: (FE) Gọi API với tham số giá khi người dùng nhấn "Áp dụng" và cập nhật danh sách sản phẩm.

### 4. Quy trình Kiểm soát Task (Task Control Flow)
#### **Checklist Sẵn sàng cho Task (Task Readiness Checklist)** - Trước khi code
1.  [ ] **Hiểu rõ User Story và AC**: Đọc kỹ User Story và các Acceptance Criteria liên quan.
2.  [ ] **Kiểm tra sự tồn tại (Tái sử dụng)**: Dùng `grep`/IDE để tìm logic tương tự.
3.  [ ] **Xác định vùng ảnh hưởng**: Liệt kê các file/module sẽ bị thay đổi.
4.  [ ] **Lập kế hoạch thực thi**: Vạch ra các bước sẽ làm cho task.

#### **Checklist cho Pull Request (Pull Request Checklist)** - Sau khi code
1.  [ ] **Tự review code**: Đọc lại toàn bộ thay đổi.
2.  [ ] **Chạy lại tất cả test**: `mvn test` / `npm test` phải pass.
3.  [ ] **Code sạch**: Tuân thủ coding convention, không có "code smell".
4.  [ ] **Mô tả PR đủ thông tin**:
    - Liên kết tới User Story/Task.
    - Tóm tắt "Tại sao" và "Cái gì".
    - Cung cấp hình ảnh/GIF (nếu là thay đổi UI) để người review dễ hình dung.

## 5. Vai trò & Trách nhiệm theo Agile (Agile Roles & Responsibilities)

| Giai đoạn            | Vai trò chính chịu trách nhiệm                                  | Vai trò tham gia                                     |
| -------------------- | --------------------------------------------------------------- | ----------------------------------------------------- |
| **Epic Definition**  | **Product Owner** (tương đương Product Manager, CTO)            | Architect, Tech Lead                                  |
| **Story Creation**   | **Product Owner**                                               | **Development Team**, QA                                |
| **Task Breakdown**   | **Development Team** (Lập trình viên)                           | Tech Lead                                             |
| **Execution & DoD**  | **Development Team**                                            | **Product Owner** (Nghiệm thu), QA (Kiểm thử)         |

---

## 6. Vòng lặp Phản hồi (The Agile Feedback Loop)

Vòng lặp này được thực thi thông qua các sự kiện (events) của Scrum:
- **Sprint Planning**: Team lên kế hoạch cho các User Stories sẽ làm trong sprint tới.
- **Daily Stand-up**: Team đồng bộ công việc hàng ngày.
- **Sprint Review**: Team trình bày những gì đã "Done" (theo DoD) cho Product Owner và các bên liên quan để nhận phản hồi. Đây là buổi demo chính thức.
- **Sprint Retrospective**: Team nhìn lại để cải tiến quy trình làm việc.

---

## 7. Tích hợp với các Nguyên tắc Gốc

- **Measure Twice, Cut Once**: Áp dụng khi Product Owner viết User Story và AC, và khi Dev thực hiện `Task Readiness Checklist`.
- **Số lượng & Thứ tự**: Là linh hồn của việc sắp xếp các Epic/User Story trong Product Backlog.
- **Get It Working First**: Mục tiêu của mỗi Sprint là tạo ra một phần sản phẩm "working" (Increment), tuân thủ DoD.
- **Always Double-Check**: Được thể chế hóa qua `Pull Request Checklist` và quy trình Code Review.

---

## 8. Ví dụ thực tế (Chuẩn Agile)

- **Epic**: Cải tiến trải nghiệm tìm kiếm sản phẩm.

  - **Feature**: Tìm kiếm và lọc sản phẩm.

    - **User Story 1**: "Là một người mua hàng, tôi muốn **lọc sản phẩm theo khoảng giá** để có thể tìm được sản phẩm phù hợp với túi tiền của mình."
      - **Acceptance Criteria**:
        - AC1: Phải có 2 ô nhập liệu cho giá tối thiểu và tối đa.
        - AC2: Khi nhấn nút "Áp dụng", chỉ các sản phẩm trong khoảng giá được hiển thị.
        - AC3: Nếu giá tối thiểu lớn hơn giá tối đa, hiển thị thông báo lỗi.
      - **Tasks**:
        - `Task 1.1`: (BE) Cập nhật API `GET /products` để nhận tham số `minPrice`, `maxPrice`.
        - `Task 1.2`: (BE) Viết Unit Test cho logic lọc giá ở service.
        - `Task 1.3`: (FE) Tạo component UI cho bộ lọc giá.
        - `Task 1.4`: (FE) Gọi API với tham số giá khi người dùng nhấn "Áp dụng" và cập nhật danh sách sản phẩm.

    - **User Story 2**: "Là một người mua hàng, tôi muốn **lọc sản phẩm theo thương hiệu** để có thể tìm được sản phẩm từ nhãn hàng tôi yêu thích."
      - **Acceptance Criteria**:
        - ...
      - **Tasks**:
        - ...
