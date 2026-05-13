package bus;

import dao.XuatKhoDAO;
import dto.XuatKhoDTO;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

public class XuatKhoBUS {
    private final XuatKhoDAO dao = new XuatKhoDAO();

    public List<String> getAllMaNhanVien() throws Exception {
        return dao.getAllMaNhanVien();
    }

    public ArrayList<Object[]> getDanhSachSoanHang() {
        return dao.getDanhSachSoanHang();
    }

    public ArrayList<Object[]> getDanhSachDonHangChoXuat() {
        return dao.getDanhSachDonHangChoXuat();
    }

    public ArrayList<Object[]> getDanhSachDonHangDaXuat() {
        return dao.getDanhSachDonHangDaXuat();
    }

    public int getSoDonHangThieuTonKho() {
        return dao.getSoDonHangThieuTonKho();
    }

    public List<Object[]> getChiTietDonHang(String maDH) throws Exception {
        return dao.getChiTietDonHang(maDH);
    }

    public String yeuCauXuatKho(String maDH) {
        try {
            dao.yeuCauXuatKho(maDH);
            return "SUCCESS";
        } catch (SQLException e) {
            return parseSqlError(e);
        } catch (Exception e) {
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    public String xacNhanSoanHang(XuatKhoDTO dto) {
        if (dto.getMaNV() == null || dto.getMaNV().trim().isEmpty()) {
            return "Vui lòng chọn nhân viên soạn hàng cho đơn hàng: " + dto.getMaXK();
        }

        try {
            dao.xacNhanXuatKho(dto);
            return "SUCCESS";
        } catch (Exception e) {
            String msg = e.getMessage();
            return "Lỗi hệ thống khi xác nhận đơn hàng: " + msg;
        }
    }

    private String parseSqlError(SQLException e) {
        String msg = e.getMessage();
        if (msg != null) {
            if (msg.contains("ORA-20024")) {
                String[] parts = msg.split("ORA-20024:");
                if (parts.length > 1) {
                    return parts[1].split("\n")[0].trim();
                }
                return "Lỗi kho không đủ hàng cho đơn hàng: kiểm tra tồn kho và số lượng yêu cầu.";
            }
            if (msg.contains("ORA-20023")) {
                return "Lỗi trạng thái đơn hàng không hợp lệ khi tạo yêu cầu xuất kho.";
            }
        }
        return "Lỗi hệ thống Database: " + msg;
    }
}