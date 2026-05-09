package bus;

import dto.SanPhamDTO;
import java.util.List;
import dao.CuaHangDAO;

public class CuaHangBUS {

    private CuaHangDAO dao = new CuaHangDAO();

    // lấy tất cả
    public List<SanPhamDTO> getAllSanPham() {
        return dao.getAllSanPham();
    }

    // tìm kiếm
    public List<SanPhamDTO> timKiem(String keyword) {
        return dao.timKiem(keyword);
    }

    // lọc loại
    public List<SanPhamDTO> locTheoLoai(String loai) {
        return dao.locTheoLoai(loai);
    }

    // Lấy danh sách sản phẩm theo id
    public SanPhamDTO getById(String maSP) {

        return dao.getById(maSP);
    }
}