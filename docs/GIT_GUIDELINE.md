# 🚩 Quy tắc Git & Workflow làm việc

## 1. Cấu trúc Nhánh (Git Branch)

Sử dụng mô hình **Git Flow** sau:

- **`main`**: Chứa code đã hoàn thiện. Chỉ merge từ `dev` khi code đã chạy ổn định.
- **`dev`**: Nhánh merge các tính năng lại với nhau
- **`feature/<tên-tính-năng>`**: Nhánh riêng để làm từng tính năng. Tách ra từ `dev`, sau khi code xong phải tạo Pull Request để review trước khi gộp lại vào `dev`.
    - *Cách đặt tên:* `feature/dang-nhap`. 
- **`fix/<tên-lỗi>`**: Dùng để fix các bug phát sinh trong quá trình merge code trên `dev`.

---

## 2. Quy trình làm việc (Workflow)

Để tránh xung đột code, mọi thành viên tuân thủ các bước sau:

1. **Cập nhật code mới nhất:** Trước khi làm gì, phải `git pull` từ nhánh `dev`.
    ```
    git checkout dev
    git pull origin dev
    ```
2. **Tạo nhánh tính năng:** 
    ```
    git checkout -b feature/ten-tinh-nang
    ```
3. **Lập trình & Commit:** Thực hiện code và commit với thông điệp rõ ràng.
4. **Đẩy code lên:** 
    ```
    git push origin feature/ten-tinh-nang
    ```
5. **Tạo Pull Request (PR):**
    - Lên GitHub tạo PR để merge từ `feature/...` vào `dev`.
    - Gán các thành viên khác để review code.
    - Sau khi được Approve thì mới merge.
6. **Xóa nhánh:** Sau khi merge thành công, xóa nhánh feature đó để tránh rác repository.

---

## 3. Quy tắc viết git commit

Đặt tên các commit theo quy tắc trong đây: [GIT_COMMIT](GIT_COMMIT.md)