package bus;

import dao.TaiKhoanDAO;
import dto.TaiKhoanDTO;
import util.HashPass;
import util.Session;

public class TaiKhoanBUS {
    private TaiKhoanDAO taiKhoanDAO;

    public TaiKhoanBUS() {
        taiKhoanDAO = new TaiKhoanDAO();
    }

    public String login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return "Tài khoản và mật khẩu không được để trống!";
        }

        // ĐỒNG BỘ: Mã hóa mật khẩu trước khi kiểm tra với database
        String hashedPassword = HashPass.hashPassword(password);

        // Chuyền mật khẩu ĐÃ HASH xuống DAO để kiểm tra
        TaiKhoanDTO taiKhoan = taiKhoanDAO.checkLogin(username, hashedPassword);

        if (taiKhoan == null) {
            return "Sai tài khoản hoặc mật khẩu!";
        }

        // Kiểm tra trạng thái tài khoản (1 = Hoạt động, 0 = Bị khóa)
        if (taiKhoan.getTrangThaiTK() == 0) {
            return "Tài khoản của bạn đã bị khóa!";
        }

        // Lưu thông tin người dùng vào Session toàn cục
        Session.currentUser = taiKhoan;
        return "SUCCESS";
    }

    public String xacThucQuenMatKhau(String username, String emailOrPhone) {
        if (username == null || username.trim().isEmpty() || emailOrPhone == null || emailOrPhone.trim().isEmpty()) {
            return "Vui lòng nhập đầy đủ tên đăng nhập và email/số điện thoại!";
        }

        boolean isValid = taiKhoanDAO.xacThucThongTinQuenMK(username, emailOrPhone);
        if (isValid) {
            return "SUCCESS";
        }
        return "Thông tin tài khoản không chính xác!";
    }

    public String datLaiMatKhau(String username, String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return "Mật khẩu mới không được để trống!";
        }
        if (newPassword.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự!";
        }

        String hashedNewPassword = HashPass.hashPassword(newPassword);
        boolean isSuccess = taiKhoanDAO.doiMatKhau(username, hashedNewPassword);
        if (isSuccess) {
            return "SUCCESS";
        }
        return "Lỗi hệ thống khi cập nhật mật khẩu!";
    }
}