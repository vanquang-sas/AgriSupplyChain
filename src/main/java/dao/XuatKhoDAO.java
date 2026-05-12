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

    // Xác nhận xuất kho GỘP theo Đơn Hàng (MaDH được lưu tạm trong thuộc tính MaXK
    // của DTO)
    public void xacNhanXuatKho(XuatKhoDTO dto) throws SQLException {
        String sql = "{call SP_XACNHAN_XUATKHO(?, ?)}";

        try (Connection conn = DBConnection.getConnection();
                CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, dto.getMaXK()); // Nhận MaDH
            cs.setString(2, dto.getMaNV()); // Nhận MaNV
            cs.execute();
        }
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
                            rs.getString(1), // Mã đơn hàng (MaDH)
                            rs.getString(2) != null ? rs.getString(2) : "Lỗi tên SP",
                            rs.getInt(3),
                            rs.getString(4) != null ? rs.getString(4) : "Không có HSD",
                            rs.getString(5) != null ? rs.getString(5) : "",
                            "", // Dành cho Combobox Nhân viên
                            rs.getString(7) // Trạng thái
                    };
                    list.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Object[] errorRow = { "LỖI SQL!", e.getMessage(), 0, "ERROR", "ERROR", "", "Lỗi kết nối" };
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

    // L\u1ea5y chi ti\u1ebft \u0111\u01a1n h\u00e0ng k\u00e8m preview v\u1ecb
    // tr\u00ed + HSD theo FEFO (kh\u00f4ng ghi DB)
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
                            rs.getString("ViTri") != null ? rs.getString("ViTri") : "Kh\u00f4ng r\u00f5",
                            rs.getString("NgayHetHan") != null ? rs.getString("NgayHetHan") : "Ch\u01b0a c\u00f3"
                    });
                }
            }
        }
        return list;
    }

}