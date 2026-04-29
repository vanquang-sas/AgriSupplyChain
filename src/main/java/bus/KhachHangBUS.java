package bus;

import dao.KhachHangDAO;
import dao.TaiKhoanDAO; // Thêm Import TaiKhoanDAO
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
}