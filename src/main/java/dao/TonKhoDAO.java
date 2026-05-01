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
        
        // CÂU LỆNH SQL ĐÃ ĐƯỢC CHUẨN HÓA THEO CSDL CỦA BẠN
        String sql = "SELECT tk.MaTonKho, sp.TenSP, sp.MaSP, lsp.TenLSP, ncc.TenNCC, " +
                     "tk.SLConLai, sp.DonViTinh, tk.ViTri " +
                     "FROM TONKHO tk " +
                     "LEFT JOIN CHITIETLOHANG ctlh ON tk.MaCTLH = ctlh.MaCTLH " +
                     "LEFT JOIN LOHANG lh ON ctlh.MaLH = lh.MaLH " + // Thêm cầu nối qua bảng LOHANG
                     "LEFT JOIN SANPHAM sp ON ctlh.MaSP = sp.MaSP " +
                     "LEFT JOIN LOAISANPHAM lsp ON sp.MaLSP = lsp.MaLSP " + // Sửa MaLoai thành MaLSP
                     "LEFT JOIN NHACUNGCAP ncc ON lh.MaNCC = ncc.MaNCC"; 
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
             
            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = rs.getString("MaTonKho");      // Mã SKU
                row[1] = rs.getString("TenSP");         // Tên Sản Phẩm (Đổi thành TenSP)
                row[2] = rs.getString("MaSP");          // Mã SP (Dùng để móc với ảnh trong thư mục)
                row[3] = rs.getString("TenLSP");        // Tên Loại (Đổi thành TenLSP)
                row[4] = rs.getString("TenNCC");        // Nhà Cung Cấp (Đổi thành TenNCC)
                row[5] = rs.getDouble("SLConLai");      // Số lượng
                row[6] = rs.getString("DonViTinh");     // ĐVT
                row[7] = rs.getString("ViTri");         // Vị trí
                
                ketQua.add(row);
            }
        } catch (Exception e) {
            System.out.println("Lỗi tại TonKhoDAO.getDanhSachTonKho: " + e.getMessage());
            e.printStackTrace();
        }
        return ketQua;
    }
    
    // Hàm cập nhật số lượng
    public boolean capNhatSoLuong(String maTonKho, double soLuongMoi) {
        String sql = "UPDATE TONKHO SET SLConLai = ? WHERE MaTonKho = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setDouble(1, soLuongMoi);
            pst.setString(2, maTonKho);
            return pst.executeUpdate() > 0; // Trả về true nếu thành công
        } catch (Exception e) {
            System.out.println("Lỗi sửa số lượng DAO: " + e.getMessage());
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