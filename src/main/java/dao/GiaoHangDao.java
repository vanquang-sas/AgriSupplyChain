package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dto.DonHangDTO;
import util.DBConnection;

public class GiaoHangDao {

     // 1. Lấy danh sách đơn hàng chờ giao
    public List<DonHangDTO> getDanhSachChoGiao(){

        List<DonHangDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM DONHANG WHERE TrangThaiDH = 'Chờ giao hàng'";
        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ){
            while(rs.next()){
                DonHangDTO dh = new DonHangDTO();
                dh.setMaDH(rs.getString("MaDH"));
                dh.setMaKH(rs.getString("MaKH"));
                dh.setMaNV(rs.getString("MaNV"));
                dh.setDiaChiGiaoHang(rs.getString("DiaChiGiaoHang"));
                dh.setTgDat(rs.getDate("TGDat"));
                dh.setTgGiaoYC(rs.getDate("TGGiaoYC"));
                dh.setPhiVanChuyen(rs.getDouble("PhiVanChuyen"));
                dh.setTongTien(rs.getDouble("TongTien"));
                dh.setTrangThaiDH(rs.getString("TrangThaiDH"));

                list.add(dh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Nhận đơn đi giao
    public boolean nhanDonGiao(String maDH, String maNV) {
        String sql = "{call SP_XACNHAN_GIAOHANG(?, ?)}";

        try (
            Connection con = DBConnection.getConnection();
            CallableStatement cs = con.prepareCall(sql);
        ) {
            cs.setString(1, maDH);
            cs.setString(2, maNV);

            cs.execute();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 3. Giao hàng thành công
    public boolean giaoHangThanhCong(String maDH) {
        String sql = "{call SP_GIAOHANG_THANHCONG(?)}";

        try (
            Connection con = DBConnection.getConnection();
            CallableStatement cs = con.prepareCall(sql);
        ) {
            cs.setString(1, maDH);
            cs.execute();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 4. Giao hàng thất bại
    public boolean giaoHangThatBai(String maDH) {
        String sql = "UPDATE DONHANG SET TrangThaiDH = 'Giao thất bại' WHERE MaDH = ?";

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setString(1, maDH);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 5. Lịch sử giao hàng
    public List<DonHangDTO> getLichSuGiaoHang() {
        List<DonHangDTO> list = new ArrayList<>();

        String sql = """
            SELECT * FROM DONHANG
            WHERE TrangThaiDH IN ('Hoàn thành', 'Giao thất bại')
            ORDER BY TGDat DESC
        """;

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                DonHangDTO dh = new DonHangDTO();

                dh.setMaDH(rs.getString("MaDH"));
                dh.setMaKH(rs.getString("MaKH"));
                dh.setMaNV(rs.getString("MaNV"));
                dh.setDiaChiGiaoHang(rs.getString("DiaChiGiaoHang"));
                dh.setTgDat(rs.getDate("TGDat"));
                dh.setTgGiaoYC(rs.getDate("TGGiaoYC"));
                dh.setPhiVanChuyen(rs.getDouble("PhiVanChuyen"));
                dh.setTongTien(rs.getDouble("TongTien"));
                dh.setTrangThaiDH(rs.getString("TrangThaiDH"));

                list.add(dh);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}
