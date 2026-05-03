package bus;

import dao.LoHangDAO;
import dto.LoHangDTO;

import java.util.List;

public class LoHangBUS {

    private LoHangDAO loHangDAO = new LoHangDAO();

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