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

    // Thêm mới: SP_THEM_NCC
    public void them(NhaCungCapDTO ncc) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL SP_THEM_NCC(?, ?, ?, ?, ?)}")) {
            cs.setString(1, ncc.getTenNCC());
            cs.setString(2, ncc.getDiaChi());
            cs.setString(3, ncc.getSdt());
            cs.setString(4, ncc.getEmail());
            cs.setString(5, ncc.getChungNhanCL());
            cs.execute();
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

    // Cập nhật: SP_CAPNHAT_NCC
    public void capNhat(NhaCungCapDTO ncc) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL SP_CAPNHAT_NCC(?, ?, ?, ?, ?, ?)}")) {
            cs.setString(1, ncc.getMaNCC());
            cs.setString(2, ncc.getTenNCC());
            cs.setString(3, ncc.getDiaChi());
            cs.setString(4, ncc.getSdt());
            cs.setString(5, ncc.getEmail());
            cs.setString(6, ncc.getChungNhanCL());
            cs.execute();
        }
    }

    // Ngừng hợp tác: SP_NGUNG_HOPTAC
    public void ngungHopTac(String maNCC) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL SP_NGUNG_HOPTAC(?)}")) {
            cs.setString(1, maNCC);
            cs.execute();
        }
    }

    // Khôi phục hợp tác (set TrangThaiHopTac = 1)
    public void khoiPhucHopTac(String maNCC) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE NHACUNGCAP SET TrangThaiHopTac = 1 WHERE MaNCC = ?")) {
            ps.setString(1, maNCC);
            ps.executeUpdate();
            conn.commit();
        }
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