package dao;

import dto.ThongKeDTO;
import util.DBConnection;
import oracle.jdbc.OracleTypes; // Cần import OracleTypes để dùng cursor

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO {

    public List<ThongKeDTO.DoanhThu> getDoanhThuTheoNam(int nam) {
        List<ThongKeDTO.DoanhThu> list = new ArrayList<>();
        String sql = "{call SP_THONGKE_DOANHTHU_NAM(?, ?)}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, nam);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    list.add(new ThongKeDTO.DoanhThu(rs.getInt("Thang"), rs.getDouble("DoanhThu")));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<ThongKeDTO.TopSanPham> getThongKeSanPham(int limit, String type, java.util.Date from, java.util.Date to) {
        List<ThongKeDTO.TopSanPham> list = new ArrayList<>();
        String sql = "{call SP_THONGKE_SANPHAM(?, ?, ?, ?, ?)}";
        
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            
            cs.setInt(1, limit);
            cs.setString(2, type);
            cs.setDate(3, new java.sql.Date(from.getTime()));
            cs.setDate(4, new java.sql.Date(to.getTime()));
            cs.registerOutParameter(5, OracleTypes.CURSOR);
            
            cs.execute();
            
            try (ResultSet rs = (ResultSet) cs.getObject(5)) {
                while (rs.next()) {
                    list.add(new ThongKeDTO.TopSanPham(
                        rs.getString("TenSP"), 
                        rs.getInt("TongSoLuong")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ThongKeDTO.TrangThai> getTyLeTrangThai() {
        List<ThongKeDTO.TrangThai> list = new ArrayList<>();
        String sql = "{call SP_THONGKE_TRANGTHAI_DH(?)}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    list.add(new ThongKeDTO.TrangThai(rs.getString("TrangThaiDH"), rs.getInt("SoLuong")));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ============================================= TRẠNG THÁI ĐƠN HÀNG =============================================
    public List<ThongKeDTO.TrangThai> getThongKeTrangThaiDonHang(java.util.Date from, java.util.Date to) {

        List<ThongKeDTO.TrangThai> list = new ArrayList<>();

        String sql = "{call SP_THONGKE_TRANGTHAI(?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
            CallableStatement cs = conn.prepareCall(sql)) {

            cs.setDate(1, new java.sql.Date(from.getTime()));
            cs.setDate(2, new java.sql.Date(to.getTime()));
            cs.registerOutParameter(3, OracleTypes.CURSOR);

            cs.execute();

            List<ThongKeDTO.TrangThai> temp = new ArrayList<>();
            int total = 0;

            try (ResultSet rs = (ResultSet) cs.getObject(3)) {

                while (rs.next()) {

                    ThongKeDTO.TrangThai dto =
                            new ThongKeDTO.TrangThai();

                    dto.trangThai =
                            rs.getString("TrangThaiDH");

                    dto.soLuong =
                            rs.getInt("SoLuong");

                    total += dto.soLuong;

                    temp.add(dto);
                }
            }

            // Tính tỷ lệ %
            for (ThongKeDTO.TrangThai d : temp) {

                d.tyLe = total > 0
                        ? (double) d.soLuong / total * 100
                        : 0;

                list.add(d);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<ThongKeDTO.TrangThai> getThongKeTrangThaiLoHang(java.util.Date tuNgay, java.util.Date denNgay) {
        List<ThongKeDTO.TrangThai> list = new ArrayList<>();
        String sql = "SELECT TrangThaiLH, COUNT(MaLH) AS SoLuong " +
                     "FROM LOHANG " +
                     "WHERE TGNhap >= ? AND TGNhap <= ? " +
                     "GROUP BY TrangThaiLH";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(tuNgay.getTime()));
            ps.setDate(2, new java.sql.Date(denNgay.getTime()));
            ResultSet rs = ps.executeQuery();
            
            int total = 0;
            List<ThongKeDTO.TrangThai> temp = new ArrayList<>();
            while (rs.next()) {
                ThongKeDTO.TrangThai dto = new ThongKeDTO.TrangThai();
                dto.trangThai = rs.getString("TrangThaiLH");
                dto.soLuong = rs.getInt("SoLuong");
                total += dto.soLuong;
                temp.add(dto);
            }
            
            // Tính tỷ lệ %
            for (ThongKeDTO.TrangThai d : temp) {
                d.tyLe = total > 0 ? (double) d.soLuong / total * 100 : 0;
                list.add(d);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ============================================= THỐNG KÊ TÀI CHÍNH =============================================
    public List<ThongKeDTO.TaiChinh> getThongKeTaiChinh(String type, int period) {
        List<ThongKeDTO.TaiChinh> list = new ArrayList<>();
        String sql = "{call SP_THONGKE_TAICHINH(?, ?, ?)}"; // Chỉnh lại thành 3 tham số
        try (Connection conn = util.DBConnection.getConnection();
            CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, type);
            cs.setInt(2, period);
            cs.registerOutParameter(3, oracle.jdbc.OracleTypes.CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                while (rs.next()) {
                    list.add(new ThongKeDTO.TaiChinh(rs.getString("ThangNam"), rs.getDouble("DoanhThu"), rs.getDouble("ChiPhi")));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<ThongKeDTO.NhanVienThongKe> getDanhSachNhanVienThongKe(String chucVu, int months) {
        List<ThongKeDTO.NhanVienThongKe> list = new ArrayList<>();
        String sql = "SELECT nv.MaNV, nv.TenNV, nv.ChucVu, NVL(SUM(v.SoLuong), 0) AS TongCongViec, nv.Luong " +
                     "FROM NHANVIEN nv " +
                     "LEFT JOIN V_THONGKE_NHANVIEN_CHITIET v ON nv.MaNV = v.MaNV " +
                     "     AND v.ThangThongKe >= ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -?) " +
                     "WHERE (? = 'Tất cả' OR nv.ChucVu = ?) " +
                     "GROUP BY nv.MaNV, nv.TenNV, nv.ChucVu, nv.Luong " +
                     "ORDER BY nv.MaNV";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, months);
            ps.setString(2, chucVu);
            ps.setString(3, chucVu);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ThongKeDTO.NhanVienThongKe dto = new ThongKeDTO.NhanVienThongKe();
                dto.maNV = rs.getString("MaNV");
                dto.tenNV = rs.getString("TenNV");
                dto.chucVu = rs.getString("ChucVu");
                dto.tongCongViec = rs.getInt("TongCongViec");
                dto.luong = rs.getDouble("Luong");
                list.add(dto);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<ThongKeDTO.HieuSuatChiTiet> getHieuSuatNhanVien(String maNV, int months) {
        List<ThongKeDTO.HieuSuatChiTiet> list = new ArrayList<>();
        String sql = "SELECT TO_CHAR(ThangThongKe, 'MM/YYYY') AS ThangNam, " +
                     "SUM(SoLuong) AS SoLuong, SUM(TongGiaTri) AS TongTien " +
                     "FROM V_THONGKE_NHANVIEN_CHITIET " +
                     "WHERE MaNV = ? AND ThangThongKe >= ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -?) " +
                     "GROUP BY ThangThongKe " +
                     "ORDER BY ThangThongKe";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ps.setInt(2, months);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ThongKeDTO.HieuSuatChiTiet dto = new ThongKeDTO.HieuSuatChiTiet();
                dto.thangNam = rs.getString("ThangNam");
                dto.soLuong = rs.getInt("SoLuong");
                dto.tongGiaTri = rs.getDouble("TongTien");
                list.add(dto);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}