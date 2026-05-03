package dao;

import dto.LoHangDTO;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoHangDAO {

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
                lh.setMaLH(rs.getInt("MaLH"));
                lh.setMaNCC(rs.getInt("MaNCC"));
                lh.setTrangThai(rs.getString("TrangThai"));
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