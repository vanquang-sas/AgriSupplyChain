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

            try (CallableStatement cs = conn.prepareCall("{call SP_YEUCAU_NHAPKHO(?)}")) {
                cs.setString(1, maLH);
                cs.execute();
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

        try {
            Connection conn = DBConnection.getConnection();
            CallableStatement cs = conn.prepareCall("{call SP_GET_ALL_LOHANG}");
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                LoHangDTO lh = new LoHangDTO();
                lh.setMaLH(rs.getString("MaLH"));
                lh.setMaNCC(rs.getString("MaNCC"));
                lh.setTrangThaiLH(rs.getString("TrangThai"));
                list.add(lh);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 4. Yêu cầu nhập kho
    public boolean yeuCauNhapKho(int maLH) {
        try {
            Connection conn = DBConnection.getConnection();
            CallableStatement cs = conn.prepareCall("{call SP_YEUCAU_NHAPKHO(?)}");
            cs.setInt(1, maLH);
            return cs.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}