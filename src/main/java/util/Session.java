package util;

import dto.TaiKhoanDTO;

public class Session {
    // Lưu trữ tài khoản đang đăng nhập trong suốt phiên làm việc
    public static TaiKhoanDTO currentUser = null;

    /**
     * Kiểm tra xem đã có người dùng đăng nhập chưa
     */
    public static boolean isLogged() {
        return currentUser != null;
    }

    /**
     * Xóa thông tin khi đăng xuất
     */
    public static void clear() {
        currentUser = null;
    }

    /**
     * Kiểm tra quyền của người dùng hiện tại
     * Giả định: 1 = Admin, 2 = ThuKho, 3 = BanHang...
     */
    public static boolean hasRole(int roleLevel) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.getLoaiTK() == roleLevel;
    }
}
