package bus;

import dao.ThongKeDAO;
import dto.ThongKeDTO;
import java.util.List;
import java.util.Date;

public class ThongKeBUS {
    private ThongKeDAO thongKeDAO = new ThongKeDAO();

    // ============================= THỐNG KÊ SẢN PHẨM =============================
    public List<ThongKeDTO.DoanhThu> getDoanhThuTheoNam(int nam) {
        return thongKeDAO.getDoanhThuTheoNam(nam);
    }

    public List<ThongKeDTO.TopSanPham> getThongKeSanPham(int limit, String type, Date from, Date to) {
        return thongKeDAO.getThongKeSanPham(limit, type, from, to);
    }

    public List<ThongKeDTO.TrangThai> getTyLeTrangThai() {
        return thongKeDAO.getTyLeTrangThai();
    }

    // ============================= TRẠNG THÁI ĐƠN HÀNG =============================
    public List<ThongKeDTO.TrangThai> getThongKeTrangThai(java.util.Date from, java.util.Date to) {
        return thongKeDAO.getThongKeTrangThai(from, to);
    }
}