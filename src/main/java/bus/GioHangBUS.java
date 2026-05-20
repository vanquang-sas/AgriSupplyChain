package bus;

import dao.GioHangDAO;
import dto.GioHangDTO;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;


public class GioHangBUS {

    private final GioHangDAO dao = new GioHangDAO();

    // ─────────────────────────────────────────────────────────────────
    // 1. Thêm vào giỏ (có kiểm tra tồn kho)
    // ─────────────────────────────────────────────────────────────────
    /**
     * Thêm sản phẩm vào giỏ sau khi kiểm tra SLKhaDung đủ không.
     *
     * @param maKH     Mã khách hàng
     * @param maSP     Mã sản phẩm
     * @param soLuong  Số lượng muốn thêm
     * @return Chuỗi thông báo kết quả (null = thành công)
     */
    public String addToCart(String maKH, String maSP, double soLuong) {
        if (soLuong <= 0) return "Số lượng phải lớn hơn 0.";

        // Lấy tổng SLKhaDung hiện tại (chỉ lô chưa hết hạn)
        double slKhaDung = getSlKhaDung(maSP);
        if (slKhaDung < 0) return "Không tìm thấy sản phẩm trong kho.";

        // Tính tổng đang giữ trong giỏ của KH này (tránh vượt kho)
        double dangGiuTrongGio = getSoLuongTrongGio(maKH, maSP);
        double canThem = soLuong + dangGiuTrongGio;

        if (slKhaDung < canThem) {
            return String.format(
                "Kho chỉ còn %.0f đơn vị khả dụng (bạn đang giữ %.0f trong giỏ).",
                slKhaDung, dangGiuTrongGio
            );
        }

        boolean ok = dao.addToCart(maKH, maSP, soLuong);
        return ok ? null : "Lỗi hệ thống khi thêm vào giỏ hàng.";
    }

    // ─────────────────────────────────────────────────────────────────
    // 2. Cập nhật số lượng (có kiểm tra tồn kho)
    // ─────────────────────────────────────────────────────────────────
    /**
     * Đặt lại số lượng tuyệt đối cho một dòng trong giỏ.
     *
     * @return null nếu thành công, chuỗi lỗi nếu thất bại.
     */
    public String updateQuantity(String maKH, String maSP, double soLuongMoi) {
        if (soLuongMoi <= 0) return "Số lượng phải lớn hơn 0.";

        double slKhaDung = getSlKhaDung(maSP);
        if (slKhaDung < soLuongMoi) {
            return String.format("Kho chỉ còn %.0f đơn vị khả dụng.", slKhaDung);
        }

        boolean ok = dao.updateQuantity(maKH, maSP, soLuongMoi);
        return ok ? null : "Lỗi hệ thống khi cập nhật giỏ hàng.";
    }

    // ─────────────────────────────────────────────────────────────────
    // 3. Xoá một mục
    // ─────────────────────────────────────────────────────────────────
    public boolean deleteItem(String maKH, String maSP) {
        return dao.deleteItem(maKH, maSP);
    }

    // ─────────────────────────────────────────────────────────────────
    // 4. Xoá toàn bộ giỏ (gọi sau khi đặt hàng thành công)
    // ─────────────────────────────────────────────────────────────────
    public boolean clearCart(String maKH) {
        return dao.clearCart(maKH);
    }

    // ─────────────────────────────────────────────────────────────────
    // 5. Lấy danh sách giỏ hàng
    // ─────────────────────────────────────────────────────────────────
    public List<GioHangDTO> getCart(String maKH) {
        return dao.getCartByCustomer(maKH);
    }

    // ─────────────────────────────────────────────────────────────────
    // 6. Tính tổng tiền giỏ hàng
    // ─────────────────────────────────────────────────────────────────
    /**
     * Duyệt qua danh sách DTO và cộng dồn ThanhTien.
     * Không cần truy vấn thêm vì donGia đã được JOIN sẵn trong DAO.
     */
    public BigDecimal calculateTotal(List<GioHangDTO> cartItems) {
        return cartItems.stream()
                .map(GioHangDTO::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ─────────────────────────────────────────────────────────────────
    // 7. Đếm số mục (dùng cho badge)
    // ─────────────────────────────────────────────────────────────────
    public int countItems(String maKH) {
        return dao.countItems(maKH);
    }

    // ─────────────────────────────────────────────────────────────────
    // Helper – Lấy tổng SLKhaDung của sản phẩm (chưa hết hạn)
    // ─────────────────────────────────────────────────────────────────
    public double getSlKhaDung(String maSP) {
        String sql = """
            SELECT NVL(SUM(TK.SLKhaDung), 0)
            FROM   TONKHO TK
            JOIN   CHITIETLOHANG CTLH ON TK.MaCTLH = CTLH.MaCTLH
            WHERE  CTLH.MaSP = ?
              AND  (TK.TGHetHan IS NULL OR TK.TGHetHan >= TRUNC(SYSDATE))
            """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maSP);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("GioHangBUS.getSlKhaDung() lỗi: " + e.getMessage());
        }
        return -1; // Báo lỗi tra cứu
    }

    // Helper – Lấy số lượng đang trong giỏ của KH cho sản phẩm này
    private double getSoLuongTrongGio(String maKH, String maSP) {
        String sql = "SELECT NVL(SoLuong, 0) FROM GIOHANG WHERE MaKH = ? AND MaSP = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKH);
            ps.setString(2, maSP);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("GioHangBUS.getSoLuongTrongGio() lỗi: " + e.getMessage());
        }
        return 0;
    }
}