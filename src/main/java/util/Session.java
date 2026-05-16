package util;

import dto.GioHangDTO;
import dto.TaiKhoanDTO;

import java.util.ArrayList;
import java.util.List;

public class Session {

    // ── Thông tin đăng nhập ──────────────────────────────────────────
    public static TaiKhoanDTO currentUser    = null;
    public static String      tenNguoiDung   = "Người dùng";
    public static String      chucVu         = "Khách hàng";

    // ── Cache giỏ hàng (chỉ có ý nghĩa khi LoaiTK == 2 – Khách hàng) ─
    /** Danh sách sản phẩm trong giỏ, nạp từ DB sau khi đăng nhập. */
    public static List<GioHangDTO> cartCache = new ArrayList<>();

    // ─────────────────────────────────────────────────────────────────
    // Kiểm tra đăng nhập
    // ─────────────────────────────────────────────────────────────────
    public static boolean isLogged() {
        return currentUser != null;
    }

    // ─────────────────────────────────────────────────────────────────
    // Đăng xuất – xoá toàn bộ trạng thái phiên
    // ─────────────────────────────────────────────────────────────────
    public static void clear() {
        currentUser   = null;
        tenNguoiDung  = "Người dùng";
        chucVu        = "Khách hàng";
        cartCache.clear();
    }

    // ─────────────────────────────────────────────────────────────────
    // Phân quyền
    // ─────────────────────────────────────────────────────────────────
    /** @param roleLevel 0 = Quản lý, 1 = Nhân viên, 2 = Khách hàng */
    public static boolean hasRole(int roleLevel) {
        if (currentUser == null) return false;
        return currentUser.getLoaiTK() == roleLevel;
    }

    // ─────────────────────────────────────────────────────────────────
    // Helpers cho cache giỏ hàng
    // ─────────────────────────────────────────────────────────────────

    public static void setCartCache(List<GioHangDTO> items) {
        cartCache.clear();
        if (items != null) cartCache.addAll(items);
    }

    public static void upsertCartCache(GioHangDTO item) {
        if (item == null) return;
        for (int i = 0; i < cartCache.size(); i++) {
            if (cartCache.get(i).getMaSP().equals(item.getMaSP())) {
                cartCache.set(i, item);
                return;
            }
        }
        cartCache.add(item);
    }


    public static void removeFromCartCache(String maSP) {
        cartCache.removeIf(dto -> dto.getMaSP().equals(maSP));
    }

    public static int getCartItemCount() {
        return cartCache.size();
    }

    // ─────────────────────────────────────────────────────────────────
    // CÁC HÀM BỔ SUNG ĐỂ FIX LỖI COMPILE CHO GIOHANGDIALOG
    // ─────────────────────────────────────────────────────────────────


    public static void updateCartItemCount() {
        // Không cần xử lý gì thêm vì cartCache.size() luôn lấy giá trị mới nhất (Real-time)
    }

    public static void clearCart() {
        cartCache.clear();
    }
}