package bus;

import java.util.List;

import dao.GiaoHangDAO;
import dto.DonHangDTO;

public class GiaoHangBUS {

    private GiaoHangDAO dao = new GiaoHangDAO();

    public List<DonHangDTO> layDonChoGiao(String MaNV){
        return dao.getDanhSachChoGiao(MaNV);
    }

    public boolean nhanDonGiao(String MaDH, String MaNV){
        if(MaDH == null || MaDH.trim().isEmpty()){
            return false;
        }
        return dao.nhanDonGiao(MaDH, MaNV);
    }

    public boolean giaoThanhCong(String MaDH, String MaNV) {
        return dao.giaoHangThanhCong(MaDH, MaNV);
    } 

    public boolean giaoThatBai(String MaDH, String MaNV, String LyDoHuy) {
        return dao.giaoHangThatBai(MaDH,MaNV,LyDoHuy);
    }

    public List<DonHangDTO> lichSuGiaoHang(String MaNV) {
        return dao.getLichSuGiaoHang(MaNV);
    }
}
