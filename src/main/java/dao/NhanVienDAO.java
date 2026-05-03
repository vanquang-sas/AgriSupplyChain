package dao;

import dto.NhanVienDTO;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    // ===================== ĐỌC DỮ LIỆU =====================

    public List<NhanVienDTO> getAll() {
        List<NhanVienDTO> list = new ArrayList<>();
        String sql = "SELECT NV.*, TK.TrangThaiTK FROM NHANVIEN NV " +
                     "LEFT JOIN TAIKHOAN TK ON NV.Username = TK.Username ORDER BY NV.MaNV";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public NhanVienDTO getById(String maNV) {
        String sql = "SELECT NV.*, TK.TrangThaiTK FROM NHANVIEN NV " +
                     "LEFT JOIN TAIKHOAN TK ON NV.Username = TK.Username WHERE NV.MaNV = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<NhanVienDTO> timKiem(String keyword) {
        List<NhanVienDTO> list = new ArrayList<>();
        String sql = "SELECT NV.*, TK.TrangThaiTK FROM NHANVIEN NV " +
                     "LEFT JOIN TAIKHOAN TK ON NV.Username = TK.Username " +
                     "WHERE UPPER(NV.TenNV) LIKE UPPER(?) OR UPPER(NV.MaNV) LIKE UPPER(?) OR NV.SDT LIKE ?";
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

    // Thêm mới: tạo tài khoản (LoaiTK=1: Nhân viên) + hồ sơ trong 1 transaction
    public void them(NhanVienDTO nv, String password) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            // Bước 1: Tạo tài khoản (LoaiTK = 1: Nhân viên)
            try (CallableStatement cs = conn.prepareCall("{CALL SP_THEM_TAIKHOAN(?, ?, ?)}")) {
                cs.setString(1, nv.getUsername());
                cs.setString(2, password);
                cs.setInt(3, 1);
                cs.execute();
            }

            // Bước 2: Tạo hồ sơ nhân viên
            try (CallableStatement cs = conn.prepareCall("{CALL SP_THEM_NV(?, ?, ?, ?, ?)}")) {
                cs.setString(1, nv.getUsername());
                cs.setString(2, nv.getTenNV());
                cs.setString(3, nv.getChucVu());
                cs.setString(4, nv.getSdt());
                cs.setDouble(5, nv.getLuong());
                cs.execute();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    // Cập nhật: SP_CAPNHAT_NV
    public void capNhat(NhanVienDTO nv) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL SP_CAPNHAT_NV(?, ?, ?, ?, ?)}")) {
            cs.setString(1, nv.getMaNV());
            cs.setString(2, nv.getTenNV());
            cs.setString(3, nv.getChucVu());
            cs.setString(4, nv.getSdt());
            cs.setDouble(5, nv.getLuong());
            cs.execute();
        }
    }

    // Khóa tài khoản: SP_KHOA_TAIKHOAN
    public void khoaTaiKhoan(String username) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL SP_KHOA_TAIKHOAN(?)}")) {
            cs.setString(1, username);
            cs.execute();
        }
    }

    // Mở khóa tài khoản
    public void moKhoaTaiKhoan(String username) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE TAIKHOAN SET TrangThaiTK = 1 WHERE Username = ?")) {
            ps.setString(1, username);
            ps.executeUpdate();
            conn.commit();
        }
    }

    public boolean isUsernameExists(String username) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT COUNT(*) FROM TAIKHOAN WHERE Username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===================== HELPER =====================
    private NhanVienDTO mapRow(ResultSet rs) throws SQLException {
        NhanVienDTO nv = new NhanVienDTO();
        nv.setMaNV(rs.getString("MaNV"));
        nv.setUsername(rs.getString("Username"));
        nv.setTenNV(rs.getString("TenNV"));
        nv.setChucVu(rs.getString("ChucVu"));
        nv.setSdt(rs.getString("SDT"));
        nv.setLuong(rs.getDouble("Luong"));
        nv.setTrangThaiTK(rs.getInt("TrangThaiTK"));
        return nv;
    }
}