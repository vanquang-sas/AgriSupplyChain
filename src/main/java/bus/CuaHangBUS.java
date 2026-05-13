package bus;

import dto.SanPhamDTO;
import java.util.List;
import dao.CuaHangDAO;

public class CuaHangBUS {

    private CuaHangDAO dao = new CuaHangDAO();

    // lấy tất cả theo khách hàng
    public List<SanPhamDTO> getAllSanPham(String maKH) {
        return dao.getAllSanPham(maKH);
    }

    // tìm kiếm
    public List<SanPhamDTO> timKiem(String keyword,String maKH) {
        return dao.timKiem(keyword,maKH);
    }

    // lọc loại
    public List<SanPhamDTO> locTheoLoai(String loai, String maKH) {
        return dao.locTheoLoai(loai, maKH);
    }

    // chi tiết sản phẩm
    public SanPhamDTO getById(String maSP, String maKH) {

        return dao.getById(maSP, maKH);
    }
}