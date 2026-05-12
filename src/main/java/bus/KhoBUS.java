package bus;

import dao.KhoDAO;
import dto.KhoDTO;

import java.sql.SQLException;
import java.util.List;

public class KhoBUS {
    private final KhoDAO dao = new KhoDAO();

    public List<KhoDTO> getAll() { return dao.getAll(); }
    public KhoDTO getById(String maKho) { return dao.getById(maKho); }
    public List<KhoDTO> timKiem(String keyword) { return dao.timKiem(keyword); }

    public void them(KhoDTO kho) throws IllegalArgumentException, SQLException {
        validate(kho);
        dao.them(kho);
    }

    public void capNhat(KhoDTO kho) throws IllegalArgumentException, SQLException {
        validate(kho);
        dao.capNhat(kho);
    }

    private void validate(KhoDTO kho) {
        if (kho.getTenKho() == null || kho.getTenKho().trim().isEmpty())
            throw new IllegalArgumentException("Tên kho không được để trống!");
        if (kho.getLoaiKho() == null || kho.getLoaiKho().trim().isEmpty())
            throw new IllegalArgumentException("Loại kho không được để trống!");
        if (kho.getDiaChi() == null || kho.getDiaChi().trim().isEmpty())
            throw new IllegalArgumentException("Địa chỉ không được để trống!");
    }

    public boolean delete(String maKho) throws SQLException {
        return dao.delete(maKho);
    }
}