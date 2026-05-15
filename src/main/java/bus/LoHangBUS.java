package bus;

import dao.LoHangDAO;
import dao.NhanVienDAO;
import dto.ChiTietLoHangDTO;
import dto.LoHangDTO;
import dto.NhanVienDTO;
import util.Session;

import java.util.List;

public class LoHangBUS {

    private final LoHangDAO loHangDAO = new LoHangDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    public boolean createLoHang(String maNCC, List<ChiTietLoHangDTO> details) {
        if (maNCC == null || maNCC.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn nhà cung cấp.");
        }
        if (details == null || details.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng thêm ít nhất một sản phẩm vào lô hàng.");
        }
        if (!Session.isLogged()) {
            throw new IllegalStateException("Phiên đăng nhập không hợp lệ.");
        }

        NhanVienDTO currentNv = nhanVienDAO.getByUsername(Session.currentUser.getUsername());
        if (currentNv == null || currentNv.getMaNV() == null || currentNv.getMaNV().trim().isEmpty()) {
            throw new IllegalStateException("Không xác định được nhân viên hiện tại.");
        }

        return loHangDAO.createLoHang(maNCC, currentNv.getMaNV(), details);
    }

    public boolean themLoHang(int maNCC) {
        if (maNCC <= 0) {
            System.out.println("Mã NCC không hợp lệ");
            return false;
        }
        return loHangDAO.themLoHang(maNCC);
    }

    public boolean themChiTiet(int maLH, int maSP, int soLuong) {
        if (maLH <= 0 || maSP <= 0 || soLuong <= 0) {
            System.out.println("Dữ liệu không hợp lệ");
            return false;
        }
        return loHangDAO.themChiTietLoHang(maLH, maSP, soLuong);
    }

    public List<LoHangDTO> getAll() {
        return loHangDAO.getAllLoHang();
    }

    public boolean yeuCauNhapKho(int maLH) {
        return loHangDAO.yeuCauNhapKho(maLH);
    }
}