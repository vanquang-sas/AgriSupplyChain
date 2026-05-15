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

            try (CallableStatement cs = conn.prepareCall("{call SP_THEM_LH(?, ?)}")) {
                cs.setString(1, maNCC);
                cs.setString(2, maNV);
                cs.execute();
            }

            String maLH;
            try (PreparedStatement ps = conn.prepareStatement("SELECT 'LH' || LPAD(SEQ_LOHANG.CURRVAL,6,'0') AS MaLH FROM DUAL");
                 ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }
                maLH = rs.getString("MaLH");
            }

            try (CallableStatement cs = conn.prepareCall("{call SP_THEM_CTLH(?, ?, ?)}")) {
                for (ChiTietLoHangDTO item : details) {
                    cs.setString(1, maLH);
                    cs.setString(2, item.getMaSP());
                    cs.setDouble(3, item.getSoLuong());
                    cs.execute();
                }
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

    // 1. Thêm lô hàng
    public boolean themLoHang(int maNCC) {
        try {
            Connection conn = DBConnection.getConnection();
            CallableStatement cs = conn.prepareCall("{call SP_THEM_LH(?)}");
            cs.setInt(1, maNCC);
            return cs.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. Thêm chi tiết lô hàng
    public boolean themChiTietLoHang(int maLH, int maSP, int soLuong) {
        try {
            Connection conn = DBConnection.getConnection();
            CallableStatement cs = conn.prepareCall("{call SP_THEM_CTLH(?,?,?)}");
            cs.setInt(1, maLH);
            cs.setInt(2, maSP);
            cs.setInt(3, soLuong);
            return cs.executeUpdate() > 0;
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
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call SP_XOA_LH(?)}")) {
            cs.setString(1, maLH);
            cs.execute();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<ChiTietLoHangDTO> getChiTietLoHang(String maLH) {
        List<ChiTietLoHangDTO> list = new ArrayList<>();
        String sql = "SELECT MaCTLH, MaLH, MaSP, GiaMua, SoLuong, ThanhTien " +
                     "FROM CHITIETLOHANG WHERE MaLH = ? ORDER BY MaCTLH";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLH);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietLoHangDTO item = new ChiTietLoHangDTO();
                    item.setMaCTLH(rs.getString("MaCTLH"));
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