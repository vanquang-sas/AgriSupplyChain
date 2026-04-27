### Khách hàng Module Developer - 28/04/2026 - feature/login-auth
- **Thêm mới:**
  - `src/main/java/util/Session.java`: Tạo lớp quản lý trạng thái, phân quyền và phiên làm việc của người dùng hiện tại.
  - `src/main/java/dao/TaiKhoanDAO.java`: Thêm lớp Data Access Object thực hiện các truy vấn cơ sở dữ liệu cho bảng `TAIKHOAN`.
  - `src/main/java/bus/TaiKhoanBUS.java`: Thêm lớp Business Logic Layer xử lý nghiệp vụ xác thực tài khoản và kiểm tra trạng thái hoạt động.
  - `src/main/java/gui/LoginGUI.java`: Xây dựng giao diện đăng nhập cho người dùng.
  - `src/main/java/gui/MainGUI.java`: Xây dựng giao diện hệ thống chính, tích hợp phân quyền hiển thị chức năng theo Role.
- **Cập nhật:**
  - `src/main/java/Main.java`: Thay đổi điểm khởi chạy (entry point) của ứng dụng để hiển thị `LoginGUI` đầu tiên.