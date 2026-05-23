package bus;

import dao.NhanVienDAO;
import dto.NhanVienDTO;
import util.HashPass;

import java.sql.SQLException;
import java.util.List;

public class NhanVienBUS {
    private NhanVienDAO nvDAO;

    public NhanVienBUS() {
        nvDAO = new NhanVienDAO();
    }

    public List<NhanVienDTO> getAll() {
        return nvDAO.getAll();
    }

    public List<NhanVienDTO> timKiem(String keyword) {
        return nvDAO.timKiem(keyword);
    }

    public NhanVienDTO getById(String maNV) {
        return nvDAO.getById(maNV);
    }

    public void them(NhanVienDTO nv, String password) throws SQLException, IllegalArgumentException {
        validate(nv);
        if (nvDAO.isUsernameExists(nv.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại!");
        }
        
        // KHI ADMIN THÊM NHÂN VIÊN -> MẬT KHẨU PHẢI ĐƯỢC HASH
        String hashedPass = HashPass.hashPassword(password);
        nvDAO.them(nv, hashedPass);
    }

    public void capNhat(NhanVienDTO nv) throws SQLException, IllegalArgumentException {
        validate(nv);
        nvDAO.capNhat(nv);
    }

    public void khoaTaiKhoan(String username) throws SQLException {
        nvDAO.khoaTaiKhoan(username);
    }

    public void moKhoaTaiKhoan(String username) throws SQLException {
        nvDAO.moKhoaTaiKhoan(username);
    }

    private void validate(NhanVienDTO nv) {
        if (nv.getTenNV() == null || nv.getTenNV().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhân viên không được để trống!");
        }
        if (nv.getSdt() == null || !nv.getSdt().matches("\\d{10,12}")) {
            throw new IllegalArgumentException("Số điện thoại chỉ chứa số và dài 10-12 ký tự!");
        }
        if (nv.getChucVu() == null || nv.getChucVu().trim().isEmpty()) {
            throw new IllegalArgumentException("Chức vụ không được để trống!");
        }
        if (nv.getLuong() < 0) {
            throw new IllegalArgumentException("Mức lương không hợp lệ!");
        }
    }

    public dto.NhanVienDTO getByUsername(String username) {
        return nvDAO.getByUsername(username);
    }

    public boolean delete(String maNV) throws SQLException, IllegalArgumentException {
        if (nvDAO.coHoatDong(maNV)) {
            throw new IllegalArgumentException("Nhân viên đã phát sinh hoạt động (nhập lô hàng, giao đơn hàng hoặc xuất kho), không được phép xóa! Hãy chọn vô hiệu hóa tài khoản của họ.");
        }
        return nvDAO.delete(maNV);
    }
}