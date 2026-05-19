package bus;

import java.util.List;

import dao.GiaoHangDAO;
import dto.DonHangDTO;

public class GiaoHangBUS {

    private GiaoHangDAO dao = new GiaoHangDAO();

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

    public List<DonHangDTO> layDonChoGiao(){
        return dao.getDanhSachChoGiao();
    }

    public List<DonHangDTO> layDonDaNhan(String MaNV){
        return dao.getDanhSachDaNhan(MaNV);
    }
}
