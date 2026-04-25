package bus;

import dao.KhachHangDAO;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class KhachHangBUS {
    private KhachHangDAO khDAO = new KhachHangDAO();

    // Thuật toán băm mật khẩu SHA-256
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
        // Validate dữ liệu
        if (username.trim().isEmpty() || password.isEmpty() || ten.trim().isEmpty()) {
            return "Vui lòng nhập đầy đủ các thông tin bắt buộc!";
        }
        if (!password.equals(confirmPassword)) {
            return "Mật khẩu xác nhận không khớp!";
        }
        if (!sdt.matches("^[0-9]{10,11}$")) {
            return "Số điện thoại không hợp lệ!";
        }
        
        // Băm mật khẩu trước khi đẩy xuống DAO
        String hashedPassword = hashPassword(password);
        
        // 2 là LoaiTK của Khách hàng, 'Thường' là LoaiKH mặc định
        boolean isSuccess = khDAO.dangKyTaiKhoan(username, hashedPassword, 2, ten, diaChi, sdt, email, "Thường");
        
        return isSuccess ? "Thành công" : "Đăng ký thất bại. Tên đăng nhập có thể đã tồn tại!";
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
}