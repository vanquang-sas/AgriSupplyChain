package bus;

import dao.KhachHangDAO;
import dto.KhachHangDTO;

import java.sql.SQLException;
import java.util.List;

public class KhachHangBUS {
    private final KhachHangDAO dao = new KhachHangDAO();

    public List<KhachHangDTO> getAll() {
        return dao.getAll();
    }

    public List<KhachHangDTO> timKiem(String keyword) {
        return dao.timKiem(keyword);
    }

    public KhachHangDTO getById(String maKH) {
        return dao.getById(maKH);
    }

    // Thêm mới: validate -> kiểm tra username trùng -> gọi DAO
    public void them(KhachHangDTO kh, String password) throws IllegalArgumentException, SQLException {
        validate(kh);

        if (kh.getUsername() == null || kh.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống!");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống!");
        }
        if (dao.isUsernameExists(kh.getUsername())) {
            throw new IllegalArgumentException("Username '" + kh.getUsername() + "' đã tồn tại!");
        }

        dao.them(kh, password);
    }

    // Cập nhật: validate -> gọi DAO
    public void capNhat(KhachHangDTO kh) throws IllegalArgumentException, SQLException {
        validate(kh);
        dao.capNhat(kh);
    }

    // Khóa tài khoản
    public void khoaTaiKhoan(String username) throws SQLException {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username không hợp lệ!");
        dao.khoaTaiKhoan(username);
    }

    // Mở khóa tài khoản
    public void moKhoaTaiKhoan(String username) throws SQLException {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username không hợp lệ!");
        dao.moKhoaTaiKhoan(username);
    }

    // ===================== VALIDATION =====================
    private void validate(KhachHangDTO kh) throws IllegalArgumentException {
        if (kh.getTenKH() == null || kh.getTenKH().trim().isEmpty())
            throw new IllegalArgumentException("Tên khách hàng không được để trống!");

        if (kh.getSdt() == null || kh.getSdt().trim().isEmpty())
            throw new IllegalArgumentException("Số điện thoại không được để trống!");
        if (!kh.getSdt().matches("\\d{10,12}"))
            throw new IllegalArgumentException("Số điện thoại chỉ gồm 10–12 chữ số!");

        if (kh.getEmail() != null && !kh.getEmail().trim().isEmpty()) {
            if (!kh.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$"))
                throw new IllegalArgumentException("Email không đúng định dạng!");
        }

        if (kh.getDiaChi() == null || kh.getDiaChi().trim().isEmpty())
            throw new IllegalArgumentException("Địa chỉ không được để trống!");

        if (kh.getLoaiKH() == null || kh.getLoaiKH().trim().isEmpty())
            throw new IllegalArgumentException("Loại khách hàng không được để trống!");
    }
}