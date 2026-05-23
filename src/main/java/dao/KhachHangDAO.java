package dao;

import dto.KhachHangDTO;
import util.DBConnection;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class KhachHangDAO {

    public boolean capNhatHoSoKH(String maKH, String tenKH, String diaChi, String sdt, String email) {
        String sqlUsername = "SELECT Username FROM KHACHHANG WHERE MaKH = ?";

        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String username = null;

            try (PreparedStatement ps = conn.prepareStatement(sqlUsername)) {
                ps.setString(1, maKH);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        username = rs.getString("Username");
                    }
                }
            }

            if (username == null) {
                conn.rollback();
                return false;
            }

            try (CallableStatement cs = conn.prepareCall("{call SP_CAPNHAT_KH(?, ?, ?)}")) {
                cs.setString(1, maKH);
                cs.setString(2, tenKH);
                cs.setNull(3, java.sql.Types.NVARCHAR);
                cs.execute();
            }

            try (CallableStatement cs = conn.prepareCall("{call SP_CAPNHAT_TAIKHOAN(?, ?, ?, ?)}")) {
                cs.setString(1, username);
                cs.setString(2, diaChi);
                cs.setString(3, sdt);
                cs.setString(4, email);
                cs.execute();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {

            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Lỗi rollback capNhatHoSoKH: " + rollbackEx.getMessage());
            }

            System.err.println("Lỗi DAO - capNhatHoSoKH: " + e.getMessage());
            return false;

        } finally {

            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Lấy toàn bộ danh sách khách hàng
    public List<KhachHangDTO> getAll() {
        List<KhachHangDTO> list = new ArrayList<>();
        String sql = "SELECT KH.MaKH, KH.Username, KH.TenKH, KH.LoaiKH, TK.DiaChi, TK.SDT, TK.Email, TK.TrangThaiTK " +
                     "FROM KHACHHANG KH LEFT JOIN TAIKHOAN TK ON KH.Username = TK.Username ORDER BY KH.MaKH";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy 1 khách hàng theo MaKH để load lên Form chỉnh sửa
    public KhachHangDTO getById(String maKH) {
        String sql = "SELECT KH.MaKH, KH.Username, KH.TenKH, KH.LoaiKH, TK.DiaChi, TK.SDT, TK.Email, TK.TrangThaiTK " +
                     "FROM KHACHHANG KH LEFT JOIN TAIKHOAN TK ON KH.Username = TK.Username WHERE KH.MaKH = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm kiếm theo từ khóa
    public List<KhachHangDTO> timKiem(String keyword) {
        List<KhachHangDTO> list = new ArrayList<>();
        String sql = "SELECT KH.MaKH, KH.Username, KH.TenKH, KH.LoaiKH, TK.DiaChi, TK.SDT, TK.Email, TK.TrangThaiTK " +
                     "FROM KHACHHANG KH LEFT JOIN TAIKHOAN TK ON KH.Username = TK.Username " +
                     "WHERE UPPER(KH.TenKH) LIKE UPPER(?) OR UPPER(KH.MaKH) LIKE UPPER(?) OR TK.SDT LIKE ?";
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

    // Thêm mới: tạo tài khoản + hồ sơ trong 1 transaction
    public void them(KhachHangDTO kh, String password) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            // Bước 1: Tạo tài khoản (LoaiTK = 2: Khách hàng)
            try (CallableStatement cs = conn.prepareCall("{CALL SP_THEM_TAIKHOAN(?, ?, ?, ?, ?, ?)}")) {
                cs.setString(1, kh.getUsername());
                cs.setString(2, password);
                cs.setInt(3, 2);
                cs.setString(4, kh.getDiaChi());
                cs.setString(5, kh.getSdt());
                cs.setString(6, kh.getEmail());
                cs.execute();
            }

            // Bước 2: Tạo hồ sơ khách hàng
            try (CallableStatement cs = conn.prepareCall("{CALL SP_THEM_KH(?, ?, ?)}")) {
                cs.setString(1, kh.getUsername());
                cs.setString(2, kh.getTenKH());
                cs.setString(3, kh.getLoaiKH());
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

    // Cập nhật: SP_CAPNHAT_KH
    public void capNhat(KhachHangDTO kh) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            String username = null;
            try (PreparedStatement ps = conn.prepareStatement("SELECT Username FROM KHACHHANG WHERE MaKH = ?")) {
                ps.setString(1, kh.getMaKH());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) username = rs.getString("Username");
                }
            }

            if (username == null) {
                conn.rollback();
                throw new SQLException("Không tìm thấy Username của khách hàng.");
            }

            try (CallableStatement cs = conn.prepareCall("{CALL SP_CAPNHAT_KH(?, ?, ?)}")) {
                cs.setString(1, kh.getMaKH());
                cs.setString(2, kh.getTenKH());
                cs.setString(3, kh.getLoaiKH());
                cs.execute();
            }

            try (CallableStatement cs = conn.prepareCall("{CALL SP_CAPNHAT_TAIKHOAN(?, ?, ?, ?)}")) {
                cs.setString(1, username);
                cs.setString(2, kh.getDiaChi());
                cs.setString(3, kh.getSdt());
                cs.setString(4, kh.getEmail());
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

    // Khóa tài khoản: SP_KHOA_TAIKHOAN
    public void khoaTaiKhoan(String username) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL SP_KHOA_TAIKHOAN(?)}")) {
            cs.setString(1, username);
            cs.execute();
        }
    }

    // Kiểm tra khách hàng có đơn hàng nào không để phục vụ kiểm tra trước khi xóa
    public boolean coDonHang(String maKH) {
        String sql = "SELECT COUNT(*) FROM DONHANG WHERE MaKH = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKH);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - coDonHang: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Mở khóa tài khoản (chưa có SP riêng nên dùng UPDATE trực tiếp)
    public void moKhoaTaiKhoan(String username) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                conn.setAutoCommit(true);
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE TAIKHOAN SET TrangThaiTK = 1 WHERE Username = ?")) {
                    ps.setString(1, username);
                    ps.executeUpdate();
                }
            }
        }
    }

    // Kiểm tra username đã tồn tại chưa (để validate trước khi thêm)
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

    // ===================== HÀM XÓA KHÁCH HÀNG =====================
    public boolean delete(String maKH) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            
            // 1. Lấy Username của khách hàng này để xóa luôn Tài khoản
            String username = null;
            try (PreparedStatement ps = conn.prepareStatement("SELECT Username FROM KHACHHANG WHERE MaKH = ?")) {
                ps.setString(1, maKH);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) username = rs.getString("Username");
                }
            }
            
            // 2. Xóa Khách Hàng trước (Xóa bảng con)
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM KHACHHANG WHERE MaKH = ?")) {
                ps.setString(1, maKH);
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
            throw e; // Ném lỗi lên BUS để giao diện bắt (VD: Lỗi dính khóa ngoại Đơn hàng)
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // ===================== HELPER =====================
    private KhachHangDTO mapRow(ResultSet rs) throws SQLException {
        KhachHangDTO kh = new KhachHangDTO();
        kh.setMaKH(rs.getString("MaKH"));
        kh.setUsername(rs.getString("Username"));
        kh.setTenKH(rs.getString("TenKH"));
        kh.setLoaiKH(rs.getString("LoaiKH"));
        kh.setDiaChi(rs.getString("DiaChi"));
        kh.setSdt(rs.getString("SDT"));
        kh.setEmail(rs.getString("Email"));
        kh.setTrangThaiTK(rs.getInt("TrangThaiTK"));
        return kh;
    }

    public dto.KhachHangDTO getByUsername(String username) {
        String sql = "SELECT KH.MaKH, KH.Username, KH.TenKH, KH.LoaiKH, TK.DiaChi, TK.SDT, TK.Email, TK.TrangThaiTK " +
                     "FROM KHACHHANG KH LEFT JOIN TAIKHOAN TK ON KH.Username = TK.Username WHERE KH.Username = ?";
        try (java.sql.Connection conn = util.DBConnection.getConnection();
             java.sql.PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            java.sql.ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}