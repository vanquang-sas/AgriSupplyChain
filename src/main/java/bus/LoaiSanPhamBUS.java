package bus;

import dao.LoaiSanPhamDAO;
import dto.LoaiSanPhamDTO;

public class LoaiSanPhamBUS {
    private LoaiSanPhamDAO dao = new LoaiSanPhamDAO();

    public String addLoaiSanPham(LoaiSanPhamDTO dto) {
        // Validation logic
        if (dto.getTenLSP() == null || dto.getTenLSP().trim().isEmpty()) {
            return "Lỗi: Tên loại sản phẩm không được để trống!";
        }
        
        // Gọi xuống DAO
        boolean isSuccess = dao.insert(dto);
        if (isSuccess) {
            return "Thành công: Đã thêm loại sản phẩm mới!";
        } else {
            return "Lỗi: Lỗi hệ thống khi lưu vào cơ sở dữ liệu.";
        }
    }
}
