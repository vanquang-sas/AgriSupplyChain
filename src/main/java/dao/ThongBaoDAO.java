/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.util.ArrayList;

public class ThongBaoDAO {

    // 1. Lấy danh sách thông báo (mới nhất lên đầu)
    public ArrayList<Object[]> getDanhSachThongBao() {
        ArrayList<Object[]> list = new ArrayList<>();
        // Lưu ý: Sử dụng prefix AGRIAPP nếu cần thiết, ở đây dùng mặc định theo schema kết nối
        String sql = "SELECT LoaiTB, NoiDung, TGTao, TrangThaiTB FROM THONGBAO ORDER BY TGTao DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getString("LoaiTB");
                row[1] = rs.getString("NoiDung");
                row[2] = rs.getTimestamp("TGTao");
                row[3] = rs.getInt("TrangThaiTB"); // 0: chưa đọc, 1: đã đọc
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Đếm số lượng thông báo chưa đọc (để hiển thị badge trên icon)
    public int getSoLuongChuaDoc() {
        String sql = "SELECT COUNT(*) FROM THONGBAO WHERE TrangThaiTB = 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 3. Đánh dấu tất cả thông báo là đã đọc
    public void danhDauDaDocTatCa() {
        String sql = "UPDATE THONGBAO SET TrangThaiTB = 1 WHERE TrangThaiTB = 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 4. Gọi Procedure kiểm tra tồn kho & hết hạn để tự động sinh thông báo
    public void kiemTraHeThong() {
        // Procedure SP_KIEMTRA_HETHAN_THONGBAO trong file 05_Procedures.sql
        String sql = "{CALL SP_KIEMTRA_HETHAN_THONGBAO()}";
        try (Connection con = DBConnection.getConnection();
             CallableStatement cst = con.prepareCall(sql)) {
            cst.execute();
            System.out.println("DEBUG: [ThongBaoDAO] Đã chạy SP_KIEMTRA_HETHAN_THONGBAO");
        } catch (Exception e) {
            System.err.println("DEBUG: [ThongBaoDAO] Lỗi gọi Procedure: " + e.getMessage());
            e.printStackTrace();
        }
    }
}