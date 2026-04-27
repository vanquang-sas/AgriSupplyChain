package dao;

import dto.DonHangDTO;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonHangDAO {

    // 1. Tính phí vận chuyển
    public double tinhPhiVanChuyen(String diaChi) {
        double phi = 0;
        String sql = "{ ? = call FN_TINH_PHIVANCHUYEN(?) }";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setString(2, diaChi);
            cs.execute();
            phi = cs.getDouble(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return phi;
    }

    // 2. Tạo đơn hàng mới
    public boolean themDonHang(DonHangDTO dh) {
        String sql = "{ call SP_THEM_DH(?,?,?,?,?,?,?,?,?,?) }";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, dh.getMaDH());
            cs.setString(2, dh.getMaKH());
            cs.setString(3, dh.getDiaChiGiaoHang());
            // dùng đúng tên trong DTO của bạn: getTgGiaoDK()
            cs.setDate(4, dh.getTgGiaoDK() != null
                    ? new java.sql.Date(dh.getTgGiaoDK().getTime()) : null);
            cs.setDouble(5, dh.getPhiVanChuyen());
            cs.setDouble(6, dh.getTongTienHang());   // field mới đã thêm
            cs.setDouble(7, 0);                       // giamGia tạm = 0
            cs.setDouble(8, dh.getTongTien());
            // tính trangThaiTT từ phuongThucTT: COD=0, còn lại=1
            cs.setInt(9, "COD".equals(dh.getPhuongThucTT()) ? 0 : 1);
            cs.setString(10, dh.getPhuongThucTT());   // field mới đã thêm
            cs.execute();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Thêm chi tiết đơn hàng
    public boolean themChiTietDonHang(String maDH, String maSP, int soLuong) {
        String sql = "{ call SP_THEM_CTDH(?,?,?) }";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, maDH);
            cs.setString(2, maSP);
            cs.setInt(3, soLuong);
            cs.execute();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Hủy đơn hàng
    public boolean huyDonHang(String maDH) {
        String sql = "{ call SP_XOA_DH(?) }";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, maDH);
            cs.execute();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. Lấy danh sách đơn hàng theo khách hàng
    public List<DonHangDTO> layDonHangTheoKH(String maKH) {
        List<DonHangDTO> list = new ArrayList<>();
        String sql = "{ call SP_LAY_DH_THEO_KH(?) }";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, maKH);
            boolean hasRS = cs.execute();
            if (hasRS) {
                try (ResultSet rs = cs.getResultSet()) {
                    while (rs.next()) {
                        DonHangDTO dh = new DonHangDTO();
                        dh.setMaDH(rs.getString("MaDH"));
                        dh.setMaKH(rs.getString("MaKH"));
                        dh.setMaNV(rs.getString("MaNV"));
                        dh.setDiaChiGiaoHang(rs.getString("DiaChiGiaoHang"));
                        dh.setTgDat(rs.getDate("TGDat"));
                        // dùng đúng tên DTO của bạn: setTgGiaoDK
                        dh.setTgGiaoDK(rs.getDate("TGGiaoYC"));
                        dh.setPhiVanChuyen(rs.getDouble("PhiVanChuyen"));
                        dh.setTongTienHang(rs.getDouble("TongTienHang"));
                        dh.setTongTien(rs.getDouble("TongTien"));
                        dh.setTrangThaiDH(rs.getString("TrangThaiDH"));
                        dh.setPhuongThucTT(rs.getString("PhuongThucTT"));
                        list.add(dh);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}