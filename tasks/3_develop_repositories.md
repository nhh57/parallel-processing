# Task 3: Phát triển Repository (Card, Pin) (Đã hoàn thành)

## Mục tiêu
Tạo các interface repository cho `Card` và `Pin` để `Processing Service` có thể tương tác với cơ sở dữ liệu.

## Các bước thực hiện

- [x] Tạo interface `CardRepository.java`:
    - [x] Tạo file `src/main/java/com/example/processingservice/repository/CardRepository.java`.
    - [x] Kế thừa từ `JpaRepository` để có sẵn các phương thức CRUD cơ bản.

- [x] Tạo interface `PinRepository.java`:
    - [x] Tạo file `src/main/java/com/example/processingservice/repository/PinRepository.java`.
    - [x] Kế thừa từ `JpaRepository` để có sẵn các phương thức CRUD cơ bản.