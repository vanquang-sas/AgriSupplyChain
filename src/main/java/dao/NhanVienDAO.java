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
        String sql = "SELECT NV.MaNV, NV.Username, NV.TenNV, NV.ChucVu, NV.Luong, TK.SDT, TK.TrangThaiTK " +
                     "FROM NHANVIEN NV LEFT JOIN TAIKHOAN TK ON NV.Username = TK.Username ORDER BY NV.MaNV";
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
        String sql = "SELECT NV.MaNV, NV.Username, NV.TenNV, NV.ChucVu, NV.Luong, TK.SDT, TK.TrangThaiTK " +
                     "FROM NHANVIEN NV LEFT JOIN TAIKHOAN TK ON NV.Username = TK.Username WHERE NV.MaNV = ?";
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
        String sql = "SELECT NV.MaNV, NV.Username, NV.TenNV, NV.ChucVu, NV.Luong, TK.SDT, TK.TrangThaiTK " +
                     "FROM NHANVIEN NV LEFT JOIN TAIKHOAN TK ON NV.Username = TK.Username " +
                     "WHERE UPPER(NV.TenNV) LIKE UPPER(?) OR UPPER(NV.MaNV) LIKE UPPER(?) OR TK.SDT LIKE ?";
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
            try (CallableStatement cs = conn.prepareCall("{CALL SP_THEM_TAIKHOAN(?, ?, ?, ?, ?, ?)}")) {
                cs.setString(1, nv.getUsername());
                cs.setString(2, password);
                cs.setInt(3, 1);
                cs.setNull(4, java.sql.Types.NVARCHAR);
                cs.setString(5, nv.getSdt());
                cs.setNull(6, java.sql.Types.NVARCHAR);
                cs.execute();
            }

            // Bước 2: Tạo hồ sơ nhân viên
            try (CallableStatement cs = conn.prepareCall("{CALL SP_THEM_NV(?, ?, ?, ?)}")) {
                cs.setString(1, nv.getUsername());
                cs.setString(2, nv.getTenNV());
                cs.setString(3, nv.getChucVu());
                cs.setDouble(4, nv.getLuong());
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
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            String username = null;
            try (PreparedStatement ps = conn.prepareStatement("SELECT Username FROM NHANVIEN WHERE MaNV = ?")) {
                ps.setString(1, nv.getMaNV());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) username = rs.getString("Username");
                }
            }

            if (username == null) {
                conn.rollback();
                throw new SQLException("Không tìm thấy Username của nhân viên.");
            }

            try (CallableStatement cs = conn.prepareCall("{CALL SP_CAPNHAT_NV(?, ?, ?, ?)}")) {
                cs.setString(1, nv.getMaNV());
                cs.setString(2, nv.getTenNV());
                cs.setString(3, nv.getChucVu());
                cs.setDouble(4, nv.getLuong());
                cs.execute();
            }

            try (CallableStatement cs = conn.prepareCall("{CALL SP_CAPNHAT_TAIKHOAN(?, ?, ?, ?)}")) {
                cs.setString(1, username);
                cs.setNull(2, java.sql.Types.NVARCHAR);
                cs.setString(3, nv.getSdt());
                cs.setNull(4, java.sql.Types.NVARCHAR);
                cs.execute();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // ===================== HÀM XÓA NHÂN VIÊN =====================
    public boolean delete(String maNV) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            
            // 1. Lấy Username của nhân viên này để xóa luôn Tài khoản
            String username = null;
            try (PreparedStatement ps = conn.prepareStatement("SELECT Username FROM NHANVIEN WHERE MaNV = ?")) {
                ps.setString(1, maNV);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) username = rs.getString("Username");
                }
            }
            
            // 2. Xóa Nhân Viên trước (Xóa bảng con)
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM NHANVIEN WHERE MaNV = ?")) {
                ps.setString(1, maNV);
                ps.executeUpdate();
            }
            
            // 3. Xóa Tài Khoản tương ứng (Xóa bảng cha)
            if (username != null) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM TAIKHOAN WHERE Username = ?")) {
                    ps.setString(1, username);
                    ps.executeUpdate();
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e; // Ném lỗi lên BUS để giao diện bắt (VD: Lỗi dính khóa ngoại phiếu xuất nhập)
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
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

    public dto.NhanVienDTO getByUsername(String username) {
        String sql = "SELECT NV.MaNV, NV.Username, NV.TenNV, NV.ChucVu, NV.Luong, TK.SDT, TK.TrangThaiTK " +
                     "FROM NHANVIEN NV LEFT JOIN TAIKHOAN TK ON NV.Username = TK.Username WHERE NV.Username = ?";
        try (java.sql.Connection conn = util.DBConnection.getConnection();
            java.sql.PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            java.sql.ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapRow(rs); // Dùng lại hàm mapRow của bạn
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}