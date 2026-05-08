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
    public List<SanPhamDTO> getAllSanPham() {

        List<SanPhamDTO> list = new ArrayList<>();

        String sql = "SELECT * FROM SANPHAM";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {

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

    // 2. Tìm kiếm theo tên
    public List<SanPhamDTO> timKiem(String keyword) {

        List<SanPhamDTO> list = new ArrayList<>();

        String sql = """
                SELECT * FROM SANPHAM
                WHERE LOWER(TENSP) LIKE LOWER(?)
                """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
        ) {

            ps.setString(1, "%" + keyword + "%");

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

    // 3. Lọc theo loại sản phẩm
    public List<SanPhamDTO> locTheoLoai(String loai) {

        List<SanPhamDTO> list = new ArrayList<>();

        String sql = """
                SELECT * FROM SANPHAM
                WHERE MALSP = ?
                """;

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
        ) {

            ps.setString(1, loai);

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
}