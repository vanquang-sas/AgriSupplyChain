package dao;

import dto.TonKhoDTO;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhapKhoDAO {

    public void xacNhanViTri(TonKhoDTO dto) throws SQLException {
        String sql = "{call SP_XACNHAN_VITRI_CTLH(?, ?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
                CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, dto.getMaCTLH());
            cs.setString(2, dto.getMaKho());
            cs.setDate(3, new java.sql.Date(dto.getTgHetHan().getTime()));
            cs.setString(4, dto.getViTri());

            cs.execute();
        }
    }

    // Lấy danh sách lô hàng đang chờ nhập kho
    public ArrayList<Object[]> getDanhSachNhapKho() {
        ArrayList<Object[]> list = new ArrayList<>();
        // Sử dụng Function trong Database đã có
        String sql = "{ ? = call FN_GET_DS_NHAPKHO() }";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();
            
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("MaCTLH"),
                        rs.getString("TenSP"),
                        rs.getInt("SoLuong"),
                        rs.getString("MaKho"),
                        rs.getString("BaoQuan"),
                        rs.getString("ViTri"),
                        rs.getString("NgayHetHan"),
                        rs.getString("TrangThaiLH")
                    };
                    list.add(row);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    // Lấy danh sách mã kho
    public List<String> getAllMaKho() throws Exception {
        List<String> list = new ArrayList<>();
        String sql = "SELECT MaKho FROM KHO ORDER BY MaKho ASC";
         
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("MaKho"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return list;
    }

    // Lấy map mã kho và loại kho (để kiểm tra quy cách bảo quản)
    public java.util.Map<String, String> getKhoLoaiKhoMap() throws Exception {
        java.util.Map<String, String> map = new java.util.LinkedHashMap<>();
        String sql = "SELECT MaKho, LoaiKho FROM KHO ORDER BY MaKho ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("MaKho"), rs.getString("LoaiKho"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return map;
    }
}