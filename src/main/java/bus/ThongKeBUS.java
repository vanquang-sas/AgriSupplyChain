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

    public List<ThongKeDTO.SanPham> getThongKeSanPham(Date from, Date to) {
        return thongKeDAO.getThongKeSanPham(from, to);
    }

    public List<ThongKeDTO.LichSuGia> getLichSuGiaTheoSanPham(String maSP, Date tuNgay, Date denNgay) {
        return thongKeDAO.getLichSuGiaTheoSanPham(maSP, tuNgay, denNgay);
    }

    public List<ThongKeDTO.TrangThai> getTyLeTrangThai() {
        return thongKeDAO.getTyLeTrangThai();
    }

    // ============================= TRẠNG THÁI ĐƠN HÀNG =============================
    public List<ThongKeDTO.TrangThai> getThongKeTrangThaiDonHang(java.util.Date from, java.util.Date to) {
        return thongKeDAO.getThongKeTrangThaiDonHang(from, to);
    }

    public List<ThongKeDTO.TrangThai> getThongKeTrangThaiLoHang(java.util.Date tuNgay, java.util.Date denNgay) {
        return thongKeDAO.getThongKeTrangThaiLoHang(tuNgay, denNgay);
    }

    // ============================= THỐNG KÊ TÀI CHÍNH =============================
    public List<ThongKeDTO.TaiChinh> getThongKeTaiChinh(String type, int period) {
        return thongKeDAO.getThongKeTaiChinh(type, period);
    }

    // ============================= THỐNG KÊ NHÂN VIÊN =============================
    public List<ThongKeDTO.NhanVienThongKe> getDanhSachNhanVienThongKe(String chucVu, int months) {
        return thongKeDAO.getDanhSachNhanVienThongKe(chucVu, months);
    }
    public List<ThongKeDTO.HieuSuatChiTiet> getHieuSuatNhanVien(String maNV, int months) {
        return thongKeDAO.getHieuSuatNhanVien(maNV, months);
    }

    // ============================= THỐNG KÊ CÔNG NỢ KHÁCH HÀNG =============================
    public List<ThongKeDTO.CongNo> getThongKeCongNo() {
        return thongKeDAO.getThongKeCongNo();
    }

    public List<ThongKeDTO.CongNo> getThongKeCongNo(int months) {
        return thongKeDAO.getThongKeCongNo(months);
    }
}