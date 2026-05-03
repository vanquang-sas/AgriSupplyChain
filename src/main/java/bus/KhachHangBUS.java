package bus;

import dao.KhachHangDAO;
import dto.KhachHangDTO;

import dao.TaiKhoanDAO; // Thêm Import TaiKhoanDAO
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.sql.SQLException;
import java.util.List;

public class KhachHangBUS {
    private KhachHangDAO khDAO = new KhachHangDAO();
    private TaiKhoanDAO tkDAO = new TaiKhoanDAO(); // Khởi tạo TaiKhoanDAO

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Lỗi thuật toán mã hoá!", e);
        }
    }

    public String dangKyKhachHang(String username, String password, String confirmPassword, String ten, String sdt, String email, String diaChi) {
        // Validate dữ liệu cơ bản
        if (username.trim().isEmpty() || password.isEmpty() || ten.trim().isEmpty()) {
            return "Vui lòng nhập đầy đủ các thông tin bắt buộc!";
        }
        if (!password.equals(confirmPassword)) {
            return "Mật khẩu xác nhận không khớp!";
        }
        if (!sdt.matches("^[0-9]{10,11}$")) {
            return "Số điện thoại không hợp lệ!";
        }
        
        String hashedPassword = hashPassword(password);

        String result = tkDAO.dangKyTaiKhoan(username, hashedPassword, 2, ten, diaChi, sdt, email, "Thường");
        
        // Phân nhánh thông báo cho người dùng
        if ("SUCCESS".equals(result)) {
            return "Đăng ký tài khoản thành công!";
        } else if ("DUPLICATE".equals(result)) {
            return "Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác.";
        } else {
            return "Hệ thống đang gặp sự cố! Vui lòng thử lại sau.";
        }
    }

    public String capNhatHoSo(String maKH, String ten, String sdt, String email, String diaChi) {
        if (ten.trim().isEmpty()) {
            return "Tên không được để trống!";
        }
        if (!sdt.matches("^[0-9]{10,11}$")) {
            return "Số điện thoại không hợp lệ!";
        }
        
        boolean isSuccess = khDAO.capNhatHoSoKH(maKH, ten, diaChi, sdt, email);
        return isSuccess ? "Cập nhật thành công!" : "Lỗi cập nhật hồ sơ!";
    }

    //-----------------------------------------------------------MERGE-----------------------------------------------------------
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