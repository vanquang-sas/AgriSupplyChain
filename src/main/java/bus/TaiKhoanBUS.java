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
}