package bus;

import dao.NhanVienDAO;
import dto.NhanVienDTO;

import java.sql.SQLException;
import java.util.List;

public class NhanVienBUS {
    private final NhanVienDAO dao = new NhanVienDAO();

    public List<NhanVienDTO> getAll() {
        return dao.getAll();
    }

    public List<NhanVienDTO> timKiem(String keyword) {
        return dao.timKiem(keyword);
    }

    public NhanVienDTO getById(String maNV) {
        return dao.getById(maNV);
    }

    public void them(NhanVienDTO nv, String password) throws IllegalArgumentException, SQLException {
        validate(nv);

        if (nv.getUsername() == null || nv.getUsername().trim().isEmpty())
            throw new IllegalArgumentException("Username không được để trống!");
        if (password == null || password.trim().isEmpty())
            throw new IllegalArgumentException("Mật khẩu không được để trống!");
        if (dao.isUsernameExists(nv.getUsername()))
            throw new IllegalArgumentException("Username '" + nv.getUsername() + "' đã tồn tại!");

        dao.them(nv, password);
    }

    public void capNhat(NhanVienDTO nv) throws IllegalArgumentException, SQLException {
        validate(nv);
        dao.capNhat(nv);
    }

    public void khoaTaiKhoan(String username) throws SQLException {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username không hợp lệ!");
        dao.khoaTaiKhoan(username);
    }

    public void moKhoaTaiKhoan(String username) throws SQLException {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username không hợp lệ!");
        dao.moKhoaTaiKhoan(username);
    }

    // ===================== VALIDATION =====================
    private void validate(NhanVienDTO nv) throws IllegalArgumentException {
        if (nv.getTenNV() == null || nv.getTenNV().trim().isEmpty())
            throw new IllegalArgumentException("Tên nhân viên không được để trống!");

        if (nv.getSdt() == null || nv.getSdt().trim().isEmpty())
            throw new IllegalArgumentException("Số điện thoại không được để trống!");
        if (!nv.getSdt().matches("\\d{10,12}"))
            throw new IllegalArgumentException("Số điện thoại chỉ gồm 10–12 chữ số!");

        if (nv.getChucVu() == null || nv.getChucVu().trim().isEmpty())
            throw new IllegalArgumentException("Chức vụ không được để trống!");

        if (nv.getLuong() < 0)
            throw new IllegalArgumentException("Lương không được âm!");
    }
}