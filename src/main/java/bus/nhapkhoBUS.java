package bus;

import dao.nhapkhoDAO;
import dto.TonKhoDTO;

public class nhapkhoBUS {
    private nhapkhoDAO dao = new nhapkhoDAO();

    public String xacNhanNhapKho(TonKhoDTO dto, String tenSP) {
        if (dto.getMaKho() == null || dto.getMaKho().equals("Chọn kho...")) {
            return "Vui lòng thiết lập Kho cho sản phẩm " + tenSP;
        }
        
        if (dto.getViTri() == null || dto.getViTri().equals("Chọn vị trí...")) {
            return "Vui lòng thiết lập Vị trí cho sản phẩm " + tenSP;
        }
        
        if (dto.getTgHetHan() == null) {
            return "Vui lòng chọn ngày hết hạn cho sản phẩm " + tenSP;
        }

        try {
            dao.xacNhanViTri(dto);
            return "SUCCESS";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}