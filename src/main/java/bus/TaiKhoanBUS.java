package bus;

import dao.TaiKhoanDAO;
import dto.TaiKhoanDTO;
import util.Session;

public class TaiKhoanBUS {
    private TaiKhoanDAO taiKhoanDAO;

    public TaiKhoanBUS() {
        this.taiKhoanDAO = new TaiKhoanDAO();
    }

    /**
     * Xử lý nghiệp vụ đăng nhập
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return Thông báo lỗi, hoặc chuỗi "Thành công" nếu đăng nhập hợp lệ
     */
    public String login(String username, String password) {
        // Kiểm tra trống
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return "Tài khoản và mật khẩu không được để trống!";
        }

        // Gọi DAO lấy dữ liệu
        TaiKhoanDTO dto = taiKhoanDAO.checkLogin(username, password);

        // Kiểm tra đúng/sai mật khẩu
        if (dto == null) {
            return "Sai tên đăng nhập hoặc mật khẩu!";
        }

        // Kiểm tra trạng thái tài khoản (Giả định 0 = Bị khóa, 1 = Hoạt động)
        if (dto.getTrangThaiTK() == 0) {
            return "Tài khoản của bạn đã bị khóa!";
        }

        // Nếu hợp lệ hết -> Lưu phiên đăng nhập
        Session.currentUser = dto;
        return "Thành công";
    }
}
