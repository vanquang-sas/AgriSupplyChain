package bus;

import dao.SanPhamDAO;
import dto.SanPhamDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class SanPhamBUS {
    private final SanPhamDAO dao = new SanPhamDAO();

    public List<SanPhamDTO> getAll() {
        return dao.getAll();
    }

    public List<SanPhamDTO> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return dao.timKiem(keyword.trim());
    }

    public boolean add(SanPhamDTO sp) {
        if (sp.getTenSP() == null || sp.getTenSP().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống!");
        }
        if (sp.getMaLSP() == null || sp.getMaLSP().trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn loại sản phẩm!");
        }
        if (sp.getGiaMua() <= 0 || sp.getGiaBan() <= 0) {
            throw new IllegalArgumentException("Giá mua và Giá bán phải lớn hơn 0!");
        }
        if (sp.getGiaBan() < sp.getGiaMua()) {
            throw new IllegalArgumentException("Giá bán không được nhỏ hơn Giá mua!");
        }

        return dao.add(sp);
    }

    public boolean update(SanPhamDTO sp) {
        if (sp.getMaSP() == null || sp.getMaSP().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không hợp lệ!");
        }
        if (sp.getGiaMua() <= 0 || sp.getGiaBan() <= 0) {
            throw new IllegalArgumentException("Giá mua và Giá bán phải lớn hơn 0!");
        }
        
        return dao.update(sp);
    }

    // Ném thẳng SQLException ra GUI để báo lỗi ORA-20002
    public boolean delete(String maSP) throws SQLException {
        if (maSP == null || maSP.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không hợp lệ!");
        }
        return dao.delete(maSP);
    }
}