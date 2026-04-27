package dao;

import dto.TaiKhoanDTO;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TaiKhoanDAO {
    
    /**
     * Kiểm tra thông tin đăng nhập trong cơ sở dữ liệu
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return TaiKhoanDTO nếu thành công, null nếu thất bại
     */
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
