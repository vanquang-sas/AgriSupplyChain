package dao;

import dto.ChiTietLoHangDTO;
import dto.LoHangDTO;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoHangDAO {

    public boolean createLoHang(String maNCC, String maNV, List<ChiTietLoHangDTO> details) {
        if (maNCC == null || maNCC.trim().isEmpty() || maNV == null || maNV.trim().isEmpty() || details == null || details.isEmpty()) {
            return false;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String maLH = null;
            String sqlLH = "INSERT INTO LOHANG (MaNCC, MaNV, TrangThaiLH) VALUES (?, ?, 'Chờ kiểm duyệt')";
            try (PreparedStatement ps = conn.prepareStatement(sqlLH, new String[]{"MALH"})) {
                ps.setString(1, maNCC);
                ps.setString(2, maNV);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        maLH = rs.getString(1);
                    }
                }
            }

            if (maLH == null) {
                conn.rollback();
                return false;
            }

            String sqlCT = "INSERT INTO CHITIETLOHANG (MaLH, MaSP, SoLuong) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlCT)) {
                for (ChiTietLoHangDTO item : details) {
                    ps.setString(1, maLH);
                    ps.setString(2, item.getMaSP());
                    ps.setDouble(3, item.getSoLuong());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    // 1. Thêm lô hàng trực tiếp bằng SQL
    public boolean themLoHang(int maNCC) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO LOHANG (MaNCC, TrangThaiLH) VALUES (?, 'Chờ kiểm duyệt')")) {
            ps.setInt(1, maNCC);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. Thêm chi tiết lô hàng trực tiếp bằng SQL
    public boolean themChiTietLoHang(int maLH, int maSP, int soLuong) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO CHITIETLOHANG (MaLH, MaSP, SoLuong) VALUES (?, ?, ?)")) {
            ps.setInt(1, maLH);
            ps.setInt(2, maSP);
            ps.setInt(3, soLuong);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 3. Lấy danh sách lô hàng
    public List<LoHangDTO> getAllLoHang() {
        List<LoHangDTO> list = new ArrayList<>();

        String sql = "SELECT LH.MaLH, LH.MaNCC, N.TenNCC, LH.MaNV, LH.TGNhap, LH.TongTien, LH.TrangThaiLH " +
                     "FROM LOHANG LH LEFT JOIN NHACUNGCAP N ON LH.MaNCC = N.MaNCC " +
                     "ORDER BY LH.TGNhap DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LoHangDTO lh = new LoHangDTO();
                lh.setMaLH(rs.getString("MaLH"));
                lh.setMaNCC(rs.getString("MaNCC"));
                lh.setTenNCC(rs.getString("TenNCC"));
                lh.setMaNV(rs.getString("MaNV"));
                lh.setTgNhap(rs.getDate("TGNhap"));
                lh.setTongTien(rs.getDouble("TongTien"));
                lh.setTrangThaiLH(rs.getString("TrangThaiLH"));
                list.add(lh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<LoHangDTO> getLoHangByNhanVien(String maNV) {
        List<LoHangDTO> list = new ArrayList<>();
        if (maNV == null || maNV.trim().isEmpty()) {
            return list;
        }

        String sql = "SELECT LH.MaLH, LH.MaNCC, N.TenNCC, LH.MaNV, LH.TGNhap, LH.TongTien, LH.TrangThaiLH " +
                     "FROM LOHANG LH LEFT JOIN NHACUNGCAP N ON LH.MaNCC = N.MaNCC " +
                     "WHERE LH.MaNV = ? ORDER BY LH.TGNhap DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoHangDTO lh = new LoHangDTO();
                    lh.setMaLH(rs.getString("MaLH"));
                    lh.setMaNCC(rs.getString("MaNCC"));
                    lh.setTenNCC(rs.getString("TenNCC"));
                    lh.setMaNV(rs.getString("MaNV"));
                    lh.setTgNhap(rs.getDate("TGNhap"));
                    lh.setTongTien(rs.getDouble("TongTien"));
                    lh.setTrangThaiLH(rs.getString("TrangThaiLH"));
                    list.add(lh);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 4. Yêu cầu nhập kho
    public boolean yeuCauNhapKho(String maLH) {
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call SP_YEUCAU_NHAPKHO(?)}")) {
            cs.setString(1, maLH);
            cs.execute();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public boolean xoaLoHang(String maLH) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Xóa chi tiết lô hàng trước
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM CHITIETLOHANG WHERE MaLH = ?")) {
                ps.setString(1, maLH);
                ps.executeUpdate();
            }

            // 2. Xóa lô hàng cha
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM LOHANG WHERE MaLH = ?")) {
                ps.setString(1, maLH);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public List<ChiTietLoHangDTO> getChiTietLoHang(String maLH) {
        List<ChiTietLoHangDTO> list = new ArrayList<>();
        String sql = "SELECT MaLH, MaSP, GiaMua, SoLuong, ThanhTien " +
                     "FROM CHITIETLOHANG WHERE MaLH = ? ORDER BY MaSP";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLH);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietLoHangDTO item = new ChiTietLoHangDTO();
                    item.setMaLH(rs.getString("MaLH"));
                    item.setMaSP(rs.getString("MaSP"));
                    item.setGiaMua(rs.getDouble("GiaMua"));
                    item.setSoLuong(rs.getDouble("SoLuong"));
                    item.setThanhTien(rs.getDouble("ThanhTien"));
                    list.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}