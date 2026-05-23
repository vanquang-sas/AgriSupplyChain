package dao;

import dto.SanPhamDTO;
import util.DBConnection;
import oracle.jdbc.OracleTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO {

    public List<SanPhamDTO> getAll() {
        List<SanPhamDTO> list = new ArrayList<>();
        String sql = "{? = call FN_LAY_DS_SANPHAM()}";
        
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
             
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    SanPhamDTO sp = new SanPhamDTO();
                    sp.setMaSP(rs.getString("MaSP"));
                    sp.setTenSP(rs.getString("TenSP"));
                    sp.setMaLSP(rs.getString("MaLSP"));
                    sp.setTenLSP(rs.getString("TenLSP")); // Lấy từ JOIN
                    sp.setChatLuong(rs.getString("ChatLuong"));
                    sp.setGiaMua(rs.getDouble("GiaMua"));
                    sp.setGiaBan(rs.getDouble("GiaBan"));
                    sp.setDonViTinh(rs.getString("DonViTinh"));
                    sp.setBaoQuan(rs.getString("BaoQuan"));
                    sp.setHinhAnh(rs.getString("HinhAnh"));
                    list.add(sp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<SanPhamDTO> timKiem(String keyword) {
        List<SanPhamDTO> list = new ArrayList<>();
        String sql = "SELECT SP.MaSP, SP.TenSP, SP.MaLSP, LSP.TenLSP, SP.ChatLuong, SP.GiaMua, SP.GiaBan, SP.DonViTinh, SP.BaoQuan, SP.HinhAnh " +
                     "FROM SANPHAM SP " +
                     "LEFT JOIN LOAISANPHAM LSP ON SP.MaLSP = LSP.MaLSP " +
                     "WHERE UPPER(SP.TenSP) LIKE UPPER(?) OR UPPER(SP.MaSP) LIKE UPPER(?) OR UPPER(LSP.TenLSP) LIKE UPPER(?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SanPhamDTO sp = new SanPhamDTO();
                    sp.setMaSP(rs.getString("MaSP"));
                    sp.setTenSP(rs.getString("TenSP"));
                    sp.setMaLSP(rs.getString("MaLSP"));
                    sp.setTenLSP(rs.getString("TenLSP"));
                    sp.setChatLuong(rs.getString("ChatLuong"));
                    sp.setGiaMua(rs.getDouble("GiaMua"));
                    sp.setGiaBan(rs.getDouble("GiaBan"));
                    sp.setDonViTinh(rs.getString("DonViTinh"));
                    sp.setBaoQuan(rs.getString("BaoQuan"));
                    sp.setHinhAnh(rs.getString("HinhAnh"));
                    list.add(sp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean add(SanPhamDTO sp) {
        String sql = "INSERT INTO SANPHAM (TenSP, MaLSP, ChatLuong, GiaMua, GiaBan, DonViTinh, BaoQuan, HinhAnh) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sp.getTenSP());
            ps.setString(2, sp.getMaLSP()); // Nhận thẳng mã từ BUS/GUI
            ps.setString(3, sp.getChatLuong());
            ps.setDouble(4, sp.getGiaMua());
            ps.setDouble(5, sp.getGiaBan());
            ps.setString(6, sp.getDonViTinh());
            ps.setString(7, sp.getBaoQuan());
            ps.setString(8, sp.getHinhAnh());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(SanPhamDTO sp) {
        String sql = "UPDATE SANPHAM SET TenSP=?, MaLSP=?, ChatLuong=?, GiaMua=?, GiaBan=?, DonViTinh=?, BaoQuan=?, HinhAnh=? WHERE MaSP=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, sp.getTenSP());
            ps.setString(2, sp.getMaLSP());
            ps.setString(3, sp.getChatLuong());
            ps.setDouble(4, sp.getGiaMua());
            ps.setDouble(5, sp.getGiaBan());
            ps.setString(6, sp.getDonViTinh());
            ps.setString(7, sp.getBaoQuan());
            ps.setString(8, sp.getHinhAnh());
            ps.setString(9, sp.getMaSP());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maSP) throws SQLException {
        String sql = "DELETE FROM SANPHAM WHERE MaSP = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, maSP);
            return ps.executeUpdate() > 0;
        }
    }
}