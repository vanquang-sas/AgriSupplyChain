package bus;

import java.util.List;

import dao.GiaoHangDAO;
import dto.DonHangDTO;

public class GiaoHangBUS {

    private GiaoHangDAO dao = new GiaoHangDAO();

    public List<DonHangDTO> layDonChoGiao(){
        return dao.getDanhSachChoGiao();
    }

    public boolean nhanDonGiao(String MaDH, String MaNV){
        if(MaDH == null || MaDH.trim().isEmpty()){
            return false;
        }
        return dao.nhanDonGiao(MaDH, MaNV);
    }

    public boolean giaoThanhCong(String MaDH, String MaNV) {
        return dao.giaoHangThanhCong(MaDH); 
    }

    public boolean giaoThatBai(String MaDH) {
        return dao.giaoHangThatBai(MaDH);
    }

    public List<DonHangDTO> lichSuGiaoHang() {
        return dao.getLichSuGiaoHang();
    }
}
