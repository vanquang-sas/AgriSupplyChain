package util;
import dto.TaiKhoanDTO;

public class Session {
    public static TaiKhoanDTO currentUser = null;
    public static String tenNguoiDung = "Người dùng"; // Lưu tên (TenNV hoặc TenKH)
    public static String chucVu = "Khách hàng";            // Lưu chức vụ hiển thị

    public static boolean isLogged() {
        return currentUser != null;
    }

    public static void clear() {
        currentUser = null;
        tenNguoiDung = "Người dùng";
        chucVu = "Khách hàng";
    }

    public static boolean hasRole(int roleLevel) {
        if (currentUser == null) return false;
        return currentUser.getLoaiTK() == roleLevel;
    }
}