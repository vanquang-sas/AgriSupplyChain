package dao;

import dto.XuatKhoDTO;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class XuatKhoDAO {
    
    // Tạo yêu cầu xuất kho (phân bổ hàng theo FEFO)
    public void yeuCauXuatKho(String maDH) throws SQLException {
        String sql = "{call SP_YEUCAU_XUATKHO(?)}";
        try (Connection conn = DBConnection.getConnection();
                CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, maDH);
            cs.execute();
        }
    }

    // Lấy danh sách đơn hàng chờ xuất kho
    public ArrayList<Object[]> getDanhSachDonHangChoXuat() {
        ArrayList<Object[]> list = new ArrayList<>();
        String sql = "{ ? = call FN_GET_DS_DONHANG_CHO_XUAT() }";

        try (Connection conn = DBConnection.getConnection();
                CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Object[] row = {
                            rs.getString("MaDH"),
                            rs.getString("TenKH"),
                            rs.getInt("SoLuongSP"),
                            rs.getInt("TongSL"),
                            rs.getString("TrangThaiDH")
                    };
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy danh sách đơn hàng đã xuất kho (trạng thái Chờ giao hàng hoặc Hoàn thành)
    public ArrayList<Object[]> getDanhSachDonHangDaXuat() {
        ArrayList<Object[]> list = new ArrayList<>();
        String sql = "SELECT DH.MaDH, NVL(KH.TenKH, N'Khách lẻ') as TenKH, " +
                     "COUNT(CTDH.MaSP) as SoLuongSP, SUM(CTDH.SoLuong) as TongSL, DH.TrangThaiDH " +
                     "FROM DONHANG DH " +
                     "JOIN CHITIETDONHANG CTDH ON DH.MaDH = CTDH.MaDH " +
                     "LEFT JOIN KHACHHANG KH ON DH.MaKH = KH.MaKH " +
                     "WHERE DH.TrangThaiDH IN (N'Chờ giao hàng', N'Đang giao', N'Hoàn thành', N'Chờ thanh toán') " +
                     "GROUP BY DH.MaDH, KH.TenKH, DH.TrangThaiDH " +
                     "ORDER BY DH.MaDH DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("MaDH"),
                    rs.getString("TenKH"),
                    rs.getInt("SoLuongSP"),
                    rs.getInt("TongSL"),
                    rs.getString("TrangThaiDH")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Đếm số đơn hàng thiếu tồn kho
    public int getSoDonHangThieuTonKho() {
        String sql = "{ ? = call FN_GET_SO_DONHANG_THIEU_TONKHO() }";
        try (Connection conn = DBConnection.getConnection();
                CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.execute();
            return cs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Xác nhận xuất kho (Gán nhân viên soạn hàng và trừ tồn kho thực tế)
    public void xacNhanXuatKho(XuatKhoDTO dto) throws SQLException {
        String sql = "{call SP_XACNHAN_XUATKHO(?, ?)}";

        try (Connection conn = DBConnection.getConnection();
                CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, dto.getMaXK()); // MaDH
            cs.setString(2, dto.getMaNV()); // MaNV
            cs.execute();
        }
    }

    public boolean checkDonHangDaXuat(String maDH) throws SQLException {
        String sql = "SELECT TrangThaiDH FROM DONHANG WHERE MaDH = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             ps.setString(1, maDH);
             try (ResultSet rs = ps.executeQuery()) {
                 if (rs.next()) {
                      String status = rs.getString("TrangThaiDH");
                      if ("Chờ giao hàng".equals(status) || "Đang giao".equals(status) || "Hoàn thành".equals(status) || "Chờ thanh toán".equals(status)) {
                          return true;
                      }
                 }
             }
        }
        return false;
    }

    // Lấy danh sách cần soạn hàng (FEFO)
    public ArrayList<Object[]> getDanhSachSoanHang() {
        ArrayList<Object[]> list = new ArrayList<>();
        String sql = "{ ? = call FN_GET_DS_SOANHANG() }";

        try (Connection conn = DBConnection.getConnection();
                CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Object[] row = {
                            rs.getString(1), // MaDH
                            rs.getString(2) != null ? rs.getString(2) : "Lỗi tên SP",
                            rs.getInt(3),
                            rs.getString(4) != null ? rs.getString(4) : "Không có HSD",
                            rs.getString(5) != null ? rs.getString(5) : "",
                            "", // Nhân viên
                            rs.getString(7) // Trạng thái
                    };
                    list.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy danh sách nhân viên kho
    public List<String> getAllMaNhanVien() throws Exception {
        List<String> list = new ArrayList<>();
        String sql = "SELECT MaNV FROM NHANVIEN WHERE ChucVu = N'NV kho' ORDER BY MaNV ASC";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("MaNV"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy chi tiết đơn hàng (phân bổ FEFO)
    public List<Object[]> getChiTietDonHang(String maDH) throws Exception {
        List<Object[]> list = new ArrayList<>();
        String sql = "{ ? = call FN_GET_CHITIET_DONHANG(?) }";
        try (Connection con = DBConnection.getConnection();
                CallableStatement cs = con.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.REF_CURSOR);
            cs.setString(2, maDH);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    list.add(new Object[] {
                            rs.getString("MaSP"),
                            rs.getString("TenSP"),
                            rs.getInt("SoLuongYeuCau"),
                            rs.getInt("SoLuongXuat"),
                            rs.getString("ViTri") != null ? rs.getString("ViTri") : "Không rõ",
                            rs.getString("NgayHetHan") != null ? rs.getString("NgayHetHan") : "Chưa có"
                    });
                }
            }
        }
        return list;
    }
}