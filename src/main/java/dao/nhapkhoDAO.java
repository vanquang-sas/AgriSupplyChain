package dao;

import dto.TonKhoDTO;
import util.DBConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class nhapkhoDAO {
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
}