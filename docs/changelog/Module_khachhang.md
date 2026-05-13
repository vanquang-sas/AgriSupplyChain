### Khách hàng Module Developer - 28/04/2026 - feature/login-auth
- **Thêm mới:**
  - `src/main/java/util/Session.java`: Tạo lớp quản lý trạng thái, phân quyền và phiên làm việc của người dùng hiện tại.
  - `src/main/java/dao/TaiKhoanDAO.java`: Thêm lớp Data Access Object thực hiện các truy vấn cơ sở dữ liệu cho bảng `TAIKHOAN`.
  - `src/main/java/bus/TaiKhoanBUS.java`: Thêm lớp Business Logic Layer xử lý nghiệp vụ xác thực tài khoản và kiểm tra trạng thái hoạt động.
  - `src/main/java/gui/LoginGUI.java`: Xây dựng giao diện đăng nhập cho người dùng.
  - `src/main/java/gui/MainGUI.java`: Xây dựng giao diện hệ thống chính, tích hợp phân quyền hiển thị chức năng theo Role.
- **Cập nhật:**
  - `src/main/java/Main.java`: Thay đổi điểm khởi chạy (entry point) của ứng dụng để hiển thị `LoginGUI` đầu tiên.

### Khách hàng Module Developer - 03/05/2026 - feature/login-auth
- **Thêm mới:**
  - `src/main/resources/images/Background_DangKy.jpg`: Thêm hình nền dùng chung cho GUI Đăng nhập và Đăng ký.
  - `src/main/java/test/TestMainLogin.java`: Tạo file test riêng cho chức năng Đăng nhập (chuyển code từ `Main.java` sang đây).
- **Cập nhật:**
  - `src/main/java/gui/LoginGUI.java`: Xây dựng lại giao diện Đăng nhập đồng bộ với `DangKyGUI` (layout 60-40, background, logo, AppColor, custom alert dialog).
  - `src/main/java/Main.java`: Xóa code test chức năng, giữ file trống (code test đã chuyển sang `test/TestMainLogin.java`).
- **Xóa:**
  - `src/main/java/test/TestMainLSP.java`: Xóa file test cũ không còn sử dụng, thay thế bằng `TestMainLogin.java`.

### Từ Lê Việt Hoàng - 27/04/2026 - feature/lich-su-don-hang
- **Thêm mới:**
  - Procedure `SP_LAY_DS_DONHANG_BY_KH` (database/05_Procedures.sql): Lấy danh sách đơn hàng của khách hàng kèm chi tiết sản phẩm gom bằng LISTAGG (ví dụ: "2 Xoài, 1 Chuối"). Kết hợp 3 bảng DONHANG, CHITIETDONHANG, SANPHAM.
  - Procedure `SP_XOA_DH` (database/05_Procedures.sql): Thực hiện soft delete đơn hàng (UPDATE TrangThaiDH thành 'Đã huỷ' thay vì DELETE). Kiểm tra điều kiện hủy: chỉ hủy nếu TrangThaiDH = "Đã đặt" HOẶC "Chờ xử lý".
  - Class `DonHangDAO` (src/main/java/dao/DonHangDAO.java): Lớp Data Access Object với 2 hàm gọi Procedure qua CallableStatement (không dùng SQL thô).
  - Class `DonHangBUS` (src/main/java/bus/DonHangBUS.java): Lớp Business Logic Layer với validation logic kiểm tra điều kiện hủy đơn.
- **Cập nhật:**
  - `DonHangDTO` (src/main/java/dto/DonHangDTO.java): Thêm 2 field mới `trangThaiTT` (int) và `danhSachSP` (String) để hỗ trợ lịch sử đơn hàng. Thêm Getter/Setter và constructor đầy đủ.

### Từ Lê Việt Hoàng - 02/05/2026 - feature/lich-su-don-hang
- **Thêm mới:**
  - Hoàn thiện cơ bản giao diện GUI cho phần lịch sử đơn hàng (theo mẫu trên canva)
- **Sửa lỗi:**
  - Xử lý lỗi tràn chuỗi LISTAGG trong procedure SP_LAY_DS_DONHANG_BY_KH.
- **Xoá:**
  - Loại bỏ Procedure SP_XOA_DH dư thừa (sử dụng SP_HUY_DH có sẵn)

### Khách hàng Module Developer - 08/05/2026 - feature/quen-mat-khau
- **Thêm mới:**
  - `src/main/java/gui/QuenMKPanel.java`: Xây dựng giao diện Quên mật khẩu 3 bước (Xác thực thông tin → Nhập OTP giả lập → Đặt mật khẩu mới) sử dụng CardLayout.
- **Cập nhật:**
  - `src/main/java/dao/TaiKhoanDAO.java`: Thêm method `xacThucThongTinQuenMK()` (JOIN bảng TAIKHOAN, NHANVIEN, KHACHHANG để xác thực danh tính) và method `doiMatKhau()` (UPDATE mật khẩu đã hash).
  - `src/main/java/bus/TaiKhoanBUS.java`: Thêm method `xacThucQuenMatKhau()` (validate input + gọi DAO) và method `datLaiMatKhau()` (hash mật khẩu mới bằng SHA-256 trước khi lưu).
  - `src/main/java/util/DBConnection.java`: Sửa URL kết nối từ `orclpdb` sang `orcl` cho phù hợp với Oracle Non-CDB trên môi trường phát triển.
