package dao;

import dto.KhachHangDTO;
import util.DBConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import oracle.jdbc.OracleTypes;

public class KhachHangDAO {

    public boolean capNhatHoSoKH(String maKH, String tenKH, String diaChi, String sdt, String email) {
        String sql = "{call SP_CAPNHAT_KH(?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            
            cs.setString(1, maKH);
            cs.setString(2, tenKH);
            cs.setNull(3, java.sql.Types.NVARCHAR); // Không tự đổi LoaiKH
            cs.setString(4, diaChi);
            cs.setString(5, sdt);
            cs.setString(6, email);
            
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - capNhatHoSoKH: " + e.getMessage());
            return false;
        }
    }

    // Lấy toàn bộ danh sách qua Function SYS_REFCURSOR
    public List<KhachHangDTO> getAll() {
        List<KhachHangDTO> list = new ArrayList<>();
        String sql = "{ ? = call FN_LAY_DS_KHACHHANG() }";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy 1 khách hàng theo MaKH để load lên Form chỉnh sửa
    public KhachHangDTO getById(String maKH) {
        String sql = "SELECT KH.*, TK.TrangThaiTK FROM KHACHHANG KH " +
                     "LEFT JOIN TAIKHOAN TK ON KH.Username = TK.Username WHERE KH.MaKH = ?";
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
        String sql = "SELECT KH.*, TK.TrangThaiTK FROM KHACHHANG KH " +
                     "LEFT JOIN TAIKHOAN TK ON KH.Username = TK.Username " +
                     "WHERE UPPER(KH.TenKH) LIKE UPPER(?) OR UPPER(KH.MaKH) LIKE UPPER(?) OR KH.SDT LIKE ?";
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
            try (CallableStatement cs = conn.prepareCall("{CALL SP_THEM_TAIKHOAN(?, ?, ?)}")) {
                cs.setString(1, kh.getUsername());
                cs.setString(2, password);
                cs.setInt(3, 2);
                cs.execute();
            }

            // Bước 2: Tạo hồ sơ khách hàng
            try (CallableStatement cs = conn.prepareCall("{CALL SP_THEM_KH(?, ?, ?, ?, ?, ?)}")) {
                cs.setString(1, kh.getUsername());
                cs.setString(2, kh.getTenKH());
                cs.setString(3, kh.getLoaiKH());
                cs.setString(4, kh.getDiaChi());
                cs.setString(5, kh.getSdt());
                cs.setString(6, kh.getEmail());
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
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL SP_CAPNHAT_KH(?, ?, ?, ?, ?, ?)}")) {
            cs.setString(1, kh.getMaKH());
            cs.setString(2, kh.getTenKH());
            cs.setString(3, kh.getLoaiKH());
            cs.setString(4, kh.getDiaChi());
            cs.setString(5, kh.getSdt());
            cs.setString(6, kh.getEmail());
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

    // Mở khóa tài khoản (chưa có SP riêng nên dùng UPDATE trực tiếp)
    public void moKhoaTaiKhoan(String username) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE TAIKHOAN SET TrangThaiTK = 1 WHERE Username = ?")) {
            ps.setString(1, username);
            ps.executeUpdate();
            conn.commit();
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

}