package dao;

import dto.TonKhoDTO;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;

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

    public ArrayList<Object[]> getDanhSachNhapKho() {
        ArrayList<Object[]> list = new ArrayList<>();

        String sql = """
            SELECT 
                CTLH.MaCTLH,
                SP.TenSP,
                CTLH.SoLuong,
                K.MaKho,
                SP.BaoQuan,
                ' ' as ViTri,
                ' ' AS NgayHetHan,
                LH.TrangThaiLH
            FROM CHITIETLOHANG CTLH
            JOIN SANPHAM SP ON CTLH.MaSP = SP.MaSP
            JOIN LOHANG LH ON CTLH.MaLH = LH.MaLH
            JOIN KHO K ON SP.BaoQuan = K.LoaiKho
            WHERE LH.TrangThaiLH = 'Chờ nhập kho'
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}