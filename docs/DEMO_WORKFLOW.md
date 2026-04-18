# Workflow
<span style="color:red; background-color:yellow">
    Đây là bản demo ý tưởng ban đầu, chưa phải logic thực tế sẽ cài đặt
</span>

---

**DEMO 1**: Thêm một sản phẩm mới
Người dùng (Quản lý) mở màn hình Java Swing, nhập thông tin sản phẩm mới gồm: Mã SP (SP001), Tên SP (Cà rốt loại 1), Giá Mua (15000), Giá Bán (20000). Bấm nút "Lưu".

`View` -> `DTO` -> `BLL` -> `DAO` -> Database 

1. Màn hình (View) lấy text từ các ô nhập liệu, đóng gói tất cả vào 1 đối tượng DTO.
2. View truyền đối tượng DTO này xuống BLL, BLL kiểm tra logic rồi đẩy tiếp xuống DAL.
3. DAL mở đối tượng DTO ra, lấy dữ liệu và nhét vào câu lệnh INSERT để lưu xuống Oracle.

---

**DEMO 2**: Khi người dùng mua hàng

1. User bấm nút trên giao diện.
2. Nút bấm nằm ở `view/DonHangView.java`. Khối View này không tự xử lý mà chuyển sự kiện cho Controller.
3. `controller/DonHangController.java` bắt được sự kiện. Nó gom dữ liệu người dùng nhập lại thành một đối tượng DonHangDTO.
4. Controller gọi xuống Tầng Nghiệp vụ: `donHangBLL.xuLyDatHang(donHangDTO)`. *(Lưu ý: Tòa bộ khối bll, dal và dto lúc này đóng vai trò là khối MODEL khổng lồ trong mô hình MVC).*
5. `bll/DonHangBLL.java` kiểm tra hợp lệ, sau đó gọi `donHangDAL.insert(donHangDTO)`.
6. `dal/DonHangDAL.java` thực thi câu lệnh `INSERT` vào Oracle và trả kết quả `true/false` ngược lên trên.