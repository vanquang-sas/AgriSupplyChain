package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import util.DBConnection;

public class KhachHangDAO {

    public boolean dangKyTaiKhoan(String username, String hashPassword, int loaiTK, String ten, String diaChi, String sdt, String email, String loaiHoacChucVu) {
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
            cs.setString(8, loaiHoacChucVu);
            cs.setNull(9, java.sql.Types.NUMERIC); // Lương null vì đây là đăng ký cho Khách Hàng
            
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - dangKyTaiKhoan: " + e.getMessage());
            return false;
        }
    }

    public boolean capNhatHoSoKH(String maKH, String tenKH, String diaChi, String sdt, String email) {
        String sql = "{call SP_CAPNHAT_KH(?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            
            cs.setString(1, maKH);
            cs.setString(2, tenKH);
            cs.setNull(3, java.sql.Types.NVARCHAR); // Không cho phép khách hàng tự đổi LoaiKH
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