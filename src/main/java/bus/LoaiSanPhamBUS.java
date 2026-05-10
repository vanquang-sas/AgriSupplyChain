package bus;

import dao.LoaiSanPhamDAO;
import dto.LoaiSanPhamDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class LoaiSanPhamBUS {
    private final LoaiSanPhamDAO dao = new LoaiSanPhamDAO();

    public List<LoaiSanPhamDTO> getAll() {
        return dao.getAll();
    }

    public List<LoaiSanPhamDTO> timKiem(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return dao.timKiem(keyword.trim());
    }

    public boolean add(LoaiSanPhamDTO lsp) {
        if (lsp.getTenLSP() == null || lsp.getTenLSP().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại sản phẩm không được để trống!");
        }

        return dao.add(lsp);
    }

    public boolean update(LoaiSanPhamDTO lsp) {
        if (lsp.getMaLSP() == null || lsp.getMaLSP().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã loại sản phẩm không hợp lệ!");
        }
        if (lsp.getTenLSP() == null || lsp.getTenLSP().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại sản phẩm không được để trống!");
        }
        return dao.update(lsp);
    }

    public boolean delete(String maLSP) throws SQLException {
        if (maLSP == null || maLSP.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã loại sản phẩm không hợp lệ!");
        }
        return dao.delete(maLSP);
    }
}