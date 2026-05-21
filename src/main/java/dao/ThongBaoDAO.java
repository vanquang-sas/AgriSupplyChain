package dao;

import dto.ThongBaoDTO;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ThongBaoDAO {

    public List<ThongBaoDTO> getNotificationsForUser(String role, String userId) {
        List<ThongBaoDTO> list = new ArrayList<>();
        String sql;
        String filterValue = null;
        boolean hasFilter = true;

        if ("Khách hàng".equalsIgnoreCase(role)) {
            sql = "SELECT * FROM THONGBAO WHERE NguoiNhan = ? ORDER BY TGTao DESC";
            filterValue = userId;
        } else if ("NV kho".equalsIgnoreCase(role) || "NV thu mua".equalsIgnoreCase(role)) {
            sql = "SELECT * FROM THONGBAO WHERE NguoiNhan = ? ORDER BY TGTao DESC";
            filterValue = "Nhân viên kho/thu mua";
        } else if ("NV giao hàng".equalsIgnoreCase(role)) {
            sql = "SELECT * FROM THONGBAO WHERE NguoiNhan = ? ORDER BY TGTao DESC";
            filterValue = "Nhân viên giao hàng";
        } else if ("Quản lý (Admin)".equalsIgnoreCase(role)) {
            sql = "SELECT * FROM THONGBAO ORDER BY TGTao DESC";
            hasFilter = false;
        } else {
            sql = "SELECT * FROM THONGBAO WHERE NguoiNhan = ? ORDER BY TGTao DESC";
            filterValue = userId;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (hasFilter) {
                ps.setString(1, filterValue);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getUnreadCount(String role, String userId) {
        String sql;
        String filterValue = null;
        boolean hasFilter = true;

        if ("Khách hàng".equalsIgnoreCase(role)) {
            sql = "SELECT COUNT(*) FROM THONGBAO WHERE NguoiNhan = ? AND TrangThaiTB = 0";
            filterValue = userId;
        } else if ("NV kho".equalsIgnoreCase(role) || "NV thu mua".equalsIgnoreCase(role)) {
            sql = "SELECT COUNT(*) FROM THONGBAO WHERE NguoiNhan = ? AND TrangThaiTB = 0";
            filterValue = "Nhân viên kho/thu mua";
        } else if ("NV giao hàng".equalsIgnoreCase(role)) {
            sql = "SELECT COUNT(*) FROM THONGBAO WHERE NguoiNhan = ? AND TrangThaiTB = 0";
            filterValue = "Nhân viên giao hàng";
        } else if ("Quản lý (Admin)".equalsIgnoreCase(role)) {
            sql = "SELECT COUNT(*) FROM THONGBAO WHERE TrangThaiTB = 0";
            hasFilter = false;
        } else {
            sql = "SELECT COUNT(*) FROM THONGBAO WHERE NguoiNhan = ? AND TrangThaiTB = 0";
            filterValue = userId;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (hasFilter) {
                ps.setString(1, filterValue);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void markAllAsRead(String role, String userId) {
        String sql;
        String filterValue = null;
        boolean hasFilter = true;

        if ("Khách hàng".equalsIgnoreCase(role)) {
            sql = "UPDATE THONGBAO SET TrangThaiTB = 1 WHERE NguoiNhan = ? AND TrangThaiTB = 0";
            filterValue = userId;
        } else if ("NV kho".equalsIgnoreCase(role) || "NV thu mua".equalsIgnoreCase(role)) {
            sql = "UPDATE THONGBAO SET TrangThaiTB = 1 WHERE NguoiNhan = ? AND TrangThaiTB = 0";
            filterValue = "Nhân viên kho/thu mua";
        } else if ("NV giao hàng".equalsIgnoreCase(role)) {
            sql = "UPDATE THONGBAO SET TrangThaiTB = 1 WHERE NguoiNhan = ? AND TrangThaiTB = 0";
            filterValue = "Nhân viên giao hàng";
        } else if ("Quản lý (Admin)".equalsIgnoreCase(role)) {
            sql = "UPDATE THONGBAO SET TrangThaiTB = 1 WHERE TrangThaiTB = 0";
            hasFilter = false;
        } else {
            sql = "UPDATE THONGBAO SET TrangThaiTB = 1 WHERE NguoiNhan = ? AND TrangThaiTB = 0";
            filterValue = userId;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (hasFilter) {
                ps.setString(1, filterValue);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ThongBaoDTO mapRow(ResultSet rs) throws SQLException {
        return new ThongBaoDTO(
            rs.getString("MaTB"),
            rs.getString("LoaiTB"),
            rs.getString("NoiDung"),
            rs.getInt("TrangThaiTB"),
            rs.getTimestamp("TGTao"),
            rs.getString("NguoiNhan"),
            null
        );
    }
}
