package dao;

import dto.KhoDTO;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhoDAO {

    // ===================== ĐỌC DỮ LIỆU =====================

    public List<KhoDTO> getAll() {
        List<KhoDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM KHO ORDER BY MaKho";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public KhoDTO getById(String maKho) {
        String sql = "SELECT * FROM KHO WHERE MaKho = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKho);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<KhoDTO> timKiem(String keyword) {
        List<KhoDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM KHO WHERE UPPER(TenKho) LIKE UPPER(?) " +
                     "OR UPPER(MaKho) LIKE UPPER(?) OR UPPER(LoaiKho) LIKE UPPER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===================== GHI DỮ LIỆU =====================

    // Thêm kho mới trực tiếp bằng SQL
    public void them(KhoDTO kho) throws SQLException {
        String sql = "INSERT INTO KHO (TenKho, LoaiKho, DiaChi, MoTa) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kho.getTenKho());
            ps.setString(2, kho.getLoaiKho());
            ps.setString(3, kho.getDiaChi());
            ps.setString(4, kho.getMoTa());
            ps.executeUpdate();
        }
    }

    // Cập nhật kho trực tiếp bằng SQL
    public void capNhat(KhoDTO kho) throws SQLException {
        String sql = "UPDATE KHO SET TenKho = ?, LoaiKho = ?, DiaChi = ?, MoTa = ? WHERE MaKho = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kho.getTenKho());
            ps.setString(2, kho.getLoaiKho());
            ps.setString(3, kho.getDiaChi());
            ps.setString(4, kho.getMoTa());
            ps.setString(5, kho.getMaKho());
            ps.executeUpdate();
        }
    }

    // ===================== HELPER =====================
    private KhoDTO mapRow(ResultSet rs) throws SQLException {
        return new KhoDTO(
            rs.getString("MaKho"),
            rs.getString("TenKho"),
            rs.getString("LoaiKho"),
            rs.getString("DiaChi"),
            rs.getString("MoTa")
        );
    }

    // ===================== HÀM XÓA KHO BÃI =====================
    public boolean delete(String maKho) throws SQLException {
        String sql = "DELETE FROM KHO WHERE MaKho = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKho);
            return ps.executeUpdate() > 0;
        }
    }
}