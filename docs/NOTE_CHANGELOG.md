# Quy định Ghi chép Thay đổi (Changelog)

## 📌 Yêu cầu cơ bản
- **Mọi thay đổi** so với mã nguồn gốc trên nhánh `dev` đều phải được ghi chép lại một cách chi tiết trong file changelog của module mà mình đảm nhiệm.
- Nên ghi chép ngay sau khi bạn hoàn thành một đơn vị công việc (Task/Feature), **sắp xếp theo thứ tự thời gian**.

## 📑 Template
### [Tên] - DD/MM/YYYY - [Nhánh thực hiện]
- **Thêm mới:**
  - Nội dung.
- **Cập nhật:**
  - Nội dung.
- **Sửa lỗi:**
  - Nội dung.
- **Xoá:**
  - Nội dung.

## 🖊️ Ví dụ
### Nguyễn Văn A - 12/04/2026 - feature/quan-ly
- **Thêm mới:**
  - Tạo Procedure `SP_THEM_DH` để thêm đơn hàng mới.
### Nguyễn Văn B - 13/04/2026 - feature/dat-hang
- **Cập nhật:**
  - Chỉnh sửa logic tính thuế trong Function `FN_TINH_PHIVANCHUYEN`.
- **Sửa lỗi:**
  - Fix lỗi null pointer khi lấy dữ liệu từ bảng `KHACHHANG`.

## ⚠️ Lưu ý
- Phải kiểm tra trùng lặp với các Procedure hiện có trên nhánh `dev`.
- **[Thêm mới]** và **[Xoá]** -> **BẮT BUỘC** phải ghi changelog
- **[Cập nhật]** và **[Sửa lỗi]** -> Có thể không cần ghi vì không ảnh hưởng trực tiếp tới các nhánh khác, module khác. Muốn xem lịch sử thay đổi vẫn có thể xem qua git log.

## 🤔 Tại sao cần việc này?
1. **Theo dõi tiến độ:** Biết được ai đã làm gì và khi nào.
2. **Tránh xung đột mã nguồn:** Phát hiện sớm và tránh các tình huống xung đột *VD: hai nhóm làm việc độc lập cùng tạo trùng một **Procedure** hoặc **Function***.
3. **Dễ dàng Debug:** Khi có lỗi phát sinh, có thể nhanh chóng truy vết lại các thay đổi gần nhất để tìm nguyên nhân *mà không cần tìm lại nhiều dòng git commit / chưa có tổng hợp git commit nếu chưa merge code giữa các nhóm*.

## ⚙️ Giải quyết xung đột
Trong trường hợp xảy ra xung đột (ví dụ: trùng tên Procedure hoặc thay đổi đè lên nhau).
- **Người liên hệ giải quyết:** Nông Văn Quang.


