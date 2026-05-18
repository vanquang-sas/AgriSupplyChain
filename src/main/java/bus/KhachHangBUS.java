package bus;

import dao.KhachHangDAO;
import dao.TaiKhoanDAO;
import dto.KhachHangDTO;
import util.HashPass;

import java.sql.SQLException;
import java.util.List;

public class KhachHangBUS {
    private KhachHangDAO khDAO;
    private TaiKhoanDAO tkDAO;

    public KhachHangBUS() {
        khDAO = new KhachHangDAO();
        tkDAO = new TaiKhoanDAO();
    }

    // ================= LOGIC DÀNH CHO TRANG ĐĂNG KÝ =================

    public String dangKyKhachHang(String username, String password, String confirmPassword, 
                                  String tenKH, String sdt, String email, String diaChi) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty() || 
            tenKH == null || tenKH.trim().isEmpty() || 
            sdt == null || sdt.trim().isEmpty()) {
            return "Vui lòng nhập các thông tin bắt buộc!";
        }

        if (!password.equals(confirmPassword)) {
            return "Xác nhận mật khẩu không khớp!";
        }

        if (!sdt.matches("^[0-9]{10,12}$")) {
            return "Số điện thoại không hợp lệ (phải từ 10-12 số)!";
        }

        // ĐỒNG BỘ: Dùng class HashPass chung
        String hashedPassword = HashPass.hashPassword(password);

        // Gọi DAO đăng ký tài khoản (Role Khách Hàng mặc định = 2)
        String result = tkDAO.dangKyTaiKhoan(username, hashedPassword, 2, tenKH, diaChi, sdt, email, "Thường");
        
        if (result.equals("SUCCESS")) {
            return "Đăng ký tài khoản thành công!";
        } else if (result.equals("DUPLICATE")) {
            return "Tên đăng nhập đã tồn tại!";
        } else {
            return "Lỗi hệ thống! Vui lòng thử lại sau.";
        }
    }

    // ================= LOGIC DÀNH CHO PANEL QUẢN LÝ (CRUD) =================

    public boolean capNhatHoSoKH(String maKH, String tenKH, String diaChi, String sdt, String email) {
        return khDAO.capNhatHoSoKH(maKH, tenKH, diaChi, sdt, email);
    }

    public List<KhachHangDTO> getAll() {
        return khDAO.getAll();
    }

    public List<KhachHangDTO> timKiem(String keyword) {
        return khDAO.timKiem(keyword);
    }

    public KhachHangDTO getById(String maKH) {
        return khDAO.getById(maKH);
    }
    
    public boolean delete(String maKH) throws SQLException {
        return khDAO.delete(maKH);
    }

    public void them(KhachHangDTO kh, String password) throws SQLException, IllegalArgumentException {
        validate(kh);
        if (khDAO.isUsernameExists(kh.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại!");
        }
        
        // KHI ADMIN THÊM KHÁCH HÀNG -> CŨNG PHẢI HASH MẬT KHẨU
        String hashedPass = HashPass.hashPassword(password);
        khDAO.them(kh, hashedPass);
    }

    public void capNhat(KhachHangDTO kh) throws SQLException, IllegalArgumentException {
        validate(kh);
        khDAO.capNhat(kh);
    }

    public void khoaTaiKhoan(String username) throws SQLException {
        khDAO.khoaTaiKhoan(username);
    }

    public void moKhoaTaiKhoan(String username) throws SQLException {
        khDAO.moKhoaTaiKhoan(username);
    }

    private void validate(KhachHangDTO kh) {
        if (kh.getTenKH() == null || kh.getTenKH().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống!");
        }
        if (kh.getSdt() == null || !kh.getSdt().matches("\\d{10,12}")) {
            throw new IllegalArgumentException("Số điện thoại chỉ chứa số và dài 10-12 ký tự!");
        }
        if (kh.getEmail() != null && !kh.getEmail().trim().isEmpty() && !kh.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email không đúng định dạng!");
        }
    }

    public dto.KhachHangDTO getByUsername(String username) {
        return khDAO.getByUsername(username);
    }
}