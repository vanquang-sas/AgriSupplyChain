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

    // TAB 1: Lấy danh sách CHỜ GIAO (Trạng thái 'Chờ giao hàng' và chưa có ai nhận đơn)
    public List<DonHangDTO> getDanhSachChoGiao() {
        List<DonHangDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM DONHANG WHERE TRANGTHAIDH = N'Chờ giao hàng' AND MANV IS NULL ORDER BY TGDAT ASC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()){ list.add(mapRowToDTO(rs)); }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // TAB 2: Lấy danh sách ĐÃ NHẬN (Trạng thái vẫn là 'Chờ giao hàng' nhưng đã gán mã nhân viên cụ thể)
    //  Sửa lại thành trạng thái đang giao và chạy lại cái procedure trong thư mục database: SP_GIAOHANG_THANHCONG, SP_GIAOHANG_THATBAI, SP_XACNHAN_GIAOHANG
    public List<DonHangDTO> getDanhSachDaNhan(String MaNV) {
        List<DonHangDTO> list = new ArrayList<>();
        // String sql = "SELECT * FROM DONHANG WHERE TRIM(MANV) = ? AND TRANGTHAIDH = N'Chờ giao hàng' ORDER BY TGDAT ASC";
        String sql = "SELECT * FROM DONHANG WHERE TRIM(MANV) = ? AND TRANGTHAIDH = N'Đang giao' ORDER BY TGDAT ASC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, MaNV.trim());
            ResultSet rs = ps.executeQuery();
            while(rs.next()){ list.add(mapRowToDTO(rs)); }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Thực thi Nhận đơn đi giao
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
    
    // Thực thi Giao hàng thành công
    public boolean giaoHangThanhCong(String MaDH, String MaNV) {
        String sql = "UPDATE DONHANG SET TRANGTHAIDH = N'Hoàn thành', TGGIAOTT = SYSDATE, TRANGTHAITT = 1 WHERE MADH = ? AND TRIM(MANV) = ? AND TRANGTHAIDH = N'Đang giao'";
        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setString(1, MaDH);
            ps.setString(2, MaNV.trim());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Thực thi Giao hàng thất bại
    public boolean giaoHangThatBai(String maDH, String maNV, String lyDoHuy) {
        String sql = "UPDATE DONHANG SET TRANGTHAIDH = N'Đã huỷ', TGGIAOTT = SYSDATE, LYDOHUY = ? WHERE MADH = ? AND TRIM(MANV) = ? AND TRANGTHAIDH = N'Đang giao'";
        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setString(1, lyDoHuy);
            ps.setString(2, maDH);
            ps.setString(3, maNV.trim());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // TAB 3: Lịch sử giao hàng (Đơn đã chuyển sang trạng thái cuối cùng)
    public List<DonHangDTO> getLichSuGiaoHang(String MaNV) {
        List<DonHangDTO> list = new ArrayList<>();
        String sql = """
        SELECT * FROM DONHANG
        WHERE TRIM(MANV) = ?
        AND TRANGTHAIDH IN (N'Hoàn thành', N'Đã huỷ')
        ORDER BY TGDAT DESC
        """;
        try (
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setString(1, MaNV.trim());
            ResultSet rs = ps.executeQuery();

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
                dh.setPhuongThucTT(rs.getString("PHUONGTHUCTT"));
                list.add(dh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private DonHangDTO mapRowToDTO(ResultSet rs) throws Exception {
        DonHangDTO dh = new DonHangDTO();
        dh.setMaDH(rs.getString("MADH"));
        dh.setMaKH(rs.getString("MAKH"));
        dh.setMaNV(rs.getString("MANV"));
        dh.setDiaChiGiaoHang(rs.getString("DIACHIGIAOHANG"));
        dh.setTgDat(rs.getDate("TGDAT"));
        dh.setTgGiaoYC(rs.getDate("TGGIAOYC"));
        dh.setTgGiaoTT(rs.getDate("TGGIAOTT"));
        dh.setTongTien(rs.getDouble("TONGTIEN"));
        dh.setTrangThaiDH(rs.getString("TRANGTHAIDH"));
        dh.setPhuongThucTT(rs.getString("PHUONGTHUCTT"));
        return dh;
    }
}