package bus;

import dao.LoaiSanPhamDAO;
import dto.LoaiSanPhamDTO;

import java.util.List;

public class LoaiSanPhamBUS {
    private LoaiSanPhamDAO lspDAO;

    public LoaiSanPhamBUS() {
        this.lspDAO = new LoaiSanPhamDAO();
    }

    public List<LoaiSanPhamDTO> layDanhSachLSP() throws Exception {
        return lspDAO.layDanhSachLSP();
    }

    public boolean themLSP(LoaiSanPhamDTO lsp) throws Exception {
        validateData(lsp, true);
        return lspDAO.themLSP(lsp);
    }

    public boolean capNhatLSP(LoaiSanPhamDTO lsp) throws Exception {
        validateData(lsp, false);
        return lspDAO.capNhatLSP(lsp);
    }

    public boolean xoaLSP(String maLSP) throws Exception {
        if (maLSP == null || maLSP.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn loại sản phẩm cần xóa!");
        }
        return lspDAO.xoaLSP(maLSP);
    }

    // Logic kiểm tra dữ liệu dùng chung
    private void validateData(LoaiSanPhamDTO lsp, boolean isInsert) throws Exception {
        if (lsp.getTenLSP() == null || lsp.getTenLSP().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại sản phẩm không được để trống!");
        }
        
        // Kiểm tra trùng lặp tên
        List<LoaiSanPhamDTO> currentList = layDanhSachLSP();
        for (LoaiSanPhamDTO item : currentList) {
            // Bỏ qua chính nó khi đang update
            if (!isInsert && item.getMaLSP().equals(lsp.getMaLSP())) {
                continue;
            }
            if (item.getTenLSP().equalsIgnoreCase(lsp.getTenLSP().trim())) {
                throw new IllegalArgumentException("Tên loại sản phẩm '" + lsp.getTenLSP() + "' đã tồn tại!");
            }
        }
    }
}