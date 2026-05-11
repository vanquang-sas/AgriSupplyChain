package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dto.DonHangDTO;
import util.DBConnection;

public class GiaoHangDAO {

    public GiaoHangDAO() {}

     // 1. Lấy danh sách đơn hàng chờ giao
    public List<DonHangDTO> getDanhSachChoGiao(){

        List<DonHangDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM DONHANG WHERE TRANGTHAIDH = 'Chờ giao hàng'";
        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ){
            while(rs.next()){
                DonHangDTO dh = new DonHangDTO();
                dh.setMaDH(rs.getString("MADH"));
                dh.setMaKH(rs.getString("MAKH"));
                dh.setMaNV(rs.getString("MANV"));
                dh.setDiaChiGiaoHang(rs.getString("DIACHIGIAOHANG"));
                dh.setTgDat(rs.getDate("TGDAT"));
                dh.setTgGiaoYC(rs.getDate("TGGIAOYC"));
                dh.setTgGiaoTT(rs.getDate("TGGIAOTT"));
                dh.setLyDoHuy(rs.getString("LYDOHUY"));
                dh.setPhiVanChuyen(rs.getDouble("PHIVANCHUYEN"));
                dh.setTongTienHang(rs.getDouble("TONGTIENHANG"));
                dh.setGiamGia(rs.getDouble("GIAMGIA"));
                dh.setTongTien(rs.getDouble("TONGTIEN"));
                dh.setTrangThaiDH(rs.getString("TRANGTHAIDH"));
                dh.setTrangThaiTT(rs.getInt("TRANGTHAITT"));
                // dh.setDanhSachSP(rs.getString("DANHSACHSP"));
                dh.setPhuongThucTT(rs.getString("PHUONGTHUCTT"));

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
    public boolean giaoHangThanhCong(String MaDH, String MaNV) {
        String sql = "{call SP_GIAOHANG_THANHCONG(?,?)}";

        try (
            Connection con = DBConnection.getConnection();
            CallableStatement cs = con.prepareCall(sql);
        ) {
            cs.setString(1, MaDH);
            cs.setString(2, MaNV);
            cs.execute();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 4. Giao hàng thất bại
    public boolean giaoHangThatBai(String maDH) {
        String sql = "UPDATE DONHANG SET TRANGTHAIDH =  'Đã huỷ' WHERE MADH = ?";

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
            WHERE TRANGTHAIDH IN ('Hoàn thành', 'Đã huỷ')
            ORDER BY TGDAT DESC
        """;

        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                DonHangDTO dh = new DonHangDTO();
                dh.setMaDH(rs.getString("MADH"));
                dh.setMaKH(rs.getString("MAKH"));
                dh.setMaNV(rs.getString("MANV"));
                dh.setDiaChiGiaoHang(rs.getString("DIACHIGIAOHANG"));
                dh.setTgDat(rs.getDate("TGDAT"));
                dh.setTgGiaoYC(rs.getDate("TGGIAOYC"));
                dh.setTgGiaoTT(rs.getDate("TGGIAOTT"));
                dh.setLyDoHuy(rs.getString("LYDOHUY"));
                dh.setPhiVanChuyen(rs.getDouble("PHIVANCHUYEN"));
                dh.setTongTienHang(rs.getDouble("TONGTIENHANG"));
                dh.setGiamGia(rs.getDouble("GIAMGIA"));
                dh.setTongTien(rs.getDouble("TONGTIEN"));
                dh.setTrangThaiDH(rs.getString("TRANGTHAIDH"));
                dh.setTrangThaiTT(rs.getInt("TRANGTHAITT"));
                // dh.setDanhSachSP(rs.getString("DANHSACHSP"));
                dh.setPhuongThucTT(rs.getString("PHUONGTHUCTT"));

                list.add(dh);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}
