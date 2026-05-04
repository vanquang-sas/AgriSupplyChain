package dao;

import dto.TaiKhoanDTO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import util.DBConnection;

public class TaiKhoanDAO {

    public String dangKyTaiKhoan(String username, String hashPassword, int loaiTK, String ten, String diaChi, String sdt, String email, String loaiKHHoacChucVu) {
        String sql = "{call SP_DANGKY_TAIKHOAN(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DBConnection.getConnection();
            CallableStatement cs = conn.prepareCall(sql)) {
            
            cs.setString(1, username);
            cs.setString(2, hashPassword);
            cs.setInt(3, loaiTK);
            cs.setString(4, ten);
            cs.setString(5, diaChi);
            cs.setString(6, sdt);
            cs.setString(7, email);
            cs.setString(8, loaiKHHoacChucVu);
            cs.setNull(9, java.sql.Types.NUMERIC); // Lương null cho Khách hàng
            
            cs.executeUpdate();
            return "SUCCESS";
        } catch (SQLException e) {
            System.err.println("Lỗi TaiKhoanDAO - dangKyTaiKhoan: " + e.getMessage());
            // Mã lỗi 1 trong Oracle/SQL là lỗi Unique Constraint (trùng Username)
            if (e.getErrorCode() == 1 || e.getMessage().contains("ORA-00001")) {
                return "DUPLICATE";
            }
            return "SYSTEM_ERROR";
        }
    }

    public TaiKhoanDTO checkLogin(String username, String password) {
        TaiKhoanDTO taiKhoan = null;
        String sql = "SELECT * FROM TAIKHOAN WHERE USERNAME = ? AND PASSWORD = ?";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, username);
            pst.setString(2, password);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    taiKhoan = new TaiKhoanDTO();
                    taiKhoan.setUsername(rs.getString("USERNAME"));
                    taiKhoan.setPassword(rs.getString("PASSWORD"));
                    taiKhoan.setLoaiTK(rs.getInt("LOAITK"));
                    taiKhoan.setTrangThaiTK(rs.getInt("TRANGTHAITK"));
                    taiKhoan.setTgTao(rs.getDate("TGTAO"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return taiKhoan;
    }
}
