package dao;

import dto.LoaiSanPhamDTO;
import util.DBConnection;
import oracle.jdbc.OracleTypes;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LoaiSanPhamDAO {

    public List<LoaiSanPhamDTO> layDanhSachLSP() throws Exception {
        List<LoaiSanPhamDTO> list = new ArrayList<>();
        String sql = "{CALL SP_LAY_DS_LSP(?)}";
        
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.execute();
            
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs.next()) {
                    LoaiSanPhamDTO lsp = new LoaiSanPhamDTO();
                    lsp.setMaLSP(rs.getString("MaLSP"));
                    lsp.setTenLSP(rs.getString("TenLSP"));
                    lsp.setMoTa(rs.getString("MoTa"));
                    list.add(lsp);
                }
            }
        }
        return list;
    }

    public boolean themLSP(LoaiSanPhamDTO lsp) throws Exception {
        // Tham chiếu đúng chữ ký Procedure trong 05_Procedures.sql
        String sql = "{CALL SP_THEM_LSP(?, ?)}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, lsp.getTenLSP());
            cstmt.setString(2, lsp.getMoTa());
            return cstmt.executeUpdate() > 0;
        }
    }

    public boolean capNhatLSP(LoaiSanPhamDTO lsp) throws Exception {
        String sql = "{CALL SP_CAPNHAT_LSP(?, ?, ?)}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, lsp.getMaLSP());
            cstmt.setString(2, lsp.getTenLSP());
            cstmt.setString(3, lsp.getMoTa());
            return cstmt.executeUpdate() > 0;
        }
    }

    public boolean xoaLSP(String maLSP) throws Exception {
        String sql = "{CALL SP_XOA_LSP(?)}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, maLSP);
            return cstmt.executeUpdate() > 0;
        }
    }
}