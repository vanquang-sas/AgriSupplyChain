package dao;

import dto.DonHangDTO;
import util.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DonHangDAO {

    public List<DonHangDTO> getLichSuDonHang(String maKH) throws Exception {
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;
        List<DonHangDTO> danhSachDH = new ArrayList<>();

        try {
            // 1. Lấy kết nối tới Database
            conn = DBConnection.getConnection();
            if (conn == null) {
                throw new Exception("Không thể kết nối tới Database!");
            }

            // 2. Chuẩn bị lệnh gọi Procedure
            String sql = "{ call SP_LAY_DS_DONHANG_BY_KH(?, ?) }";
            cstmt = conn.prepareCall(sql);

            // 3. Đặt giá trị tham số đầu vào
            cstmt.setString(1, maKH);

            // 4. Đăng ký tham số đầu ra (SYS_REFCURSOR)
            cstmt.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR);

            // 5. Thực thi Procedure
            cstmt.execute();

            // 6. Lấy ResultSet từ tham số đầu ra
            rs = (ResultSet) cstmt.getObject(2);

            // 7. Duyệt qua từng dòng dữ liệu và map vào DTO
            while (rs != null && rs.next()) {
                DonHangDTO donHangDTO = new DonHangDTO();
                
                // Map dữ liệu từ ResultSet
                donHangDTO.setMaDH(rs.getString("MaDH"));
                donHangDTO.setMaKH(rs.getString("MaKH"));
                donHangDTO.setTgDat(rs.getDate("TGDat"));
                donHangDTO.setTongTien(rs.getDouble("TongTien"));
                donHangDTO.setTrangThaiDH(rs.getString("TrangThaiDH"));
                donHangDTO.setTrangThaiTT(rs.getInt("TrangThaiTT"));
                
                // Lấy chuỗi danh sách sản phẩm từ hàm LISTAGG
                // Ví dụ: "2 Xoài, 1 Chuối"
                donHangDTO.setDanhSachSP(rs.getString("DanhSachSP"));
                
                danhSachDH.add(donHangDTO);
            }

            System.out.println("✓ Lấy danh sách đơn hàng thành công. Tổng số: " + danhSachDH.size());
            return danhSachDH;

        } catch (SQLException e) {
            System.err.println("✗ Lỗi SQL khi gọi SP_LAY_DS_DONHANG_BY_KH:");
            e.printStackTrace();
            throw new Exception("Lỗi cơ sở dữ liệu: " + e.getMessage(), e);

        } finally {
            // 8. Đóng tài nguyên
            try {
                if (rs != null) rs.close();
                if (cstmt != null) cstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    
    public void xoaDonHang(String maDH) throws Exception {
        Connection conn = null;
        CallableStatement cstmt = null;

        try {
            // 1. Lấy kết nối tới Database
            conn = DBConnection.getConnection();
            if (conn == null) {
                throw new Exception("Không thể kết nối tới Database!");
            }

            // 2. Chuẩn bị lệnh gọi Procedure
            String sql = "{ call SP_XOA_DH(?) }";
            cstmt = conn.prepareCall(sql);

            // 3. Đặt giá trị tham số đầu vào
            cstmt.setString(1, maDH);

            // 4. Thực thi Procedure
            cstmt.execute();

            System.out.println("✓ Xóa đơn hàng [" + maDH + "] thành công");

        } catch (SQLException e) {
            System.err.println("✗ Lỗi SQL khi gọi SP_XOA_DH:");
            e.printStackTrace();
            
            // Nếu là lỗi từ Application_Error của Procedure, ném exception với message rõ ràng
            throw new Exception("Lỗi khi xóa đơn hàng: " + e.getMessage(), e);

        } finally {
            // 5. Đóng tài nguyên
            try {
                if (cstmt != null) cstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
