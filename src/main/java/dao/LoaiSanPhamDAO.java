package dao;

import dto.LoaiSanPhamDTO;
import util.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoaiSanPhamDAO {

    public List<LoaiSanPhamDTO> getAll() {
        List<LoaiSanPhamDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM LOAISANPHAM ORDER BY MaLSP DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                LoaiSanPhamDTO lsp = new LoaiSanPhamDTO();
                lsp.setMaLSP(rs.getString("MaLSP"));
                lsp.setTenLSP(rs.getString("TenLSP"));
                lsp.setMoTa(rs.getString("MoTa"));
                list.add(lsp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<LoaiSanPhamDTO> timKiem(String keyword) {
        List<LoaiSanPhamDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM LOAISANPHAM WHERE LOWER(TenLSP) LIKE LOWER(?) OR LOWER(MaLSP) LIKE LOWER(?) ORDER BY MaLSP DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoaiSanPhamDTO lsp = new LoaiSanPhamDTO();
                    lsp.setMaLSP(rs.getString("MaLSP"));
                    lsp.setTenLSP(rs.getString("TenLSP"));
                    lsp.setMoTa(rs.getString("MoTa"));
                    list.add(lsp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean add(LoaiSanPhamDTO lsp) {
        String sql = "INSERT INTO LOAISANPHAM (TenLSP, MoTa) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, lsp.getTenLSP());
            ps.setString(2, lsp.getMoTa());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(LoaiSanPhamDTO lsp) {
        String sql = "UPDATE LOAISANPHAM SET TenLSP=?, MoTa=? WHERE MaLSP=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, lsp.getTenLSP());
            ps.setString(2, lsp.getMoTa());
            ps.setString(3, lsp.getMaLSP());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maLSP) throws SQLException {
        String sql = "{call SP_XOA_LSP(?)}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
             
            cs.setString(1, maLSP);
            cs.execute();
            return true;
        }
        // Không dùng try-catch ở đây để quăng lỗi (SQLException) lên tầng BUS và Panel xử lý
    }
}