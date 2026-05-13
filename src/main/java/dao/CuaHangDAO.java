package dao;

import dto.SanPhamDTO;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CuaHangDAO {

    // 1. Lấy tất cả sản phẩm
    public List<SanPhamDTO> getAllSanPham(String maKH) {

        List<SanPhamDTO> list = new ArrayList<>();

        String sql = """
            SELECT DISTINCT SP.*
            FROM SANPHAM SP
            JOIN CHITIETDONHANG CT ON SP.MASP = CT.MASP
            JOIN DONHANG DH ON CT.MADH = DH.MADH
            WHERE DH.MAKH = ?
        """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setString(1, maKH);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                SanPhamDTO sp = new SanPhamDTO();

                sp.setMaSP(rs.getString("MASP"));
                sp.setTenSP(rs.getString("TENSP"));
                sp.setMaLSP(rs.getString("MALSP"));
                sp.setGiaBan(rs.getDouble("GIABAN"));

                list.add(sp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 2. Tìm kiếm theo tên + khách hàng
    public List<SanPhamDTO> timKiem(String keyword, String maKH) {

        List<SanPhamDTO> list = new ArrayList<>();

        String sql = """
            SELECT DISTINCT SP.*
            FROM SANPHAM SP
            JOIN CHITIETDONHANG CT ON SP.MASP = CT.MASP
            JOIN DONHANG DH ON CT.MADH = DH.MADH
            WHERE DH.MAKH = ?
            AND LOWER(SP.TENSP) LIKE LOWER(?)
        """;
        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setString(1, maKH);
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                SanPhamDTO sp = new SanPhamDTO();

                sp.setMaSP(rs.getString("MASP"));
                sp.setTenSP(rs.getString("TENSP"));
                sp.setMaLSP(rs.getString("MALSP"));
                sp.setGiaBan(rs.getDouble("GIABAN"));

                list.add(sp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 3.  Lọc theo loại + khách hàng
    public List<SanPhamDTO> locTheoLoai(String loai,String maKH) {

        List<SanPhamDTO> list = new ArrayList<>();

        String sql = """
            SELECT DISTINCT SP.*
            FROM SANPHAM SP
            JOIN CHITIETDONHANG CT ON SP.MASP = CT.MASP
            JOIN DONHANG DH ON CT.MADH = DH.MADH
            WHERE DH.MAKH = ?
            AND SP.MALSP = ?
        """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setString(1, maKH);
            ps.setString(2, loai);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                SanPhamDTO sp = new SanPhamDTO();

                sp.setMaSP(rs.getString("MASP"));
                sp.setTenSP(rs.getString("TENSP"));
                sp.setMaLSP(rs.getString("MALSP"));
                sp.setGiaBan(rs.getDouble("GIABAN"));

                list.add(sp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    // 4. Chi tiết sản phẩm theo id + khách hàng
    public SanPhamDTO getById(String maSP,String maKH) {

        SanPhamDTO sp = null;

        try {
            String sql = """
                SELECT DISTINCT SP.*
                FROM SANPHAM SP
                JOIN CHITIETDONHANG CT ON SP.MASP = CT.MASP
                JOIN DONHANG DH ON CT.MADH = DH.MADH
                WHERE SP.MASP = ?
                AND DH.MAKH = ?
            """;
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, maSP);
            ps.setString(2, maKH);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                sp = new SanPhamDTO();

                sp.setMaSP(rs.getString("MASP"));
                sp.setTenSP(rs.getString("TENSP"));
                sp.setMaLSP(rs.getString("MALSP"));
                sp.setChatLuong(rs.getString("CHATLUONG"));
                sp.setGiaMua(rs.getDouble("GIAMUA"));
                sp.setGiaBan(rs.getDouble("GIABAN"));
                sp.setDonViTinh(rs.getString("DONVITINH"));
                sp.setBaoQuan(rs.getString("BAOQUAN"));
                sp.setHinhAnh(rs.getString("HINHANH"));
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return sp;
}
}