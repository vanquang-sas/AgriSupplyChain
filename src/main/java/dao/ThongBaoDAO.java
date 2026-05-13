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
import java.sql.CallableStatement;

public class ThongBaoDAO {

    // 1. Sửa lại hàm lấy danh sách: Lấy thêm cột TrangThaiTB
    public ArrayList<Object[]> getDanhSachThongBao() {
        ArrayList<Object[]> list = new ArrayList<>();
        // Lấy thêm TrangThaiTB
        String sql = "SELECT LoaiTB, NoiDung, TGTao, TrangThaiTB FROM THONGBAO ORDER BY TGTao DESC";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Object[] row = new Object[4]; // Tăng lên 4 phần tử
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

    // 2. Thêm hàm đếm số lượng thông báo chưa đọc (để hiển thị lên nút chuông)
    public int getSoLuongChuaDoc() {
        String sql = "SELECT COUNT(*) FROM THONGBAO WHERE TrangThaiTB = 0";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            if (rs.next())
                return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 3. Thêm hàm cập nhật tất cả thành "Đã đọc"
    public void danhDauDaDocTatCa() {
        String sql = "UPDATE THONGBAO SET TrangThaiTB = 1 WHERE TrangThaiTB = 0";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 4. Gọi Procedure kiểm tra hết hạn (để tự động sinh thông báo mới nếu cần)
    public void kiemTraHetHan() {
        String sql = "{CALL SP_KIEMTRA_HETHAN_THONGBAO()}";
        try (Connection con = DBConnection.getConnection();
                CallableStatement cst = con.prepareCall(sql)) {
            cst.execute();
            System.out.println("✅ Gọi SP_KIEMTRA_HETHAN_THONGBAO thành công");
        } catch (Exception e) {
            System.err.println("❌ Lỗi gọi SP_KIEMTRA_HETHAN_THONGBAO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}