package bus;

import dao.NhapKhoDAO;
import dto.TonKhoDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NhapKhoBUS {
    private final NhapKhoDAO dao = new NhapKhoDAO();

    public List<String> getAllMaKho() throws Exception {
        return dao.getAllMaKho();
    }

    public Map<String, String> getKhoLoaiKhoMap() throws Exception {
        return dao.getKhoLoaiKhoMap();
    }

    public ArrayList<Object[]> getDanhSachNhapKho() {
        return dao.getDanhSachNhapKho();
    }

    public String xacNhanNhapKho(TonKhoDTO dto, String tenSP) {
        if (dto.getMaKho() == null || dto.getMaKho().trim().isEmpty()) {
            return "Vui lòng chọn kho cho " + tenSP;
        }

        if (dto.getViTri() == null || dto.getViTri().trim().isEmpty()) {
            return "Vui lòng chọn vị trí cho " + tenSP;
        }

        if (dto.getTgHetHan() == null) {
            return "Vui lòng chọn ngày hết hạn";
        }

        try {
            dao.xacNhanViTri(dto);
            return "SUCCESS";
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg == null) return "Lỗi không xác định!";

            if (msg.contains("ORA-20028")) {
                return "Lỗi quy cách: " + tenSP + " yêu cầu loại kho bảo quản khác!";
            }
            if (msg.contains("ORA-20026")) {
                return "Lỗi: Lô hàng " + tenSP + " không ở trạng thái chờ nhập kho!";
            }
            if (msg.contains("ORA-20027")) {
                return "Lỗi: Sản phẩm này đã được cất vào kho rồi (trùng mã)!";
            }
            if (msg.contains("CK_TGHETHAN")) {
                return "Lỗi: Ngày hết hạn không đạt yêu cầu (phải sau ngày hiện tại)!";
            }
            return "Lỗi hệ thống Database: " + msg;
        }
    }
}