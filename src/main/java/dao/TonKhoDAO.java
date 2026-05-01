/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dto.TonKhoDTO;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class TonKhoDAO {
    public ArrayList<TonKhoDTO> selectAll() {
        ArrayList<TonKhoDTO> ketQua = new ArrayList<>();
        String sql = "SELECT * FROM TONKHO";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
             
            while (rs.next()) {
                TonKhoDTO tk = new TonKhoDTO();
                tk.setMaTonKho(rs.getString("MaTonKho"));
                tk.setMaKho(rs.getString("MaKho"));
                tk.setMaCTLH(rs.getString("MaCTLH"));
                tk.setSlConLai(rs.getDouble("SLConLai"));
                
                try {
                    tk.setSlKhaDung(rs.getDouble("SLKhaDung"));
                } catch (Exception e) {
                    tk.setSlKhaDung(0); // Phòng hờ DB chưa có cột SLKhaDung
                }
                
                tk.setTgNhapKho(rs.getDate("TGNhapKho"));
                tk.setTgHetHan(rs.getDate("TGHetHan"));
                tk.setViTri(rs.getString("ViTri"));
                
                ketQua.add(tk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ketQua;
    }
}
