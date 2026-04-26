package dao;

import dto.LoaiSanPhamDTO;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LoaiSanPhamDAO {
    public boolean insert(LoaiSanPhamDTO dto) {
        String sql = "SP_THEM_LSP (TenLSP, MoTa) VALUES (?, ?)"; // MaLSP do trigger sinh tự động
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
             
            pst.setString(1, dto.getTenLSP());
            pst.setString(2, dto.getMoTa());
            
            int rowAffected = pst.executeUpdate();
            return rowAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}