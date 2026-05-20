package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dto.ChiTietDonHangDTO;
import dto.DonHangDTO;
import util.DBConnection;


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
                donHangDTO.setTgDat(rs.getTimestamp("TGDat"));
                donHangDTO.setTongTien(rs.getDouble("TongTien"));
                donHangDTO.setTrangThaiDH(rs.getString("TrangThaiDH"));
                donHangDTO.setTrangThaiTT(rs.getInt("TrangThaiTT"));
                
                // Lấy chuỗi danh sách sản phẩm từ hàm LISTAGG
                // Ví dụ: "2 Xoài, 1 Chuối"
                donHangDTO.setDanhSachSP(rs.getString("DanhSachSP"));
                
                danhSachDH.add(donHangDTO);
            }

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

            // 2. Chuẩn bị lệnh gọi Procedure SP_HUY_DH (Cần 2 tham số: MaDH và LyDoHuy)
            String sql = "{ call SP_HUY_DH(?, ?) }";
            cstmt = conn.prepareCall(sql);

            // 3. Đặt giá trị tham số đầu vào
            cstmt.setString(1, maDH);
            cstmt.setString(2, "Khách hàng huỷ đơn từ Lịch sử"); // Truyền lý do huỷ mặc định

            // 4. Thực thi Procedure
            cstmt.execute();

            System.out.println("Hủy đơn hàng [" + maDH + "] thành công");

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi gọi SP_HUY_DH:");
            e.printStackTrace();
            
            // Nếu là lỗi từ Application_Error của Procedure, ném exception với message rõ ràng
            throw new Exception("Lỗi khi hủy đơn hàng: " + e.getMessage(), e);

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
    
    public boolean insertDonHang(DonHangDTO donHang, List<ChiTietDonHangDTO> chiTietList) {
        Connection conn = null;
        java.sql.PreparedStatement pstmtDH = null;
        java.sql.PreparedStatement pstmtCT = null;
        boolean result = false;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // Đã bổ sung DiaChiGiaoHang và PhuongThucTT (bỏ GhiChu)
            String sqlDH = "INSERT INTO DONHANG (MaDH, MaKH, TgDat, TongTienHang, PhiVanChuyen, TongTien, TrangThaiDH, DiaChiGiaoHang, PhuongThucTT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmtDH = conn.prepareStatement(sqlDH);
            pstmtDH.setString(1, donHang.getMaDH());
            pstmtDH.setString(2, donHang.getMaKH());
            pstmtDH.setTimestamp(3, new java.sql.Timestamp(donHang.getTgDat().getTime()));
            pstmtDH.setDouble(4, donHang.getTongTienHang());
            pstmtDH.setDouble(5, donHang.getPhiVanChuyen());
            pstmtDH.setDouble(6, donHang.getTongTien());
            pstmtDH.setString(7, donHang.getTrangThaiDH());
            pstmtDH.setString(8, donHang.getDiaChiGiaoHang());
            pstmtDH.setString(9, donHang.getPhuongThucTT());
            pstmtDH.executeUpdate();

            // 2. Insert Chi tiết đơn hàng vào bảng CHITIETDONHANG
            String sqlCT = "INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, GiaBan, SoLuong) VALUES ('CTDH' || LPAD(SEQ_CHITIETDONHANG.NEXTVAL, 4, '0'), ?, ?, ?, ?)";
            pstmtCT = conn.prepareStatement(sqlCT);

            for (ChiTietDonHangDTO ct : chiTietList) {
                pstmtCT.setString(1, ct.getMaDH());
                pstmtCT.setString(2, ct.getMaSP());
                pstmtCT.setDouble(3, ct.getGiaBan());
                pstmtCT.setDouble(4, ct.getSoLuong());
                pstmtCT.addBatch(); // Gom lệnh để thực thi 1 lần
            }
            pstmtCT.executeBatch(); 

            // 3. Nếu mọi thứ thành công thì Commit
            conn.commit(); 
            result = true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // Nếu có lỗi thì Rollback không lưu cái nào
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (pstmtDH != null) pstmtDH.close();
                if (pstmtCT != null) pstmtCT.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
