package bus;

import dao.ThamSoDAO;
import dto.ThamSoDTO;

import java.sql.SQLException;
import java.util.List;

public class ThamSoBUS {
    private final ThamSoDAO dao = new ThamSoDAO();

    public List<ThamSoDTO> getAll() { return dao.getAll(); }
    public ThamSoDTO getById(String maTS) { return dao.getById(maTS); }

    public void capNhat(String maTS, String giaTriStr, String moTa)
            throws IllegalArgumentException, SQLException {

        if (giaTriStr == null || giaTriStr.trim().isEmpty())
            throw new IllegalArgumentException("Giá trị không được để trống!");

        double giaTri;
        try {
            giaTri = Double.parseDouble(giaTriStr.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Giá trị phải là số (không nhập chữ)!");
        }

        if (giaTri < 0)
            throw new IllegalArgumentException("Giá trị không được âm!");

        // Validation thêm theo từng tham số cụ thể
        ThamSoDTO ts = dao.getById(maTS);
        if (ts != null) {
            switch (ts.getTenTS()) {
                case "MIN_TONKHO":
                    if (giaTri < 1)
                        throw new IllegalArgumentException("Tồn kho tối thiểu phải ≥ 1!");
                    break;
                case "CANHBAO_HETHAN":
                    if (giaTri < 1 || giaTri > 365)
                        throw new IllegalArgumentException("Số ngày cảnh báo phải từ 1 đến 365!");
                    break;
                case "SHIP_THUONG":
                case "SHIP_THANTHIET":
                case "SHIP_VIP":
                    if (giaTri < 0)
                        throw new IllegalArgumentException("Phí vận chuyển không được âm!");
                    break;
                case "GG_THUONG": case "GG_THANTHIET": case "GG_VIP":
                    if (giaTri < 0 || giaTri > 1)
                        throw new IllegalArgumentException("Giảm giá phải từ 0.00 đến 1.00 (0% đến 100%)!");
                    break;
                case "MAX_TG_THANHTOAN":
                    if (giaTri < 1)
                        throw new IllegalArgumentException("Thời gian tối đa phải ≥ 1 giờ!");
                    break;
            }
        }

        dao.capNhat(maTS, giaTri, moTa);
    }

    public double getValueByName(String name, double defaultValue) {
        return dao.getValueByName(name, defaultValue);
    }
}