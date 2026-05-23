package dao;

import dto.NhaCungCapDTO;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAO {

    // ===================== ĐỌC DỮ LIỆU =====================

    public List<NhaCungCapDTO> getAll() {
        List<NhaCungCapDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM NHACUNGCAP ORDER BY MaNCC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public NhaCungCapDTO getById(String maNCC) {
        String sql = "SELECT * FROM NHACUNGCAP WHERE MaNCC = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNCC);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<NhaCungCapDTO> timKiem(String keyword) {
        List<NhaCungCapDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM NHACUNGCAP WHERE UPPER(TenNCC) LIKE UPPER(?) " +
                     "OR UPPER(MaNCC) LIKE UPPER(?) OR SDT LIKE ?";
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

    // Thêm mới trực tiếp bằng SQL
    public void them(NhaCungCapDTO ncc) throws SQLException {
        String sql = "INSERT INTO NHACUNGCAP (TenNCC, DiaChi, SDT, Email, ChungNhanCL, TrangThaiHopTac) VALUES (?, ?, ?, ?, ?, 1)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ncc.getTenNCC());
            ps.setString(2, ncc.getDiaChi());
            ps.setString(3, ncc.getSdt());
            ps.setString(4, ncc.getEmail());
            ps.setString(5, ncc.getChungNhanCL());
            ps.executeUpdate();
        }
    }

    // ===================== HÀM XÓA NHÀ CUNG CẤP =====================
    public boolean delete(String maNCC) throws SQLException {
        String sql = "DELETE FROM NHACUNGCAP WHERE MaNCC = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNCC);
            return ps.executeUpdate() > 0;
        }
    }

    // Cập nhật trực tiếp bằng SQL
    public void capNhat(NhaCungCapDTO ncc) throws SQLException {
        String sql = "UPDATE NHACUNGCAP SET TenNCC = ?, DiaChi = ?, SDT = ?, Email = ?, ChungNhanCL = ? WHERE MaNCC = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ncc.getTenNCC());
            ps.setString(2, ncc.getDiaChi());
            ps.setString(3, ncc.getSdt());
            ps.setString(4, ncc.getEmail());
            ps.setString(5, ncc.getChungNhanCL());
            ps.setString(6, ncc.getMaNCC());
            ps.executeUpdate();
        }
    }

    // Ngừng hợp tác trực tiếp bằng SQL
    public void ngungHopTac(String maNCC) throws SQLException {
        String sql = "UPDATE NHACUNGCAP SET TrangThaiHopTac = 0 WHERE MaNCC = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNCC);
            ps.executeUpdate();
        }
    }

    // Khôi phục hợp tác (set TrangThaiHopTac = 1)
    public void khoiPhucHopTac(String maNCC) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE NHACUNGCAP SET TrangThaiHopTac = 1 WHERE MaNCC = ?")) {
            ps.setString(1, maNCC);
            ps.executeUpdate();
        }
    }

    public boolean daCungCapHang(String maNCC) {
        String sql = "SELECT COUNT(*) FROM LOHANG WHERE MaNCC = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNCC);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===================== HELPER =====================
    private NhaCungCapDTO mapRow(ResultSet rs) throws SQLException {
        NhaCungCapDTO ncc = new NhaCungCapDTO();
        ncc.setMaNCC(rs.getString("MaNCC"));
        ncc.setTenNCC(rs.getString("TenNCC"));
        ncc.setDiaChi(rs.getString("DiaChi"));
        ncc.setSdt(rs.getString("SDT"));
        ncc.setEmail(rs.getString("Email"));
        ncc.setChungNhanCL(rs.getString("ChungNhanCL"));
        ncc.setTrangThaiHopTac(rs.getInt("TrangThaiHopTac"));
        return ncc;
    }
}