/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class TonKhoDAO {

    // Lấy danh sách tổng hợp để hiển thị lên bảng GUI
    public ArrayList<Object[]> getDanhSachTonKho() {
        ArrayList<Object[]> ketQua = new ArrayList<>();
        String sql = "SELECT tk.MaTonKho, tk.MaKho, sp.TenSP, sp.MaSP, lsp.TenLSP, ncc.TenNCC, " +
                "tk.SLConLai, sp.DonViTinh, tk.ViTri, tk.TGNhapKho, tk.TGHetHan " +
                "FROM TONKHO tk " +
                "LEFT JOIN CHITIETLOHANG ctlh ON tk.MaCTLH = ctlh.MaCTLH " +
                "LEFT JOIN LOHANG lh ON ctlh.MaLH = lh.MaLH " +
                "LEFT JOIN SANPHAM sp ON ctlh.MaSP = sp.MaSP " +
                "LEFT JOIN LOAISANPHAM lsp ON sp.MaLSP = lsp.MaLSP " +
                "LEFT JOIN NHACUNGCAP ncc ON lh.MaNCC = ncc.MaNCC";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Object[] row = new Object[11];
                row[0] = rs.getString("MaKho"); // Hiển thị Mã Kho
                row[1] = rs.getString("TenSP");
                row[2] = rs.getString("TenLSP"); // Loại
                row[3] = rs.getString("TenNCC"); // NCC
                row[4] = rs.getDouble("SLConLai"); // Số Lượng
                row[5] = rs.getString("DonViTinh"); // ĐVT
                row[6] = rs.getString("ViTri"); // Vị trí

                // Cập Nhật
                java.sql.Timestamp ts = rs.getTimestamp("TGNhapKho");
                if (ts != null) {
                    row[7] = new java.text.SimpleDateFormat("dd/MM/yyyy").format(ts);
                } else {
                    row[7] = "";
                }

                // TG Hết Hạn
                java.sql.Timestamp tsHetHan = rs.getTimestamp("TGHetHan");
                if (tsHetHan != null) {
                    row[8] = new java.text.SimpleDateFormat("dd/MM/yyyy").format(tsHetHan);
                } else {
                    row[8] = "";
                }

                row[10] = rs.getString("MaTonKho"); // Ẩn

                ketQua.add(row);
            }
        } catch (Exception e) {
            System.out.println("Lỗi tại TonKhoDAO.getDanhSachTonKho: " + e.getMessage());
            e.printStackTrace();
        }
        return ketQua;
    }

    // Hàm cập nhật số lượng
    public boolean capNhatTonKho(String maTonKho, double soLuongMoi, String viTriMoi) {
        String sql = "UPDATE TONKHO SET SLConLai = ?, ViTri = ? WHERE MaTonKho = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setDouble(1, soLuongMoi);
            pst.setString(2, viTriMoi);
            pst.setString(3, maTonKho);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Lỗi cập nhật tồn kho: " + e.getMessage());
            return false;
        }
    }

    // Hàm xóa tồn kho
    public boolean xoaTonKho(String maTonKho) {
        String sql = "DELETE FROM TONKHO WHERE MaTonKho = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, maTonKho);
            return pst.executeUpdate() > 0; // Trả về true nếu thành công
        } catch (Exception e) {
            System.out.println("Lỗi xóa tồn kho DAO: " + e.getMessage());
            return false;
        }
    }

}