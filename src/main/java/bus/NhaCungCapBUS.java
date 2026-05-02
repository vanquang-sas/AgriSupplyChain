package bus;

import dao.NhaCungCapDAO;
import dto.NhaCungCapDTO;

import java.sql.SQLException;
import java.util.List;

public class NhaCungCapBUS {
    private final NhaCungCapDAO dao = new NhaCungCapDAO();

    public List<NhaCungCapDTO> getAll() {
        return dao.getAll();
    }

    public List<NhaCungCapDTO> timKiem(String keyword) {
        return dao.timKiem(keyword);
    }

    public NhaCungCapDTO getById(String maNCC) {
        return dao.getById(maNCC);
    }

    public void them(NhaCungCapDTO ncc) throws IllegalArgumentException, SQLException {
        validate(ncc);
        dao.them(ncc);
    }

    public void capNhat(NhaCungCapDTO ncc) throws IllegalArgumentException, SQLException {
        validate(ncc);
        dao.capNhat(ncc);
    }

    // Ngừng hợp tác
    public void ngungHopTac(String maNCC) throws SQLException {
        if (maNCC == null || maNCC.trim().isEmpty())
            throw new IllegalArgumentException("Mã NCC không hợp lệ!");
        dao.ngungHopTac(maNCC);
    }

    // Khôi phục hợp tác
    public void khoiPhucHopTac(String maNCC) throws SQLException {
        if (maNCC == null || maNCC.trim().isEmpty())
            throw new IllegalArgumentException("Mã NCC không hợp lệ!");
        dao.khoiPhucHopTac(maNCC);
    }

    // ===================== VALIDATION =====================
    private void validate(NhaCungCapDTO ncc) throws IllegalArgumentException {
        if (ncc.getTenNCC() == null || ncc.getTenNCC().trim().isEmpty())
            throw new IllegalArgumentException("Tên nhà cung cấp không được để trống!");

        if (ncc.getSdt() == null || ncc.getSdt().trim().isEmpty())
            throw new IllegalArgumentException("Số điện thoại không được để trống!");
        if (!ncc.getSdt().matches("\\d{10,12}"))
            throw new IllegalArgumentException("Số điện thoại chỉ gồm 10–12 chữ số!");

        if (ncc.getEmail() != null && !ncc.getEmail().trim().isEmpty()) {
            if (!ncc.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$"))
                throw new IllegalArgumentException("Email không đúng định dạng!");
        }

        if (ncc.getDiaChi() == null || ncc.getDiaChi().trim().isEmpty())
            throw new IllegalArgumentException("Địa chỉ không được để trống!");
    }
}