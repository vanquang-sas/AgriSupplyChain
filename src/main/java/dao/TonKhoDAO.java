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
                "LEFT JOIN CHITIETLOHANG ctlh ON tk.MaLH = ctlh.MaLH AND tk.MaSP = ctlh.MaSP " +
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

    // Lấy danh sách tổng hợp (nhóm theo Mã Sản Phẩm)
    public ArrayList<Object[]> getDanhSachTonKhoTongHop() {
        ArrayList<Object[]> ketQua = new ArrayList<>();
        String sql = "SELECT sp.HinhAnh, sp.TenSP, sp.MaSP, sp.DonViTinh, " +
                "SUM(tk.SLConLai) as TongSoLuong, " +
                "SUM(tk.SLKhaDung) as TongKhaDung, " +
                "CASE " +
                "   WHEN SUM(CASE WHEN tk.TrangThai = N'Hết hạn' THEN 1 ELSE 0 END) > 0 THEN N'Hết hạn' " +
                "   WHEN SUM(CASE WHEN tk.TrangThai = N'Sắp hết hạn' THEN 1 ELSE 0 END) > 0 THEN N'Sắp hết hạn' " +
                "   ELSE N'Còn hạn' " +
                "END as TrangThaiTongHop " +
                "FROM TONKHO tk " +
                "JOIN CHITIETLOHANG ctlh ON tk.MaLH = ctlh.MaLH AND tk.MaSP = ctlh.MaSP " +
                "JOIN SANPHAM sp ON ctlh.MaSP = sp.MaSP " +
                "GROUP BY sp.HinhAnh, sp.TenSP, sp.MaSP, sp.DonViTinh";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getString("HinhAnh");
                row[1] = rs.getString("TenSP");
                row[2] = rs.getString("MaSP");
                row[3] = rs.getString("DonViTinh");
                row[4] = rs.getDouble("TongSoLuong");
                row[5] = rs.getDouble("TongKhaDung");
                row[6] = rs.getString("TrangThaiTongHop");
                ketQua.add(row);
            }
        } catch (Exception e) {
            System.out.println("Lỗi tại TonKhoDAO.getDanhSachTonKhoTongHop: " + e.getMessage());
            e.printStackTrace();
        }
        return ketQua;
    }

    // Lấy chi tiết tồn kho của 1 sản phẩm
    public ArrayList<Object[]> getChiTietTonKhoByMaSP(String maSP) {
        ArrayList<Object[]> ketQua = new ArrayList<>();
        String sql = "SELECT tk.MaTonKho, k.TenKho, tk.ViTri, tk.MaKho, tk.SLConLai, tk.TGNhapKho, tk.TGHetHan, tk.TrangThai "
                +
                "FROM TONKHO tk " +
                "JOIN CHITIETLOHANG ctlh ON tk.MaLH = ctlh.MaLH AND tk.MaSP = ctlh.MaSP " +
                "JOIN KHO k ON tk.MaKho = k.MaKho " +
                "WHERE ctlh.MaSP = ? AND tk.SLConLai > 0 " +
                "ORDER BY tk.TGNhapKho DESC";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, maSP);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[7];
                    String tenKho = rs.getString("TenKho");
                    String viTri = rs.getString("ViTri");
                    row[0] = (tenKho != null ? tenKho : "") + (viTri != null && !viTri.isEmpty() ? " - " + viTri : "");
                    row[1] = rs.getString("MaKho");
                    row[2] = rs.getDouble("SLConLai");

                    java.sql.Timestamp ts = rs.getTimestamp("TGNhapKho");
                    if (ts != null) {
                        row[3] = new java.text.SimpleDateFormat("dd-MM-yyyy").format(ts);
                    } else {
                        row[3] = "";
                    }

                    java.sql.Timestamp tsHetHan = rs.getTimestamp("TGHetHan");
                    if (tsHetHan != null) {
                        row[4] = new java.text.SimpleDateFormat("dd-MM-yyyy").format(tsHetHan);
                    } else {
                        row[4] = "";
                    }

                    row[5] = rs.getString("TrangThai");
                    row[6] = rs.getString("MaTonKho");

                    ketQua.add(row);
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi tại TonKhoDAO.getChiTietTonKhoByMaSP: " + e.getMessage());
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
        String sql = "UPDATE TONKHO SET SLConLai = 0 WHERE MaTonKho = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, maTonKho);
            return pst.executeUpdate() > 0; // Trả về true nếu thành công
        } catch (Exception e) {
            System.out.println("Lỗi xóa tồn kho DAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}