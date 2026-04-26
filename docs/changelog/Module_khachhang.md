### Từ Lê Việt Hoàng - 27/04/2026 - feature/lich-su-don-hang
- **Thêm mới:**
  - Procedure `SP_LAY_DS_DONHANG_BY_KH` (database/05_Procedures.sql): Lấy danh sách đơn hàng của khách hàng kèm chi tiết sản phẩm gom bằng LISTAGG (ví dụ: "2 Xoài, 1 Chuối"). Kết hợp 3 bảng DONHANG, CHITIETDONHANG, SANPHAM.
  - Procedure `SP_XOA_DH` (database/05_Procedures.sql): Thực hiện soft delete đơn hàng (UPDATE TrangThaiDH thành 'Đã huỷ' thay vì DELETE). Kiểm tra điều kiện hủy: chỉ hủy nếu TrangThaiDH = "Đã đặt" HOẶC "Chờ xử lý".
  - Class `DonHangDAO` (src/main/java/dao/DonHangDAO.java): Lớp Data Access Object với 2 hàm gọi Procedure qua CallableStatement (không dùng SQL thô).
  - Class `DonHangBUS` (src/main/java/bus/DonHangBUS.java): Lớp Business Logic Layer với validation logic kiểm tra điều kiện hủy đơn.
- **Cập nhật:**
  - `DonHangDTO` (src/main/java/dto/DonHangDTO.java): Thêm 2 field mới `trangThaiTT` (int) và `danhSachSP` (String) để hỗ trợ lịch sử đơn hàng. Thêm Getter/Setter và constructor đầy đủ.
