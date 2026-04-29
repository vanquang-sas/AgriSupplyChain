package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import util.DBConnection;

public class KhachHangDAO {

    public boolean capNhatHoSoKH(String maKH, String tenKH, String diaChi, String sdt, String email) {
        String sql = "{call SP_CAPNHAT_KH(?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            
            cs.setString(1, maKH);
            cs.setString(2, tenKH);
            cs.setNull(3, java.sql.Types.NVARCHAR); // Không tự đổi LoaiKH
            cs.setString(4, diaChi);
            cs.setString(5, sdt);
            cs.setString(6, email);
            
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - capNhatHoSoKH: " + e.getMessage());
            return false;
        }
    }
}