package dao;

import dto.ThamSoDTO;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThamSoDAO {

    // Lấy toàn bộ tham số (màn hình Cấu hình sẽ hiển thị tất cả)
    public List<ThamSoDTO> getAll() {
        List<ThamSoDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM THAMSO ORDER BY MaTS";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy 1 tham số theo MaTS
    public ThamSoDTO getById(String maTS) {
        String sql = "SELECT * FROM THAMSO WHERE MaTS = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTS);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cập nhật tham số trực tiếp bằng SQL
    public void capNhat(String maTS, double giaTri, String moTa) throws SQLException {
        String sql = "UPDATE THAMSO SET GiaTri = ?, MoTa = ? WHERE MaTS = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, giaTri);
            ps.setString(2, moTa);
            ps.setString(3, maTS);
            ps.executeUpdate();
        }
    }

    public double getValueByName(String name, double defaultValue) {
        String sql = "SELECT GiaTri FROM THAMSO WHERE TenTS = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("GiaTri");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    // ===================== HELPER =====================
    private ThamSoDTO mapRow(ResultSet rs) throws SQLException {
        return new ThamSoDTO(
            rs.getString("MaTS"),
            rs.getString("TenTS"),
            rs.getDouble("GiaTri"),
            rs.getString("MoTa")
        );
    }
}