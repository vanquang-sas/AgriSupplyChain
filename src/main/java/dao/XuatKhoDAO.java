package dao;

import dto.XuatKhoDTO;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class XuatKhoDAO {
    public void yeuCauXuatKho(String maDH) throws SQLException {
        String sql = "{call SP_YEUCAU_XUATKHO(?)}";
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, maDH);
            cs.execute();
        }
    }

    public ArrayList<Object[]> getDanhSachDonHangChoXuat() {
        ArrayList<Object[]> list = new ArrayList<>();
        String sql = """
            SELECT DH.MaDH,
                   NVL(KH.TenKH, N'Khách lẻ') AS TenKH,
                   COUNT(CTDH.MaSP) AS SoLuongSP,
                   SUM(CTDH.SoLuong) AS TongSL,
                   DH.TrangThaiDH
            FROM DONHANG DH
            JOIN CHITIETDONHANG CTDH ON DH.MaDH = CTDH.MaDH
            LEFT JOIN KHACHHANG KH ON DH.MaKH = KH.MaKH
            WHERE DH.TrangThaiDH IN (N'Đã đặt', N'Chờ xử lý')
              AND NOT EXISTS (
                  SELECT 1
                  FROM XUATKHO XK
                  JOIN CHITIETDONHANG CT ON XK.MaCTDH = CT.MaCTDH
                  WHERE CT.MaDH = DH.MaDH
                    AND XK.TrangThaiXK = N'Tạm giữ'
              )
            GROUP BY DH.MaDH, KH.TenKH, DH.TrangThaiDH
            ORDER BY DH.MaDH ASC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    //tự thêm đơn từ donhang vào xuatkho với trạng thái tạm giữ
    public void yeuCauXuatKhoChoDonHangMoi() throws SQLException {
    String selectSql = """
        SELECT DH.MaDH
        FROM DONHANG DH
        WHERE DH.TrangThaiDH IN (N'Đã đặt', N'Chờ xử lý')
          AND NOT EXISTS (
              SELECT 1
              FROM XUATKHO XK
              JOIN CHITIETDONHANG CT ON XK.MaCTDH = CT.MaCTDH
              WHERE CT.MaDH = DH.MaDH
                AND XK.TrangThaiXK = N'Tạm giữ'
          )
    """;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(selectSql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            try (CallableStatement cs = conn.prepareCall("{call SP_YEUCAU_XUATKHO(?)}")) {
                cs.setString(1, rs.getString("MaDH"));
                cs.execute();
             }
            }
        }
    }
    // Xác nhận xuất kho GỘP theo Đơn Hàng (MaDH được lưu tạm trong thuộc tính MaXK của DTO)
    public void xacNhanXuatKho(XuatKhoDTO dto) throws SQLException {
        String sql = "{call SP_XACNHAN_XUATKHO(?, ?)}"; 

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, dto.getMaXK()); // Nhận MaDH
            cs.setString(2, dto.getMaNV()); // Nhận MaNV
            cs.execute();
        }
    }

    // Lấy danh sách cần soạn hàng
    public ArrayList<Object[]> getDanhSachSoanHang() {
        ArrayList<Object[]> list = new ArrayList<>();

        // Lấy Mã đơn hàng (MaDH) thay vì Mã xuất kho, sắp xếp chuẩn FEFO
        String sql = """
            SELECT 
                DH.MaDH, 
                SP.TenSP, 
                XK.SLXuat, 
                TO_CHAR(TK.TGHetHan, 'DD/MM/YYYY') AS NgayHetHan, 
                TK.ViTri, 
                ' ' AS NhanVien, 
                XK.TrangThaiXK
            FROM XUATKHO XK
            LEFT JOIN CHITIETDONHANG CTDH ON XK.MaCTDH = CTDH.MaCTDH
            LEFT JOIN DONHANG DH ON CTDH.MaDH = DH.MaDH
            LEFT JOIN SANPHAM SP ON CTDH.MaSP = SP.MaSP
            LEFT JOIN TONKHO TK ON XK.MaTonKho = TK.MaTonKho
            WHERE XK.TrangThaiXK = N'Tạm giữ'
            ORDER BY DH.MaDH ASC, TK.TGHetHan ASC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                    rs.getString(1),  // Mã đơn hàng (MaDH)
                    rs.getString(2) != null ? rs.getString(2) : "Lỗi tên SP", 
                    rs.getInt(3),
                    rs.getString(4) != null ? rs.getString(4) : "Không có HSD",  // HSD
                    rs.getString(5) != null ? rs.getString(5) : "", // Vị trí
                    "",               // Dành cho Combobox Nhân viên
                    rs.getString(7)   // Trạng thái
                };
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace(); 
            Object[] errorRow = {"LỖI SQL!", e.getMessage(), 0, "ERROR", "ERROR", "", "Lỗi kết nối"};
            list.add(errorRow);
        }
        return list;
    }

    // Lấy nhân viên kho
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
    
}