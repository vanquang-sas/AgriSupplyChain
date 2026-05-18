package dao;

import dto.GioHangDTO;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GioHangDAO {

    // ─────────────────────────────────────────────────────────────────
    // 1. Thêm / cộng dồn vào giỏ
    // ─────────────────────────────────────────────────────────────────

    public boolean addToCart(String maKH, String maSP, double soLuong) {
        // MERGE xử lý cả hai nhánh trong một câu lệnh duy nhất
        String sql = """
            MERGE INTO GIOHANG GH
            USING (SELECT ? AS MaKH, ? AS MaSP FROM DUAL) SRC
            ON (GH.MaKH = SRC.MaKH AND GH.MaSP = SRC.MaSP)
            WHEN MATCHED THEN
                UPDATE SET GH.SoLuong = GH.SoLuong + ?
            WHEN NOT MATCHED THEN
                INSERT (MaKH, MaSP, SoLuong)
                VALUES (?, ?, ?)
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKH);
            ps.setString(2, maSP);
            ps.setDouble(3, soLuong);   // cho WHEN MATCHED
            ps.setString(4, maKH);
            ps.setString(5, maSP);
            ps.setDouble(6, soLuong);   // cho WHEN NOT MATCHED
            ps.executeUpdate();
            con.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("GioHangDAO.addToCart() lỗi: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // 2. Cập nhật số lượng
    // ─────────────────────────────────────────────────────────────────
    public boolean updateQuantity(String maKH, String maSP, double soLuongMoi) {
        String sql = "UPDATE GIOHANG SET SoLuong = ? WHERE MaKH = ? AND MaSP = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, soLuongMoi);
            ps.setString(2, maKH);
            ps.setString(3, maSP);
            int affected = ps.executeUpdate();
            con.commit();
            return affected > 0;

        } catch (SQLException e) {
            System.err.println("GioHangDAO.updateQuantity() lỗi: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // 3. Xoá một mục khỏi giỏ
    // ─────────────────────────────────────────────────────────────────
    public boolean deleteItem(String maKH, String maSP) {
        String sql = "DELETE FROM GIOHANG WHERE MaKH = ? AND MaSP = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKH);
            ps.setString(2, maSP);
            int affected = ps.executeUpdate();
            con.commit();
            return affected > 0;

        } catch (SQLException e) {
            System.err.println("GioHangDAO.deleteItem() lỗi: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // 4. Xoá toàn bộ giỏ của một khách (dùng sau khi đặt hàng thành công)
    // ─────────────────────────────────────────────────────────────────
    public boolean clearCart(String maKH) {
        String sql = "DELETE FROM GIOHANG WHERE MaKH = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKH);
            ps.executeUpdate();
            con.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("GioHangDAO.clearCart() lỗi: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // 5. Lấy toàn bộ giỏ hàng của một khách (JOIN SANPHAM)
    // ─────────────────────────────────────────────────────────────────
    public List<GioHangDTO> getCartByCustomer(String maKH) {
        List<GioHangDTO> list = new ArrayList<>();

        String sql = """
            SELECT GH.MaKH, GH.MaSP, GH.SoLuong, GH.TGCapNhat,
                   SP.TenSP, SP.GiaBan AS DonGia, SP.HinhAnh
            FROM   GIOHANG GH
            JOIN   SANPHAM SP ON GH.MaSP = SP.MaSP
            WHERE  GH.MaKH = ?
            ORDER BY GH.TGCapNhat DESC
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GioHangDTO dto = new GioHangDTO(
                        rs.getString("MaKH"),
                        rs.getString("MaSP"),
                        rs.getDouble("SoLuong"),
                        rs.getTimestamp("TGCapNhat"),
                        rs.getString("TenSP"),
                        rs.getBigDecimal("DonGia"),
                        rs.getString("HinhAnh")
                    );
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("GioHangDAO.getCartByCustomer() lỗi: " + e.getMessage());
        }

        return list;
    }

    // ─────────────────────────────────────────────────────────────────
    // 6. Đếm số mục trong giỏ (dùng cho badge icon)
    // ─────────────────────────────────────────────────────────────────
    public int countItems(String maKH) {
        String sql = "SELECT COUNT(*) FROM GIOHANG WHERE MaKH = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("GioHangDAO.countItems() lỗi: " + e.getMessage());
        }
        return 0;
    }
}