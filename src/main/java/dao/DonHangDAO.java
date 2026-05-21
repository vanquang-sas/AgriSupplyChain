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

            // 2. Chuẩn bị lệnh gọi Function
            String sql = "{ ? = call FN_LAY_DS_DONHANG_BY_KH(?) }";
            cstmt = conn.prepareCall(sql);

            // 3. Đăng ký tham số trả về (SYS_REFCURSOR)
            cstmt.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);

            // 4. Đặt giá trị tham số đầu vào
            cstmt.setString(2, maKH);

            // 5. Thực thi Function
            cstmt.execute();

            // 6. Lấy ResultSet từ tham số trả về
            rs = (ResultSet) cstmt.getObject(1);

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
            System.err.println("✗ Lỗi SQL khi gọi FN_LAY_DS_DONHANG_BY_KH:");
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
            cstmt.setString(2, "Khách hàng huỷ đơn");

            // 4. Thực thi Procedure
            cstmt.execute();

            System.out.println("Hủy đơn hàng [" + maDH + "] thành công");

        } catch (SQLException e) {
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

            // Đã bổ dung DiaChiGiaoHang và PhuongThucTT (bỏ GhiChu, MaDH để trigger sinh tự động)
            String sqlDH = "INSERT INTO DONHANG (MaKH, TgDat, TongTienHang, PhiVanChuyen, TongTien, TrangThaiDH, DiaChiGiaoHang, PhuongThucTT) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmtDH = conn.prepareStatement(sqlDH, new String[]{"MADH"});
            pstmtDH.setString(1, donHang.getMaKH());
            pstmtDH.setTimestamp(2, new java.sql.Timestamp(donHang.getTgDat().getTime()));
            pstmtDH.setDouble(3, 0.0);
            pstmtDH.setDouble(4, donHang.getPhiVanChuyen());
            pstmtDH.setDouble(5, donHang.getPhiVanChuyen());
            pstmtDH.setString(6, donHang.getTrangThaiDH());
            pstmtDH.setString(7, donHang.getDiaChiGiaoHang());
            pstmtDH.setString(8, donHang.getPhuongThucTT());
            pstmtDH.executeUpdate();

            // Lấy mã đơn hàng được sinh tự động bởi trigger
            String generatedMaDH = null;
            try (ResultSet rs = pstmtDH.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedMaDH = rs.getString(1);
                }
            }

            if (generatedMaDH == null || generatedMaDH.isEmpty()) {
                throw new SQLException("Không thể lấy Mã Đơn Hàng tự động sinh từ trigger!");
            }

            // Gán ngược lại mã đơn hàng thật vào donHang DTO để bên ngoài sử dụng
            donHang.setMaDH(generatedMaDH);

            // 2. Insert Chi tiết đơn hàng vào bảng CHITIETDONHANG
            String sqlCT = "INSERT INTO CHITIETDONHANG (MaCTDH, MaDH, MaSP, GiaBan, SoLuong) VALUES ('CTDH' || LPAD(SEQ_CHITIETDONHANG.NEXTVAL, 4, '0'), ?, ?, ?, ?)";
            pstmtCT = conn.prepareStatement(sqlCT);

            for (ChiTietDonHangDTO ct : chiTietList) {
                pstmtCT.setString(1, generatedMaDH); // Dùng mã thật được trigger sinh ra
                pstmtCT.setString(2, ct.getMaSP());
                pstmtCT.setDouble(3, ct.getGiaBan());
                pstmtCT.setDouble(4, ct.getSoLuong());
                pstmtCT.addBatch(); // Gom lệnh để thực thi 1 lần
            }
            pstmtCT.executeBatch(); 

            // 3. Gọi SP_YEUCAU_XUATKHO để trừ số lượng khả dụng (tạo các dòng XUATKHO trạng thái Tạm giữ)
            String sqlYeuCau = "{call SP_YEUCAU_XUATKHO(?)}";
            try (java.sql.CallableStatement cs = conn.prepareCall(sqlYeuCau)) {
                cs.setString(1, generatedMaDH);
                cs.execute();
            }

            // 4. Nếu mọi thứ thành công thì Commit
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
