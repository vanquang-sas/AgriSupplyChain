# Quy trình hoạt động (Workflow)

Để đảm bảo code nhất quán, tất cả các thành viên phải tuân thủ luồng dữ liệu (Data Flow) theo hướng từ trên xuống dưới: **GUI > BLL > DAO > Database** *(Không được phép gọi vượt cấp)*.

---

## DEMO: Thêm một "Loại Sản Phẩm" mới

Giả sử người dùng đang ở màn hình `LoaiSanPhamGUI`, nhập tên loại sản phẩm là "Rau củ hữu cơ" và bấm nút **"Thêm"**. Dưới đây là những gì xảy ra bên dưới hệ thống:

### Bước 1: Tại tầng Giao diện (GUI Layer)
- **File:** `gui/LoaiSanPhamGUI.java`
- **Hành động:** 
    1. Sự kiện `ActionListener` của nút "Thêm" được kích hoạt.
    2. Giao diện lấy dữ liệu text từ các ô nhập liệu (`JTextField`).
    3. Đóng gói các dữ liệu này vào một đối tượng **DTO**: `LoaiSanPhamDTO dto = new LoaiSanPhamDTO(..., ...);`.
    4. Truyền đối tượng `dto` này xuống tầng BLL: `String message = bll.addLoaiSanPham(dto);`.
    5. Nhận kết quả từ BLL trả về và hiển thị thông báo lên màn hình (`JOptionPane`).

### Bước 2: Tại tầng Nghiệp vụ (BLL Layer)
- **File:** `bll/LoaiSanPhamBLL.java`
- **Hành động:**
    1. Nhận đối tượng `dto` từ GUI.
    2. **Kiểm tra (Validation):** Kiểm tra xem tên loại sản phẩm có bị bỏ trống hay không, độ dài có hợp lệ không.
    3. Nếu dữ liệu LỖI ➔ Dừng lại ngay và trả về chuỗi thông báo lỗi cho GUI.
    4. Nếu dữ liệu CHUẨN ➔ Gọi hàm thao tác dữ liệu từ tầng DAO: `boolean isSuccess = dao.insert(dto);`.
    5. Đọc kết quả `true/false` từ DAO và chuyển đổi thành câu thông báo (VD: "Thêm thành công!") để trả ngược lại cho GUI.

### Bước 3: Tại tầng Truy cập Dữ liệu (DAO Layer)
- **File:** `dao/LoaiSanPhamDAO.java` (Implement từ `IBaseDAO`)
- **Hành động:**
    1. Nhận đối tượng `dto` đã được "làm sạch" từ BLL.
    2. Mở kết nối tới Oracle thông qua `DBConnection.getConnection()`.
    3. Khởi tạo câu lệnh SQL (`INSERT INTO LOAISANPHAM...`).
    4. Tách các thuộc tính từ đối tượng `dto` (`dto.getTenLSP()`) và nạp vào câu lệnh SQL thông qua `PreparedStatement`.
    5. Thực thi (`executeUpdate()`) và trả về `true` (nếu thêm thành công) hoặc `false` (nếu có lỗi SQL).

### Bước 4: Tại Cơ sở dữ liệu (Database)
- **Hành động:** Oracle nhận lệnh INSERT. Trigger sinh mã tự động (VD: `TRG_LSP_ID`) sẽ chạy để tạo ID mới. Dữ liệu được lưu vào bảng `LOAISANPHAM`.

---
**⚠️ QUY TẮC BẮT BUỘC KHI CODE:**
    - **Tuyệt đối KHÔNG** viết câu lệnh SQL (SELECT, INSERT...) ở tầng GUI hoặc BLL. SQL chỉ nằm ở tầng DAO.
    - Mọi logic tính toán, kiểm tra đúng/sai phải nằm ở BLL.