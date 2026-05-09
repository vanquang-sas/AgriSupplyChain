package dao;

import dto.ThongKeDTO;
import util.DBConnection;
import oracle.jdbc.OracleTypes; // Cần import OracleTypes để dùng cursor

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO {

    public List<ThongKeDTO.DoanhThu> getDoanhThuTheoNam(int nam) {
        List<ThongKeDTO.DoanhThu> list = new ArrayList<>();
        String sql = "{call SP_THONGKE_DOANHTHU_NAM(?, ?)}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, nam);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    list.add(new ThongKeDTO.DoanhThu(rs.getInt("Thang"), rs.getDouble("DoanhThu")));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<ThongKeDTO.TopSanPham> getThongKeSanPham(int limit, String type, java.util.Date from, java.util.Date to) {
        List<ThongKeDTO.TopSanPham> list = new ArrayList<>();
        String sql = "{call SP_THONGKE_SANPHAM(?, ?, ?, ?, ?)}";
        
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            
            cs.setInt(1, limit);
            cs.setString(2, type);
            cs.setDate(3, new java.sql.Date(from.getTime()));
            cs.setDate(4, new java.sql.Date(to.getTime()));
            cs.registerOutParameter(5, OracleTypes.CURSOR);
            
            cs.execute();
            
            try (ResultSet rs = (ResultSet) cs.getObject(5)) {
                while (rs.next()) {
                    list.add(new ThongKeDTO.TopSanPham(
                        rs.getString("TenSP"), 
                        rs.getInt("TongSoLuong")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ThongKeDTO.TrangThai> getTyLeTrangThai() {
        List<ThongKeDTO.TrangThai> list = new ArrayList<>();
        String sql = "{call SP_THONGKE_TRANGTHAI_DH(?)}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    list.add(new ThongKeDTO.TrangThai(rs.getString("TrangThaiDH"), rs.getInt("SoLuong")));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ============================================= TRẠNG THÁI ĐƠN HÀNG =============================================
    public List<ThongKeDTO.TrangThai> getThongKeTrangThai(java.util.Date from, java.util.Date to) {
        List<ThongKeDTO.TrangThai> list = new ArrayList<>();
        String sql = "{call SP_THONGKE_TRANGTHAI(?, ?, ?)}";
        try (java.sql.Connection conn = util.DBConnection.getConnection();
             java.sql.CallableStatement cs = conn.prepareCall(sql)) {
            
            cs.setDate(1, new java.sql.Date(from.getTime()));
            cs.setDate(2, new java.sql.Date(to.getTime()));
            cs.registerOutParameter(3, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            
            try (java.sql.ResultSet rs = (java.sql.ResultSet) cs.getObject(3)) {
                while (rs.next()) {
                    list.add(new ThongKeDTO.TrangThai(rs.getString("TrangThaiDH"), rs.getInt("SoLuong")));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ============================================= THỐNG KÊ TÀI CHÍNH =============================================
    public List<ThongKeDTO.TaiChinh> getThongKeTaiChinh(int periodMonths) {
        List<ThongKeDTO.TaiChinh> list = new ArrayList<>();
        String sql = "{call SP_THONGKE_TAICHINH(?, ?)}";
        try (Connection conn = util.DBConnection.getConnection();
            CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, periodMonths);
            cs.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    list.add(new ThongKeDTO.TaiChinh(rs.getString("ThangNam"), rs.getDouble("DoanhThu"), rs.getDouble("ChiPhi")));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}