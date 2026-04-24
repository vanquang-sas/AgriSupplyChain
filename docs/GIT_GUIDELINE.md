# 🚩 Quy tắc Git & Workflow làm việc

## 1. Cấu trúc Nhánh (Git Branch)

Sử dụng mô hình **Git Flow** sau:
```
(main) ●────────────●
                    ↑ merge dev

(dev)   ●───●───────●───────●
             \     / 
              ●───●   (feature/login)
                   \
                    ●───● (feature/cart)
```

- **`main`**: Chứa code đã hoàn thiện. Chỉ merge từ `dev` khi code đã chạy ổn định.
- **`dev`**: Nhánh merge các tính năng lại với nhau
- **`feature/<tên-tính-năng>`**: Nhánh riêng để làm từng tính năng. Tách ra từ `dev`, sau khi code xong phải tạo Pull Request để review trước khi gộp lại vào `dev`.
    - *Cách đặt tên:* `feature/dang-nhap`. 
- **`fix/<tên-lỗi>`**: Dùng để fix các bug phát sinh trong quá trình merge code trên `dev`.

---

## 2. Quy trình tạo nhánh mới để làm việc

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

## 3. Nếu đang làm trên `feature` mà trên `dev` có cập nhật mới thì sao?

Nếu cả trên `dev` và `feature` đều có những thay đổi mà muốn cập nhật cái mới từ `dev` vào `feature` thì làm như sau:

1. **Đảm bảo đang ở nhánh `feature`:**
    ```
    git checkout feature
    ```
2. **Cập nhật `dev` mới nhất:**
    ```
    git checkout dev
    git pull origin dev
    ```
3. **Quay về và merge vào `feature`:**
    ```
    git checkout feature
    git merge dev
    ```
4. **Kết quả:** Nhánh `feature` bây giờ sẽ có tất cả code mới từ `dev` + code mới.
5. **Merge conflict:**
    Nếu không có xung đột (conflict), Git sẽ mở trình soạn thảo để bạn nhập nội dung commit (mặc định là "Merge branch 'dev' into 'feature'"). Chỉ cần lưu và thoát.
    Nếu có xung đột (conflict), nếu mở file bị báo lỗi lên, sẽ thấy những ký tự "lạ" mà Git tự chèn vào.
    ```
    <<<<<<< HEAD
    code của bạn (nhánh hiện tại)
    =======
    code của nhánh merge vào
    >>>>>>> branch-name
    ```
    Cách giải quyết
    - Mở file bị conflict
    - **Xoá code không cần, để lại code muốn giữ**
    - *Lưu ý: xóa hết các dấu `<<<<<<<`, `=======`, `>>>>>>>`*
    - `git add` lại file đã sửa
    - `git commit` để lưu lại phiên bản sau khi xử lý conflict
---

## 4. Quy tắc viết git commit

Đặt tên các commit theo quy tắc trong đây: [GIT_COMMIT](GIT_COMMIT.md)